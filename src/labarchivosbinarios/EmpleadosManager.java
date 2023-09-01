/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labarchivosbinarios;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

public class EmpleadosManager {
    private RandomAccessFile rcods, remps;
    public EmpleadosManager() {
        try{
            File f=new File("company");
            f.mkdir();
            rcods=new RandomAccessFile("company/codigos.emp","rw");
            remps=new RandomAccessFile("company/empleados.emp","rw");
            initCode();
        }catch(IOException e){
            System.out.println("NO DEBERIA PASAR ESTO");
        }
    }
    private void initCode() throws IOException{
        if(rcods.length()==0){
            rcods.writeInt(1);
        }
    }
    private int getCode() throws IOException{
        rcods.seek(0);
        int code=rcods.readInt();//LEE
        rcods.seek(0);//MOVER INICIO
        rcods.writeInt(code+1);
        return code;
    }
    public void addEmployes(String name, double salary) throws IOException{
       // codigo nombre  salario  fecha contratacion  sfecha despido
        remps.seek(remps.length());
        int code=getCode();//archivos para cada empleado con su folder por ventas
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        //aseguramos sus archivos individuales
        createEmployeeFolder(code);
    } 
    private String employeeFolder(int code){//existe carpeta por cada empleado es decir raiz
        return "company/empleado"+code;
    }
    
    private void createEmployeeFolder(int code) throws IOException{
        File edir=new File(employeeFolder(code));//crear folder empleado+code
        edir.mkdir();
        //crear el archvo de las ventas
        createYearSalesFileFor(code);
    }
    private RandomAccessFile salesFileFor(int code) throws IOException{//funcion que crea los archivos de la venta que va contenr de cada mes de cada año
        String dirPadre=employeeFolder(code);
        int yearActual=Calendar.getInstance().get(Calendar.YEAR);
        String path=dirPadre+"/ventas"+yearActual+".emp";
        return new RandomAccessFile(path,"rw");//si no existe se crea, si existe usara ese
    }
    private void createYearSalesFileFor(int code) throws IOException{
        RandomAccessFile ryear= salesFileFor(code);
        if(ryear.length()==0){
            for (int m = 0; m < 12; m++) {
                ryear.writeDouble(0);
                ryear.writeBoolean(false);
            }
        }
    }
    public String listado() throws IOException{
        String todo = "";
        remps.seek(0);
        while(remps.getFilePointer()<remps.length()){
            int code=remps.readInt();
            String name=remps.readUTF();
            double sal=remps.readDouble();
            Date fc=new Date(remps.readLong());
            todo += code + "\t" + name + "\t" + sal + "\t" + fc + "\n";
            if(remps.readLong()==0){
                System.out.println(code+" - "+name+" - "+sal+" - "+fc);
            }
        }
        return todo;
    }
    private boolean isEmployeeActive(int code)throws IOException{
        remps.seek(0);
        while(remps.getFilePointer()<remps.length()){
            int cod=remps.readInt();
            long pos=remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(16);//me salta salario  y contrataccion
            if(remps.readLong()==0 && cod==code){
                remps.seek(pos);
                return true;
            }
        }
        return false;
    }
    public boolean fireEmployee(int code)throws IOException{
        if(isEmployeeActive(code)){
            String name=remps.readUTF();
            double salary= remps.readDouble();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a: "+name);
            System.out.println("Salario de pago: "+salary);
            return true;
        }
        return false;
    }
    public void addSaleToEmployee(int code, double sale) throws IOException{
        if(isEmployeeActive(code)){
            int mesActual=Calendar.getInstance().get(Calendar.MONTH);
            RandomAccessFile mes=salesFileFor(code);
            int bytesEstas=mesActual*12;//ubica
            mes.seek(bytesEstas);
            double salActual=remps.readDouble();
            mes.seek(bytesEstas);
            mes.writeDouble(salActual+sale);
        }
    }
        private RandomAccessFile billsFilefor(int code) throws IOException 
    {
       String dirPadre = employeeFolder(code);
       String path= dirPadre +"/recibos.emp";
       return new RandomAccessFile(path,"rw");
    }

    public void payEmployee(int code) throws IOException {
        if (isEmployeeActive(code) && !isEmployeePayed(code)) {
            remps.seek(0);
            while (remps.getFilePointer() < remps.length()) {
                int cod = remps.readInt();
                long pos = remps.getFilePointer();
                String name = remps.readUTF();
                double salario = remps.readDouble();
                Date contrato = new Date(remps.readLong());
                long ultimoPago = remps.readLong(); // Obtener la última fecha de pago

                if (ultimoPago == 0 && cod == code) {
                    RandomAccessFile raf = salesFileFor(code);
                    int cM = Calendar.getInstance().get(Calendar.MONTH);
                    raf.seek(cM * 12);

                    double total = salario + raf.readDouble() * 0.10;
                    factura(code, new Date().getTime(), total, total * 0.035, Calendar.getInstance().get(Calendar.YEAR), cM);
                    // Marcar el mes como pagado en el archivo de ventas
                    raf.seek(cM * 12 + 8);
                    raf.writeBoolean(true);

                    // Actualizar la fecha de último pago en el archivo de empleados
                    remps.seek(pos + 24); // Avanzar al campo de última fecha de pago
                    remps.writeLong(new Date().getTime()); // Actualizar con la fecha actual

                    System.out.println("Nombre empleado a pagar:\t " + name);
                    System.out.println("Total saldo:\t" + total);
                }
            }
        }
    }

    private void factura(int code, long diaPago, double total, double menos, int yyyy, int MM) throws IOException {
        RandomAccessFile fact = billsFilefor(code);
        fact.writeLong(diaPago);
        fact.writeDouble(total);
        fact.writeDouble(menos);
        fact.writeInt(yyyy);
        fact.writeInt(MM);
        fact.writeBoolean(true);
    }

    public boolean isEmployeePayed(int code) throws IOException {
        RandomAccessFile recibo = billsFilefor(code);
        int cM = Calendar.getInstance().get(Calendar.MONTH);
        recibo.seek(cM * 24 + 20);
        return recibo.readBoolean();
    }


}
