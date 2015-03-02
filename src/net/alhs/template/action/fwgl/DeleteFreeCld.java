package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 删除费用处理明细(根据主表Guid)
 * @author Administrator
 *
 */
public class DeleteFreeCld implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		
		
		String guid = context.getReqParameter("ids");
		
		if(guid==null || guid.equals("")){
			return SUCCESS;
			
		}

		deleteByFreeGuid(conn, guid);
		
		return SUCCESS;
	}
    public void deleteByFreeGuid(Connection conn,String guid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;

	    String sql = "select zjmxGuid from fwfyclmx where zbGuid=?";
		ps= conn.prepareStatement(sql);
		ps.setString(1, guid.substring(0,38));
		rs = ps.executeQuery();
		while(rs.next()){
			/**
			 * 修改是否付款状态：0（未付）
			 */
			String type = guid.substring(guid.lastIndexOf("@")+1);   //费用类型
			
			if(type.equals("zj")){   //租金
				conn.createStatement().executeUpdate("update fwzjmx set fkzt='0' where guid='"+rs.getString("zjmxGuid")+"'");
			}
			else{                    //其他费
				conn.createStatement().executeUpdate("update fwqtfy set fkzt_"+type+"='0',"+type+"=0,byhj=IFNULL(glf,0)+IFNULL(df,0)+IFNULL(sf,0)+IFNULL(rqf,0)+IFNULL(wlf,0)+IFNULL(dsf,0)+IFNULL(wxf,0)+IFNULL(qtf,0) where guid='"+rs.getString("zjmxGuid")+"'");
			}
		
		}
    }
	
	public String getErrorMessage() {
		return null;
	}
}
