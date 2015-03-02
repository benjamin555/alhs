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
* @author �¼���
* @version ����ʱ�䣺2015-2-10 ����4:21:33
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
	 * ������չ�ֶ�
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
		   //���ص�ǰȱʡ���Ի�����ȱʡ��ֵ��ʽ��
		  String myString = NumberFormat.getInstance().format(myNumber);
		   System.out.println(myString);
		   //getCurrencyInstance()���ص�ǰȱʡ���Ի�����ͨ�ø�ʽ
		  myString = NumberFormat.getCurrencyInstance().format(myNumber); 
		   System.out.println(myString);
		   //getNumberInstance() ���ص�ǰȱʡ���Ի�����ͨ����ֵ��ʽ�� 
		  myString = NumberFormat.getNumberInstance().format(myNumber); 
		   System.out.println(myString);
		   
		   //getPercentInstance()  ���ص�ǰȱʡ���Ի����İٷֱȸ�ʽ��
		  myString = NumberFormat.getPercentInstance().format(test); 
		   System.out.println(myString);
		   
		   //setMaximumFractionDigits(int) ������ֵ��С��������������λ���� 
		  //setMaximumIntegerDigits(int)  ������ֵ������������������λ���� 
		  //setMinimumFractionDigits(int) ������ֵ��С�������������Сλ���� 
		  //setMinimumIntegerDigits(int)  ������ֵ�����������������Сλ��.
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
		 house.setFx("һ��һ��");
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
//		 house.setFx("һ��һ��");
		vo.setHouse(house );
		ModelAndView modelAndView = controller.list(vo, request, modelMap);
		Map<String, String> m = (Map<String, String>) modelAndView.getModel().get("orderCountMap");
		logger.info("m:{}",m);
		
	}
	
	/**
	 * �������ͽ��в�ѯ
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
		house.setFx("һ��һ��");
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
	 * ���ݾƵ����ƽ��в�ѯ
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
		house.setHotelName("������˹˫���幫Ԣ");
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
	 * ��ȡ�����Ϣ
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
