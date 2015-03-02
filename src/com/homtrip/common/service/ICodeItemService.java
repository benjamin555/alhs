package com.homtrip.common.service;

import java.util.List;

import com.homtrip.common.model.CodeItem;
import com.homtrip.common.model.CodeItemKey;


/**
* @author �¼���
* @version ����ʱ�䣺2015-2-28 ����9:14:21
* @email benjaminchen555@gmail.com
*/
public interface ICodeItemService {

	List<CodeItem> getListById(int id);

	CodeItem getBy(CodeItemKey codeItemKey);

}
