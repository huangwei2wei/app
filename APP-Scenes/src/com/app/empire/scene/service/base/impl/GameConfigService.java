package com.app.empire.scene.service.base.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.db.mysql.dao.AiConfigDao;
import com.app.db.mysql.dao.BaseLanguageDao;
import com.app.db.mysql.dao.BufferStatusDao;
import com.app.db.mysql.dao.CampaignInfoDao;
import com.app.db.mysql.dao.CampaignTaskInfoDao;
import com.app.db.mysql.dao.DropInfoDao;
import com.app.db.mysql.dao.DropItemInfoDao;
import com.app.db.mysql.dao.FieldInfoDao;
import com.app.db.mysql.dao.FieldSpawnDao;
import com.app.db.mysql.dao.HeroStatusDao;
import com.app.db.mysql.dao.MonsterInfoDao;
import com.app.db.mysql.dao.NpcInfoDao;
import com.app.db.mysql.dao.SkillActioninfoDao;
import com.app.db.mysql.dao.SkillActioninfoMoveDao;
import com.app.db.mysql.dao.SkillBufferDao;
import com.app.db.mysql.dao.SkillPropertyDao;
import com.app.db.mysql.dao.SkillinfoDao;
import com.app.db.mysql.dao.SnareInfoDao;
import com.app.db.mysql.entity.AiConfig;
import com.app.db.mysql.entity.BaseLanguage;
import com.app.db.mysql.entity.BufferStatus;
import com.app.db.mysql.entity.CampaignInfo;
import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.db.mysql.entity.DropInfo;
import com.app.db.mysql.entity.DropItemInfo;
import com.app.db.mysql.entity.FieldInfo;
import com.app.db.mysql.entity.FieldSpawn;
import com.app.db.mysql.entity.HeroStatus;
import com.app.db.mysql.entity.MonsterInfo;
import com.app.db.mysql.entity.NpcInfo;
import com.app.db.mysql.entity.SkillActioninfo;
import com.app.db.mysql.entity.SkillActioninfoMove;
import com.app.db.mysql.entity.SkillBuffer;
import com.app.db.mysql.entity.SkillInfo;
import com.app.db.mysql.entity.SnareInfo;
import com.app.empire.scene.constant.SpwanInfoType;

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
	@Autowired
	private MonsterInfoDao monsterInfoDao;
	@Autowired
	private HeroStatusDao heroStatusDao;
	@Autowired
	private AiConfigDao aiConfigDao;
	@Autowired
	private CampaignInfoDao campaignInfoDao;
	@Autowired
	private CampaignTaskInfoDao campaignTaskInfoDao;

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
	private Map<Integer, NpcInfo> npcInfo = new HashMap<Integer, NpcInfo>();
	// 掉落池
	private Map<Integer, DropInfo> dropPool = new HashMap<>();
	// 掉落池中物品
	private Map<Integer, Map<Integer, DropItemInfo>> dropItemMap = new HashMap<>();
	// 英雄状态
	private Map<Integer, HeroStatus> heroStatus = new HashMap<Integer, HeroStatus>();

	private Map<Integer, SkillInfo> skillTemps = new HashMap<Integer, SkillInfo>();
	private HashMap<Integer, SkillActioninfo> skillActioninfoTemps = new HashMap<Integer, SkillActioninfo>();
	private HashMap<Integer, SkillActioninfoMove> skillActioninfoMoveTemps = new HashMap<Integer, SkillActioninfoMove>();
	private Map<Integer, SkillBuffer> skillBufferTemps = new HashMap<Integer, SkillBuffer>();
	private Map<Integer, BufferStatus> bufferStatusTemps = new HashMap<Integer, BufferStatus>();
	private Map<Integer, SnareInfo> snareInfoTemps = new HashMap<Integer, SnareInfo>();
	private Map<Integer, MonsterInfo> monsterInfoTemps = new HashMap<Integer, MonsterInfo>();
	// ai 配置
	private Map<Integer, AiConfig> aiConfigs = new HashMap<Integer, AiConfig>();
	// 副本配置
	private Map<Integer, CampaignInfo> campaignTemps = new HashMap<>();
	// 副本任务
	private Map<Integer, CampaignTaskInfo> campaignTasks = new HashMap<>();

	/**
	 * 加载配置数据
	 */
	public void load() {
		this.loadLanguageConfig();
		this.loadFieldInfoConfig();
		this.loadFieldSpawnConfig();
		this.loadNpcInfoConfig();
		this.loadDropInfo();
		this.loadDropItemInfo();
		this.loadskillInfo();
		this.loadMonsterInfo();
		this.loadHeroStatus();
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
		List<NpcInfo> rsl = npcInfoDao.getAll(NpcInfo.class);
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
		List<SkillInfo> skillInfo = skillInfoDao.getAll(SkillInfo.class);
		for (SkillInfo obj : skillInfo) {
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

	public void loadMonsterInfo() {
		monsterInfoTemps.clear();
		List<MonsterInfo> rsl = monsterInfoDao.getAll(MonsterInfo.class);
		for (MonsterInfo monsterInfo : rsl) {
			monsterInfoTemps.put(monsterInfo.getMonsterId(), monsterInfo);
		}
	}

	public void loadHeroStatus() {
		heroStatus.clear();
		List<HeroStatus> rsl = monsterInfoDao.getAll(HeroStatus.class);
		for (HeroStatus status : rsl) {
			heroStatus.put(status.getId(), status);
		}
	}

	public void loadAiConfigs() {
		aiConfigs = aiConfigDao.loadAiConfig();
	}

	public void loadCampaignInfo() {
		campaignTemps = campaignInfoDao.loadCampaignInfo();
	}

	public void loadCampignTaskTemp() {
		campaignTasks = campaignTaskInfoDao.loadCampaignTaskInfo();
	}

	public HashMap<Integer, FieldInfo> getFieldInfoConfig() {
		return fieldInfoConfig;
	}

	public HashMap<Integer, HashMap<Integer, FieldInfo>> getCampaignFieldInfoMaps() {
		return campaignFieldInfoMaps;
	}

	public Map<Integer, List<FieldSpawn>> getReLiveNodes() {
		return reLiveNodes;
	}

	public HashMap<Integer, FieldSpawn> getFieldSpawnConfig() {
		return fieldSpawnConfig;
	}

	public Map<Integer, Map<Integer, FieldSpawn>> getFieldSpawnMap() {
		return fieldSpawnMap;
	}

	public Map<Integer, MonsterInfo> getMonsterInfoTemps() {
		return monsterInfoTemps;
	}

	public Map<Integer, NpcInfo> getNpcInfo() {
		return npcInfo;
	}

	public Map<Integer, HeroStatus> getHeroStatus() {
		return heroStatus;
	}

	public Map<Integer, Integer> getTagIdToSpanId() {
		return tagIdToSpanId;
	}

	public Map<Integer, AiConfig> getAiConfigs() {
		return aiConfigs;
	}

	public Map<Integer, CampaignInfo> getCampaignTemps() {
		return campaignTemps;
	}

	public Map<Integer, CampaignTaskInfo> getCampaignTasks() {
		return campaignTasks;
	}

	public Map<Integer, SkillBuffer> getSkillBufferTemps() {
		return skillBufferTemps;
	}

	public Map<Integer, SkillInfo> getSkillTemps() {
		return skillTemps;
	}

	public HashMap<Integer, SkillActioninfo> getSkillActioninfoTemps() {
		return skillActioninfoTemps;
	}

	public Map<Integer, SnareInfo> getSnareInfoTemps() {
		return snareInfoTemps;
	}

	public Map<Integer, DropInfo> getDropPool() {
		return dropPool;
	}

	public Map<Integer, Map<Integer, DropItemInfo>> getDropItemMap() {
		return dropItemMap;
	}

}
