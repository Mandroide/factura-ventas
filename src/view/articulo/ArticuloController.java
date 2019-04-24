package view.articulo;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.DoubleValidator;
import com.jfoenix.validation.IntegerValidator;
import data.Articulo;
import data.Estatus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import view.Main;
import view.RadioButtonCell;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

public class ArticuloController implements Initializable {

    private static Stage primaryStage;

    public static void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = load(ArticuloController.class.getResource("Articulo.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ver Articulos");
    }

    @FXML
    private TableView<Articulo> tableView;
    @FXML
    private TableColumn<Articulo, Integer> columnaNo;
    @FXML
    private TableColumn<Articulo, String> columnaNombre;
    @FXML
    private TableColumn<Articulo, String> columnaDescripcion;
    @FXML
    private TableColumn<Articulo, BigDecimal> columnaPrecio;
    @FXML
    private TableColumn<Articulo, Integer> columnaUnidadesStock;
    @FXML
    private TableColumn<Articulo, String> columnaCodigo;
    @FXML
    private TableColumn<Articulo, Estatus> columnaEstatus;

    private void initTabla() {
        columnaNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaUnidadesStock.setCellValueFactory(new PropertyValueFactory<>("unidadesStock"));
        columnaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        columnaEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

        columnaCodigo.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaDescripcion.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaUnidadesStock.setCellFactory(TextFieldTableCell.forTableColumn(
                new IntegerStringConverter()
        ));
        columnaPrecio.setCellFactory(TextFieldTableCell.forTableColumn(
                new BigDecimalStringConverter()
        ));

        columnaEstatus.setCellFactory((param) -> new RadioButtonCell<>(EnumSet.allOf(Estatus.class)));
    }

    @FXML
    private TreeView<String> treeView;

    @FXML
    private JFXTextField nombre;
    @FXML
    private JFXTextArea descripcion;
    @FXML
    private JFXTextField unidadesStock;
    @FXML
    private JFXTextField precio;
    @FXML
    private JFXTextField codigo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTabla();
        tableView.setItems(business.Articulo.mostrar());

        treeView.setRoot(Main.iniciarItems());
        treeView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                Main.cambiarScene(primaryStage, newValue));
        tableView.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) ->
                articulo = newValue
        );

        unidadesStock.getValidators().add(new IntegerValidator("Solo numeros naturales"));
        unidadesStock.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue)
                unidadesStock.validate();
        });

        precio.getValidators().add(new DoubleValidator("Solo numeros reales no negativos"));

        precio.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue)
                precio.validate();
        });
    }

    @FXML
    private void buscar() {
        tableView.setItems(business.Articulo.buscar(nombre.getText()));
    }


    private boolean haConfirmado() {
        String mensaje = ("Codigo: " + codigo.getText() + "\n") +
                "Nombre: " + nombre.getText() + "\n" +
                "Descripcion: " + descripcion.getText() + "\n" +
                "Unidades en Stock: " + unidadesStock.getText() + "\n" +
                "Precio: " + precio.getText() + "\n";

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("\"¿Desea continuar?\"");
        alerta.setHeaderText(mensaje);
        return alerta.showAndWait().isPresent();
    }

    private void clear() {
        codigo.setText("");
        nombre.setText("");
        descripcion.setText("");
        unidadesStock.setText("");
        precio.setText("");
    }

    @FXML
    private void agregar() {
        if (haConfirmado()) {
            String context = business.Articulo.insertar(codigo.getText(), nombre.getText(), descripcion.getText(),
                    new BigDecimal(precio.getText()), Integer.parseInt(unidadesStock.getText()));
            Alert insercion = new Alert(Alert.AlertType.INFORMATION, context);
            insercion.show();
            tableView.setItems(business.Articulo.mostrar());
            clear();
        }
    }

    private Articulo articulo = new Articulo();

    @FXML
    private void actualizar(TableColumn.CellEditEvent newValue){

        Articulo articulo = (Articulo) newValue.getTableView().getItems().get(
                newValue.getTablePosition().getRow()
        );
        if (articulo == null)
            return;
        if (newValue.getNewValue().equals(newValue.getOldValue()))
            return;

        TableColumn col = newValue.getTableColumn();
        String value = newValue.getNewValue().toString();
        if(col.equals(columnaNombre)){
            articulo.setNombre(value);
        } else if (col.equals(columnaCodigo)){
            articulo.setCodigo(value);
        } else if (col.equals(columnaDescripcion)){
            articulo.setDescripcion(value);
        } else if(col.equals(columnaPrecio)){
            articulo.setPrecio(new BigDecimal(value));
        } else if (col.equals(columnaEstatus)){
            articulo.setEstatus(Estatus.valueOf(newValue.getNewValue().toString().toUpperCase()));
        } else if (col.equals(columnaUnidadesStock)){
            articulo.setUnidadesStock(Integer.parseInt(value));
        }

        String context = business.Articulo.actualizar(articulo.getId(), articulo.getCodigo(),
                articulo.getNombre(), articulo.getDescripcion(), articulo.getPrecio(),
                articulo.getUnidadesStock(), articulo.getEstatus());
        Alert insercion = new Alert(Alert.AlertType.INFORMATION, context);
        insercion.show();
        tableView.setItems(business.Articulo.mostrar());

    }


}
