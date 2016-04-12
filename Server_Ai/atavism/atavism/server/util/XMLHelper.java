// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import org.w3c.dom.Document;
import java.io.File;
import java.util.Iterator;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Node;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

public class XMLHelper
{
    static final String[] typeName;
    
    public static DocumentBuilder makeDocBuilder() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void fatalError(final SAXParseException exception) throws SAXException {
                }
                
                @Override
                public void error(final SAXParseException e) throws SAXParseException {
                    throw e;
                }
                
                @Override
                public void warning(final SAXParseException err) throws SAXParseException {
                    System.out.println("** Warning, line " + err.getLineNumber() + ", uri " + err.getSystemId());
                    System.out.println("   " + err.getMessage());
                }
            });
        }
        catch (ParserConfigurationException pce) {
            Log.exception("XMLHelper.makeDocBuilder caught ParserConfigurationException", pce);
        }
        return builder;
    }
    
    public static String getNodeValue(final Node node) {
        return node.getFirstChild().getNodeValue();
    }
    
    public static List<Node> getMatchingChildren(final Node node, final String name) {
        if (node == null) {
            return null;
        }
        final LinkedList<Node> returnList = new LinkedList<Node>();
        final NodeList childList = node.getChildNodes();
        for (int len = childList.getLength(), i = 0; i < len; ++i) {
            final Node curNode = childList.item(i);
            if (name.equals(curNode.getNodeName())) {
                returnList.add(curNode);
            }
        }
        return returnList;
    }
    
    public static Node getMatchingChild(final Node node, final String name) {
        if (node == null) {
            return null;
        }
        final NodeList childList = node.getChildNodes();
        for (int len = childList.getLength(), i = 0; i < len; ++i) {
            final Node curNode = childList.item(i);
            if (name.equals(curNode.getNodeName())) {
                return curNode;
            }
        }
        return null;
    }
    
    public static String getMatchingChildValue(final Node node, final String name) {
        final Node childNode = getMatchingChild(node, name);
        return getNodeValue(childNode);
    }
    
    public static String getAttribute(final Node node, final String attrName) {
        final NamedNodeMap attrMap = node.getAttributes();
        if (attrMap == null) {
            Log.debug("getAttribute: attr map is null");
            return null;
        }
        final Node attrNode = attrMap.getNamedItem(attrName);
        if (attrNode == null) {
            Log.debug("getAttribute: attr node is null");
            return null;
        }
        return getNodeValue(attrNode);
    }
    
    public static String toXML(final Node node) {
        String xml = "";
        final String name = node.getNodeName();
        xml = xml + "<" + name;
        final NamedNodeMap attrMap = node.getAttributes();
        if (attrMap != null) {
            for (int len = attrMap.getLength(), i = 0; i < len; ++i) {
                final Node attrNode = attrMap.item(i);
                final String attrName = attrNode.getNodeName();
                final String attrVal = getNodeValue(attrNode);
                xml = xml + " " + attrName + "=\"" + attrVal + "\"";
            }
        }
        xml += ">";
        final NodeList children = node.getChildNodes();
        if (children != null) {
            for (int len2 = children.getLength(), j = 0; j < len2; ++j) {
                final Node childNode = children.item(j);
                final short nodeType = childNode.getNodeType();
                if (nodeType == 3) {
                    xml += childNode.getNodeValue();
                }
                else if (nodeType == 1) {
                    xml += toXML(childNode);
                }
                else if (Log.loggingDebug) {
                    Log.debug("XMLHelper: unknown child node: , name=" + childNode.getNodeName() + ", type=" + XMLHelper.typeName[childNode.getNodeType()] + ", val=" + childNode.getNodeValue());
                }
            }
        }
        xml = xml + "</" + name + ">";
        return xml;
    }
    
    public static void printAllChildren(final Node node) {
        if (node == null) {
            return;
        }
        final NodeList childList = node.getChildNodes();
        for (int len = childList.getLength(), i = 0; i < len; ++i) {
            final Node curNode = childList.item(i);
            if (Log.loggingDebug) {
                Log.debug("XMLHelper.printAllChildren: childnode= " + nodeToString(curNode));
            }
        }
    }
    
    public static String nodeToString(final Node domNode) {
        if (domNode == null) {
            return "";
        }
        String s = XMLHelper.typeName[domNode.getNodeType()];
        final String nodeName = domNode.getNodeName();
        if (!nodeName.startsWith("#")) {
            s = s + ": " + nodeName;
        }
        if (domNode.getNodeValue() != null) {
            if (s.startsWith("ProcInstr")) {
                s += ", ";
            }
            else {
                s += ": ";
            }
            String t = domNode.getNodeValue().trim();
            final int x = t.indexOf("\n");
            if (x >= 0) {
                t = t.substring(0, x);
            }
            s += t;
        }
        return s;
    }
    
    public static Map<String, Serializable> nameValuePairsHelper(final Node node) {
        final Map<String, Serializable> resultMap = new HashMap<String, Serializable>();
        final List<Node> nameValueNodes = getMatchingChildren(node, "NameValuePair");
        for (final Node pairNode : nameValueNodes) {
            final String key = getAttribute(pairNode, "Name");
            final String valueString = getAttribute(pairNode, "Value");
            final String type = getAttribute(pairNode, "Type");
            Serializable value = null;
            if (type.equalsIgnoreCase("string") || type.equalsIgnoreCase("enum")) {
                value = valueString;
            }
            else if (type.equalsIgnoreCase("boolean")) {
                value = Boolean.parseBoolean(valueString);
            }
            else if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("uint")) {
                value = Integer.parseInt(valueString);
            }
            else if (type.equalsIgnoreCase("float")) {
                value = Float.parseFloat(valueString);
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }
    
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("specify config file");
            System.exit(1);
        }
        final DocumentBuilder builder = makeDocBuilder();
        Document doc;
        try {
            doc = builder.parse(new File(args[0]));
        }
        catch (Exception e) {
            Log.error(e.toString());
            return;
        }
        final Node worldDescNode = getMatchingChild(doc, "WorldDescription");
        final Node terrainNode = getMatchingChild(worldDescNode, "Terrain");
        if (Log.loggingDebug) {
            Log.debug("toXML: " + toXML(terrainNode));
        }
    }
    
    static {
        typeName = new String[] { "none", "Element", "Attr", "Text", "CDATA", "EntityRef", "Entity", "ProcInstr", "Comment", "Document", "DocType", "DocFragment", "Notation" };
    }
}
