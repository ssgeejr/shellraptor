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

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;

public class TagReader implements Serializable{
  private ArrayList subnodes = null;
  private Hashtable rootNode = null;
  private Hashtable childNodes = null;
  private String rootTag = "raptor";
  private DocumentBuilderFactory factory;
  public TagReader() throws Exception{}

  public TagReader(InputStream istream) throws Exception{construct(istream);}

  public TagReader(String filename) throws Exception{construct(new FileInputStream(filename));}

  public TagReader(InputStream istream, String _rootTag) throws Exception{
    rootTag = _rootTag;
    construct(istream);
  }

  public TagReader(String filename, String _rootTag) throws Exception{
    rootTag = _rootTag;
    construct(new FileInputStream(filename));
  }

  public void setRootTag(String _rootTag){ rootTag = _rootTag;}

  public void construct(String filename) throws Exception{construct(new FileInputStream(filename));}

  public void setFile(String filename) throws Exception{construct(filename);}

  public void construct(InputStream fileInputStream) throws Exception {
    factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docbuilder = factory.newDocumentBuilder();
    factory.setValidating(false);
    parse(docbuilder.parse(fileInputStream));
  }

  public void parse(String externalInput) throws Exception {
    factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    parse(factory.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(externalInput.getBytes()))));
  }

  public void parse(Document doc) throws Exception{
//    org.apache.crimson.parser.Parser2 p2 = null;

    NodeList nodeLst = doc.getElementsByTagName(rootTag);
    Node rcpNode = null;
    NodeList nl = null;
    Node n;
    int cntr = 0;
    if (nodeLst.getLength() == 0)
      throw new Exception("No tags provided");
    NodeList nodelist = null;
    Node noed = null;
    rootNode = new Hashtable();
//    Node inode = null;
    subnodes = new ArrayList();
    while (cntr < nodeLst.getLength()) {
      rcpNode = nodeLst.item(cntr);
      nl = rcpNode.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        n = nl.item(i);
        if (n.getNodeType() == 1) {
          subnodes.add(n.getNodeName());
          nodelist = n.getChildNodes();
          childNodes = new Hashtable();
          int size = nodelist.getLength();
          noed = n.getLastChild();
          int iloop = 0;
          while (iloop < size) {
            noed = nodelist.item(iloop);
            if (noed.getNodeType() == 1) {
              try{
                childNodes.put(noed.getNodeName(), noed.getLastChild().getNodeValue());
              }catch(NullPointerException npe){
                childNodes.put(noed.getNodeName(), "");
              }//end try-C
            }// end note Type == 1
            iloop++;
          }//end while
          rootNode.put(n.getNodeName(), childNodes);
        } //end if
      } //end for
      cntr++;
    } //end while
  } //end parse(String msg)

  public Iterator getSubNodes() {
    return subnodes.iterator();
  }

  public Hashtable getNodes() {
    return rootNode;
  }

  public Hashtable getTags(String s1) throws Exception {
    return (Hashtable) rootNode.get(s1);
  }

  public String getTagValue(String s1, String s2) throws Exception {
    return (String) ( (Hashtable) rootNode.get(s1)).get(s2);
  }

  public String getRootTag() {
    return this.rootTag;
  }

  public String readConfigFile(String filename) throws Exception {
    FileInputStream fileInputStream = null;
    try {
      StringBuffer data = new StringBuffer();
      fileInputStream = new FileInputStream(filename);
      int availble = fileInputStream.available();
      byte bites[] = new byte[availble];
      fileInputStream.read(bites);
      data.append(new String(bites));
      return data.toString();
    } catch (Exception e) {
      throw e;
    } finally {
      try {fileInputStream.close(); } catch (Exception e) {
      }
    }
  }

}
