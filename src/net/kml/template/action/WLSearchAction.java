package net.kml.template.action;

import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

public class WLSearchAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception 
    {
		String wlid = context.getRequest().getParameter("wlid");
		String sql = context.getListObjectPara().getQuerySql();
		String order = context.getRequest().getParameter("isOrder");
        String isError = context.getRequest().getParameter("isError");
        
        /**
         * 检测物料的分类是否为小类
         */
        String sSql = "Select Hierarchy from goodsClass where id=" + wlid;
        ResultSet rs = context.getConn().createStatement().executeQuery(sSql);
        if(rs.next() && rs.getString(1).length() >=9)
        {
            context.put("xiaolei", "1");
        }
        
        if(!wlid.equals("0"))
		{//id为0查所有，不设置条件 
			sql = sql.replaceFirst(" from ", " from goodsclass a,goodsclass b,");
			if(sql.indexOf(" where ") == -1)
			{
				sql = sql + " where prod.wlfl=a.id and a.Hierarchy like concat(b.Hierarchy, '%') and b.Id=" + wlid;
			}
			else
			{
				sql = sql.replaceFirst(" where "," where prod.wlfl=a.id and a.Hierarchy like concat(b.Hierarchy,'%') and b.Id=" + wlid + " and ");
			}
		}
        /**
         * 只显示有误差的数据
         */
        if("1".equals(isError))
        {
            sql = sql + " and round(prod.lastMonth)<>round(prod.count+prod.blp+prod.currentOccupy+prod.monthOutNum-prod.monthEnterNum)";
        }
        
		if(order != null){

			sql += " order by count/warncount*100 "+order;

		}
		context.getListObjectPara().setTransitionSql(sql);
		
        return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
