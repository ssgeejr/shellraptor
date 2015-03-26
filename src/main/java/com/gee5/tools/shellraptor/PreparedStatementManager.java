package com.gee5.tools.shellraptor;

import java.util.*;
import java.sql.*;
import java.io.*;

public abstract class PreparedStatementManager extends Hashtable{
  private final String NEWLINE = System.getProperties().getProperty("line.separator");
  private TagReader treader = null;
  private Hashtable argTable = new Hashtable();
  private PreparedStmtArguement psa = null;
  private int argCounter = 0;
  private int psType = -1;

  public void reset(){
    argCounter = 0;
    psType = -1;
    argTable.clear();
    this.clear();
  }

  public void listStatements() throws Exception{
    for(Enumeration enm = this.keys();enm.hasMoreElements();){
      String key = (String)enm.nextElement();
      println("Alias [" + key + "]");
      println("\t" + this.get(key.trim().toUpperCase()));
      println("---------------------------");
    }
  }

  public void savePreStatements(String fileName) throws Exception{
    if(!fileName.toLowerCase().endsWith(".xml")) fileName += ".xml";
    File out = new File(fileName);
    if(out.exists()) out.delete();
    DataOutputStream output = null;
    try{
      output = new DataOutputStream(new FileOutputStream(out));
      output.writeBytes("<raptor>" + NEWLINE);
      for (Enumeration enm = this.keys(); enm.hasMoreElements(); ) {
        String key = (String) enm.nextElement();
        output.writeBytes("\t<" + key + ">" + NEWLINE);
        output.writeBytes("\t\t<query>" + this.get(key.trim().toUpperCase()) + "</query>");
        output.writeBytes(NEWLINE + "\t</" + key + ">" + NEWLINE);
      }
      output.writeBytes("</raptor>" + NEWLINE);
      output.flush();
      System.out.println("Prepared Statements [" + this.size() + " total] successfully saved to <" + fileName + ">");
    }finally{
      try {output.close();} catch (Exception ex) {}
    }
  }

  public void createPreparedStatement(String alias, String query) throws Exception{
    this.put(alias.trim().toUpperCase(),query);
    println("New prepared statement Alias created [" + alias + "]");
  }

  public void loadExternalFile(String file) throws Exception{
    reset();
    treader = new TagReader(file);
    Hashtable ht = treader.getNodes();
    for(Enumeration enm = ht.keys();enm.hasMoreElements();){
      String key = (String)enm.nextElement();
      println("Loading prepared statement alias [" + key + "]");
      this.put(key.trim().toUpperCase(),treader.getTagValue(key,"query"));
    }
  }

  public String getStatement(String name){
    return (String)this.get(name.trim().toUpperCase());
  }

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
  public void resetArgs(){
    argCounter = 0;
    argTable.clear();
    psType = -1;
  }

  public void setArguement(String val) throws Exception{
    System.out.println("ARG[" + argCounter + "] <" + val + ">");
    argTable.put((argCounter + ""),new PreparedStmtArguement(val));
    argCounter++;
  }

  public int getPSType(String alias){
    String qry = (String)this.get(alias.trim().toUpperCase());
    if(qry.trim().toUpperCase().startsWith("SELECT")){
      psType = 0;
    }else if(qry.trim().toUpperCase().startsWith("DELETE")){
      psType = 1;
    }else if(qry.trim().toUpperCase().startsWith("INSERT")){
      psType = 2;
    }else if(qry.trim().toUpperCase().startsWith("UPDATE")){
      psType = 3;
    }
    return psType;
  }

  public PreparedStatement fetchStatement(String alias) throws Exception{
    PreparedStatement pstmt = getOpenConnection().prepareStatement((String)this.get(alias.trim().toUpperCase()));
    for(int loop = 0;loop < argTable.size();loop++){
      psa = (PreparedStmtArguement)argTable.get(loop + "");
      if(psa.getType() == psa.INTEGER){
        pstmt.setInt((loop + 1),psa.getIValue());
        println("\tPSA-TYPE[" + loop + "] <" + psa.ARG_TYPE[psa.getType()] + "> <" + psa.getIValue() + ">");
      }else if(psa.getType() == psa.FLOAT){
        pstmt.setFloat((loop + 1),psa.getFValue());
        println("\tPSA-TYPE[" + loop + "] <" + psa.ARG_TYPE[psa.getType()] + "> <" + psa.getFValue() + ">");
      }else if(psa.getType() == psa.DOUBLE){
        pstmt.setDouble((loop + 1),psa.getDValue());
        println("\tPSA-TYPE[" + loop + "] <" + psa.ARG_TYPE[psa.getType()] + "> <" + psa.getDValue() + ">");
      }else if(psa.getType() == psa.STRING){
        pstmt.setString((loop + 1),psa.getSValue());
        println("\tPSA-TYPE[" + loop + "] <" + psa.ARG_TYPE[psa.getType()] + "> <" + psa.getSValue() + ">");
      }
    }
    return pstmt;
  }

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
  abstract public void println(String doOut) throws Exception;
  abstract public Connection getOpenConnection();
}
