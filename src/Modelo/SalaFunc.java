/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pauli
 */
public class SalaFunc {
    
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    
    
    
    public boolean RegistrarSala(Sala sala){
        String sql = "INSERT INTO salas(nombre, mesas) VALUES (?,?)";
        try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setString(1, sala.getNombre());
           ps.setInt(2, sala.getMesas());
           ps.execute();
           return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
    
    public List Listar(){
        List<Sala> Lista = new ArrayList();
        String sql = "SELECT * FROM salas";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {                
                Sala sala = new Sala();
                sala.setId(rs.getInt("id"));
                sala.setNombre(rs.getString("nombre"));
                sala.setMesas(rs.getInt("mesas"));
                Lista.add(sala);
            }
            
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return Lista;
    }
    
    public boolean Eliminar(int id){
        String sql = "DELETE FROM salas WHERE id = ? ";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
    
    public boolean Modificar(Sala sala){
        String sql = "UPDATE salas SET nombre=?, mesas=? WHERE id=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, sala.getNombre());
            ps.setInt(2, sala.getMesas());
            ps.setInt(3, sala.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }    
    
}
