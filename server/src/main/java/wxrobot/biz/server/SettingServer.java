package wxrobot.biz.server;

import java.util.LinkedList;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import wxrobot.dao.entity.Setting;
import wxrobot.dao.entity.field.Permissions;
import wxrobot.dao.entity.field.Switchs;
import wxrobot.dao.enums.SettingModule;

  
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
	* @Title: initSetting  
	* @Description: 根据权限初始化设置
	* @param @param userName
	* @param @param permissions    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void initSetting(String userName, Permissions permissions){
		Setting set = new Setting();
		set.setUserName(userName);
		set.setTuring(new LinkedList<String>());
		set.setKeywords(new LinkedList<String>());
		set.setTimers(new LinkedList<String>());
		set.setForwards(new LinkedList<String>());
		set.setTips(new LinkedList<String>());
		Switchs switchs =new Switchs();
		switchs.setAutoAcceptFriend(false);
		switchs.setGlobalKeyword(false);
		switchs.setGlobalTimer(false);
		set.setSwitchs(switchs);
		set.setPermissions(permissions);
		mongoTemplate.insert(set);
	}
	  
	  
	/**  
	* @Title: addSeq  
	* @Description: 通用seq列表增加
	* @param @param userName
	* @param @param module
	* @param @param seq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long addSeq(String userName, SettingModule module, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.addToSet(module.getModule(), seq);
		
		return mongoTemplate.upsert(query, update, Setting.class).getModifiedCount();
	}
	
	    
	  
	/**  
	* @Title: modSeq  
	* @Description: 通用seq列表修改
	* @param @param userName
	* @param @param module
	* @param @param oldSeq
	* @param @param newSeq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long modSeq(String userName, SettingModule module, String oldSeq, String newSeq){
		Query query = new Query(Criteria.where("userName").is(userName).and(module.getModule()).is(oldSeq));
		
		Update update = Update.update(module.getModule()+".$", newSeq);
		
		return mongoTemplate.updateFirst(query, update, Setting.class).getModifiedCount();
	}
	  
	
	  
	/**  
	* @Title: delSeq  
	* @Description: 通用seq列表删除
	* @param @param userName
	* @param @param module
	* @param @param seq
	* @param @return    参数  
	* @return long    返回类型  
	* @throws  
	*/  
	    
	public long delSeq(String userName, SettingModule module, String seq){
		Query query = new Query(Criteria.where("userName").is(userName));
		
		Update update = new Update();
		update.pull(module.getModule(), seq);
		
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
