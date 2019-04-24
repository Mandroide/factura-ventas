package view.factura;

import data.Articulo;
import data.Cliente;
import data.FacturaDetalle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import view.Main;
import view.articulo.ArticuloActivoController;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

public class FacturaActivaController implements Initializable {

    private static data.Factura factura_ = new data.Factura();
    private static Stage primaryStage = new Stage();
    private static final ObservableList<FacturaDetalle> detalles_ = FXCollections.observableArrayList();

    public static void start(Stage stage, Cliente cliente,
                             Map<Articulo, Integer> articulos) throws IOException {
        primaryStage = stage;
        factura_ = new data.Factura(cliente);
        cargarDetalles(articulos);
        Parent root = load(FacturaActivaController.class.getResource("FacturaActiva.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Confirmar factura");
    }


    @FXML
    private Label id;
    @FXML
    private Label nombre;
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
    private TableColumn<?, Integer> columnaCantidad;
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
        columnaCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        calcularTotal();
    }

    private void calcularTotal() {

        BigDecimal bruto = new BigDecimal("0.00");
        BigDecimal descuento = new BigDecimal("0.00");
        BigDecimal impuesto = new BigDecimal("0.00");
        BigDecimal cargo = new BigDecimal(Math.random() * 300);
        BigDecimal neto = new BigDecimal(cargo.doubleValue());
        for (FacturaDetalle detalle : detalles_) {
            bruto = bruto.add(detalle.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            descuento = descuento.add(detalle.getDescuento().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            impuesto = impuesto.add(detalle.getImpuesto().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            neto = neto.add(detalle.getNeto().multiply(BigDecimal.valueOf(detalle.getCantidad())));
        }
        DecimalFormat format = new DecimalFormat("###,##0.00");
        factura_.setTotalBruto(bruto);
        factura_.setTotalImpuesto(impuesto);
        factura_.setTotalCargo(cargo);
        factura_.setTotalDescuento(descuento);
        factura_.setTotalNeto(neto);

        totalBruto.setText(format.format(factura_.getTotalBruto()));
        totalDescuento.setText(format.format(factura_.getTotalDescuento()));
        totalImpuesto.setText(format.format(factura_.getTotalImpuesto() ));
        totalCargo.setText(format.format(factura_.getTotalCargo()));
        totalNeto.setText(format.format(factura_.getTotalNeto()));
    }

    @FXML
    private TreeView<String> treeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTabla();
        tableView.setItems(detalles_);
        id.setText(String.valueOf(factura_.getClienteId()));
        nombre.setText(factura_.getClienteNombre());
        fecha.setText(LocalDate.now().toString());
        hora.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        treeView.setRoot(Main.iniciarItems());
        treeView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                Main.cambiarScene(primaryStage, newValue));
    }

    private static void cargarDetalles(Map<Articulo, Integer> articulos) {
        factura_.getDetalles().clear();
        short linea = 0;
        final double ITBIS = 0.18;
        for (Map.Entry<Articulo, Integer> entry : articulos.entrySet()) {
            FacturaDetalle detalle = new FacturaDetalle(factura_.getId(), entry.getKey(), entry.getValue());
            detalle.setLinea(++linea);
            detalle.setImpuesto(new BigDecimal(ITBIS * detalle.getPrecio().doubleValue())
                    .setScale(2, RoundingMode.CEILING));
            final double DESCT = 0.08 * Math.random();
            detalle.setDescuento(new BigDecimal( DESCT * detalle.getPrecio().doubleValue())
                    .setScale(2, RoundingMode.CEILING));
            detalle.setNeto(detalle.getPrecio().add(detalle.getImpuesto()).subtract(detalle.getDescuento()));
            factura_.getDetalles().add(detalle);
            detalles_.add(detalle);
        }

    }


    @FXML
    private void cambiarCantidad(TableColumn.CellEditEvent newValue){
        FacturaDetalle detalle = tableView.getSelectionModel().getSelectedItem();

        if (detalle == null || newValue == null)
            return;
        if (newValue.getNewValue().equals(newValue.getOldValue()))
            return;

        detalle.setCantidad((int)newValue.getNewValue());
    }

    @FXML
    private void confirmar() throws IOException {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("¿Desea continuar?");
        alerta.setHeaderText("Presione ok para confirmar la factura");
        if (alerta.showAndWait().isPresent()) {
            Alert insercion = new Alert(Alert.AlertType.INFORMATION,
                    factura_.insertar());
            insercion.show();
            FacturaController.start(primaryStage);
        }
    }

    @FXML
    private void cancelar() throws IOException {
        detalles_.clear();
        ArticuloActivoController.start(primaryStage, factura_.getCliente());
    }

}
