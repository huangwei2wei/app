// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import java.util.HashMap;
import java.util.Map;
import com.app.server.atavism.server.util.AORuntimeException;
import java.util.Iterator;
import java.util.Collection;
//import atavism.server.util.Log;
import java.util.List;
import com.app.server.atavism.server.objects.Region;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.util.LockFactory;
import java.util.HashSet;
//import atavism.server.util.Logger;
import java.util.concurrent.locks.Lock;
import com.app.server.atavism.server.math.Geometry;
import java.util.Set;

import org.apache.log4j.Logger;

public class QuadTree<ElementType extends QuadTreeElement<ElementType>> {
	private Logger log = Logger.getLogger("navmesh");
	public NewsAndFrees spawningNewsAndFrees;
	protected Set<FixedPerceiver<ElementType>> fixedPerceivers;
	private Geometry localGeometry;
	private int hysteresis;
	private int maxObjects;
	private int maxDepth;
	private QuadTreeNode<ElementType> rootNode;
	private Lock lock;
	boolean supportsExtentBasedPerceiver;

	public QuadTree(final Geometry g) {
		this.spawningNewsAndFrees = null;
		this.fixedPerceivers = new HashSet<FixedPerceiver<ElementType>>();
		this.localGeometry = null;
		this.hysteresis = 0;
		this.maxObjects = 30;
		this.maxDepth = 20;
		this.rootNode = null;
		this.lock = LockFactory.makeLock("QuadTreeLock");
		this.createQuadTree(g, 0);
	}

	public QuadTree(final Geometry g, final int hysteresis) {
		this.spawningNewsAndFrees = null;
		this.fixedPerceivers = new HashSet<FixedPerceiver<ElementType>>();
		this.localGeometry = null;
		this.hysteresis = 0;
		this.maxObjects = 30;
		this.maxDepth = 20;
		this.rootNode = null;
		this.lock = LockFactory.makeLock("QuadTreeLock");
		this.createQuadTree(g, hysteresis);
	}

	protected void createQuadTree(final Geometry g, final int hysteresis) {
		this.supportsExtentBasedPerceiver = true;
		this.hysteresis = hysteresis;
		this.rootNode = new QuadTreeNode<ElementType>(this, null, g, QuadTreeNode.NodeType.REMOTE);
	}

	public QuadTree(final Geometry g, final boolean supportsExtentBasedPerceiver) {
		this.spawningNewsAndFrees = null;
		this.fixedPerceivers = new HashSet<FixedPerceiver<ElementType>>();
		this.localGeometry = null;
		this.hysteresis = 0;
		this.maxObjects = 30;
		this.maxDepth = 20;
		this.rootNode = null;
		this.lock = LockFactory.makeLock("QuadTreeLock");
		this.supportsExtentBasedPerceiver = supportsExtentBasedPerceiver;
		this.rootNode = new QuadTreeNode<ElementType>(this, null, g, QuadTreeNode.NodeType.REMOTE);
	}

	QuadTreeNode<ElementType> getRoot() {
		return this.rootNode;
	}

	public void printTree() {
		this.rootNode.recurseToString();
	}

	public Set<ElementType> getElements(final Point loc, final int radius) {
		return this.rootNode.getElements(loc, radius);
	}

	public Set<ElementType> getElementsBetween(final Point loc1, final Point loc2) {
		return this.rootNode.getElementsBetween(loc1, loc2);
	}

	public Set<ElementType> getElements(final ElementType elem, final int radius) {
		return this.getElements(elem.getLoc(), radius);
	}

	public Geometry getLocalGeometry() {
		return this.localGeometry;
	}

	public void addRegion(final Region region) {
		this.rootNode.addRegion(region);
	}

	public List<Region> getRegionsContainingPoint(final Point loc) {
		this.lock.lock();
		try {
			final QuadTreeNode<ElementType> newNode = this.findLeafNode(loc);
			return newNode.getRegionByLoc(loc);
		} finally {
			this.lock.unlock();
		}
	}

	public void setMaxObjects(final int max) {
		if (max > 0 && max != this.maxObjects) {
			log.info("QuadTree maximum-objects-per-node changed from " + this.maxObjects + " to " + max);
			this.maxObjects = max;
		}
	}

