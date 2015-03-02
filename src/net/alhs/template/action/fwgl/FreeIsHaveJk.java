package net.alhs.template.action.fwgl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.alhs.template.action.SelectJKZjeAction;
import net.business.engine.ListObjectBean;
import net.business.engine.ListResult;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * ������ϸ�Ƿ��н��
 * @author Administrator
 *
 */
public class FreeIsHaveJk implements I_TemplateAction{
	public int execute(TemplateContext context) throws Exception {
		ListResult lr = context.getListResult();
		Connection conn = context.getConn();
		
		int sqridIndex = context.getListObjectPara().getListObject().getListFieldByName("����id").getIndex();
		int jkjeIndex = context.getListObjectPara().getListObject().getListFieldByName("�����").getIndex();
		
		if(lr != null && lr.length() > 0){
			for(int i = 0; i < lr.length(); i++){
				ListObjectBean lob = lr.get(i);

				String sqrid = lob.get(sqridIndex).getOriginalValue();           //sqrGuid

				/**
				 * ����֯�����й�����xmid��Ϊ�գ������ѯ�Ƿ��н����
				 */
				if(sqrid!=null &&!sqrid.equals("")){
					String je = SelectJKZjeAction.getJkje(conn, sqrid);
					lob.get(jkjeIndex).setListFieldValue(je);
				}
				else{
					lob.get(jkjeIndex).setListFieldValue("0");
				}
			}
		}
		return SUCCESS;
	}
	public String getErrorMessage() {
		return null;
	}
}
