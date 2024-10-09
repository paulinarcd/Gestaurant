/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
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
    
    
    
    public List<String> obtenerFechasDisponibles() {
        List<String> fechas = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE_FORMAT(fecha, '%d/%m/%Y') AS fecha FROM pedidos ORDER BY fecha";

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String fecha = rs.getString("fecha");
                fechas.add(fecha);  // Agregar la fecha a la lista
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fechas;
    }

    // Reporte de ventas por día con tabla
    public void generarReporteVentasPorDiaEnPDF(String fechaSeleccionada) throws FileNotFoundException {
        String sql = "SELECT p.fecha, dp.nombre AS plato, SUM(dp.cantidad) AS total_cantidad, SUM(dp.cantidad * dp.precio) AS total_ventas " +
                     "FROM pedidos p " +
                     "JOIN detalle_pedidos dp ON p.id = dp.id_pedido " +
                     "WHERE DATE_FORMAT(p.fecha, '%d/%m/%Y') = ? " +  // Asegurarse de que el formato coincida
                     "GROUP BY p.fecha, dp.nombre " +
                     "ORDER BY dp.nombre";

        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + File.separator + "reporteDia.pdf");
        archivo = new FileOutputStream(salida);

        Document document = new Document();

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fechaSeleccionada);  // Establecer la fecha seleccionada como parámetro
            rs = ps.executeQuery();

            PdfWriter.getInstance(document, archivo);
            document.open();
            document.add(new Paragraph("Reporte de Ventas por Día: " + fechaSeleccionada + "\n\n"));

            // Crear la tabla con cuatro columnas: Fecha, Plato, Cantidad Vendida y Total Ventas
            PdfPTable table = new PdfPTable(4);
            table.addCell("Fecha");
            table.addCell("Plato");
            table.addCell("Cantidad Vendida");
            table.addCell("Total Ventas");

            double totalGeneral = 0.0;
            while (rs.next()) {
                String fecha = rs.getString("fecha");
                String nombrePlato = rs.getString("plato");
                int totalCantidad = rs.getInt("total_cantidad");
                double totalVentas = rs.getDouble("total_ventas");

                table.addCell(fecha);
                table.addCell(nombrePlato);
                table.addCell(String.valueOf(totalCantidad));
                table.addCell(String.format("%.2f", totalVentas)); // Formato para mostrar el total

                totalGeneral += totalVentas;  // Sumar el total vendido
            }

            // Añadir la tabla al documento
            document.add(table);

            // Agregar el total general
            document.add(new Paragraph("\nTotal Vendido: " + totalGeneral));

            document.close();
            System.out.println("Reporte de ventas por día generado con éxito en 'reporteDia.pdf'.");
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
        String sqlVentas = "SELECT fecha, total FROM pedidos WHERE DATE_FORMAT(fecha, '%Y-%m') = ? ORDER BY fecha";
        String sqlPlatos = "SELECT dp.nombre AS plato, SUM(dp.cantidad) AS total_cantidad, SUM(dp.cantidad * dp.precio) AS total_ventas " +
                           "FROM detalle_pedidos dp " +
                           "JOIN pedidos p ON p.id = dp.id_pedido " +
                           "WHERE DATE_FORMAT(p.fecha, '%Y-%m') = ? " +
                           "GROUP BY dp.nombre " +
                           "ORDER BY total_ventas DESC";

        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + File.separator + "ventasMes.pdf");
        archivo = new FileOutputStream(salida);

        Document document = new Document();

        try {
            con = cn.getConnection();

            // Consulta para obtener las ventas totales por fecha
            ps = con.prepareStatement(sqlVentas);
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

            // Añadir la tabla de ventas totales al documento
            document.add(table);

            // Agregar el total mensual
            document.add(new Paragraph("\nTotal Vendido en el Mes: " + totalMensual + "\n\n"));

            // Consulta para obtener los platos más vendidos
            ps = con.prepareStatement(sqlPlatos);
            ps.setString(1, mesSeleccionado);
            rs = ps.executeQuery();

            // Crear la tabla para los platos más vendidos
            PdfPTable tablePlatos = new PdfPTable(3);
            tablePlatos.addCell("Plato");
            tablePlatos.addCell("Cantidad Vendida");
            tablePlatos.addCell("Total Ventas");

            while (rs.next()) {
                String nombrePlato = rs.getString("plato");
                int totalCantidad = rs.getInt("total_cantidad");
                double totalVentasPlato = rs.getDouble("total_ventas");

                tablePlatos.addCell(nombrePlato);
                tablePlatos.addCell(String.valueOf(totalCantidad));
                tablePlatos.addCell(String.format("%.2f", totalVentasPlato)); // Formato para mostrar el total
            }

            // Añadir la tabla de platos más vendidos al documento
            document.add(new Paragraph("\nPlatos Más Vendidos:\n"));
            document.add(tablePlatos);

            document.close();
            System.out.println("Reporte de ventas por mes generado con éxito en 'ventasMes.pdf'.");
            Desktop.getDesktop().open(salida);

        } catch (SQLException | DocumentException | IOException e) {
            System.out.println(e.toString());
        }
    }


    // Reporte de cantidad de platos vendidos con tabla
    public void generarReporteCantidadPorPlatoEnPDF() throws FileNotFoundException {
        String sql = "SELECT nombre, SUM(cantidad) AS total_cantidad, precio FROM detalle_pedidos GROUP BY nombre ORDER BY total_cantidad DESC";

        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + File.separator + "ventasPlatos.pdf");
        archivo = new FileOutputStream(salida);

        Document document = new Document();

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            PdfWriter.getInstance(document, archivo);
            document.open();
            document.add(new Paragraph("Reporte de Cantidad Vendida por Plato\n\n"));

            // Crear la tabla con tres columnas: Plato, Cantidad Vendida y Valor Total
            PdfPTable table = new PdfPTable(3);
            table.addCell("Plato");
            table.addCell("Cantidad Vendida");
            table.addCell("Valor Total");

            // Variables para almacenar la cantidad mínima vendida y la lista de platos menos vendidos
            int cantidadMinima = Integer.MAX_VALUE;
            List<String> platosMenosVendidos = new ArrayList<>();

            // Recorrer el ResultSet
            while (rs.next()) {
                String nombrePlato = rs.getString("nombre");
                int cantidadVendida = rs.getInt("total_cantidad");
                double precioPlato = rs.getDouble("precio");
                double valorTotal = cantidadVendida * precioPlato;

                // Añadir a la tabla
                table.addCell(nombrePlato);
                table.addCell(String.valueOf(cantidadVendida));
                table.addCell(String.format("%.2f", valorTotal)); // Formato para mostrar el valor total

                // Lógica para encontrar los platos menos vendidos
                if (cantidadVendida < cantidadMinima) {
                    cantidadMinima = cantidadVendida;
                    platosMenosVendidos.clear(); // Limpiar la lista si encontramos un nuevo mínimo
                    platosMenosVendidos.add(nombrePlato);
                } else if (cantidadVendida == cantidadMinima) {
                    platosMenosVendidos.add(nombrePlato); // Añadir a la lista de platos menos vendidos
                }
            }

            // Añadir la tabla al documento
            document.add(table);

            // Resaltar los platos menos vendidos
            document.add(new Paragraph("\nPlatos Menos Vendidos:", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
            for (String plato : platosMenosVendidos) {
                Paragraph p = new Paragraph(plato, new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE, BaseColor.RED));
                document.add(p);
            }

            document.close();

            System.out.println("Reporte de cantidad por plato generado con éxito en 'ventasPlatos.pdf'.");

            Desktop.getDesktop().open(salida);

        } catch (SQLException | DocumentException | IOException e) {
            System.out.println(e.toString());
        }
    }



    
}

    
    

