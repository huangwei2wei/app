//package com.app.empire.scene.service.warField.spawn;
//
//import com.app.empire.scene.service.role.objects.Living;
//import com.app.empire.scene.service.role.objects.Player;
//import com.app.empire.scene.service.warField.field.Field;
//import com.app.empire.scene.service.world.ArmyProxy;
//import com.app.empire.scene.service.world.WorldMgr;
//
//public abstract class FieldBossSpawnNode extends MonsterSpawnNode {
//	
//	protected FieldBoss monster;
//
//	public FieldBossSpawnNode(SpawnInfo info, Field field) {
//		super(info, field);
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	public void reset() {
//		super.reset();
//	}
//	
//	@Override
//	public void stateTransition(NodeState state) {
//		super.stateTransition(state);
//	}
//	
//	@Override
//	public void prepare() {
//		
//	}
//	
//	@Override
//	public void start() {
//		// TODO Auto-generated method stub
//		if(state.getCode() == NodeState.WORK){
//			createBoss();
//			
//			bornNotice();
//		}
//	}
//	
//	@Override
//	public void over() {
//		// TODO Auto-generated method stub
//	}
//	
//	@Override
//	public void lvingDie(Living living) {
//		// TODO Auto-generated method stub
//		if(field != null){
//			field.addDeathLiving(living);
//			stateTransition(new OverState(this));
//			
//			this.dieTrigger(living);
//			this.monster = null;
//			
//			FieldBossHelper.bossKillAward(this, (FieldBoss)living);
//		}
//	}
//	
//	/**
//	 * 刷新公告
//	 */
//	protected void bornNotice(){
//		FieldBossCfg bossCfg = getBossCfg();
//		if(bossCfg.getBornNotice() > 0){
//			NoticeCfg noticeCfg = NoticeTemplateMgr.getNoticeCfg(bossCfg.getBornNotice());
//			
//			if(noticeCfg == null || noticeCfg.getNoticeClose() > 0){
//				return;
//			}
//			
//			if(this.monster == null || this.monster.getMonsterInfo() == null || this.field == null || this.field.getFieldInfo() == null){
//				return;
//			}
//			String content = noticeCfg.getContent();
//			content = content.replaceAll("@bossName@", this.monster.getMonsterInfo().getName());
//			content = content.replaceAll("@mapName@", this.field.getFieldInfo().getName());
//			
//			ChatManager.sendNotice(noticeCfg.getChannel(), noticeCfg.getNotifyRange(), field, content);
//		}
//	}
//	
//	/**
//	 * 死亡公告
//	 */
//	protected void dieNotice(NoticeCfg noticeCfg, String content){
//		if(noticeCfg == null || noticeCfg.getNoticeClose() > 0){
//			return;
//		}
//		if(this.monster == null || this.monster.getMonsterInfo() == null || this.field == null || this.field.getFieldInfo() == null){
//			return;
//		}
//		DamageStatistic statistic = this.monster.getStatistic();
//		if(statistic == null || statistic.getKiller() == null){
//			return;
//		}
//		Player player;
//		if(statistic.getKiller() instanceof Player){
//			player = (Player)statistic.getKiller();
//		}else{
//			ArmyProxy army = WorldMgr.getArmy(statistic.getKiller().getArmyId());
//			if(army == null){
//				return;
//			}
//			player = army.getPlayer();
//		}
//		content = content.replaceAll("@playerName@", player.getSimpleInfo().getNickName());
//		content = content.replaceAll("@bossName@", monster.getMonsterInfo().getName());
//		content = content.replaceAll("@mapName@", field.getFieldInfo().getName());
//		
//		ChatManager.sendNotice(noticeCfg.getChannel(), noticeCfg.getNotifyRange(), field, content);
//	}
//	
//	public abstract FieldBossCfg getBossCfg();
//	
//	/**
//	 * 刷新BOSS
//	 */
//	protected abstract void createBoss();
//	
//	/**
//	 * 死亡触发事件
//	 * @param living
//	 */
//	protected abstract void dieTrigger(Living living);
//
//}
