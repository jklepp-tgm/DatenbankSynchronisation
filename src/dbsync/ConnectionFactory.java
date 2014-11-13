package dbsync;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.jdbc2.optional.SimpleDataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * This factory provides a method to open a connection to various database
 * servers.
 */
public class ConnectionFactory
{
	// currently supported drivers
	public enum EDBDriver
	{
		MySQL, Postgre
	}

	/**
	 * Open a connection to the specified database server using the passed
	 * connection details.
	 * 
	 * @param driver
	 *            The database driver to use (currently: MySQL/Postgre)
	 * @param configuration
	 *            A Connection instance containing the connection details.
	 * @return A Connection object, null on error (unknown driver/connection
	 *         error).
	 * @throws SQLException
	 *             Thrown when a connection error occurs.
	 */
	public static Connection getConnection(EDBDriver driver,
			Configuration.Connection configuration) throws SQLException
	{
		if (configuration == null)
			return null;

		if (driver == EDBDriver.MySQL)
		{
			MysqlDataSource mds = new MysqlDataSource();

			mds.setServerName(configuration.host);
			mds.setUser(configuration.username);
			mds.setPassword(configuration.password);
			mds.setDatabaseName(configuration.database);

			return mds.getConnection();
		}
		else if (driver == EDBDriver.Postgre)
		{
			SimpleDataSource pds = new SimpleDataSource();
			pds.setServerName(configuration.host);
			pds.setUser(configuration.username);
			pds.setPassword(configuration.password);
			pds.setDatabaseName(configuration.database);

			return pds.getConnection();
		}
		return null;
	}
}