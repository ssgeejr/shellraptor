package com.gee5.tools.shellraptor;

import java.util.*;

public class DesciptionHandler {
  private final String COLUMN_HEADER = "Column Name            ";
  private final String COLUMN_TYPE = "Data Type        ";
  private final String COLUMN_NULLABLE = "Null";
  private final String COLUMN_DEFAULT = "Default";
  private final String COLUMN_REMARKS = "Remarks";
  private int maxTableLen = COLUMN_HEADER.length();
  private int maxRemarkLen = COLUMN_REMARKS.length();
  private int maxTypeLen = COLUMN_TYPE.length();
  private int nullLen = COLUMN_NULLABLE.length();
  private int defaultLen = COLUMN_DEFAULT.length();
  private ArrayList list = new ArrayList();
  private StringBuffer descHeader = new StringBuffer();
  private StringBuffer descSpacer = new StringBuffer();
  private StringBuffer spacer = new StringBuffer();

  public void addNewColumn(String columnName,
                           String columnType,
                           String columnLen,
                           String decimalLen,
                           String nullable,
                           String defvalue,
                           String remarks){

    if(columnName.length() > maxTableLen)
      maxTableLen = columnName.length();

    if(noNulls(remarks).length() > maxRemarkLen){
      if(remarks.length() > 25){
        maxRemarkLen = 25;
        remarks = remarks.substring(0,25);
      }else{
        maxRemarkLen = remarks.length();
      }
    }

    if(columnType.length() > maxTypeLen)
      maxTypeLen = columnType.length();

    list.add(new Column(columnName,columnType,columnLen,decimalLen,nullable,defvalue,remarks));

  }

  private void setSpacers(){
    descHeader.setLength(0);
    descSpacer.setLength(0);
    descHeader.append(space(COLUMN_HEADER,maxTableLen));
    descHeader.append(space(COLUMN_TYPE,maxTypeLen));
    descHeader.append(space(COLUMN_DEFAULT,defaultLen));
    descHeader.append(space(COLUMN_NULLABLE,nullLen));
    descHeader.append(space(COLUMN_REMARKS,maxRemarkLen));
    descHeader.append(" |");
    buildSpacer();
  }

  private String space(String inc, int len){
    len += 3;
    spacer.setLength(0);
    spacer.append("| ").append(inc);
    while(spacer.length() < len){
      spacer.append(" ");
    }
    return spacer.toString();
  }

  private void buildSpacer(){
    descSpacer.setLength(0);
    descSpacer.append("+");
    int breakPoint = 1;
    int setBreak = 1;
    while(descSpacer.length() < descHeader.length()){
      breakPoint = descHeader.indexOf("|",setBreak);
      if(setBreak > 0 && breakPoint == setBreak){
        descSpacer.append("+");
      }else{
        descSpacer.append("-");
      }
      setBreak++;
    }
  }

  private Column column = null;
  StringBuffer desc = new StringBuffer();
  public String fetchDescription(){
    desc.setLength(0);
    Iterator itr = list.iterator();
    int len = list.size();
    int workingLocation = 0;
    while(itr.hasNext()){
      column = (Column)itr.next();
      desc.append(space(column.getColumnName(),maxTableLen));
      desc.append(space(column.getColumnType(),maxTypeLen));
      desc.append(space(column.getDefaultValue(),defaultLen));
      desc.append(space(column.getColumnNullable(),nullLen));
      desc.append(space(column.getRemarks(),maxRemarkLen));
      desc.append(" |");
      workingLocation++;
      if(workingLocation < len)
        desc.append("\n");
    }

    return desc.toString();
  }


  public String getDescHeader(){
    if(descHeader.length() < 1)
      setSpacers();
    return descHeader.toString();
  }

  public String getDescSpacer(){
    if(descHeader.length() < 1)
      setSpacers();
    return descSpacer.toString();
  }

  private String noNulls(String in){
    if(in == null)
      return "";
    else
      return in;
  }

}
