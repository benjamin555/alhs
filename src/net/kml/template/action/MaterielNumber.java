package net.kml.template.action;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

public class MaterielNumber implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		String wlGuid = context.getRequest().getParameter("guid");

		String sql = "select c.hierarchy from goodsclass c,goods g where g.wlfl=c.id and g.guid=?";

		Connection conn = context.getConn();

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, wlGuid);
		ResultSet rs = ps.executeQuery();
		//DecimalFormat df = new DecimalFormat("00"); 

		if(rs.next())
        {
			String hir = rs.getString(1);
			//System.out.println(hir);
			String maxClass = hir.substring(1,3);//Integer.valueOf(hir.substring(0,3),16);
			String midClass=null;
			String minClass=null;
			
			if(hir.length()>=6)
            {
				 midClass = hir.substring(4,6);//Integer.valueOf(hir.substring(3,6),16);
			}

			if(hir.length()>6)
            {
				 minClass = hir.substring(7, 9);//Integer.valueOf(hir.substring(6),16);
			}

			//System.out.println(maxClass+"/"+midClass+"/"+minClass);
			
//			context.put("maxClass", df.format(maxClass));
//			context.put("midClass", midClass==null?"":df.format(midClass)+"");
//			context.put("minClass", minClass==null?"":df.format(minClass)+"");
            
            context.put("maxClass", maxClass);
            context.put("midClass", midClass==null?"":midClass);
            context.put("minClass", minClass==null?"":minClass);
            
            String l = maxClass + (midClass==null?"":midClass) + (minClass==null?"":minClass);
            /**
             * 获取前最大的编号
             */
            sql = "select max(lh) from goods where left(lh, 6)='" + l + "'";
            ResultSet rs1 = context.getConn().createStatement().executeQuery(sql);
            if(rs1.next() && rs1.getString(1) != null && rs1.getString(1).length() > 6)
            {
                String maxLh = rs1.getString(1);
                maxLh = "分类[" + maxLh.substring(0, 6) + "]最大序号：<font color=red>" + maxLh.substring(6) + "</font>";
                context.put("lhAlert", maxLh);
            }
            else
            {
                context.put("lhAlert", "当前分类无料号，可以取序号<font color=red>001</font>");
            }
		}
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}

}
