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
 * 查看是否有合作
 * @author Administrator
 *
 */
public class SearchIsHz implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();
		
		int guidIndex = context.getListObjectPara().getListObject().getListFieldByName("有无合作").getIndex();
		
		Map map = new HashMap(0);

		String sql = "select yzguid,count(*) c from house where gzlzt>4 or gzlzt=2 group by yzGuid";

		ResultSet rst = conn.createStatement().executeQuery(sql);

		while(rst.next()){
			String yzGuid = rst.getString(1);
			int cn = rst.getInt(2);
			map.put(yzGuid, cn+"");
		}
		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);

				String yzGuid = lob.get(guidIndex).getOriginalValue();           //业主Guid

				if(map.containsKey(yzGuid))
				{
					lob.get(guidIndex).setListFieldValue(1+"");
				}
				else
				{
					lob.get(guidIndex).setListFieldValue(0+"");
				}
			}
		}
		return SUCCESS;
	}
	public String getErrorMessage() {
		return null;
	}
}
