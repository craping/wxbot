package wxrobot.biz.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import wxrobot.dao.entity.Keyword;

  
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
	* @Description: 根据seq获取关键词
	* @param @param userName
	* @param @param seq
	* @param @return    参数  
	* @return List<KeywordMap>    返回类型  
	* @throws  
	*/  
	    
	public Map<String, Map<String, String>> getKeywords(String userName, String seq){
		Query query = seq == null? 
		new BasicQuery(
			Criteria.where("userName").is(userName).getCriteriaObject(),
			new Field().include("keyMap").exclude("_id").getFieldsObject()
		):
		new BasicQuery(
			Criteria.where("userName").is(userName).and("keyMap."+seq).exists(true).getCriteriaObject(),
			new Field().include("keyMap."+seq).exclude("_id").getFieldsObject()
		);
		Keyword keyword = mongoTemplate.findOne(query, Keyword.class);
		if(keyword == null)
			return null;
		return keyword.getKeyMap();
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
	    
	public long addKeyword(String userName, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.addToSet("keyMap."+seq, new HashMap<>());
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
	
	
	/**  
	* @Title: modKeyword  
	* @Description: 修改词库seq
	* @param @param userName
	* @param @param oldSeq
	* @param @param newSeq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long modKeyword(String userName, String oldSeq, String newSeq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = Update.update("keyMap."+oldSeq, newSeq);
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
	  
	/**  
	* @Title: delKeyword  
	* @Description: 删除词库
	* @param @param userName
	* @param @param seq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long delKeyword(String userName, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.unset("keyMap."+seq);
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
	
	/**  
	* @Title: addOrMod  
	* @Description: 新增或修改关键词
	* @param @param userName
	* @param @param seq
	* @param @param keyMap
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long addOrMod(String userName, String seq, Map<String, String> keyMap){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		keyMap.forEach((k, v) -> {
			update.set("keyMap."+seq+"."+k, v);
		});
		
		return mongoTemplate.upsert(query, update, Keyword.class).getModifiedCount();
	}
	
	  
	/**  
	* @Title: del  
	* @Description: 删除关键词
	* @param @param userName
	* @param @param seq
	* @param @param keys
	* @param @return    参数  
	* @return long    影响条数 
	* @throws  
	*/  
	    
	public long del(String userName, String seq, List<String> keys){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		keys.forEach(k -> {
			update.unset("keyMap."+seq+"."+k);
		});
		
		return mongoTemplate.updateFirst(query, update, Keyword.class).getModifiedCount();
	}
}
