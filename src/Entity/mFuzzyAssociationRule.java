package Entity;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class mFuzzyAssociationRule {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/fuzzy_association_rule";
    static final String USER = "root";
    static final String PASS = "";
    
    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    
    public void openConnec()
    {
        try {
            // register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);
            
            // buat koneksi ke database
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // buat objek statement
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
        }
    }
    
    public void closeConnec()
    {
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
        }
    }
    
    public ResultSet getUniqueTermCollection()
    {
        try {            
            // buat query ke database
            String sql = "SELECT * FROM unique_term_collection";
            
            // eksekusi query dan simpan hasilnya di obj ResultSet
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
        }
        return rs;
    }
    
    public void setUniqueTermCollection(String term)
    {
        try {
            // query simpan
            String sql = "INSERT INTO unique_term_collection (unique_term) VALUE('%s')";
            sql = String.format(sql, term);

            // simpan buku
            stmt.execute(sql);
        } catch (SQLException e) {
        }
    }
    
    public void deleteAllUniqueTermCollection()
    {
        try {
            // buat query hapus
            String sql = "DELETE FROM unique_term_collection";

            // hapus data
            stmt.execute(sql);
        } catch (SQLException e) {
        }
    }
}
