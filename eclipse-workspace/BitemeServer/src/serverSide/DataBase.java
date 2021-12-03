package serverSide;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONObject;

import common.Logger;
import common.Logger.Level;
import common.Message;

public class DataBase {

	private static Connection conn;

	private String password;
	private String user;

	public void start() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

			// log
			Logger.log(Level.DEBUG, "DataBase : Driver definition succeed");
			System.out.println("DataBase : Driver definition succeed");
		} catch (Exception ex) {
			// log
			Logger.log(Level.WARNING, "DataBase : Driver definition failed");
			System.out.println("DataBase : Driver definition failed");
		}

		System.out.println("DataBase: DB user name: " + user);
		Logger.log(Level.DEBUG, "DataBase: DB user name: " + user);
		System.out.println("DataBase: DB password: " + password);
		Logger.log(Level.DEBUG, "DataBase: DB password: " + password);

		conn = DriverManager.getConnection("jdbc:mysql://localhost/bitemedb?serverTimezone=IST", user, password);

		// log
		Logger.log(Level.DEBUG, "DataBase : SQL connection succeed");
		System.out.println("DataBase : SQL connection succeed");

	}

	@SuppressWarnings("unchecked")
	public JSONObject validateUser(JSONObject json) {
		ResultSet rs;
		String username = Message.getValue(json, "username");
		JSONObject response = new JSONObject();
		//
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM USERS WHERE Username = ?");
			stmt.setString(1, username);

			rs = stmt.executeQuery();
			if (rs.next()) {
				if (json.get("password").equals(rs.getString("Password"))) {

					// log
					Logger.log(Level.DEBUG, "DATABASE: user (" + username + ") is approved");
					System.out.println("DATABASE: user (" + username + ") is approved");

					response.put("command", "handshake");
					response.put("portalType", rs.getString("Role"));
					response.put("status", "ok");
					response.put("branch", rs.getString("Branch"));
					response.put("FirstName", rs.getString("FirstName"));
					response.put("LastName", rs.getString("LastName"));
					return response;
				}
				// log
				Logger.log(Level.DEBUG, "DATABASE: user (" + username + ") is NOT approved");
				System.out.println("DATABASE: user (" + username + ") is NOT approved");

				response.put("command", "handshake");
				response.put("status", "notOk");
				return response;
			}
		} catch (SQLException e) {
			Logger.log(Level.WARNING, "DATABASE: SQLException in approveUser");
		}
		// log
		Logger.log(Level.DEBUG, "DATABASE: user (" + username + ") is NOT approved");
		System.out.println("DATABASE: user (" + username + ") is NOT approved");

		response.put("command", "handshake");
		response.put("status", "notOk");
		return response;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@SuppressWarnings("unchecked")
	public JSONObject registerRegularClient(JSONObject json) {
		JSONObject response = new JSONObject();

		try {
			conn.setAutoCommit(false);

			PreparedStatement stmt1 = conn.prepareStatement(
					"INSERT INTO bitemedb.USERS (USERNAME,PASSWORD,ROLE,BRANCH,FIRSTNAME,LASTNAME) VALUES(?,?,'customer',?,?,?)");
			stmt1.setString(1, Message.getValue(json, "username"));
			stmt1.setString(2, Message.getValue(json, "password"));
			stmt1.setString(3, Message.getValue(json, "branch"));
			stmt1.setString(4, Message.getValue(json, "firstname"));
			stmt1.setString(5, Message.getValue(json, "lastname"));
			stmt1.executeUpdate();

			PreparedStatement stmt2 = conn.prepareStatement(
					"INSERT INTO bitemedb.CUSTOMERS (USERID,ID,PHONENUMBER,EMAIL,CREDITNUMBER,STATUS) VALUES((SELECT MAX(USERID) FROM bitemedb.USERS),?,?,?,?,'active')");
			stmt2.setString(1, Message.getValue(json, "id"));
			stmt2.setString(2, Message.getValue(json, "phone"));
			stmt2.setString(3, Message.getValue(json, "email"));
			stmt2.setString(4, Message.getValue(json, "credit"));
			stmt2.executeUpdate();

		} catch (SQLException e) {
			// log
			Logger.log(Level.WARNING, "DATABASE: SQLException in registerRegularClient");
			System.out.println("DATABASE: SQLException in registerRegularClient");

			try {
				conn.rollback();
			} catch (SQLException e1) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in registerRegularClient, rollback");
				System.out.println("DATABASE: SQLException in registerRegularClient, rollback");
			}
			response.put("command", "update");
			response.put("update", "could not add regular user to database");
			return response;
		}

		try {
			conn.commit();
		} catch (SQLException e) {
			Logger.log(Level.WARNING, "DATABASE: SQLException in registerRegularClient, commit");
			System.out.println("DATABASE: SQLException in registerRegularClient, commit");

			response.put("command", "update");
			response.put("update", "could not add regular user to database");
			return response;
		}
		response.put("command", "update");
		response.put("update", "regular user has been added to database");
		return response;
	}

	public int isEmployerExists(JSONObject json) {
		ResultSet rs;
		String employer = Message.getValue(json, "employer");
		//
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM EMPLOYERS WHERE name = ?");
			stmt.setString(1, employer);

			rs = stmt.executeQuery();
			if (rs.next()) {
				if (rs.getString("status").equals("Freeze")) {

					// log
					Logger.log(Level.DEBUG, "DATABASE: employer (" + employer + ") is Freezed");
					System.out.println("DATABASE: employer (" + employer + ") is Freezed");

					return -1;
				}
				// log
				Logger.log(Level.DEBUG, "DATABASE: employer (" + employer + ") is Active");
				System.out.println("DATABASE: employer (" + employer + ") is Active");

				return rs.getInt("employerID");
			}
		} catch (SQLException e) {
			Logger.log(Level.WARNING, "DATABASE: SQLException in isEmployerExists");
		}
		// log
		Logger.log(Level.DEBUG, "DATABASE: employer (" + employer + ") was NOT found");
		System.out.println("DATABASE: employer (" + employer + ") was NOT found");

		return -1;
	}

	@SuppressWarnings("unchecked")
	public JSONObject registerBusinessClient(JSONObject json, int employerID) {
		JSONObject response = new JSONObject();

		try {
			conn.setAutoCommit(false);

			PreparedStatement stmt1 = conn.prepareStatement(
					"INSERT INTO bitemedb.USERS (USERNAME,PASSWORD,ROLE,BRANCH,FIRSTNAME,LASTNAME) VALUES(?,?,'business customer',?,?,?)");
			stmt1.setString(1, Message.getValue(json, "username"));
			stmt1.setString(2, Message.getValue(json, "password"));
			stmt1.setString(3, Message.getValue(json, "branch"));
			stmt1.setString(4, Message.getValue(json, "firstname"));
			stmt1.setString(5, Message.getValue(json, "lastname"));
			stmt1.executeUpdate();

			PreparedStatement stmt2 = conn.prepareStatement(
					"INSERT INTO bitemedb.CUSTOMERS (USERID,ID,PHONENUMBER,EMAIL,CREDITNUMBER,STATUS,EMPLOYERID) VALUES((SELECT MAX(USERID) FROM bitemedb.USERS),?,?,?,?,'freeze',?)");
			stmt2.setString(1, Message.getValue(json, "id"));
			stmt2.setString(2, Message.getValue(json, "phone"));
			stmt2.setString(3, Message.getValue(json, "email"));
			stmt2.setString(4, Message.getValue(json, "credit"));
			stmt2.setInt(5, employerID);
			stmt2.executeUpdate();

		} catch (SQLException e) {
			// log
			Logger.log(Level.WARNING, "DATABASE: SQLException in registerBusinessClient");
			System.out.println("DATABASE: SQLException in registerBusinessClient");

			try {
				conn.rollback();
			} catch (SQLException e1) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in registerBusinessClient, rollback");
				System.out.println("DATABASE: SQLException in registerBusinessClient, rollback");
			}
			response.put("command", "update");
			response.put("update", "could not add business user to database");
			return response;
		}

		try {
			conn.commit();
		} catch (SQLException e) {
			Logger.log(Level.WARNING, "DATABASE: SQLException in registerBusinessClient, commit");
			System.out.println("DATABASE: SQLException in registerBusinessClient, commit");

			response.put("command", "update");
			response.put("update", "could not add business user to database");
			return response;
		}
		response.put("command", "update");
		response.put("update", "business user has been added to database");
		return response;
	}
}
