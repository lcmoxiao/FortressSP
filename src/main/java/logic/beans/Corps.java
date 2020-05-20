package logic.beans;

import logic.tool.Cloner;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

public class Corps implements Serializable {

    private static ArrayList<Occupation> corps = new ArrayList<>();

    static {
        getFromDB();
    }

    //唯一的方法，用于提供复刻的兵种列表兵种
    public static ArrayList<Occupation> getOneData() {
        return Cloner.clone(corps);
    }

    private static void getFromDB() {
        Connection con;
        Statement stat;
        final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/db_fortress?useSSL=true&serverTimezone=UTC";
        final String DB_USER = "lichen";
        final String DB_PASSWORD = "123";
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stat = con.createStatement();
            ResultSet res = stat.executeQuery("select * from corps");
            while (res.next()) {
                int id = res.getInt(1);
                String name = res.getString(2);
                int HP = res.getInt(3);
                int ATK = res.getInt(4);
                int DEF = res.getInt(5);
                int SPD = res.getInt(6);
                double DOD = res.getDouble(7);
                int RAG = res.getInt(8);
                int cost = res.getInt(9);
                boolean isAOE = res.getBoolean(10);
                corps.add(new Occupation(id, name, HP, ATK, DEF, SPD, DOD, RAG, cost, isAOE));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


}
