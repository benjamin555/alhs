package net.alhs.api;

import java.util.Timer;
import javax.servlet.ServletContext;
import net.sysmain.common.I_Job;
import net.sysmain.util.StringTools;

/**
 * 定时的将租金支付信息，写入网站会员中
 * @author Owner
 *
 */
public class WebRentInsert implements I_Job{
	
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
		//timer= new Timer();
        //timer.schedule(new WebRentAction(), 0, timeLong * 1000);
		java.util.concurrent.ScheduledExecutorService executor = java.util.concurrent.Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new WebRentAction(), 0, timeLong * 1000, java.util.concurrent.TimeUnit.MILLISECONDS); 

	}


	public void setTrigger(String expression) {
		String[] arr = expression.split(";");
    	if(StringTools.isNumeric(arr[0])) timeLong = Long.parseLong(arr[0]);
    	
	}

	public void setJobName(String name) {
	}

}
