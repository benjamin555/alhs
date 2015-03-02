package com.homtrip.common.tags;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.homtrip.common.Page;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-10 下午2:01:01
* @email benjaminchen555@gmail.com
*/

@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/springMVC.xml"})
public class PagerTest {
	Pager pager = new Pager();
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testRender() throws Exception {
		Page p = new Page();
		p.setStart(0);
		p.setPageSize(5);
		p.setTotalCount(16);
		pager.setFormId("searchForm");
		pager.setUrl("sss");
		pager.render("", p );
	}
	
}
