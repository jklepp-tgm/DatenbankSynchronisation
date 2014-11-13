package dbsync;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
	final static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args)
	{
		Configuration configuration = Configuration.getConfiguration();
		
		if(configuration == null)
		{
			logger.error("Unable to load the configuration file.");
			System.exit(1);
		}
		Middleware middleware = new Middleware(configuration);
		
		try
		{
			middleware.run();
		}
		catch(Exception ex)
		{
			logger.error("An error occured", ex);
		}
	}
}