	public int getMaxObjects() {
		return this.maxObjects;
	}

	public void setMaxDepth(final int max) {
		if (max > 0 && max != this.maxDepth) {
			log.info("QuadTree maximum-depth changed from " + this.maxDepth + " to " + max);
			this.maxDepth = max;
		}
	}

	public int getMaxDepth() {
		return this.maxDepth;
	}

	public boolean getSupportsExtentBasedPerceiver() {
		return this.supportsExtentBasedPerceiver;
	}

	public Lock getLock() {
		return this.lock;
	}

	public int getHysteresis() {
		return this.hysteresis;
	}

	public void setHysteresis(final int hysteresis) {
		this.hysteresis = hysteresis;
	}

	public QuadTreeNode<ElementType> addElement(final ElementType elem) {
		final NewsAndFrees newsAndFrees = new NewsAndFrees();
		this.lock.lock();
		try {
			final QuadTreeNode<ElementType> node = this.addElementInternal(elem, newsAndFrees);
			newsAndFrees.processNewsAndFrees();
			return node;
		} finally {
			this.lock.unlock();
		}
	}

	public Integer addElementReturnCountForPerceiver(final ElementType elem, final OID mobilePerceiverOid) {
		final NewsAndFrees newsAndFrees = new NewsAndFrees();
		this.lock.lock();
		try {
			this.addElementInternal(elem, newsAndFrees);
			final Integer count = newsAndFrees.processNewsAndFrees(mobilePerceiverOid);
			return (count == null) ? 0 : count;
		} finally {
			this.lock.unlock();
		}
	}

	public boolean removeElement(final ElementType elem) {
		final NewsAndFrees newsAndFrees = new NewsAndFrees();
		this.lock.lock();
		try {
			final boolean rv = this.removeElementInternal(elem, newsAndFrees);
			newsAndFrees.processNewsAndFrees();
			return rv;
		} finally {
			this.lock.unlock();
		}
	}

	public Integer removeElementReturnCountForPerceiver(final ElementType elem, final OID mobilePerceiverOid) {
		final NewsAndFrees newsAndFrees = new NewsAndFrees();
		this.lock.lock();
		try {
			this.removeElementInternal(elem, newsAndFrees);
			final Integer count = newsAndFrees.processNewsAndFrees(mobilePerceiverOid);
			return (count == null) ? 0 : count;
		} finally {
			this.lock.unlock();
		}
	}

	public void updateElement(final ElementType elem, final Point loc) {
		final NewsAndFrees newsAndFrees = new NewsAndFrees();
		this.lock.lock();
		try {
			this.updateElementInternal(elem, loc, newsAndFrees);
			newsAndFrees.processNewsAndFrees();
		} finally {
			this.lock.unlock();
		}
	}

	protected void updatePerceiver(final Perceiver<ElementType> perceiver) {
		final NewsAndFrees newsAndFrees = new NewsAndFrees();
		this.lock.lock();
		try {
			this.updatePerceiverInternal(perceiver, newsAndFrees);
			newsAndFrees.processNewsAndFrees();
		} finally {
			this.lock.unlock();
		}
	}

	public void addFixedPerceiver(final FixedPerceiver<ElementType> perceiver) {
		log.debug("QuadTree.addFixedPerceiver p=" + perceiver);
		this.fixedPerceivers.add(perceiver);
		this.updatePerceiver(perceiver);
	}

	public void removeFixedPerceiver(final FixedPerceiver<ElementType> perceiver) {
		this.fixedPerceivers.remove(perceiver);
		this.updatePerceiver(perceiver);
	}

	public void setLocalGeometry(final Geometry g) {
		final NewsAndFrees newsAndFrees = new NewsAndFrees();
		this.lock.lock();
		try {
			this.localGeometry = g;
			this.setLocalGeometryHelper(this.rootNode, g, newsAndFrees);
			newsAndFrees.processNewsAndFrees();
		} finally {
			this.lock.unlock();
		}
	}

