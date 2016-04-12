// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.physics;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.LinkedList;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NodeList;
import atavism.server.math.AOVector;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

public class ColladaPhysicsParser
{
    private static Logger logger;
    public static float UNITS_PER_METER;
    
    public static Shape<? extends Geometry> processShape(final Element shapeElement) throws SAXException {
        final NodeList shapeElementChildren = shapeElement.getChildNodes();
        final List<Transform> transformChain = new ArrayList<Transform>();
        Shape<? extends Geometry> rv = null;
        for (int i = 0; i < shapeElementChildren.getLength(); ++i) {
            final Node childNode = shapeElementChildren.item(i);
            if (childNode.getNodeType() == 1) {
                final String nodeName = childNode.getNodeName();
                if (nodeName.equals("translate")) {
                    final String translateText = childNode.getTextContent();
                    final String[] data = translateText.split("\\s+");
                    if (data.length != 3) {
                        ColladaPhysicsParser.logger.warn((Object)("Invalid translate text: " + translateText));
                        return null;
                    }
                    final TranslateTransform t = new TranslateTransform();
                    t.setX(ColladaPhysicsParser.UNITS_PER_METER * Float.valueOf(data[0]));
                    t.setY(ColladaPhysicsParser.UNITS_PER_METER * Float.valueOf(data[1]));
                    t.setZ(ColladaPhysicsParser.UNITS_PER_METER * Float.valueOf(data[2]));
                    transformChain.add(t);
                }
                else if (nodeName.equals("rotate")) {
                    final String rotateText = childNode.getTextContent();
                    final String[] data = rotateText.split("\\s+");
                    if (data.length != 4) {
                        ColladaPhysicsParser.logger.warn((Object)("Invalid rotate text: " + rotateText));
                        return null;
                    }
                    final RotateTransform t2 = new RotateTransform();
                    final AOVector axis = new AOVector(Float.valueOf(data[0]), Float.valueOf(data[1]), Float.valueOf(data[2]));
                    final float halfAngle = 0.5f * (float)Math.toRadians(Float.valueOf(data[3]));
                    final float cos = (float)Math.cos(halfAngle);
                    final float sin = (float)Math.sin(halfAngle);
                    final AOVector normAxis = axis.normalize();
                    t2.setW(cos);
                    t2.setX(sin * normAxis.getX());
                    t2.setY(sin * normAxis.getY());
                    t2.setZ(sin * normAxis.getZ());
                    transformChain.add(t2);
                }
                else if (nodeName.equals("box")) {
                    final NodeList halfExtentsList = ((Element)childNode).getElementsByTagName("half_extents");
                    final Node halfExtentsNode = halfExtentsList.item(0);
                    final String halfExtentsText = halfExtentsNode.getTextContent();
                    final String[] data2 = halfExtentsText.split("\\s+");
                    if (data2.length != 3) {
                        ColladaPhysicsParser.logger.warn((Object)("Invalid half_extents text: " + halfExtentsText));
                        return null;
                    }
                    final AOVector halfExtents = new AOVector(ColladaPhysicsParser.UNITS_PER_METER * Float.valueOf(data2[0]), ColladaPhysicsParser.UNITS_PER_METER * Float.valueOf(data2[1]), ColladaPhysicsParser.UNITS_PER_METER * Float.valueOf(data2[2]));
                    final Box box = new Box(halfExtents);
                    if (ColladaPhysicsParser.logger.isDebugEnabled()) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Got box:");
                        sb.append(box);
                        sb.append("\n");
                        sb.append("Transform Chain: ");
                        for (int j = 0; j < transformChain.size(); ++j) {
                            sb.append("  ");
                            sb.append(transformChain.get(j));
                        }
                        ColladaPhysicsParser.logger.debug((Object)sb.toString());
                    }
                    final Shape<Box> shape = new Shape<Box>();
                    shape.setGeometry(box);
                    rv = shape;
                    for (int j = 0; j < transformChain.size(); ++j) {
                        rv.addTransform(transformChain.get(j));
                    }
                    ColladaPhysicsParser.logger.debug((Object)rv);
                }
                else {
                    ColladaPhysicsParser.logger.warn((Object)("Unsupported node type: " + nodeName));
                }
            }
        }
        return rv;
    }
    
    public static void main(final String[] argv) {
        String filename = "water_trim_corner_sqr.physics";
        if (argv.length != 0) {
            filename = argv[0];
        }
        final File f = new File(filename);
        parsePhysics(f);
    }
    
    public static List<Shape<? extends Geometry>> parsePhysics(final String physicsData) {
        return parsePhysics(new ByteArrayInputStream(physicsData.getBytes()));
    }
    
    public static List<Shape<? extends Geometry>> parsePhysics(final File f) {
        try {
            final FileReader reader = new FileReader(f);
            final StringBuilder sb = new StringBuilder();
            final char[] buf = new char[4096];
            while (true) {
                final int bytes_read = reader.read(buf, 0, buf.length);
                if (bytes_read > 0) {
                    sb.append(buf, 0, bytes_read);
                }
                else {
                    if (bytes_read == 0) {
                        continue;
                    }
                    break;
                }
            }
            return parsePhysics(sb.toString());
        }
        catch (IOException e) {
            ColladaPhysicsParser.logger.error((Object)("IOException: " + e));
            return null;
        }
    }
    
    public static List<Shape<? extends Geometry>> parsePhysics(final InputStream physicsData) {
        try {
            final List<Shape<? extends Geometry>> rv = new LinkedList<Shape<? extends Geometry>>();
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(physicsData);
            final NodeList physicsModelList = doc.getElementsByTagName("physics_model");
            for (int i = 0; i < physicsModelList.getLength(); ++i) {
                final Node physicsModelNode = physicsModelList.item(i);
                if (physicsModelNode.getNodeType() == 1) {
                    final NodeList rigidBodyList = ((Element)physicsModelNode).getElementsByTagName("rigid_body");
                    for (int j = 0; j < rigidBodyList.getLength(); ++j) {
                        final Node rigidBodyNode = rigidBodyList.item(j);
                        if (rigidBodyNode.getNodeType() == 1) {
                            final Element rigidBodyElement = (Element)rigidBodyNode;
                            final String rigidBodySID = rigidBodyElement.getAttribute("sid");
                            if (rigidBodySID != null) {
                                ColladaPhysicsParser.logger.debug((Object)("Got rigidBody for sid: " + rigidBodySID));
                            }
                            final NodeList techniqueCommonList = rigidBodyElement.getChildNodes();
                            for (int k = 0; k < techniqueCommonList.getLength(); ++k) {
                                final Node techniqueCommonNode = techniqueCommonList.item(k);
                                if (techniqueCommonNode.getNodeType() == 1) {
                                    final NodeList shapeList = ((Element)rigidBodyNode).getElementsByTagName("shape");
                                    for (int l = 0; l < shapeList.getLength(); ++l) {
                                        final Node shapeNode = shapeList.item(l);
                                        if (shapeNode.getNodeType() == 1) {
                                            rv.add(processShape((Element)shapeNode));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return rv;
        }
        catch (SAXParseException err) {
            ColladaPhysicsParser.logger.error((Object)("Parsing error, line " + err.getLineNumber() + ", uri " + err.getSystemId()));
            ColladaPhysicsParser.logger.error((Object)(" " + err.getMessage()));
        }
        catch (SAXException e) {
            final Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
    
    static {
        ColladaPhysicsParser.logger = Logger.getLogger((Class)ColladaPhysicsParser.class);
        ColladaPhysicsParser.UNITS_PER_METER = 1.0f;
    }
}
