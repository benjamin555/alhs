package com.homtrip.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.TransformedMap;
import org.apache.commons.lang.StringUtils;
import org.springside.modules.orm.PropertyFilter.PropertyType;
import org.springside.modules.utils.ReflectionUtils;

/**
* @author �¼���
* @version ����ʱ�䣺2015-2-4 ����11:37:41
* @email benjaminchen555@gmail.com
*/
public class SqlUtils {
	public static final String SORT_PRE_FIX = "sort_";
	/**
	 * ǰ׺
	 */
	public static final String PRE_FIX = "flt_";
	/**
	 * �ָ���
	 */
	public static final String PARA_SEPARATOR = "_";


	/**
	 * ���ز���Where���������
	 * ҳ������ eg. flt_tb1_and_likeS_field1
	 * 
	 * @param filterParamMap
	 * @param values
	 * @return
	 */

	public static String buildDynamicNoWhrSql(Map<String, String> paramMap, Map<String, Object> values) {
		Map<String, String> ret = filterMap(paramMap);

		return buildDynamicSql(ret, values);
	}

	/**
	 * ����map�����˳� ǰ׺ΪPRE_FIX��entry������ȡ��ǰ׺
	 * @param paramMap
	 * @return
	 */
	public static Map<String, String> filterMap(Map<String, String> paramMap) {
		Map<String, String> filterParamMap = keyFilter(paramMap, PRE_FIX);
		//filterParamMap ת��ΪhahsMap,�ٽ���key��ȡ����
		Map<String, String> ret = new LinkedHashMap<String, String>();
		Set<String> keySet =filterParamMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String newKey = key.substring(PRE_FIX.length());
			ret.put(newKey, filterParamMap.get(key));
		}
//		ret.putAll(filterParamMap);
//
//		//��ȥǰ׺
//		Transformer keyTransformer = new Transformer() {
//			public Object transform(Object arg0) {
//				String key = (String) arg0;
//				return key.substring(PRE_FIX.length());
//			}
//		};
//		// ��map�л�ȡ������ǰ׺���Ĳ���,����ȥ��ǰ׺����Ĳ���Map.
//		
//		ret = TransformedMap.decorate(ret, keyTransformer, null);
		return ret;
	}

	/* ��ȡwhere hql ������map �����Ĺ���
	 * @param result
	 * @param filterParamMap
	 * @param values
	 * @return
	 */
	public static String buildDynamicSql(Map<String, String> filterParamMap, Map<String, Object> values) {
		StringBuilder result = new StringBuilder();

		String tableName = "";
		String separator = "";
		String matchType = "";
		String propertyTypeCode = "";
		String fieldName = "";
		int iSuffix = 1;
		// ��������Map,����hql
		for (Map.Entry<String, String> entry : filterParamMap.entrySet()) {
			String filterName = entry.getKey();
			String value = entry.getValue();
			// ���ֵΪ�գ�����
			if (StringUtils.isNotBlank(value)) {
				String[] strTmp = filterName.split(PARA_SEPARATOR);
				tableName = strTmp[0];
				separator = strTmp[1];
				matchType = strTmp[2].substring(0, strTmp[2].length() - 1);
				propertyTypeCode = strTmp[2].substring(strTmp[2].length() - 1);
				fieldName = strTmp[3];

				result.append(" ");
				result.append(separator);
				result.append(" ");

				if (values.containsKey(fieldName)) {
					fieldName = fieldName + "SUFFIX" + iSuffix;
					iSuffix++;
				}

				// ƴ���������
				result.append(buildWhrSql(tableName, fieldName, value, matchType));

				if ("like".equals(matchType)) {
					value = "%" + value + "%";
				} else if (MatchType.optLike.toString().equals(matchType)) {
					//ͨ���ģ��ƥ��
					if (value.indexOf("*") >= 0) {
						value = value.replaceAll("\\*", "%");
					}
				}

				Class<?> propertyType = Enum.valueOf(PropertyType.class, propertyTypeCode).getValue();
				if ("in".equals(matchType)) {
					String inValues = value;
					String[] inValueArr = inValues.split(",");
					List<Object> inValueLst = new ArrayList<Object>();
					for (String inValue : inValueArr) {
						inValueLst.add(ReflectionUtils.convertStringToObject(inValue, propertyType));
					}
					values.put(fieldName, inValueLst);
				} else {
					// �������������︳ֵ
					values.put(fieldName, ReflectionUtils.convertStringToObject(value, propertyType));
				}
			}
		}

		return result.toString();
	}

	/**
	 * 
	 * @param tableName
	 * @param fieldName
	 * @param value
	 * @param matchType
	 * @return
	 */
	protected static String buildWhrSql(String tableName, String fieldName, String value, String matchType) {
		StringBuilder result = new StringBuilder();
		StringBuilder tblFldName = new StringBuilder();
		StringBuilder paramName = new StringBuilder();

		String realFieldName = fieldName;
		if (fieldName.indexOf("SUFFIX") > -1) {
			realFieldName = fieldName.substring(0, fieldName.indexOf("SUFFIX"));
		}
		if (StringUtils.isNotEmpty(tableName)) {
			tblFldName.append(tableName);
			tblFldName.append(".");
		}
		tblFldName.append(realFieldName);

		paramName.append(":");
		paramName.append(fieldName);

		result.append(tblFldName);

		if ("eq".equals(matchType)) {
			result.append(" = ");
		} else if ("like".equals(matchType)) {
			result.append(" like ");
		} else if ("le".equals(matchType)) {
			result.append(" <= ");
		} else if ("lt".equals(matchType)) {
			result.append(" < ");
		} else if ("ge".equals(matchType)) {
			result.append(" >= ");
		} else if ("gt".equals(matchType)) {
			result.append(" > ");
		} else if ("in".equals(matchType)) {
			result.append(" in ( ");
		} else if (MatchType.optLike.toString().equals(matchType)) {
			if (value.indexOf("*") >= 0) {
				result.append(" like ");
				value = value.replaceAll("\\*", "%");
			} else {
				result.append(" = ");
			}

		}
		result.append(paramName);

		if ("in".equals(matchType)) {
			result.append(" ) ");
		}

		return result.toString();
	}

	public enum MatchType {
		eq, like, lt, gt, le, ge

		/**
		 * ���ǰ̨����*��Ϊlike,����%���*��û�еĻ���Ϊeq.
		 */
		, optLike;
	}

	/**
	 * ��������hql
	 * @param filterParamMap key�ĸ�ʽΪ:tb1_field1,value:Ϊ asc������Ϊdesc
	 * @return
	 */
	public static String buildOrderSql(Map<String, String> filterParamMap) {
		if (filterParamMap==null||filterParamMap.isEmpty()) {
			return "";
		}
		Map<String, String> map = MapUtils.predicatedMap(filterParamMap, null, new Predicate() {
			public boolean evaluate(Object arg0) {
				String value = (String) arg0;
				return "asc".equalsIgnoreCase(value) || "desc".equalsIgnoreCase(value);
			}
		});

		String tableName = "";
		String fieldName = "";
		StringBuffer sBuffer = new StringBuffer();
		int i = 0;
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String filterName = entry.getKey();
			String value = entry.getValue();
			String[] a = filterName.split("_");
			tableName = a[0];
			fieldName = a[1];
			if (i > 0) {
				sBuffer.append(",");
			}
			sBuffer.append(" ").append(tableName).append(".").append(fieldName).append(" ").append(value).append(" ");
			i++;
		}
		if (StringUtils.isNotBlank(sBuffer.toString())) {
			sBuffer.insert(0, " order by ");
		}
		return sBuffer.toString();
	}


	/**
	 * ����hql��where ��Ӻ� order ���
	 * @param filterParamMap {flt_tb1_and_likeS_field1:value},sort_tb1_field1,value:Ϊ asc������Ϊdesc
	 * @param values
	 * @return ���������where �ؼ���
	 */
	public static String buildSqlAppend(Map<String, String> filterParamMap, Map<String, Object> values) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(buildDynamicNoWhrSql(filterParamMap, values));
		sBuffer.append(buildOrderSql2(filterParamMap));
		return sBuffer.toString();
	}

	/**
	 * ����map�����˳� ǰ׺Ϊ������ȡ��ǰ׺
	 * @param paramMap
	 * @param preFix
	 * @return
	 */
	private static Map<String, String> filterMap(Map<String, String> paramMap, final String preFix) {
		Map<String, String> filterParamMap = keyFilter(paramMap, preFix);

		//filterParamMap ת��ΪhahsMap,�ٽ���key��ȡ����
		Map<String, String> ret = new LinkedHashMap<String, String>();
		ret.putAll(filterParamMap);

		//��ȥǰ׺
		Transformer keyTransformer = new Transformer() {
			public Object transform(Object arg0) {
				String key = (String) arg0;
				return key.substring(preFix.length());
			}
		};
		// ��map�л�ȡ������ǰ׺���Ĳ���,����ȥ��ǰ׺����Ĳ���Map.
		ret = TransformedMap.decorate(ret, keyTransformer, null);
		return ret;
	}

	/**
	 * ��������hql
	 * @param filterParamMap key�ĸ�ʽΪ:SORT_PRE_FIXtb1_field1,value:Ϊ asc������Ϊdesc
	 * @return
	 */
	public static String buildOrderSql2(Map<String, String> filterParamMap) {
		filterParamMap = filterMap(filterParamMap, SORT_PRE_FIX);
		return buildOrderSql(filterParamMap);
	}

	private static Map<String, String> keyFilter(Map<String, String> paramMap, String prefix) {
		Set<String> keySet = paramMap.keySet();
		Map<String, String> filterParamMap = new LinkedHashMap<String, String>();
		for (String key : keySet) {

			if (key.startsWith(prefix)) {
				filterParamMap.put(key, paramMap.get(key));
			}
		}
		return filterParamMap;
	}
}
