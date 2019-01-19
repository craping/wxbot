package wxrobot.dao.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MongoObjectParams {
	private String javaType = "java";

	/**
	 * 获取查询的参数
	 *
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> createParams(Object object) throws Exception {
		Map<String, Object> params = new HashMap<>(16);
		setIntoParams(params, object, null);
		return params;
	}

	private void setIntoParams(Map<String, Object> params, Object object, String fatherName) throws IllegalAccessException, Exception {
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field file : fields) {
			boolean accessFlag = file.isAccessible();
			file.setAccessible(true);
			String name = file.getName();
			Object value = file.get(object);
			if (file.getType().getName().equals("java.lang.Class")) {
				break;
			} else if (file.getType().getName().contains(javaType)) {
				if (fatherName != null && !fatherName.equals(" ")) {
					name = fatherName + "." + name;
				}
				if (value != null) {
					params.put(name, value);
				}
			} else {
				if (value != null) {
					setIntoParams(params, file.get(object), name);
				}
			}
			file.setAccessible(accessFlag);
		}
	}
}