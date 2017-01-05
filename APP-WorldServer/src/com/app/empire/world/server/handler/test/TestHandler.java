package com.app.empire.world.server.handler.test;

import java.util.List;

import com.app.empire.protocol.data.test.ItemVo;
import com.app.empire.protocol.data.test.Test;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class TestHandler implements IDataHandler {
	public void handle(AbstractData data) throws Exception {
		Test test = (Test) data;
		int a = test.getA();
		System.out.println(test.isC() + "---------");
		List<ItemVo> vos = test.getM();
		System.out.println("itemVo ---size:::" + vos.size());

		for (ItemVo itemVo : vos) {
			System.out.println("itemVo ---" + itemVo.getA() + "---" + itemVo.getB() + "---" + itemVo.isC() + "---" + itemVo.getD() + "---" + itemVo.getE() + "---" + itemVo.getF() + "---"
					+ itemVo.getH() + "---" + itemVo.getI() + "---" + itemVo.getJ() + "---" + itemVo.getK() + "---" + itemVo.getL()+"--"+itemVo.getVo2().getE());
//			short[] s = itemVo.getL();
//			for (int i = 0; i < s.length; i++) {
//				System.out.println(s[i]);
//			}
		}
		
		ItemVo vo = test.getItemVo();
		System.out.println(vo.getE()+"-----"+vo.getVo2().getE());
	}
}
