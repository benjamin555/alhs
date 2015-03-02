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
 * �û�ѡ���������û���
 * @author ���崺
 *
 */
public class UserSelector extends A_CommonSelector
{	
	/**
	 * ����parameter�����¼�����
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
			//�ô�Ҳ����ֱ�ӷ������ݿ�
			ArrayList list1 = null;
			if(parameter == null)
			{
				parameter = "0001O";
				Organize o = orgMg.getObjectInfo(parameter);
				NodeElement node = new NodeElement();
				list = new ArrayList();
				node.setId(o.getHierarchy());          //������ȡ���ݵĲ�������Ӧparameter
				node.setParentId(o.getParentId() + "");//��Id
				node.setName(o.getName());
				node.setDisplayOrder(o.getDisplayOrder());
				node.setHierarchy(o.getHierarchy());
				//��������Ԫ���Ƿ��ܱ�ѡ��
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
						node.setId(o.getHierarchy());          //������ȡ���ݵĲ�������Ӧparameter
						node.setParentId(o.getParentId() + "");//��Id
						node.setName(o.getName());
						node.setDisplayOrder(o.getDisplayOrder());
						node.setHierarchy(o.getHierarchy());
						//��������Ԫ���Ƿ��ܱ�ѡ��
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
	 * ����parameter�����û������û��Ҷ�ڵ㣬��÷���ֱ�ӷ���null
	 */
	public ArrayList getPageElement(String parameter)
	{
		ArrayList list = null;
		Connection conn = null;
        /**
         * ����Ϊ�ջ��β����O
         */
        if(parameter == null || parameter.charAt(parameter.length()-1) != 'O') return null;
        
		/**
         * �Ƿ���ʾ�û���ȫ·������
		 */
        boolean isShowFullName = this.otherPara != null && "1".equals(this.otherPara);
		UserManager userMg = UserManager.getInstance();
        //�ָ���
		String seprator = "--9h87_09p9--";
		
		try
		{
			conn = PlatConnectionManager.getInstance().getConnection();
			//userMg.setDbLanguage(this.dbLanguage);
			userMg.setConnection(conn);
			//�ô�Ҳ����ֱ�ӷ������ݿ�
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
					node.setId(user.getHierarchy()); //������ȡ���ݵĲ���
					node.setParentId("@");        //@��ʾ��Ҷ�ڵ�
					node.setName(user.getName());
					node.setHierarchyName(user.getHierarchyName());
					//������
					node.setDisplayOrder(user.getDisplayOrder());
					node.setHierarchy(user.getHierarchy());
					//node.setValue(user.getLoginid() + "/" + ((isShowFullName)?user.getHierarchyName():user.getName()));   //������Ҫ�����ݣ�����ʹ��,�ָ�
					if(value.length() > 0) value.delete(0, value.length());
					
					//������Ϣ
					String [] bank = (String[])map.get(user.getLoginid()); 
					/**
					 * ���ӵ��ֶ�
					 */
					value.append(user.getLoginid());    //�˺�
					value.append(seprator);
					value.append(user.getName());       //����
					value.append(seprator);
					value.append(user.getOrganizeName()); //����ȫ��
					value.append(seprator);
					value.append(Util.ifNull(user.getMobile(), ""));  //�ֻ�
					value.append(seprator);
					value.append(Util.ifNull(user.getOfficePhone(), ""));  //�绰
					value.append(seprator);
					value.append(Util.ifNull(bank[0],""));
					value.append(seprator);
					value.append(Util.ifNull(bank[1],""));
					node.setValue(value.toString());
					//�ڵ�رյ�ͼ��
                    if(user.getStatus() == I_UserConstant.STATE_NORMAL)
                    {//�û�����
                        node.setIcon("../images/user.gif");
                        //�ڵ�򿪵�ͼ��
                        node.setOpenIcon("../images/user.gif");
                    }
                    else
                    {//�û�����
                        node.setIcon("../images/user1.gif");
                        //�ڵ�򿪵�ͼ��
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
	 * �Ƿ������ѡ
	 */
	public boolean isMultiSelect()
	{
		return false;
	}
	
	/**
	 * ���ڵ������
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
	 * ���ݲ��������Ѿ�ѡ��Ķ��󣬲��Ե�����
	 */
	public ArrayList getSelectedElements(String parameter)
	{
		return null;
	}
}

