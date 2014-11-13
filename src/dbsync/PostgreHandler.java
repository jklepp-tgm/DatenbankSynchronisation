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
			logger.error("Failure while connecting to the database");
		}
	}

	public List<Animallog> getAnimallog()
	{
		if (this.connection == null)
			return null;

		try
		{
			this.connection.setAutoCommit(false);

			List<Animallog> logs = new ArrayList<Animallog>();

			Statement statement = this.connection.createStatement();

			ResultSet result = statement
					.executeQuery("SELECT * FROM animallog");

			while (result.next())
			{
				Animallog log = new Animallog();
				System.out.println("== PG result ==");
				for (int i = 1; i <= 8; i++)
				{
					log.setWnameold((String) result.getObject(1));
					log.setGer_nameold((String) result.getObject(2));
					log.setEng_nameold((String) result.getObject(3));
					log.setWnamenew((String) result.getObject(4));
					log.setGer_namenew((String) result.getObject(5));
					log.setEng_namenew((String) result.getObject(6));
					log.setAction((String) result.getObject(7));
					log.setLstamp((Integer) result.getObject(8));
				}
				logs.add(log);
			}

			result.close();
			return logs;
		}
		catch (SQLException ex)
		{
			logger.error("Failure while querying the database", ex);
			return null;
		}
	}
	
	public boolean doSync(List<Animallog> logs, Statement mysql)
	{
		if(logs == null || mysql == null) return false;
		
		for (Animallog log : logs)
		{
			try
			{
				if (log.getAction().equals("new"))
				{
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
					
					if (log.getGer_namenew() != null
							&& log.getGer_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("INSERT INTO translation VALUES('%s', '%s', '%s');",
										log.getWnamenew(), "ger",
										log.getGer_namenew()));
					}

					if (log.getEng_namenew() != null
							&& log.getEng_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("INSERT INTO translation VALUES('%s', '%s', '%s');",
										log.getWnamenew(), "eng",
										log.getEng_namenew()));
					}
				}
				else if (log.getAction().equals("update"))
				{
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

					if (log.getGer_namenew() != null
							&& log.getGer_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("UPDATE translation SET wname='%s', tname='%s' WHERE wname='%s' AND language='%s';",
										log.getWnamenew(), log.getGer_namenew(),
										log.getWnameold(), "ger"));
					}

					if (log.getEng_namenew() != null
							&& log.getEng_namenew().length() > 0)
					{
						mysql.executeUpdate(String
								.format("UPDATE translation SET wname='%s', tname='%s' WHERE wname='%s' AND language='%s';",
										log.getWnamenew(), log.getEng_namenew(),
										log.getWnameold(), "eng"));
					}
				}
				else if (log.getAction().equals("delete"))
				{
					String query = "DELETE FROM translation WHERE wname='%s' AND language='%s';";

					if (log.getGer_nameold() != null
							&& log.getGer_nameold().length() > 0)
					{
						mysql.executeUpdate(String.format(query, log.getWnameold(),
								"ger"));
					}

					if (log.getEng_nameold() != null
							&& log.getEng_nameold().length() > 0)
					{
						mysql.executeUpdate(String.format(query, log.getWnameold(),
								"eng"));
					}

					ResultSet existing = mysql
							.executeQuery("SELECT COUNT(*) FROM translation WHERE wname='"
									+ log.getWnameold() + "'");

					Long count = 0L;
					while (existing.next())
					{
						count = (Long) existing.getObject(1);
					}
					existing.close();

					if (count == 0)
						mysql.executeUpdate("DELETE FROM m_animal WHERE wname='"
								+ log.getWnameold() + "'");
				}
			}
			catch (SQLException ex)
			{
				logger.error("[Postgre] Database error", ex);
				return false;
			}
		}
		
		return true;
	}
	
	public Connection getConnection()
	{
		return this.connection;
	}
}