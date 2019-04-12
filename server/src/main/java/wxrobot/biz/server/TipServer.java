package wxrobot.biz.server;

import java.util.Map;

import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import wxrobot.dao.entity.Tip;
import wxrobot.dao.entity.field.Msg;

  
    
  
/**  
* @ClassName: TipServer  
* @Description: 群体示语Mongodb操作Server
* @author Crap  
* @date 2019年4月9日  
*    
*/  
    
@Service
public class TipServer extends BaseServer {
	
	  
	public Map<String, Map<String, Msg>> getTips(String userName, String seq){
		Query query = seq == null? 
			new BasicQuery(
				Criteria.where("userName").is(userName).getCriteriaObject(),
				new Field().include("tipMap").exclude("_id").getFieldsObject()
			):
			new BasicQuery(
				Criteria.where("userName").is(userName).and("tipMap."+seq).exists(true).getCriteriaObject(),
				new Field().include("tipMap."+seq).exclude("_id").getFieldsObject()
			);
		Tip tip = mongoTemplate.findOne(query, Tip.class);
		if(tip == null)
			return null;
		return tip.getTipMap();
	}
	
	public long modTip(String userName, String oldSeq, String newSeq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update().rename("tipMap."+oldSeq, "tipMap."+newSeq);
		
		return mongoTemplate.updateFirst(query, update, Tip.class).getModifiedCount();
	}
	  
	public long delTip(String userName, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.unset("tipMap."+seq);
		
		return mongoTemplate.updateFirst(query, update, Tip.class).getModifiedCount();
	}
	
	    
	public long set(String userName, String seq, String type, Msg msg){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = Update.update("tipMap."+seq+"."+type, msg);
		return mongoTemplate.upsert(query, update, Tip.class).getModifiedCount();
	}
	
	public long del(String userName, String seq, String type){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.unset("tipMap."+seq+"."+type);
		
		return mongoTemplate.updateFirst(query, update, Tip.class).getModifiedCount();
	}
}
