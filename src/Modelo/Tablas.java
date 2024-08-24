/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 *
 * @author pauli
 */
public class Tablas extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable jtable, Object o, boolean bln, boolean bln1, int row, int col) {
        super.getTableCellRendererComponent(jtable, o, bln, bln1, row, col);
        switch (jtable.getValueAt(row, 6).toString()) {
            case "PENDIENTE" -> {
                setBackground(new Color(255,51,51));
                setForeground(Color.white);
            }
            case "FINALIZADO" -> {
                setBackground(new Color(0,102,102));
                setForeground(Color.white);
            }
            default -> {
                    setBackground(Color.white);
                    setForeground(Color.black);
            }
        }
        return this;
    }
    
}
