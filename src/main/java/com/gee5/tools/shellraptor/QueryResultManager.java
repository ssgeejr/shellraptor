package com.gee5.tools.shellraptor;

public class QueryResultManager {
  public QueryResultManager() {}
  private static StringBuffer out = new StringBuffer();
  private static String[] message = {"Select","Create","Delete","Drop"};
  public static String returnResult(int type, String errorMsg){
    out.setLength(0);
    out.append(message[type] + " failed:\n" + errorMsg);
    return out.toString();
  }

  public static String returnResult(int type, int results, boolean success, long start){
    out.setLength(0);
    if(type == 0){
      out.append("Select executed succesfully");
    }else if(type == 1){
      out.append(results + " records deleted");
    }else if(type == 2){
      if(success)
        out.append("Table succesfully created");
      else
        out.append("Table creation failed");
    }else if(type == 3){
      if(success)
        out.append("Table succesfully dropped");
      else
        out.append("Table drop failed");
    }else if(type == 4){
      if(success)
        out.append("Insert successful");
      else
        out.append("Insert failed");
    }else if(type == 5){
      if(success)
        out.append("Update successful");
      else
        out.append("Update failed");
    }
    out.append("\n-------------------------------------------\n\t{Runtime: [" + ((System.currentTimeMillis() - start) / 1000) + " seconds]}\n");
    return out.toString();
  }

}
