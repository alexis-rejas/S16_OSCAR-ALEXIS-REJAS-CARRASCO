package pe.edu.vallegrande.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.vallegrande.app.dao.ProductoDAO;
import pe.edu.vallegrande.app.model.Producto;

import java.util.function.UnaryOperator;

public class ProductoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtMarca;
    @FXML private TextField txtPrecioCompra;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtStock;
    @FXML private TextField txtProveedor;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, Double> colPrecioCompra;
    @FXML private TableColumn<Producto, Double> colPrecioVenta;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colProveedor;

    private final ProductoDAO dao = new ProductoDAO();
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private Producto productoSeleccionado = null;

    @FXML
    public void initialize() {
        configurarColumnas();
        aplicarRestriccionesEntrada();
        cargar();

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                productoSeleccionado = newSel;
                txtNombre.setText(newSel.getNombre());
                txtCategoria.setText(newSel.getCategoria());
                txtMarca.setText(newSel.getMarca());
                txtPrecioCompra.setText(String.valueOf(newSel.getPrecioCompra()));
                txtPrecioVenta.setText(String.valueOf(newSel.getPrecioVenta()));
                txtStock.setText(String.valueOf(newSel.getStock()));
                txtProveedor.setText(newSel.getProveedor());
            }
        });
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
    }

    private void aplicarRestriccionesEntrada() {
        // Solo letras (incluye tildes, ñ y espacios), máx. 60 caracteres
        UnaryOperator<TextFormatter.Change> soloLetrasFilter = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.length() <= 60 && nuevoTexto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                return change;
            }
            return null;
        };

        // Solo enteros positivos hasta 6 dígitos (máx. 999,999 para stock)
        UnaryOperator<TextFormatter.Change> soloEnterosFilter = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d{0,6}")) {
                return change;
            }
            return null;
        };

        // Solo decimales positivos hasta 6 enteros y 2 decimales (máx. 999999.99)
        UnaryOperator<TextFormatter.Change> soloDecimalesFilter = change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d{0,6}(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        };

        txtNombre.setTextFormatter(new TextFormatter<>(soloLetrasFilter));
        txtCategoria.setTextFormatter(new TextFormatter<>(soloLetrasFilter));
        txtMarca.setTextFormatter(new TextFormatter<>(soloLetrasFilter));
        txtProveedor.setTextFormatter(new TextFormatter<>(soloLetrasFilter));

        txtStock.setTextFormatter(new TextFormatter<>(soloEnterosFilter));
        txtPrecioCompra.setTextFormatter(new TextFormatter<>(soloDecimalesFilter));
        txtPrecioVenta.setTextFormatter(new TextFormatter<>(soloDecimalesFilter));
    }

    private boolean validarFormulario() {
        String nombre = txtNombre.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String marca = txtMarca.getText().trim();
        String proveedor = txtProveedor.getText().trim();
        String pCompraStr = txtPrecioCompra.getText().trim();
        String pVentaStr = txtPrecioVenta.getText().trim();
        String stockStr = txtStock.getText().trim();

        if (nombre.isEmpty() || categoria.isEmpty() || marca.isEmpty() ||
                proveedor.isEmpty() || pCompraStr.isEmpty() || pVentaStr.isEmpty() || stockStr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Todos los campos son obligatorios.");
            return false;
        }

        if (nombre.length() < 3) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nombre muy corto", "El nombre del producto debe tener al menos 3 letras.");
            return false;
        }

        try {
            double pCompra = Double.parseDouble(pCompraStr);
            double pVenta = Double.parseDouble(pVentaStr);
            int stock = Integer.parseInt(stockStr);

            if (pCompra <= 0 || pVenta <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precios inválidos", "Los precios deben ser mayores a 0.");
                return false;
            }

            if (pCompra > 999999.99 || pVenta > 999999.99) {
                mostrarAlerta(Alert.AlertType.WARNING, "Monto excedido", "Los precios no pueden superar 999,999.99.");
                return false;
            }

            if (pVenta < pCompra) {
                mostrarAlerta(Alert.AlertType.WARNING, "Margen inválido", "El precio de venta no puede ser menor al precio de compra.");
                return false;
            }

            if (stock < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido", "El stock no puede ser negativo.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de formato", "Verifique que los precios y el stock sean números válidos.");
            return false;
        }

        return true;
    }

    @FXML
    public void onRegistrar() {
        if (!validarFormulario()) return;

        Producto p = new Producto();
        p.setNombre(txtNombre.getText().trim());
        p.setCategoria(txtCategoria.getText().trim());
        p.setMarca(txtMarca.getText().trim());
        p.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
        p.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
        p.setStock(Integer.parseInt(txtStock.getText().trim()));
        p.setProveedor(txtProveedor.getText().trim());

        if (dao.registrar(p)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto registrado correctamente.");
            limpiar();
            cargar();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el producto.");
        }
    }

    @FXML
    public void onActualizar() {
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Seleccione un producto de la tabla para actualizar.");
            return;
        }
        if (!validarFormulario()) return;

        productoSeleccionado.setNombre(txtNombre.getText().trim());
        productoSeleccionado.setCategoria(txtCategoria.getText().trim());
        productoSeleccionado.setMarca(txtMarca.getText().trim());
        productoSeleccionado.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
        productoSeleccionado.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
        productoSeleccionado.setStock(Integer.parseInt(txtStock.getText().trim()));
        productoSeleccionado.setProveedor(txtProveedor.getText().trim());

        if (dao.actualizar(productoSeleccionado)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto actualizado correctamente.");
            limpiar();
            cargar();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el producto.");
        }
    }

    @FXML
    public void onEliminar() {
        if (productoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Seleccione un producto de la tabla para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas eliminar '" + productoSeleccionado.getNombre() + "'?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmar eliminación");
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            if (dao.eliminar(productoSeleccionado.getIdProducto())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "Producto eliminado correctamente.");
                limpiar();
                cargar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el producto.");
            }
        }
    }

    @FXML
    public void onLimpiar() {
        limpiar();
    }

    private void cargar() {
        listaProductos.setAll(dao.listar());
        tblProductos.setItems(listaProductos);
    }

    private void limpiar() {
        txtNombre.clear();
        txtCategoria.clear();
        txtMarca.clear();
        txtPrecioCompra.clear();
        txtPrecioVenta.clear();
        txtStock.clear();
        txtProveedor.clear();
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}