	public Collection<ElementType> getElementPerceivables(final ElementType elem) {
		final Set<ElementType> result = new HashSet<ElementType>();
		this.lock.lock();
		try {
			final Perceiver<ElementType> perceiver = elem.getPerceiver();
			if (perceiver == null) {
				return result;
			}
			final Set<QuadTreeNode<ElementType>> nodes = perceiver.getQuadTreeNodes();
			for (final QuadTreeNode<ElementType> node : nodes) {
				result.addAll((Collection<? extends ElementType>) node.getNodeElements());
				final Set<ElementType> perceiverExtentObjects = node.getPerceiverExtentObjects();
				if (perceiverExtentObjects != null) {
					result.addAll((Collection<? extends ElementType>) perceiverExtentObjects);
				}
			}
			return result;
		} finally {
			this.lock.unlock();
		}
	}

	protected QuadTreeNode<ElementType> addElementInternal(final ElementType elem, final NewsAndFrees newsAndFrees) {
		final Point loc = elem.getLoc();
		this.lock.lock();
		try {
			if (this.supportsExtentBasedPerceiver) {
				final int radius = elem.getPerceptionRadius();
				log.debug("QuadTree.addElementInternal: elem " + elem + ", percept radius " + radius);
				if (radius > 0) {
					this.rootNode.addPerceiverExtentObject(elem, loc, radius);
				}
			}
			return this.addHelper(this.rootNode, elem, loc, newsAndFrees);
		} finally {
			this.lock.unlock();
		}
	}

	protected QuadTreeNode<ElementType> addHelper(final QuadTreeNode<ElementType> node, final ElementType elem, final Point loc, final NewsAndFrees newsAndFrees) {
		if (loc == null) {
			throw new AORuntimeException("QuadTree.addHelper: obj has null location");
		}
		if (!node.getGeometry().contains(loc)) {
			log.warn("QuadTree.addHelper: element not within node, element=" + elem + ", quadtreenode=" + node);
			return null;
		}
		if (!node.isLeaf()) {
			final QuadTreeNode<ElementType> childNode = node.whichChild(loc);
			return this.addHelper(childNode, elem, loc, newsAndFrees);
		}
		log.debug("QuadTree.addHelper: node is leaf: " + node + ", rootNode is leaf? " + this.rootNode.isLeaf() + ", qtree=" + this.hashCode());
		final int curSize = node.numElements();
		final int maxSize = this.getMaxObjects();
		if (curSize >= maxSize && node.getDepth() < this.getMaxDepth()) {
			log.debug("QuadTree.addHelper: maxObj=" + maxSize + ", cursize=" + curSize + ".. dividing");
			node.divide(newsAndFrees);
			return this.addHelper(node, elem, loc, newsAndFrees);
		}
		log.debug("QuadTree.addHelper: adding element " + elem + " to quadnode " + node + " -- maxObjects=" + this.getMaxObjects());
		this.updateElementInternal(elem, loc, newsAndFrees);
		if (!node.containsElement(elem)) {
			throw new AORuntimeException("QuadTree.addHelper: Failed check");
		}
		return node;
	}

	protected boolean removeElementInternal(final ElementType elem, final NewsAndFrees newsAndFrees) {
		this.lock.lock();
		final QuadTreeNode<ElementType> node = elem.getQuadNode();
		try {
			final boolean rv = node.removeElement(elem);
			elem.setQuadNode(null);
			for (final Perceiver<ElementType> p : node.getPerceivers()) {
				newsAndFrees.noteFreedElement(p, elem);
			}
			Perceiver<ElementType> p2 = elem.getPerceiver();
			if (p2 != null) {
				this.updatePerceiverInternal(p2, newsAndFrees);
				p2 = null;
			}
			this.rootNode.removePerceiverExtentObject(elem);
			return rv;
		} finally {
			this.lock.unlock();
		}
	}

