package business;

import data.Estatus;
import javafx.collections.ObservableList;

import java.math.BigDecimal;

public class Articulo {

    private Articulo() {

    }

    public static String insertar(String codigo, String nombre, String descripcion, BigDecimal precio,
                                  int unidadesStock) {
        data.Articulo articulo = new data.Articulo(codigo, nombre, descripcion, precio, unidadesStock);
        return articulo.insertar(articulo);
    }

    public static String actualizar(int id, String codigo, String nombre, String descripcion, BigDecimal precio,
                                    int unidadesStock, Estatus estatus) {
        data.Articulo articulo = new data.Articulo(id, codigo, nombre, descripcion, precio, unidadesStock, estatus);
        return articulo.actualizar(articulo);
    }

    public static ObservableList<data.Articulo> buscar(String textoABuscar) {
        data.Articulo articulo = new data.Articulo(textoABuscar);
        return articulo.buscar(articulo);
    }

    public static ObservableList<data.Articulo> mostrar() {
        return new data.Articulo().mostrar();
    }

    public static ObservableList<data.Articulo> mostrarActivos() {
        return new data.Articulo().mostrarActivos();
    }

}
