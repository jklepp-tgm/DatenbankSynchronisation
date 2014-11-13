package dbsync;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Middleware
{
	final static Logger logger = LoggerFactory.getLogger(Middleware.class);
	
	private Configuration configuration;
	
	public Middleware(Configuration configuration)
	{
		this.configuration = configuration;
	}
	
	public void run() throws Exception
	{
		List<Animallog> logs = new ArrayList<Animallog>();
		
		MySQLHandler mysql = new MySQLHandler(this.configuration);
		PostgreHandler postgre = new PostgreHandler(this.configuration);
		
		while(true)
		{
			if(mysql.getConnection() == null || postgre.getConnection() == null)
			{
				logger.error("Failed to connect to the databases.");
				break;
			}
			
			logs = postgre.getAnimallog();
			
			if(!mysql.doSync(postgre.getConnection().createStatement()))
			{
				mysql.getConnection().rollback();
				postgre.getConnection().rollback();
				
				logger.error("Failed to synchronize MySQL");
				
				break;
			}
			
			if(!postgre.doSync(logs, mysql.getConnection().createStatement()))
			{
				mysql.getConnection().rollback();
				postgre.getConnection().rollback();
				
				logger.error("Failed to synchronize Postgre");
				
				break;
			}
			
			mysql.getConnection().createStatement().executeUpdate("TRUNCATE TABLE translationlog");
			postgre.getConnection().createStatement().executeUpdate("TRUNCATE TABLE animallog");

			mysql.getConnection().commit();
			postgre.getConnection().commit();
			
			Thread.sleep(configuration.pollingInterval);
		}
		
		mysql.getConnection().close();
		postgre.getConnection().close();
	}
}
