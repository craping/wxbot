package client.utils;

public class RegexUtil {

	public static final String PHONE_NUM_REGEX = "^0?(13[0-9]|15[0-9]|16[0-9]|17[0-9]|18[0-9]|19[0-9]|14[57])[0-9]{8}$";

	public static boolean validate(String str, String regex) {
		return str.matches(regex);
	}
}
