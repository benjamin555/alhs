package net.kml.template.action;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;

public class StorageLocation implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		String wlGuid = context.getRequest().getParameter("wlGuid");
		String cwmc = context.getRequest().getParameter("cwmc");
		String cwgs = context.getRequest().getParameter("cwgs");
		
		//System.out.println(wlGuid+"/"+cwmc+"/"+cwgs);
		
		context.getConn().createStatement().execute("update goods set cwmc='"+cwmc+"',cwgs="+cwgs+" where guid='"+wlGuid+"'");
		
		return SUCCESS;
	}

	public String getErrorMessage() {
		return null;
	}

}
