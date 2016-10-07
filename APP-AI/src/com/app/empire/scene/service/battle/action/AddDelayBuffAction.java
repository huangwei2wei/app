package com.app.empire.scene.service.battle.action;

import java.util.ArrayList;
import java.util.List;

import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.buffer.BufferFactory;
import com.chuangyou.xianni.battle.buffer.BufferTargetType;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.exec.DelayAction;
import com.chuangyou.xianni.role.objects.Living;

public class AddDelayBuffAction extends DelayAction {
	Living					source;
	List<Living>			targets;
	SkillBufferTemplateInfo	temp;

	public AddDelayBuffAction(Living source, List<Living> targets, SkillBufferTemplateInfo temp) {
		super(source, temp.getDelay());
		this.source = source;
		this.targets = targets;
		this.temp = temp;
	}

	public AddDelayBuffAction(Living source, Living target, SkillBufferTemplateInfo temp) {
		super(source, temp.getDelay());
		this.source = source;
		this.temp = temp;
		targets = new ArrayList<>();
		targets.add(target);
	}

	@Override
	public void execute() {
		if (temp.getTargetType() == BufferTargetType.SOURCE) {
			Buffer buff = BufferFactory.createBuffer(source, source, temp);
			source.addBuffer(buff);
		}
		if (temp.getTargetType() == BufferTargetType.SKILL_TARGET) {
			for (Living target : targets) {
				Buffer buff = BufferFactory.createBuffer(source, target, temp);
				buff.setPermanent(false);
				target.addBuffer(buff);
			}
		}
	}

}
