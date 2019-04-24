package view.cliente;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import data.Cliente;
import data.Estatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import view.Main;
import view.RadioButtonCell;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {

    private static Stage primaryStage;

    public static void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = FXMLLoader.load(ClienteController.class.getResource("Cliente.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ver Clientes");
    }

    @FXML
    private JFXTextField nombre;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField telefono;
    @FXML
    private JFXTextField direccion;
    @FXML
    private JFXTextField ciudad;
    @FXML
    private JFXTextField codigoPostal;

    @FXML
    private JFXComboBox<String> paises;

    @FXML
    private TableView<Cliente> tableView;
    @FXML
    private TableColumn<Cliente, Integer> columnaNo;
    @FXML
    private TableColumn<Cliente, String> columnaNombre;
    @FXML
    private TableColumn<Cliente, String> columnaEmail;
    @FXML
    private TableColumn<Cliente, String> columnaTelefono;
    @FXML
    private TableColumn<Cliente, String> columnaDireccion;
    @FXML
    private TableColumn<Cliente, String> columnaPais;
    @FXML
    private TableColumn<Cliente, String> columnaCiudad;
    @FXML
    private TableColumn<Cliente, String> columnaCodigoPostal;
    @FXML
    private TableColumn<Cliente, Estatus> columnaEstatus;

    @FXML
    private TreeView<String> treeView;


    private void initTabla() {
        columnaNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columnaDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        columnaPais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        columnaCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        columnaCodigoPostal.setCellValueFactory(new PropertyValueFactory<>("codigoPostal"));
        columnaEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

        columnaNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaTelefono.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaDireccion.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaPais.setCellFactory(ChoiceBoxTableCell.forTableColumn(data));
        columnaCiudad.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaCodigoPostal.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaEstatus.setCellFactory((param) -> new RadioButtonCell<>(EnumSet.allOf(Estatus.class)));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarPaises();
        initTabla();
        tableView.setItems(business.Cliente.mostrar());
        tableView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                cliente = newValue
        );
        treeView.setRoot(Main.iniciarItems());
        treeView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                Main.cambiarScene(primaryStage, newValue));
    }

    private final ObservableList<String> data = FXCollections.observableArrayList();
    private void llenarPaises() {
        data.add("");
        for (String countrylist : Locale.getISOCountries()) {
            Locale pais = new Locale("", countrylist);
            data.add(pais.getDisplayCountry());
        }
        Collections.sort(data);
        paises.setItems(data);
    }

    private void clear() {
        nombre.setText("");
        email.setText("");
        telefono.setText("");
        direccion.setText("");
        codigoPostal.setText("");
        ciudad.setText("");
        paises.getSelectionModel().select(null);
    }

    @FXML
    private void buscar() {
        tableView.setItems(business.Cliente.buscar(nombre.getText()));
    }

    private boolean haConfirmado() {
        String mensaje = ("Nombre: " + nombre.getText() + "\n") +
                "Email: " + email.getText() + "\n" +
                "Telefono: " + telefono.getText() + "\n" +
                "Direccion: " + direccion.getText() + "\n" +
                "Pais: " + paises.getSelectionModel().getSelectedItem() + "\n" +
                "Ciudad: " + ciudad.getText() + "\n" +
                "Codigo Postal: " + codigoPostal.getText() + "\n";

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("\"¿Desea continuar?\"");
        alerta.setHeaderText(mensaje);
        return alerta.showAndWait().isPresent();
    }


    @FXML
    private void agregar() {
        if (haConfirmado()) {
            String context = business.Cliente.insertar(
                    nombre.getText(), direccion.getText(), ciudad.getText(), email.getText(), telefono.getText(),
                    codigoPostal.getText(), paises.getValue());
            Alert insercion = new Alert(Alert.AlertType.INFORMATION, context);
            insercion.show();
            tableView.setItems(business.Cliente.mostrar());
            clear();
        }

    }

    private Cliente cliente = new Cliente();

    @FXML
    private void actualizar(TableColumn.CellEditEvent newValue) {
        Cliente cliente = (Cliente) newValue.getTableView().getItems().get(
                newValue.getTablePosition().getRow()
        );
        if (cliente == null)
            return;
        if (newValue.getNewValue().equals(newValue.getOldValue()))
            return;

        TableColumn col = newValue.getTableColumn();
        String value = newValue.getNewValue().toString();

        if (col.equals(columnaNombre)){
            cliente.setNombre(value);
        } else if(col.equals(columnaEmail)){
            if (value.toLowerCase().equals(newValue.getOldValue())) {
                cliente.setEmail(newValue.getOldValue().toString());
                initTabla();
                return;
            } else{
                cliente.setEmail(value.toLowerCase());
            }
        } else if (col.equals(columnaTelefono)){
            cliente.setTelefono(value);
        } else if (col.equals(columnaDireccion)){
            cliente.setDireccion(value);
        } else if (col.equals(columnaCodigoPostal)){
            cliente.setCodigoPostal(value);
        } else if (col.equals(columnaCiudad)){
            cliente.setCiudad(value);
        } else if (col.equals(columnaPais)){
            cliente.setPais(value);
        } else {
            cliente.setEstatus(Estatus.valueOf(newValue.getNewValue().toString().toUpperCase()));
        }

        String context = business.Cliente.actualizar(
                cliente.getId(), cliente.getNombre(), cliente.getDireccion(), cliente.getCiudad(), cliente.getEmail(),
                cliente.getTelefono(), cliente.getCodigoPostal(), cliente.getPais(), cliente.getEstatus()
        );
        Alert insercion = new Alert(Alert.AlertType.INFORMATION, context);
        insercion.show();
        tableView.setItems(business.Cliente.mostrar());

    }
}
