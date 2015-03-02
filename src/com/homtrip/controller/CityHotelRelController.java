package com.homtrip.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.homtrip.common.JsonUtils;
import com.homtrip.common.Page;
import com.homtrip.model.CityHotelRelVO;
import com.homtrip.model.CityHotelRelVO.Type;
import com.homtrip.service.ICityHotelRelService;
import com.homtrip.utils.ServletUtils;
/**
 * 
 * @author hh
 * 2015.2.6
 * 城市地标
 */
@Controller
@RequestMapping("/cityhotel")
public class CityHotelRelController {
	private Logger  logger = LoggerFactory.getLogger(getClass());
	/**
	 * 城市地标接口端service
	 */
	@Resource
	private ICityHotelRelService cityHotelRelService;
	
	/**
	 * 获取城市地标view接口
	 */
	
	@RequestMapping(value = "/cityHotelRelInfo")
	public String getHotelCusInfo(HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		Type[] types = CityHotelRelVO.Type.values();
		List<String> texts = new ArrayList<String>();
		for (int i = 0; i < types.length; i++) {
			texts.add(types[i].getText());
		}
		modelMap.addAttribute("typeList",texts);
		return "/setting/rel/cityLandmark";
	}
	
	
	@SuppressWarnings({"rawtypes" })
	@RequestMapping(value = "/relList")
	public ModelAndView  relList( CityHotelRelVO vo, HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView("/setting/rel/cityLandmark_list");
		Page p = ServletUtils.getPageInfo(request);
		Map<String, Object> searchMap = ServletUtils.getSearchMap(vo);
		Page<CityHotelRelVO> page = cityHotelRelService.getPage(p.getStart(), p.getPageSize(), searchMap ) ;
		modelAndView.addObject("page", page);
		
		
		return modelAndView ;
	}
	
	/**
	 *  获取城市地标数据
	 */
	@RequestMapping(value = "/getcityHotelRelInfo",method = RequestMethod.POST)
	@ResponseBody
	public String getcityHotelRelInfo(HttpServletRequest request,
			ModelMap modelMap,
			CityHotelRelVO cityVO
			)
			throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		/**
		 * 带条件查询
		 */
		if (StringUtils.isNotBlank(cityVO.getCity())) {
			List<CityHotelRelVO> ctyHotelVO = cityHotelRelService.findByObject(cityVO);
			map.put("list", ctyHotelVO);
		}
		/**
		 * 没条件查询
		 */
		else
		{
			List<CityHotelRelVO> ctyHotelVO = cityHotelRelService.findall();
			map.put("list", ctyHotelVO);
			
		}
		if(map.size()>0)
		{
			return JsonUtils.toJson(map);
		}
		else
		{
			map.put("list", "null");
			return JsonUtils.toJson(map);
		}
		
}

	/**
	 *保存操作
	 */
	@RequestMapping(value = "/save")
	@ResponseBody
	public String save(CityHotelRelVO vo)
			throws Exception {
		if (vo.getId()!=null) {
			cityHotelRelService.update(vo);
		}else {
			cityHotelRelService.insert(vo);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", "success");
		return JsonUtils.toJson(map);
	}

	@RequestMapping(value = "/del")
	@ResponseBody
	public String del(int id) {
		cityHotelRelService.delete(id);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", "success");
		return JsonUtils.toJson(map);
	}
}