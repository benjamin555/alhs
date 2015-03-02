package net.kml.template.action;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import net.sysmain.common.I_CommonConstant;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.StringTools;

public class WLHierarchy implements I_TemplateAction {

	public int execute(TemplateContext context) throws Exception 
	{
		int id = Integer.parseInt(context.getRequest().getParameter("pid"));
		HttpServletRequest requet = context.getRequest();
		if(id == 0)
		{
			if(context.getTemplatePara().getEditType() == 0)
		    {
			    requet.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "parent.location='../kml/wllb.jsp'");
			}
			else
			{
				requet.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "parent.updateName('" + requet.getParameter("name") + "');" +
						"location='gettemplate.jsp?temp_Id=229&edittype=1&id=" + requet.getParameter("id") + "'");
			}
		}
		else
		{ 
			if(context.getTemplatePara().getEditType() == 0)
		    {
				requet.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "parent.nodeReload(" + id + ");" +
						"location='gettemplate.jsp?temp_Id=229&pid=" + id + "'");
			}
			else
			{
				requet.setAttribute(I_CommonConstant.JAVA_SCRIPT_CODE, "parent.updateName('" + requet.getParameter("name") + "');" +
						"location='gettemplate.jsp?temp_Id=229&edittype=1&id=" + requet.getParameter("id") + "'");
			}
		}
		
		if(context.getTemplatePara().getEditType() == 1)
			return 0;
		
		//I_CommonConstant.JAVA_SCRIPT_CODE
		
		Connection conn = context.getConn();
		
		String hierarchy = getChildHierarchy(id, conn);
		
		context.getTables()[0].getFieldByName("Hierarchy").setFieldValue(hierarchy);
		return 0;
	}
	
	private String getChildHierarchy(int id, Connection conn) throws Exception 
    {
        String sql = null;
        Statement smt = null;
        ResultSet rs = null;
        String currentNode = null;
        String maxChildNode = null;
        
        /**
         * 当前层次码
         */
        sql = "select hierarchy from goodsclass where Id=" + id;
        smt = conn.createStatement();
        rs = smt.executeQuery(sql);
        if(rs.next())
        {
            currentNode = rs.getString("hierarchy");	        
        }
        /**
         * 下级层次码
         */
        String sql1 = "select max(hierarchy) as hierarchy from goodsclass where ParentId=" + id;
        Statement smt1 = conn.createStatement();
        ResultSet rs1 = smt1.executeQuery(sql1);
        if(rs1.next())
        {
            maxChildNode = rs1.getString("hierarchy");
            //maxChildNode = maxChildNode.substring(currentNode.length());
        }
        return StringTools.getHierarchy(currentNode, maxChildNode);
    }

	public String getErrorMessage() {
		return null;
	}

}
