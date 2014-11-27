package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by frederik on 25/11/14.
 */
public class DBConnect {

    private Connection con;
    private Statement st;
    private ResultSet rs;

    public DBConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

    }

    private Connection getCon() {
        try {
            if(!con.isValid(3)) {
                con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return con;
    }

    public LinkedHashMap<Integer, String> getMovies() {
        LinkedHashMap<Integer, String> movies = new LinkedHashMap<Integer, String>();
        try {
            st = getCon().createStatement();

            String query = "SELECT * FROM movies ORDER BY name ASC";

            rs = st.executeQuery(query);
            while(rs.next()) {
                int id = rs.getInt("id");
                String movieName = rs.getString("name");
                movies.put(id, movieName);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return movies;
    }

    public LinkedHashMap<Integer, Timestamp> getMovieSchedule(int movieId) {
        LinkedHashMap<Integer, Timestamp> times = new LinkedHashMap<Integer, Timestamp>();
        try {
            st = getCon().createStatement();
            String query = "SELECT * FROM shows WHERE movie_id=" + movieId + " ORDER BY time ASC";
            rs = st.executeQuery(query);
            while(rs.next()) {
                int id = rs.getInt("id");
                Timestamp timestamp = rs.getTimestamp("time");
                times.put(id, timestamp);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return times;
    }

}