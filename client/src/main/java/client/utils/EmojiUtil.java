package client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vdurmont.emoji.EmojiParser;

/**
 * emoji 表情工具类
 * 
 * @author wr
 *
 */
public class EmojiUtil {
	/**
	 * 正则表达式处理工具
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 上午12:27:10
	 * @return
	 */
	public static Matcher getMatcher(String regEx, String text) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(text);
		return matcher;
	}

	public static String getEmoji(String emojiStr) {
		Matcher matcher = getMatcher("<span class=\"emoji emoji(.{1,10})\"></span>", emojiStr);
		StringBuilder sb = new StringBuilder();
		int lastStart = 0;
		while (matcher.find()) {
			String str = matcher.group(1);
			if (str.length() == 6) {

			} else if (str.length() == 10) {

			} else {
				str = "&#x" + str + ";";
				String tmp = emojiStr.substring(lastStart, matcher.start());
				sb.append(tmp + str);
				lastStart = matcher.end();
			}
		}
		if (lastStart < emojiStr.length()) {
			sb.append(emojiStr.substring(lastStart));
		}
		return EmojiParser.parseToUnicode(sb.toString());
	}

	public static void main(String args[]) {
		String s = "RIP<span class=\"emoji emoji1f60a\"></span><span class=\"emoji emoji1f639\"></span>";
		System.out.println(getEmoji(s));
	}
}