package data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {

    private int id_;
    private String nombre_;
    private String direccion_;
    private String ciudad_;
    private String email_;
    private String telefono_;
    private String codigoPostal_;
    private String pais_;
    private Estatus estatus_;

    public Cliente() {
    }

    public Cliente(int id) {
        setId(id);
    }

    // Para actualizar
    public Cliente(int id, String nombre, String direccion, String ciudad, String email,
                   String telefono, String codigoPostal, String pais, Estatus estatus) {
        this(nombre, direccion, ciudad, email, telefono, codigoPostal, pais); // Constructor de insertar
        setEstatus(estatus);
        setId(id);
    }

    // Para insertar
    public Cliente(String nombre, String direccion, String ciudad, String email,
                   String telefono, String codigoPostal, String pais) {
        setNombre(nombre);
        setDireccion(direccion);
        setCiudad(ciudad);
        setEmail(email);
        setTelefono(telefono);
        setCodigoPostal(codigoPostal);
        setPais(pais);
    }

   public String insertar(Cliente cliente) {
        String mensaje;
        try (Connection conn = Conexion.conectar()) {
            try (PreparedStatement query = conn.prepareStatement("INSERT INTO cliente(ClienteNombre, " +
                    "ClienteDireccion, ClienteCiudad, ClienteEmail, ClienteTelefono, ClienteCodigoPostal, " +
                    "ClientePais) VALUES (?, ?, ?, ?, ?, ?, ?);")) {
                query.setString(1, cliente.nombre_);
                query.setString(2, cliente.direccion_);
                query.setString(3, cliente.ciudad_);
                query.setString(4, cliente.email_);
                query.setString(5, cliente.telefono_);
                query.setString(6, cliente.codigoPostal_);
                query.setString(7, cliente.pais_);

                int tuplas = query.executeUpdate();
                if (tuplas > 0) {
                    mensaje = "El registro ha sido agregado exitosamente.";
                } else {
                    throw new SQLException("El registro no pudo ser agregado correctamente.\n");
                }

            } catch (SQLException ex) {
                mensaje = ex.getMessage();
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);

            }

        } catch (SQLException ex) {
            mensaje = "La conexion a la base de datos no pudo ser realizada exitosamente.";
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mensaje;
    }

    public String actualizar(Cliente cliente) {
        String mensaje;
        try (Connection conn = Conexion.conectar()) {
            try (PreparedStatement query = conn.prepareStatement("UPDATE Cliente SET ClienteNombre = ?," +
                    "ClienteDireccion = ?, ClienteCiudad = ?, ClienteEmail = ?, ClienteTelefono = ?," +
                    "ClienteCodigoPostal = ?, ClientePais = ?, ClienteEstatus = ? WHERE ClienteId = ? ;\n")) {
                query.setString(1, cliente.nombre_);
                query.setString(2, cliente.direccion_);
                query.setString(3, cliente.ciudad_);
                query.setString(4, cliente.email_);
                query.setString(5, cliente.telefono_);
                query.setString(6, cliente.codigoPostal_);
                query.setString(7, cliente.pais_);
                query.setString(8, cliente.estatus_.getChar());
                query.setInt(9, cliente.id_); // Modificar

                boolean esEjecutado = (query.executeUpdate() > 0);
                if (esEjecutado) {
                    mensaje = "El registro ha sido actualizado exitosamente.";
                } else {
                    throw new SQLException("El registro no pudo ser actualizado correctamente.");
                }

            } catch (SQLException ex) {
                mensaje = ex.getMessage();
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            mensaje = "La conexion a la base de datos no pudo ser realizada exitosamente.";
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mensaje;

    }

    public ObservableList<Cliente> buscar(String textoABuscar) {
        ObservableList<Cliente> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * from cliente_buscar(?)");
            query.setString(1, textoABuscar);
            data = leer(query.executeQuery());
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public ObservableList<Cliente> buscarActivos(String textoABuscar) {
        ObservableList<Cliente> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * from cliente_buscaractivos(?)");
            query.setString(1, textoABuscar);
            data = leer(query.executeQuery());
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public ObservableList<Cliente> mostrar() {
        ObservableList<Cliente> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * from cliente_mostrar()");
            data = leer(query.executeQuery());
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public ObservableList<Cliente> mostrarActivos() {
        ObservableList<Cliente> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * from cliente_mostraractivos()");
            data = leer(query.executeQuery());
        } catch (SQLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    private ObservableList<Cliente> leer(ResultSet resultSet) throws SQLException {
        ObservableList<Cliente> data = FXCollections.observableArrayList();
        while (resultSet.next()) {
            data.add(crear(resultSet));
        }
        return data;
    }

    private Cliente crear(ResultSet resultSet) throws SQLException {
        int no = resultSet.getInt("Id");
        final String nombre = resultSet.getString("Nombre");
        final String email = resultSet.getString("Email");
        final String telefono = resultSet.getString("Telefono");
        final String direccion = resultSet.getString("Direccion");
        final String pais = resultSet.getString("Pais");
        final String ciudad = resultSet.getString("Ciudad");
        final String codigoPostal = resultSet.getString("CodigoPostal");
        Cliente cliente = new Cliente(nombre, direccion, ciudad, email, telefono, codigoPostal, pais);
        cliente.setId(no);

        HashMap<String, Estatus> opciones = new HashMap<>();
        opciones.put("A", Estatus.ACTIVO);
        opciones.put("I", Estatus.INACTIVO);
        final String estatus = resultSet.getString("Estatus");
        cliente.setEstatus(opciones.get(estatus));

        return cliente;
    }

    public int getId() {
        return id_;
    }

    public String getNombre() {
        return nombre_;
    }

    public String getDireccion() {
        return direccion_;
    }

    public String getCiudad() {
        return ciudad_;
    }

    public String getEmail() {
        return email_;
    }

    public String getTelefono() {
        return telefono_;
    }

    public String getCodigoPostal() {
        return codigoPostal_;
    }

    public String getPais() {
        return pais_;
    }

    public Estatus getEstatus() {
        return estatus_;
    }

    public void setId(int id) {
        id_ = id;
    }

    public void setNombre(String nombre) {
        nombre_ = nombre;
    }

    public void setDireccion(String direccion) {
        direccion_ = direccion;
    }

    public void setCiudad(String ciudad) {
        ciudad_ = ciudad;
    }

    public void setEmail(String email) {
        email_ = email;
    }

    public void setTelefono(String telefono) {
        telefono_ = telefono;
    }

    public void setCodigoPostal(String codigoPostal) {
        codigoPostal_ = codigoPostal;
    }

    public void setPais(String pais) {
        pais_ = pais;
    }

    public void setEstatus(Estatus estatus) {
        estatus_ = estatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id_ == cliente.id_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id_=" + id_ +
                ", nombre_='" + nombre_ + '\'' +
                ", direccion_='" + direccion_ + '\'' +
                ", ciudad_='" + ciudad_ + '\'' +
                ", email_='" + email_ + '\'' +
                ", telefono_='" + telefono_ + '\'' +
                ", codigoPostal_='" + codigoPostal_ + '\'' +
                ", pais_='" + pais_ + '\'' +
                ", estatus_=" + estatus_ +
                '}';
    }
}
