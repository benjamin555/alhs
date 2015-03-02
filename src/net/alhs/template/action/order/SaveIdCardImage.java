package net.alhs.template.action.order;

import java.io.File;

import net.alhs.common.MsgInfo;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * 存储身份证图片
 * @author Administrator
 *
 */
public class SaveIdCardImage implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象
		String [] cardNos = context.getReqParameterValues("c_539_306_rzr_sfz");

		String [] photos = context.getReqParameterValues("base64code");

		for (int i = 0; i < photos.length; i++) {
			String cardNo = cardNos[i];
			String photo = photos[i];

			//没有图片
			if(photo.trim().equals("")){
				continue;
			}

			String basePath = "/WEB-INF/idcard-images/";
			String path = basePath + cardNo + ".jpg"; //看是jpg还是gif，暂时采用jpg

			//获取数据，并还原成byte的数据
			byte[] contet = net.sysmain.util.StringTools.base64Decoder(photo);

			//转换成文件的绝对路径
			path = context.getServletContext().getRealPath(path);

			//创建目录
			File dir = new File(context.getServletContext().getRealPath(basePath)); 
			dir.mkdirs();

			//存储文件
			java.io.FileOutputStream out = new java.io.FileOutputStream(path);
			out.write(contet);
		}
		
		/**
		 * 发送短信
		 */
		String ddGuid = context.getReqParameter("guid");                 //订单Guid
		String ddMsg = context.getReqParameter("ddMsg");                 //是否发送订单短信
		String addressMsg = context.getReqParameter("addressMsg");       //是否发送地址短信
		
		if(ddMsg!=null && ddMsg.equals("1")){
			MsgInfo.sendOrderMsg(ddGuid);
		}
		if(addressMsg!=null && addressMsg.equals("1")){
			MsgInfo.sendOrderAddressMsg(ddGuid);
		}
		
		/**
		 * 业主券使用
		 */
		useYZq(context);
		
		/**
		 * 初始化订单核算信息(已入住)
		 */
		if(context.getReqParameter("ddzt").equals("2")){
			OrderFreeAction.initOrderFree(context.getReqParameter("guid"));
		}

		return SUCCESS;
	}

	/**
	 * 业主券使用
	 * @param context
	 * @throws Exception
	 */
	private void useYZq(TemplateContext context) throws Exception {
		String ddGuid = context.getReqParameter("guid");
		String [] yzqGuids = context.getReqParameterValues("yzqGuids");     //业主券Guid
		String [] ddfjGuids = context.getReqParameterValues("ddfjGuids");   //订单房价GUID

		if(yzqGuids!=null && yzqGuids.length>0){
			for (int i = 0; i < yzqGuids.length; i++) {
				String fjGuid = ddfjGuids[i];
				String yzqGuid = yzqGuids[i];
				if(yzqGuid.trim().equals("")){
					continue;
				}
				String sql = "update yzq set ddGuid='"+ddGuid+"',ddfjGuid='"+fjGuid+"',zt='1' where guid in("+yzqGuid+")";
				context.getConn().createStatement().executeUpdate(sql);
			}
		}
	}

	public String getErrorMessage() {
		return null;
	}
}