	protected void updateElementInternal(final ElementType elem, final Point loc, final NewsAndFrees newsAndFrees) {
		this.lock.lock();
		try {
			log.debug("updateElement: elem=" + elem);
			final QuadTreeNode<ElementType> node = elem.getQuadNode();
			if (node != null && node.getGeometry().contains(loc)) {
				final Perceiver<ElementType> perceiver = elem.getPerceiver();
				if (perceiver != null) {
					if (perceiver.shouldUpdateBasedOnLoc(loc)) {
						this.updatePerceiverInternal(perceiver, newsAndFrees);
					}
				} else {
					log.debug("updateElementInternal: has no perceiver");
				}
				return;
			}
			final QuadTreeNode<ElementType> newNode = this.findLeafNode(loc);
			if (node != null) {
				if (!node.removeElement(elem)) {
					throw new RuntimeException("updateElementInternal: could not remove from node");
				}
				log.debug("QuadTree.updateElementInternal: element moved out of node obj=" + elem.getQuadTreeObject() + " oldNode=" + node + " newNode=" + newNode + ", loc=" + loc);
			}
			newNode.addElement(elem);
			elem.setQuadNode(newNode);
			this.updateElementPerceiversInternal(elem, node, newNode, newsAndFrees);
			final Perceiver<ElementType> perceiver2 = elem.getPerceiver();
			if (perceiver2 != null) {
				this.updatePerceiverInternal(perceiver2, newsAndFrees);
			}
		} finally {
			this.lock.unlock();
		}
	}

	protected void updateElementPerceiversInternal(final ElementType elem, final QuadTreeNode<ElementType> oldNode, final QuadTreeNode<ElementType> newNode, final NewsAndFrees newsAndFrees) {
		Set<Perceiver<ElementType>> removePerceivers = new HashSet<Perceiver<ElementType>>();
		this.rootNode.removePerceiverExtentObject(elem);
		this.rootNode.addPerceiverExtentObject(elem, elem.getLoc(), elem.getPerceptionRadius());

		log.debug("updateElementPerceivers: elem=" + elem + " oldNode=" + oldNode + " newNode=" + newNode);
		if (oldNode != null) {
			removePerceivers = oldNode.getPerceivers();
		}
		final Set<Perceiver<ElementType>> addPerceivers = newNode.getPerceivers();
		removePerceivers.removeAll(newNode.getPerceivers());
		if (oldNode != null) {
			addPerceivers.removeAll(oldNode.getPerceivers());
		}

		log.debug("updateElementPerceivers: remove perceivers size=" + removePerceivers.size() + "add perceivers size=" + addPerceivers.size());
		final HashSet<ElementType> freed = new HashSet<ElementType>();
		for (final Perceiver<ElementType> p : removePerceivers) {
			newsAndFrees.noteFreedElement(p, elem);
		}
		final HashSet<ElementType> news = new HashSet<ElementType>();
		for (final Perceiver<ElementType> p2 : addPerceivers) {
			newsAndFrees.noteNewElement(p2, elem);
		}
	}

