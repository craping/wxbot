package wxrobot.server.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Character.UnicodeBlock;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：基础操作函数集合
 */
@SuppressWarnings({ "rawtypes"})
public class Tools {
	
	//private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 排序 获得 Aa-Zz akey=avalue&bkey=bvalue
	 * 
	 * @param str userid=1025499&storeid=1037833
	 * @return
	 */
	public static Map getContentForString(String str) {
		String[] strs = str.split("&");
		Map<String, String> params = new HashMap<>();
		for (int i = 0; i < strs.length; i++) {
			String key = strs[i].split("=")[0];
			String value = strs[i].split("=")[1];
			params.put(key, value);
		}
		return params;
	}
	
	/**
	 * 排序 获得 Aa-Zz akey=avalue&bkey=bvalue
	 * 
	 * @param str userid=1025499&storeid=1037833
	 * @return
	 */
	public static Map getContent(String str) {
		String[] strs = str.split("&");
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < strs.length; i++) {
			String key = strs[i].split("=")[0];
			String value = strs[i].split("=")[1];
			params.put(key, value);
		}
		return params;
	}
	
	/**
	 * HashMap按值进行排序
	 * @param map
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V>  sortByValue( Map<K, V> map ){  
	    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );  
	    Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
	        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {  
	            return (o1.getValue()).compareTo( o2.getValue() );  
	        }  
	    });  
	
	    Map<K, V> result = new LinkedHashMap<K, V>();  
	    for (Map.Entry<K, V> entry : list) {  
	        result.put( entry.getKey(), entry.getValue() );  
	    }  
	    return result;  
	}  

	/**
	 * 检查字符串是否为空；如果字符串为null,或空串，或全为空格，返回true;否则返回false
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isStrEmpty(String str) {
		if ((str != null) && (str.trim().length() > 0)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * @param str 1:a,2:b,3:c
	 * @return
	 */
	public static Map<String, String> split(String str) {
		Map<String, String> result = new HashMap<>();
		String[] first = str.split(",");
		for (int i = 0; i < first.length; i++) {
			String[] second = first[i].split(":");
			result.put(second[0], second[1]);
		}
		return result;
	}

	/**
	 * 去除字符串的前后空格；如果字符串为null,返回空串;
	 * 
	 * @param str 输入字符串
	 * @return 处理的后字符串
	 */
	public static String ruleStr(String str) {
		if (str == null) {
			return "";
		} else {
			return str.trim();
		}
	}

	/**
	 * 字符串转码，把GBK转ISO-8859-1
	 * 
	 * @param str GBK编码的字符串
	 * @return ISO-8859-1编码的字符串
	 */
	public static String GBK2Unicode(String str) {
		try {
			str = new String(str.getBytes("GBK"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
		}
		;
		return str;
	}
	
	/**
	 * 字符串转码，把UTF-8转unicode
	 * 
	 * @param str utf-8编码的字符串
	 * @return unicode编码的字符串
	 */
	public static String UTF82Unicode(String str) {
		char[] myBuffer = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
         UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if(ub == UnicodeBlock.BASIC_LATIN){
	             //英文及数字等
	             sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            	//全角半角字符
            	int j = (int) myBuffer[i] - 65248;
            	sb.append((char)j);
            } else {
            	//汉字
            	short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u"+hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
	}
	
	/**
     * 解码 Unicode \\uXXXX
     * @param str
     * @return
     */
    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher( str );
        int start = 0 ;
        int start2 = 0 ;
        StringBuffer sb = new StringBuffer();
        while( m.find( start ) ) {
            start2 = m.start() ;
            if( start2 > start ){
                String seg = str.substring(start, start2) ;
                sb.append( seg );
            }
            String code = m.group( 1 );
            int i = Integer.valueOf( code , 16 );
            byte[] bb = new byte[ 4 ] ;
            bb[ 0 ] = (byte) ((i >> 8) & 0xFF );
            bb[ 1 ] = (byte) ( i & 0xFF ) ;
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append( String.valueOf( set.decode(b) ).trim() );
            start = m.end() ;
        }
        start2 = str.length() ;
        if( start2 > start ){
            String seg = str.substring(start, start2) ;
            sb.append( seg );
        }
        return sb.toString() ;
    }

	/**
	 * 字符串转码，把GBK转ISO-8859-1
	 * 
	 * @param str ISO-8859-1编码的字符串
	 * @return GBK编码的字符串
	 */
	public static String Unicode2GBK(String str) {
		try {
			str = new String(str.getBytes("ISO-8859-1"), "GBK");
		} catch (java.io.UnsupportedEncodingException e) {
		}
		;
		return str;
	}

	/**
	 * 以字符串的格式取系统时间;格式：YYYYMMDDHHMMSS
	 * 
	 * @return 时间字符串
	 */
	public static String getSysTime() {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(new java.util.Date());
	}

	/**
	 * 以字符串的格式取系统日期;格式：YYYYMMDD
	 * 
	 * @return 日期字符串
	 */
	public static String getSysDate() {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
		return df.format(new java.util.Date());
	}

	public static String getYesterday(String format) {
		Date d1 = new Date();
		long myTime = (d1.getTime() / 1000) - 60 * 60 * 24;
		d1.setTime(myTime * 1000);
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.getDefault());
		return simpleFormat.format(d1);
	}

	/**
	 * 按输入的时间格式获取时间串
	 * 
	 * @param format ： 时间的格式 ， 如：yyyy-MM-dd HH:mm:ss ， yyyyMMddHHmmss
	 * @return ： 时间字符串
	 */
	public static String getSysTimeFormat(String format) {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(format);
		return df.format(new java.util.Date());
	}

	/**
	 * 判断字符串是否是有效的日期字符
	 * 
	 * @param d 需要判断的日期字符串
	 * @param format java日期格式
	 * @return true:有效日期 false：无效日期
	 */
	public static boolean isDay(String d, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			sdf.parse(d);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断字符串的日期是否已经超过当前系统时间
	 * 
	 * @param d 需要判断的日期字符串
	 * @param format java日期格式
	 * @return true:已过期 false：未过期
	 */
	public static boolean isOverTime(String d, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			Date date = sdf.parse(d);
			Date sys = new Date();

			if (sys.compareTo(date) == -1) {
				return false;
			}
		} catch (Exception e) {
			return true;
		}
		return true;
	}
	
	/**
	 * 校验时间是否过期
	 * 
	 * @param verifyTime 时间戳：IOS 安卓 10位
	 * @param limit 超时单位：分钟
	 * @return
	 */
	public static boolean isOverTime(Long verifyTime, int limit) {
		long currentTimeSecond = Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0, 13));
		verifyTime = Long.parseLong(String.valueOf(verifyTime));
		Long IntervalTime = (currentTimeSecond - verifyTime);
		if (IntervalTime > (limit * 60)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串的日期与当前系统时间是否已经超过指定的时间差
	 * 
	 * @param d 需要判断的日期字符串
	 * @param format java日期格式
	 * @param limit 时间差 秒
	 * @return true:已过期 false：未过期
	 */
	public static boolean isOverTimeLimit(String d, String format, long limit) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			Date date = sdf.parse(d);
			long now = System.currentTimeMillis();
			long base = date.getTime();

			if ((now - base) > (limit * 1000)) {
				return true;
			}
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是正确的日期
	 * 
	 * @param d String 需要判断的日期字符串
	 * @return boolean
	 */
	public static boolean isDay(String d) {
		int len;
		if (d != "") {
			len = d.length();
		} else {
			return true;
		}

		if (len == 0) {
			return true;
		}
		if (len != 8) {
			return false;
		}

		int year, month, day;

		try {
			year = Integer.parseInt(d.substring(0, 4));
			month = Integer.parseInt(d.substring(4, 6));
			day = Integer.parseInt(d.substring(6, 8));
		} catch (NumberFormatException e) {
			return false;
		}

		if (!((1 <= month) && (12 >= month) && (31 >= day) && (1 <= day))) {
			return false;
		}
		if (!((year % 4) == 0) && (month == 2) && (day == 29)) {
			return false;
		}
		if ((month <= 7) && ((month % 2) == 0) && (day >= 31)) {
			return false;
		}
		if ((month >= 8) && ((month % 2) == 1) && (day >= 31)) {
			return false;
		}
		if ((month == 2) && (day == 30)) {
			return false;
		}

		return true;
	}

	/**
	 * 把字符串转成UTF8的WAP码；用于手机显示；通过iso-8859-1进行编码的转换，所以对输入字符串的编码没有要求
	 * 
	 * @param gbStr 原字符串
	 * @return 转码后的字符串
	 */
	public static String convertUTF8(String gbStr) {
		try {
			byte b[] = gbStr.getBytes("iso-8859-1");
			String sg = new String(b, "gbk");
			String s = GB2UTF8(sg);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 把gb2312编码的字符串转utf8的wap编码,用于手机显示
	 * 
	 * @param gbString gb2312编码的原字符串
	 * @return String 转码后的字符串
	 */
	public static String GB2UTF8(final String gbString) {
		if (gbString == null) {
			return "";
		}
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			if (utfBytes[byteIndex] >= '!' && utfBytes[byteIndex] <= '~') {
				unicodeBytes += utfBytes[byteIndex];
			} else {
				String hexB = Integer.toHexString(utfBytes[byteIndex]);
				if (hexB.length() <= 2) {
					hexB = "00" + hexB;
				}
				unicodeBytes = unicodeBytes + "&#x" + hexB + ";";
			}
		}
		return unicodeBytes;
	}

	/**
	 * UTF-8的String串转换成GBK
	 * 
	 * @param strValue 原字符串
	 * @return 转码后的字符串
	 */
	public static String UTF82GB(String strValue) {
		if (strValue == null || strValue.trim().length() == 0)
			return "";

		StringBuffer strbuf = new StringBuffer();
		int pos = 0;
		String[] strarr = strValue.split(";");

		for (int i = 0; i < strarr.length; i++) {
			pos = strarr[i].toLowerCase().indexOf("&#x");
			if (pos >= 0) {
				String pre = strarr[i].substring(0, pos);
				strbuf.append(pre);
				String tmp = strarr[i].substring(pos + 3);
				if (tmp.startsWith("00")) {
					tmp = tmp.substring(2);
				}
				int l = Integer.valueOf(tmp, 16).intValue();
				strbuf.append((char) l);
			} else {
				strbuf.append(strarr[i]);
			}
		}

		return strbuf.toString();
	}

	/**
	 * base64解码
	 * 
	 * @param ss String 原字符串
	 * @return String base64解码后的字符串
	 */
	public static String base64Decoder(final String ss) {
		try {
			Decoder decoder = Base64.getDecoder();
			return new String(decoder.decode(ss));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * base64编码
	 * 
	 * @param ss
	 *            String 原字符串
	 * @return String base64编码后的字符串
	 */
	public static String base64Encoder(final String ss) {
		Encoder encoder = Base64.getEncoder(); // base64编码
		return encoder.encodeToString(ss.getBytes());
	}

	/**
	 * 取得系统时间，为了提高效率，所以未将时间格式化。目前用于DEBUG类中。
	 * 
	 * @return Date 日期-时间
	 */
	public static Date getSystemDate() {
		return new Date();
	}

	/**
	 * 取得长整形转化的字符串
	 * 
	 * @param length int 位数。最长10位
	 * @return String
	 */
	public static String getRandomInt(int length) {
		int iMax = (int) (Math.pow(10, length) - Math.pow(10, length - 1) - 1);
		Random random = new Random();
		int iRandom = random.nextInt(iMax) + (int) Math.pow(10, length - 1);
		if (iRandom < 0) {
			iRandom = (-iRandom);
		}
		return Integer.toString(iRandom);
	}

	private static long jExt = 1;
	private static long pre = System.currentTimeMillis();

	/**
	 * 获取不重复的流水号
	 * 
	 * @return String 流水号
	 */
	public static synchronized String getJournal() {
		long now = System.currentTimeMillis();
		if (now == pre) {
			return (now + "" + jExt++);
		} else {
			pre = now;
			jExt = 1;
			return (now + "");
		}
	}
	
	/**
	 * 获取全局唯一标识符
	 * @return
	 */
	public static String getUuid(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 格式化数字串
	 * 
	 * @param number 数字字符串
	 * @param format java数字格式， 如："#######0.00"
	 * @return 格式化好的字符串
	 */
	public static String numberFomat(String number, String format) {

		if (number == null)
			number = "";

		String checkExpressions = "^[\\-\\d\\.]+$";
		// 把包含.和数字以外的字符先行过滤掉
		// 如:12f,12.13d等
		if (!Pattern.matches(checkExpressions, number)) {
			throw new java.lang.NumberFormatException();
		}

		DecimalFormat df = new DecimalFormat(format);
		return df.format(Double.parseDouble(number));
	}

	/**
	 * 
	 * 功能：将数据库中查询出来的日期格式 yyyy-mm-dd hh24:mi:ss 转换成 yyyymmddhh24miss
	 * 
	 * @param date yyyy-mm-dd hh24:mi:ss的字符串
	 * @return yyyymmddhh24miss的字符串
	 */
	public static String dbToJavaDate(String date) {
		if (isStrEmpty(date)) {
			return "";
		}
		String tmp = new String(date);
		tmp = tmp.replaceAll("-", "");
		tmp = tmp.replaceAll(":", "");
		tmp = tmp.replaceAll(" ", "");
		return tmp;
	}

	/**
	 * 检查数字串格式，使用于检查金额，费率等
	 * 
	 * @param data 要检查的数据
	 * @param iDotPos 允许小数位，0为没有小数点
	 * @param b0isErr 值为0时返回错
	 * @return ： 0－格式正确， 其他－格式错误
	 */
	public static int iCheckNumber(String data, int iDotPos, boolean b0isErr) {
		if (isStrEmpty(data)) {
			return 1;
		}
		boolean bDot = false;
		for (int i = data.length() - 1; i >= 0; i--) {
			char oneChar = data.charAt(i);
			// 检查小数点
			if ((oneChar == '.') && (!bDot)) {
				if ((data.length() - 1 - i) > iDotPos) {
					return 2;
				}
				bDot = true;
				continue;
			}
			// 若开始为'-',为负数
			if ((oneChar == '-') && (i == 0)) {
				continue;
			}
			if (oneChar < '0' || oneChar > '9') {
				return 3;
			}
		}
		if (b0isErr) {
			double dd = Double.parseDouble(data);
			if (dd == 0) {
				return 4;
			}
		}
		return 0;
	}

	/**
	 * 检查字符串是否只是由数字或字母组成
	 * 
	 * @param str 需要被检查的字符串
	 * @return ： true－被检查的串中只有数字或字母，false-含有其它字符
	 */
	public static boolean checkNumOrLetter(String str) {
		if (str == null) {
			return false;
		}
		String checkExpressions;
		checkExpressions = "^[a-zA-Z0-9]+$";
		return Pattern.matches(checkExpressions, str);
	}

	/**
	 * 检查字符串是否表示金额，此金额小数点后最多带2位
	 * 
	 * @return ： true－表示金额，false-不表示金额
	 */
	public static boolean checkAmount(String amount) {
		if (amount == null) {
			return false;
		}
		String checkExpressions;
		checkExpressions = "^([1-9]\\d*|[0])\\.\\d{1,2}$|^[1-9]\\d*$|^0$";
		return Pattern.matches(checkExpressions, amount);
	}

	/**
	 * 功能：检查传入字符串是否为手机号码 检查规则:13或15开头后面9位数字
	 * 
	 * @param mobileNo
	 * @return
	 */
	public static boolean isMobileNo(String mobileNo) {
		if (mobileNo == null) {
			return false;
		}
		String checkExpressions;
		checkExpressions = "^[1][358]\\d{9}$";
		return Pattern.matches(checkExpressions, mobileNo);
	}
	
	/**
     * 根据 timestamp 生成各类时间状态串
     * 
     * @param timestamp 距1970 00:00:00 GMT的秒数
     * @return 时间状态串(如：刚刚5分钟前)
     */
    public static String getTimeState(String timestamp) {
        if (timestamp == null || "".equals(timestamp)) {
            return "";
        }
        try {
            long _timestamp = Long.parseLong(timestamp); 
            if (System.currentTimeMillis() - _timestamp < 1 * 60 * 1000) {
                return "刚刚";
            } else if (System.currentTimeMillis() - _timestamp < 30 * 60 * 1000) {
                return ((System.currentTimeMillis() - _timestamp) / 1000 / 60) + "分钟前";
            } else {
                Calendar now = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(_timestamp);
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.MONTH) == now.get(Calendar.MONTH) && c.get(Calendar.DATE) == now.get(Calendar.DATE)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("今天 HH:mm");
                    return sdf.format(c.getTime());
                }
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.MONTH) == now.get(Calendar.MONTH) && c.get(Calendar.DATE) == now.get(Calendar.DATE) - 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
                    return sdf.format(c.getTime());
                } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    return sdf.format(c.getTime());
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss");
                    return sdf.format(c.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     *  获取四位随机数 小写 数字+字母
     * @return
     */
    public static String generateWord() {  
        String[] beforeShuffle = new String[] { "2", "3", "4", "5", "6", "7",  
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",  
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",  
                "W", "X", "Y", "Z" };  
        List list = Arrays.asList(beforeShuffle);  
        Collections.shuffle(list);  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < list.size(); i++) {  
            sb.append(list.get(i));  
        }  
        String afterShuffle = sb.toString();  
        String result = afterShuffle.substring(5, 9);  
        return result.toLowerCase();  
    }

	public static void main(String args[]) throws Exception {
		/*String a = "/uploadfile/NV156FHM-N48/NV156FHM-N48.doc";
		String as[] = a.split("/");
		//System.out.println(a.split("/").length);
		for (int i = 0; i < as.length; i++) {
			System.out.println(as[i]);
		}*/
		Integer a = null;
		System.out.println(a);
	}

	/**
	 * 功能：检查输入字符串是否为邮箱地址 检查规则:'@'之前全部为可见字符，'@'与'.'之间要有数字或字线或下划线， '.'之后至少有一个可见字符
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmailAdd(String email) {
		if (email == null) {
			return false;
		}
		String checkExpressions;
		checkExpressions = "^\\S+@\\w+\\.\\S+$";
		return Pattern.matches(checkExpressions, email);
	}


	/**
	 * 获取请求http结果
	 * 
	 * @param url 请求地址
	 * @param body 请求参数体
	 * @return
	 */
	public static String getHttpRequestResult(String url, String body){
		String result = "";
		try {
			OutputStreamWriter out = null;
			BufferedReader in = null;
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();

			// 设置连接参数
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(20000);

			// 提交数据
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(body);
			out.flush();

			// 读取返回数据
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			boolean firstLine = true; // 读第一行不加换行符
			while ((line = in.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
				} else {
					result += System.lineSeparator();
				}
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("result:" + System.lineSeparator() + result);
		return result;
	}
	
	/**
	 * 把分转换为元
	 * 
	 * @param price
	 * @return
	 */
	public static String getPrice(String price) {
		if (price != null && !price.equals("")) {
			float f = Float.parseFloat(price);
			if (f > 0) {
				price = (f / 100) + "";
			}
		} else {
			price = "0";
		}
		return price;
	}
	
	/**
	 * 把元转换为分
	 * 
	 * @return
	 */
	public static String yuanToCent(String price) {
		if (price != null && !price.equals("")) {
			float f = Float.parseFloat(price);
			if (f > 0) {
				price = (f * 100) + "";
				price = price.split("\\.")[0];
			}
		}
		return price;
	}

	/**
	 * 把元转换为分
	 * 
	 * @return
	 */
	public static String yuanToCent2(String price) {
		if (price != null && !price.equals("")) {
			float f = Float.parseFloat(price);
			if (f > 0) {
				DecimalFormat df = new DecimalFormat(".00");
				price = df.format(f).replaceAll("\\.", "");
			}
		}
		return price;
	}
}
