package net.alhs.api;
import net.alhs.common.MsgTemplate;
import net.alhs.common.SendMobile;
import net.business.db.CodeFactory;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.common.ShutCutFactory;
import net.sysmain.util.StringTools;

/**
 * ���������ύ��ִ����
 * @author Administrator
 *
 */
public class HouseEndAction implements I_TemplateAction {

	public  int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // ��װ�ػ�����

		String guid = context.getReqParameter("guid");                        //����GUID
		int itemid = 0;                                                       //itemid
		int areaid = 0;                                                       //����id
		String yzGuid = context.getReqParameter("yzGuid");                    //ҵ��Guid
		String fjmc = context.getReqParameter("fwzb_fjmc");                   //��������
		String _fjfl = context.getReqParameter("fwzb_flid");                   
		int fjfl = Integer.parseInt(_fjfl);                                   //�������
		String _price = context.getReqParameter("fwzb_price");                //�۸�
		double price = Double.parseDouble(StringTools.isBlankStr(_price)? "0" : _price);
		String fptg = context.getReqParameter("fptg");                        //��Ʊ�ṩ(1.�ṩ 0.���ṩ)

		String qtmc = context.getReqParameter("qtxx_qtmc");                 //ǰ̨����
		String huayuan = context.getReqParameter("fwzb_xqhy");              //��԰	
		String fangxing = context.getReqParameter("fwzb_fangxing");         //��԰����
		String fangwudong = context.getReqParameter("fwzb_dong");           //���ݶ�
		String louceng = context.getReqParameter("fwzb_louceng");           //¥��
		String fangjianhao = context.getReqParameter("fwzb_fjh");  ;        //�����   

		String _htms = context.getReqParameter("fwzb_htms");                //��ͬģʽ
		String _fcbl = context.getReqParameter("fwzb_fcbl");
		double fcbl = Double.parseDouble(_fcbl.trim().equals("")?"0":_fcbl);  //�ֳɱ���


		String fx = context.getReqParameter("fwzb_fx");                       //����
		String _mj = context.getReqParameter("fwzb_fwmj");
		int mj = Integer.parseInt(_mj.trim().equals("")?"0":_mj);             //���

		String [] _bedNo = context.getReqParameterValues("c_120_258_wlxx_chuang");
		int bedNo = 0;                                                        //����
		if(_bedNo!=null && _bedNo.length>0){
			for (int i = 0; i < _bedNo.length; i++) { 
				bedNo+=Integer.parseInt(_bedNo[i].trim().equals("")?"0":_bedNo[i]);
			}
		}
		String _discount = context.getReqParameter("fwzb_zk");
		double discount = Double.parseDouble(StringTools.isBlankStr(_discount)? "0" :_discount); //�ۿ�

		String homeaddress = context.getReqParameter("fwzb_xxdz");            //��ϸ��ַ

		String _number = context.getReqParameter("fwzb_rsxz");              
		int number = Integer.parseInt(StringTools.isBlankStr(_number)? "0" : _number);           //��������

		String [] _cx = context.getReqParameterValues("c_120_258_wlxx_jc");       //����
		String cx = (_cx==null)?"":_cx[0];

		String [] _jg = context.getReqParameterValues("c_120_258_wlxx_jg");       //����
		String jg = (_jg==null)?"":_jg[0];

		String sqsj = context.getReqParameter("fwzb_sqsj");                   //����ʱ��

		String ksrq = context.getReqParameter("fwzb_ksrq");                   //��ʼ����
		String jsrq = context.getReqParameter("fwzb_jsrq");                   //��������
		
		String area = context.getReqParameter("fwzb_qu");                     //��
		String fxGuid = context.getReqParameter("fxGuid");                    //����Guid



		/**
		 * Ŀǰ���� http://localhost/homtrip/file/upload/oa/
		 * ��ʽ���ߺ�õ���Ϊ http://www.homtrip.com/file/upload/oa/
		 */
		//String url1 = "http://www.homtrip.com/file/upload/oa/";                                                     //ͼƬ����1
		//String url2 = "http://www.homtrip.com/file/upload/oa/";                                                     //ͼƬ����2
		//String url3 = "http://www.homtrip.com/file/upload/oa/";                                                     //ͼƬ����3