	protected void updatePerceiverInternal(final Perceiver<ElementType> perceiver, final NewsAndFrees newsAndFrees) {
		this.lock.lock();
		try {
			if (perceiver instanceof MobilePerceiver) {
				final MobilePerceiver<ElementType> p = (MobilePerceiver<ElementType>) (MobilePerceiver) perceiver;
				log.debug("QuadTree.updatePerceiver: mobile perceiver radius=" + p.getRadius() + " owner=" + p.getElement().getQuadTreeObject());
			} else {
				final FixedPerceiver<ElementType> p2 = (FixedPerceiver<ElementType>) (FixedPerceiver) perceiver;
				log.debug("QuadTree.updatePerceiver: fixed perceiver geom=" + p2.getGeometry());
			}
			final Set<QuadTreeNode<ElementType>> oldNodes = perceiver.getQuadTreeNodes();
			final Set<QuadTreeNode<ElementType>> removeNodes = new HashSet<QuadTreeNode<ElementType>>(oldNodes);
			final Set<QuadTreeNode<ElementType>> newNodes = new HashSet<QuadTreeNode<ElementType>>();
			this.updatePerceiverHelper(newNodes, this.rootNode, perceiver);
			final Set<QuadTreeNode<ElementType>> addNodes = new HashSet<QuadTreeNode<ElementType>>(newNodes);
			final Set<ElementType> bothPerceiverExtentElements = new HashSet<ElementType>();
			final Set<ElementType> oldPerceiverExtentElements = new HashSet<ElementType>();
			for (final QuadTreeNode<ElementType> node : oldNodes) {
				final Set<ElementType> perceiverExtentObjects = node.getNodeElements();
				if (perceiverExtentObjects != null) {
					for (final ElementType elem : perceiverExtentObjects) {
						oldPerceiverExtentElements.add(elem);
					}
				}
			}
			final Set<ElementType> newPerceiverExtentElements = new HashSet<ElementType>();
			for (final QuadTreeNode<ElementType> node2 : addNodes) {
				final Set<ElementType> perceiverExtentObjects2 = node2.getNodeElements();
				if (perceiverExtentObjects2 != null) {
					for (final ElementType elem2 : perceiverExtentObjects2) {
						newPerceiverExtentElements.add(elem2);
						if (oldPerceiverExtentElements.contains(elem2)) {
							bothPerceiverExtentElements.add(elem2);
						}
					}
				}
			}
			removeNodes.removeAll(newNodes);
			addNodes.removeAll(oldNodes);
			log.debug("Before removing, newPerceiverExtentElements.size(): " + newPerceiverExtentElements.size() + " oldPerceiverExtentElements.size(): " + oldPerceiverExtentElements.size()
					+ " bothPerceiverExtentElements.size(): " + bothPerceiverExtentElements.size() + " num remove nodes=" + removeNodes.size());
			for (final QuadTreeNode<ElementType> node2 : removeNodes) {
				log.debug("updatePerceiver: removing from node " + node2);
				perceiver.removeQuadTreeNode(node2);
				node2.removePerceiver(perceiver);
				for (final ElementType elem : node2.getNodeElements()) {
					if (!newPerceiverExtentElements.contains(elem) && !oldPerceiverExtentElements.contains(elem)) {
						newsAndFrees.noteFreedElement(perceiver, elem);
					}
				}
			}
			oldPerceiverExtentElements.removeAll(bothPerceiverExtentElements);
			newPerceiverExtentElements.removeAll(bothPerceiverExtentElements);
			log.debug("After removing, newPerceiverExtentElements.size(): " + newPerceiverExtentElements.size() + " oldPerceiverExtentElements.size(): " + oldPerceiverExtentElements.size());
			for (final ElementType elem3 : oldPerceiverExtentElements) {
				newsAndFrees.noteFreedElement(perceiver, elem3);
			}
			log.debug("updatePerceiver: num addnodes=" + addNodes.size());
			for (final QuadTreeNode<ElementType> node2 : addNodes) {

				log.debug("updatePerceiver: adding to node " + node2);
				perceiver.addQuadTreeNode(node2);
				node2.addPerceiver(perceiver);
				for (final ElementType elem : node2.getNodeElements()) {
					if (!newPerceiverExtentElements.contains(elem) && !bothPerceiverExtentElements.contains(elem)) {
						newsAndFrees.noteNewElement(perceiver, elem);
					}
				}
			}
			for (final ElementType elem3 : newPerceiverExtentElements) {
				newsAndFrees.noteNewElement(perceiver, elem3);
			}
			log.debug("updatedPerceiver: done updating, printing out list of all nodes");
			for (final QuadTreeNode<ElementType> node2 : perceiver.getQuadTreeNodes()) {
				log.debug("updatePerceiver: IS IN NODE " + node2);
			}
		} finally {
			this.lock.unlock();
		}
	}

	protected void updatePerceiverHelper(final Set<QuadTreeNode<ElementType>> nodeSet, final QuadTreeNode<ElementType> node, final Perceiver<ElementType> perceiver) {
		if (perceiver.overlaps(node.getGeometry())) {
			if (!node.isLeaf()) {
				for (final QuadTreeNode<ElementType> child : node.getChildren()) {
					this.updatePerceiverHelper(nodeSet, child, perceiver);
				}
			} else {
				nodeSet.add(node);
			}
		}
	}

	protected void setLocalGeometryHelper(final QuadTreeNode<ElementType> node, final Geometry g, final NewsAndFrees newsAndFrees) {
		if (!g.overlaps(node.getGeometry())) {
			return;
		}
		if (g.contains(node.getGeometry())) {
			node.setNodeType(QuadTreeNode.NodeType.LOCAL);
		} else {
			if (node.isLeaf()) {
				log.debug("setLocalGeometryHelper: divide");
				node.divide(newsAndFrees);
			}
			node.setNodeType(QuadTreeNode.NodeType.MIXED);
		}
		if (!node.isLeaf()) {
			for (final QuadTreeNode<ElementType> child : node.getChildren()) {
				this.setLocalGeometryHelper(child, g, newsAndFrees);
			}
		}
	}

