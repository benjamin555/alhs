package com.homtrip.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

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
import com.homtrip.model.HotelOrderVO;
import com.homtrip.model.HotelSummary;
import com.homtrip.model.HouseVO;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-10 下午4:21:33
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/springMVC.xml"})
public class HotelOrderControllerTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private HotelOrderController controller;

	@Test
	public void testList() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "5");
		request.addParameter("page.start", "0");
		HotelOrderVO vo = new HotelOrderVO();
		
		vo.setSqdh("TJ201407040022");
		
		ModelAndView modelAndView  = controller.list(vo, request, modelMap);
		Page<HotelOrderVO> page = (Page<HotelOrderVO>) modelAndView.getModel().get("page");
		if (page!=null) {
			List<HotelOrderVO>list = page.getResult();
			for (HotelOrderVO hotelOrderVO : list) {
				logger.info("hotelOrderVO:{}",hotelOrderVO.toString());
			}
		}
	}
	
	/**
	 * 测试扩展字段
	 * @throws Exception
	 */
	@Test
	public void testEx() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "5");
		request.addParameter("page.start", "0");
		HotelOrderVO vo = new HotelOrderVO();
		
		vo.setSqdh("YZ201407050020");
		
		ModelAndView modelAndView  = controller.listRight(vo, request, modelMap);
		Page<HotelOrderVO> page = (Page<HotelOrderVO>) modelAndView.getModel().get("page");
		List<HotelOrderVO>list = page.getResult();
		for (HotelOrderVO hotelOrderVO : list) {
			logger.info("hotelOrderVO:{}",hotelOrderVO.toString());
			logger.info("hotelOrderVO.getKqzyh:{}",hotelOrderVO.getKqzyh());
			Assert.isTrue(hotelOrderVO.getKqzyh()!=null);
		}
	}
	
	
	@Test
	public void testStringFormat() throws Exception {
		NumberFormat fmt = NumberFormat.getPercentInstance();  
		String string = fmt.format(0.1);
		Assert.isTrue("10%".equals(string));
		
		// TODO Auto-generated method stub
		   Double myNumber=23323.3323232323;
		   Double test=0.3434;
		   //getInstance() 
		   //返回当前缺省语言环境的缺省数值格式。
		  String myString = NumberFormat.getInstance().format(myNumber);
		   System.out.println(myString);
		   //getCurrencyInstance()返回当前缺省语言环境的通用格式
		  myString = NumberFormat.getCurrencyInstance().format(myNumber); 
		   System.out.println(myString);
		   //getNumberInstance() 返回当前缺省语言环境的通用数值格式。 
		  myString = NumberFormat.getNumberInstance().format(myNumber); 
		   System.out.println(myString);
		   
		   //getPercentInstance()  返回当前缺省语言环境的百分比格式。
		  myString = NumberFormat.getPercentInstance().format(test); 
		   System.out.println(myString);
		   
		   //setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。 
		  //setMaximumIntegerDigits(int)  设置数值的整数部分允许的最大位数。 
		  //setMinimumFractionDigits(int) 设置数值的小数部分允许的最小位数。 
		  //setMinimumIntegerDigits(int)  设置数值的整数部分允许的最小位数.
		   NumberFormat format = NumberFormat.getInstance();
		   format.setMinimumFractionDigits( 3 );
		   format.setMaximumFractionDigits(5);
		   format.setMaximumIntegerDigits( 10 );
		   format.setMinimumIntegerDigits(0);
		   System.out.println(format.format(2132323213.23266666666));
		
	}
	
	@Test
	public void testGetOrderCountMap() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "5");
		request.addParameter("page.start", "0");
		HotelOrderVO vo = new HotelOrderVO();
		 HouseVO house = new HouseVO();
		 house.setFx("一室一厅");
		vo.setHouse(house );
		ModelAndView modelAndView = controller.list(vo, request, modelMap);
		Map<String, String> m = (Map<String, String>) modelAndView.getModel().get("orderCountMap");
		logger.info("m:{}",m);
		
	}
	
	@Test
	public void testGetOrderCountMap2() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "5");
		request.addParameter("page.start", "0");
		HotelOrderVO vo = new HotelOrderVO();
		 HouseVO house = new HouseVO();
