package com.wyd.channel.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.wyd.channel.bean.ChannelLogin;
import com.wyd.channel.bean.ChannelLoginHandle;
import com.wyd.channel.bean.ChannelLoginResult;

/**
 * 渠道登陆
 * 
 * @author zengxc
 * 
 */
public class ChannelLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1911747458628093909L;
	Logger log = Logger.getLogger(ChannelLoginServlet.class);

	//private static final String CONTENT_TYPE = "text/html";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
        Enumeration<?> params= request.getParameterNames();
        log.info("登陆请求信息:");
        String paramstr="";
        while(params.hasMoreElements()){
            String paramName=(String) params.nextElement();
            String []paramValues=request.getParameterValues(paramName);
            paramstr+=paramName+"="+paramValues[0]+"&";
        }
        log.info(paramstr);
        String channelStr = request.getParameter("channelid");
		String idStr = request.getParameter("serialno");
		int id = StringUtils.hasText(idStr)?Integer.parseInt(idStr):0;
		ChannelLogin loginParamter =getLoginParamter(request);
		if (channelStr == null) {
			out.write("接口准备就绪！");
		} else {
			ChannelService service = ChannelService.getInstance();
			ChannelLoginHandle handle = null;
			if(id==0){
				handle = service.createLoginHandle(loginParamter);
			}else{
				handle = service.getLoginHandle(id);
				if(handle==null){
					handle = new ChannelLoginHandle(id,new ChannelLoginResult("-1","登陆已效，请重新登陆"));
				}
				//查询后,从队列删除已完成的				
				service.complete(handle);
			}
			String result = handle.toJSON();
			System.out.println(result);
			out.write(result);			
		}

		out.flush();
		out.close();
	}
	
	private ChannelLogin getLoginParamter(HttpServletRequest request){
		ChannelLogin loginParamter =new ChannelLogin();
		loginParamter.setParameter(request.getParameterValues("data"));
		String channelStr = request.getParameter("channelid");
		int channelid = StringUtils.hasText(channelStr)?Integer.parseInt(channelStr):0;
		loginParamter.setChannel(channelid);
		return loginParamter;
	}
	
}
