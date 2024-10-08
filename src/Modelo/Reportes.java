/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileSystemView;


/**
 *
 * @author pauli
 */
public class Reportes {
    
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;

    // Reporte de ventas por día con tabla
    public void generarReporteVentasPorDiaEnPDF() throws FileNotFoundException {
        String sql = "SELECT fecha, total FROM pedidos ORDER BY fecha";
        
        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + File.separator + "reporteDia.pdf");
        archivo = new FileOutputStream(salida);
        
        Document document = new Document();

        try {
            
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            PdfWriter.getInstance(document, archivo);
            document.open();
            document.add(new Paragraph("Reporte de Ventas por Día\n\n"));

            // Crear la tabla con dos columnas: Fecha y Total Ventas
            PdfPTable table = new PdfPTable(2);
            table.addCell("Fecha");
            table.addCell("Total Ventas");

            double totalGeneral = 0.0;
            while (rs.next()) {
                String fecha = rs.getString("fecha");
                double totalVentas = rs.getDouble("total");

                table.addCell(fecha);
                table.addCell(String.valueOf(totalVentas));

                totalGeneral += totalVentas;  // Sumar el total vendido
            }

            // Añadir la tabla al documento
            document.add(table);

            // Agregar el total general
            document.add(new Paragraph("\nTotal Vendido: " + totalGeneral));

            document.close();
            System.out.println("Reporte de ventas por día generado con éxito en 'ventas_por_dia.pdf'.");
            Desktop.getDesktop().open(salida);

        } catch (SQLException | DocumentException | IOException e) {
             System.out.println(e.toString());
        }
    }

    // Obtener los meses disponibles desde la base de datos
    public List<String> obtenerMesesDisponibles() {
        List<String> meses = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE_FORMAT(fecha, '%Y-%m') AS mes FROM pedidos ORDER BY mes";

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String mes = rs.getString("mes");
                meses.add(mes);  // Agregar el mes a la lista
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meses;
    }

    // Generar reporte de ventas por mes
    public void generarReporteVentasPorMesEnPDF(String mesSeleccionado) throws FileNotFoundException {
        String sql = "SELECT fecha, total FROM pedidos WHERE DATE_FORMAT(fecha, '%Y-%m') = ? ORDER BY fecha";

        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + File.separator + "ventasMes.pdf");
        archivo = new FileOutputStream(salida);

        Document document = new Document();

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, mesSeleccionado);  // Usar el mes seleccionado en la consulta
            rs = ps.executeQuery();

            PdfWriter.getInstance(document, archivo);
            document.open();
            document.add(new Paragraph("Reporte de Ventas por Mes: " + mesSeleccionado + "\n\n"));

            // Crear la tabla con dos columnas: Fecha y Total Ventas
            PdfPTable table = new PdfPTable(2);
            table.addCell("Fecha");
            table.addCell("Total Ventas");

            double totalMensual = 0.0;
            while (rs.next()) {
                String fecha = rs.getString("fecha");
                double totalVentas = rs.getDouble("total");

                table.addCell(fecha);
                table.addCell(String.valueOf(totalVentas));

                totalMensual += totalVentas;  // Sumar el total vendido en el mes
            }

            // Añadir la tabla al documento
            document.add(table);

            // Agregar el total mensual
            document.add(new Paragraph("\nTotal Vendido en el Mes: " + totalMensual));

            document.close();
            System.out.println("Reporte de ventas por mes generado con éxito en 'pedido.pdf'.");
            Desktop.getDesktop().open(salida);

        } catch (SQLException | DocumentException | IOException e) {
            System.out.println(e.toString());;
        }
    }

    // Reporte de cantidad de platos vendidos con tabla
    public void generarReporteCantidadPorPlatoEnPDF() throws FileNotFoundException {
        String sql = "SELECT nombre, SUM(cantidad) AS total_cantidad FROM detalle_pedidos GROUP BY nombre ORDER BY total_cantidad DESC";
        
        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + File.separator + "vemtasPlatos.pdf");
        archivo = new FileOutputStream(salida);
        
        
        Document document = new Document();

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            PdfWriter.getInstance(document, archivo);
            document.open();
            document.add(new Paragraph("Reporte de Cantidad Vendida por Plato\n\n"));

            // Crear la tabla con dos columnas: Plato y Cantidad Vendida
            PdfPTable table = new PdfPTable(2);
            table.addCell("Plato");
            table.addCell("Cantidad Vendida");

            while (rs.next()) {
                String nombrePlato = rs.getString("nombre");
                int cantidadVendida = rs.getInt("total_cantidad");

                table.addCell(nombrePlato);
                table.addCell(String.valueOf(cantidadVendida));
            }

            // Añadir la tabla al documento
            document.add(table);

            document.close();
            
            System.out.println("Reporte de cantidad por plato generado con éxito en 'cantidad_por_plato.pdf'.");
            
            Desktop.getDesktop().open(salida);

        } catch (SQLException | DocumentException | IOException e) {
             System.out.println(e.toString());
        }
    }

    
}

    
    

