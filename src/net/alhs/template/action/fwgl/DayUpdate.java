package net.alhs.template.action.fwgl;

import java.util.Timer;
import javax.servlet.ServletContext;
import net.sysmain.common.I_Job;
import net.sysmain.util.StringTools;

public class DayUpdate implements I_Job{
	
	private Timer timer ;
	private long timeLong;
	
	
	public void destroy() throws Exception {
		if(timer != null) timer.cancel();
	}

	public void execute(ServletContext context) throws Exception {
		
	}

	public String getJobName() {
		return null;
	}

	public void init(ServletContext context) throws Exception 
    {
		timer= new Timer();
        /**
         * ��СʱΪ��λ�Ķ�ʱ����timeLong��Сʱ�����ڲ����ļ�������
         */
		timer.schedule(new HouseTimer(), 0, timeLong * 3600000);
        //���Ե�
        //timer.schedule(new HouseTimer(), 0, timeLong * 10000);
	}


	public void setTrigger(String expression) {
		String[] arr = expression.split(";");
    	if(StringTools.isNumeric(arr[0])) timeLong = Long.parseLong(arr[0]);
    	
	}

	public void setJobName(String name) {
	}

}
