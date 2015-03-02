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
 * �����б��ѯ
 * @author Administrator
 *
 */
public class BonusListSearch implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;

		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("����").getIndex();

		Map map =new HashMap();

		map.put("tfj", "�ط���");
		map.put("fxj", "������");
		map.put("dmj", "���˽�");
		map.put("mxj", "������");
		map.put("jsj", "���ܽ�");
		map.put("xsj", "���۽�");
		map.put("qjj", "��ཱ");
		map.put("zlj", "������");
		map.put("klj", "������");
		map.put("dxj", "������");
		map.put("wxj", "������");
		map.put("zxj", "������");

		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);

				String sql = "select * from jjyb where zbGuid=? and gzlzt <> '2'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, lob.get(guidIndex).getOriginalValue());
				rs = ps.executeQuery();
				while(rs.next()){
					String jjlx = rs.getString("jjlx");     //��������

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
