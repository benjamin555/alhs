package net.kml.template.action.cggl;

import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/*
 * 与查询数据列表部件结合使用！
 * 
 */
public class SelectGysAction implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
	
		//查询用sql语句
		String sql = context.getListObjectPara().getQuerySql();		

		String type= context.getReqParameter("wlid");
		
		String str= " and gc.Id=" + type;
		
		//查询子节点
		StringBuffer ids = new StringBuffer("(");
		ResultSet rs = context.getConn().createStatement().executeQuery("select id from goodsclass where parentId="+type);
		while (rs.next())
		{
			ids.append(rs.getString(1)).append(",");
		}
		ids.append(type).append(")");
		
		
		if(ids.length()>3)
		{
			str = " and gc.Id in"+ids.toString();
		}else if("0".equals(type))
		{
			str="";
		}
		
		if (sql.indexOf("select") != -1) {
				
				sql = sql.replaceAll("select"," select distinct ");
				sql = sql.replaceAll("provide prov,stockStorage ssd where ssd.suppliersGUID=prov.guid",
                          "goodsclass gc,goods prod,provide prov,stockStorage ssd where ssd.suppliersGUID=prov.guid And ssd.materialsGuid=prod.guid And prod.wlfl=gc.ID" + str);						
			}
			//System.out.println(sql);
			context.getListObjectPara().setTransitionSql(sql);
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
