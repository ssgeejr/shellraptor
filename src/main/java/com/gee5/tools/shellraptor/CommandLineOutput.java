package com.gee5.tools.shellraptor;

import java.io.*;

public class CommandLineOutput {
  private final String NEW_LINE = System.getProperties().getProperty("line.separator");
  private DataOutputStream outputStream = null;
  private boolean stdout = false;
  public CommandLineOutput(String outputLocation) throws Exception{
    if(outputLocation.equalsIgnoreCase("stdout")){
      stdout = true;
    }else{
      outputStream = new DataOutputStream(new FileOutputStream(outputLocation));
    }
  }

  public void write(String line) throws Exception{
    if(stdout)
      System.out.print(line);
    else
      outputStream.writeBytes(line);
  }
  public void flush() throws Exception{
    if(!stdout) outputStream.flush();
  }
  public void close(){
    try {outputStream.close(); } catch (Exception ex) {}
  }
}