	QuadTreeNode<ElementType> findLeafNode(final Point loc) {
		return this.findLeafNodeHelper(this.rootNode, loc);
	}

	QuadTreeNode<ElementType> findLeafNodeHelper(final QuadTreeNode<ElementType> node, final Point loc) {
		if (node.isLeaf()) {
			if (node.getGeometry().contains(loc)) {
				return node;
			}
			return null;
		} else {
			log.trace("QUAD: checking node: " + node.toString() + " against loc: " + loc + ". is node leaf? " + node.isLeaf());
			final QuadTreeNode<ElementType> childNode = node.getChild(loc);
			if (childNode == null) {
				log.error("QUAD: got null child for loc: " + loc + "with node: " + node.toString() + " with children: " + node.getChildren());
				return node;
			}
			return this.findLeafNodeHelper(childNode, loc);
		}
	}

	public class NewsAndFrees {
		protected Map<Perceiver<ElementType>, PerceiverNewsAndFrees<ElementType>> perceiverMap;

		public NewsAndFrees() {
			this.perceiverMap = new HashMap<Perceiver<ElementType>, PerceiverNewsAndFrees<ElementType>>();
		}

		public Map<Perceiver<ElementType>, PerceiverNewsAndFrees<ElementType>> getMap() {
			return this.perceiverMap;
		}

		public void noteNewElement(final Perceiver<ElementType> perceiver, final ElementType element) {
			if (perceiver.shouldNotifyNewElement(element)) {
				PerceiverNewsAndFrees<ElementType> newsAndFrees = this.perceiverMap.get(perceiver);
				if (newsAndFrees == null) {
					newsAndFrees = new PerceiverNewsAndFrees<ElementType>();
					this.perceiverMap.put(perceiver, newsAndFrees);
				}
				newsAndFrees.addNewElement(element);
			}
		}

		public void noteFreedElement(final Perceiver<ElementType> perceiver, final ElementType element) {
			if (perceiver.shouldFreeElement(element)) {
				PerceiverNewsAndFrees<ElementType> newsAndFrees = this.perceiverMap.get(perceiver);
				if (newsAndFrees == null) {
					newsAndFrees = new PerceiverNewsAndFrees<ElementType>();
					this.perceiverMap.put(perceiver, newsAndFrees);
				}
				newsAndFrees.addFreedElement(element);
			}
		}

		public Integer processNewsAndFrees() {
			this.processNewsAndFrees(null);
			return null;
		}

		public Integer processNewsAndFrees(final OID mobilePerceiverOid) {
			return this.processBatchedNewsAndFrees(mobilePerceiverOid);
		}

		protected Integer processBatchedNewsAndFrees(final OID mobilePerceiverOid) {
			int news = 0;
			int frees = 0;
			Integer perceiverOidCount = null;
			for (final PerceiverNewsAndFrees<ElementType> newsAndFrees : this.perceiverMap.values()) {
				news += newsAndFrees.newCount();
				frees += newsAndFrees.freedCount();
			}
			final boolean workToDo = news > 0 || frees > 0;

			log.debug("QuadTree.NewsAndFrees.processBatchedNewsAndFrees: starting to process " + frees + " frees, " + news + " news");

			if (workToDo) {
				for (final Map.Entry<Perceiver<ElementType>, PerceiverNewsAndFrees<ElementType>> entry : this.perceiverMap.entrySet()) {
					final Perceiver<ElementType> perceiver = entry.getKey();
					final PerceiverNewsAndFrees<ElementType> newsAndFrees2 = entry.getValue();
					final Integer count = perceiver.processNewsAndFrees(newsAndFrees2, mobilePerceiverOid);
					if (count != null) {
						perceiverOidCount = count;
					}
				}
			}

			log.debug("QuadTree.NewsAndFrees.processBatchedNewsAndFrees: finished processing " + frees + " frees, " + news + " news");
			return perceiverOidCount;
		}
	}
}
