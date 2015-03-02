package com.homtrip.controller;
/**
 * �Ƶ�����ͻ�control
 */
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sysmain.util.GUID;

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
import com.homtrip.service.IHotelJoinService;

@Controller
@RequestMapping("/hoteljoin")
public class HotelJoinController {
	
	public static final String PAGE_SIZE = "5";
	
	private Logger  logger = LoggerFactory.getLogger(getClass());
	/**
	 * ����Ƶ�ӿڶ�service
	 */
	@Resource
	private IHotelJoinService hotelJoinService;

	/**
	 * ��ȡ�Ƶ�����ͻ�view�ӿ�
	 */
	@RequestMapping(value = "/getHotelJoinInfo")
	public String getHotelCusInfo(HttpServletRequest request, ModelMap modelMap)
			throws Exception {
		return "/intentCustomer";
	}

	/**
	 * ��ȡ�Ƶ�����ͻ��б����� ����ҳ 2014.2.3
	 */
	@RequestMapping(value = "/getHotelJoinInfoRight")
	public ModelAndView getHotelJoinInfoRight(HotelJoinVO vo,
			HttpServletRequest request, ModelMap modelMap) throws Exception {
		ModelAndView modelAndView = new ModelAndView("intentCustomer_right");
		Page<HotelJoinVO> p = getPageInfo(request);

		Map<String, String> searchMap = getSearchMap(vo);
		logger.info(searchMap.toString());
		Page<HotelJoinVO> page = hotelJoinService.getPage(p.getStart(),
				p.getPageSize(), searchMap);
		List<HotelJoinVO> hotelList = page.getResult();
		modelAndView.addObject("hotelList", hotelList);
		modelAndView.addObject("page", page);
		return modelAndView;
	}

	/**
	 * ��ȡ��ҳ��Ϣ
	 * @param request
	 * @return
	 */
	private Page<HotelJoinVO> getPageInfo(HttpServletRequest request) {
		String size = request.getParameter("page.size");
		size = StringUtils.isBlank(size) ? PAGE_SIZE : size;
		String start = request.getParameter("page.start");
		start = StringUtils.isBlank(start) ? "0" : start;

		// ��ҳ
		String no = request.getParameter("page.pageNo");
		Page<HotelJoinVO> p = new Page<HotelJoinVO>(Integer.parseInt(start),
				Integer.parseInt(size));
		if (StringUtils.isNotBlank(no)) {
			p.setPageNo(Integer.parseInt(no));
		}
		return p;
	}

	/**
	 * ��ȡ��ѯ��map
	 * @param vo
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, String> getSearchMap(HotelJoinVO vo) throws IllegalAccessException, InvocationTargetException,
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

	/**
	 * �������ҳ��ʾ 2014.2.3
	 */
	@RequestMapping(value = "/getHotelJoinInfoLeft")
	public ModelAndView getHotelJoinInfoLeft(HttpServletRequest request,
			ModelMap modelMap, String id) throws Exception {
		ModelAndView modelAndView = new ModelAndView("intentCustomer_left");
		String guid = request.getParameter("guid");
		if (StringUtils.isNotBlank(guid)) {
			HotelJoinVO vo = hotelJoinService.findByGuid(guid);
			modelAndView.addObject("item", vo);
		}
		return modelAndView;
	}

	/**
	 * ��������
	 */
	/*
	 * @RequestMapping(value = "/getHotelCusInfoView", method =
	 * RequestMethod.GET) public ModelAndView
	 * getHotelCusInfoView(HttpServletRequest request, ModelMap modelMap) throws
	 * Exception {
	 * 
	 * ModelAndView modelAndView = new ModelAndView("intentCustomer");
	 * List<HotelJoinVO> hotelList = hotelJoinService.findAll();
	 * modelAndView.addObject("hotelList", hotelList); return modelAndView;
	 * 
	 * }
	 */

	/**
	 * ���б�����Ϊ����Ƶ� �˷�����ѡ�еľƵ�תΪ���˾Ƶ� hh 2015.2.4
	 * 
	 */
	@RequestMapping(value = "/updatestatus", method = RequestMethod.POST)
	@ResponseBody
	public String getCheckboxList(HttpServletRequest request,
			ModelMap modelMap,
			@RequestParam(value = "checkValArr[]") String guid[])
			throws Exception {

		int i = 0;
		boolean status = false;
		for (; i < guid.length; i++) {
			System.out.println(guid[i]);
			status = hotelJoinService.updatestatus(guid[i]);

		}
		if (status) {
			return JsonUtils.toJson("succ");

		} else {
			return JsonUtils.toJson("err");

		}
	}
	
	
	
	/**
	 * ��ȡ�Ƶ�����ͻ�view�ӿ�
	 */
	@RequestMapping(value = "/input")
	public String input()
			throws Exception {
		return "/intentCustomer_form";
	}
	
	
	/**
	 *�������
	 */
	@RequestMapping(value = "/save")
	public String save(HotelJoinVO vo)
			throws Exception {
		vo.setGuid(new GUID().toString());
		vo.setStatus(1);
		hotelJoinService.insert(vo);
		return "redirect:/hoteljoin/getHotelJoinInfo.do";
	}
	
}
