package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Database {

    Connection conn = null;

    private final String DB_URL;

    public Database(String url){
        this.DB_URL = url;
    }
    public Database(){
        this.DB_URL = "jdbc:sqlite:database.db";
    }

    public void Experiment() {
        connectToDatabase();
        //getGames();

        /*getUsers();
        int userCreated = insertUser("test", "test");
        System.out.println("User id: " + userCreated);
        */

        /*
        ResultSet rs = getUsers();
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("username");

                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
         */


        /*
        // get a game that does not exist
        ResultSet rs = getGame(10);
        if (rs==null){
            System.out.println("Game not found");
        }
        else{
            try {
                int id = rs.getInt("id");
                int user1_id = rs.getInt("user1_id");
                int user2_id = rs.getInt("user2_id");
                String game_type = rs.getString("game_type");

                System.out.println("ID: " + id + ", User1 ID: " + user1_id + ", User2 ID: " + user2_id + ", Game Type: " + game_type);
            } catch (Exception e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        /// get a user that does not exist
        rs = getUserInfo(10);
        if (rs==null){
            System.out.println("User not found");
        }
        else{
            try {
                int id = rs.getInt("id");
                String name = rs.getString("username");

                System.out.println("ID: " + id + ", Name: " + name);
            } catch (Exception e) {
                System.out.println("An error occurred.32");
                e.printStackTrace();
            }
        }
         */

        /*
        int game = isUserInGame(2);
        System.out.println("GAME: " + game);
        ResultSet g1 = getGame(game);
        try {
            int id = g1.getInt("id");
            int user1_id = g1.getInt("user1_id");
            int user2_id = g1.getInt("user2_id");
            String game_type = g1.getString("game_type");

            System.out.println("ID: " + id + ", User1 ID: " + user1_id + ", User2 ID: " + user2_id + ", Game Type: " + game_type);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        */

        //int ret = insertUser("user1", "test");
        //System.out.println("User id: " + ret);

        //int game = insertGame(2, 3, "simple");
        //System.out.println("Game id: " + game);

        //int ret = isUserInGame(2);
        //System.out.println("IS IN GAME: " + ret);


        //int ret = finishGame(2, 2);
        //int ret = insertGame(1, 2, "ranked");
        //System.out.println("RET: " + ret);

        try {
            ResultSet rs = getOngoingGames();
            while (rs.next()) {
                int id = rs.getInt("id");
                int user1_id = rs.getInt("user1_id");
                int user2_id = rs.getInt("user2_id");
                String game_type = rs.getString("game_type");

                System.out.println("ID: " + id + ", User1 ID: " + user1_id + ", User2 ID: " + user2_id + ", Game Type: " + game_type);
            }
        }
        catch (Exception e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //int finish = finishGame(ret, 1);
        //System.out.println("FINISH: " + finish);
        closeDatabaseConnection();
    }



    public int connectToDatabase(){
        try {
            Class.forName("org.sqlite.JDBC");

            // Open a connection
            System.out.println("Connecting to SQLite database...");
            conn = DriverManager.getConnection(DB_URL);

            if (conn != null) {
                System.out.println("Connected to the SQLite database");
            }
        }catch (Exception e) {
            System.out.println("An error occurred. " + e);
            return 0;
        }
        return 1;
    }

    public void closeDatabaseConnection(){
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }


    public ResultSet getUsers(){

        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT * FROM users";
            rs = stmt.executeQuery(sql);
            return rs;

        }
        catch (Exception e){
            e.printStackTrace();
            closeDatabaseConnection();
            return null;
        }

    }

    public ResultSet getGames(){

        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql2 = "SELECT * FROM games";
            rs = stmt.executeQuery(sql2);
            return rs;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return null;
        }
    }

    public ResultSet getUserInfo(int id){

        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT * FROM users WHERE id = " + id;
            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                return null;
            }
            return rs;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return null;
        }
    }

    public ResultSet getUserInfo(String username){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT * FROM users WHERE username = '" + username + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                closeDatabaseConnection();
                return null;
            }
            return rs;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return null;
        }
    }

    public int getUserID(String Username){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT id FROM users WHERE username = '" + Username + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                closeDatabaseConnection();
                return -1;
            }
            var ret =  rs.getInt("id");
            closeDatabaseConnection();
            return ret;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return -1;
        }
    }

    public ArrayList<String> getTop10(){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT username, score FROM users ORDER BY score DESC LIMIT 10";
            rs = stmt.executeQuery(sql);
            ArrayList<String> ret = new ArrayList<>();
            int i = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                ret.add(i + ". " + username + " -> " + score + "\n");
                i++;
            }
            closeDatabaseConnection();
            return ret;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return null;
        }
    }

    public String getRank(String username){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT username, score FROM users WHERE username = '" + username + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                closeDatabaseConnection();
                return "User not found";
            }
            int score = rs.getInt("score");
            sql = "SELECT COUNT(*) FROM users WHERE score > " + score;
            rs = stmt.executeQuery(sql);
            rs.next();
            int rank = rs.getInt(1) + 1;
            closeDatabaseConnection();
            return rank + ". " + username + " -> " + score;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return "An error occurred";
        }
    }

    public int finishActiveGames(){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "UPDATE games SET winner_id = 0 WHERE winner_id IS NULL";
            stmt.executeUpdate(sql);
            closeDatabaseConnection();
            return 1;
        }
        catch (Exception e){
            closeDatabaseConnection();
            return 0;
        }
    }



    public int insertUser(String username, String password) {
        try {
            connectToDatabase();
            Statement stmt = null;
            stmt = conn.createStatement();
            String sql = "INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')";
            stmt.executeUpdate(sql);
            System.out.println("User inserted successfully");
            ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
            rs.next();
            return rs.getInt(1);


        } catch (Exception e) {
            // Handle other exceptions
            //System.out.println("An error occurred.");
            //e.printStackTrace();
            closeDatabaseConnection();
            return 0;
        }
    }


    public int insertGame(int user1_id, int user2_id, String game_type) {
        try {
            connectToDatabase();
            Statement stmt = null;
            stmt = conn.createStatement();
            String sql = "INSERT INTO games (user1_id, user2_id, game_type) VALUES (" + user1_id + ", " + user2_id + ", '" + game_type + "')";
            stmt.executeUpdate(sql);
            System.out.println("Game inserted successfully");
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM games");
            rs.next();
            int game = rs.getInt(1);
            closeDatabaseConnection();
            return game;
        } catch (Exception e) {
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return 0;
        }
    }

    public ResultSet getGame(int id){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT * FROM games WHERE id = " + id;
            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                return null;
            }
            return rs;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return null;
        }
    }

    public int isUserInGame(int id){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT * FROM games WHERE (user1_id = " + id + " OR user2_id = " + id + ") AND winner_id IS NULL";
            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                return 0;
            }
            int ret = rs.getInt("id");
            return ret;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return 0;
        }
    }

    public ResultSet getOngoingGames(){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "SELECT * FROM games WHERE winner_id IS NULL";
            rs = stmt.executeQuery(sql);
            return rs;

        }
        catch (Exception e){
            // Handle other exceptions
            System.out.println("An error occurred.");
            e.printStackTrace();
            closeDatabaseConnection();
            return null;
        }
    }

    public int finishGame(int game_id, int winner){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "UPDATE games SET winner_id = " + winner + " WHERE id = " + game_id;
            stmt.executeUpdate(sql);
            return 1;
        }
        catch (Exception e){
            closeDatabaseConnection();
            return 0;
        }
    }

    public int logoutAll(){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "UPDATE users SET is_connected = 0";
            stmt.executeUpdate(sql);
            closeDatabaseConnection();
            return 1;
        }
        catch (Exception e){
            System.out.println("An error occurred." + e);
            closeDatabaseConnection();
            return 0;
        }
    }

    public int logoutUser(int id){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "UPDATE users SET is_connected = 0 WHERE id = " + id;
            stmt.executeUpdate(sql);
            closeDatabaseConnection();
            return 1;
        }
        catch (Exception e){
            closeDatabaseConnection();
            return 0;
        }
    }

    public int logoutUser(String username){
        try{
            connectToDatabase();
            Statement stmt = null;

            stmt = conn.createStatement();

            // Execute the SQL query
            String sql = "UPDATE users SET is_connected = 0 WHERE username = '" + username + "'";
            int rowsUpdated = stmt.executeUpdate(sql);
            closeDatabaseConnection();
            if (rowsUpdated == 0) {
                // No rows were updated, which could indicate an error
                System.out.println("Error: No rows were updated");
                return 0;
            }
            return 1;
        }
        catch (Exception e){
            closeDatabaseConnection();
            return 0;
        }
    }

    public int loginUser(String username, String password){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            String sql = "SELECT is_connected FROM users WHERE username = '" + username + "' and password = '" + password + "'";

            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                closeDatabaseConnection();
                return -1;
            }
            if(rs.getInt("is_connected") == 1){
                closeDatabaseConnection();
                return -2;
            }
            sql = "UPDATE users SET is_connected = 1 WHERE username = '" + username + "' and password = '" + password + "'";
            stmt.executeUpdate(sql);
            closeDatabaseConnection();
            return 1;
        }
        catch (Exception e){
            System.out.println("ERROR IN LOGIN " + e);
            closeDatabaseConnection();
            return 0;
        }
    }

    public int getScore(String username){
        try{
            connectToDatabase();
            Statement stmt = null;
            ResultSet rs = null;

            stmt = conn.createStatement();

            String sql = "SELECT score FROM users WHERE username = '" + username + "'";

            rs = stmt.executeQuery(sql);
            if (rs.next() == false) {
                closeDatabaseConnection();
                return -1;
            }
            int score = rs.getInt("score");
            closeDatabaseConnection();
            return score;
        }
        catch (Exception e){
            System.out.println("ERROR IN GET SCORE " + e);
            closeDatabaseConnection();
            return 0;
        }
    }
}
