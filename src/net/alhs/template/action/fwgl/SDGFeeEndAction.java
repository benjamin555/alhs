package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.business.template.action.SerialNumberAction;
import net.sysmain.common.EngineTools;
import net.sysmain.util.GUID;

/**
 * 房屋水，电，管理费，提交后自动产生账号叠加单
 * @author Administrator
 *
 */
public class SDGFeeEndAction implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		String guid = context.getReqParameter("guid");

		Connection conn = context.getConn();

		List listYhzh = new ArrayList();        //银行账号信息

		List listWebId = new ArrayList();       //网络账号信息

		Map mapYhzh = new HashMap();            //银行账号叠加

		Map mapWebId = new HashMap();           //网络账号叠加

		String sql = "SELECT fwzh.glfzhlx,fwzh.glfzhGuid,fwzh.glfyzzhid,ifnull(sqmx.glf,0) glf,fwzh.sfzhlx,fwzh.sfzhGuid,fwzh.sfyzzhid,ifnull(sqmx.sf,0) sf,fwzh.dfzhlx,fwzh.dfzhGuid,fwzh.dfyzzhid,ifnull(sqmx.df,0) df,fwzh.rqfzhlx,fwzh.rqfzhGuid,fwzh.rqfyzzhid,ifnull(sqmx.rqf,0) rqf FROM house fwzb,fwskzh fwzh,fy_sqmx sqmx WHERE sqmx.houseGuid=fwzb.guid AND fwzh.houseGuid=fwzb.guid AND sqmx.zbGuid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, guid);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			String glflx = rs.getString("glfzhlx");
			String sflx = rs.getString("sfzhlx");
			String dflx = rs.getString("dfzhlx");
			String rqflx = rs.getString("rqfzhlx");

			/**
			 * 管理费
			 */
			if(glflx.equals("1")){
				/**
				 * 网络账号ID
				 * 费用
				 */
				listWebId.add(new String[]{rs.getString("glfyzzhid"),rs.getString("glf")});
			}
			else{
				/**
				 * 账号Guid(关联账号表)
				 * 费用
				 */
				listYhzh.add(new String[]{rs.getString("glfzhGuid"),rs.getString("glf")});
			}

			/**
			 * 水费
			 */
			if(sflx.equals("1")){
				/**
				 * 网络账号ID
				 * 费用
				 */
				listWebId.add(new String[]{rs.getString("sfyzzhid"),rs.getString("sf")});
			}
			else{
				/**
				 * 账号Guid(关联账号表)
				 * 费用
				 */
				listYhzh.add(new String[]{rs.getString("sfzhGuid"),rs.getString("sf")});
			}

			/**
			 * 电费
			 */
			if(dflx.equals("1")){
				/**
				 * 网络账号ID
				 * 费用
				 */
				listWebId.add(new String[]{rs.getString("dfyzzhid"),rs.getString("df")});
			}
			else{
				/**
				 * 账号Guid(关联账号表)
				 * 费用
				 */
				listYhzh.add(new String[]{rs.getString("dfzhGuid"),rs.getString("df")});
			}

			/**
			 * 燃气费
			 */
			if(rqflx.equals("1")){
				/**
				 * 网络账号ID
				 * 费用
				 */
				listWebId.add(new String[]{rs.getString("rqfyzzhid"),rs.getString("rqf")});
			}
			else{
				/**
				 * 账号Guid(关联账号表)
				 * 费用
				 */
				listYhzh.add(new String[]{rs.getString("rqfzhGuid"),rs.getString("rqf")});
			}
		}

		for (int i = 0; i < listWebId.size(); i++) {
			String [] zhIds = (String [])listWebId.get(i);
			String id = zhIds[0];                           //网络ID
			double free = Double.parseDouble(zhIds[1]);     //费用
			
			if(free<=0){
				continue;
			}
			
			if(mapWebId.containsKey(zhIds[0])){
				mapWebId.put(id, Double.parseDouble(mapWebId.get(id)+"")+free+"");
			}
			else{
				mapWebId.put(id, free+"");
			}
		}

		for (int i = 0; i < listYhzh.size(); i++) {
			String [] yhzh = (String [])listYhzh.get(i);
			String id = yhzh[0];                           //银行账号GUID
			double free = Double.parseDouble(yhzh[1]);     //费用
			
			if(free<=0){
				continue;
			}
			
			if(mapYhzh.containsKey(yhzh[0])){
				mapYhzh.put(id, Double.parseDouble(mapYhzh.get(id)+"")+free+"");
			}
			else{
				mapYhzh.put(id, free+"");
			}
		}

		/**
		 * 修改
		 */
		if(context.getTemplatePara().getEditType() == 1){
			conn.createStatement().execute("DELETE FROM fy_zhdjmx where zbGuid='"+guid+"'");
		}
		else{
			/**
			 * 修改叠加单号
			 */
			EngineTools etools = new EngineTools();
			//etools.setType(EngineTools.CALL_FOR_POST);

			sql = "update fy_sqzb set djdh=? where guid = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, "DJD"+etools.dateSerial(conn, 43, 3));
			ps.setString(2, guid);
			ps.execute();
		}

		sql = "insert into fy_zhdjmx(guid,zbGuid,yhzhGuid,wlzhId,zje) values(?,?,?,?,?)";
		ps = conn.prepareStatement(sql);

		Iterator it = mapWebId.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it.next();
			String key = (String)pairs.getKey();
			String value = (String)pairs.getValue();
			
			ps.setString(1, new GUID().toString());
			ps.setString(2, guid);
			ps.setString(3, "");
			ps.setString(4, key);
			ps.setDouble(5, Double.parseDouble(value));
			ps.addBatch();
		}
		Iterator it0 = mapYhzh.entrySet().iterator();
		while (it0.hasNext())
		{
			Map.Entry pairs = (Map.Entry)it0.next();
			String key = (String)pairs.getKey();
			String value = (String)pairs.getValue();
			
			ps.setString(1, new GUID().toString());
			ps.setString(2, guid);
			ps.setString(3, key);
			ps.setString(4, "");
			ps.setDouble(5, Double.parseDouble(value));
			ps.addBatch();
		}
		ps.executeBatch();

		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
