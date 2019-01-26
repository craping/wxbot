package client.view.function;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;

@Component
public class ContactsFunction extends SeetingFunction {
	  
	/**  
	* @Title: getIndividuals  
	* @Description: 获取联系人列表
	* @param @return    参数  
	* @return JSONString    返回类型  
	* @throws  
	*/  
	    
	public JSONString getIndividuals() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(cacheService.getIndividuals()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	  
	/**  
	* @Title: getChatRooms  
	* @Description: 获取群列表
	* @param @return    参数  
	* @return JSONString    返回类型  
	* @throws  
	*/  
	    
	public JSONString getChatRooms() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(cacheService.getChatRooms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	  
	/**  
	* @Title: getMediaPlatforms  
	* @Description: 获取公众号列表
	* @param @return    参数  
	* @return JSONString    返回类型  
	* @throws  
	*/  
	    
	public JSONString getMediaPlatforms() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(cacheService.getMediaPlatforms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
}
