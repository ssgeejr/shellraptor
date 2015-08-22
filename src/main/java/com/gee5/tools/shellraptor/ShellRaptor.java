/*************************************************************************
 Copyright (C) 2005  Steve Gee
 ioexcept@gmail.com
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*************************************************************************/
package com.gee5.tools.shellraptor;

import java.sql.*;
import java.io.*;

import org.apache.log4j.Logger;

public class ShellRaptor extends SQLEditor{
 static Logger logger = Logger.getLogger("ShellRaptor");
	
  private boolean silent = true;
  private boolean verbose = false;
  private TagReader stdoutReader = null;
  private final String CONFIG_FILE = "conf/shellraptor.xml";
  private final String USER_CONFIG = System.getProperty("user.home") + "/.shellraptor";
  private String driver = null;
  private String url = null;
  private String username = null;
  private String password = null;
  private int maxRows = -1;
  private String fileOutput = null;
  private String configFile = null;
  private boolean testOnly = false;

  public ShellRaptor(String args[]){
	new ClassloadManager(verbose);
    try {
      if(args.length > 0){
        try{
          for (int loop = 0; loop < args.length; loop++) {
            if(args[loop].startsWith("-verbose")){
              verbose = true;
            }else if (args[loop].startsWith("-s")) {
              loop++;
              fileOutput = args[loop];
            }else if (args[loop].startsWith("-mx")) {
              loop++;
              maxRows = Integer.parseInt(args[loop]);
            }else if (args[loop].startsWith("-c")) {
              loop++;
              configFile = args[loop];
            }else if (args[loop].startsWith("-d")) {
              loop++;
              driver = args[loop];
            }else if (args[loop].startsWith("-u")) {
              loop++;
              url = args[loop];
            }else if (args[loop].startsWith("-l")) {
              loop++;
              username = args[loop];
            }else if (args[loop].startsWith("-p")) {
              loop++;
              password = args[loop];
            }else if (args[loop].startsWith("-p")) {
              testOnly = true;
            }else if (args[loop].startsWith("-h")) {
              showHelp();
              System.exit(0);
            }else if (args[loop].startsWith("--stdout")) {
//              System.out.println("***** EXECUTING COMMAND STDOUT OUT MODE *****");
              int indx = args[loop].indexOf("=");
              File cnfg = null;
              String userFile = "./conf/stdout.xml";
              if(indx > 0){
                userFile = args[loop].substring(indx + 1);
                cnfg = new File(userFile);
//                System.out.println("\tUsing default config file [" + userFile + "]");
                logger.info("Using user defined config file [" + userFile + "]");
              }else{
//                System.out.println("\tUsing default config file [./conf/stdout.xml]");
            	  logger.info("Using default config file [./conf/stdout.xml]");
                cnfg = new File("conf/stdout.xml");
              }
              try{
                //----- FILE ERROR HANDELING ------//
                if (cnfg.exists()) {
                  stdoutReader = new TagReader(userFile);
                } else {
                  logger.info("Unable to find config file [" + userFile + "]");
                }
                break;
              }catch(Exception ex){
                logger.error("**FATAL-ERR**",ex);
              }
            }else{
              showHelp();
            }
          }
        }catch(Exception ex){
          showHelp();
        }
      }//end if args > 0

     
      if(verbose)
        System.out.println("{**** " + new java.util.Date().toString() + " ****}");

        if (configFile != null) {
          tReader = new TagReader(configFile);
        } else {
        	File f = new File(USER_CONFIG);	
        	if(f.exists()){
//                System.out.println("File exists");
                tReader = new TagReader(USER_CONFIG);
            }else{
//                System.out.println("File not found!");
                tReader = new TagReader(CONFIG_FILE);
            }
        }

        if (driver == null)
          driver = tReader.getTagValue("default", "driver");
        if (url == null)
          url = tReader.getTagValue("default", "url");
        if (username == null)
          username = tReader.getTagValue("default", "username");
        if (password == null)
          password = tReader.getTagValue("default", "password");
        if (maxRows == -1)
          maxRows = Integer.parseInt(tReader.getTagValue("default", "maxrows"));

      if(stdoutReader == null){
    	  String version = new ManifestVersionReader().fetchShellRaptorManifestVersion();
        System.out.println("ShellRaptor Version: " + version + "\n"
        				   + "Connectvity Info:\n"
                           + "Driver\t\t[" + driver + "]\n"
                           + "URL\t\t[" + url + "]\n"
                           + "Username\t[" + username + "]\n"
                           + "Password\t[********]\n"
                           + "Test Only\t[" + testOnly + "]\n"
                           + "Max Rows\t[" + maxRows + "]\n");
        if (fileOutput != null)
          System.out.println("Save Output\t\t[" + fileOutput + "]");
        conn = conMgr.getConnection(driver, url, username, password);
        if (testOnly) {
          System.out.println(" **** CONNNECTION STATUS WAS SUCCESSFUL [" + !conn.isClosed() + "] **** ");
        } else {
          System.out.println("READY FOR NEXT PHASE");
          startEditor();
        }
      }else{
//***********************************************************************************//
//***********************************************************************************//
        silent = new Boolean(stdoutReader.getTagValue("stdout", "silent")).booleanValue();
        boolean withHeader = new Boolean(stdoutReader.getTagValue("stdout", "with-column-names")).booleanValue();
//System.out.println("SILENT [" + silent + "]");
        stdout("Loading config file....");
        stdout("opening alias [" + stdoutReader.getTagValue("stdout", "alias") + "]");
        String pword = null;
        try{
          pword = stdoutReader.getTagValue("stdout","password");
          if(pword.trim().length() == 0)
            pword = null;
          else
            stdout("using defined password [************]");
        }catch(Exception ex){}
        if(pword == null) stdout("using alias defined password");
        openAliasDBConnection(stdoutReader.getTagValue("stdout", "alias"),pword,silent);
        stdout("piping output to [" + stdoutReader.getTagValue("stdout","outputfile") + "]");
        stdout("Using Delimeter [" + stdoutReader.getTagValue("stdout","delimiter") + "]");
        stdout("executing sql ...");
        stdout(stdoutReader.getTagValue("stdout","sql"));
        stdout("________________________________________");
        executeCommandLineSQL(stdoutReader.getTagValue("stdout","sql"),
                              stdoutReader.getTagValue("stdout","delimiter"),
                              stdoutReader.getTagValue("stdout","outputfile"),
                              withHeader);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }finally{
      try {conn.close(); } catch (Exception ex) {}
    }
  }

  public void exit(){
    try {conn.close(); } catch (Exception ex) {}
    System.exit(0);
  }

  private void showHelp(){
    System.out.println("ShellRaptor (2005): Command Line SQL Utility\n"
                       +"Base Arguements\n"
                       +"\t -s\t\t<saveOutputTo_File>\n"
                       +"\t -mx\t\t<maximumRowCountResults>\n"
                       +"\t -test\t\t(-only- test connectivity)\n"
                       +"\t -h\t\t(this help screen)\n"
                       +"\t --stdout\tUse a config file (default is conf/stdout.xml) to execute command from the command line to stdout\n"
                       +"\t --stdout=<input.File>\tProvide a config file to execute"
                       +"Connection Arguements\n"
                       +"1) no arguements uses default config file conf\\dbconf.xml\n"
                       +"2) External Config File\n\t-c\t<config file>\n"
                       +"3) Command Line Values\n\t-d\t<driver>\n"
                       +"\t-u\t<connection-url>\n"
                       +"\t-l\t<username>\n"
                       +"\t-p\t<password\n"
        +" *** COMMENTS ***\n"
        +" It is possible to use a config and override the values\n"
        +" of that file by supplying driver, url..etc values.\n"
        +" For example;\n java -jar ShellRaptor.jar -c myconf.xml -d newest.Version.ofSomeDriver.jar");
  }

  public static void main(String args[]){
    new ShellRaptor(args);
  }

  private void stdout(String out){
    if(!silent)
      System.out.println(out);
  }

  
  

  
  
  
  
}
