package net.alhs.wf;

import java.sql.Connection;
import java.util.ArrayList;
import net.sysplat.common.I_NativeGroup;
import net.sysplat.common.Operator;

/**
 * 动态角色，
 * @author hewei
 *
 */
public class FreeGroup implements I_NativeGroup
{
	/**
	 * 参数或条件，设置最高的级数
	 */
	private String parameter = null;
	private Connection conn = null;
	
	public ArrayList getGroupMembers(int gId, String insId, Operator operator) throws Exception
	{
		ArrayList list = (ArrayList)operator.getAttribute("userList");
		
		return list;
	}

	public void setParameter(String parameter)
	{
		this.parameter = parameter;
	}

	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}

	public void setFilterParameter(String filters)
	{
		// TODO Auto-generated method stub
		
	}
}