//		 house.setFx("一室一厅");
		vo.setHouse(house );
		ModelAndView modelAndView = controller.list(vo, request, modelMap);
		Map<String, String> m = (Map<String, String>) modelAndView.getModel().get("orderCountMap");
		logger.info("m:{}",m);
		
	}
	
	/**
	 * 关联房型进行查询
	 * @throws Exception
	 */
	@Test
	public void testRelations() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "5");
		request.addParameter("page.start", "0");
		HotelOrderVO vo = new HotelOrderVO();
		HouseVO house = new HouseVO();
		house.setFx("一室一厅");
		house.setDong(2);
		house.setLouceng(17);
		house.setFjh(11);
		vo.setHouse(house );
		
		ModelAndView modelAndView  = controller.listRight(vo, request, modelMap);
		Page<HotelOrderVO> page = (Page<HotelOrderVO>) modelAndView.getModel().get("page");
		List<HotelOrderVO>list = page.getResult();
		for (HotelOrderVO hotelOrderVO : list) {
			logger.info("hotelOrderVO:{}",hotelOrderVO.toString());
			logger.info("hotelOrderVO.getKqzyh:{}",hotelOrderVO.getKqzyh());
			Assert.isTrue(hotelOrderVO.getKqzyh()!=null);
		}
	}
	/**
	 * 根据酒店名称进行查询
	 * @throws Exception
	 */
	@Test
	public void testHouseOtherInfo() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "5");
		request.addParameter("page.start", "0");
		HotelOrderVO vo = new HotelOrderVO();
		HouseVO house = new HouseVO();
		house.setHotelName("艾丽豪斯双月湾公寓");
		vo.setHouse(house );
		
		ModelAndView modelAndView  = controller.listRight(vo, request, modelMap);
		Page<HotelOrderVO> page = (Page<HotelOrderVO>) modelAndView.getModel().get("page");
		List<HotelOrderVO>list = page.getResult();
		for (HotelOrderVO hotelOrderVO : list) {
			logger.info("hotelOrderVO:{}",hotelOrderVO.toString());
		}
	}
	
	@Test
	public void testHotelSummary() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("guid", "{781E8839-0000-0000-7A96-433100000A8B}");
		 controller.listLeft(request, modelMap);
		 HotelSummary sum = (HotelSummary) modelMap.get("summary");
		 logger.info("sum:{}",sum);
	}
	
	@Test
	public void testQueryBySource() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page.size", "5");
		request.addParameter("page.start", "0");
		HotelOrderVO vo = new HotelOrderVO();
		String meituan = "MT";
		vo.setDdsx(meituan);
		ModelAndView modelAndView  = controller.listRight(vo, request, modelMap);
		Page<HotelOrderVO> page = (Page<HotelOrderVO>) modelAndView.getModel().get("page");
		List<HotelOrderVO>list = page.getResult();
		for (HotelOrderVO hotelOrderVO : list) {
			logger.info("hotelOrderVO:{}",hotelOrderVO.toString());
			Assert.isTrue(hotelOrderVO.getDdsx().equals("MT"));
		}
		
	}
	
	@Test
	/**
	 * 获取清洁信息
	 * @throws Exception
	 */
	public void testFClean() throws Exception {
		ModelMap modelMap = new ModelMap();
		MockHttpServletRequest request = new MockHttpServletRequest();
		controller.fClean(request, modelMap);
		HouseVO v = (HouseVO) modelMap.get("item");
		Assert.notNull(v.getCleanInfo());
		Assert.notNull(v.getCheckInfo());
		Assert.notNull(v.getOwner());
	}
	
	
	
}
