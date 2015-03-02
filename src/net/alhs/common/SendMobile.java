package net.alhs.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import net.chysoft.common.I_MobileMessage;

/**
 * 家游网短信接口
 * @author Owner
 *
 */
public class SendMobile implements I_MobileMessage 
{

	/**
	 * 家游网目前采用的调用地址
	 */
	private final static String PLAT_URL = "http://jiekou.zh106.com/sms/HttpInterface.aspx?comid=1930&username=homtrip&userpwd=h1718omtrip&smsnumber=10690&handtel=";
	
	public String sendMobileMessage(String[] phones, String content, long delay)
	{
		try
		{
			//System.out.println(content);
			
			for(int i=0; i<phones.length; i++)
			{
				content = content.replaceFirst("\\[OA系统发送:管理员\\]", "");				
				//System.out.println(PLAT_URL + phones[i] + "&sendcontent=" + content + "，【家游网】");
				String sUrl = PLAT_URL + phones[i] + "&sendcontent=" + URLEncoder.encode(content + "，【家游网】", "gb2312");
				URL url = new URL(sUrl);
				
			    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			    httpCon.connect();
			    BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			    String line = in.readLine();
			    int i_ret = httpCon.getResponseCode();
			    String sRet = httpCon.getResponseMessage();
			    //System.out.println("sRet   is:   " + sRet);
			    //System.out.println("i_ret   is:   " + i_ret);
			    
			    /**
			     * 间隔200毫秒发送
			     */
			    Thread.sleep(100);
		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return ex.getMessage();
		}
		
		return null;
	}

	public void setSender(String arg0, String arg1) 
	{
		// TODO Auto-generated method stub

	}
}
