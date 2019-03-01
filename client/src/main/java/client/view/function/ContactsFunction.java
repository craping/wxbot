package client.view.function;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.Launch;

@Component
public class ContactsFunction extends SettingFunction {
	
	public ContactsFunction() {
		super();
	}
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
	 * 获取域名路径
	 * @return qq.wx.com、qq2.wx.com
	 */
	public String getHostUrl() {
		return cacheService.getHostUrl();
	}
	
	/**
	 * 获取当前机器人用户头像地址
	 * @return
	 */
	public String getOwnerHeadImgUrl() {
		return getHostUrl() + cacheService.getOwner().getHeadImgUrl();
	}
	
	/**
	 * 获取群成员头像
	 * @param chatRoomName 群名称
	 * @param memberUserName 群成员名称
	 * @return
	 */
	public String getChatRoomMemberHeadImgUrl(String chatRoomName, String memberUserName) {
		Wxbot wxbot = Launch.context.getBean(Wxbot.class);
		Set<Contact> members = wxbot.getChatRoom(cacheService.getChatRooms(), chatRoomName).getMemberList();
		Contact member = members.stream().filter(x -> memberUserName.equals(x.getNickName())).findFirst().orElse(null);
		return getHostUrl() + member.getHeadImgUrl();
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
			
			System.out.println(jsonMapper.writeValueAsString(cacheService.getChatRooms()));
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
