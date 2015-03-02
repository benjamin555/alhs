package net.alhs.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sysmain.common.I_UserConstant;
import net.sysplat.admin.eo.Organize;
import net.sysplat.admin.eo.User;
import net.sysplat.admin.manager.OrganizeManager;
import net.sysplat.admin.manager.UserManager;
import net.sysplat.common.A_CommonSelector;
import net.sysplat.common.LanguageOfSysplat;
import net.sysplat.common.NodeElement;
import net.sysplat.common.PlatConnectionManager;
import net.sysplat.common.Util;

/**
 * 用户选择器（单用户）
 * @author 王义春
 *
 */
public class UserSelector extends A_CommonSelector
{	
	/**
	 * 根据parameter返回下级部门
	 */
	public ArrayList getSubNodeElement(String parameter)
	{
		ArrayList list = null;
		Connection conn = null;
		OrganizeManager orgMg = OrganizeManager.getInstance();
		
		try
		{
			conn = PlatConnectionManager.getInstance().getConnection();
			orgMg.setConnection(conn);
			//该处也可以直接访问数据库
			ArrayList list1 = null;
			if(parameter == null)
			{
				parameter = "0001O";
				Organize o = orgMg.getObjectInfo(parameter);
				NodeElement node = new NodeElement();
				list = new ArrayList();
				node.setId(o.getHierarchy());          //用于提取数据的参数，对应parameter
				node.setParentId(o.getParentId() + "");//父Id
				node.setName(o.getName());
				node.setDisplayOrder(o.getDisplayOrder());
				node.setHierarchy(o.getHierarchy());
				//可以设置元素是否能被选中
				node.setCanSelect(false);
				list.add(node);
			}
			else
			{
                list1 = this.getOrganizations(parameter, orgMg);
                if(list1 != null)
				{
					list = new ArrayList();
					for(int i=0; i<list1.size(); i++)
					{
						NodeElement node = new NodeElement();
						Organize o = (Organize)list1.get(i);
						node.setId(o.getHierarchy());          //用于提取数据的参数，对应parameter
						node.setParentId(o.getParentId() + "");//父Id
						node.setName(o.getName());
						node.setDisplayOrder(o.getDisplayOrder());
						node.setHierarchy(o.getHierarchy());
						//可以设置元素是否能被选中
						node.setCanSelect(false);
						list.add(node);
					}
				}
			}
		}
		catch(Exception ex)
        {
            ex.printStackTrace();
        }
		finally
		{
			PlatConnectionManager.close(conn);
		}
		return list;
	}
	
	/**
	 * 根据parameter返回用户，如果没有叶节点，则该方法直接返回null
	 */
	public ArrayList getPageElement(String parameter)
	{
		ArrayList list = null;
		Connection conn = null;
        /**
         * 参数为空或结尾不是O
         */
        if(parameter == null || parameter.charAt(parameter.length()-1) != 'O') return null;
        
		/**
         * 是否显示用户的全路径名称
		 */
        boolean isShowFullName = this.otherPara != null && "1".equals(this.otherPara);
		UserManager userMg = UserManager.getInstance();
        //分隔符
		String seprator = "--9h87_09p9--";
		
		try
		{
			conn = PlatConnectionManager.getInstance().getConnection();
			//userMg.setDbLanguage(this.dbLanguage);
			userMg.setConnection(conn);
			//该处也可以直接访问数据库
			ArrayList list1 = userMg.getCurrentLevelUser(parameter, null);
			
			Map map = new HashMap();
			String sql = "select LoginId,yhkh,khyh from employee_archive";
			ResultSet rs = conn.createStatement().executeQuery(sql);
			while(rs.next()){
				map.put(rs.getString(1),new String[]{rs.getString(2),rs.getString(3)});
			}
			
			StringBuffer value = new StringBuffer();
			if(list1 != null)
			{
				list = new ArrayList();
				for(int i=0; i<list1.size(); i++)
				{
					NodeElement node = new NodeElement();
					User user = (User)list1.get(i);
					node.setId(user.getHierarchy()); //用于提取数据的参数
					node.setParentId("@");        //@表示是叶节点
					node.setName(user.getName());
					node.setHierarchyName(user.getHierarchyName());
					//排序码
					node.setDisplayOrder(user.getDisplayOrder());
					node.setHierarchy(user.getHierarchy());
					//node.setValue(user.getLoginid() + "/" + ((isShowFullName)?user.getHierarchyName():user.getName()));   //设置需要的数据，避免使用,分割
					if(value.length() > 0) value.delete(0, value.length());
					
					//银行信息
					String [] bank = (String[])map.get(user.getLoginid()); 
					/**
					 * 连接的字段
					 */
					value.append(user.getLoginid());    //账号
					value.append(seprator);
					value.append(user.getName());       //姓名
					value.append(seprator);
					value.append(user.getOrganizeName()); //姓名全称
					value.append(seprator);
					value.append(Util.ifNull(user.getMobile(), ""));  //手机
					value.append(seprator);
					value.append(Util.ifNull(user.getOfficePhone(), ""));  //电话
					value.append(seprator);
					value.append(Util.ifNull(bank[0],""));
					value.append(seprator);
					value.append(Util.ifNull(bank[1],""));
					node.setValue(value.toString());
					//节点关闭的图标
                    if(user.getStatus() == I_UserConstant.STATE_NORMAL)
                    {//用户正常
                        node.setIcon("../images/user.gif");
                        //节点打开的图标
                        node.setOpenIcon("../images/user.gif");
                    }
                    else
                    {//用户禁用
                        node.setIcon("../images/user1.gif");
                        //节点打开的图标
                        node.setOpenIcon("../images/user1.gif");
                    }
					list.add(node);
				}
			}
		}
		catch(Exception ex)
        {
            ex.printStackTrace();
        }
		finally
		{
			PlatConnectionManager.close(conn);
		}
		return list;
	}
	
	/**
	 * 是否允许多选
	 */
	public boolean isMultiSelect()
	{
		return false;
	}
	
	/**
	 * 根节点的名称
	 */
	public String getRootName()
	{
		return LanguageOfSysplat.GLOBAL_INFO[LanguageOfSysplat.getIndex(this.language)][0];
	}

	public String getHtmlCss()
	{
		return null;
	}
	
	/**
	 * 根据参数返回已经选择的对象，测试的例子
	 */
	public ArrayList getSelectedElements(String parameter)
	{
		return null;
	}
}

