package view.articulo;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.Articulo;
import data.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import view.Main;
import view.cliente.ClienteActivoController;
import view.factura.FacturaActivaController;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ArticuloActivoController implements Initializable {
    @FXML
    private Label id_;
    @FXML
    private Label nombre_;
    @FXML
    private Label email_;

    private static Stage primaryStage;
    private static Cliente cliente_;

    public static void start(Stage stage, Cliente cliente) throws IOException {
        primaryStage = stage;
        cliente_ = cliente;
        Parent root = FXMLLoader.load(ArticuloActivoController.class.getResource("ArticuloActivo.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Seleccione articulos");

    }

    @FXML
    private TableView<Articulo> tableView;
    @FXML
    private TableColumn<?, ?> columnaNo;
    @FXML
    private TableColumn<?, ?> columnaNombre;
    @FXML
    private TableColumn<?, ?> columnaDescripcion;
    @FXML
    private TableColumn<?, ?> columnaPrecio;
    @FXML
    private TableColumn<?, ?> columnaUnidadesStock;
    @FXML
    private TableColumn<?, ?> columnaCodigo;

    private void initTabla() {
        columnaNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaUnidadesStock.setCellValueFactory(new PropertyValueFactory<>("unidadesStock"));
        columnaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
    }


    @FXML
    private TreeView<String> treeView;

    private final HashMap<Articulo, Integer> articulos = new HashMap<>();
    private Articulo articulo = new Articulo();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa labels
        id_.setText(String.valueOf(cliente_.getId()));
        nombre_.setText(cliente_.getNombre());
        email_.setText(cliente_.getEmail());
        //---------------------

        initTabla();
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (v, oldValue, newValue) -> {
                    if (newValue == null)
                        return;
                    articulo = newValue;
                    cantidad.setDisable(false);
                    botonAgregar.setDisable(false);
                }
        );
        tableView.getSelectionModel().selectedItemProperty().removeListener(
                (v, oldValue, newValue) -> {
                    cantidad.setDisable(true);
                    botonAgregar.setDisable(true);
                }
        );
        tableView.setItems(business.Articulo.mostrarActivos());
        treeView.setRoot(Main.iniciarItems());
        treeView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                Main.cambiarScene(primaryStage, newValue));
    }

    @FXML
    private void volver() throws IOException {
        ClienteActivoController.start(primaryStage);
    }

    @FXML
    private void continuar() throws IOException {

        FacturaActivaController.start(primaryStage, cliente_, articulos);
    }


    @FXML
    private void seleccionar() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    @FXML
    private JFXButton botonContinuar;
    @FXML
    private JFXButton botonAgregar;
    @FXML
    private JFXButton botonRemover;
    @FXML
    private JFXTextField cantidad;

    @FXML
    private void agregar() {
        articulos.put(articulo, Integer.parseUnsignedInt(cantidad.getText()));
        botonRemover.setDisable(false);
        botonContinuar.setDisable(false);
    }

    @FXML
    private void remover() {
        articulos.remove(articulo);
        articulos.remove(articulo);
        if (articulos.isEmpty()) {
            botonRemover.setDisable(true);
            botonContinuar.setDisable(true);
        }
    }
}
