package dbsync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbsync.ConnectionFactory.EDBDriver;

/**
 * This class handles the synchronization FROM the PostGre database TO a MySQL
 * one.
 */
public class PostgreHandler
{
	final static Logger logger = LoggerFactory.getLogger(PostgreHandler.class);
	private Connection connection;

	public PostgreHandler(Configuration configuration)
	{
		try
		{
			this.connection = ConnectionFactory.getConnection(
					EDBDriver.Postgre, configuration.postgre);
		}
		catch (SQLException ex)
		{
			logger.debug("postgre error: " + ex.getMessage());
		}
	}

	/**
	 * Polls a list of changes made on the PostGre p_animal table and saves them
	 * in a list.
	 * 
	 * @return A list of changes, null if there was a database error.
	 */
	public List<Animallog> getAnimallog()
	{
		if (this.connection == null)
			return null;

		try
		{
			// start a transaction
			this.connection.setAutoCommit(false);

			List<Animallog> logs = new ArrayList<Animallog>();

			Statement statement = this.connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT * FROM animallog");

			while (result.next())
			{
				Animallog log = new Animallog();
				System.out.println("-> PostGre changes detected");
				for (int i = 1; i <= 8; i++)
				{
					log.setWnameold((String) result.getObject(1));
					log.setGer_nameold((String) result.getObject(2));
					log.setEng_nameold((String) result.getObject(3));
					log.setWnamenew((String) result.getObject(4));
					log.setGer_namenew((String) result.getObject(5));
					log.setEng_namenew((String) result.getObject(6));
					log.setAction((String) result.getObject(7));
				}
				logs.add(log);
			}

			result.close();
			return logs;
		}
		catch (SQLException ex)
		{
			logger.debug("Failure while querying the database: "
					+ ex.getMessage());
			return null;
		}
	}

	/**
	 * Does the synchronization, by looping through the logs list and writing
	 * each change (INSERT, UPDATE, DELETE) to the MySQL database. Also maps the
	 * PostGre table to respect the constraints on the MySQL side.
	 * 
	 * @param logs
	 *            A list containing the changes on the PostGre side.
	 * @param mysql
	 *            A Statement object from an open MySQL connection.
	 * @return true, if synchronization was successful, false if not -
	 *         transaction has been rolled back.
	 */
	public boolean doSync(List<Animallog> logs, Statement mysql)
	{
		if (logs == null || mysql == null)
			return false;

		for (Animallog log : logs)
		{
			try
			{
				// INSERT
				if (log.getAction().equals("new"))
				{
					// if there is no row in the m_animal table yet, create one
					ResultSet existing = mysql
							.executeQuery("SELECT COUNT(*) FROM m_animal WHERE wname='"
									+ log.getWnamenew() + "'");

					Long count = 0L;
					if (existing.next())
					{
						count = (Long) existing.getObject(1);
					}
					existing.close();

					// ON DUPLICATE KEY: replace currently existing key.
					if (count == 0)
						mysql.executeUpdate("INSERT INTO m_animal VALUES('"
								+ log.getWnamenew()
								+ "') ON DUPLICATE KEY UPDATE wname = '"
								+ log.getWnamenew() + "'");

					// actually insert t he translations, if they exist.
					if (log.getGer_namenew() != null
							&& log.getGer_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("INSERT INTO translation VALUES('%s', '%s', '%s') ON DUPLICATE KEY UPDATE tname = '%s';",
										log.getWnamenew(), "ger",
										log.getGer_namenew(),
										log.getGer_namenew()));
					}

					if (log.getEng_namenew() != null
							&& log.getEng_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("INSERT INTO translation VALUES('%s', '%s', '%s') ON DUPLICATE KEY UPDATE tname = '%s';",
										log.getWnamenew(), "eng",
										log.getEng_namenew(),
										log.getEng_namenew()));
					}
				}
				// UPDATE
				else if (log.getAction().equals("update"))
				{
					// basically the same as insert, just with different column
					// locations
					ResultSet existing = mysql
							.executeQuery("SELECT COUNT(*) FROM m_animal WHERE wname='"
									+ log.getWnamenew() + "'");

					Long count = 0L;
					while (existing.next())
					{
						count = (Long) existing.getObject(1);
					}
					existing.close();

					if (count == 0)
						mysql.executeUpdate("INSERT INTO m_animal VALUES('"
								+ log.getWnamenew() + "')");

					// update the already existing translations only if the ones
					// on the PostGre side are not empty.
					if (log.getGer_namenew() != null
							&& log.getGer_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("UPDATE translation SET wname='%s', tname='%s' WHERE wname='%s' AND language='%s';",
										log.getWnamenew(),
										log.getGer_namenew(),
										log.getWnameold(), "ger"));
					}

					if (log.getEng_namenew() != null
							&& log.getEng_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("UPDATE translation SET wname='%s', tname='%s' WHERE wname='%s' AND language='%s';",
										log.getWnamenew(),
										log.getEng_namenew(),
										log.getWnameold(), "eng"));
					}
				}
				// DELETE
				else if (log.getAction().equals("delete"))
				{
					String query = "DELETE FROM translation WHERE wname='%s' AND language='%s';";

					// first, remove all translations
					mysql.executeUpdate(String.format(query, log.getWnameold(),
							"ger"));
					mysql.executeUpdate(String.format(query, log.getWnameold(),
							"eng"));

					ResultSet existing = mysql
							.executeQuery("SELECT COUNT(*) FROM translation WHERE wname='"
									+ log.getWnameold() + "'");

					Long count = 0L;
					while (existing.next())
					{
						count = (Long) existing.getObject(1);
					}
					existing.close();

					// and if there are actually no translations left, delete
					// the entry in the m_animal table too.
					if (count == 0)
						mysql.executeUpdate("DELETE FROM m_animal WHERE wname='"
								+ log.getWnameold() + "'");
				}
			}
			catch (SQLException ex)
			{
				logger.debug("[Postgre] Database error: " + ex.getMessage());
				return false;
			}
		}

		return true;
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