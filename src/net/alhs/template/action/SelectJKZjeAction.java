package net.alhs.template.action;

import java.sql.Connection;
import java.sql.ResultSet;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.util.StringTools;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

public class SelectJKZjeAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();
		
        String sqrguid = (String)context.get("F_sqrguid");

		String je = getJkje(conn, sqrguid);
        
		context.put("jkje", je);

		
        return SUCCESS;
	}

	public static String getJkje(Connection conn,String sqrguid) throws Exception{
		/**
		 * 1、借款记录(只显示个人借款)
		 */
        String sql = "select sum(jkje)jkje from gsjk where sqrguid = '" + sqrguid + "' and gzlzt='2'";	
		
		double jkje = 0.0d;
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if(rs.next())
		{
			jkje = rs.getDouble("jkje");
		}
		
		//还借款表查询还款记录
		sql = "select sum(bchkje)je from gshjk  where sqrguid = '" + sqrguid + "' and gzlzt='2' ";
		//sql = "select sum(mx.je)je from gshjkmx mx,gshjk zb where mx.hkrguid = '" + sqrguid + "' and zb.gzlzt='2' and mx.bxguid = zb.guid";	
		double hkje = 0.0D;
		rs = conn.createStatement().executeQuery(sql);
		if(rs.next())
		{
			hkje = hkje + rs.getDouble("je");
		}
		
        //费用报销表查询还款记录
		sql = "select sum(hkje)je from fy_bxsq  where sqrguid = '" + sqrguid + "' and gzlzt='2' ";
		rs = conn.createStatement().executeQuery(sql);
		if(rs.next())
		{
			hkje = hkje + rs.getDouble("je");
		}
		
		//房屋费用叠加明细查询还款记录
		sql = "select sum(hkje)je from fy_zhdjmx a,yzyhxx b,fwfycld c where a.zbGuid=c.guid and a.yhzhGuid=b.guid and b.xmid = '" + sqrguid + "' and c.gzlzt='2' ";
		rs = conn.createStatement().executeQuery(sql);
		if(rs.next())
		{
			hkje = hkje + rs.getDouble("je");
		}
		//jkje = (Double.parseDouble(jkje) - hkje) + "";
		double v = jkje - hkje;
		if(v < 0) 
		{
			v = v - 0.005;
		}
		else
		{
			v = v + 0.005;
		}
		//v = 89.9656;   //测试数据
		//四舍五入，取两位小数
		String _jkje = StringTools.getDoubleString(v, 2);
		
		return _jkje;
	}
	
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
