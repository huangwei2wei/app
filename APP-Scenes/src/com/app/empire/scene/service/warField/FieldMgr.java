package com.app.empire.scene.service.warField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.FieldInfo;
import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.scene.constant.SpwanInfoType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.warField.action.FieldPollingAction;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.helper.ParseMapDataHelper;
import com.app.empire.scene.service.warField.navi.seeker.NavmeshSeeker;
import com.app.empire.scene.service.warField.spawn.ActiveSpwanNode;
import com.app.empire.scene.service.warField.spawn.GatherSpawnNode;
import com.app.empire.scene.service.warField.spawn.MonsterSpawnNode;
import com.app.empire.scene.service.warField.spawn.NpcSpawnNode;
import com.app.empire.scene.service.warField.spawn.PerareState;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.warField.spawn.TimeControlerNodeMgr;
import com.app.empire.scene.service.warField.spawn.TouchPointSpwanNode;
import com.app.empire.scene.service.warField.spawn.TriggerPointSpwanNode;
import com.app.empire.scene.service.warField.spawn.WorkingState;
import com.app.empire.scene.util.FileOperate;
import com.app.empire.scene.util.Rect;
import com.app.empire.scene.util.engine.DelayAction;

public class FieldMgr {
	private Logger log = Logger.getLogger(FieldMgr.class);
	private static FieldMgr ins = new FieldMgr();

	public static FieldMgr getIns() {
		return ins;
	}

	/**
	 * 寻路模板数据
	 */
	private static Map<String, NavmeshSeeker> _seekersTemp = new HashMap<String, NavmeshSeeker>();

	/**
	 * 地图边界配置
	 */
	private static Map<String, Rect> _mapBounds = new HashMap<String, Rect>();

	/**
	 * 获取一个地图的查看器
	 * 
	 * @param fieldName
	 * @return
	 */
	public NavmeshSeeker GetSeekerTemp(String fieldName) {
		if (_seekersTemp.containsKey(fieldName)) {
			return _seekersTemp.get(fieldName);
		}
		return null;
	}

	/**
	 * 获取一个地图的边界
	 * 
	 * @param fieldName
	 * @return
	 */
	public Rect GetBound(String fieldName) {
		if (_mapBounds.containsKey(fieldName)) {
			return _mapBounds.get(fieldName);
		}
		return null;
	}

	/**
	 * 创建数据对象
	 */
	public boolean initilize() {
		// 初始化地图
		if (!initField()) {
			return false;
		}
		createStateField();
		return true;
	}

