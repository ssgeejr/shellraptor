package com.gee5.tools.shellraptor;

import java.sql.*;
import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

public abstract class SQLEditor extends PreparedStatementManager{
	static Logger logger = Logger.getLogger("ShellRaptor");
  private final String NEW_LINE = System.getProperties().getProperty("line.separator");

  private final String[] TABLE_TYPES = {"TABLE"};
  protected Connection conn = null;
  protected ConnectionManager  conMgr = new ConnectionManager();
  protected TagReader tReader = null;
  public abstract void exit();
  private long start = 0l;
  private Statement stmt = null;
  private ResultSet rset = null;
  private ResultSetMetaData rsmd = null;
  private int maxRowCount = 0;
  private int queryType = 0;
  private int results = 0;
  private int setBreak = 0;
  private int breakPoint = 0 ;
  private boolean resultBoolean = false;
  private boolean didFail = false;
  private StringBuffer resultData = new StringBuffer();
  private StringBuffer dividor = new StringBuffer();
  private ResultSetHeader rsHeader = null;
  private boolean EXPORT_TO_FILE = false;
  private DataOutputStream outputStream = null;
  private StringBuffer exportFile = new StringBuffer();
  private boolean SILENT = false;
  private boolean IS_SELECT = false;
  protected void startEditor(){
    BufferedReader input = new BufferedReader( new InputStreamReader ( System.in ));
    try{
      stmt = conn.createStatement();
      System.out.println("***** NOW ACCEPTING SQL COMMANDS *****");
      System.out.print(">> ");
      String command = null;
      StringBuffer rollingCommand = new StringBuffer();
      while ( (command = input.readLine()) != null) {
        if(command.trim().endsWith(";")){
          rollingCommand.append("\t" + command + "\n");
          if(rollingCommand.indexOf("exit") > -1)
            break;
          processQuery(rollingCommand.toString());
          rollingCommand.setLength(0);
          System.out.print(">> ");
        }else{
          rollingCommand.append("\t" + command + "\n");
        }
      }
    }catch(Exception ex){
    	logger.error("**FATAL-ERR**",ex);
    }finally{
      try {input.close(); } catch (Exception ex) {}
    }
    exit();
  }

