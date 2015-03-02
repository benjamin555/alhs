package net.alhs.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

import net.sysmain.util.StringTools;

public class CalendarUtil implements Serializable
{   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public final static String[] weekName = new String[]{"<font color=red>星期日</font>", "星期一", "星期二", "星期三", 
                                                         "星期四", "星期五", "<font color=blue>星期六</font>"}; 
    
    public static ArrayList getMonthCalendar(HttpServletRequest request)
    {
        ArrayList list = new ArrayList();
        
        /**
         * 当前年、月
         */
        int year = 0;
        int month = 0; 
        
        int days = 0;
        Calendar cal = Calendar.getInstance();
        
        /**
         * 通过参数传入年和月
         */
        if(StringTools.isInteger(request.getParameter("year")))
        {
            year = Integer.parseInt(request.getParameter("year").trim());     
            if(year <1999 || year > 9999) year = 0;
        }
        if(StringTools.isInteger(request.getParameter("month")))
        {
            month = Integer.parseInt(request.getParameter("month").trim());
            if(month <0 || month > 12) month = 0;
        }
        
        if(year == 0) year = cal.get(Calendar.YEAR);
        if(month == 0) month = cal.get(Calendar.MONTH) + 1;

        net.chysoft.util.DateUtil dateUtil = new net.chysoft.util.DateUtil();
        //记录当前年和月
        request.setAttribute("month" , new Integer(month));
        request.setAttribute("year" , new Integer(year));
        
        
        StringBuffer yearStr= new StringBuffer("<select id=year onchange='changeDate();'>");
        //年
        for(int i = year-4; i <year+3; i++)
        {
            if(i==year)
            {
                yearStr.append("<option value=").append(i).append(" selected=selected>").append(i).append("</option>");
            }
            else
            {
                yearStr.append("<option value=").append(i).append(">").append(i).append("</option>");
            }
        }
        yearStr.append("</select>年");

        //月
        StringBuffer monthStr= new StringBuffer("&nbsp;<select id=month onchange='changeDate();'>");
        for(int i=1; i<13; i++)
        {
            if(i == month)
            {
                monthStr.append("<option value=").append(i).append(" selected=selected>").append(i).append("</option>");
            }
            else
            {
                monthStr.append("<option value=").append(i).append(">").append(i).append("</option>");
            }
        }
        monthStr.append("</select>月");
        
        request.setAttribute("yearSel", yearStr.toString());
        request.setAttribute("monthSel", monthStr.toString());
        
        /**
         * 返回一个月中的天数
         */
        days = dateUtil.getDays(month - 1, year);

        //计算周一到周六的时间段(星期日到星期一分别是  1 2 3 4 5 6 7, 星期日是1）
        
        
        /**
         * 返回当前的年、月、日
         */
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH)+1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("cDate", new int[]{d, m, y});

