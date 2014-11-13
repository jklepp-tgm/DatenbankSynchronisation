package dbsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The entry point of the application.
 * 
 */
public class Main
{
	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args)
	{
		Configuration configuration = Configuration.getConfiguration();
		Middleware middleware = new Middleware(configuration);

		try
		{
			middleware.run();
		}
		catch (Exception ex)
		{
			logger.error("An unknwon error occured: " + ex.getMessage());
		}
	}
}