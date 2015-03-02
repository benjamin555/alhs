package net.alhs.template.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpSession;


import net.sysplat.access.Authentication;
import net.sysplat.admin.eo.Organize;
import net.sysplat.admin.manager.OrganizeManager;
import net.sysplat.common.ActionContext;
import net.sysplat.common.I_ResourceAction;
import net.sysplat.common.Operator;
import net.sysplat.common.PlatConnectionManager;

/**
 * 更改部门的归属
 * @author Owner
 *
 */
public class DeptChangeAction implements I_ResourceAction {

	public void execute(ActionContext context) throws Exception 
	{
		HttpSession session = context.getRequest().getSession();
		Operator operator = Authentication.getUserFromSession(context.getRequest());
		//System.out.println(operator.getHierarchy());
		if(operator != null && !operator.isSuperAdminUser())
		{
			if(depts.size() == 0) init();
			String hierarchy = operator.getHierarchy();			
			TempDepartment dept = getTempDepartment(hierarchy.substring(0, hierarchy.length()-5));
			
			if(dept != null)
			{
				operator.setOrganizeId(dept.organizeId);
				
				OrganizeManager om = OrganizeManager.getInstance();
				om.setConnection(context.getConnection());
				Organize org = om.getOrganizeById(dept.organizeId);
				operator.setAttribute("jiudian", org.getName());
				
				//operator.setHierarchy(dept.hierarchy);
				//operator.setHierarchyName(dept.hierarchyName);
				
			    net.sysmain.common.Operator op = (net.sysmain.common.Operator)session.getAttribute(net.sysmain.common.I_UserConstant.USER_INFO); 
			    op.setUserOrgId(dept.organizeId + "");
			    op.setOrgName(dept.name);            
			}
			else
			{
				operator.setAttribute("jiudian", operator.getShortOrgName());       
			}
		}

	}
	
	private static HashMap depts = new HashMap();
	
	
	private TempDepartment getTempDepartment(String hierarchy)
	{
		if(depts.size() == 0) return null;
		
		while(hierarchy.length() > 5)
		{
			TempDepartment dept = (TempDepartment)depts.get(hierarchy);
			
			if(dept != null) return dept;
			hierarchy = hierarchy.substring(0, hierarchy.length()-5);
		}
		
		return null;
	}
	
	public void clear()
	{
		synchronized(depts)
        {
			if(depts.size() > 0) depts.clear();
        }
	}
	
	public void init()
	{
		synchronized(depts)
        {
			if(depts.size() > 0) depts.clear();
            Connection conn = null;
            try
            {
                conn = PlatConnectionManager.getInstance().getConnection();
                Statement smt = conn.createStatement();
                ResultSet rs = smt.executeQuery("Select OrganizeId,Name,Hierarchy,HierarchyName from Organization_sys where Sfjd='1'");
                while(rs.next())
                {
	                TempDepartment dept = new TempDepartment();
	                String hierarcy = rs.getString("hierarchy");
	                dept.organizeId = rs.getInt("OrganizeId");
	                dept.name = rs.getString("Name");
	                dept.hierarchy = hierarcy;
	                dept.hierarchyName = rs.getString("HierarchyName");
	                
	                depts.put(hierarcy, dept);
                }
                //空的
                if(depts.size() == 0) depts.put("__NONE__", "d");
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                PlatConnectionManager.close(conn);
            }
        }
	}
	
	class TempDepartment
	{
		int organizeId = -1;
		String name = null;
		String hierarchy = null;
		String hierarchyName = null;
	}
}
