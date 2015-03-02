package com.homtrip.controller;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import com.homtrip.common.Page;
import com.homtrip.model.CityHotelRelVO;
import com.homtrip.model.HotelJoinVO;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-9 上午9:09:40
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/springMVC.xml"})
public class CityHotelRelControllerTest {
	
	private Logger logger =LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CityHotelRelController controller;
	
	@Test
	public void testSave() throws Exception {
		for (int i = 0; i < 7; i++) {
			CityHotelRelVO vo = new CityHotelRelVO();
			vo.setProvince("北京市");
			vo.setCity("市辖区");
			String mString = controller.save(vo);
			logger.info(mString);
			Assert.isTrue(mString.indexOf("success")>0);
		}
		
	}
	@Test
	public void testUpdate() throws Exception {
		//test update
				CityHotelRelVO vo2= new CityHotelRelVO();
				vo2.setId(1);
				vo2.setCity("test1");
				vo2.setLandmark("test3");
				controller.save(vo2);
	}
	
	
	@Test
	public void testRefList() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "3");
		request.addParameter("page.start", "0");
		CityHotelRelVO vo = new CityHotelRelVO();
		vo.setLandmark("test3");
		ModelAndView m = controller.relList(vo, request, modelMap);
		String viewName = m.getViewName();
		Page<HotelJoinVO> page = (Page<HotelJoinVO>) m.getModel().get("page");
		List<HotelJoinVO> list =page.getResult();
		Assert.isTrue("/setting/rel/cityLandmark_list".equals(viewName));
		Assert.notEmpty(list);
		logger.info(list.toString());
	}
	
	@Test
	public void testDel() throws Exception {
		int id = 5;
		controller.del(id);
		
	}

}
