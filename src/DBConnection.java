import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class DBConnection {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";

    private static final String USER = "system";
    private static final String PASSWORD = "oracle";

    Connection connection;
    Statement statement;

    public DBConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            statement = connection.createStatement();
            System.out.println("Connecting to database...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public DefaultTableModel QueryExecution(String query) {
        ResultSet result = null;
        DefaultTableModel defaultTableModel = null;
        try {
            result = statement.executeQuery(query);
            ResultSetMetaData metaData = result.getMetaData();

            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }

            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (result.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(result.getObject(columnIndex));
                }
                data.add(vector);
            }
            defaultTableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultTableModel;
    }

    public DefaultTableModel preparedStatementExecution(ResultSet result) {
        DefaultTableModel defaultTableModel = null;
        try {
            ResultSetMetaData metaData = result.getMetaData();

            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }

            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (result.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(result.getObject(columnIndex));
                }
                data.add(vector);
            }
            defaultTableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            result.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return defaultTableModel;
    }
}
