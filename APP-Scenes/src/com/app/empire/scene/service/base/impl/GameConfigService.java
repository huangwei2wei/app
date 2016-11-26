package com.app.empire.scene.service.base.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.scene.constant.SpwanInfoType;
import com.app.empire.scene.dao.impl.BaseLanguageDao;
import com.app.empire.scene.dao.impl.BufferStatusDao;
import com.app.empire.scene.dao.impl.DropInfoDao;
import com.app.empire.scene.dao.impl.DropItemInfoDao;
import com.app.empire.scene.dao.impl.FieldInfoDao;
import com.app.empire.scene.dao.impl.FieldSpawnDao;
import com.app.empire.scene.dao.impl.NpcInfoDao;
import com.app.empire.scene.dao.impl.SkillActioninfoDao;
import com.app.empire.scene.dao.impl.SkillActioninfoMoveDao;
import com.app.empire.scene.dao.impl.SkillBufferDao;
import com.app.empire.scene.dao.impl.SkillPropertyDao;
import com.app.empire.scene.dao.impl.SkillinfoDao;
import com.app.empire.scene.dao.impl.SnareInfoDao;
import com.app.empire.scene.entity.BaseLanguage;
import com.app.empire.scene.entity.BufferStatus;
import com.app.empire.scene.entity.DropInfo;
import com.app.empire.scene.entity.DropItemInfo;
import com.app.empire.scene.entity.FieldInfo;
import com.app.empire.scene.entity.FieldSpawn;
import com.app.empire.scene.entity.NpcInfo;
import com.app.empire.scene.entity.SkillActioninfo;
import com.app.empire.scene.entity.SkillActioninfoMove;
import com.app.empire.scene.entity.SkillBuffer;
import com.app.empire.scene.entity.Skillinfo;
import com.app.empire.scene.entity.SnareInfo;

/**
 * 加载游戏配置
 */

@Service
public class GameConfigService {
	@Autowired
	private BaseLanguageDao baseLanguageDao;// 语言提示包配置
	@Autowired
	private FieldInfoDao fieldInfoDao;// 地图数据
	@Autowired
	private FieldSpawnDao fieldSpawnDao;// 地图孵化数据
	@Autowired
	private SkillinfoDao skillInfoDao;
	@Autowired
	private SkillActioninfoDao skillActioninfoDao;// 地图数据
	@Autowired
	private SkillActioninfoMoveDao skillActioninfoMoveDao;// 地图数据
	@Autowired
	private SkillBufferDao skillBufferDao;// 地图数据
	@Autowired
	private SkillPropertyDao skillPropertyDao;// 地图数据
	@Autowired
	private BufferStatusDao bufferStatusDao;
	@Autowired
	private SnareInfoDao snareInfoDao;

	@Autowired
	private NpcInfoDao npcInfoDao;//
	@Autowired
	private DropInfoDao dropInfoDao;//
	@Autowired
	private DropItemInfoDao dropItemInfoDao;

	private HashMap<Integer, BaseLanguage> gameLanguageConfig = new HashMap<Integer, BaseLanguage>();
	// 所有地图配置
	private HashMap<Integer, FieldInfo> fieldInfoConfig = new HashMap<Integer, FieldInfo>();
	// 副本地图信息
	private HashMap<Integer, HashMap<Integer, FieldInfo>> campaignFieldInfoMaps = new HashMap<Integer, HashMap<Integer, FieldInfo>>();
	// 刷新节点配置
	private Map<Integer, Map<Integer, FieldSpawn>> fieldSpawnMap = new HashMap<Integer, Map<Integer, FieldSpawn>>();
	// 刷怪等数据
	private HashMap<Integer, FieldSpawn> fieldSpawnConfig = new HashMap<Integer, FieldSpawn>();
	// ID标记与节点ID关系表
	private Map<Integer, Integer> tagIdToSpanId = new HashMap<>();
	// 复活点
	private Map<Integer, List<FieldSpawn>> reLiveNodes = new HashMap<>();
	// npc 信息
	public static Map<Integer, NpcInfo> npcInfo = new HashMap<Integer, NpcInfo>();
	// 掉落池
	public static Map<Integer, DropInfo> dropPool = new HashMap<>();
	// 掉落池中物品
	public static Map<Integer, Map<Integer, DropItemInfo>> dropItemMap = new HashMap<>();

	private Map<Integer, Skillinfo> skillTemps = new HashMap<Integer, Skillinfo>();
	private HashMap<Integer, SkillActioninfo> skillActioninfoTemps = new HashMap<Integer, SkillActioninfo>();
	private HashMap<Integer, SkillActioninfoMove> skillActioninfoMoveTemps = new HashMap<Integer, SkillActioninfoMove>();
	private Map<Integer, SkillBuffer> skillBufferTemps = new HashMap<Integer, SkillBuffer>();
	private Map<Integer, BufferStatus> bufferStatusTemps = new HashMap<Integer, BufferStatus>();
	private Map<Integer, SnareInfo> snareInfoTemps = new HashMap<Integer, SnareInfo>();

	/**
	 * 加载配置数据
	 */
	public void load() {
		this.loadLanguageConfig();
		this.loadFieldInfoConfig();
		this.loadFieldSpawnConfig();
		this.loadNpcInfoConfig();
		this.loadDropInfo();
	}

	/**
	 * f 一般格式配置　id->map
	 * 
	 * @param dao
	 * @param clazz
	 */
	private void loadLanguageConfig() {
		gameLanguageConfig.clear();
		List<BaseLanguage> rsl = baseLanguageDao.getAll(BaseLanguage.class);
		for (BaseLanguage object : rsl) {
			gameLanguageConfig.put(object.getId(), object);
		}
	}

