package com.app.empire.scene.service.warfield.cmd;

import com.chuangyou.common.protobuf.pb.army.PetInfoProto.PetInfoMsg;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_PET_INFO_UPDATE, desc = "宠物信息更新")
public class PetInfoUpdate extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub
		PetInfoMsg req = PetInfoMsg.parseFrom(packet.getBytes());
		
		army.updatePet(req);
	}

}
