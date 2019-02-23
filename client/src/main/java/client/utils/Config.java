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
