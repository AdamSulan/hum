package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {

    public Database() {
        Employee emp=new Employee(0, "Marduk Árpád", "Miskolc", 395.);
        //this.insertEmployee(emp);
        ArrayList<Employee> empList= this.getEmployees();
        empList.forEach((employee)->{
            System.out.println(employee.name);
        });
    }

    public Connection connectDb() throws ClassNotFoundException, SQLException{
        Connection conn=null;
        String url="jdbc:mariadb://localhost:3306/hum";
        Class.forName("org.mariadb.jdbc.Driver");
        conn=DriverManager.getConnection(url, "hum", "titok");
        System.out.println("Működik!");
        return conn;
    }
    public void closeDb(Connection conn) throws SQLException{
        conn.close();
    }

    public void insertEmployee(Employee emp){
        try {
            tryInsertEmployee(emp);
        } catch (SQLException e) {
            System.err.println("Hiba! Az adatbázishoz a kapcsolat sikertelen!");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e){
            System.err.println("Hiba! Nincs MariaDB driver betöltve");
            System.err.println(e.getMessage());
        }
    }
    public void tryInsertEmployee(Employee emp) throws SQLException, ClassNotFoundException{
        Connection conn=this.connectDb();
        String sql="insert into employees "+"(name, city, salary) values "+"(?, ?, ?)";
        PreparedStatement pstmt=conn.prepareStatement(sql);
        pstmt.setString(1, emp.name);
        pstmt.setString(2, emp.city);
        pstmt.setDouble(3, emp.salary);
        pstmt.execute();
        this.closeDb(conn);
    }
    public ArrayList<Employee> getEmployees(){
        ArrayList<Employee> empList;
        try {
            empList=tryGetEmployees();
        } catch (Exception e) {
            System.err.println("Hiba, a dolgozók lekérdezése sikertelen!");
            empList=null;
        }
        return empList;
    }
    public ArrayList<Employee> tryGetEmployees() throws ClassNotFoundException, SQLException{
        ArrayList<Employee> empList=new ArrayList<>();
        Connection conn=null;
        String url="jdbc:mariadb://localhost:3306/hum";
        Class.forName("org.mariadb.jdbc.Driver");
        conn=DriverManager.getConnection(url, "hum", "titok");
        System.out.println("Működik!");
        String sql="select * from employees";
        Statement stmt= conn.createStatement();
        ResultSet rs=stmt.executeQuery(sql);
        empList=convertResToList(rs);
        closeDb(conn);
        return empList;
    }
    public ArrayList<Employee> convertResToList(ResultSet rs) throws SQLException{
        ArrayList<Employee> empList=new ArrayList<>();
        while(rs.next()){
            Employee emp=new Employee(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("city"),
                rs.getDouble("salary")
            );
        empList.add(emp);
        }
        return empList;
    }
}
