package view.factura;

import data.Factura;
import data.FacturaDetalle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class FacturaDetallesController implements Initializable {
    private static Stage primaryStage;
    private static Factura factura_;

    static void start(Stage stage, Factura factura) throws IOException {
        primaryStage = stage;
        factura_ = factura;
        Parent root = FXMLLoader.load(FacturaDetallesController.class.getResource("FacturaDetalles.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ver Facturas");
        primaryStage.showAndWait();
    }


    @FXML
    private TableView<FacturaDetalle> tableView;
    @FXML
    private TableColumn<?, ?> columnaNo;
    @FXML
    private TableColumn<?, ?> columnaCodigo;
    @FXML
    private TableColumn<?, ?> columnaNombre;
    @FXML
    private TableColumn<?, ?> columnaDescripcion;
    @FXML
    private TableColumn<?, ?> columnaCantidad;
    @FXML
    private TableColumn<?, ?> columnaPrecio;
    @FXML
    private TableColumn<?, ?> columnaDescuento;
    @FXML
    private TableColumn<?, ?> columnaImpuesto;
    @FXML
    private TableColumn<?, ?> columnaNeto;

    private void initTabla() {
        columnaNo.setCellValueFactory(new PropertyValueFactory<>("linea"));
        columnaCodigo.setCellValueFactory(new PropertyValueFactory<>("articuloCodigo"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("articuloNombre"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("articuloDescripcion"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        columnaImpuesto.setCellValueFactory(new PropertyValueFactory<>("impuesto"));
        columnaNeto.setCellValueFactory(new PropertyValueFactory<>("neto"));
    }

    @FXML
    private Label id;
    @FXML
    private Label nombre;
    @FXML
    private Label numero;
    @FXML
    private Label fecha;
    @FXML
    private Label hora;
    @FXML
    private Label totalBruto;
    @FXML
    private Label totalDescuento;
    @FXML
    private Label totalImpuesto;
    @FXML
    private Label totalCargo;
    @FXML
    private Label totalNeto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTabla();
        initLabels();
        tableView.setItems(business.Factura.mostrarDetalles(factura_.getId()));
    }

    private void initLabels() {
        id.setText(String.valueOf(factura_.getClienteId()));
        nombre.setText(factura_.getClienteNombre());
        numero.setText(String.valueOf(factura_.getNumero()));
        fecha.setText(factura_.getFecha().toString());
        hora.setText(factura_.getHora().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        DecimalFormat f = new DecimalFormat("###,##0.00");
        totalBruto.setText(f.format(factura_.getTotalBruto()));//.setScale(2, RoundingMode.CEILING).toPlainString()
        totalDescuento.setText(f.format(factura_.getTotalDescuento()));
        totalImpuesto.setText(f.format(factura_.getTotalImpuesto()));
        totalCargo.setText(f.format(factura_.getTotalCargo()));
        totalNeto.setText(f.format(factura_.getTotalNeto()));
    }

    @FXML
    private void cancelarOrden() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("¿Desea cancelar la factura?");
        alerta.setHeaderText("La factura va a ser cancelada.");
        if (alerta.showAndWait().isPresent()) {
            Alert insercion = new Alert(Alert.AlertType.INFORMATION,
                    business.Factura.eliminar(factura_.getId()));
            insercion.show();
            primaryStage.close();
        }

    }

}
