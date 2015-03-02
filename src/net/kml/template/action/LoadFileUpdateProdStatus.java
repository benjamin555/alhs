package net.kml.template.action;

import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * @author Administrator
 * 物料上传文件，修改状态
 */
public class LoadFileUpdateProdStatus implements I_TemplateAction {
	public int execute(TemplateContext context) throws Exception {
		String wlGuid = context.getReqParameter("wlwj_gguid");	

		//      修改物料表信息
        	context.getConn().createStatement().executeUpdate("update goods set IsLoadFile='1' where guid='"+wlGuid+"'");
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
