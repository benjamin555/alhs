package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.ResultSet;
import net.business.engine.ListObjectBean;
import net.business.engine.ListResult;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 查看是否有BOM
 * @author Administrator
 *
 */
public class SearchIsBOM implements I_TemplateAction{
	
	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();

		String sql = null;
	
		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);
				
				String houseGuid = lob.get(0).getOriginalValue();           //房屋Guid

				//System.out.println(houseGuid);

				boolean isBOM = false;

				if(houseGuid!=null){  
					sql = "select count(*) from fjbom w where w.houseGuid='"+houseGuid+"'";
					ResultSet rst = conn.createStatement().executeQuery(sql);
					if(rst.next() && rst.getInt(1)>0){
						isBOM = true;
					}
				}	
				lob.get(14).setListFieldValue((isBOM?1:0)+"");

			}
		}
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}
}
