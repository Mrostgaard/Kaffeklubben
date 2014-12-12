package test;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.DBConnect;
import model.buildHolder;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DBConnectTest {
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private int lastid;
    private int lastid1;
    private DBConnect dbConnect;

    public DBConnectTest() {

        dbConnect = new DBConnect();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://mysql.itu.dk:3306/kaffeklubben", "kaffeklubben", "kp8473moxa");
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }
    //denne test tjekker om film og ide passer med det forventede - OG om rækkefølgen er rigtig.
    @Test
    public void testGetMovies() {
        Map<Integer, String> expected = new LinkedHashMap<>();
        expected.put(1, "Interstellar");
        expected.put(2, "Fury");
        expected.put(3, "Antboy");
        expected.put(5, "Mockingjay");
        expected.put(6, "Dommeren");
        expected.put(7, "Bryllupskaos");
        expected.put(8, "Guardians of the Galaxy");
        expected.put(9, "Dumb and Dumber To");
        expected.put(10, "Nightcrawler");
        expected.put(11, "Jurrasic World");
        assertEquals(expected, dbConnect.getMovies());
    }
    //denne test tjekker tidspunkterne på film nr 1 virker - 1 kan udskiftes med andre id's. Virker stadig.
    @Test
    public void testTimeStamp() throws SQLException {
        try {
            st = con.createStatement();
            String query = "SELECT * FROM shows WHERE movie_id=1 ORDER BY time ASC";
            rs = st.executeQuery(query);
            Timestamp tmstmp = new Timestamp(0);
            while (rs.next()) {
                //System.out.println("Id eksisterer"); //id 4 eksisterer ikke
                assertTrue(tmstmp.getTime() < rs.getTimestamp("time").getTime());
                if (rs.getTimestamp("time").getTime() > tmstmp.getTime()) {
                    tmstmp = rs.getTimestamp("time");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    //denne test tjekker om et reserveret sæde for farven rød
    @Test
    public void testReservedSetColor() {
        DBConnect dbcon = new DBConnect();
        buildHolder bh = dbcon.getBuildSceneInfo(109);
        Boolean[][] resSeat = bh.getResSeat();
        for(int i = 1; i < 31; i++) { //hvorfor 31?
            for(int j = 1; j < 21; j++) { //hvorfor 21?
                double width = (879-8*bh.getColumns()-8)/bh.getColumns();
                double height = (521-8*bh.getRows()-8)/bh.getRows();
                final Rectangle r = new Rectangle(width,height);
                int x = i;
                int y = j;
                if(resSeat[i][j] != null) {
                    if(resSeat[i][j]) {
                        r.setFill(Color.web("#E53935"));
                    }
                } else {
                    r.setFill(Color.web("#43A047"));
                }
                //if(i==15 && j==9){
                  // assertTrue(r.getFill().toString().equals("0xe53935ff"));
                //}
            }
        }
    }
    //tester om reservationer der bliver lavet, gemmes i databasen
    @Test
    public void testInsertReservation(){
        ArrayList<String> seats = new ArrayList<String>();
        seats.add("1:2");
        dbConnect.insertReservation(seats, 120, "Amanda", "26802103");
        dbConnect.getLastReservationId();

        try {
            rs = st.executeQuery("SELECT reservations.id, seat_x, seat_y FROM reservations, reservationlines WHERE reservationlines.reservation_id" +
                    " = reservations.id AND show_id = '120' AND customer_name = 'Amanda' AND customer_phone = '26802103'");
            while(rs.next()){
                lastid = rs.getInt(1);
                System.out.print(lastid);
                //assertEquals(rs.getString("id"), (lastid));
                //assertEquals(rs.getInt("seat_x"), 1);
                //assertEquals(rs.getInt("seat_y"), 2);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }
    //sletter den reservation vi har lavet ovenfor.
    @After
    public void deleteReservationtest() {
        dbConnect.deleteReservation(lastid);

    }
    
    //Denne test tester om der kommer en liste ud når man har reserveret for et bestemt nummer.
    @Test
    public void testGetReservation() {
        Boolean reservations = dbConnect.getBooleanReservations("11223344");
        assertTrue(!reservations);
        ArrayList<String> seats = new ArrayList<String>();
        seats.add("2:2");
        String customerName = "Markus";
        String phoneNumber = "11223344";
        int showId = 109;
        dbConnect.insertReservation(seats, showId, customerName, phoneNumber);
        reservations = dbConnect.getBooleanReservations("11223344");
        assertTrue(reservations);
        try {
            rs = st.executeQuery("SELECT id FROM reservations ORDER BY id DESC LIMIT 1");
            if (rs.next()) {
                lastid1 = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    //sletter indput til databasen som blev lavet opover.
    @After
    public void deleteInputReservation() {
        dbConnect.deleteReservation(lastid1);

    }

}