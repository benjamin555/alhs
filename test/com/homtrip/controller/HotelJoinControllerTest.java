package com.homtrip.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sysmain.util.GUID;

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

import com.homtrip.model.HotelJoinVO;

/**
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/springMVC.xml"})
public class HotelJoinControllerTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private HotelJoinController hotelJoinController;
	
	
	@Test
	public void testGetHotelCusInfo() throws Exception {
		ModelMap modelMap = new ModelMap();
		HttpServletRequest request = new MockHttpServletRequest();
		hotelJoinController.getHotelCusInfo(request, modelMap);
	}
	
	@Test
	public void testGetHotelJoinInfoRight() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "3");
		request.addParameter("page.start", "3");
		HotelJoinVO vo = new HotelJoinVO();
		ModelAndView m = hotelJoinController.getHotelJoinInfoRight(vo, request, modelMap);
		String viewName = m.getViewName();
		List<HotelJoinVO> list = (List<HotelJoinVO>) m.getModel().get("hotelList");
		Assert.isTrue("intentCustomer_right".equals(viewName));
		Assert.notEmpty(list);
		logger.info(list.toString());
	}
	
	@Test
	public void testSave() throws Exception {
		
		HotelJoinVO vo = new HotelJoinVO();
		vo.setGuid(new GUID().toString());
		vo.setAddress("sfasd");
		vo.setBh("YA0001");
		vo.setClsj(new Date());
		vo.setPassword("test");
		vo.setStep("1");
		vo.setLogo("test");
		
		hotelJoinController.save(vo );
		
	}
	
	
	

}
