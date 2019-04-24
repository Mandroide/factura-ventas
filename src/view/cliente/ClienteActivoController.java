package view.cliente;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import view.Main;
import view.articulo.ArticuloActivoController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClienteActivoController implements Initializable {

    private static Stage primaryStage;

    public static void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = FXMLLoader.load(ClienteActivoController.class.getResource("ClienteActivo.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Seleccione cliente");

    }

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TableView<Cliente> tableView;
    @FXML
    private TableColumn<?, ?> columnaNo;
    @FXML
    private TableColumn<?, ?> columnaNombre;
    @FXML
    private TableColumn<?, ?> columnaEmail;
    @FXML
    private TableColumn<?, ?> columnaTelefono;
    @FXML
    private TableColumn<?, ?> columnaDireccion;
    @FXML
    private TableColumn<?, ?> columnaPais;
    @FXML
    private TableColumn<?, ?> columnaCiudad;
    @FXML
    private TableColumn<?, ?> columnaCodigoPostal;

    private void initTabla() {
        columnaNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columnaDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        columnaPais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        columnaCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        columnaCodigoPostal.setCellValueFactory(new PropertyValueFactory<>("codigoPostal"));
    }

    @FXML
    private JFXTextField nombre;
    @FXML
    private JFXButton botonSiguiente;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTabla();
        tableView.setItems(business.Cliente.mostrarActivos());
        tableView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                {
                    cliente = newValue;
                    botonSiguiente.setDisable(cliente == null);
                }
        );
        treeView.setRoot(Main.iniciarItems());
        treeView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                Main.cambiarScene(primaryStage, newValue));

    }

    @FXML
    private void completarNombre() {
        tableView.setItems(business.Cliente.buscarActivos(nombre.getText()));
    }

    private Cliente cliente;

    @FXML
    private void continuar() throws IOException {
        if (cliente == null)
            return;
        ArticuloActivoController.start(primaryStage, cliente);
    }

}
