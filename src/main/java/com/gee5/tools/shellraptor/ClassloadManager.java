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
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

public class ClassloadManager{
  private URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
  private Class sysclass = URLClassLoader.class;
  private static final Class[] parameters = new Class[] {URL.class};
  private final String extDir = "ext";

  protected ClassloadManager(boolean verbose) {
    File workingDirectory = new File(extDir);
    if (workingDirectory.isDirectory()) {
        File[] fileArray = workingDirectory.listFiles();
        for (int i = 0; i < fileArray.length; i++) {
            File item = (File) fileArray[i];
            if (item.isFile()
                && item.getAbsolutePath().toUpperCase().endsWith(".JAR")
                || item.getAbsolutePath().toUpperCase().endsWith(".ZIP")) {
              if(verbose)
                System.out.println("ADDING LIBRARY [" + item.getAbsoluteFile() + "]");
              try {
                  Method method = sysclass.getDeclaredMethod("addURL", parameters);
                  method.setAccessible(true);
                  method.invoke(sysloader, new Object[] {item.toURL()});
              } catch (Throwable t) {
                System.err.println("Error, could not add Jar File [" + item.getAbsoluteFile() + "] to the ClassLoader");
                  t.printStackTrace();
              }
            }
        }
    }
  }


}