		/**
		String [] _fjpz = context.getReqParameterValues("c_61_725_2");      
		String fjpz = "";                                                     //�������ã���ʽ��1��2��3�������ֵ���ͳһ
		if(_fjpz!=null && _fjpz.length>0)
		{
			for (int i = 0; i < _fjpz.length; i++) {
				fjpz += _fjpz[i]+",";
			}
			fjpz = fjpz.substring(0, fjpz.length()-1);
		}
		String fjjs = context.getReqParameter("qtxx_fjjs");                   //�������
		String hjjs = context.getReqParameter("qtxx_hjjs");                   //��������
		String yljs = context.getReqParameter("qtxx_yljs");                   //���ֽ���
		String jtjs = context.getReqParameter("qtxx_jtjs");                   //��ͨ����
		String gwjs = context.getReqParameter("qtxx_gwjs");                   //�������
		String jdsljs = context.getReqParameter("qtxx_jdsljs");               //�������ý���
		 **/
		CodeFactory c = new CodeFactory();

		HouseBean house = new HouseBean();
		house.setGuid(guid);
		house.setAreaid(areaid);
		house.setItemid(itemid);
		house.setYzGuid(yzGuid);
		house.setFjmc(fjmc);
		house.setFjfl(fjfl);
		house.setPrice(price);
		house.setFptg(fptg);
		house.setFjpz("");
		//house.setKsrq(ksrq);
		//house.setJsrq(jsrq);

		//2014-05-31���ӵ��ֶ�
		house.setQtmc(qtmc);
		house.setHuayuan(huayuan);
		house.setFangxing(fangxing);
		house.setFangwudong(fangwudong);
		house.setLouceng(louceng);
		house.setFangjianhao(fangjianhao);
		house.setFangshu("ҵ����");
		house.setHtlb(c.getNameValueByCode("a","htms",_htms));           //��ͬ���
		house.setFcbl(fcbl);                                             //�ֳɱ���
		house.setBedtype(cx);    //����
		house.setJingguan(jg);   //����

		house.setSqsj(sqsj);
		house.setUrl1("");
		house.setUrl2("");
		house.setUrl3("");
		house.setFjjs("");
		house.setHjjs("");
		house.setYljs("");
		house.setJtjs("");
		house.setGwjs("");
		house.setJdsljs("");
		house.setFx(fx);
		house.setMj(mj);
		house.setBedNo(bedNo);
		house.setHomeaddress(homeaddress);
		house.setDiscount(discount);
		house.setNumber(number);
		
		house.setArea(area);       //��
		house.setFxGuid(fxGuid);   //����Guid


		//�޸�

		//if(context.getTemplatePara().getEditType() == 1){
		//HouseAction.UpdateHouseToWeb(house);
		//}
		//else{
		//HouseAction.addHouseToWeb(house);
		//}
		/**
		 * ͬ��
		 */
		HouseAction.addOrUpdateHouse(house);


		/**
		 * ���Ͷ���
		 */
		String sffsyzxx = context.getReqParameter("sffsyzxx");  //�Ƿ�����Ϣ

		if(sffsyzxx!=null && sffsyzxx.equals("1")){
			String name = context.getReqParameter("yzzl_name");         //ҵ������
			String yzsj = context.getReqParameter("MobilePhone");       //ҵ���ֻ�
			String yzj = context.getReqParameter("c_964_871_fkzq_yz");  //���⣨ȡ��һ����

			String [] values  = null;
			int id = Integer.parseInt(_htms);   //����ģ��id
			if(id==1){       //���ⷿ
				values = new String[]{name,homeaddress,ksrq,jsrq,yzj};
			}
			else if(id==2){  //�ֳɷ�
				values = new String[]{name,homeaddress,ksrq,jsrq,_fcbl};
			}
			//�������ޣ��������

			if(values != null && values.length>0){
				String content = MsgTemplate.getContent(id, values);
				
				new ShutCutFactory().sendMobileMessage(new String[]{yzsj}, content, -1, null, null);

			}
		}

		return SUCCESS;
	}
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
