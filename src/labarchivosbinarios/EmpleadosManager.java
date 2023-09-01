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
        private RandomAccessFile billsFilefor(int code) throws IOException 
    {
       String dirPadre = employeeFolder(code);
       String path= dirPadre +"/recibos.emp";
       return new RandomAccessFile(path,"rw");
    }
    
}
/*
Instrucciones:

-Crear el ejercicio en SWING. JOSUE


 NICOLE Y EMA:       
        addSaleToEmployee(int code, double sale): primero se necesita buscar ese empleado que envía un código por parámetro, luego que la identifica que existe el empleado se debe de sumarle el monto recibido a las ventas del mes actual al empleado.
        public void payEmployee(int code): Escribe el recibo de pago, si el empleado está activo y no se le ha pagado, con el siguiente formato:
            * long fecha de pago

            * double sueldo: es basándonos en el salario más la comisión de las ventas por el 10%.

            * double deducción: Tiene una deducción del 3.5% al sueldo.

            * int año

            * int mes

           *Escribir un boolean que cambia el boolean de pagado en el archivo de ventas (salesFileFor(code))

ANA:

    *También se necesita enviar una salida de pantalla del nombre del empleado y el total del sueldo de pago.

     *Para esta función necesita la siguiente llamada de funciones:

     private RandomAccessFile billsFilefor(int code) : Se necesita buscar la carpeta del empleado y agregar un archivo nombrado como recibos.emp para crear un recibo con el formato mostrado antes.



NICOLE Y EMA :public boolean isEmployeePayed(int code) : Se busca el empleado con la ayuda de salesFileFor con el propósito en devolver si ya se le pagó el mes al empleado.




Se necesita hacer un menú:
            MENÚ

1- Agregar Empleado JOSUE

2- Listar Empleados No Despedidos ANA

3- Agregar Venta a Empleado JOSUE

4- Pagar Empleado NICOLE Y EMA

5- Despedir Empleado ANA

6- Salir
*/
