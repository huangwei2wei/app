// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

import atavism.server.pathing.detour.MeshHeader;
import java.util.List;
import org.w3c.dom.NodeList;
import atavism.server.pathing.detour.OffMeshConnection;
import atavism.server.pathing.detour.BVNode;
import atavism.server.pathing.detour.PolyDetail;
import atavism.server.pathing.detour.Link;
import atavism.server.pathing.detour.Poly;
import atavism.server.pathing.detour.NavMeshBuilder;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import java.io.IOException;
import atavism.server.util.Log;
import java.io.File;
import org.w3c.dom.Node;
import atavism.server.util.XMLHelper;
import org.w3c.dom.Element;
import atavism.server.pathing.detour.NavMesh;
import org.w3c.dom.Document;

public class NavMeshXmlLoader {
	protected String navMeshBuilderFileName;// 导航网格生成器文件名
	protected Document navMeshBuilderDoc;

	public NavMeshXmlLoader(final String fileName) {
		this.navMeshBuilderFileName = fileName;
	}

	public NavMeshXmlLoader() {
	}

	public boolean load(final NavMesh navMesh) {
		if (!this.parse()) {
			return false;
		}
		final Element builderNode = (Element) XMLHelper.getMatchingChild(this.navMeshBuilderDoc, "AtavismNavTile");
		return this.generate(navMesh, builderNode);
	}
	/**
	 * 解析器
	 * 
	 * @return
	 */
	public boolean parse() {
		try {
			final DocumentBuilder builder = XMLHelper.makeDocBuilder();
			final File xmlFile = new File(this.navMeshBuilderFileName);
			this.navMeshBuilderDoc = builder.parse(xmlFile);
		} catch (IOException e2) {
			Log.error("NavMesh WorldFile not found: " + this.navMeshBuilderFileName + ". Reverting to old pathing system");
			return false;
		} catch (SAXException e) {
			Log.exception("WorldFileLoader.parse(" + this.navMeshBuilderFileName + ")", e);
			return false;
		}
		return true;
	}
	/**
	 * 生成
	 * 
	 * @param navMesh
	 * @param builderNode
	 * @return
	 */
	public boolean generate(final NavMesh navMesh, final Element builderNode) {
		final NavMeshBuilder navMeshBuilder = new NavMeshBuilder();
		if (builderNode == null) {
			Log.error("No <NavMeshBuilder> node in file " + this.navMeshBuilderFileName);
			return false;
		}
		final NodeList nodeList = builderNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {// 如果当前节点是元素节点的话。
				if (nodeList.item(i).getNodeName().equals("Header")) {// header信息
					navMeshBuilder.Header = this.processHeader(nodeList.item(i));
				} else if (nodeList.item(i).getNodeName().equals("NavVerts")) {
					final List<Node> navVertsNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "float");
					final float[] navVerts = new float[navVertsNodes.size()];
					for (int j = 0; j < navVertsNodes.size(); ++j) {
						final Element eElement = (Element) navVertsNodes.get(j);
						navVerts[j] = Float.parseFloat(eElement.getTextContent());
					}
					navMeshBuilder.NavVerts = navVerts;
				} else if (nodeList.item(i).getNodeName().equals("NavPolys")) {
					final List<Node> navPolysNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "Poly");
					final Poly[] navPolys = new Poly[navPolysNodes.size()];
					for (int j = 0; j < navPolysNodes.size(); ++j) {
						navPolys[j] = this.processNavPoly(navPolysNodes.get(j));
					}
					navMeshBuilder.NavPolys = navPolys;
				} else if (nodeList.item(i).getNodeName().equals("NavLinks")) {
					final List<Node> navLinkNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "Link");
					final Link[] navLinks = new Link[navLinkNodes.size()];
					for (int j = 0; j < navLinkNodes.size(); ++j) {
						navLinks[j] = this.processLink(navLinkNodes.get(j));
					}
					navMeshBuilder.NavLinks = navLinks;
				} else if (nodeList.item(i).getNodeName().equals("NavDMeshes")) {
					final List<Node> navPolyDetailNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "PolyDetail");
					final PolyDetail[] navPolyDetails = new PolyDetail[navPolyDetailNodes.size()];
					for (int j = 0; j < navPolyDetailNodes.size(); ++j) {
						navPolyDetails[j] = this.processPolyDetail(navPolyDetailNodes.get(j));
					}
					navMeshBuilder.NavDMeshes = navPolyDetails;
				} else if (nodeList.item(i).getNodeName().equals("NavDVerts")) {
					final List<Node> navDVertsNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "float");
					final float[] navDVerts = new float[navDVertsNodes.size()];
					for (int j = 0; j < navDVertsNodes.size(); ++j) {
						final Element eElement = (Element) navDVertsNodes.get(j);
						navDVerts[j] = Float.parseFloat(eElement.getTextContent());
					}
					navMeshBuilder.NavDVerts = navDVerts;
				} else if (nodeList.item(i).getNodeName().equals("NavDTris")) {
					final List<Node> navDTrisNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "short");
					final short[] navDTris = new short[navDTrisNodes.size()];
					for (int j = 0; j < navDTrisNodes.size(); ++j) {
						final Element eElement = (Element) navDTrisNodes.get(j);
						navDTris[j] = Short.parseShort(eElement.getTextContent());
					}
					navMeshBuilder.NavDTris = navDTris;
				} else if (nodeList.item(i).getNodeName().equals("NavBvTree")) {
					final List<Node> navBVNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "BVNode");
					final BVNode[] navBvTree = new BVNode[navBVNodes.size()];
					for (int j = 0; j < navBVNodes.size(); ++j) {
						navBvTree[j] = this.processBVNode(navBVNodes.get(j));
					}
					navMeshBuilder.NavBvTree = navBvTree;
				} else if (nodeList.item(i).getNodeName().equals("NavBvTree")) {
					final List<Node> navOffMeshConnectionNodes = XMLHelper.getMatchingChildren(nodeList.item(i), "OffMeshConnection");// 关网状连接
					final OffMeshConnection[] offMeshCons = new OffMeshConnection[navOffMeshConnectionNodes.size()];
					for (int j = 0; j < navOffMeshConnectionNodes.size(); ++j) {
						offMeshCons[j] = this.processOffMeshConnection(navOffMeshConnectionNodes.get(j));
					}
					navMeshBuilder.OffMeshCons = offMeshCons;
				}
			}
		}
		final long tempRef = 0L;
		long temp = 0L;
		if (navMeshBuilder.Header != null) {
			temp = navMesh.AddTile(navMeshBuilder, NavMesh.TileFreeData, tempRef).longValue;
			Log.debug("NAVMESH: added tile");
		}
		return true;
	}

	protected MeshHeader processHeader(final Node node) {
		final MeshHeader header = new MeshHeader();
		Element eElement = (Element) XMLHelper.getMatchingChild(node, "Magic");
		Log.debug("NAVMESH: magic=" + eElement.getTextContent());
		header.Magic = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "Version");
		header.Version = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "X");
		header.X = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "Y");
		header.Y = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "Layer");
		header.Layer = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "UserId");
		header.UserId = Long.parseLong(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "PolyCount");
		header.PolyCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "VertCount");
		header.VertCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "MaxLinkCount");
		header.MaxLinkCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "DetailMeshCount");
		header.DetailMeshCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "DetailVertCount");
		header.DetailVertCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "DetailTriCount");
		header.DetailTriCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "BVNodeCount");
		header.BVNodeCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "OffMeshConCount");
		header.OffMeshConCount = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "OffMeshBase");
		header.OffMeshBase = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "WalkableHeight");
		header.WalkableHeight = Float.parseFloat(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "WalkableRadius");
		header.WalkableRadius = Float.parseFloat(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "WalkableClimb");
		header.WalkableClimb = Float.parseFloat(eElement.getTextContent());
		final Node bminNode = XMLHelper.getMatchingChild(node, "BMin");
		if (bminNode == null) {
			Log.debug("No <BMin> node in BVNode");
		} else {
			final List<Node> bminNodes = XMLHelper.getMatchingChildren(bminNode, "float");
			final float[] bmin = new float[bminNodes.size()];
			for (int i = 0; i < bminNodes.size(); ++i) {
				eElement = (Element) bminNodes.get(i);
				bmin[i] = Float.parseFloat(eElement.getTextContent());
			}
			header.BMin = bmin;
		}
		final Node bmaxNode = XMLHelper.getMatchingChild(node, "BMax");
		if (bmaxNode == null) {
			Log.debug("No <BMax> node in BVNode");
		} else {
			final List<Node> bmaxNodes = XMLHelper.getMatchingChildren(bmaxNode, "float");
			final float[] bmax = new float[bmaxNodes.size()];
			for (int j = 0; j < bmaxNodes.size(); ++j) {
				eElement = (Element) bmaxNodes.get(j);
				bmax[j] = Float.parseFloat(eElement.getTextContent());
			}
			header.BMax = bmax;
		}
		eElement = (Element) XMLHelper.getMatchingChild(node, "TileRef");
		header.TileRef = Long.parseLong(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "BVQuantFactor");
		header.BVQuantFactor = Float.parseFloat(eElement.getTextContent());
		return header;
	}

	protected Poly processNavPoly(final Node node) {
		final Poly poly = new Poly();
		Element eElement = (Element) XMLHelper.getMatchingChild(node, "_areaAndType");
		poly._areaAndType = Short.parseShort(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "FirstLink");
		poly.FirstLink = Long.parseLong(eElement.getTextContent());
		final Node vertsNode = XMLHelper.getMatchingChild(node, "Verts");
		if (vertsNode == null) {
			Log.debug("No <NavVerts> node in Poly");
		} else {
			final List<Node> vertsNodes = XMLHelper.getMatchingChildren(vertsNode, "int");
			final int[] verts = new int[vertsNodes.size()];
			for (int i = 0; i < vertsNodes.size(); ++i) {
				eElement = (Element) vertsNodes.get(i);
				verts[i] = Integer.parseInt(eElement.getTextContent());
			}
			poly.Verts = verts;
		}
		final Node neisNode = XMLHelper.getMatchingChild(node, "Neis");
		if (neisNode == null) {
			Log.debug("No <NavVerts> node in Poly");
		} else {
			final List<Node> neisNodes = XMLHelper.getMatchingChildren(neisNode, "int");
			final int[] neis = new int[neisNodes.size()];
			for (int j = 0; j < neisNodes.size(); ++j) {
				eElement = (Element) neisNodes.get(j);
				neis[j] = Integer.parseInt(eElement.getTextContent());
			}
			poly.Neis = neis;
		}
		eElement = (Element) XMLHelper.getMatchingChild(node, "Flags");
		poly.Flags = Integer.parseInt(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "VertCount");
		poly.VertCount = Short.parseShort(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "Area");
		poly.setArea(Short.parseShort(eElement.getTextContent()));
		eElement = (Element) XMLHelper.getMatchingChild(node, "Type");
		poly.setType(Short.parseShort(eElement.getTextContent()));
		return poly;
	}

	protected Link processLink(final Node node) {
		final Link link = new Link();
		Element eElement = (Element) XMLHelper.getMatchingChild(node, "Ref");
		link.Ref = Long.parseLong(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "Next");
		link.Next = Long.parseLong(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "Edge");
		link.Edge = Short.parseShort(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "Side");
		link.Side = Short.parseShort(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "BMin");
		link.BMin = Short.parseShort(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "BMax");
		link.BMax = Short.parseShort(eElement.getTextContent());
		return link;
	}

	protected PolyDetail processPolyDetail(final Node node) {
		final PolyDetail detail = new PolyDetail();
		Element eElement = (Element) XMLHelper.getMatchingChild(node, "VertBase");
		detail.VertBase = Long.parseLong(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "TriBase");
		detail.TriBase = Long.parseLong(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "VertCount");
		detail.VertCount = Short.parseShort(eElement.getTextContent());
		eElement = (Element) XMLHelper.getMatchingChild(node, "TriCount");
		detail.TriCount = Short.parseShort(eElement.getTextContent());
		return detail;
	}

	protected BVNode processBVNode(final Node node) {
		final BVNode bvNode = new BVNode();
		Element eElement = (Element) XMLHelper.getMatchingChild(node, "I");
		bvNode.I = Integer.parseInt(eElement.getTextContent());
		final Node bminNode = XMLHelper.getMatchingChild(node, "BMin");
		if (bminNode == null) {
			Log.debug("No <BMin> node in BVNode");
		} else {
			final List<Node> bminNodes = XMLHelper.getMatchingChildren(bminNode, "int");
			final int[] bmin = new int[bminNodes.size()];
			for (int i = 0; i < bminNodes.size(); ++i) {
				eElement = (Element) bminNodes.get(i);
				bmin[i] = Integer.parseInt(eElement.getTextContent());
			}
			bvNode.BMin = bmin;
		}
		final Node bmaxNode = XMLHelper.getMatchingChild(node, "BMax");
		if (bmaxNode == null) {
			Log.debug("No <BMax> node in BVNode");
		} else {
			final List<Node> bmaxNodes = XMLHelper.getMatchingChildren(bmaxNode, "int");
			final int[] bmax = new int[bmaxNodes.size()];
			for (int j = 0; j < bmaxNodes.size(); ++j) {
				eElement = (Element) bmaxNodes.get(j);
				bmax[j] = Integer.parseInt(eElement.getTextContent());
			}
			bvNode.BMax = bmax;
		}
		return bvNode;
	}

	protected OffMeshConnection processOffMeshConnection(final Node node) {
		final OffMeshConnection omc = new OffMeshConnection();
		final Node vertsNode = XMLHelper.getMatchingChild(node, "Pos");
		if (vertsNode == null) {
			Log.debug("No <Pos> node in OffMeshConnection ");
		} else {
			final List<Node> posNodes = XMLHelper.getMatchingChildren(vertsNode, "float");
			final float[] pos = new float[posNodes.size()];
			for (int i = 0; i < posNodes.size(); ++i) {
				final Element eElement = (Element) posNodes.get(i);
				pos[i] = Float.parseFloat(eElement.getTextContent());
			}
			omc.Pos = pos;
		}
		Element eElement2 = (Element) XMLHelper.getMatchingChild(node, "Rad");
		omc.Rad = Float.parseFloat(eElement2.getTextContent());
		eElement2 = (Element) XMLHelper.getMatchingChild(node, "Poly");
		omc.Poly = Integer.parseInt(eElement2.getTextContent());
		eElement2 = (Element) XMLHelper.getMatchingChild(node, "Flags");
		omc.Flags = Short.parseShort(eElement2.getTextContent());
		eElement2 = (Element) XMLHelper.getMatchingChild(node, "Side");
		omc.Side = Short.parseShort(eElement2.getTextContent());
		eElement2 = (Element) XMLHelper.getMatchingChild(node, "UserId");
		omc.UserId = Long.parseLong(eElement2.getTextContent());
		return omc;
	}
}
