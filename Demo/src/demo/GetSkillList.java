package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * 
 * @author doter
 * 
 */
public class GetSkillList {
	private int[] heroId;// 要获取英雄技能的id
	private List<HashMap> list;

	public int[] getHeroId() {
		return heroId;
	}
	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}
	public List<HashMap> getList() {
		return list;
	}
	public void setList(List<HashMap> list) {
		this.list = list;
	}

}
