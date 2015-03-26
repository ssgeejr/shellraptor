package com.gee5.tools.shellraptor;

import java.sql.*;

public class RaptorTesting {
  ConnectionManager conMgr = null;
  private final String[] TABLE_TYPES = {"TABLE"};
  public RaptorTesting(){
    try {
      System.out.println(findExportFile("@goDoIt/sql  #saveMeHere;").trim());
      System.out.println("-------------------------------------------------");
      System.out.println(findExportFile("#saveMeHere select * from XXX where blah blah blah  ;").trim());
      System.exit(-1);



      TagReader tReader = new TagReader("conf/dbconf.xml");
      conMgr = new ConnectionManager();
      Connection conn = conMgr.getConnection(tReader.getTagValue("vcm-test","driver"),
                                  tReader.getTagValue("vcm-test","url"),
                                  tReader.getTagValue("vcm-test","username"),
                                  tReader.getTagValue("vcm-test","password"));
      Statement stmt = conn.createStatement();
      DatabaseMetaData dbmd = conn.getMetaData();
//      ResultSet tset = dbmd.getColumns(null, null, "ATBAT",null );
      ResultSet tset = dbmd.getColumns(null, null, "ATMGROUP" ,null );

//      ResultSet tset = dbmd.getColumns(null, null, "columntest",null );
//      ResultSet tset = dbmd.getColumns(null, null, "pktab",null );

      DesciptionHandler dHandler = new DesciptionHandler();
      while(tset.next()){
        dHandler.addNewColumn(tset.getString("COLUMN_NAME"),
                              tset.getString("TYPE_NAME"),
                              tset.getString("COLUMN_SIZE"),
                              tset.getString("DECIMAL_DIGITS"),
                              tset.getString("NULLABLE"),
                              tset.getString("COLUMN_DEF"),
                              tset.getString("REMARKS"));
//System.out.println("\tCOLUMN_NAME\t" + tset.getString("COLUMN_NAME")
//+ "\tCOLUMN_SIZE\t" + tset.getString("COLUMN_SIZE")
//+ "\tDECIMAL_DIGITS\t" + tset.getString("DECIMAL_DIGITS")
//+ "\tREMARKS\t" + tset.getString("REMARKS")
//+ "\tSQL_DATA_TYPE\t" + tset.getString("SQL_DATA_TYPE")
//+ "\tNULLABLE\t" + tset.getString("NULLABLE")
//+ "\tTYPE_NAME\t" + tset.getString("TYPE_NAME")
//+ "\tCOLUMN_DEF\t" + tset.getString("COLUMN_DEF"));
      }
      System.out.println(dHandler.getDescSpacer());
      System.out.println(dHandler.getDescHeader());
      System.out.println(dHandler.getDescSpacer());
      System.out.println(dHandler.fetchDescription());
      System.out.println(dHandler.getDescSpacer());
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      try {conMgr.closeConnection(); } catch (Exception ex) {}
    }
  }

StringBuffer  exportFile = new StringBuffer();

  private String findExportFile(String inSQL){
    int start = inSQL.indexOf("#");
    int stop = inSQL.indexOf(" ",start);
    System.out.println("START [" + start + "]");
    System.out.println("STOP [" + stop + "]");
    if(stop < 0){
      exportFile.append(inSQL.substring(start + 1));
      return inSQL.substring(0,start);
    }else{
      exportFile.append(inSQL.substring(start + 1, stop));
      return inSQL.substring(0,start) + inSQL.substring(stop);
    }

  }

  public static void main(String args[]){new RaptorTesting();}
}
