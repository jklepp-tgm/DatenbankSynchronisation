package dbsync;

/**
 * NO LONGER IN USE
 * @deprecated
 */
public class AllInOne
{
	public static void main(String[] args) throws Exception
	{
		/*
		List<Animallog> postgre = new ArrayList<Animallog>();

		int mlast = 0, plast = 0;

		// mysql
		MysqlDataSource mds = new MysqlDataSource();

		mds.setServerName("127.0.0.1");
		mds.setUser("root");
		mds.setPassword(msqlPW);
		mds.setDatabaseName("dbsync");

		// postgres
		SimpleDataSource pds = new SimpleDataSource();
		pds.setServerName("127.0.0.1");
		pds.setUser("postgres");
		pds.setPassword(psqlPW);
		pds.setDatabaseName("dbsync");

		Connection mcon = mds.getConnection();
		Connection pcon = pds.getConnection();

		int runs = 0;
		while (true)
		{
			mcon.setAutoCommit(false);
			pcon.setAutoCommit(false);

			Statement ms = mcon.createStatement();
			Statement ps = pcon.createStatement();

			ResultSet mresult = ms
					.executeQuery("SELECT * FROM translationlog WHERE lstamp > "
							+ mlast);
			ResultSet presult = ps
					.executeQuery("SELECT * FROM animallog WHERE lstamp > "
							+ plast);

			while (presult.next())
			{
				Animallog log = new Animallog();
				System.out.println("== PG result ==");
				for (int i = 1; i <= 8; i++)
				{
					log.wnameold = (String) presult.getObject(1);
					log.ger_nameold = (String) presult.getObject(2);
					log.eng_nameold = (String) presult.getObject(3);
					log.wnamenew = (String) presult.getObject(4);
					log.ger_namenew = (String) presult.getObject(5);
					log.eng_namenew = (String) presult.getObject(6);
					log.action = (String) presult.getObject(7);
					log.lstamp = (Integer) presult.getObject(8);
				}
				postgre.add(log);
			}

			presult.close();

			while (mresult.next())
			{
				try
				{
					System.out.println("== MySQL result ==");
					String type = (String) mresult.getObject(7);
					String language = (String) mresult.getObject(5);

					if (type.equals("new"))
					{
						ResultSet existing = ps
								.executeQuery("SELECT COUNT(*) FROM p_animal WHERE wname='"
										+ mresult.getObject(4) + "'");

						Long count = 0L;
						while (existing.next())
						{
							count = (Long) existing.getObject(1);
						}
						existing.close();

						if (count == 0)
							ps.executeUpdate("INSERT INTO p_animal VALUES('"
									+ mresult.getObject(4) + "', '', '');");

						String query = "UPDATE p_animal SET ";
						if (language.equals("ger"))
						{
							query += "ger_name = ";
						}
						else
						{
							query += "eng_name = ";
						}
						query += "'" + mresult.getObject(6) + "' WHERE wname='"
								+ mresult.getObject(4) + "'";
						ps.executeUpdate(query);
					}
					else if (type.equals("update"))
					{
						ResultSet existing = ps
								.executeQuery("SELECT COUNT(*) FROM p_animal WHERE wname='"
										+ mresult.getObject(4) + "'");

						Long count = 0L;
						while (existing.next())
						{
							count = (Long) existing.getObject(1);
						}
						existing.close();

						if (count == 0)
							ps.executeUpdate("INSERT INTO p_animal VALUES('"
									+ mresult.getObject(4) + "', '', '');");

						String query = "UPDATE p_animal SET wname='"
								+ mresult.getObject(4) + "', ";

						if (language.equals("ger"))
						{
							query += "ger_name = ";
						}
						else
						{
							query += "eng_name = ";
						}
						query += "'" + mresult.getObject(6) + "' WHERE wname='"
								+ mresult.getObject(1) + "'";
						// System.out.println(query);
						ps.executeUpdate(query);
					}
					else if (type.equals("delete"))
					{
						String query = "UPDATE p_animal SET ";

						if (mresult.getObject(2).equals("ger"))
						{
							query += "ger_name = ''";
						}
						else
						{
							query += "eng_name = ''";
						}
						query += " WHERE wname='" + mresult.getObject(1) + "'";

						ps.executeUpdate(query);

						ResultSet existing = ps
								.executeQuery("SELECT * FROM p_animal WHERE wname='"
										+ mresult.getObject(1) + "'");

						if (existing.next())
						{
							if (existing.getObject(2).equals("")
									&& existing.getObject(3).equals(""))
							{
								ps.executeUpdate("DELETE FROM p_animal WHERE wname='"
										+ existing.getObject(1) + "'");
							}
						}

						existing.close();
					}
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
					pcon.rollback();
					mcon.rollback();
					break;
				}
			}

			mresult.close();

			for (Animallog log : postgre)
			{
				try
				{
					if (log.action.equals("new"))
					{
						ResultSet existing = ms
								.executeQuery("SELECT COUNT(*) FROM m_animal WHERE wname='"
										+ log.wnamenew + "'");

						Long count = 0L;
						while (existing.next())
						{
							count = (Long) existing.getObject(1);
						}
						existing.close();

						if (count == 0)
							ms.executeUpdate("INSERT INTO m_animal VALUES('"
									+ log.wnamenew + "')");

						if (log.ger_namenew != null
								&& log.ger_namenew.length() > 0)
						{
							ms.executeUpdate(String
									.format("INSERT INTO translation VALUES('%s', '%s', '%s');",
											log.wnamenew, "ger",
											log.ger_namenew));
						}

						if (log.eng_namenew != null
								&& log.eng_namenew.length() > 0)
						{
							ms.executeUpdate(String
									.format("INSERT INTO translation VALUES('%s', '%s', '%s');",
											log.wnamenew, "eng",
											log.eng_namenew));
						}
					}
					else if (log.action.equals("update"))
					{
						ResultSet existing = ms
								.executeQuery("SELECT COUNT(*) FROM m_animal WHERE wname='"
										+ log.wnamenew + "'");

						Long count = 0L;
						while (existing.next())
						{
							count = (Long) existing.getObject(1);
						}
						existing.close();

						if (count == 0)
							ms.executeUpdate("INSERT INTO m_animal VALUES('"
									+ log.wnamenew + "')");

						if (log.ger_namenew != null
								&& log.ger_namenew.length() > 0)
						{
							ms.executeUpdate(String
									.format("UPDATE translation SET wname='%s', tname='%s' WHERE wname='%s' AND language='%s';",
											log.wnamenew, log.ger_namenew,
											log.wnameold, "ger"));
						}

						if (log.eng_namenew != null
								&& log.eng_namenew.length() > 0)
						{
							ms.executeUpdate(String
									.format("UPDATE translation SET wname='%s', tname='%s' WHERE wname='%s' AND language='%s';",
											log.wnamenew, log.eng_namenew,
											log.wnameold, "eng"));
						}
					}
					else if (log.action.equals("delete"))
					{
						String query = "DELETE FROM translation WHERE wname='%s' AND language='%s';";

						if (log.ger_nameold != null
								&& log.ger_nameold.length() > 0)
						{
							ms.executeUpdate(String.format(query, log.wnameold,
									"ger"));
						}

						if (log.eng_nameold != null
								&& log.eng_nameold.length() > 0)
						{
							ms.executeUpdate(String.format(query, log.wnameold,
									"eng"));
						}

						ResultSet existing = ms
								.executeQuery("SELECT COUNT(*) FROM translation WHERE wname='"
										+ log.wnameold + "'");

						Long count = 0L;
						while (existing.next())
						{
							count = (Long) existing.getObject(1);
						}
						existing.close();

						if (count == 0)
							ms.executeUpdate("DELETE FROM m_animal WHERE wname='"
									+ log.wnameold + "'");
					}
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
					pcon.rollback();
					mcon.rollback();
				}
			}
			postgre.clear();

			ms = mcon.createStatement();
			ps = pcon.createStatement();

			ms.executeUpdate("TRUNCATE TABLE translationlog");
			ps.executeUpdate("TRUNCATE TABLE animallog");

			mcon.commit();
			pcon.commit();

			if (runs == 100)
				break;
			Thread.sleep(1000);
		}

		mcon.close();
		pcon.close();*/
	}
}