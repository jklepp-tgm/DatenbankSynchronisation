package dbsync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dbsync.ConnectionFactory.EDBDriver;

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
			logger.error("Failure while connecting to the database");
		}
	}

	public boolean doSync(Statement postgre)
	{
		if(this.connection == null || postgre == null) return false;
		
		ResultSet result = null;

		try
		{
			this.connection.setAutoCommit(false);

			result = this.connection.createStatement().executeQuery(
					"SELECT * FROM translationlog");

			while (result.next())
			{
				System.out.println("== MySQL result ==");

				String type = (String) result.getObject(7);
				String language = (String) result.getObject(5);

				if (type.equals("new"))
				{
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
				else if (type.equals("update"))
				{
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
				else if (type.equals("delete"))
				{
					String query = "UPDATE p_animal SET ";

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

					ResultSet existing = postgre
							.executeQuery("SELECT * FROM p_animal WHERE wname='"
									+ result.getObject(1) + "'");

					if (existing.next())
					{
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
			logger.error("[MySQL] Database error", ex);
			return false;
		}
	}
	
	public Connection getConnection()
	{
		return this.connection;
	}
}