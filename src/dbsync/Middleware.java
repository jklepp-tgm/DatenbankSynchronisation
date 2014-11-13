package dbsync;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the actual synchronization between the databases. It does
 * this by calling each database respective Handler class.
 * 
 */
public class Middleware
{
	final static Logger logger = LoggerFactory.getLogger(Middleware.class);

	private Configuration configuration;

	public Middleware(Configuration configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * Starts the synchronization cycle. This can also be detoured to a separate
	 * thread.
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception
	{
		// this will hold all rows from the Animallog table
		List<Animallog> logs = new ArrayList<Animallog>();

		MySQLHandler mysql = new MySQLHandler(this.configuration);
		PostgreHandler postgre = new PostgreHandler(this.configuration);

		while (true)
		{
			logger.info("== CHECKING ==");

			// one or more connections failed
			if (mysql.getConnection() == null
					|| postgre.getConnection() == null)
			{
				logger.error("Failed to connect to the databases.");
				break;
			}

			// query PostGre changes & save them
			logs = postgre.getAnimallog();

			// synchronize MySQL
			if (!mysql.doSync(postgre.getConnection().createStatement()))
			{
				mysql.getConnection().rollback();
				postgre.getConnection().rollback();

				logger.error("Failed to synchronize MySQL");

				break;
			}

			// Synchronize PostGre
			if (!postgre.doSync(logs, mysql.getConnection().createStatement()))
			{
				mysql.getConnection().rollback();
				postgre.getConnection().rollback();

				logger.error("Failed to synchronize Postgre");

				break;
			}

			// clear the logging tables, so we don't synchronize the same stuff
			// multiple times.
			mysql.getConnection().createStatement()
					.executeUpdate("TRUNCATE TABLE translationlog");
			postgre.getConnection().createStatement()
					.executeUpdate("TRUNCATE TABLE animallog");

			// write the above changes to the databases.
			mysql.getConnection().commit();
			postgre.getConnection().commit();

			Thread.sleep(configuration.pollingInterval);
		}

		if (mysql.getConnection() != null)
			mysql.getConnection().close();
		if (postgre.getConnection() != null)
			postgre.getConnection().close();
	}
}
