package cn.edu.zju.zjj;

import java.sql.*;

/**
 * author: zjj(zhujiajunup@163.com)
 * <p>
 * Data: 2017/12/16 19:03
 */
public class Jdbc {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/demo";
    private static final String user = "root";
    private static final String password = "123456";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);

        System.out.println("Statement 语句结果: ");
        Statement statement = connection.createStatement();
        statement.execute("SELECT * FROM SU_City limit 3");
        ResultSet resultSet = statement.getResultSet();
        printResultSet(resultSet);
        resultSet.close();
        statement.close();
        System.out.println();

        System.out.println("PreparedStatement 语句结果: ");
        PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM SU_City WHERE city_en_name = ? limit 3");
        preparedStatement.setString(1, "beijing");
        preparedStatement.execute();
        resultSet = preparedStatement.getResultSet();
        printResultSet(resultSet);
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }

    /**
     * 处理返回结果集
     */
    private static void printResultSet(ResultSet rs) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            StringBuffer b = new StringBuffer();
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    b.append(meta.getColumnName(i) + "=");
                    b.append(rs.getString(i) + "\t");
                }
                b.append("\n");
            }
            System.out.print(b.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
