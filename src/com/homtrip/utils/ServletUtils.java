package com.homtrip.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.homtrip.common.Page;
import com.homtrip.model.HotelJoinVO;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-9 上午10:10:30
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ServletUtils {

	private static final String PAGE_SIZE = "5";

	/**
	 * 获取分页信息
	 * @param request
	 * @return
	 */
	public static Page<HotelJoinVO> getPageInfo(HttpServletRequest request) {
		String size = request.getParameter("page.size");
		size = StringUtils.isBlank(size) ? PAGE_SIZE : size;
		String start = request.getParameter("page.start");
		start = StringUtils.isBlank(start) ? "0" : start;
		// 翻页
		String no = request.getParameter("page.pageNo");
		Page<HotelJoinVO> p = new Page<HotelJoinVO>(Integer.parseInt(start), Integer.parseInt(size));
		if (StringUtils.isNotBlank(no)) {
			p.setPageNo(Integer.parseInt(no));
		}
		return p;
	}

	public static Map<String, Object> getSearchMap(Object vo) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Map<String, Object> searchMap = new HashMap<String, Object>();
		Map m = BeanUtils.describe(vo);
		Set<String> keySet = m.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (m.get(key) == null || StringUtils.isBlank(m.get(key).toString())) {
				iterator.remove();
			}
		}
		m.put("model", vo);
		searchMap.putAll(m);
		return searchMap;
	}

}
