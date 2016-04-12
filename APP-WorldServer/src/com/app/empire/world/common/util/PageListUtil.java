package com.app.empire.world.common.util;

import java.util.ArrayList;
import java.util.List;

public class PageListUtil<T> {

	public List<T> getList(int startNum, int pageSize, List<T> list) {
		List<T> values = new ArrayList<T>();
		int currentPage = (startNum - 1) * pageSize;
		int pageSizes = 0;
		if (currentPage == 0) {
			pageSizes = pageSize;
		} else {
			pageSizes = pageSize * startNum;
		}

		if (list.size() > pageSizes) {
			for (int i = currentPage; i < pageSizes; i++) {
				values.add(list.get(i));
			}
		} else {
			for (int i = currentPage; i < list.size(); i++) {
				values.add(list.get(i));
			}
		}
		return values;
	}
}
