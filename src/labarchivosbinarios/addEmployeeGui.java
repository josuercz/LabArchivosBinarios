/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labarchivosbinarios;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author josuc
 */
public class addEmployeeGui extends JFrame implements ActionListener {
    JPanel panelAdd = new JPanel();
    JLabel lblTitulo = new JLabel();
    JTextArea txtnombre = new JTextArea();
    JTextArea txtsalario = new JTextArea();
    JButton botonRegresar = new JButton();
    
    public addEmployeeGui() {
        setSize(500,500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        panelAdd.setSize(500, 500);
        panelAdd.setLayout(null);
        add(panelAdd);
        
        lblTitulo.setText("Agregar Empleado");
        lblTitulo.setBounds(120, 30, 280, 35);
        lblTitulo.setFont(new Font(null, Font.PLAIN, 30));
        lblTitulo.setForeground(Color.black);
        panelAdd.add(lblTitulo); 
        
        botonRegresar.setText("Regresar");
        botonRegresar.setBounds(180, 400, 130, 30);
        botonRegresar.addActionListener(this);
        botonRegresar.setBackground(Color.white);
        panelAdd.add(botonRegresar);
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonRegresar) {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
            this.dispose();
        }
    }
    
}