	/**
	 * 场景静态地图
	 */
	private boolean initField() {
		String filePath = Thread.currentThread().getContextClassLoader().getResource("mapData/").getPath();
		// String ROOT = ServiceManager.getManager().getConfiguration().getString("mapdata");
		File f = new File(filePath);
		Map<String, String> realNameMaping = new HashMap<>();
		if (f.isDirectory()) {
			String[] fileNames = f.list();
			for (String str : fileNames) {
				realNameMaping.put(str.toLowerCase(), str);
			}
		}
		for (Map.Entry<Integer, FieldInfo> entry : ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().entrySet()) {
			if (!_seekersTemp.containsKey(entry.getValue().getResName())) {
				String sonFileName = entry.getValue().getResName() + ".txt";
				String realFileName = realNameMaping.get(sonFileName.toLowerCase());
				File configFile = new File(filePath + realFileName);
				System.out.println(filePath + realFileName);
				if (configFile.exists()) {
					String name = configFile.getName();
					String configName = name.split("[.]")[0];
					try {
						byte[] data = FileOperate.read2Bytes(configFile.getAbsolutePath());
						NavmeshSeeker seeker = ParseMapDataHelper.parse2Seeker(data);
						Rect rect = seeker.getRect().clone();
						_mapBounds.put(configName.toLowerCase(), rect);
						_seekersTemp.put(configName.toLowerCase(), seeker);
					} catch (IOException e) {
						log.error("初始化场景数据错误", e);
						e.printStackTrace();
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 创建静态地图
	 */
	private void createStateField() {
		HashMap<Integer, FieldInfo> fieldInfoConfig = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig();
		for (Map.Entry<Integer, FieldInfo> entry : fieldInfoConfig.entrySet()) {
			if (entry.getValue().getType() == 1) { // 公共/静态地图
				initCreateField(entry.getValue().getMapKey(), entry.getValue().getType());
			}
		}
	}

	// /所有地图的集合
	private static ConcurrentHashMap<Integer, Field> fields = new ConcurrentHashMap<Integer, Field>();

	public Field getField(int fid) {
		if (fields.containsKey(fid))
			return fields.get(fid);
		return null;
	}

	/**
	 * 获取指定mapkey的地图列表(副本地图相同key的地图可能会创建多个)
	 * 
	 * @param mapKey
	 * @return
	 */
	public List<Field> getFieldsByMapKey(int mapKey) {
		List<Field> resultList = new ArrayList<>();

		Iterator<Entry<Integer, Field>> it = fields.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Field> entry = it.next();
			if (entry.getValue().getMapKey() == mapKey) {
				resultList.add(entry.getValue());
			}
		}

		return resultList;
	}

	/**
	 * 创建副本地图
	 * 
	 * @param mapkey
	 * @return
	 */
	public Field createCampaignField(int mapkey, short type, int campaignId) {
		Field f = new Field();
		f.setCampaignId(campaignId);
		if (type == 1) { // 静态地图id直接由mapkey
			f.id = mapkey;
		} else {
			f.id = IDMakerHelper.nextFieldId();// 动态地图由100000+ 生成 //
			// //IdUtilFacotry.getIdUtil(mapkey).nextId();
			// notice2Center(f.id, mapkey);
			// 创建/销毁一个动态地图，需要通知center服务器创建一个镜像
		}
		f.setMapKey(mapkey);
		fields.put(f.id, f);
		spwanInit(f);
		// Log.error("-初始化地图，mapKey = " + mapkey+" f.id: "+f.id);

		// f.enDelayQueue(new FieldPollingAction(f));

		return f;
	}

	/** 服务器启动时，创建初始化地图 */
	public Field initCreateField(int mapkey, short type) {
		Field f = new Field();
		f.setMapKey(mapkey);
		f.id = mapkey;
		fields.put(f.id, f);
		spwanInit(f);
		log.error("初始化地图，mapKey = " + mapkey);
		// f.enDelayQueue(new FieldPollingAction(f));
		DelayAction action = new FieldPollingAction(f);
		action.startWithFixedDelay();

		return f;
	}

	protected void spwanInit(Field f) {
		Map<Integer, FieldSpawn> spawnInfos = ServiceManager.getManager().getGameConfigService().getFieldSpawnMap().get(f.getMapKey());
		if (spawnInfos == null || spawnInfos.size() == 0) {
			log.error("map has not anly spawnInfo ,the mapKey is:" + f.getMapKey());
			return;
		}
		for (FieldSpawn sf : spawnInfos.values()) {
			SpwanNode node = null;
			// System.out.println(sf.getEntityType());
			switch (sf.getEntityType()) {
			case SpwanInfoType.MONSTER:
				node = new MonsterSpawnNode(sf, f);
				break;
			case SpwanInfoType.NPC:
				node = new NpcSpawnNode(sf, f);
				break;
			case SpwanInfoType.TRANSPOINT:
				node = new TouchPointSpwanNode(sf, f);
				break;
			case SpwanInfoType.GATHER_POINT:
				node = new GatherSpawnNode(sf, f);
				break;
			case SpwanInfoType.TASK_TRIGGER:
				node = new TriggerPointSpwanNode(sf, f);
				break;
			case SpwanInfoType.COMMON_TRIGGER:
				node = new ActiveSpwanNode(sf, f);
				break;
			// case SpwanInfoType.BOSS_ELITE:
			// node = new EliteBossSpawnNode(sf, f);
			// break;
			// case SpwanInfoType.BOSS_WORLD:
			// node = new WorldBossSpawnNode(sf, f);
			// break;
			default:
				node = new SpwanNode(sf, f);
			}
			f.addSpawnNode(node);
			TimeControlerNodeMgr.addNode(node);
			node.build();
			if (node.getSpawnInfo().getInitStatu() == 1) {
				node.stateTransition(new WorkingState(node));
			} else {
				node.stateTransition(new PerareState(node));
			}
		}
	}

	public boolean clear(int id) {
		synchronized (fields) {
			fields.remove(id);
		}
		return true;
	}

	// 地图创建成功，通知center服务器创建代理
	// private void notice2Center(int id, int mapKey) {
	// PostionMsg.Builder builder = PostionMsg.newBuilder();
	// builder.setMapId(id);
	// builder.setMapKey(mapKey);
	//
	// PBMessage message = MessageUtil.buildMessage(Protocol.C_SCENE_CREATE_MAP, builder.build());
	// GatewayLinkedSet.send2Server(message);
	//
	// }
}
