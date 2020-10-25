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

public class ConnectionManager {
  private Connection primaryConn;
  public ConnectionManager(){}

  public Connection getConnection(String driver, String url,
                                  String username, String password) throws NullPointerException, Exception{
    if(primaryConn == null  ||  primaryConn.isClosed()){
      Class.forName(driver);
      primaryConn = DriverManager.getConnection(url,username,password);
    }
    return primaryConn;
  }//end getConnection

  public void closeConnection(){
    try{
      if(primaryConn != null  ||  !primaryConn.isClosed()){ primaryConn.close();}
    }catch(Exception ex){}
  }//end closeConnection

}
