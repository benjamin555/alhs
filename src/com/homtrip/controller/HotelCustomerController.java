		package com.homtrip.controller;
		
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.homtrip.common.JsonUtils;
import com.homtrip.common.Page;
import com.homtrip.model.HotelJoinVO;
import com.homtrip.service.IHotelCustomerService;
		
		/**
		 * 加盟酒店control
		 * 
		 * @author hh 2015.2.4
		 * 
		 */
		@Controller
		@RequestMapping("/hotelcustomer")
		public class HotelCustomerController {
		
			private Logger  logger = LoggerFactory.getLogger(getClass());
			/**
			 * service
			 */
			@Resource
			private IHotelCustomerService hotelCustomerService;
		
			/**
			 * 获取酒店加盟客户view接口
			 */
			@RequestMapping(value = "/getHotelCustomerInfo")
			public String getHotelCustomerInfo(HttpServletRequest request,
					ModelMap modelMap) throws Exception {
				return "/intentCustomerVip";
			}
		
			/**
			 * 获取酒店加盟客户列表数据 ，分页 2014.2.3
			 */
			@RequestMapping(value = "/getHotelCustomerInfoRight")
			public ModelAndView getHotelJoinInfoRight(HotelJoinVO vo,
					HttpServletRequest request, ModelMap modelMap) throws Exception {
				ModelAndView modelAndView = new ModelAndView("intentCustomer_rightVip");
				String size = request.getParameter("page.size");
				size = StringUtils.isBlank(size) ? "3" : size;
				String start = request.getParameter("page.start");
				start = StringUtils.isBlank(start) ? "0" : start;

				// 翻页
				String no = request.getParameter("page.pageNo");
				Page<HotelJoinVO> p = new Page<HotelJoinVO>(Integer.parseInt(start),
						Integer.parseInt(size));
				if (StringUtils.isNotBlank(no)) {
					p.setPageNo(Integer.parseInt(no));
				}

				Map<String, String> searchMap = getCusSearchMap(vo);
				logger.info(searchMap.toString());
				Page<HotelJoinVO> page = hotelCustomerService.getPage(p.getStart(),
						p.getPageSize(), searchMap);
				List<HotelJoinVO> hotelList = page.getResult();
				modelAndView.addObject("hotelList", hotelList);
				modelAndView.addObject("page", page);
				return modelAndView;
			}
			/**
			 * 左边数据页显示 
			 * 2014.2.3
			 */
			@RequestMapping(value = "/getHotelCustomerInfoLeft", method = RequestMethod.GET)
			public ModelAndView getHotelJoinInfoLeft(HttpServletRequest request,
					ModelMap modelMap) throws Exception {
		
				ModelAndView modelAndView = new ModelAndView("intentCustomer_leftVip");
				String guid = request.getParameter("guid");
				if (StringUtils.isNotBlank(guid)) {
					HotelJoinVO vo = hotelCustomerService.findById(guid);
					modelAndView.addObject("item", vo);
				}
				return modelAndView;
			}
			/**
			 * 此列表数据为加盟酒店 此方法将选中的酒店取消加盟 hh 2015.2.4
			 * 
			 */
			@RequestMapping(value = "/updateUnstatus", method = RequestMethod.POST)
			@ResponseBody
			public String updateUnstatus(HttpServletRequest request,
					ModelMap modelMap,
					@RequestParam(value = "checkValArr[]") String guid[])
					throws Exception {

				int i = 0;
				boolean status = false;
				for (; i < guid.length; i++) {
					status = hotelCustomerService.updateUnstatus(guid[i]);

				}
				if (status) {
					return JsonUtils.toJson("succ");

				} else {
					return JsonUtils.toJson("err");

				}
			}
			
			/**
			 * 获取查询的map
			 * @param vo
			 * @return
			 * @throws IllegalAccessException
			 * @throws InvocationTargetException
			 * @throws NoSuchMethodException
			 */
			@SuppressWarnings({ "rawtypes", "unchecked" })
			private Map<String, String> getCusSearchMap(HotelJoinVO vo) throws IllegalAccessException, InvocationTargetException,
					NoSuchMethodException {
				Map<String, String> searchMap = new HashMap<String, String>();
				Map m = BeanUtils.describe(vo); 
				Set<String> keySet  = m.keySet();
				for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
					String key = iterator.next();
					if (m.get(key)==null||StringUtils.isBlank(m.get(key).toString())) {
						iterator.remove();
					}
				}
				searchMap.putAll(m);
				return searchMap;
			}
		}
