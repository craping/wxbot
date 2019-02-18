package client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtil {
	public static void writeHtml(String html, String path, String fileName) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}

			fos = new FileOutputStream(new File(path + fileName));
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(html);
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

		/*
		 * File file = new File(name); Long filelength = file.length(); //获取文件长度 byte[]
		 * filecontent = new byte[filelength.intValue()]; try { FileInputStream in = new
		 * FileInputStream(file); in.read(filecontent); in.close(); } catch
		 * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) {
		 * e.printStackTrace(); } return new String(filecontent);//返回文件内容,默认编码
		 */ }

}
