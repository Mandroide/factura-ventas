package view;

import javafx.application.Application;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import view.articulo.ArticuloController;
import view.cliente.ClienteActivoController;
import view.cliente.ClienteController;
import view.factura.FacturaController;

import java.io.IOException;

import static javafx.application.Platform.exit;


public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.getIcons().add(new Image("view/resources/main.png"));
        primaryStage.setOnCloseRequest((WindowEvent e) ->
                exit()
        );
        ClienteController.start(primaryStage);
        primaryStage.show();
    }

    public static TreeItem<String> iniciarItems() {
        ImageView iconCliente = new ImageView(
                new Image(Main.class.getResourceAsStream("/view/resources/clientes.png"))
        );
        iconCliente.setFitWidth(15);
        iconCliente.setFitHeight(12);
        TreeItem<String> nodoClientes = new TreeItem<>("Ver clientes", iconCliente);
        TreeItem<String> rootCliente = new TreeItem<>("Cliente");
        rootCliente.getChildren().add(nodoClientes);

        ImageView iconArticulo = new ImageView(
                new Image(Main.class.getResourceAsStream("/view/resources/articulos.png"))
        );
        iconArticulo.setFitWidth(15);
        iconArticulo.setFitHeight(12);
        TreeItem<String> nodoArticulos = new TreeItem<>("Ver articulos", iconArticulo);
        TreeItem<String> rootArticulo = new TreeItem<>("Articulo");
        rootArticulo.getChildren().add(nodoArticulos);


        ImageView iconFactura = new ImageView(
                new Image(Main.class.getResourceAsStream("/view/resources/facturas.png"))
        );
        iconFactura.setFitWidth(15);
        iconFactura.setFitHeight(12);
        TreeItem<String> nodoFacturas = new TreeItem<>("Ver facturas", iconFactura);
        ImageView iconComprar = new ImageView(
                new Image(Main.class.getResourceAsStream("/view/resources/comprar.png"))
        );
        iconComprar.setFitWidth(15);
        iconComprar.setFitHeight(12);
        TreeItem<String> nodoCompras = new TreeItem<>("Efectuar una compra", iconComprar);
        TreeItem<String> rootFactura = new TreeItem<>("Factura");
        rootFactura.getChildren().add(nodoFacturas);
        rootFactura.getChildren().add(nodoCompras);


        TreeItem<String> root = new TreeItem<>("NULL");
        root.getChildren().addAll(rootCliente, rootArticulo, rootFactura);

        return root;

    }

    public static void cambiarScene(Stage primaryStage, TreeItem<String> item) {
        if (item == null)
            return;
        try {
            switch (item.getValue()) {
                case "Ver clientes":
                    ClienteController.start(primaryStage);
                    break;
                case "Ver facturas":
                    FacturaController.start(primaryStage);
                    break;
                case "Ver articulos":
                    ArticuloController.start(primaryStage);
                    break;
                case "Efectuar una compra":
                    ClienteActivoController.start(primaryStage);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}