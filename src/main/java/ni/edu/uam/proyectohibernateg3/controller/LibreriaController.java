package ni.edu.uam.proyectohibernateg3.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.proyectohibernateg3.dao.CategoriaDAO;
import ni.edu.uam.proyectohibernateg3.dao.LibroDAO;
import ni.edu.uam.proyectohibernateg3.model.Categoria;
import ni.edu.uam.proyectohibernateg3.model.Libro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class LibreriaController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private TextField txtAnio;
    @FXML
    private TextField txtPrecio;
    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnCargar;
    @FXML
    private Button btnGuardar;

    @FXML
    private TableView<Libro> tblLibros;

    @FXML
    private TableColumn<Libro, Long> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, Integer> colAnio;
    @FXML
    private TableColumn<Libro, BigDecimal> colPrecio;
    @FXML
    private TableColumn<Libro, String> colCategoria;
    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private Long idSeleccionado = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        tblLibros.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                seleccionarLibro(newValue);
            }
        });

        actualizarBotones();

        try {
            cargarCategorias();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudieron cargar las categorías: " + ex.getMessage());
        }
    }

    private void cargarCategorias() {
        cmbCategoria.setItems(FXCollections.observableList(categoriaDAO.listar()));
    }

    @FXML
    private void guardarLibro() {
        try
            {
            Libro libro = obtenerFormulario();
            if (libro == null) {
                return;
            }
            libroDAO.guardar(libro);
            limpiarCampos();
            cargarLibros();
            }catch(Exception ex){
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar el libro: " + ex.getMessage());
        }
    }

    @FXML
    private void cargarLibros() {
        try {
            List<Libro> Libros = libroDAO.listar();
            // Refresca también las categorías por si se agregaron nuevas en la base de datos
            cargarCategorias();
            limpiarCampos();
            tblLibros.setItems(FXCollections.observableList(Libros));
        }catch(Exception ex){
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudieron cargar los libros: " + ex.getMessage());
        }
    }

    @FXML
    private void modificarLibro() {
        Libro libro = obtenerFormulario();
        if (libro == null) {
            return;
        }
        libro.setId(idSeleccionado);
        try {
            libroDAO.actualizar(libro);
            limpiarCampos();
            cargarLibros();
        } catch (RuntimeException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo modificar el libro: " + ex.getMessage());
        }
    }

    @FXML
    private void eliminarLibro() {
        try{
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmacion de eliminacion de datos");
            confirmacion.setHeaderText("Eliminar libro");
            confirmacion.setContentText("¿Estas seguro que desea eliminar el registro seleccionado ?");

            Optional<ButtonType> resultado =confirmacion.showAndWait();
            if(resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return;
            }
            boolean eliminado = libroDAO.eliminar(idSeleccionado);
            if (!eliminado){
                mostrarAlerta(Alert.AlertType.WARNING, "El libro ya no existe en la base de datos.");
                return;
            }
            limpiarCampos();
            cargarLibros();

        }catch (Exception ex){
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar el libro: " + ex.getMessage());
        }
    }

    @FXML
    private void limpiarCampos() {
        txtTitulo.clear();
        txtAutor.clear();
        txtAnio.clear();
        txtPrecio.clear();
        cmbCategoria.setValue(null);
        idSeleccionado = null;
        tblLibros.getSelectionModel().clearSelection();
        actualizarBotones();
    }

    private void seleccionarLibro(Libro libro) {
        idSeleccionado = libro.getId();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtPrecio.setText(String.valueOf(libro.getPrecio()));
        txtAnio.setText(String.valueOf(libro.getAnioPublicacion()));
        cmbCategoria.setValue(buscarCategoria(libro.getCategoria()));
        actualizarBotones();
    }

    // Busca en el ComboBox la categoría con el mismo id; se compara por id porque
    // la categoría del libro y las del ComboBox son objetos de consultas distintas
    private Categoria buscarCategoria(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        for (Categoria c : cmbCategoria.getItems()) {
            if (c.getId().equals(categoria.getId())) {
                return c;
            }
        }
        return null;
    }

    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;
        btnModificar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    // Lee y valida los campos; devuelve null (y muestra un aviso) si algún dato es inválido
    private Libro obtenerFormulario() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        if (titulo.isEmpty() || autor.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "El título y el autor son obligatorios.");
            return null;
        }

        int anio;
        try {
            anio = Integer.parseInt(txtAnio.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "El año debe ser un número entero.");
            return null;
        }
        // Mismo rango que la restricción chk_anio de la tabla
        if (anio < 1450 || anio > 2100) {
            mostrarAlerta(Alert.AlertType.WARNING, "El año debe estar entre 1450 y 2100.");
            return null;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "El precio debe ser un número válido (ej. 250.50).");
            return null;
        }
        // Mismo límite que la restricción chk_precio de la tabla
        if (precio.signum() < 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "El precio no puede ser negativo.");
            return null;
        }

        // La columna categoria_id es NOT NULL, así que la categoría es obligatoria
        Categoria categoria = cmbCategoria.getValue();
        if (categoria == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Debe seleccionar una categoría.");
            return null;
        }

        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setAnioPublicacion(anio);
        libro.setPrecio(precio);
        libro.setCategoria(categoria);
        return libro;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
