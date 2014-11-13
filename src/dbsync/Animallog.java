package dbsync;

/**
 * This class purpose is to cache all entries from the PostgreSQL Animallog table.
 * The reason behind this is that when synchronizing with MySQL, the Animallog table gets
 * new entries too, which causes double inserts when synchronizing back.
 * 
 * @author Andreas Willinger
 */
public class Animallog
{
	private String wnameold;
	private String ger_nameold;
	private String eng_nameold;
	private String wnamenew;
	private String ger_namenew;
	private String eng_namenew;
	private String action;
	
	/**
	 * Gets the wnameold.
	 *
	 * @return the wnameold
	 */
	public String getWnameold()
	{
		return wnameold;
	}
	
	/**
	 * Sets the wnameold.
	 *
	 * @param wnameold the new wnameold
	 */
	public void setWnameold(String wnameold)
	{
		this.wnameold = wnameold;
	}
	
	/**
	 * Gets the ger_nameold.
	 *
	 * @return the ger_nameold
	 */
	public String getGer_nameold()
	{
		return ger_nameold;
	}
	
	/**
	 * Sets the ger_nameold.
	 *
	 * @param ger_nameold the new ger_nameold
	 */
	public void setGer_nameold(String ger_nameold)
	{
		this.ger_nameold = ger_nameold;
	}
	
	/**
	 * Gets the eng_nameold.
	 *
	 * @return the eng_nameold
	 */
	public String getEng_nameold()
	{
		return eng_nameold;
	}
	
	/**
	 * Sets the eng_nameold.
	 *
	 * @param eng_nameold the new eng_nameold
	 */
	public void setEng_nameold(String eng_nameold)
	{
		this.eng_nameold = eng_nameold;
	}
	
	/**
	 * Gets the wnamenew.
	 *
	 * @return the wnamenew
	 */
	public String getWnamenew()
	{
		return wnamenew;
	}
	
	/**
	 * Sets the wnamenew.
	 *
	 * @param wnamenew the new wnamenew
	 */
	public void setWnamenew(String wnamenew)
	{
		this.wnamenew = wnamenew;
	}
	
	/**
	 * Gets the ger_namenew.
	 *
	 * @return the ger_namenew
	 */
	public String getGer_namenew()
	{
		return ger_namenew;
	}
	
	/**
	 * Sets the ger_namenew.
	 *
	 * @param ger_namenew the new ger_namenew
	 */
	public void setGer_namenew(String ger_namenew)
	{
		this.ger_namenew = ger_namenew;
	}
	
	/**
	 * Gets the eng_namenew.
	 *
	 * @return the eng_namenew
	 */
	public String getEng_namenew()
	{
		return eng_namenew;
	}
	
	/**
	 * Sets the eng_namenew.
	 *
	 * @param eng_namenew the new eng_namenew
	 */
	public void setEng_namenew(String eng_namenew)
	{
		this.eng_namenew = eng_namenew;
	}
	
	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}
	
	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action)
	{
		this.action = action;
	}
}