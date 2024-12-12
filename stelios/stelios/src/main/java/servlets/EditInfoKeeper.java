package servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import database.tables.EditPetOwnersTable;
import database.DB_Connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mainClasses.PetOwner;
import com.google.gson.Gson;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.util.List;

@WebServlet(name = "EditInfoKeeper", urlPatterns = {"/EditInfoKeeper"})
public class EditInfoKeeper extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Opened EditInfoKeeper");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                buffer.append(line);
            }
            String data = buffer.toString();
            JSONObject jsonObj = new JSONObject(data);


            Connection con = DB_Connection.getConnection();

            String query = "UPDATE petkeepers SET password = ?, firstname = ?, lastname = ?, birthdate = ?, gender = ?, country = ?, city = ?, address = ?, personalpage = ?, job = ?, telephone = ?,property = ?,propertydescription = ?, catprice = ?, dogprice = ? WHERE username = ?";
            PreparedStatement pst = con.prepareStatement(query);

            // Setting parameters for the prepared statement from JSON object
            pst.setString(1, jsonObj.getString("password"));
            pst.setString(2, jsonObj.getString("firstname"));
            pst.setString(3, jsonObj.getString("lastname"));
            pst.setString(4, jsonObj.getString("birthdate"));
            pst.setString(5, jsonObj.getString("gender"));
            pst.setString(6, jsonObj.getString("country"));
            pst.setString(7, jsonObj.getString("city"));
            pst.setString(8, jsonObj.getString("address"));
            pst.setString(9, jsonObj.getString("personalpage"));
            pst.setString(10, jsonObj.getString("job"));
            pst.setString(11, jsonObj.getString("telephone"));
            pst.setString(12, jsonObj.getString("property"));
            pst.setString(13, jsonObj.getString("propertydescription"));
            pst.setDouble(14, jsonObj.getDouble("catprice"));
            pst.setDouble(15, jsonObj.getDouble("dogprice"));
            pst.setString(16, jsonObj.getString("username"));
            //debug print
            System.out.println(pst.toString());

            int i = pst.executeUpdate();

            if (i > 0) {
                out.println("{\"success\": \"Information updated successfully.\"}");
            } else {
                out.println("{\"error\": \"Failed to update information.\"}");
            }
        } catch (Exception e) {
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }
}