package com.app.empire.scene.service.battle.action;

import java.util.ArrayList;
import java.util.List;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.battle.buffer.BufferTargetType;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.util.exec.DelayAction;

public class AddDelayBuffAction extends DelayAction {
	Living					source;
	List<Living>			targets;
	SkillBuffer 	temp;

	public AddDelayBuffAction(Living source, List<Living> targets, SkillBuffer temp) {
		super(source, temp.getDelay());
		this.source = source;
		this.targets = targets;
		this.temp = temp;
	}

	public AddDelayBuffAction(Living source, Living target, SkillBuffer temp) {
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
