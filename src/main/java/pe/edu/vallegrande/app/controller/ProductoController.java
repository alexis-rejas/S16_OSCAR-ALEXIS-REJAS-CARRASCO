package pe.edu.vallegrande.app.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.vallegrande.app.dao.ProductoDAO;
import pe.edu.vallegrande.app.model.Producto;

public class ProductoController {
    @FXML
    private TextField txtNombre, txtCategoria, txtMarca, txtPrecioCompra, txtPrecioVenta, txtStock, txtProveedor;
    @FXML
    private TableView<Producto> tblProductos;
    @FXML
    private TableColumn<Producto, Integer> colId, colStock;
    @FXML
    private TableColumn<Producto, String> colNombre, colCategoria, colMarca, colProveedor;
    @FXML
    private TableColumn<Producto, Double> colPrecioCompra, colPrecioVenta;
    private final ProductoDAO dao = new ProductoDAO();
    private Producto sel;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
        cargar();
        tblProductos.getSelectionModel().selectedItemProperty().addListener((o, a, n) -> {
            if (n != null) {
                sel = n;
                txtNombre.setText(n.getNombre());
                txtCategoria.setText(n.getCategoria());
                txtMarca.setText(n.getMarca());
                txtPrecioCompra.setText("" + n.getPrecioCompra());
                txtPrecioVenta.setText("" + n.getPrecioVenta());
                txtStock.setText("" + n.getStock());
                txtProveedor.setText(n.getProveedor());
            }
        });
    }

    private void cargar() {
        tblProductos.setItems(FXCollections.observableArrayList(dao.listar()));
    }

    @FXML
    private void onRegistrar() {
        try {
            if (dao.registrar(leer())) {
                alerta("OK", "Registrado");
                onLimpiar();
                cargar();
            }
        } catch (Exception e) {
            alerta("Error", "Datos invalidos");
        }
    }

    @FXML
    private void onActualizar() {
        if (sel == null) return;
        try {
            Producto p = leer();
            p.setIdProducto(sel.getIdProducto());
            if (dao.actualizar(p)) {
                alerta("OK", "Actualizado");
                onLimpiar();
                cargar();
            }
        } catch (Exception e) {
            alerta("Error", "Datos invalidos");
        }
    }

    @FXML
    private void onEliminar() {
        if (sel != null && dao.eliminar(sel.getIdProducto())) {
            alerta("OK", "Eliminado");
            onLimpiar();
            cargar();
        }
    }

    @FXML
    private void onLimpiar() {
        sel = null;
        txtNombre.clear();
        txtCategoria.clear();
        txtMarca.clear();
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        txtStock.clear();
        txtProveedor.clear();
        tblProductos.getSelectionModel().clearSelection();
    }

    private Producto leer() {
        return new Producto(0, txtNombre.getText(), txtCategoria.getText(), txtMarca.getText(), Double.parseDouble(txtPrecioCompra.getText()), Double.parseDouble(txtPrecioVenta.getText()), Integer.parseInt(txtStock.getText()), txtProveedor.getText());
    }

    private void alerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}