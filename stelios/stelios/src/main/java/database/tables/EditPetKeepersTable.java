/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.tables;

import com.google.gson.Gson;
import mainClasses.PetKeeper;
import database.DB_Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mike
 */
public class EditPetKeepersTable {

    public void updatePetKeeperDetails(PetKeeper petKeeper) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        try {
            String query = "UPDATE petkeepers SET " +
                    "username=?, " +
                    "email=?, " +
                    "password=?, " +
                    "firstname=?, " +
                    "lastname=?, " +
                    "birthdate=?, " +
                    "gender=?, " +
                    "country=?, " +
                    "city=?, " +
                    "address=?, " +
                    "personalpage=?, " +
                    "job=?, " +
                    "telephone=?, " +
                    "lat=?, " +
                    "lon=?, " +
                    "property=?, " +
                    "propertydescription=?, " +
                    "catkeeper=?, " +
                    "dogkeeper=?, " +
                    "catprice=?, " +
                    "dogprice=? " +
                    "WHERE telephone=?";

            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, petKeeper.getUsername());
            pstmt.setString(2, petKeeper.getEmail());
            pstmt.setString(3, petKeeper.getPassword());
            pstmt.setString(4, petKeeper.getFirstname());
            pstmt.setString(5, petKeeper.getLastname());
            pstmt.setDate(6, java.sql.Date.valueOf(petKeeper.getBirthdate()));
            pstmt.setString(7, petKeeper.getGender());
            pstmt.setString(8, petKeeper.getCountry());
            pstmt.setString(9, petKeeper.getCity());
            pstmt.setString(10, petKeeper.getAddress());
            pstmt.setString(11, petKeeper.getPersonalpage());
            pstmt.setString(12, petKeeper.getJob());
            pstmt.setString(13, petKeeper.getTelephone());
            pstmt.setDouble(14, petKeeper.getLat());
            pstmt.setDouble(15, petKeeper.getLon());
            pstmt.setString(16, petKeeper.getProperty());
            pstmt.setString(17, petKeeper.getPropertydescription());
            pstmt.setString(18, petKeeper.getCatkeeper());
            pstmt.setString(19, petKeeper.getDogkeeper());
            pstmt.setInt(20, petKeeper.getCatprice());
            pstmt.setInt(21, petKeeper.getDogprice());
            pstmt.setString(22, petKeeper.getTelephone());

            pstmt.executeUpdate();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.getLogger(EditPetKeepersTable.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }


    public void addPetKeeperFromJSON(String json) throws ClassNotFoundException{
        PetKeeper user=jsonToPetKeeper(json);
        addNewPetKeeper(user);
    }

    public PetKeeper jsonToPetKeeper(String json){
        Gson gson = new Gson();

        PetKeeper user = gson.fromJson(json, PetKeeper.class);
        return user;
    }

    public String petKeeperToJSON(PetKeeper user){
        Gson gson = new Gson();

        String json = gson.toJson(user, PetKeeper.class);
        return json;
    }



    public void updatePetKeeper(String username,String personalpage) throws SQLException, ClassNotFoundException{
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update="UPDATE petkeepers SET personalpage='"+personalpage+"' WHERE username = '"+username+"'";
        stmt.executeUpdate(update);
    }

    public void printPetKeeperDetails(String username, String password) throws SQLException, ClassNotFoundException{
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT * FROM petkeepers WHERE username = '" + username + "' AND password='"+password+"'");
            while (rs.next()) {
                System.out.println("===Result===");
                DB_Connection.printResults(rs);
            }

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    }

    public PetKeeper databaseToPetKeepers(String username, String password) throws SQLException, ClassNotFoundException{
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT * FROM petkeepers WHERE username = '" + username + "' AND password='"+password+"'");
            rs.next();
            String json=DB_Connection.getResultsToJSON(rs);
            Gson gson = new Gson();
            PetKeeper user = gson.fromJson(json, PetKeeper.class);
            return user;
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }



