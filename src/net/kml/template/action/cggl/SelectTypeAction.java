package net.kml.template.action.cggl;

import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/*
 * 与查询数据列表部件结合使用！
 * 
 */
public class SelectTypeAction implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception {
	
		String wlid = context.getRequest().getParameter("wlid");
   
        /**
         * 检测物料的分类是否为小类
         */
        String sSql = "Select Hierarchy from goodsClass where id=" + wlid;
        ResultSet rs = context.getConn().createStatement().executeQuery(sSql);
        if(rs.next() && rs.getString(1).length() >=9)
        {
            context.put("xiaolei", "1");
        }
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
