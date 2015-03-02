package com.homtrip.common.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.homtrip.common.dao.ICodeItemDao;
import com.homtrip.common.mapper.CodeItemMapper;
import com.homtrip.common.model.CodeItem;
import com.homtrip.common.model.CodeItemExample;
import com.homtrip.common.model.CodeItemExample.Criteria;
import com.homtrip.common.model.CodeItemKey;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-28 上午9:36:27
* @email benjaminchen555@gmail.com
*/
@Repository
public class CodeItemDaoImpl implements ICodeItemDao {
	private SqlSessionTemplate sqlSession;
	@Resource
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
	@Override
	public List<CodeItem> getListById(int id) {
		CodeItemMapper mapper = sqlSession.getMapper(CodeItemMapper.class);
		
		CodeItemExample example = new CodeItemExample();
		Criteria c = example.createCriteria();
		c.andIdEqualTo(id);
		return mapper.selectByExample(example );
	}
	@Override
	public CodeItem getBy(CodeItemKey key) {
		CodeItemMapper mapper = sqlSession.getMapper(CodeItemMapper.class);
		return mapper.selectByPrimaryKey(key);
	}

}
