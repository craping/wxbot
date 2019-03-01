package client.utils;

import java.io.File;
import java.io.IOException;

import client.enums.OsNameEnum;

/**
 * 配置信息
 *
 */
public class Config {
	
	// 图片显示最大宽度
	public static final int MAX_IMG_WIDTH = 200;
	public static final String CHAT_RECORD_PATH = "resource/chat/";
	public static final String USERINFO_URL = "http://127.0.0.1:9527/user/getUserInfo?format=json";
	public static final String REGISTER_URL = "http://127.0.0.1:9527/user/register?format=json";
	public static final String LOGIN_URL = "http://127.0.0.1:9527/user/login?format=json";
	public static final String LOGOUT_URL = "http://127.0.0.1:9527/user/logout?format=json";

	/**
	 * 获取文件目录
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月8日 下午10:27:42
	 * @return
	 */
	public static String getLocalPath() {
		String localPath = null;
		try {
			localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return localPath;
	}

	/**
	 * 获取系统平台
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月8日 下午10:27:53
	 */
	public static OsNameEnum getOsNameEnum() {
		String os = System.getProperty("os.name").toUpperCase();
		if (os.indexOf(OsNameEnum.DARWIN.toString()) >= 0) {
			return OsNameEnum.DARWIN;
		} else if (os.indexOf(OsNameEnum.WINDOWS.toString()) >= 0) {
			return OsNameEnum.WINDOWS;
		} else if (os.indexOf(OsNameEnum.LINUX.toString()) >= 0) {
			return OsNameEnum.LINUX;
		} else if (os.indexOf(OsNameEnum.MAC.toString()) >= 0) {
			return OsNameEnum.MAC;
		}
		return OsNameEnum.OTHER;
	}
}
