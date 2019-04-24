package business;

import data.Estatus;
import javafx.collections.ObservableList;

public class Cliente {

    private Cliente(){
        
    }
    
    public static String insertar(String nombre, String direccion, String ciudad, String email,
            String telefono, String codigoPostal, String pais) {
        data.Cliente cliente = new data.Cliente(nombre, direccion, ciudad, email,
            telefono, codigoPostal, pais);

        return new data.Cliente().insertar(cliente);
    }

    public static String actualizar(int id, String nombre, String direccion, String ciudad, String email,
            String telefono, String codigoPostal, String pais, Estatus estatus) {
        data.Cliente cliente = new data.Cliente(id, nombre, direccion, ciudad, email,
            telefono, codigoPostal, pais, estatus);
        return new data.Cliente().actualizar(cliente);
    }

    public static ObservableList<data.Cliente> buscar(String textoABuscar) {
        //data.Cliente cliente = new data.Cliente(textoABuscar);
        return new data.Cliente().buscar(textoABuscar);
    }

    public static ObservableList<data.Cliente> buscarActivos(String textoABuscar) {
        //data.Cliente cliente = new data.Cliente(textoABuscar);
        return new data.Cliente().buscarActivos(textoABuscar);
    }

    public static ObservableList<data.Cliente> mostrar() {
        return new data.Cliente().mostrar();
    }

    public static ObservableList<data.Cliente> mostrarActivos() {
        return new data.Cliente().mostrarActivos();
    }

}
