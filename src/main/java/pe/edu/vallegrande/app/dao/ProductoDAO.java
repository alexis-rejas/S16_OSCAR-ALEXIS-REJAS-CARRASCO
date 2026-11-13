package pe.edu.vallegrande.app.dao;

import pe.edu.vallegrande.app.config.ConexionDB;
import pe.edu.vallegrande.app.model.Producto;

import java.sql.*;
import java.util.*;

public class ProductoDAO {
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM producto"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Producto(rs.getInt("id_producto"), rs.getString("nombre"), rs.getString("categoria"), rs.getString("marca"), rs.getDouble("precio_compra"), rs.getDouble("precio_venta"), rs.getInt("stock"), rs.getString("proveedor")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean registrar(Producto p) {
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO producto (nombre, categoria, marca, precio_compra, precio_venta, stock, proveedor) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getMarca());
            ps.setDouble(4, p.getPrecioCompra());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getProveedor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Producto p) {
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE producto SET nombre=?, categoria=?, marca=?, precio_compra=?, precio_venta=?, stock=?, proveedor=? WHERE id_producto=?")) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setString(3, p.getMarca());
            ps.setDouble(4, p.getPrecioCompra());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getProveedor());
            ps.setInt(8, p.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM producto WHERE id_producto=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}