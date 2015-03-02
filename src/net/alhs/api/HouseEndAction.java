package net.alhs.api;
import net.alhs.common.MsgTemplate;
import net.alhs.common.SendMobile;
import net.business.db.CodeFactory;
import net.business.engine.common.I_TemplateAction;
import net.business.engine.common.TemplateContext;
import net.chysoft.common.ShutCutFactory;
import net.sysmain.util.StringTools;

/**
 * 房间数据提交后执行类
 * @author Administrator
 *
 */
public class HouseEndAction implements I_TemplateAction {

	public  int execute(TemplateContext context) throws Exception {
		//Operator op = Authentication.getUserFromSession(context.getRequest()); // 封装回话对象

		String guid = context.getReqParameter("guid");                        //房间GUID
		int itemid = 0;                                                       //itemid
		int areaid = 0;                                                       //区域id
		String yzGuid = context.getReqParameter("yzGuid");                    //业主Guid
		String fjmc = context.getReqParameter("fwzb_fjmc");                   //房间名称
		String _fjfl = context.getReqParameter("fwzb_flid");                   
		int fjfl = Integer.parseInt(_fjfl);                                   //房间分类
		String _price = context.getReqParameter("fwzb_price");                //价格
		double price = Double.parseDouble(StringTools.isBlankStr(_price)? "0" : _price);
		String fptg = context.getReqParameter("fptg");                        //发票提供(1.提供 0.不提供)

		String qtmc = context.getReqParameter("qtxx_qtmc");                 //前台名称
		String huayuan = context.getReqParameter("fwzb_xqhy");              //花园	
		String fangxing = context.getReqParameter("fwzb_fangxing");         //花园房型
		String fangwudong = context.getReqParameter("fwzb_dong");           //房屋栋
		String louceng = context.getReqParameter("fwzb_louceng");           //楼层
		String fangjianhao = context.getReqParameter("fwzb_fjh");  ;        //房间号   

		String _htms = context.getReqParameter("fwzb_htms");                //合同模式
		String _fcbl = context.getReqParameter("fwzb_fcbl");
		double fcbl = Double.parseDouble(_fcbl.trim().equals("")?"0":_fcbl);  //分成比例


		String fx = context.getReqParameter("fwzb_fx");                       //房型
		String _mj = context.getReqParameter("fwzb_fwmj");
		int mj = Integer.parseInt(_mj.trim().equals("")?"0":_mj);             //面积

		String [] _bedNo = context.getReqParameterValues("c_120_258_wlxx_chuang");
		int bedNo = 0;                                                        //床数
		if(_bedNo!=null && _bedNo.length>0){
			for (int i = 0; i < _bedNo.length; i++) { 
				bedNo+=Integer.parseInt(_bedNo[i].trim().equals("")?"0":_bedNo[i]);
			}
		}
		String _discount = context.getReqParameter("fwzb_zk");
		double discount = Double.parseDouble(StringTools.isBlankStr(_discount)? "0" :_discount); //折扣

		String homeaddress = context.getReqParameter("fwzb_xxdz");            //详细地址

		String _number = context.getReqParameter("fwzb_rsxz");              
		int number = Integer.parseInt(StringTools.isBlankStr(_number)? "0" : _number);           //人数限制

		String [] _cx = context.getReqParameterValues("c_120_258_wlxx_jc");       //床型
		String cx = (_cx==null)?"":_cx[0];

		String [] _jg = context.getReqParameterValues("c_120_258_wlxx_jg");       //景观
		String jg = (_jg==null)?"":_jg[0];

		String sqsj = context.getReqParameter("fwzb_sqsj");                   //申请时间

		String ksrq = context.getReqParameter("fwzb_ksrq");                   //开始日期
		String jsrq = context.getReqParameter("fwzb_jsrq");                   //结束日期
		
		String area = context.getReqParameter("fwzb_qu");                     //区
		String fxGuid = context.getReqParameter("fxGuid");                    //房型Guid



		/**
		 * 目前先用 http://localhost/homtrip/file/upload/oa/
		 * 正式上线后得调整为 http://www.homtrip.com/file/upload/oa/
		 */
		//String url1 = "http://www.homtrip.com/file/upload/oa/";                                                     //图片链接1
		//String url2 = "http://www.homtrip.com/file/upload/oa/";                                                     //图片链接2
		//String url3 = "http://www.homtrip.com/file/upload/oa/";                                                     //图片链接3

		/**
		String [] _fjpz = context.getReqParameterValues("c_61_725_2");      
		String fjpz = "";                                                     //房间配置（格式：1，2，3）数据字典需统一
		if(_fjpz!=null && _fjpz.length>0)
		{
			for (int i = 0; i < _fjpz.length; i++) {
				fjpz += _fjpz[i]+",";
			}
			fjpz = fjpz.substring(0, fjpz.length()-1);
		}
		String fjjs = context.getReqParameter("qtxx_fjjs");                   //房间介绍
		String hjjs = context.getReqParameter("qtxx_hjjs");                   //环境介绍
		String yljs = context.getReqParameter("qtxx_yljs");                   //娱乐介绍
		String jtjs = context.getReqParameter("qtxx_jtjs");                   //交通介绍
		String gwjs = context.getReqParameter("qtxx_gwjs");                   //购物介绍
		String jdsljs = context.getReqParameter("qtxx_jdsljs");               //景点商旅介绍
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

		//2014-05-31增加的字段
		house.setQtmc(qtmc);
		house.setHuayuan(huayuan);
		house.setFangxing(fangxing);
		house.setFangwudong(fangwudong);
		house.setLouceng(louceng);
		house.setFangjianhao(fangjianhao);
		house.setFangshu("业主房");
		house.setHtlb(c.getNameValueByCode("a","htms",_htms));           //合同类别
		house.setFcbl(fcbl);                                             //分成比例
		house.setBedtype(cx);    //床型
		house.setJingguan(jg);   //景观

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
		
		house.setArea(area);       //区
		house.setFxGuid(fxGuid);   //房型Guid


		//修改

		//if(context.getTemplatePara().getEditType() == 1){
		//HouseAction.UpdateHouseToWeb(house);
		//}
		//else{
		//HouseAction.addHouseToWeb(house);
		//}
		/**
		 * 同步
		 */
		HouseAction.addOrUpdateHouse(house);


		/**
		 * 发送短信
		 */
		String sffsyzxx = context.getReqParameter("sffsyzxx");  //是否发送信息

		if(sffsyzxx!=null && sffsyzxx.equals("1")){
			String name = context.getReqParameter("yzzl_name");         //业主姓名
			String yzsj = context.getReqParameter("MobilePhone");       //业主手机
			String yzj = context.getReqParameter("c_964_871_fkzq_yz");  //月租（取第一条）

			String [] values  = null;
			int id = Integer.parseInt(_htms);   //短信模版id
			if(id==1){       //包租房
				values = new String[]{name,homeaddress,ksrq,jsrq,yzj};
			}
			else if(id==2){  //分成房
				values = new String[]{name,homeaddress,ksrq,jsrq,_fcbl};
			}
			//其它暂无，后续添加

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
