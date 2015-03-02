package net.chysoft.logon;

import java.sql.PreparedStatement;
import java.util.ArrayList;

import net.alhs.template.action.DeptChangeAction;
import net.chysoft.common.MemberNameProxy;
import net.sysplat.admin.eo.Organize;
import net.sysplat.admin.eo.User;
import net.sysplat.admin.manager.UserManager;
import net.sysplat.common.ActionContext;
import net.sysplat.common.I_PlatAction;
import net.sysplat.common.I_ResourceAction;

/**
 * 组织的缓存统计同步
 * @author 王义春
 *
 */
public class OrganizeSynAction implements I_ResourceAction
{
    public void execute(ActionContext context) throws Exception
    {
        Organize org = (Organize)context.getResourceObject();
        
        if(context.getActionType().equals(I_PlatAction.ACTION_RESOURCE_MODIFY))
        {//移除组织缓存
            MemberNameProxy.getInstance().removeDepartmentCache(org.getHierarchy()); 
        }
        else if(context.getActionType().equals(I_PlatAction.ACTION_RESOURCE_DELETE))
        {//移除组织缓存 
            MemberNameProxy.getInstance().removeDepartmentCache(org.getHierarchy());
        }
        else if(context.getActionType().equals(I_PlatAction.ACTION_RESOURCE_SORT))
        {//部门排序，同步人力资源的排序
            sortEmployee(org, context);
        }
        new DeptChangeAction().clear();
    }
    
    /**
     * 执行排序后，同步人力资源的数据
     * @param org
     */
    private void sortEmployee(Organize org, ActionContext context) throws Exception
    {
        UserManager um = UserManager.getInstance();
        um.setConnection(context.getConnection());
        ArrayList list = um.getCurrentLevelUser(org.getHierarchy(), null);
        
        PreparedStatement ps = context.getConnection().prepareStatement("update employee_archive set user_hierarchy=? where name=?");
        for(int i=0; i<list.size(); i++)
        {
            User user = (User)list.get(i);
            ps.setString(1, user.getDisplayOrder());
            ps.setString(2, user.getName());
            ps.execute();
        }
    }
}
