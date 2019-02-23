package client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {

	private static final Logger logger = LogManager.getLogger(FileUtil.class);

	public static void main(String args[]) throws IOException {
		//String sep = File.separator;
		//String path = "d:" + sep + "chat1" + sep + "20190218";
		//String fileName = "/2.txt";
		//String content = "{1111}{2222}";
		// write(content, path, fileName);
		//String realPath = path + fileName;
		//System.out.println(readFile(realPath).size());
		//System.out.println(readFile(realPath).get(0));
	}

	/**
	 * 文件夹重命名
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static boolean renameFile(String oldPath, String newPath) {
		File file = new File(oldPath);
		if (file.exists()) {
			logger.error("目录[" + oldPath + "]不存在");
			return false;
		}
		return file.renameTo(new File(newPath));
	}

	/**
	 * 读取一个文本 一行一行读取
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> readFile(String path) throws IOException {
		// 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
		List<String> list = new ArrayList<String>();
		
		// 目录是否存在
		if (!(new File(path)).exists()) {
			logger.error("目录[" + path + "]不存在");
			return list;
		}
		
		FileInputStream fis = new FileInputStream(path);
		// 防止路径乱码 如果utf-8 乱码 改GBK eclipse里创建的txt 用UTF-8，在电脑上自己创建的txt 用GBK
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
			// 如果 t x t文件里的路径 不包含---字符串 这里是对里面的内容进行一个筛选
			if (line.lastIndexOf("---") < 0) {
				list.add(line);
			}
		}
		br.close();
		isr.close();
		fis.close();
		return list;
	}

	/**
	 * 写入文件，一行一行
	 * 
	 * @param path
	 * @param fileName
	 * @param content
	 */
	public static void writeFile(String path, String fileName, String content) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}

			fos = new FileOutputStream(new File(path + File.separator + fileName), true);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(content + System.getProperty("line.separator"));
			osw.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				osw.close();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getXmlString(String flie) {
		File xmlfile = new File(flie);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlfile), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.trim().equals("")) {
					sb.append(line);
				}
			}
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static boolean copy(String fileFrom, String fileTo) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new java.io.FileInputStream(fileFrom);
			out = new FileOutputStream(fileTo);
			byte[] bt = new byte[2048];
			int count;
			while ((count = in.read(bt)) > 0) {
				out.write(bt, 0, count);
			}
			return true;
		} catch (IOException ex) {
			return false;
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getString(String name) {
		String s = "";
		try {
			String encoding = "UTF-8"; // 字符编码(可解决中文乱码问题 )
			File file = new File(name);

			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = bufferedReader.readLine()) != null) {
					s += lineTXT.toString();
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件！");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容操作出错");
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 查询文件夹
	 * 
	 * @param path 目录
	 * @param mk   是否创建 true创建
	 */
	public static boolean mkdirs(String path, boolean mk) {
		try {
			File file = new File(path);
			if (!file.exists())
				return false;
			if (mk) {
				file.mkdir();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mk;
	}
}