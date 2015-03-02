package com.homtrip.common.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.homtrip.common.model.CodeItem;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-28 上午9:13:17
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/springMVC.xml"})
public class CodeItemServiceTest {
	@Autowired
	private ICodeItemService service;
	
	@Test
	public void testGetList() throws Exception {
		int id = 71;
		List<CodeItem>  l = service.getListById(id);
		Assert.isTrue(l.size()==19);
	}

}
