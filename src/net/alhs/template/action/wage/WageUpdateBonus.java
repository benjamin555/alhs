package net.alhs.template.action.wage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.mail.util.GUID;
import net.sysmain.common.ConnectionManager;
import net.sysmain.util.StringTools;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;
/**
 * 工资提交后，修改奖金的月出勤，及跟出勤有关的奖金
 * @author Administrator
 *
 */
public class WageUpdateBonus implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		Connection conn = context.getConn();
		
		String gzGuid = context.getReqParameter("guid");                //工资主表Guid
		String [] guids = context.getReqParameterValues("guids");       //工资明细guid
		String [] cqls =context.getReqParameterValues("cql");           //出勤率       
		if(guids!=null && guids.length>0){
			for (int i = 0; i < guids.length; i++) {
				/**
				 * 修改与出勤有关的奖金明细
				 */
				double cql = Double.parseDouble(cqls[i]);   
				String sql = "update jiangjinmx set "+
						"zlj=ifnull(zlj,0)*"+cql+",klj=ifnull(klj,0)*"+cql+"," +
						"dxj=ifnull(dxj,0)*"+cql+",wxj=ifnull(wxj,0)*"+cql+"," +
						"zxj=ifnull(zxj,0)*"+cql+"," +
						"jjhj=ifnull(tfj,0)+ifnull(dmj,0)+ifnull(zlj,0)+ifnull(xsj,0)+ifnull(wxj,0)+ifnull(jsj,0)+ifnull(fxj,0)+ifnull(mxj,0)+ifnull(klj,0)+ifnull(dxj,0)+ifnull(zxj,0)+ifnull(qjj,0) " +
						"where gzmxGuid='"+guids[i]+"'";
				conn.createStatement().executeUpdate(sql);
			}
			/**
			 * 修改奖金主表的奖金
			 */
			String querySql = "select sum(zlj) zlj,sum(klj) klj,sum(dxj) dxj,sum(wxj) wxj,sum(zxj) zxj from jiangjinmx where zbGuid=(select guid from jiangjin where gzGuid='"+gzGuid+"')";
			ResultSet rs = conn.createStatement().executeQuery(querySql);
			if(rs.next()){
				double zlj = rs.getDouble("zlj");
				double klj = rs.getDouble("klj");
				double dxj = rs.getDouble("dxj");
				double wxj = rs.getDouble("wxj");
				double zxj = rs.getDouble("zxj");
				String updateSql = "update jiangjin set zlj="+zlj+",klj="+klj+",dxj="+dxj+",wxj="+wxj+",zxj="+zxj+"," +
						        "jjhj=ifnull(tfj,0)+ifnull(dmj,0)+ifnull(zlj,0)+ifnull(xsj,0)+ifnull(wxj,0)+ifnull(jsj,0)+ifnull(fxj,0)+ifnull(mxj,0)+ifnull(klj,0)+ifnull(dxj,0)+ifnull(zxj,0)+ifnull(qjj,0) " +
						        "where gzGuid='"+gzGuid+"'";
				
				conn.createStatement().executeUpdate(updateSql);
			}
		}
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}