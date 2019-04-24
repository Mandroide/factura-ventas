package data;

public enum Estatus {
    ACTIVO('A'), INACTIVO('I');

    private final char estatus_;
    Estatus(char estatus){
        estatus_ = estatus;
    }
    public String getChar(){
        return String.valueOf(estatus_);
    }

    @Override
    public String toString() {
        return super.toString().toUpperCase().charAt(0) + super.toString().substring(1).toLowerCase();
    }
}
