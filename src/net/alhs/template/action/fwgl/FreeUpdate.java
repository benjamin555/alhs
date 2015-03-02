package net.alhs.template.action.fwgl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.GUID;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * 费用修改
 * @author Administrator
 *
 */
public class FreeUpdate implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		Operator op = Authentication.getUserFromSession(context.getRequest());
		String fyxgGuid = context.getReqParameter("guid"); //费用修改Guid
		String houseGuid = context.getReqParameter("houseGuid");
		String fylx = context.getReqParameter("fylx");            //费用类型
		String [] year = context.getReqParameterValues("nian");
		String [] month = context.getReqParameterValues("yue");
		String [] je = context.getReqParameterValues("je");       //金额
		String [] isfk = context.getReqParameterValues("isfk");   //是否支付
		
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map map = new HashMap();
		String sql = "select zjmx.nyf,qtfy.guid from fwzjmx zjmx,fwqtfy qtfy where zjmx.guid=qtfy.guid and zjmx.houseGuid='"+houseGuid+"'";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		while(rs.next()){
			String nyf = rs.getString("nyf");
			String guid = rs.getString("guid");
			map.put(nyf, guid);
		}
		
		/**
		 * 修改房屋其他费用的金额，付款状态,费用修改Guid
		 */
		sql = "update fwqtfy set "+fylx+"=?,fkzt_"+fylx+"=?,fyxgGuid_"+fylx+"=?,byhj=byhj+? where guid=?";
		ps = conn.prepareStatement(sql);
		
		/**
		 * 手动添加费用修改明细
		 */
		String sql0 = "insert into fyxgmx(guid,zbGuid,nian,yue,jine,sffk) values(?,?,?,?,?,?)";
		PreparedStatement ps0 = conn.prepareStatement(sql0);
		
		
		if(je!=null && je.length>0){
			for (int i = 0; i < je.length; i++) {
				String ym = year[i]+"-"+month[i];
		        if(map.containsKey(ym)){
		        	ps.setDouble(1, Double.parseDouble(je[i]));
					ps.setString(2, isfk[i]);
					ps.setString(3, fyxgGuid);
					ps.setDouble(4, Double.parseDouble(je[i]));
					ps.setString(5, (String)map.get(ym));
					ps.addBatch();
		        }
		        
		        ps0.setString(1, new GUID().toString());
		        ps0.setString(2, fyxgGuid);
		        ps0.setString(3, year[i]);
		        ps0.setString(4, month[i]);
		        ps0.setDouble(5, Double.parseDouble(je[i]));
		        ps0.setString(6, isfk[i]);
		        ps0.addBatch();
		        
			}
			ps0.executeBatch();
			ps.executeBatch();
		}
		
		
		 

		
		
		return SUCCESS;
	}
	
   
	public String getErrorMessage() {
		return null;
	}
}