        /**
         * 设置为本月的1号
         */
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, month-1);
        //获取星期
        int week = cal.get(Calendar.DAY_OF_WEEK); 
        if(week == 1) week = 8;
        int weekInterVal1 = week - 2;  //距离星期一的天数
        
        /**
         * 设置为本月的最后一天
         */
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, days);
        cal.set(Calendar.MONTH, month-1);
        int week1 = cal.get(Calendar.DAY_OF_WEEK); 
        if(week1 == 1) week1 = 8;
        int weekInterVal2 = 8 - week1;  //距离星期日的天数
        
        /**
         * 1、上个月剩余的日期
         */
        if(weekInterVal1 > 0)
        {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_MONTH, 1); //到月初
            cal.set(Calendar.MONTH, month-1);
            //返回到到星期一 
            cal.add(Calendar.DAY_OF_MONTH, -weekInterVal1);
            int n = year;
            int lastDay = cal.get(Calendar.DAY_OF_MONTH);
            int x = month-1;
            if(x == 0) 
            {
                x = 12;
                n = n-1;
            }
            for(int i=lastDay; i<lastDay+weekInterVal1; i++)
            {
                list.add(new int[]{i, x, n});
            }
        }
        
        /**
         * 2、本月的天数
         */
        for(int i=1; i<=days; i++)
        {
            list.add(new int[]{i, month, year});
        }
        
        /**
         * 3、下个月的天数
         */
        if(weekInterVal2 > 0)
        {
            int x = month+1;
            int n = year;
            if(x == 13) 
            {
                x = 1;
                n = n + 1;
            }
            for(int i=1; i<=weekInterVal2; i++)
            {
                list.add(new int[]{i, x, n});
            }
        }
        
        return list;
        
    }
    
    /**
     * 返回周的相关信息
     * @param request
     * @return
     */
    public static ArrayList getWeekCalendar(HttpServletRequest request)
    {   
        /**
         * 当前年、月
         */
        int year = 0;
        int month = 0; 
        int day = 0;
        ArrayList list = new ArrayList();
        
        /**
         * 通过参数传入年和月
         */
        if(StringTools.isInteger(request.getParameter("year")))
        {
            year = Integer.parseInt(request.getParameter("year").trim());     
            if(year <1999 || year > 9999) year = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("month")))
        {
            month = Integer.parseInt(request.getParameter("month").trim());
            if(month <0 || month > 12) month = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("day")))
        {
            day = Integer.parseInt(request.getParameter("day").trim());
            if(day <0 || day > 31) month = 0;
        }
        
        //记录当前的年、月、日
        Calendar cal = Calendar.getInstance();
        int yy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("cDate", new int[]{yy, mm, dd});
        
        if(year == 0) year = cal.get(Calendar.YEAR);
        if(month == 0) month = cal.get(Calendar.MONTH) + 1;
        if(day == 0) day = cal.get(Calendar.DAY_OF_MONTH);
        
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month-1);
        
        int week = cal.get(Calendar.DAY_OF_WEEK); 
        if(week == 1) week = 8;
        int weekInterVal1 = week - 2;  //距离星期一的天数
        
        int y, m , d;
        
       
        //设置到周一
        if(weekInterVal1 > 0)
        {
            cal.add(Calendar.DAY_OF_MONTH, -weekInterVal1);
        }
        //判断是否在本周
        request.setAttribute("isCurrentWeek", isCurrentWeek(cal)?"1":"0");
        
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH) + 1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        list.add(new int[]{y, m, d});
        
        //返回年月日
        for(int i=1; i<7; i++)
        {
            //向后增加1天
            cal.add(Calendar.DAY_OF_MONTH, 1);
            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH) + 1;
            d = cal.get(Calendar.DAY_OF_MONTH);
            list.add(new int[]{y, m, d});
        }
        
        //下周的内容， 下周一
        cal.add(Calendar.DAY_OF_MONTH, 1);
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH) + 1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("nextWeek", new int[]{y, m, d});
        
        //上周的内容，上周一
        cal.add(Calendar.DAY_OF_MONTH, -14);
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH) + 1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        request.setAttribute("preWeek", new int[]{y, m, d});
        
        return list;
    }
    
   /**
    * 判断是否在同一周
    * @param cal0 周一的日期
    * @return
    */
    private static boolean isCurrentWeek(Calendar cal0)
    {
        Calendar cal = Calendar.getInstance();
                
        int week = cal.get(Calendar.DAY_OF_WEEK); 
        if(week == 1) week = 8;
        int weekInterVal1 = week - 2;  //距离星期一的天数
        
        //设置到周一
        if(weekInterVal1 > 0)
        {
            cal.add(Calendar.DAY_OF_MONTH, -weekInterVal1);
        }
        int y1 = cal.get(Calendar.YEAR);
        int m1 = cal.get(Calendar.MONTH);
        int d1 = cal.get(Calendar.DAY_OF_MONTH);
        
        int y2 = cal0.get(Calendar.YEAR);
        int m2 = cal0.get(Calendar.MONTH);
        int d2 = cal0.get(Calendar.DAY_OF_MONTH);
        
        return y1==y2 && m1==m2 && d1==d2;
    }
    
    /**
     * 返回日的相关信息
     * @param request
     * @return
     */
    public static int[] getDayCalendar(HttpServletRequest request)
    {   
        /**
         * 当前年、月
         */
        int year = 0;
        int month = 0; 
        int day = 0;
        
        /**
         * 通过参数传入年和月
         */
        if(StringTools.isInteger(request.getParameter("year")))
        {
            year = Integer.parseInt(request.getParameter("year").trim());     
            if(year <1999 || year > 9999) year = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("month")))
        {
            month = Integer.parseInt(request.getParameter("month").trim());
            if(month <0 || month > 12) month = 0;
        }
        
        if(StringTools.isInteger(request.getParameter("day")))
        {
            day = Integer.parseInt(request.getParameter("day").trim());
            if(day <0 || day > 31) month = 0;
        }
        
        Calendar cal = Calendar.getInstance();

        int yy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH)+1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
                
        if(year == 0) year = cal.get(Calendar.YEAR);
        if(month == 0) month = cal.get(Calendar.MONTH) + 1;
        if(day == 0) day = cal.get(Calendar.DAY_OF_MONTH);
        
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month-1);
        
        /**
         * 返回当前的年、月、日、星期
         */
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        int w = cal.get(Calendar.DAY_OF_WEEK)-1; 
        
        //判断选中的年、月、日是否是今天
        request.setAttribute("isToday", (yy==y && mm==m && dd == d)?"1":"0");
        
        return new int[]{y, m, d, w};
    }
    
    private StringBuffer title = new StringBuffer();
    
    public String getMonthDayHtml(int[] v, int selectedMonth, int[] cDate)
    {   
        if(title.length() > 0) title.delete(0, title.length());
        if(v[1] == selectedMonth) 
        {
            if(v[0]==cDate[0] && v[1]==cDate[1] && v[2]==cDate[2])
            {
                title.append("<b>");
                title.append(v[1]);
                title.append("月");
                title.append(v[0]);
                title.append("日</b>[<font color=\"#F05050\">今天</font>]");
            }
            else
            {
                title.append(v[1]);
                title.append("月");
                title.append(v[0]);
                title.append("日");
            }
        }
        else
        {
            if(v[0]==cDate[0] && v[1]==cDate[1] && v[2]==cDate[2])
            {
                title.append("<font color=\"#C0C0C0\"><b>");
                title.append(v[1]);
                title.append("月");
                title.append(v[0]);
                title.append("日</b>[<font color=\"#F05050\">今天</font>]");
            }
            else
            {
                title.append("<font color=\"#C0C0C0\">");
                title.append(v[1]);
                title.append("月");
                title.append(v[0]);
                title.append("日</font>");
           }
       }
       
       return title.toString();
    }
}
