package serverSide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import communicationUtil.Logger;
import communicationUtil.Logger.Level;

public class DataBase {

	private static Connection conn;

	final private String password = "adminadmin";

	final private String user = "root";

	// TODO if driver fails ?????
	public void initialize() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

			// log
			Logger.log(Level.DEBUG, "Driver definition succeed");
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			// log
			Logger.log(Level.WARNING, "Driver definition failed");
			System.out.println("Driver definition failed");
		}

		conn = DriverManager.getConnection("jdbc:mysql://localhost/myskype?serverTimezone=IST", user, password);

		// log
		Logger.log(Level.DEBUG, "SQL connection succeed");
		System.out.println("SQL connection succeed");

	}

	public void addTextMsg(String sourceUserName, String destUserName, String text) {
		synchronized (conn) {
			Statement stmt;

			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO history (source,destination,text,date) VALUES ('" + sourceUserName
						+ "','" + destUserName + "','" + text + "',now())");

				// log
				Logger.log(Level.DEBUG, "DATABASE: text has been added: " + text);
				System.out.println("DATABASE: text has been added: " + text);

			} catch (SQLException e) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in addTextMsg");
			}

		}
	}

	public Boolean approveUser(String userName, String password) {
		synchronized (conn) {
			Statement stmt;
			ResultSet rs;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM users WHERE username='" + userName + "'");
				if (rs.next()) {
					if (password.equals(rs.getString("password"))) {

						// log
						Logger.log(Level.DEBUG, "DATABASE: user (" + userName + ") is approved");
						System.out.println("DATABASE: user (" + userName + ") is approved");
						return true;
					}
					// log
					Logger.log(Level.DEBUG, "DATABASE: user (" + userName + ") is NOT approved");
					System.out.println("DATABASE: user (" + userName + ") is NOT approved");
					return false;
				}
			} catch (SQLException e) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in approveUser");
			}
			return false;
		}
	}

	public Boolean findUser(String userName) {
		synchronized (conn) {
			Statement stmt;
			ResultSet rs;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM users WHERE username='" + userName + "'");
				if (rs.next() == false) {

					// log
					Logger.log(Level.DEBUG, "DATABASE: user (" + userName + ") was NOT found");
					System.out.println("DATABASE: user (" + userName + ") was NOT found");
					return false;
				}
			} catch (SQLException e) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in findUser");
			}
			// log
			Logger.log(Level.DEBUG, "DATABASE: user (" + userName + ") was found");
			System.out.println("DATABASE: user (" + userName + ") was found");
			return true;
		}
	}

	public void addUser(String userName, String password) {
		synchronized (conn) {
			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(
						"INSERT INTO users (username,password) VALUES ('" + userName + "','" + password + "')");

				// log
				Logger.log(Level.DEBUG, "DATABASE: user (" + userName + ") was added");
				System.out.println("DATABASE: user (" + userName + ") was added");
			} catch (SQLException e) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in addUser");
			}
		}
	}

	// TODO maybe order by DateTime
	public ResultSet getAllChat(String sourceUserName, String destUserName) {
		synchronized (conn) {
			Statement stmt;
			ResultSet rs;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(
						"SELECT * FROM myskype.history WHERE source in (\"" + sourceUserName + "\",\"" + destUserName
								+ "\") and destination in (\"" + sourceUserName + "\",\"" + destUserName + "\")");
				if (rs.next() == false) {
					// log
					Logger.log(Level.DEBUG,
							"DATABASE: history between " + sourceUserName + " and " + destUserName + " was NOT found");
					System.out.println(
							"DATABASE: history between " + sourceUserName + " and " + destUserName + " was NOT found");
					return null;
				}
			} catch (SQLException e) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in getAllChat");
				return null;
			}
			// log
			Logger.log(Level.DEBUG,
					"DATABASE: history between " + sourceUserName + " and " + destUserName + " was found");
			System.out.println("DATABASE: history between " + sourceUserName + " and " + destUserName + " was found");
			return rs;
		}
	}

	// TODO maybe order by DateTime
	public ResultSet getNewChat(String sourceUserName, String destUserName, String time, String date) {
		synchronized (conn) {
			Statement stmt;
			ResultSet rs;

			// YYYY-MM-DD HH:MM:SS
			String dateTime = date + " " + time;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM myskype.history WHERE source in (\"" + sourceUserName + "\",\""
						+ destUserName + "\") and destination in (\"" + sourceUserName + "\",\"" + destUserName
						+ "\") and date > \"" + dateTime + "\"");
				if (rs.next() == false) {
					// log
					Logger.log(Level.DEBUG, "DATABASE: history between (time: " + time + " date: " + date + ") "
							+ sourceUserName + " and " + destUserName + " was NOT found");
					System.out.println("DATABASE: history between (time: " + time + " date: " + date + ") "
							+ sourceUserName + " and " + destUserName + " was NOT found");
					return null;
				}
			} catch (SQLException e) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in getNewChat");
				return null;
			}
			// log
			Logger.log(Level.DEBUG, "DATABASE: history between (time: " + time + " date: " + date + ") "
					+ sourceUserName + " and " + destUserName + " was found");
			System.out.println("DATABASE: history between (time: " + time + " date: " + date + ") " + sourceUserName
					+ " and " + destUserName + " was found");
			return rs;
		}
	}

	public ResultSet getFriendList(String userName) {
		synchronized (conn) {
			Statement stmt;
			ResultSet rs;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM users WHERE username !='" + userName + "'");
				if (rs.next() == false) {
					// log
					Logger.log(Level.DEBUG, "DATABASE: friend list for: " + userName + " could not been found");
					System.out.println("DATABASE: friend list for: " + userName + " could not been found");
					return null;
				}
			} catch (SQLException e) {
				Logger.log(Level.WARNING, "DATABASE: SQLException in getFriendList");
				return null;
			}
			// log
			Logger.log(Level.DEBUG, "DATABASE: friend list for: " + userName + " has been found");
			System.out.println("DATABASE: friend list for: " + userName + " has been found");
			return rs;
		}
	}
}
