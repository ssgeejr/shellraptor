package com.gee5.tools.shellraptor;

public class Column {
  private String columnName = "";
  private int columnLen = 0;
  private int decimalLen = 0;
  private String remarks = "";
  private String defaultValue = "";
  private String columnType = "";
  private int columnNullable = 0;

  
  public Column(){}
  
  /**
   * 
   * @param cname Column Name
   * @param cType Column Type
   * @param cLen Column Length
   * @param dLen Decimal Length
   * @param cNull Nullable
   * @param defVal Default Value
   * @param rmk Rarks
   */
  public Column(String cname,
                String cType,
                String cLen,
                String dLen,
                String cNull,
                String defVal,
                String rmk){
    this.setColumnName(cname);
    this.setColumnType(cType);
    this.setColumnLen(cLen);
    this.setDecimalLen(dLen);
    this.setColumnNullable(cNull);
    this.setDefaultValue(defVal);
    this.setRemarks(rmk);
  }

  public String getColumnName() {
    return this.columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = noNulls(columnName);
  }

  public void setColumnLen(String ScolumnLen) {
    try {
      this.columnLen = Integer.parseInt(noNulls(ScolumnLen));
    } catch (Exception ex) {
      this.columnLen = 0;
    }
  }

  public void setDecimalLen(String SdecimalLen) {
    try {
      this.decimalLen = Integer.parseInt(noNulls(SdecimalLen));
    } catch (Exception ex) {
      this.decimalLen = 0;
    }
  }

  public String getRemarks() {
    return this.remarks;
  }

  public void setRemarks(String remarks) {
   this.remarks = noNulls(remarks);
  }

  public String getColumnType() {
    return this.columnType + "(" + columnLen + "," + decimalLen + ")";
  }

  public void setColumnType(String columnType) {
    this.columnType = noNulls(columnType);
  }

  public String getColumnNullable() {
    return (columnNullable == 0)?"N":"Y";
  }

  public void setColumnNullable(String ScolumnNullable) {
    try {
     this.columnNullable = Integer.parseInt(noNulls(ScolumnNullable));
   } catch (Exception ex) {
     this.columnNullable = 0;
   }
  }

  private String noNulls(String in){
    if(in == null)
      return "";
    else
      return in;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

}
