package data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Factura {

    private int id_;
    private int numero_;

    private Cliente cliente_ = new Cliente();
    private LocalDate fecha_;
    private LocalTime hora_;
    private BigDecimal totalBruto_;
    private BigDecimal totalDescuento_;
    private BigDecimal totalImpuesto_;
    private BigDecimal totalCargo_;
    private BigDecimal totalNeto_;
    private Set<FacturaDetalle> detalles_ = new HashSet<>();

    // Para realizar consultas a la base de datos.
    public Factura() {

    }

    // Para insertar
    public Factura(Cliente cliente) {
        cliente_ = cliente;
    }

    // Para Mostrar
    private Factura(Cliente cliente, int id, int numero, LocalDate fecha, LocalTime hora,
                    BigDecimal totalBruto, BigDecimal totalDescuento, BigDecimal totalImpuesto, BigDecimal totalCargo,
                    BigDecimal totalNeto){
        cliente_ = cliente;
        setId(id);
        setNumero(numero);
        setFecha(fecha);
        setHora(hora);
        setTotalBruto(totalBruto);
        setTotalDescuento(totalDescuento);
        setTotalImpuesto(totalImpuesto);
        setTotalCargo(totalCargo);
        setTotalNeto(totalNeto);

    }

    // Para eliminar.
    public Factura(int id) {
        setId(id);
    }

    private void insertarDetalles(Connection conn)
            throws SQLException {

        for (FacturaDetalle detalle : getDetalles()) {
            PreparedStatement statement = new AtomicReference<>(conn.prepareStatement(
                    "SELECT * FROM FacturaDetalle_Insertar(" +
                            "id := ?, articuloid := ?, precio := ?, cantidad := ?, descuento := ?, impuesto := ?)")).get();
            statement.setInt(1, getId());
            statement.setInt(2, detalle.getArticuloId());
            statement.setBigDecimal(3, detalle.getPrecio());
            statement.setInt(4, detalle.getCantidad());
            statement.setBigDecimal(5, detalle.getDescuento());
            statement.setBigDecimal(6, detalle.getImpuesto());

            boolean esEjecutado = statement.execute();
            if (!esEjecutado) {
                throw new SQLException();
            }
        }
    }

    // Listo. 
    public String insertar() {
        String mensaje;
        try (Connection conn = Conexion.conectar()) {
            try {
                // Deshabilita auto transaccion.
                conn.setAutoCommit(false);
                PreparedStatement query = new AtomicReference<>(conn.prepareStatement("INSERT INTO "
                        + "Factura(ClienteId, FacturaTotalCargo) "
                        + "VALUES ( ?, ?) RETURNING facturaid;")).get();
                query.setInt(1, getClienteId());
                query.setObject(2, getTotalCargo());
                boolean esEjecutado = query.execute();
                if (esEjecutado) {
                    ResultSet rs = query.getResultSet();
                    rs.next();
                    setId(rs.getInt(1));
                    insertarDetalles(conn);
                    conn.commit(); // Confirma la transaccion.
                    mensaje = "El registro ha sido agregado exitosamente.";

                } else {
                    throw new SQLException();
                }

            } catch (SQLException ex) {
                conn.rollback(); // Cancela la transaccion
                mensaje = "El registro no pudo ser agregado correctamente.";
                Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            mensaje = "La conexion a la base de datos no pudo ser realizada exitosamente.";
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mensaje;
    }

    public String eliminar(Factura factura) {
        String mensaje;
        try (Connection conn = Conexion.conectar()) {
            try (PreparedStatement query = conn.prepareStatement("UPDATE Factura SET FacturaEstatus = 'C'"
                    + " WHERE FacturaId = ?;")) {

                query.setInt(1, factura.id_);
                boolean esEjecutado = (query.executeUpdate() > 0);
                if (esEjecutado) {
                    mensaje = "La factura ha sido cancelada exitosamente.";
                } else {
                    throw new SQLException( "La factura no pudo ser cancelada.");
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

    // Listo
    public ObservableList<Factura> mostrar() {
        ObservableList<Factura> data = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar();
             PreparedStatement query = conn.prepareStatement("SELECT * FROM Factura_mostrar()")) {
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {

                Cliente cliente = new Cliente();
                int clienteId = resultSet.getInt("No");
                String clienteNombre = resultSet.getString("Nombre");
                cliente.setNombre(clienteNombre);
                cliente.setId(clienteId);

                final int id = resultSet.getInt("Id");
                final int numero = resultSet.getInt("Numero");
                final LocalDate fecha = resultSet.getDate("Fecha").toLocalDate();
                final LocalTime hora = resultSet.getTime("Hora").toLocalTime();
                BigDecimal totalBruto = resultSet.getBigDecimal("Total_Bruto");
                BigDecimal totalDescuento = resultSet.getBigDecimal("Total_Descuento");
                BigDecimal totalImpuesto = resultSet.getBigDecimal("Total_Impuesto");
                BigDecimal totalCargo = resultSet.getBigDecimal("Total_Cargo");
                BigDecimal totalNeto = resultSet.getBigDecimal("Total_Neto");
                Factura obj = new Factura(cliente, id, numero, fecha, hora,
                        totalBruto, totalDescuento, totalImpuesto, totalCargo, totalNeto);

                data.add(obj);
            }


        } catch (SQLException ex) {
            Logger.getLogger(Articulo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    // Listo
    public ObservableList<FacturaDetalle> mostrarDetalles(int numero) {
        // Texto
        ObservableList<FacturaDetalle> detalles_ = FXCollections.observableArrayList();
        try (Connection conn = Conexion.conectar()) {
            PreparedStatement query = conn.prepareStatement("SELECT * FROM Factura_Mostrar(?)");
            query.setInt(1, numero);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {

                final short linea = resultSet.getShort("Linea");

                Articulo articulo = new Articulo();
                final String articuloCodigo = resultSet.getString("Codigo_Articulo");
                articulo.setCodigo(articuloCodigo);
                final String articuloNombre = resultSet.getString("articulo");
                articulo.setNombre(articuloNombre);
                final String articuloDescripcion = resultSet.getString("descripcion");
                articulo.setDescripcion(articuloDescripcion);
                BigDecimal precio = resultSet.getBigDecimal("Precio");
                articulo.setPrecio(precio);

                final int cantidad = resultSet.getInt("cantidad");
                BigDecimal descuento = resultSet.getBigDecimal("Descuento");
                BigDecimal impuesto = resultSet.getBigDecimal("Impuesto");
                BigDecimal neto = resultSet.getBigDecimal("Neto");
                FacturaDetalle obj = new FacturaDetalle(id_, articulo,  cantidad,
                        descuento, impuesto, neto);
                obj.setLinea(linea);
                detalles_.add(obj);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Factura.class.getName()).log(Level.SEVERE, null, ex);
        }

        return detalles_;

    }

    public int getId() {
        return id_;
    }

    public int getNumero() {
        return numero_;
    }

    public Cliente getCliente() {
        return cliente_;
    }

    public int getClienteId() {
        return cliente_.getId();
    }

    public String getClienteNombre(){
        return cliente_.getNombre();
    }

    public LocalDate getFecha() {
        return fecha_;
    }

    public LocalTime getHora(){
        return hora_;
    }

    public Set<FacturaDetalle> getDetalles() {
        return detalles_;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto_;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento_;
    }

    public BigDecimal getTotalImpuesto() {
        return totalImpuesto_;
    }

    public BigDecimal getTotalCargo() {
        return totalCargo_;
    }

    public BigDecimal getTotalNeto() {
            return totalNeto_;
    }

    private void setId(int id) {
        id_ = id;
    }

    private void setNumero(int numero) {
        numero_ = numero;
    }

    public void setCliente(Cliente cliente) {
        cliente_ = cliente;
    }

    private void setFecha(LocalDate fecha) {
        fecha_ = fecha;
    }

    private void setHora(LocalTime hora) {
        hora_ = hora;
    }

    public void setDetalles(Set<FacturaDetalle> detalles) {
        detalles_ = detalles;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        totalBruto_ = totalBruto;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        totalDescuento_ = totalDescuento;
    }

    public void setTotalImpuesto(BigDecimal totalImpuesto) {
        totalImpuesto_ = totalImpuesto;
    }

    public void setTotalCargo(BigDecimal totalCargo) {
        totalCargo_ = totalCargo;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        totalNeto_ = totalNeto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factura factura = (Factura) o;
        return id_ == factura.id_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_);
    }

    @Override
    public String toString(){
        DecimalFormat f = new DecimalFormat("###,##0.00");

        return "Factura[No = " + getClienteId() + ", cliente = " + getClienteNombre()
                + ", numero de factura = " + getNumero() + ", fecha = " + getFecha()
                + ", totalBruto = " + f.format(getTotalBruto()) + ", totalImpuesto = " + f.format(getTotalImpuesto())
                + ", totalCargo = " + f.format(getTotalCargo()) + ", totalDescuento = " + f.format(getTotalDescuento())
                + ", totalNeto = " + f.format(getTotalNeto()) + "]\n";
    }
}