    public ArrayList<PetKeeper> getAvailableKeepers(String type) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<PetKeeper> keepers = new ArrayList<PetKeeper>();
        ResultSet rs = null;
        try {
            //if(type=="catkeeper")
            if("all".equals(type))
                rs = stmt.executeQuery("SELECT * FROM `petKeepers` WHERE  `petKeepers`.`keeper_id` not in (select keeper_id "
                        + "from `bookings` where `status`='requested' or  `status`='accepted')\n" +"");
            else if ("catKeepers".equals(type))
                rs = stmt.executeQuery("SELECT * FROM `petKeepers` WHERE `petKeepers`.`catkeeper`='true' AND `petKeepers`.`keeper_id` not in (select keeper_id "
                        + "from `bookings` where `status`='requested' or  `status`='accepted')");
            else if ("dogKeepers".equals(type))
                rs = stmt.executeQuery("SELECT * FROM `petKeepers` WHERE `petKeepers`.`dogkeeper`='true' AND `petKeepers`.`keeper_id` not in (select keeper_id "
                        + "from `bookings` where `status`='requested' or  `status`='accepted')");


            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                PetKeeper keeper = gson.fromJson(json, PetKeeper.class);
                keepers.add(keeper);
            }
            return keepers;
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    // added all case
    public ArrayList<PetKeeper> getKeepers(String type) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<PetKeeper> keepers = new ArrayList<PetKeeper>();
        ResultSet rs = null;
        try {
            if("catkeeper".equals(type))
                rs = stmt.executeQuery("SELECT * FROM petkeepers WHERE catkeeper= '" + "true" + "'");
            else if ("dogkeeper".equals(type))
                rs = stmt.executeQuery("SELECT * FROM petkeepers WHERE dogkeeper= '" + "true" + "'");
            else if ("both".equals(type))
                rs = stmt.executeQuery("SELECT * FROM petkeepers");
            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                PetKeeper keeper = gson.fromJson(json, PetKeeper.class);
                keepers.add(keeper);
            }
            return keepers;
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }


    public String databasePetKeeperToJSON(String username, String password) throws SQLException, ClassNotFoundException{
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT * FROM petkeepers WHERE username = '" + username + "' AND password='"+password+"'");
            rs.next();
            String json=DB_Connection.getResultsToJSON(rs);
            return json;
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }


    public void createPetKeepersTable() throws SQLException, ClassNotFoundException {

        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "CREATE TABLE petkeepers "
                + "(keeper_id INTEGER not NULL AUTO_INCREMENT, "
                + "    username VARCHAR(30) not null unique,"
                + "    email VARCHAR(50) not null unique,	"
                + "    password VARCHAR(32) not null,"
                + "    firstname VARCHAR(30) not null,"
                + "    lastname VARCHAR(30) not null,"
                + "    birthdate DATE not null,"
                + "    gender  VARCHAR (7) not null,"
                + "    country VARCHAR(30) not null,"
                + "    city VARCHAR(50) not null,"
                + "    address VARCHAR(50) not null,"
                + "    personalpage VARCHAR(200) not null,"
                + "    job VARCHAR(200) not null,"
                + "    telephone VARCHAR(14),"
                + "    lat DOUBLE,"
                + "    lon DOUBLE,"
                + "    property VARCHAR(10) not null,"
                + "    propertydescription VARCHAR(200),"
                + "    catkeeper VARCHAR(10) not null,"
                + "    dogkeeper VARCHAR(10) not null,"
                + "    catprice INTEGER,"
                + "    dogprice INTEGER,"
                + " PRIMARY KEY (keeper_id))";
        stmt.execute(query);
        stmt.close();
    }


    /**
     * Establish a database connection and add in the database.
     *
     * @throws ClassNotFoundException
     */
    public void addNewPetKeeper(PetKeeper user) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();

            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO "
                    + " petkeepers (username,email,password,firstname,lastname,birthdate,gender,country,city,address,personalpage,"
                    + "job,telephone,lat,lon,property,propertydescription,catkeeper,dogkeeper,catprice,dogprice)"
                    + " VALUES ("
                    + "'" + user.getUsername() + "',"
                    + "'" + user.getEmail() + "',"
                    + "'" + user.getPassword() + "',"
                    + "'" + user.getFirstname() + "',"
                    + "'" + user.getLastname() + "',"
                    + "'" + user.getBirthdate() + "',"
                    + "'" + user.getGender() + "',"
                    + "'" + user.getCountry() + "',"
                    + "'" + user.getCity() + "',"
                    + "'" + user.getAddress() + "',"
                    + "'" + user.getPersonalpage() + "',"
                    + "'" + user.getJob() + "',"
                    + "'" + user.getTelephone() + "',"
                    + "'" + user.getLat() + "',"
                    + "'" + user.getLon() + "',"
                    + "'" + user.getProperty() + "',"
                    + "'" + user.getPropertydescription()+ "',"
                    + "'" + user.getCatkeeper() + "',"
                    + "'" + user.getDogkeeper() + "',"
                    + "'" + user.getCatprice() + "',"
                    + "'" + user.getDogprice() + "'"
                    + ")";
            //stmt.execute(table);
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The pet owner was successfully added in the database.");

            /* Get the member id from the database and set it to the member */
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(EditPetKeepersTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // added delete petKeeper
    public void deletePetKeeper(String id) throws ClassNotFoundException{
        try {
            Connection con = DB_Connection.getConnection();

            Statement stmt = con.createStatement();

            String insertQuery = "DELETE FROM petkeepers WHERE Keeper_id = '" + id + "'";
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The petKeeper was successfully deleted from the database.");

            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(EditPetKeepersTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
