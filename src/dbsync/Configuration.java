package dbsync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Configuration
{
	final static Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	public Connection mysql;
	public Connection postgre;
	public int pollingInterval;
	
	public class Connection
	{
		public String host;
		public String username;
		public String password;
		public String database;
		public int port;
	}
	
	public static Configuration getConfiguration()
	{
		File file = new File("configuration.json");
		
		if(!file.exists())
		{
			logger.error("Unable to locate the configuration.json file.\nPlease make sure this file exists.");
			return null;
		}
		
		StringBuilder json = new StringBuilder();
		String line = "";
		
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			
			while((line = reader.readLine()) != null)
			{
				json.append(line);
			}
			
			reader.close();
			
			Configuration configuration = new Gson().fromJson(json.toString(), Configuration.class);
			
			return configuration;
		}
		catch (IOException e)
		{
			logger.error("An error occured: ",e);
			
			return null;
		}
	}
}