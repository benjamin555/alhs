package net.alhs.template.action.order;

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
 * NoShow处理数量
 * @author Administrator
 *
 */
public class NoShowClNum implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();
		PreparedStatement ps =null;
		ResultSet rs = null;
		
		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("主键").getIndex();
		int zsIndex = context.getListObjectPara().getListObject().getListFieldByName("总数").getIndex();
		int yclIndex = context.getListObjectPara().getListObject().getListFieldByName("已处理数").getIndex();
		int wclIndex = context.getListObjectPara().getListObject().getListFieldByName("未处理数").getIndex();

		Map map = new HashMap();
		String sql = "SELECT ddzb.noshow noshow,COUNT(*) c,mx.zbGuid guid FROM noshowclmx mx,fwddzb ddzb WHERE mx.ddGuid=ddzb.guid and noshow is not null GROUP BY ddzb.guid ";
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		while(rs.next()){
			String guid = rs.getString("guid");
			String noshow =rs.getString("noshow");
			int c = rs.getInt("c");
			
			int yclNum = 0;
			if(noshow.equals("已处理")){
				yclNum = c;
			}
			
			if(map.containsKey(guid)){
				int [] arr = (int[])map.get(guid);
				int total = arr[0];
				int ycl = arr[1];
				map.put(guid, new int[]{total+c,ycl+yclNum});
			}
			
			else{
				map.put(guid, new int[]{c,yclNum});
			}
		}

		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				
				ListObjectBean lob = lr.get(i);

				String clGuid = lob.get(guidIndex).getOriginalValue();           
				
				if(map.containsKey(clGuid))
				{ 
					int [] c =(int []) map.get(clGuid);
					lob.get(zsIndex).setListFieldValue(c[0]+"");
					lob.get(yclIndex).setListFieldValue(c[1]+"");
					lob.get(wclIndex).setListFieldValue(c[0]-c[1]+"");
				}
			}
		}
		return SUCCESS;
	}
	public String getErrorMessage() {
		return null;
	}
}
