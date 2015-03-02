package net.kml.template.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.sysplat.access.Authentication;
import net.sysplat.common.Operator;

/**
 * 预消耗明细查询
 * @author Administrator
 *
 */
public class YxhDetialSearch implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator operator = Authentication.getUserFromSession(context.getRequest());
		
		//System.out.println(context.getListObjectPara().getBaseSql());
		
		String guid = context.getReqParameter("guid");
		
		String d1 = context.getReqParameter("begindate");
		
		String d2 = context.getReqParameter("enddate");

        StringBuffer sql = new StringBuffer();
        
        StringBuffer ddSql = new StringBuffer();   //业务订单预消耗sql
        StringBuffer bhdSql = new StringBuffer();  //备货订单预消耗sql
        StringBuffer jblSql = new StringBuffer();  //贴片订单预消耗sql
        
        ddSql.append("select d.ord_no as ddxl_ddmxGuid,a.cuttingTime as ddxl_cuttingTime,ifnull(a.qrfl,a.scnum * c.quantity) as ddxl_qrfl ,a.cuttingName as ddxl_cuttingName,1 as ddxl_guid from ordercutting a,goods b,ord_dtl c,orders d where a.ddmxguid=c.guid and a.wlguid=b.guid and c.ord_guid=d.guid and c.xlzt='1' and (c.towuliu='0' or c.towuliu is null)");
		
        ddSql.append(" and a.wlGuid=").append("'").append(guid).append("'");
        
        bhdSql.append("select d.ord_no as ddxl_ddmxGuid,a.cuttingTime as ddxl_cuttingTime,ifnull(a.qrfl,a.scnum * c.quantity) as ddxl_qrfl ,a.cuttingName as ddxl_cuttingName,2 as ddxl_guid from bhddCutting a,goods b,bhdmx c,bhd d where a.ddmxguid=c.guid and a.wlguid=b.guid and c.ord_guid=d.guid and c.xlzt='1' and (c.towuliu='0' or c.towuliu is null)");
        
        bhdSql.append(" and a.wlGuid=").append("'").append(guid).append("'");
        
        jblSql.append("select d.ord_no as ddxl_ddmxGuid,a.cuttingTime as ddxl_cuttingTime,ifnull(a.qrfl,a.scnum * c.quantity) as ddxl_qrfl ,a.cuttingName as ddxl_cuttingName,3 as ddxl_guid from boardCutting a,goods b,tpdetail c,tporders d where a.tpdetailguid=c.guid and a.wlguid=b.guid and c.ord_guid=d.guid and c.xlzt='1' and c.iswuliu<>'1' and c.rkzt<>'1' ");
        
        jblSql.append(" and a.wlGuid=").append("'").append(guid).append("'");
        
        
        if(d1 != null && !"".equals(d1)){
        	ddSql.append(" and a.cuttingTime >='").append(d1).append(" 00:00:00").append("'");
        	bhdSql.append(" and a.cuttingTime >='").append(d1).append(" 00:00:00").append("'");
        	jblSql.append(" and a.cuttingTime >='").append(d1).append(" 00:00:00").append("'");
        }
        if(d2 != null && !"".equals(d2)){
        	ddSql.append(" and a.cuttingTime <='").append(d2).append(" 23:59:59").append("'");
        	bhdSql.append(" and a.cuttingTime <='").append(d2).append(" 23:59:59").append("'");
        	jblSql.append(" and a.cuttingTime <='").append(d2).append(" 23:59:59").append("'");
        }
        sql.append(ddSql).append(" union all ").append(bhdSql).append(" union all ").append(jblSql);
        
        synchronized(lock)
        {
            //统计总数量
            String drop = "drop table if exists temp_num";            
            StringBuffer sumSql = new StringBuffer("create table temp_num as ");
            sumSql.append(sql.toString());
            
            Statement st = context.getConn().createStatement();
            
            st.addBatch(drop);
            st.addBatch(sumSql.toString());
            st.executeBatch();
            
            ResultSet rs = st.executeQuery("select sum(ddxl_qrfl) from temp_num");
            if(rs.next())
            {
            	context.put("sumNum", rs.getDouble(1)+"");
            }
        }
        
		context.getListObjectPara().setTransitionSql(sql.toString());

	return SUCCESS;
}

private static String lock = "1";
    
public String getErrorMessage() {
	return null;
}

}