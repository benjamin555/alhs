package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.business.engine.ListObjectBean;
import net.business.engine.ListResult;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 查询模版房型的业主房数和无主房数
 * @author Administrator
 *
 */
public class SearchHouseNum implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();
		ResultSet rst = null;

		Map map0 = new HashMap(0);    //业主房
		
		Map map = new HashMap(0);     //无主房

		String sql = "SELECT fxGuid,COUNT(*) FROM house WHERE fwzt='1' AND fxGuid IS NOT NULL GROUP BY fxGuid";

		rst= conn.createStatement().executeQuery(sql);

		while(rst.next()){
			String fxGuid = rst.getString(1);
			int cn = rst.getInt(2);
			map0.put(fxGuid, cn+"");
		}
		
	    sql = "SELECT fxGuid,COUNT(*) FROM house WHERE fwzt='3' AND fxGuid IS NOT NULL GROUP BY fxGuid";
	    
	    rst= conn.createStatement().executeQuery(sql);

		while(rst.next()){
			String fxGuid = rst.getString(1);
			int cn = rst.getInt(2);
			map.put(fxGuid, cn+"");
		}
		
		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);

				String fxGuid = lob.get(23).getOriginalValue();           //房型Guid
				
				//业主房
				if(map0.containsKey(fxGuid))
				{
					lob.get(6).setListFieldValue(map0.get(fxGuid)+"");
				}
				else
				{
					lob.get(6).setListFieldValue(0+"");
				}
				
				//无主房
				if(map.containsKey(fxGuid))
				{
					lob.get(7).setListFieldValue(map.get(fxGuid)+"");
				}
				else
				{
					lob.get(7).setListFieldValue(0+"");
				}
			}
		}
		return SUCCESS;
	}
	public String getErrorMessage() {
		return null;
	}
}
