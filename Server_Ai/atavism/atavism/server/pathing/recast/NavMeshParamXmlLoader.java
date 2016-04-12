// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

import java.util.List;
import org.w3c.dom.Element;
import atavism.server.pathing.detour.NavMeshParams;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.File;
import atavism.server.util.Log;
import atavism.server.util.XMLHelper;
import atavism.server.pathing.detour.NavMesh;
import org.w3c.dom.Document;
/**
 * NavMesh 参数加载
 * 
 * @author doter
 * 
 */
public class NavMeshParamXmlLoader {
	protected String worldFileName;
	protected Document worldDoc;

	public NavMeshParamXmlLoader(final String worldFileName) {
		this.worldFileName = worldFileName;
	}

	public boolean load(final NavMesh navMesh) {
		return this.parse() && this.generate(navMesh);
	}

	public boolean parse() {
		try {
			final DocumentBuilder builder = XMLHelper.makeDocBuilder();
			Log.debug("NAVMESH: loading in file: " + this.worldFileName);
			final File xmlFile = new File(this.worldFileName);
			this.worldDoc = builder.parse(xmlFile);
		} catch (IOException e2) {
			Log.error("NavMesh WorldFile not found: " + this.worldFileName + ". Reverting to old pathing system");
			return false;
		} catch (SAXException e) {
			Log.exception("NavMeshParamXmlLoader.parse(" + this.worldFileName + ")", e);
			return false;
		}
		return true;
	}
	/**
	 * 生成
	 * @param navMesh
	 * @return
	 */
	public boolean generate(final NavMesh navMesh) {
		final Node paramNode = XMLHelper.getMatchingChild(this.worldDoc, "NavMeshParams");
		if (paramNode == null) {
			Log.error("No <Param> node in file " + this.worldFileName);
			return false;
		}
		final NavMeshParams param = new NavMeshParams();
		final Node origNode = XMLHelper.getMatchingChild(paramNode, "Orig");
		if (origNode == null) {
			Log.debug("No <Orig> node in BVNode");
		} else {
			final List<Node> origNodes = XMLHelper.getMatchingChildren(origNode, "float");
			final float[] orig = new float[origNodes.size()];
			for (int i = 0; i < origNodes.size(); ++i) {
				final Node eElement = origNodes.get(i);
				orig[i] = Float.parseFloat(eElement.getTextContent());
			}
			param.Orig = orig;
		}
		Element eElement2 = (Element) XMLHelper.getMatchingChild(paramNode, "TileWidth");
		param.TileWidth = Float.parseFloat(eElement2.getTextContent());
		eElement2 = (Element) XMLHelper.getMatchingChild(paramNode, "TileHeight");
		param.TileHeight = Float.parseFloat(eElement2.getTextContent());
		eElement2 = (Element) XMLHelper.getMatchingChild(paramNode, "MaxTiles");
		param.MaxTiles = Integer.parseInt(eElement2.getTextContent());
		eElement2 = (Element) XMLHelper.getMatchingChild(paramNode, "MaxPolys");
		param.MaxPolys = Integer.parseInt(eElement2.getTextContent());
		navMesh.Init(param);// 初始化
		final int maxX = (int) Math.sqrt(param.MaxTiles);// 开方
		final String fileName = this.worldFileName.substring(0, this.worldFileName.length() - 4);
		for (int x = 0; x < maxX; ++x) {
			for (int y = 0; y < maxX; ++y) {
				Log.debug("NAVMESH: loading file: " + fileName + "_tile" + x + "_" + y + ".xml");// 每一片的信息
				final NavMeshXmlLoader navMeshLoader = new NavMeshXmlLoader(fileName + "_tile" + x + "_" + y + ".xml");
				navMeshLoader.load(navMesh);
			}
		}
		return true;
	}
}
