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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pauli
 */
public class PlatoFunc {

    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;

    private Set<Integer> platosDeshabilitados = new HashSet<>();
    
    public boolean Registrar(Plato pla) {
        String sql = "INSERT INTO platos (nombre, precio, fecha) VALUES (?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pla.getNombre());
            ps.setDouble(2, pla.getPrecio());
            ps.setString(3, pla.getFecha());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    public List Listar(String valor) {
        List<Plato> Lista = new ArrayList();
        String sql = "SELECT * FROM platos ";
        String consulta = "SELECT * FROM platos WHERE nombre LIKE '%"+valor+"%' ";
        try {
            con = cn.getConnection();
            if(valor.equalsIgnoreCase("")){
                ps = con.prepareStatement(sql);
            }else{
                ps = con.prepareStatement(consulta);
            }
            
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");

                // Solo agregar a la lista si el plato NO está deshabilitado
                if (!platosDeshabilitados.contains(id)) {
                    Plato pl = new Plato();
                    pl.setId(id);
                    pl.setNombre(rs.getString("nombre"));
                    pl.setPrecio(rs.getDouble("precio"));
                    Lista.add(pl);
                }}
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return Lista;
    }

    public boolean Eliminar(int id) {
        String sql = "DELETE FROM platos WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    public boolean Modificar(Plato pla) {
        String sql = "UPDATE platos SET nombre=?, precio=? WHERE id=?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, pla.getNombre());
            ps.setDouble(2, pla.getPrecio());
            ps.setInt(3, pla.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
    public void deshabilitarPlato(int id) {
        platosDeshabilitados.add(id);  
    }
    public void habilitarPlato(int id) {
        platosDeshabilitados.remove(id); 
    }
    public boolean estaDeshabilitado(int id) {
        return platosDeshabilitados.contains(id);
    }
    
}
