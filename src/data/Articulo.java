package data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Articulo {

    private int id_;
    private String nombre_;
    private String descripcion_;
    private BigDecimal precio_;
    private int unidadesStock_;
    private String codigo_;
    private Estatus estatus_;

    private String textoABuscar_;

    // Para realizar consultas a la base de datos.
    public Articulo() {

    }

    // Para actualizar.
    public Articulo(int id, String codigo, String nombre, String descripcion,
                    BigDecimal precio, int unidadesStock, Estatus estatus) {
        this(codigo, nombre, descripcion, precio, unidadesStock);
        setEstatus(estatus);
        setId(id);
    }

    // Para insertar
    public Articulo(String codigo, String nombre, String descripcion,
                    BigDecimal precio, int unidadesStock) {
        setCodigo(codigo);
        setNombre(nombre);
        setDescripcion(descripcion);
        setPrecio(precio);
        setUnidadesStock(unidadesStock);
    }

    // Para buscar
    public Articulo(String textoABuscar) {
        setTextoABuscar(textoABuscar);
    }

    // Para eliminar
    public Articulo(int id){
        setId(id);
    }

    public String insertar(Articulo articulo) {
        String mensaje;
        try (Connection conn = Conexion.conectar()) {
            try (PreparedStatement query = conn.prepareStatement("INSERT INTO Articulo(ArticuloCodigo, ArticuloNombre, "
                    + "ArticuloDescripcion, ArticuloPrecio, ArticuloUnidadesStock) VALUES (?, ?, ?, ?, ?);")) {
                query.setString(1, articulo.codigo_);
                query.setString(2, articulo.nombre_);
                query.setString(3, articulo.descripcion_);
                query.setBigDecimal(4, articulo.precio_);
                query.setInt(5, articulo.unidadesStock_);

                boolean esEjecutado =  (query.executeUpdate() > 0);
                if (esEjecutado) {
                    mensaje = "El registro ha sido agregado exitosamente.";
                } else {
                    throw new SQLException("El registro no pudo ser agregado correctamente.");
                }
            } catch (SQLException ex) {
                mensaje = ex.getMessage();
                Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            mensaje = "La conexion a la base de datos no pudo ser realizada exitosamente.";
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mensaje;
    }

    public String actualizar(Articulo articulo) {
        String mensaje;
        try (Connection conn = Conexion.conectar()) {
            try (PreparedStatement query = conn.prepareStatement("UPDATE ONLY Articulo SET  ArticuloCodigo = ?, " +
                    "ArticuloNombre = ?, ArticuloDescripcion = ?, ArticuloPrecio = ?, ArticuloUnidadesStock = ?,"
                    + "ArticuloEstatus = ? WHERE ArticuloId = ?;")) {
                query.setString(1, articulo.codigo_);
                query.setString(2, articulo.nombre_);
                query.setString(3, articulo.descripcion_);
                query.setBigDecimal(4, articulo.precio_);
                query.setInt(5, articulo.unidadesStock_);
                query.setString(6, articulo.getEstatus().getChar());
                query.setInt(7, articulo.id_); // Modificar

                boolean esEjecutado = (query.executeUpdate() > 0);
                if (esEjecutado) {
                    mensaje = "El registro ha sido actualizado exitosamente.";
                } else {
                    throw new SQLException("El registro no pudo ser actualizado correctamente.");
                }
            } catch (SQLException ex) {
                mensaje = ex.getMessage();
                Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);

            }
        } catch (SQLException ex) {
            mensaje = "La conexion a la base de datos no pudo ser realizada exitosamente.";
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mensaje;

    }

    public ObservableList<Articulo> buscar(Articulo articulo) {
        ObservableList<Articulo> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * from articulo_buscar(?)");
            query.setString(1, articulo.textoABuscar_);
            data = leer(query.executeQuery());
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public ObservableList<Articulo> mostrar() {
        ObservableList<Articulo> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * from articulo_mostrar()");
            data = leer(query.executeQuery());
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public ObservableList<Articulo> mostrarActivos() {
        ObservableList<Articulo> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * from articulo_mostraractivos()");
            data = leer(query.executeQuery());
        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    private ObservableList<Articulo> leer(ResultSet resultSet) throws SQLException {
        ObservableList<Articulo> data = FXCollections.observableArrayList();
        while (resultSet.next()) {
            data.add(crear(resultSet));
        }
        return data;
    }

    private Articulo crear(ResultSet resultSet) throws SQLException {
        int no = resultSet.getInt("Id");
        final String nombre = resultSet.getString("Nombre");
        final String descripcion = resultSet.getString("Descripcion");
        final BigDecimal precio = resultSet.getBigDecimal("Precio");
        final int unidadesStock = resultSet.getInt("Unidades_Stock");
        final String codigo = resultSet.getString("Codigo");
        Articulo articulo = new Articulo(codigo, nombre, descripcion, precio, unidadesStock);
        articulo.setId(no);

        // Set Estatus.
        HashMap<String, Estatus> opciones = new HashMap<>();
        opciones.put(Estatus.ACTIVO.getChar(), Estatus.ACTIVO);
        opciones.put(Estatus.INACTIVO.getChar(), Estatus.INACTIVO);
        final Estatus estatus = opciones.get(resultSet.getString("Estatus"));
        articulo.setEstatus(estatus);

        return articulo;
    }

    public int getId() {
        return id_;
    }

    public String getNombre() {
        return nombre_;
    }

    public String getDescripcion() {
        return descripcion_;
    }

    public BigDecimal getPrecio() {
        return precio_;
    }

    public int getUnidadesStock() {
        return unidadesStock_;
    }

    public String getCodigo() {
        return codigo_;
    }

    public Estatus getEstatus() {
        return estatus_;
    }

    private void setId(int id) {
        id_ = id;
    }

    public void setNombre(String nombre) {
        nombre_ = nombre;
    }

    public void setDescripcion(String descripcion) {
        descripcion_ = descripcion;
    }

    public void setPrecio(BigDecimal precio) {
        precio_ = precio;
    }

    public void setUnidadesStock(int unidadesStock) {
        unidadesStock_ = unidadesStock;
    }

    public void setCodigo(String codigo) {
        codigo_ = codigo;
    }

    public void setEstatus(Estatus estatus) {
        estatus_ = estatus;
    }

    private void setTextoABuscar(String textoABuscar) {
        textoABuscar_ = textoABuscar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Articulo)) return false;
        Articulo articulo = (Articulo) o;
        return Objects.equals(codigo_, articulo.codigo_);
    }

    @Override
    public int hashCode() {

        return Objects.hash(codigo_);
    }

    @Override
    public String toString() {
        DecimalFormat f = new DecimalFormat("###,##0.00");
        return "Articulo{" +
                "id_=" + id_ +
                ", codigo_='" + codigo_ + '\'' +
                ", nombre_='" + nombre_ + '\'' +
                ", descripcion_='" + descripcion_ + '\'' +
                ", unidadesStock_=" + unidadesStock_ +
                ", precio_=" + f.format(precio_) +
                ", estatus_=" + estatus_ + '}';
    }
}
