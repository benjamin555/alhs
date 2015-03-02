package net.alhs.template.yezhu;

import java.util.Timer;
import javax.servlet.ServletContext;
import net.sysmain.common.I_Job;
import net.sysmain.util.StringTools;

public class YeZhuTimer implements I_Job{
	
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
         * 以小时为单位的定时器，timeLong是小时数，在参数文件中配置
         */
        timer.schedule(new YeZhuTotalInfo(), 0, timeLong * 1000);
	}


	public void setTrigger(String expression) {
		String[] arr = expression.split(";");
    	if(StringTools.isNumeric(arr[0])) timeLong = Long.parseLong(arr[0]);
    	
	}

	public void setJobName(String name) {
	}

}
