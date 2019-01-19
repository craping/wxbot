package wxrobot.dao.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class MongoUtil {
	@Autowired
	private MongoObjectParams objectParams;

	public Map<String, Object> converObjectToParams(Object object) throws Exception {
		Map<String, Object> map = new HashMap<>(16);
		Update update = new Update();
		Map<String, Object> params = objectParams.createParams(object);
		String id = (String) params.get("id");
		Set<Map.Entry<String, Object>> sets = params.entrySet();
		Iterator<Map.Entry<String, Object>> iteratos = sets.iterator();
		while (iteratos.hasNext()) {
			Map.Entry<String, Object> entry = iteratos.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			if (!key.equals("id")) {
				update.set(key, value);
			}
		}
		map.put("id", id);
		map.put("update", update);
		return map;
	}
}