  private void processQuery(String sql) throws Exception{
    start = System.currentTimeMillis();
//    SILENT = false;
    EXPORT_TO_FILE = false;
    IS_SELECT = false;
//--this is just an idea, but I think it's important
//to have a way to pipe huge files out to the a comma delimmeted file
//    System.out.println("[" + sql + "]  LOC: " + sql.trim().toUpperCase().indexOf("-SILENT"));
    if(sql.trim().toUpperCase().indexOf("-SILENT") > -1){
      System.out.println("\t**Entering silent mode");
      SILENT = true;
      return;
    }else if(sql.trim().toUpperCase().indexOf("-VERBOSE") > -1){
      System.out.println("\t**Entering verbose mode");
      SILENT = false;
      return;
    }

    if(sql.trim().indexOf("#") > -1){
      exportFile.setLength(0);
      sql = findExportFile(sql).trim();
      println("SQL [" + sql + "]");
      println("ExportFile [" + exportFile.toString() + "]");
      try{
        outputStream = new DataOutputStream(new FileOutputStream(exportFile.toString()));
        EXPORT_TO_FILE = true;
      }catch(Exception ex){
        println("File failed to open for writing: " + ex.getMessage());
        EXPORT_TO_FILE = false;
        return;
      }
    }

    if(sql.trim().startsWith("@")){
      String filename = sql.trim().substring(1,sql.indexOf(";")-1);
       if(!new File(filename).exists()){
         println("External SQL File [" + filename + "] does not exist");
         return;
       }else{
         BufferedReader inputStream = null;
         try {
           StringBuffer sqlBuffer = new StringBuffer();
           inputStream = new BufferedReader(new FileReader(filename));
           String input = "";
           while( (input = inputStream.readLine()) != null) {
             sqlBuffer.append(input).append("\n");
           } //end while
           sql = sqlBuffer.toString();
           println(">> " + sql);
         } catch (Exception ex) {
         	logger.error("**FATAL-ERR**",ex);
           return;
         }finally{
           try {inputStream.close(); } catch (Exception ex) {}
         }
       }//end if-else
    }

    if(sql.length() < 12
       && !sql.trim().toUpperCase().startsWith("DESC")
       && !sql.trim().toUpperCase().startsWith("HELP")
       && !sql.trim().toUpperCase().startsWith("OPEN")
       && !sql.trim().toUpperCase().startsWith("LIST-ALIAS")
       && !sql.trim().toUpperCase().startsWith("LIST-TABLES")
       && !sql.trim().toUpperCase().startsWith("LIST-DBINFO")
       && !sql.trim().toUpperCase().startsWith("LIST-INDEX")
       && !sql.trim().toUpperCase().startsWith("LOAD-PS")
       && !sql.trim().toUpperCase().startsWith("INIT-PS")
       && !sql.trim().toUpperCase().startsWith("LIST-PS")
       && !sql.trim().toUpperCase().startsWith("EXEC-PS")
       && !sql.trim().toUpperCase().startsWith("SAVE-PS")){
      println("INVALID SQL STATEMENT\n");
      return;
    }



    try{
      int pstateType = -1;
      String aliasName = null;
//      if(sql.trim().startsWith("@")){
//        println("(Execute External Script [@]) NOT YET IMPLEMENTED...ON THE LIST THOUGH [Action item 1]");
//        return;
//      }else

      if(sql.trim().toUpperCase().startsWith("EXEC-PS")){
        try {
          resetArgs();
          StringTokenizer stok = new StringTokenizer(sql.substring(0,sql.indexOf(";"))," ");
          stok.nextToken();
          aliasName = stok.nextToken();
          while(stok.hasMoreTokens()){
            setArguement(stok.nextToken());
          }
          pstateType = getPSType(aliasName);
        } catch (Exception ex) {
          println("Invalid format: exec-ps <alias> <args1> <args2> <args..n>;");
          logger.error("**FATAL-ERR**",ex);
          return;
        }
      }else if(sql.trim().toUpperCase().startsWith("SAVE-PS")){
         try {
           StringTokenizer stok = new StringTokenizer(sql.substring(0, sql.indexOf(";")), " ");
           stok.nextToken();
           savePreStatements(stok.nextToken());
         } catch (Exception ex) {
           println("Invalid format: save-ps <file-name>;");
           logger.error("**FATAL-ERR**",ex);
           return;
         }
         return;
      }

      if(sql.trim().toUpperCase().startsWith("LOAD-PS")){
        try {
          StringTokenizer stok = new StringTokenizer(sql," ");
          stok.nextToken();
          String filename = stok.nextToken().trim();
          loadExternalFile(filename.substring(0,filename.indexOf(";")));
//          System.out.println("Loading external preparedStatement File [" + stok.nextToken() + "]");
        } catch (Exception ex) {
        	logger.error("**FATAL-ERR**",ex);
          println("Invalid format: load-ps <file>;");
        }
        return;
      }else if(sql.trim().toUpperCase().startsWith("INIT-PS")){
        try {
          String tmpstr = sql.substring(7);
          int start = tmpstr.indexOf(" ");
          if(start > -1){
            int nextStart = tmpstr.indexOf(" ",start + 1);
            if(nextStart > -1){
              createPreparedStatement(tmpstr.substring(start,nextStart).trim(),tmpstr.substring(nextStart,tmpstr.indexOf(";")).trim());
            }else throw new Exception("Invalid format");
          }else throw new Exception("Invalid format");
        } catch (Exception ex) {
        	logger.error("**FATAL-ERR**",ex);
          println("Invalid format: init-ps <alias> <query>;");
        }
        return;
      }else if(sql.trim().toUpperCase().startsWith("LIST-PS")){
        listStatements();
        return;
      }else if(sql.trim().startsWith("%")){
        println("(Save Output to External File [#]) NOT YET IMPLEMENTED...ON THE LIST THOUGH [Action item 2]");
        return;
      }else if(sql.trim().toUpperCase().startsWith("LIST-ALIAS")){
        Enumeration enm = tReader.getNodes().keys();
        while(enm.hasMoreElements()){
          String key = (String)enm.nextElement();
          println("\tAlias [" + key + "]\t\tURL [" + tReader.getTagValue(key,"url") + "]");
        }
        System.out.println("-------------------------------------------\n\t{Runtime: [" + ((System.currentTimeMillis() - start) / 1000) + " seconds]}\n");
        return;
      }else if(sql.trim().toUpperCase().startsWith("LIST-TABLE")){
        DatabaseMetaData dbmd = conn.getMetaData();
        if( dbmd != null ){
          ResultSet catalogs = dbmd.getTables(null,null,null,TABLE_TYPES);
          println("****** TABLE LISTINGS ******");
          while( catalogs.next() )
            println("\t" + catalogs.getString("TABLE_NAME"));
          catalogs.close();
        }
        System.out.println("-------------------------------------------\n\t{Runtime: [" + ((System.currentTimeMillis() - start) / 1000) + " seconds]}\n");
        return;
      }else if(sql.trim().toUpperCase().startsWith("LIST-INDEX")){
        DatabaseMetaData dbmd = conn.getMetaData();
        String table_name = (sql.trim().substring(10,sql.indexOf(";") -1)).trim();
        println("Index detail for table [" + table_name + "]");
        ResultSet indexList = dbmd.getIndexInfo(null,null,table_name,false,false);
        while(indexList.next()) {
          print("\tColumn Name\t\t["+indexList.getString("COLUMN_NAME")+"]");
          println("\tIndex Name\t\t["+indexList.getString("INDEX_NAME")+"]");
        }
        indexList.close();
        println("-------------------------------------------\n\t{Runtime: [" + ((System.currentTimeMillis() - start) / 1000) + " seconds]}\n");
        return;
      }else if(sql.trim().toUpperCase().startsWith("LIST-DBINFO")){
        DatabaseMetaData dbmd = conn.getMetaData();
        println("Database Product\t[" + dbmd.getDatabaseProductName() + "]");
        println("Database Version\t[" + dbmd.getDatabaseProductVersion() + "]");
        println("Driver Name\t\t[" + dbmd.getDriverName() + "]");
        println("Driver Version\t\t[" + dbmd.getDriverVersion() + "]");
        System.out.println("-------------------------------------------\n\t{Runtime: [" + ((System.currentTimeMillis() - start) / 1000) + " seconds]}\n");
        return;
      }else if(sql.trim().toUpperCase().startsWith("OPEN")){
        int startNext = sql.trim().indexOf(" ",5);
        String newDBValue = null;
        String password = null;
// -- On the list...but not in the mood....
//        String username = null;
//        int pwordStart = sql.trim().indexOf("-pswd=",startNext);
//        int userStart = sql.trim().indexOf("-user=",startNext);
        if(startNext > -1){
          newDBValue = (sql.trim().substring(5,startNext)).trim();
          password = (sql.trim().substring(startNext, sql.indexOf(";") - 1)).trim();
        }else{
          newDBValue = (sql.trim().substring(5, sql.indexOf(";") - 1)).trim();
          password = tReader.getTagValue(newDBValue,"password");
        }

        if(newDBValue.length() > 0){
          openAliasDBConnection(newDBValue,password,false);
          }else{
            println("Connection alias not found [" + newDBValue + "]");
            println("\tCONNECTION -IS- CLOSED!!!");
          }

          System.out.println("-------------------------------------------\n\t{Runtime: [" + ((System.currentTimeMillis() - start) / 1000) + " seconds]}\n");
          return;
      }else if(sql.trim().toUpperCase().startsWith("DESC")){
        DatabaseMetaData dbmd = conn.getMetaData();
        String table_name = (sql.trim().substring(4,sql.indexOf(";") -1)).trim();
        println("Table [desc]ription for [" + table_name + "]");
        ResultSet tableDesc = dbmd.getColumns(null, null,table_name,null);
        try{
          DesciptionHandler dHandler = new DesciptionHandler();
          while(tableDesc.next()){
            dHandler.addNewColumn(tableDesc.getString("COLUMN_NAME"),
                                  tableDesc.getString("TYPE_NAME"),
                                  tableDesc.getString("COLUMN_SIZE"),
                                  tableDesc.getString("DECIMAL_DIGITS"),
                                  tableDesc.getString("NULLABLE"),
                                  tableDesc.getString("COLUMN_DEF"),
                                  tableDesc.getString("REMARKS"));
          }
          println(dHandler.getDescSpacer());
          println(dHandler.getDescHeader());
          println(dHandler.getDescSpacer());
          println(dHandler.fetchDescription());
          println(dHandler.getDescSpacer());
        }catch(Exception ex){
          println("TABLE_NAME [" + table_name + "] NOT FOUND");
        }
        tableDesc.close();
        return;
      }else if(sql.trim().toUpperCase().startsWith("HELP")){
        System.out.println("**************** HELP COMMANDS ****************");
        System.out.println("\thelp\t\t\tAvailable commands");
        System.out.println("\topen <alias> <password>\tOpen a new connection based on dbconfig entry. Password is optional");
        System.out.println("\tlist-alias\t\tShow the list of available aliases in the dbconfig file");
        System.out.println("\tlist-tables\t\tList database tables");
        System.out.println("\tlist-dbinfo\t\tList database information");
        System.out.println("\tlist-index <table>\tExplain the index plan for the supplied table");
        System.out.println("\t-silent\t\t\tSilent mode, no result sets returned");
        System.out.println("\t-verbose\t\tVerbose mode, result sets returned");

        System.out.println("\tload-ps\t\tload-ps <file>  Load an XML Prepared Statement File");
        System.out.println("\tinit-ps\t\tinit-ps <name> <prepared statement>  Create the prepared statement");
        System.out.println("\tload-ps\t\tlist-ps  list the alias names of all available prepared statements that are available");
        System.out.println("\texec-ps\t\texec-ps <alias> <args1>, <args2>, <args...n>  Execute the prepared Statement and use the given values");
        System.out.println("\tsave-ps\t\tsave-ps <output file>  Saves the prepared statements in memory -in xml format-");
        System.out.println("\t\tWhen executing a stored procedure each arg must begin with it's datatype:");
        System.out.println("\t\ti29\t\tInteger with the supplied value of 29");
        System.out.println("\t\td1259.82\tDouble with the supplied value of 1259.82");
        System.out.println("\t\tf12.3844\tFloat with the supplied value of 12.3844");
        System.out.println("\t\tsstrArg\t\tString with the supplied value of strArg");
        System.out.println("\t\tNote: ShellRaptor does not presently support an arg with a space in it");
//        System.out.println("\t\tsNew\\ Name\tString with the supplied value of \"New Name\"");

//&& !sql.trim().toUpperCase().startsWith("LOAD-PS")
//&& !sql.trim().toUpperCase().startsWith("INIT-PS")
//&& !sql.trim().toUpperCase().startsWith("LIST-PS")
//&& !sql.trim().toUpperCase().startsWith("EXEC-PS")){

        System.out.println("\tdesc <table>\t\tDescribe the table schema");
        System.out.println("\tselect ...\t\tbasic sql commands");
        System.out.println("\tdrop <table>\t\tDrop the given table");
        System.out.println("\tcreate <table...>\tCreate the given table");
        System.out.println("\t@<file>\t\t\tOpen and execute the listed file");
        System.out.println("\t#<file> <sql>\t\tsave sql results to...");
        System.out.println("\t#<file.out> @<file.in>\t[reversable] Read in and write to..");
        System.out.println("\tset maxrowcount <#>\tSet the max @ of return results (must be supported by your supplied driver)");
        System.out.println("\texit\t\t\tExit the Application");
        return;
      }else if(sql.toUpperCase().trim().substring(0,12).startsWith("SELECT")
               || pstateType == 0){
        queryType = 0;
        try {
          if(maxRowCount > 0 )
            stmt.setFetchSize(maxRowCount);
        } catch (Exception ex) {
          println("** Failed to set Max Row Count to [" + maxRowCount + "]");
        }

//println("SQL: " + createValidQuery(sql));
        if(pstateType == 0){
          rset = fetchStatement(aliasName).executeQuery();
        }else{
          rset = stmt.executeQuery(createValidQuery(sql));
        }
        IS_SELECT = true;
      }else if(sql.toUpperCase().trim().substring(0,12).startsWith("DELETE")
               || pstateType == 1){
        queryType = 1;
        if(pstateType == 1){
          results = fetchStatement(aliasName).executeUpdate();
        }else{
          results = stmt.executeUpdate(createValidQuery(sql));
        }
      }else if(sql.toUpperCase().substring(0,12).indexOf("CREATE") > -1){
        queryType = 2;
        try{
          stmt.execute(createValidQuery(sql));
          resultBoolean = true;
        }catch(Exception ex){
          println(ex.getMessage());
          resultBoolean = false;
        }
      }else if(sql.toUpperCase().trim().substring(0,12).indexOf("DROP") > -1){
        queryType = 3;
        try{
          stmt.execute(createValidQuery(sql));
          resultBoolean = true;
        }catch(Exception ex){
          println(ex.getMessage());
          resultBoolean = false;
        }
      }else if(sql.toUpperCase().trim().substring(0,12).indexOf("SET") > -1){
        if(sql.toUpperCase().indexOf("MAXROWCOUNT") > -1){
          println("[SET MAXROWCOUNT ?;] NOT YET IMPLEMENTED...ON THE LIST THOUGH");
        }else{
          println("INVALID SQL STATEMENT\n");
        }
        return;
      }else if(sql.toUpperCase().trim().substring(0,12).startsWith("INSERT")
          || pstateType == 2){
        queryType = 4;
        try{
          if(pstateType == 2){
            fetchStatement(aliasName).execute();
          }else{
            stmt.execute(createValidQuery(sql));
          }
          resultBoolean = true;
        }catch(Exception ex){
          println(ex.getMessage());
          resultBoolean = false;
        }
      }else if(sql.toUpperCase().trim().substring(0,12).startsWith("UPDATE")
          || pstateType == 3){
        queryType = 5;
        try{
          if(pstateType == 3){
            fetchStatement(aliasName).executeUpdate();
          }else{
            stmt.executeUpdate(createValidQuery(sql));
          }
          resultBoolean = true;
        }catch(Exception ex){
          println(ex.getMessage());
          resultBoolean = false;
        }
      }else{
        System.out.println("INVALID SQL STATEMENT\n");
        System.out.println("There is not a mapping for [" + sql.trim().toUpperCase().substring(0,12) + "...]\n");
        return;
      }

        dividor.setLength(0);
        if (queryType == 0) {
          resultData.setLength(0);
          rsmd = rset.getMetaData();
          int columnCount = rsmd.getColumnCount() + 1;
          rsHeader = new ResultSetHeader(columnCount);
          for (int cc = 1; cc < columnCount; cc++) {
            resultData.append(rsHeader.setColumnLength(cc, rsmd.getColumnDisplaySize(cc), rsmd.getColumnName(cc)));
          }
          dividor.append("+");
          breakPoint = 1;
          setBreak = 1;
          while(dividor.length() < resultData.length() + 1){
            breakPoint = resultData.indexOf("|",setBreak);
            if(setBreak > 0 && breakPoint == setBreak){
              dividor.append("+");
            }else{
              dividor.append("-");
            }
            setBreak++;
          }
          println("-------------------------------------------");
          println(dividor.toString() + "+");
          println(resultData.toString() + " |");
          println(dividor.toString() + "+");
          while (rset.next()) {
            for (int sc = 1; sc < columnCount; sc++) {
              print(rsHeader.getColumn(sc, rset.getString(sc)));
            }
            println(" |");
          }
          println(dividor.toString() + "+");
        }
        System.out.println("**" + QueryResultManager.returnResult(queryType, results, resultBoolean, start));
    }catch(Exception ex){
      didFail = true;
      println("**" + QueryResultManager.returnResult(queryType,ex.getMessage()));
  	logger.error("**FATAL-ERR**",ex);
    }finally{
      try {rset.close(); } catch (Exception ex) {}
      if(EXPORT_TO_FILE){
        try {outputStream.close(); } catch (Exception ex) {}
        EXPORT_TO_FILE = false;
      }
    }
  }

