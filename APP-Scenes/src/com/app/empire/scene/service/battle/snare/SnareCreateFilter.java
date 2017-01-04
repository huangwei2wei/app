package com.app.empire.scene.service.battle.snare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnareCreateFilter {
	private static Map<Long, SkillRecord> records = new ConcurrentHashMap<Long, SnareCreateFilter.SkillRecord>();

	/** 施法 */
	public static void casting(long livingId, int skillId, long indexId) {
		SkillRecord record = new SkillRecord();
		record.setLivingId(livingId);
		record.setSkillId(skillId);
		record.setIndexId(indexId);
		records.put(indexId, record);
	}

	/** 检查是否允许创建 */
	public static boolean checkFilter(long livingId, long indexId, int snareId) {
		if (records.containsKey(indexId)) {
			records.remove(indexId);
		}
		return true;
	}

	public static void clearExiped() {
		List<SkillRecord> all = new ArrayList<>();
		all.addAll(records.values());
		for (SkillRecord record : all) {
			if (record.getCreateTime() <= System.currentTimeMillis() - 5000) {
				records.remove(record.getIndexId());
			}
		}
	}

	static class SkillRecord {
		private long	livingId;
		private int		skillId;
		private long	indexId;
		private long	createTime;

		public SkillRecord() {
			this.createTime = System.currentTimeMillis();
		}

		public long getLivingId() {
			return livingId;
		}

		public void setLivingId(long livingId) {
			this.livingId = livingId;
		}

		public int getSkillId() {
			return skillId;
		}

		public void setSkillId(int skillId) {
			this.skillId = skillId;
		}

		public long getIndexId() {
			return indexId;
		}

		public void setIndexId(long indexId) {
			this.indexId = indexId;
		}

		public long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}

	}

}
