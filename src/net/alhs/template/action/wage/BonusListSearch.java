package net.alhs.template.action.wage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.business.engine.ListObjectBean;
import net.business.engine.ListResult;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 奖金列表查询
 * @author Administrator
 *
 */
public class BonusListSearch implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;

		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("主键").getIndex();

		Map map =new HashMap();

		map.put("tfj", "拓房奖");
		map.put("fxj", "房销奖");
		map.put("dmj", "店盟奖");
		map.put("mxj", "盟销奖");
		map.put("jsj", "介绍奖");
		map.put("xsj", "销售奖");
		map.put("qjj", "清洁奖");
		map.put("zlj", "自来奖");
		map.put("klj", "客来奖");
		map.put("dxj", "店销奖");
		map.put("wxj", "网销奖");
		map.put("zxj", "总销奖");

		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);

				String sql = "select * from jjyb where zbGuid=? and gzlzt <> '2'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, lob.get(guidIndex).getOriginalValue());
				rs = ps.executeQuery();
				while(rs.next()){
					String jjlx = rs.getString("jjlx");     //奖金类型

					if(map.containsKey(jjlx)){
						String lx = (String)map.get(jjlx);
						int idx = context.getListObjectPara().getListObject().getListFieldByName(lx).getIndex();
						lob.get(idx).setListFieldValue("-1");
						
					}
				}
			}
		}
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
