package wxrobot.biz.server;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import wxrobot.dao.entity.Setting;
import wxrobot.dao.entity.field.Permissions;
import wxrobot.dao.entity.field.Switchs;
import wxrobot.dao.entity.field.Tips;

  
/**  
* @ClassName: SettingServer  
* @Description: 设置服务Mongodb Server
* @author Crap  
* @date 2019年1月29日  
*    
*/  
    
@Service
public class SettingServer extends BaseServer {
	
	  
	/**  
	* @Title: getSetting  
	* @Description: 获取设置列表
	* @param @param userName
	* @param @return    参数  
	* @return Setting    返回类型  
	* @throws  
	*/  
	    
	public Setting getSetting(String userName){
		return mongoTemplate.findOne(Query.query(Criteria.where("userName").is(userName)), Setting.class);
	}
	
	  
	/**  
	* @Title: addForward  
	* @Description: 添加转发群
	* @param @param userName
	* @param @param seq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	public long addForward(String userName, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.addToSet("forwards", seq);
		
		return mongoTemplate.upsert(query, update, Setting.class).getModifiedCount();
	}
	
	    
	/**  
	* @Title: modForward  
	* @Description: 更新转发群seq
	* @param @param userName
	* @param @param oldSeq
	* @param @param newSeq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	public long modForward(String userName, String oldSeq, String newSeq){
		Query query = new Query(Criteria.where("userName").is(userName).and("forwards").is(oldSeq));
		
		Update update = Update.update("forwards.$", newSeq);
		
		return mongoTemplate.updateFirst(query, update, Setting.class).getModifiedCount();
	}
	  
	
	/**  
	* @Title: delForward  
	* @Description: 删除转发群
	* @param @param userName
	* @param @param seq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	public long delForward(String userName, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.pull("forwards", seq);
		
		return mongoTemplate.updateFirst(query, update, Setting.class).getModifiedCount();
	}
	
	/**  
	* @Title: setSwitchs  
	* @Description: 设置开关功能通用方法
	* @param @param userName
	* @param @param switchs
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	public long setSwitchs(String userName, Switchs switchs){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = Update.update("switchs", switchs);
		
		return mongoTemplate.upsert(query, update, Setting.class).getModifiedCount();
	}
	
	/**  
	* @Title: setTips  
	* @Description: 设置提示语通用方法
	* @param @param userName
	* @param @param tips
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	public long setTips(String userName, Tips tips){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = Update.update("tips", tips);
		
		return mongoTemplate.upsert(query, update, Setting.class).getModifiedCount();
	}
	
	  
	/**  
	* @Title: setPermissions  
	* @Description: 用户权限通用方法
	* @param @param userName
	* @param @param permissions
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	public long setPermissions(String userName, Permissions permissions){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = Update.update("permissions", permissions);
		
		return mongoTemplate.upsert(query, update, Setting.class).getModifiedCount();
	}
}
