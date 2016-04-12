// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import org.w3c.dom.NodeList;
import java.util.LinkedList;
import java.util.List;
import atavism.server.util.AORuntimeException;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class Configuration
{
    static final String[] typeName;
    private Document document;
    
    public Configuration(final String fileName) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
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
            this.document = builder.parse(new File(fileName));
        }
        catch (SAXException sxe) {
            Exception x = sxe;
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            x.printStackTrace();
        }
        catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public Node getRoot() {
        return this.document;
    }
    
    public static String getValueFromChild(final String childName, final Node node) {
        if (node == null) {
            throw new AORuntimeException("node is null");
        }
        final Node childNode = findChild(node, childName);
        if (childNode == null) {
            throw new AORuntimeException("could not find child node with name: " + childName);
        }
        return getNodeValue(childNode);
    }
    
    public static String getNodeValue(final Node node) {
        return node.getFirstChild().getNodeValue();
    }
    
    public static List getMatchingChildren(final Node node, final String name) {
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
    
    public static Node findChild(final Node node, final String name) {
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
    
    public static void printAllChildren(final Node node) {
        if (node == null) {
            return;
        }
        final NodeList childList = node.getChildNodes();
        for (int len = childList.getLength(), i = 0; i < len; ++i) {
            final Node curNode = childList.item(i);
            System.out.println("node: " + toStringNode(curNode));
        }
    }
    
    public static String toStringNode(final Node domNode) {
        if (domNode == null) {
            return "";
        }
        String s = Configuration.typeName[domNode.getNodeType()];
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
    
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("specify config file");
            System.exit(1);
        }
        final Configuration config = new Configuration(args[0]);
        final Node portNode = findChild(config.getRoot().getFirstChild(), "port");
        if (portNode == null) {
            System.out.println("could not find port node");
            System.exit(1);
        }
        System.out.println("found port node");
        System.out.println("printing all port node children");
        printAllChildren(portNode);
    }
    
    static {
        typeName = new String[] { "none", "Element", "Attr", "Text", "CDATA", "EntityRef", "Entity", "ProcInstr", "Comment", "Document", "DocType", "DocFragment", "Notation" };
    }
}
