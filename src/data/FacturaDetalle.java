package data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

public class FacturaDetalle {

    private int idFactura_;   // Factura
    private short linea;

    private Articulo articulo_;

    static {
        new Articulo();
    }

    private int cantidad_;
    private BigDecimal descuento_;
    private BigDecimal impuesto_;
    private BigDecimal neto_;

    // Para insertar.

    public FacturaDetalle(int idFactura, Articulo articulo, int cantidad) {
        idFactura_ = idFactura;
        articulo_ = articulo;
        setPrecio(articulo.getPrecio());
        setCantidad(cantidad);
    }

    // Para mostrar.
     FacturaDetalle(int idFactura, Articulo articulo, int cantidad,
                    BigDecimal descuento, BigDecimal impuesto, BigDecimal neto) {
        idFactura_ = idFactura;
        articulo_ = articulo;
        setCantidad(cantidad);
        setDescuento(descuento);
        setImpuesto(impuesto);
        setNeto(neto);
    }


    int getIdFactura() {
        return idFactura_;
    }

    int getArticuloId(){
        return articulo_.getId();
    }

    public String getArticuloCodigo(){
        return articulo_.getCodigo();
    }

    public String getArticuloNombre(){
        return articulo_.getNombre();
    }

    public String getArticuloDescripcion(){
        return articulo_.getDescripcion();
    }

    public BigDecimal getPrecio() {
        return articulo_.getPrecio();
    }

    public int getCantidad() {
        return cantidad_;
    }

    public BigDecimal getDescuento() {
        return descuento_;
    }

    public BigDecimal getImpuesto() {
        return impuesto_;
    }

    public BigDecimal getNeto() {
        return neto_;
    }

    void setIdFactura(int idFactura) {
        idFactura_ = idFactura;
    }

    public void setLinea(short linea) {
        this.linea = linea;
    }

    private void setPrecio(BigDecimal precio) {
        articulo_.setPrecio(precio);
    }

    public void setCantidad(int cantidad) {
        cantidad_ = cantidad;
    }

    public void setDescuento(BigDecimal descuento) {
        descuento_ = descuento;
    }

    public void setImpuesto(BigDecimal impuesto) {
        impuesto_ = impuesto;
    }

    public void setNeto(BigDecimal neto) {
        neto_ = neto;
    }

    public short getLinea() {
        return linea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacturaDetalle that = (FacturaDetalle) o;
        return idFactura_ == that.idFactura_ &&
                linea == that.linea;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFactura_, linea);
    }

    @Override
    public String toString(){
        DecimalFormat f = new DecimalFormat("###,##0.00");

        return "FacturaDetalle[línea = " + getLinea() + ", código = " + getArticuloCodigo()
                + ", artículo = " + getArticuloNombre() + ", descripción = " + getArticuloDescripcion()
                + ", cantidad = " + getCantidad() + ", precio = " + f.format(getPrecio())
                + ", impuesto = " + f.format(getImpuesto()) + ", descuento = " + f.format(getDescuento())
                + ", neto = " + f.format(getNeto()) + "]\n";
    }
}