	/**
	 * 加载地图配置数据
	 */
	private void loadFieldInfoConfig() {
		fieldInfoConfig.clear();
		List<FieldInfo> rsl = fieldInfoDao.getAll(FieldInfo.class);
		for (FieldInfo f : rsl) {
			fieldInfoConfig.put(f.getMapKey(), f);
			if (f.getType() == 2) {// 副本
				if (!campaignFieldInfoMaps.containsKey(f.getCampaignId())) {
					campaignFieldInfoMaps.put(f.getCampaignId(), new HashMap<Integer, FieldInfo>());
				}
				HashMap<Integer, FieldInfo> container = campaignFieldInfoMaps.get(f.getCampaignId());
				container.put(f.getCampaignIndex(), f);
			}
		}
	}

	/**
	 * 加载地图中的对象数据配置
	 */
	private void loadFieldSpawnConfig() {
		fieldSpawnConfig.clear();
		List<FieldSpawn> rsl = fieldSpawnDao.getAll(FieldSpawn.class);
		for (FieldSpawn spawn : rsl) {
			fieldSpawnConfig.put(spawn.getId(), spawn);
			// 全部节点
			if (!fieldSpawnMap.containsKey(spawn.getMapid())) {
				fieldSpawnMap.put(spawn.getMapid(), new HashMap<Integer, FieldSpawn>());
			}
			fieldSpawnMap.get(spawn.getMapid()).put(spawn.getId(), spawn);
			tagIdToSpanId.put(spawn.getTagId(), spawn.getId());

			// 地图存放点
			if (spawn.getEntityType() == SpwanInfoType.REVIVAL_NODE) {
				List<FieldSpawn> list = reLiveNodes.get(spawn.getMapid());
				if (list == null) {
					list = new ArrayList<>();
					reLiveNodes.put(spawn.getMapid(), list);
				}
				list.add(spawn);
			}
		}
	}

	/**
	 * 加载npc 数据
	 */
	public void loadNpcInfoConfig() {
		npcInfo.clear();
		List<NpcInfo> rsl = npcInfoDao.getAll(FieldSpawn.class);
		for (NpcInfo npc : rsl) {
			npcInfo.put(npc.getNpcId(), npc);
		}
	}

	/**
	 * 加载掉落池
	 */
	public void loadDropInfo() {
		dropPool.clear();
		List<DropInfo> rsl = dropInfoDao.getAll(DropInfo.class);
		for (DropInfo obj : rsl) {
			dropPool.put(obj.getId(), obj);
		}
	}

	/**
	 * 加载掉落物品
	 */
	public void loadDropItemInfo() {
		dropItemMap.clear();
		List<DropItemInfo> rsl = dropItemInfoDao.getAll(DropItemInfo.class);
		for (DropItemInfo obj : rsl) {
			Map<Integer, DropItemInfo> pool = dropItemMap.get(obj.getPoolId());
			if (pool == null) {
				pool = new HashMap<Integer, DropItemInfo>();
				dropItemMap.put(obj.getPoolId(), pool);
			}
			pool.put(obj.getId(), obj);
		}
	}

	/**
	 * 加载技能和buffer数据
	 */
	public void loadskillInfo() {
		/* 技能 */
		skillTemps.clear();
		List<Skillinfo> skillInfo = skillInfoDao.getAll(Skillinfo.class);
		for (Skillinfo obj : skillInfo) {
			skillTemps.put(obj.getTemplateId(), obj);
		}

		/* 技能action */
		skillActioninfoTemps.clear();
		List<SkillActioninfo> rsl = skillActioninfoDao.getAll(SkillActioninfo.class);
		for (SkillActioninfo obj : rsl) {
			skillActioninfoTemps.put(obj.getTemplateId(), obj);
		}
		/* 攻击移动 */
		skillActioninfoMoveTemps.clear();
		List<SkillActioninfoMove> rsl2 = skillActioninfoMoveDao.getAll(SkillActioninfoMove.class);
		for (SkillActioninfoMove obj : rsl2) {
			skillActioninfoMoveTemps.put(obj.getId(), obj);
		}
		/* 技能buffer */
		skillBufferTemps.clear();
		List<SkillBuffer> rsl3 = skillBufferDao.getAll(SkillBuffer.class);
		for (SkillBuffer obj : rsl3) {
			skillBufferTemps.put(obj.getTemplateId(), obj);
		}
		/* buffer 附的人物状态 */
		bufferStatusTemps.clear();
		List<BufferStatus> rsl4 = skillBufferDao.getAll(BufferStatus.class);
		for (BufferStatus obj : rsl4) {
			bufferStatusTemps.put(obj.getId(), obj);
		}
		/* 加载陷阱 */
		snareInfoTemps.clear();
		List<SnareInfo> rsl5 = snareInfoDao.getAll(SnareInfo.class);
		for (SnareInfo obj : rsl5) {
			snareInfoTemps.put(obj.getId(), obj);
		}
	}

	public HashMap<Integer, FieldInfo> getFieldInfoConfig() {
		return fieldInfoConfig;
	}

	public HashMap<Integer, FieldSpawn> getFieldSpawnConfig() {
		return fieldSpawnConfig;
	}

	public Map<Integer, Map<Integer, FieldSpawn>> getFieldSpawnMap() {
		return fieldSpawnMap;
	}

	public static Map<Integer, NpcInfo> getNpcInfo() {
		return npcInfo;
	}

}
