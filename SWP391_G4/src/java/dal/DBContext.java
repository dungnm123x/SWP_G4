package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DBContext<T> {
    protected Connection connection;

    public DBContext() {
        openConnection();
    }

    private void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String user = "sa";
                String pass = "123";
                String url = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=SWP391;encrypt=true;trustServerCertificate=true;";
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(url, user, pass);
                System.out.println("Database connected successfully!"); // Debug log
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "Database connection error", ex);
        }
    }

    public Connection getConnection() {
        openConnection(); // Đảm bảo connection luôn mở khi cần
        return connection;
    }

    public abstract void insert(T model);
    public abstract void update(T model);
    public abstract void delete(T model);
    public abstract ArrayList<T> list();
    public abstract T get(int id);
}
