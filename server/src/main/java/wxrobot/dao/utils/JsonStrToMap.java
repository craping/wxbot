package wxrobot.dao.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class JsonStrToMap {

	/**
	 * json 字符串转化为map格式
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Map<String, Object> jsonStrToMap(String jsonString) {
		Object parseObj = JSONObject.fromObject(jsonString); // 反序列化 把json 转化为对象
		Map<String, Object> map = (HashMap<String, Object>) parseObj; // 把对象转化为map
		return map;
	}
}
