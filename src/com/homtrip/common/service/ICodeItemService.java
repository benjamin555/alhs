package com.homtrip.common.service;

import java.util.List;

import com.homtrip.common.model.CodeItem;
import com.homtrip.common.model.CodeItemKey;


/**
* @author 陈嘉镇
* @version 创建时间：2015-2-28 上午9:14:21
* @email benjaminchen555@gmail.com
*/
public interface ICodeItemService {

	List<CodeItem> getListById(int id);

	CodeItem getBy(CodeItemKey codeItemKey);

}