  private String findExportFile(String inSQL){
    int start = inSQL.indexOf("#");
    int stop = inSQL.indexOf(" ",start);
    if(stop < 0){
      exportFile.append(inSQL.substring(start + 1));
      return inSQL.substring(0,start);
    }else{
      exportFile.append(inSQL.substring(start + 1, stop));
      return inSQL.substring(0,start) + inSQL.substring(stop);
    }
  }

  public void println(String doOut) throws Exception{
//System.out.println("[" + !SILENT+ "][" + IS_SELECT + "]");
    if(SILENT){
      if (!IS_SELECT){
        System.out.println(doOut);
      }
    }else
      System.out.println(doOut);

    if(EXPORT_TO_FILE)
      outputStream.writeBytes(doOut + NEW_LINE);
  }
  private void print(String doOut) throws Exception{
    if(SILENT){
      if (!IS_SELECT){
        System.out.print(doOut);
      }
    }else
      System.out.print(doOut);

    if(EXPORT_TO_FILE)
      outputStream.writeBytes(doOut);
  }

  private String createValidQuery(String vsql){
    if(vsql.indexOf(";") > -1)
      return vsql.substring(0,(vsql.indexOf(";")));
    return vsql;
  }

  public void openAliasDBConnection(String newDBValue,
                                    String password,
                                    boolean silent) throws Exception{
    if(!silent)
      println("OPEN CONNECTION [" + newDBValue + "]");
      conMgr.closeConnection();
      try {
        if(!silent)
        println("Connectvity Info:\n"
            +"Driver\t\t[" + tReader.getTagValue(newDBValue,"driver") + "]\n"
            +"URL\t\t[" + tReader.getTagValue(newDBValue,"url") + "]\n"
            +"Username\t[" + tReader.getTagValue(newDBValue,"username") + "]\n"
            +"Password\t[********]\n"
            +"Max Rows\t[" + tReader.getTagValue(newDBValue,"maxrows") + "]\n");
        if(password == null)
          password = tReader.getTagValue(newDBValue,"password");

        conn = conMgr.getConnection(tReader.getTagValue(newDBValue,"driver"),
             tReader.getTagValue(newDBValue,"url"),
             tReader.getTagValue(newDBValue,"username"),
             password);
        stmt = conn.createStatement();
        if(!silent)
        println("[" + newDBValue + "] Connection status [" + (!conn.isClosed()?"Open":"Closed") + "]");
        maxRowCount = Integer.parseInt(tReader.getTagValue(newDBValue,"maxrows"));
      } catch (Exception ex) {
        println("Connection Error Message: " + ex.getMessage());
        println("Connection alias not found [" + newDBValue + "]");
        println("\tCONNECTION -IS- CLOSED!!!");
    	logger.error("**FATAL-ERR**",ex);
      }

  }

