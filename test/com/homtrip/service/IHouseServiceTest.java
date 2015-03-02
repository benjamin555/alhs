package com.homtrip.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.homtrip.model.HouseVO;

/**
* @author 陈嘉镇
* @version 创建时间：2015-3-2 上午10:57:32
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/springMVC.xml"})
public class IHouseServiceTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IHouseService houseService;
	@Test
	public void testGetById() throws Exception {
		String guid = "{781E8839-0000-0000-4FE0-FAD80000023D}";
		HouseVO vo = houseService.getById(guid);
		logger.info("vo:{}",vo);
	}
	

}
