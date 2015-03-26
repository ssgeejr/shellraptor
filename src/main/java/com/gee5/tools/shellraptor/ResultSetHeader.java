package com.gee5.tools.shellraptor;

public class ResultSetHeader {

  private final int columnSizeArray[];
  private StringBuffer result = new StringBuffer();

  public ResultSetHeader(int size){
    columnSizeArray = new int[size];
  }

  public void setLen(int arrayID, int columnSize){
    columnSizeArray[arrayID] = columnSize;
  }

  public String getColumn(int rowid, String val){
    result.setLength(0);
    result.append(val);
    while(result.length() < columnSizeArray[rowid]){
      result.append(" ");
    }
    if (rowid == 1)
      result.insert(0,"| ");
    else
      result.insert(0," | ");
    return result.toString();
  }

  public String setColumnLength(int rowid, int len, String val){
    result.setLength(0);
    result.append(val);
    if(val.trim().length() > len){
      columnSizeArray[rowid] = val.length();
    }else{
      columnSizeArray[rowid] = len;
    }

    while (result.length() < len) {
      result.append(" ");
    }

    if (rowid == 1)
      result.insert(0,"| ");
    else
      result.insert(0," | ");
    return result.toString();
  }



}
