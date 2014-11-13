package dbsync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class represents the configuration of the application.
 */
public class Configuration
{
	final static Logger logger = LoggerFactory.getLogger(Configuration.class);

	// MySQL data
	public Connection mysql;
	// PostGre data
	public Connection postgre;
	// How often the application should poll the databases
	// Unit: milliseconds, default: 1000ms
	public int pollingInterval = 1000;

	public static class Connection
	{
		public String host;
		public String username;
		public String password;
		public String database;
		public int port;
	}

	/**
	 * Reads the application's configuration. If none exists, it creates a
	 * default one.
	 * 
	 * @return A Configuration object, if none exists, null
	 */
	public static Configuration getConfiguration()
	{
		File file = new File("configuration.json");

		if (!file.exists())
		{
			logger.info("No configuration found, creating a default one..");
			createDefaultConfiguration();
		}

		// cache for the json configuration
		StringBuilder json = new StringBuilder();
		String line = "";

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));

			while ((line = reader.readLine()) != null)
			{
				json.append(line);
			}

			reader.close();

			// create a Configuration object from the JSON String
			Configuration configuration = new Gson().fromJson(json.toString(),
					Configuration.class);

			return configuration;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * Creates a default configuration file.
	 */
	public static void createDefaultConfiguration()
	{
		Configuration configuration = new Configuration();

		Connection mysqlc = new Configuration.Connection();
		mysqlc.host = "127.0.0.1";
		mysqlc.username = "root";
		mysqlc.password = "";
		mysqlc.database = "dbsync";
		mysqlc.port = 3306;

		Connection postgrec = new Configuration.Connection();
		postgrec.host = "127.0.0.1";
		postgrec.username = "postgre";
		postgrec.password = "";
		postgrec.database = "dbsync";
		postgrec.port = 5432;

		configuration.mysql = mysqlc;
		configuration.postgre = postgrec;
		configuration.pollingInterval = 1000;

		// makes it easier to configure by humans
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(configuration);

		File file = new File("configuration.json");

		try
		{
			file.createNewFile();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));

			writer.write(json);
			writer.close();
		}
		catch (IOException ex)
		{
			logger.error("Failed to create a default configuration file: "
					+ ex.getMessage());
		}
	}
}