package com.contact;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * ContactServlet
 * ---------------
 * Single servlet that handles all four HTTP methods for contact CRUD:
 *
 *   GET    /api/contacts          — retrieve all contacts
 *   POST   /api/contacts          — add a new contact
 *   PUT    /api/contacts          — update an existing contact (id in JSON body)
 *   DELETE /api/contacts?id=<id>  — delete a contact by id
 */
@WebServlet("/api/contacts")
public class ContactServlet extends HttpServlet {

    // ── Helpers ──────────────────────────────────────────────────────────

    /** Sets common response headers for JSON APIs. */
    private void prepareResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // Allow requests from the same origin; widen if you use a separate dev server
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    /** Reads the full request body and returns it as a String. */
    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /** Writes a JSON object to the response. */
    private void writeJSON(HttpServletResponse response, JSONObject json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
        }
    }

    // ── OPTIONS (CORS pre-flight) ─────────────────────────────────────────

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareResponse(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // ── GET — retrieve all contacts ───────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        String sql = "SELECT id, name, phone, email FROM contacts ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            JSONArray contacts = new JSONArray();
            while (rs.next()) {
                JSONObject contact = new JSONObject();
                contact.put("id",    rs.getInt("id"));
                contact.put("name",  rs.getString("name"));
                contact.put("phone", rs.getString("phone"));
                contact.put("email", rs.getString("email"));
                contacts.put(contact);
            }

            JSONObject result = new JSONObject();
            result.put("success",  true);
            result.put("contacts", contacts);

            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = response.getWriter()) {
                out.print(result.toString());
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("success", false);
            error.put("message", "Database error: " + e.getMessage());
            writeJSON(response, error);
        }
    }

    // ── POST — add a new contact ──────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        String body = readBody(request);
        JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJSON(response, new JSONObject().put("success", false).put("message", "Invalid JSON body."));
            return;
        }

        String name  = json.optString("name",  "").trim();
        String phone = json.optString("phone", "").trim();
        String email = json.optString("email", "").trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJSON(response, new JSONObject()
                    .put("success", false)
                    .put("message", "name, phone, and email are required."));
            return;
        }

        String sql = "INSERT INTO contacts (name, phone, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.executeUpdate();

            int generatedId = -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) generatedId = keys.getInt(1);
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            writeJSON(response, new JSONObject()
                    .put("success", true)
                    .put("message", "Contact added successfully.")
                    .put("id", generatedId));

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJSON(response, new JSONObject()
                    .put("success", false)
                    .put("message", "Database error: " + e.getMessage()));
        }
    }

    // ── PUT — update an existing contact ──────────────────────────────────

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        String body = readBody(request);
        JSONObject json;
        try {
            json = new JSONObject(body);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJSON(response, new JSONObject().put("success", false).put("message", "Invalid JSON body."));
            return;
        }

        int    id    = json.optInt("id", -1);
        String name  = json.optString("name",  "").trim();
        String phone = json.optString("phone", "").trim();
        String email = json.optString("email", "").trim();

        if (id <= 0 || name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJSON(response, new JSONObject()
                    .put("success", false)
                    .put("message", "id, name, phone, and email are required."));
            return;
        }

        String sql = "UPDATE contacts SET name=?, phone=?, email=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setInt(4, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJSON(response, new JSONObject()
                        .put("success", false)
                        .put("message", "Contact with id " + id + " not found."));
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                writeJSON(response, new JSONObject()
                        .put("success", true)
                        .put("message", "Contact updated successfully."));
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJSON(response, new JSONObject()
                    .put("success", false)
                    .put("message", "Database error: " + e.getMessage()));
        }
    }

    // ── DELETE — delete a contact by id ───────────────────────────────────

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJSON(response, new JSONObject()
                    .put("success", false)
                    .put("message", "Query parameter 'id' is required."));
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJSON(response, new JSONObject()
                    .put("success", false)
                    .put("message", "Invalid id value: " + idParam));
            return;
        }

        String sql = "DELETE FROM contacts WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJSON(response, new JSONObject()
                        .put("success", false)
                        .put("message", "Contact with id " + id + " not found."));
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                writeJSON(response, new JSONObject()
                        .put("success", true)
                        .put("message", "Contact deleted successfully."));
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJSON(response, new JSONObject()
                    .put("success", false)
                    .put("message", "Database error: " + e.getMessage()));
        }
    }
}
