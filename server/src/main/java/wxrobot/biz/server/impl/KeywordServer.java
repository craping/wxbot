package wxrobot.biz.server.impl;

import java.util.List;
import java.util.Map;

import org.crap.jrain.core.util.StringUtil;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import wxrobot.dao.entity.Keyword;
import wxrobot.dao.entity.field.KeywordMap;

  
/**  
* @ClassName: KeywordServer  
* @Description: 词库关键词Mongodb Server
* @author Crap  
* @date 2019年1月29日  
*    
*/  
    
@Service
public class KeywordServer extends BaseServer {
	
	/**  
	* @Title: getKeywords  
	* @Description: 获取所有词库
	* @param @param userName
	* @param @return    参数  
	* @return List<KeywordMap>    返回类型  
	* @throws  
	*/  
	    
	public List<KeywordMap> getKeywords(String userName){
		Query query = new BasicQuery(
				Criteria.where("userName").is(userName).getCriteriaObject(),
				new Field().include("keyMaps").exclude("_id").getFieldsObject()
			);
		Keyword keyword = mongoTemplate.findOne(query, Keyword.class);
		if(keyword == null)
			return null;
		return keyword.getKeyMaps();
	}
	
	  
	/**  
	* @Title: addKeyword  
	* @Description: 添加词库
	* @param @param userName
	* @param @param name	词库名
	* @param @return    参数  
	* @return long    影响条数 
	* @throws  
	*/  
	    
	public long addKeyword(String userName, String name){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		KeywordMap kwMap = new KeywordMap();
		kwMap.setId(StringUtil.uuid());
		kwMap.setName(name);
		update.push("keyMaps", kwMap);
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
	
	
	  
	/**  
	* @Title: modKeyword  
	* @Description: 修改词库
	* @param @param userName
	* @param @param id
	* @param @param name
	* @param @return    参数  
	* @return long    影响条数 
	* @throws  
	*/  
	    
	public long modKeyword(String userName, String oldId, String newId){
		Query query = new Query(Criteria.where("userName").is(userName).and("keyMaps.id").is(oldId));
		
		Update update = Update.update("keyMaps.id", newId);
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
	  
	/**  
	* @Title: delKeyword  
	* @Description: 删除词库
	* @param @param userName
	* @param @param id
	* @param @return    参数  
	* @return long    影响条数 
	* @throws  
	*/  
	    
	public long delKeyword(String userName, String id){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.pull("keyMaps", "{'id':'"+id+"'}");
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
	
	  
	/**  
	* @Title: addOrMod  
	* @Description: 新增或修改关键词
	* @param @param userName
	* @param @param keyId
	* @param @param keyMap
	* @param @return    参数  
	* @return long    影响条数 
	* @throws  
	*/  
	    
	public long addOrMod(String userName, String id, Map<String, String> keyMap){
		Query query = new Query(Criteria.where("userName").is(userName).and("keyMaps.id").is(id));
		
		Update update = new Update();
		keyMap.forEach((k, v) -> {
			update.set("keyMaps.$.keyMap."+k, v);
		});
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
	
	  
	/**  
	* @Title: del  
	* @Description: 删除关键词
	* @param @param userName
	* @param @param keyId
	* @param @param keys
	* @param @return    参数  
	* @return long    影响条数 
	* @throws  
	*/  
	    
	public long del(String userName, String id, List<String> keys){
		Query query = new Query(Criteria.where("userName").is(userName).and("keyMaps.id").is(id));
		
		Update update = new Update();
		keys.forEach(k -> {
			update.unset("keyMaps.$.keyMap."+k);
		});
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
}
