package view.factura;

import com.jfoenix.controls.JFXTextField;
import data.Factura;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import view.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FacturaController implements Initializable {

    private static Stage primaryStage;

    public static void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = FXMLLoader.load(FacturaController.class.getResource("Factura.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ver Facturas");
    }

    @FXML
    TableView<Factura> tableView;
    @FXML
    private TableColumn<?, ?> columnaClienteNo;
    @FXML
    private TableColumn<?, ?> columnaClienteNombre;
    @FXML
    private TableColumn<?, ?> columnaNo;
    @FXML
    private TableColumn<?, ?> columnaFecha;
    @FXML
    private TableColumn<?, ?> columnaHora;
    @FXML
    private TableColumn<?, ?> columnaTotalBruto;
    @FXML
    private TableColumn<?, ?> columnaTotalDescuento;
    @FXML
    private TableColumn<?, ?> columnaTotalImpuesto;
    @FXML
    private TableColumn<?, ?> columnaTotalCargo;
    @FXML
    private TableColumn<?, ?> columnaTotalNeto;

    private void initTabla() {
        columnaClienteNo.setCellValueFactory(new PropertyValueFactory<>("clienteId"));
        columnaClienteNombre.setCellValueFactory(new PropertyValueFactory<>("clienteNombre"));
        columnaNo.setCellValueFactory(new PropertyValueFactory<>("numero"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        columnaTotalBruto.setCellValueFactory(new PropertyValueFactory<>("totalBruto"));
        columnaTotalDescuento.setCellValueFactory(new PropertyValueFactory<>("totalDescuento"));
        columnaTotalImpuesto.setCellValueFactory(new PropertyValueFactory<>("totalImpuesto"));
        columnaTotalCargo.setCellValueFactory(new PropertyValueFactory<>("totalCargo"));
        columnaTotalNeto.setCellValueFactory(new PropertyValueFactory<>("totalNeto"));
    }

    @FXML
    private TreeView<String> treeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTabla();
        tableView.setItems(business.Factura.mostrar());
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (v, oldValue, newValue) -> {
                    if (newValue == null)
                        return;
                    factura = newValue;
                    numero.setText(String.valueOf(factura.getNumero()));
                    botonVerDetalles.setDisable(false);
                }
        );
        treeView.setRoot(Main.iniciarItems());
        treeView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                Main.cambiarScene(primaryStage, newValue));
    }


    @FXML
    private JFXTextField numero;
    @FXML
    private Button botonVerDetalles;
    private Factura factura = new Factura();

    @FXML
    private void verDetalles() throws IOException {
        // Cargame un stage con los detalles, envia cliente No y ClienteNombre
        numero.clear();
        botonVerDetalles.setDisable(true);
        Stage primaryStage = new Stage();
        FacturaDetallesController.start(primaryStage, factura);
        tableView.setItems(business.Factura.mostrar());
    }


}
