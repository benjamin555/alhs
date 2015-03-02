package net.alhs.template.action.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysmain.common.I_CommonConstant;
/**
 * 删除佣金处理明细
 * @author Administrator
 *
 */
public class DeleteYjclMx implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		Connection conn = context.getConn();


		String guid = context.getReqParameter("ids");

		if(guid==null || guid.equals("")){
			return SUCCESS;

		}
		String tempId= context.getReqParameter("temp_Id");
		if(tempId.equals("2309")){
			deleteByGuid(conn, guid);
		}
		else{
			deleteByYjclmxGuid(conn, guid);
		}

		return SUCCESS;
	}
	/**
	 * 删除佣金处理明细
	 * @param conn
	 * @param guid
	 * @throws Exception
	 */
	public void deleteByYjclmxGuid(Connection conn,String guid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select a.zbGuid,a.ddGuid,b.yjlx,d.ffxj,c.ptyj,c.hxyj from yjclmx a,yjcld b,ddhs c,fwddzb d where a.zbGuid=b.guid and a.ddGuid=c.guid and c.guid=d.guid and a.guid=?";
		ps= conn.prepareStatement(sql);
		ps.setString(1, guid);
		rs = ps.executeQuery();
		if(rs.next()){
			String zbGuid = rs.getString("a.zbGuid");    //主表Guid
			String ddGuid = rs.getString("a.ddGuid");    //订单Guid
			String yjlx = rs.getString("yjlx");        //佣金类型 1.平台   2.惠选
			double ffxj = rs.getDouble("d.ffxj");      //房费小计
			double ptyj = rs.getDouble("c.ptyj");
			double hxyj = rs.getDouble("c.hxyj");

			double yj = (yjlx.equals("1")?ptyj:hxyj);
			/**
			 * 修改是否付佣状态：0（未付）
			 */
			conn.createStatement().executeUpdate("update ddhs set "+(yjlx.equals("1")?"sffy_pt":"sffy_hx")+"='0' where guid='"+ddGuid+"'");

			/**
			 * 修改佣金处理表的汇总
			 */
			String yjType = (yjlx.equals("1")?"ptyj":"hxyj");
			conn.createStatement().executeUpdate("update yjcld set ddsl=ddsl-1,zje=ifnull(zje,0)-"+ffxj+","+yjType+"=ifnull("+yjType+",0)-"+yj+" where guid='"+zbGuid+"'");
		}
	}
	/**
	 * 根据主表删除佣金处理
	 * @param conn
	 * @param guid
	 * @throws Exception
	 */
	public void deleteByGuid(Connection conn,String guid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select guid from yjclmx where zbGuid=?";
		ps= conn.prepareStatement(sql);
		ps.setString(1, guid);
		rs = ps.executeQuery();
		while(rs.next()){
			String yjmxGuid = rs.getString("guid");    //主表Guid

			deleteByYjclmxGuid(conn, yjmxGuid);
		}
	}

	public String getErrorMessage() {
		return null;
	}
}