  public void executeCommandLineSQL(String clSQL, String clDelimeter, String outputFile, boolean withHeader) throws Exception{
    start = System.currentTimeMillis();
    CommandLineOutput clOutput = new CommandLineOutput(outputFile);
    try{
      rset = stmt.executeQuery(clSQL);

      resultData.setLength(0);
      rsmd = rset.getMetaData();
      int columnCount = rsmd.getColumnCount() + 1;
      rsHeader = new ResultSetHeader(columnCount);
      if(withHeader){
        for (int cc = 1; cc < columnCount; cc++) {
          if (cc > 1)
            clOutput.write(clDelimeter + rsmd.getColumnName(cc).trim());
          else
            clOutput.write(rsmd.getColumnName(cc).trim());
        }
        clOutput.write(NEW_LINE);
      }
      while (rset.next()) {
        for (int sc = 1; sc < columnCount; sc++) {
          if (sc > 1)
            clOutput.write(clDelimeter + validValue(rset.getString(sc)));
          else
            clOutput.write(validValue(rset.getString(sc)));
        }
        clOutput.write(NEW_LINE);
      }
      clOutput.flush();
    } finally {
      clOutput.close();
    }
  }

  private String validValue(String in){
    if(in == null)
      return "null";
    else
      return in.trim();
  }

  public Connection getOpenConnection(){ return conn; }

  public void setMaxRowCount(int count){ maxRowCount = count;}
  public int getMaxRowCount(){return maxRowCount;}

}
