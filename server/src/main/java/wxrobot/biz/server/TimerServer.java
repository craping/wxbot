package wxrobot.biz.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import wxrobot.dao.entity.Timer;
import wxrobot.dao.entity.field.ScheduleMsg;

  
/**  
* @ClassName: KeywordServer  
* @Description: 词库关键词Mongodb Server
* @author Crap  
* @date 2019年1月29日  
*    
*/  
    
@Service
public class TimerServer extends BaseServer {
	
	  
	    
	  
	/**  
	* @Title: getTimers  
	* @Description: 根据seq获取计划
	* @param @param userName
	* @param @param seq
	* @param @return    参数  
	* @return List<KeywordMap>    返回类型  
	* @throws  
	*/  
	    
	public Map<String, List<ScheduleMsg>> getTimers(String userName, String seq){
		Query query = seq == null? 
			new BasicQuery(
				Criteria.where("userName").is(userName).getCriteriaObject(),
				new Field().include("timerMap").exclude("_id").getFieldsObject()
			):
			new BasicQuery(
				Criteria.where("userName").is(userName).and("timerMap."+seq).exists(true).getCriteriaObject(),
				new Field().include("timerMap."+seq).exclude("_id").getFieldsObject()
			);
		Timer timer = mongoTemplate.findOne(query, Timer.class);
		if(timer == null)
			return null;
		return timer.getTimerMap();
	}
	
	  
	  
	/**  
	* @Title: addTimer  
	* @Description: 增加计划
	* @param @param userName
	* @param @param seq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long addTimer(String userName, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.set("timerMap."+seq, new ArrayList<ScheduleMsg>());
		
		return mongoTemplate.updateFirst(query, update, Timer.class).getModifiedCount();
	}
	
	
	/**  
	* @Title: modTimer  
	* @Description: 修改计划seq
	* @param @param userName
	* @param @param oldSeq
	* @param @param newSeq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long modTimer(String userName, String oldSeq, String newSeq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = Update.update("timerMap."+oldSeq, newSeq);
		
		return mongoTemplate.updateFirst(query, update, Timer.class).getModifiedCount();
	}
	  
	/**  
	* @Title: delTimer  
	* @Description: 删除计划
	* @param @param userName
	* @param @param seq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long delTimer(String userName, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.unset("timerMap."+seq);
		
		return mongoTemplate.updateFirst(query, update, Timer.class).getModifiedCount();
	}
	
	    
	  
	/**  
	* @Title: addMsg  
	* @Description: 添加计划消息
	* @param @param userName
	* @param @param seq
	* @param @param msg
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long addMsg(String userName, String seq, ScheduleMsg msg){
		Query query = new Query(Criteria.where("userName").is(userName).and("timerMap."+seq).exists(true));
		
		Update update = new Update();
		update.push("timerMap."+seq, msg);
		
		return mongoTemplate.upsert(query, update, Timer.class).getModifiedCount();
	}
	    
	  
	/**  
	* @Title: delMsg  
	* @Description: 删除计划消息 
	* @param @param userName
	* @param @param seq
	* @param @param uuid
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long delMsg(String userName, String seq, String uuid){
		Query query = new Query(Criteria.where("userName").is(userName).and("timerMap."+seq).exists(true));
		
		Update update = new Update();
		ScheduleMsg msg = new ScheduleMsg();
		msg.setUuid(uuid);
		update.pull("timerMap."+seq, msg);
		
		return mongoTemplate.updateFirst(query, update, Timer.class).getModifiedCount();
	}
}
