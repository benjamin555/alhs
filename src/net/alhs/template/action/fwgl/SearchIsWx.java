package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.ResultSet;
import net.business.engine.ListObjectBean;
import net.business.engine.ListResult;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 查看是否有维修记录
 * @author Administrator
 *
 */
public class SearchIsWx implements I_TemplateAction{
	
	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();

		String sql = null;
	
		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);
				
				String houseGuid = lob.get(0).getOriginalValue();           //房屋Guid

				//System.out.println(houseGuid);

				boolean isWx = false;

				if(houseGuid!=null){  
					sql = "select w.guid from fwwx w where w.houseGuid='"+houseGuid+"'";
					ResultSet rst = conn.createStatement().executeQuery(sql);
					if(rst.next()){
						isWx = true;
					}
				}	
				lob.get(15).setListFieldValue((isWx?1:0)+"");

			}
		}
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
