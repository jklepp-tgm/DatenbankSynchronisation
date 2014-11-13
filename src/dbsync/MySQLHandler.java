package dbsync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbsync.ConnectionFactory.EDBDriver;

/**
 * This class handles the synchronization FROM the MySQL database TO a PostGre
 * one.
 */
public class MySQLHandler
{
	final static Logger logger = LoggerFactory.getLogger(MySQLHandler.class);
	private Connection connection;

	public MySQLHandler(Configuration configuration)
	{
		try
		{
			this.connection = ConnectionFactory.getConnection(EDBDriver.MySQL,
					configuration.mysql);
		}
		catch (SQLException ex)
		{
			logger.debug("mysql error " + ex.getMessage());
		}
	}

	/**
	 * Does the synchronization, by polling the Translationlog table and writing
	 * each change (INSERT, UPDATE, DELETE) to the PostGre database. It also
	 * maps the MySQL relations to fit into PostGre's.
	 * 
	 * @param postgre
	 *            A Statement object from an open PostGre connection.
	 * @return true, if synchronization was successful, false if not -
	 *         transaction has been rolled back.
	 */
	public boolean doSync(Statement postgre)
	{
		if (this.connection == null || postgre == null)
			return false;

		ResultSet result = null;

		try
		{
			// start a transaction
			this.connection.setAutoCommit(false);

			result = this.connection.createStatement().executeQuery(
					"SELECT * FROM translationlog");

			while (result.next())
			{
				System.out.println("-> MySQL changes detected");

				String type = (String) result.getObject(7);
				String language = (String) result.getObject(5);

				// INSERT
				if (type.equals("new"))
				{
					// if there is no row in the p_animal table yet, create one
					ResultSet existing = postgre
							.executeQuery("SELECT COUNT(*) FROM p_animal WHERE wname='"
									+ result.getObject(4) + "'");

					Long count = 0L;
					while (existing.next())
					{
						count = (Long) existing.getObject(1);
					}
					existing.close();

					if (count == 0)
						postgre.executeUpdate("INSERT INTO p_animal VALUES('"
								+ result.getObject(4) + "', '', '');");

					// map the MySQL side to PostGre
					String query = "UPDATE p_animal SET ";

					if (language.equals("ger"))
					{
						query += "ger_name = ";
					}
					else
					{
						query += "eng_name = ";
					}

					query += "'" + result.getObject(6) + "' WHERE wname='"
							+ result.getObject(4) + "'";
					postgre.executeUpdate(query);
				}
				// UPDATE
				else if (type.equals("update"))
				{
					// if there is no row in the p_animal table yet, create one
					// otherwise, we will not be able to synchronize the changes
					// if no entry exists on PostGre yet.
					ResultSet existing = postgre
							.executeQuery("SELECT COUNT(*) FROM p_animal WHERE wname='"
									+ result.getObject(4) + "'");

					Long count = 0L;
					while (existing.next())
					{
						count = (Long) existing.getObject(1);
					}
					existing.close();

					if (count == 0)
						postgre.executeUpdate("INSERT INTO p_animal VALUES('"
								+ result.getObject(4) + "', '', '');");

					String query = "UPDATE p_animal SET wname='"
							+ result.getObject(4) + "', ";

					if (language.equals("ger"))
					{
						query += "ger_name = ";
					}
					else
					{
						query += "eng_name = ";
					}
					query += "'" + result.getObject(6) + "' WHERE wname='"
							+ result.getObject(1) + "'";
					// System.out.println(query);
					postgre.executeUpdate(query);
				}
				// DELETE
				else if (type.equals("delete"))
				{
					String query = "UPDATE p_animal SET ";

					// as our first strategy, only set deleted translation to an
					// empty string.
					if (result.getObject(2).equals("ger"))
					{
						query += "ger_name = ''";
					}
					else
					{
						query += "eng_name = ''";
					}
					query += " WHERE wname='" + result.getObject(1) + "'";

					postgre.executeUpdate(query);

					// get a list of current animals on the PostGre side
					ResultSet existing = postgre
							.executeQuery("SELECT * FROM p_animal WHERE wname='"
									+ result.getObject(1) + "'");

					if (existing.next())
					{
						// if there is an entry and both translations are empty,
						// delete the row.
						if (existing.getObject(2).equals("")
								&& existing.getObject(3).equals(""))
						{
							postgre.executeUpdate("DELETE FROM p_animal WHERE wname='"
									+ existing.getObject(1) + "'");
						}
					}

					existing.close();
				}
			}

			if (result != null)
				result.close();
			return true;
		}
		catch (SQLException ex)
		{
			logger.debug("[MySQL] Database error: " + ex.getMessage());
			return false;
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection()
	{
		return this.connection;
	}
}