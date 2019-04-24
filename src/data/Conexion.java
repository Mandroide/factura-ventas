package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Conexion {

    private Conexion(){
        
    }
    
    static Connection conectar() throws SQLException {
        final String dbUrl = "jdbc:postgresql://localhost/venta";
        final String user = "usuario";
        final String password = "1234";
        return DriverManager.getConnection(dbUrl, user, password);
    }

}
