package com.gee5.tools.shellraptor;

public class PreparedStmtArguement {
  public final static String[] ARG_TYPE = {"INTEGER","FLOAT","DOUBLE","STRING"};
  public final static int INTEGER = 0;
  public final static int FLOAT = 1;
  public final static int DOUBLE = 2;
  public final static int STRING = 3;

  private int 	 type = 0;
  private int 	 ivalue = 0;
  private float  fvalue = 0;
  private double dvalue = 0;
  private String svalue = "";

  public int getType(){return type;}
  public int getIValue(){return ivalue;}
  public float getFValue(){return fvalue;}
  public double getDValue(){return dvalue;}
  public String getSValue(){return svalue;}

  public PreparedStmtArguement(String arg) throws Exception{
    addItem(arg);
  }

  private void addItem(String arg) throws Exception{
    if(arg.toUpperCase().startsWith("I")){
      type = INTEGER;
      ivalue = Integer.parseInt(arg.substring(1));
    }else if(arg.toUpperCase().startsWith("F")){
      type = FLOAT;
      fvalue = Float.parseFloat(arg.substring(1));
    }else if(arg.toUpperCase().startsWith("D")){
      type = DOUBLE;
      dvalue = Double.parseDouble(arg.substring(1));
    }else if(arg.toUpperCase().startsWith("S")){
      type = STRING;
      svalue = arg.substring(1);
    }
  }

}
