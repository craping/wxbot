package client.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import client.pojo.WxMessage;
import client.view.server.BaseServer;

public class FileUtil {

	private static final Logger logger = LogManager.getLogger(FileUtil.class);

	public static void main(String args[]) throws IOException {	
		
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile("resource/674210262/20190316.txt", "r");
			long len = rf.length();
			long start = rf.getFilePointer();
			long nextend = start + len - 1;
			StringBuffer line = new StringBuffer();
			int count = 0;
			rf.seek(nextend);
			int c = -1;
			LinkedList<String> link = new LinkedList<>();
			
			while (nextend > start) {
				c = rf.read();
				if (c == '\n' || c == '\r') {
					if(line.length() > 0) {
						link.addFirst(new String(line.toString().getBytes("ISO-8859-1"), "utf-8"));
						System.out.println(new String(line.toString().getBytes("ISO-8859-1"), "utf-8"));
						line.setLength(0);
						count++;
					}
					nextend--;
				} else {
					line.insert(0, (char)c);
				}
				rf.seek(nextend);
				if(count == 1)
					break;
				nextend--;
				rf.seek(nextend);
				if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
					line.insert(0, (char)rf.read());
					System.out.println(new String(line.toString().getBytes("ISO-8859-1"), "utf-8"));
				}
				
			}
			System.out.println(nextend);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rf != null)
					rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getMimeType(String path) {
		path = path.split("[?]")[0];
		String ext = path.substring(path.lastIndexOf(".")+1);
		switch (ext) {
		case "html":
			return "text/html";
		case "css":
			return "text/css";
		case "js":
	        return "text/javascript";
	    case "svg":
	        return "image/svg+xml";
	    case "ttf":
	        return "application/x-font-truetype";
	    case "otf":
	        return "application/x-font-opentype";
	    case "eot":
	        return "application/vnd.ms-fontobject";
	    case "woff":
	        return "application/x-font-woff";
	    case "woff2":
	        return "application/x-font-woff";
		default:
			return "text/html";
		}
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
		if (!file.exists()) {
			logger.error("目录[" + oldPath + "]不存在");
			return false;
		}
		return file.renameTo(new File(newPath));
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

	/**
	 * 复制文件夹
	 * 
	 * @param fileFrom
	 * @param fileTo
	 * @return
	 */
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

	/**
	 * 获取文件内容
	 * 
	 * @param name
	 * @return
	 */
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

	/**
	 * 获取图片高度
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static int getImgHeight(String path) {
		int height = 0;
		File picture = new File(path);
		if (!picture.exists()) {
			logger.error("目录文件[" + path + "]不存在");
			return height;
		}
		try {
			BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
			height = sourceImg.getHeight(); // 源图高度
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return height;
	}

	/**
	 * 获取图片宽度
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static int getImgWidth(String path) {
		int width = 0;
		File picture = new File(path);
		if (!picture.exists()) {
			logger.error("getImgWidth 目录文件[" + path + "]不存在");
			return width;
		}
		try {
			BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
			width = sourceImg.getWidth(); // 源图宽度
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return width;
	}

	/**
	 * 计算获取文件大小
	 * 
	 * @param path -> 文件目录
	 * @return 文件大小->long
	 */
	@SuppressWarnings("resource")
	public static long getFileSize(String path) {
		long fileSize = 0;
		FileChannel fc = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				logger.error("getFileSize 目录文件[" + path + "]不存在");
				return fileSize;
			}
			fc = new FileInputStream(file).getChannel();
			fileSize = fc.size();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fc) {
				try {
					fc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileSize;
	}

	/**
	 * 计算获取文件大小 
	 * 
	 * @param fileUrl 文件目录
	 * @return 1B 1KB 1MB 1G 文字
	 */
	public static String getFileSizeString(String fileUrl) {
		return getFileSizeString(getFileSize(fileUrl));
	}
	
	/**
	 * 计算获取文件大小 
	 * 
	 * @param size 文件大小->long
	 * @return 1B 1KB 1MB 1G 文字
	 */
	public static String getFileSizeString(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		double value = (double) size;
		if (value < 1024) {
			return String.valueOf(value) + "B";
		} else {
			value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (value < 1024) {
			return String.valueOf(value) + "KB";
		} else {
			value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		}
		if (value < 1024) {
			return String.valueOf(value) + "MB";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			return String.valueOf(value) + "GB";
		}
	}
}