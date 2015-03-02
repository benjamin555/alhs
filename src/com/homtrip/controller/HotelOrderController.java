package com.homtrip.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.homtrip.common.Page;
import com.homtrip.common.model.CodeItem;
import com.homtrip.common.model.CodeItemKey;
import com.homtrip.common.service.ICodeItemService;
import com.homtrip.model.HotelOrderVO;
import com.homtrip.model.HotelOrderVO.Status;
import com.homtrip.model.HotelSummary;
import com.homtrip.model.HouseVO;
import com.homtrip.service.IHotelOrderService;
import com.homtrip.service.IHouseService;
import com.homtrip.utils.ServletUtils;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-10 下午4:22:35
* @email benjaminchen555@gmail.com
*/
@Controller
@RequestMapping("/hotelorder")
public class HotelOrderController {
	
	@Autowired
	private IHotelOrderService service;
	@Autowired
	private ICodeItemService codeItemService;
	@Autowired
	private IHouseService houseService;
	
	
	@RequestMapping(value = "/list")
	public ModelAndView  list( HotelOrderVO vo, HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView("/order/list");
		//获取各个状态的订单数目
		Map<Status, Long> orderCountMap = service.getOrderCountMap(vo);
		Map<String, String> oMap = new HashMap<String, String>();
		Set<Status> kSet = orderCountMap.keySet();
		Long total = 0L;
		for (Iterator<Status> iterator = kSet.iterator(); iterator.hasNext();) {
			Status status = iterator.next();
			Long count = orderCountMap.get(status);
			oMap.put(status.toString(), count.toString());
			if (status!=Status.noShow) {
				total+=count;
			}
		}
		
		oMap.put("total", total.toString() );
		modelAndView.addObject("orderCountMap",oMap);
		
		//添加订单来源
		List<CodeItem> sourceList = codeItemService.getListById(71);
		modelAndView.addObject("sourceList",sourceList);
		
		
		return modelAndView ;
	}
	
	@RequestMapping(value = "/listRight")
	public ModelAndView listRight(HotelOrderVO vo,HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView("/order/list_right");
		Page p = ServletUtils.getPageInfo(request);
		Map<String, Object> searchMap = ServletUtils.getSearchMap(vo);
		Page<HotelOrderVO> page = service.getPage(p.getStart(), p.getPageSize(), searchMap ) ;
		modelAndView.addObject("page", page);
		return modelAndView;
	}
	
	@RequestMapping(value = "/listLeft")
	public String listLeft(HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		
		String id = request.getParameter("guid");
		String hotelName = request.getParameter("house.hotelName");
		if (StringUtils.isNotBlank(hotelName)) {
			HotelSummary v1  = service.getSummary(hotelName);
			modelMap.addAttribute("summary",v1);
		}
		
		if (StringUtils.isNotBlank(id)) {
			HotelOrderVO v  = service.getByGuid(id);
			modelMap.addAttribute("item",v);
			
			//设置来源
			CodeItemKey codeItemKey = new CodeItemKey();
			codeItemKey.setId(71);
			codeItemKey.setCode(v.getDdsx());
			CodeItem codeItem = codeItemService.getBy(codeItemKey);
			v.setSource(codeItem.getItemname());
			
		}
		
		
		
		return "/order/list_left";
	}
	
	@RequestMapping(value = "/fangtai/detail")
	public String fDetail(HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		String id = request.getParameter("guid");
		if (StringUtils.isNotBlank(id)) {
			HotelOrderVO v  = service.getByGuid(id);
			
			//设置来源
			CodeItemKey codeItemKey = new CodeItemKey();
			codeItemKey.setId(71);
			codeItemKey.setCode(v.getDdsx());
			CodeItem codeItem = codeItemService.getBy(codeItemKey);
			v.setSource(codeItem.getItemname());
			
			modelMap.addAttribute("item",v);
			
			
		}
		return "/order/frame/fangtai_detail";
	}
	
	@RequestMapping(value = "/fangtai/clean")
	public String fClean(HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		String id = request.getParameter("house.guid");
		HouseVO v = houseService.getById(id);
		modelMap.addAttribute("item",v);
		
		
		return "/order/frame/fangtai_clean";
	}
	
	@RequestMapping(value = "/fangtai/yili")
	public String fYili(HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		
		
		
		return "/order/frame/fangtai_yili";
	}
	

}
