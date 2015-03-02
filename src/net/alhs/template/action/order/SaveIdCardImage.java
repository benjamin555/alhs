package net.alhs.template.action.order;

import java.io.File;

import net.alhs.common.MsgInfo;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
/**
 * �洢���֤ͼƬ
 * @author Administrator
 *
 */
public class SaveIdCardImage implements I_TemplateAction{

	public int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����
		String [] cardNos = context.getReqParameterValues("c_539_306_rzr_sfz");

		String [] photos = context.getReqParameterValues("base64code");

		for (int i = 0; i < photos.length; i++) {
			String cardNo = cardNos[i];
			String photo = photos[i];

			//û��ͼƬ
			if(photo.trim().equals("")){
				continue;
			}

			String basePath = "/WEB-INF/idcard-images/";
			String path = basePath + cardNo + ".jpg"; //����jpg����gif����ʱ����jpg

			//��ȡ���ݣ�����ԭ��byte������
			byte[] contet = net.sysmain.util.StringTools.base64Decoder(photo);

			//ת�����ļ��ľ���·��
			path = context.getServletContext().getRealPath(path);

			//����Ŀ¼
			File dir = new File(context.getServletContext().getRealPath(basePath)); 
			dir.mkdirs();

			//�洢�ļ�
			java.io.FileOutputStream out = new java.io.FileOutputStream(path);
			out.write(contet);
		}
		
		/**
		 * ���Ͷ���
		 */
		String ddGuid = context.getReqParameter("guid");                 //����Guid
		String ddMsg = context.getReqParameter("ddMsg");                 //�Ƿ��Ͷ�������
		String addressMsg = context.getReqParameter("addressMsg");       //�Ƿ��͵�ַ����
		
		if(ddMsg!=null && ddMsg.equals("1")){
			MsgInfo.sendOrderMsg(ddGuid);
		}
		if(addressMsg!=null && addressMsg.equals("1")){
			MsgInfo.sendOrderAddressMsg(ddGuid);
		}
		
		/**
		 * ҵ��ȯʹ��
		 */
		useYZq(context);
		
		/**
		 * ��ʼ������������Ϣ(����ס)
		 */
		if(context.getReqParameter("ddzt").equals("2")){
			OrderFreeAction.initOrderFree(context.getReqParameter("guid"));
		}

		return SUCCESS;
	}

	/**
	 * ҵ��ȯʹ��
	 * @param context
	 * @throws Exception
	 */
	private void useYZq(TemplateContext context) throws Exception {
		String ddGuid = context.getReqParameter("guid");
		String [] yzqGuids = context.getReqParameterValues("yzqGuids");     //ҵ��ȯGuid
		String [] ddfjGuids = context.getReqParameterValues("ddfjGuids");   //��������GUID

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
