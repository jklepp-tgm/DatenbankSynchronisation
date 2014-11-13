package dbsync;

import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.jdbc2.optional.SimpleDataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class ConnectionFactory
{
	public enum EDBDriver
	{
		MySQL,
		Postgre
	}
	
	public static Connection getConnection(EDBDriver driver, Configuration.Connection configuration) throws SQLException
	{
		if(driver == EDBDriver.MySQL)
		{
			MysqlDataSource mds = new MysqlDataSource();

			mds.setServerName(configuration.host);
			mds.setUser(configuration.username);
			mds.setPassword(configuration.password);
			mds.setDatabaseName(configuration.database);
			
			return mds.getConnection();
		}
		else if(driver == EDBDriver.Postgre)
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