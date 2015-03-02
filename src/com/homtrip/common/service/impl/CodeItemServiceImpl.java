package com.homtrip.common.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homtrip.common.dao.ICodeItemDao;
import com.homtrip.common.model.CodeItem;
import com.homtrip.common.model.CodeItemKey;
import com.homtrip.common.service.ICodeItemService;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-28 上午9:15:36
* @email benjaminchen555@gmail.com
*/
@Service
public class CodeItemServiceImpl implements ICodeItemService {
	@Autowired
	private ICodeItemDao dao;

	@Override
	public List<CodeItem> getListById(int id) {
		return dao.getListById(id);
	}

	@Override
	public CodeItem getBy(CodeItemKey codeItemKey) {
		return dao.getBy(codeItemKey);
	}

}
