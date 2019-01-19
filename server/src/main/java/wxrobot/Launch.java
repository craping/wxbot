package wxrobot;


import org.crap.jrain.core.Config;
import org.crap.jrain.core.launch.Boot;
import org.crap.jrain.core.launch.SpringBoot;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import wxrobot.server.HttpServer;

public class Launch {
	
	public static void main(String[] args) {
		Boot boot = new SpringBoot(new ClassPathXmlApplicationContext("applicationContext.xml"), new Config("wxrobot.server.pump"));
		try {
			HttpServer.start(boot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
