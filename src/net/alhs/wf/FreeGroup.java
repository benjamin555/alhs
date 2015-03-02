package net.alhs.wf;

import java.sql.Connection;
import java.util.ArrayList;
import net.sysplat.common.I_NativeGroup;
import net.sysplat.common.Operator;

/**
 * ��̬��ɫ��
 * @author hewei
 *
 */
public class FreeGroup implements I_NativeGroup
{
	/**
	 * ������������������ߵļ���
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
