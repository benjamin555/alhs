package net.kml.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



import net.sysmain.common.ConnectionManager;
import net.sysplat.common.I_CommomSelector;
import net.sysplat.common.NodeElement;
import net.sysplat.common.PlatConnectionManager;

public class SelectWLFL implements I_CommomSelector {

	public String getHtmlCss() {
		return null;
	}

	public ArrayList getPageElement(String arg0) {
		return null;
	}

	public String getRootName() {
		return "<span style='cursor:hand' onClick=getInfo('0')>产品分类</span>";
	}

	public ArrayList getSelectedElements(String arg0) {
		return null;
	}

	public ArrayList getSubNodeElement(String parameter) {
		ArrayList list = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		if(parameter == null) parameter = "0";
		try {
			list = new ArrayList();
			con = ConnectionManager.getInstance().getConnection("kml.xml");
			String sql = "select * from goodsclass where parentId=?";
			pstmt = con.prepareStatement(sql);
			int p = Integer.parseInt(parameter);
			pstmt.setInt(1,p);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				NodeElement node = new NodeElement();
				node.setId(rs.getString("id"));//用于提取数据的参数
				String hierarchy = rs.getString("Hierarchy");
				if(hierarchy != null && hierarchy.length() >= 9)
				{
					node.setParentId("@");//父Id
				}
				else
				{
					node.setParentId(rs.getString("parentId"));//父Id
				}
				String name = rs.getString("gcname");
				//String url = "<a href='/engine/gettemplate.jsp?temp_Id=229&querystring=gc.guid="+rs.getString("guid")+"'>"+rs.getString("gcname")+"</a>";
				node.setName(name);
				node.setHierarchy(rs.getString("hierarchy"));
				node.setDisplayOrder(hierarchy);
				node.setValue(rs.getInt("id")+"/" + name);
				if(node.getHierarchy().length() < 9) node.setCanSelect(false);
				list.add(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
            PlatConnectionManager.close(con);
		}
		return list;
	}
	public boolean isMultiSelect() {
		return false;
	}

	public void setLanguage(String arg0) {
	}

}
