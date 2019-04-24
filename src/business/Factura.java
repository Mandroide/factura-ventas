package business;

import data.FacturaDetalle;
import javafx.collections.ObservableList;

public class Factura {

    private Factura() {

    }


    // Listo.
    public static String eliminar(int id) {
        data.Factura factura = new data.Factura(id);
        return new data.Factura().eliminar(factura);
    }

    // Listo.
    public static ObservableList<data.Factura> mostrar() {
        return new data.Factura().mostrar();
    }

    // Listo.
    public static ObservableList<FacturaDetalle> mostrarDetalles(int numero) {
        data.Factura factura = new data.Factura();
        return factura.mostrarDetalles(numero);
    }

}