package client.view.function;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSFunction;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.view.server.BaseServer;

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
	    
	public void getIndividuals(JSFunction function) {
		new Thread(() -> {
			try {
				function.invoke(function, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(cacheService.getIndividuals())));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * 获取当前登录微信用户信息
	 * @return
	 */
	public JSONString getOwner() {
		try {
			return new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(cacheService.getOwner()));
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
		return cacheService.getHostUrl() + cacheService.getOwner().getHeadImgUrl();
	}
	
	/**
	 * 获取群成员头像
	 * @param chatRoomName 群名称
	 * @param memberUserName 群成员名称
	 * @return
	 */
	public String getChatRoomMemberHeadImgUrl(String chatRoomName, String memberUserName) {
		try {
			ConcurrentLinkedQueue<Contact> members = wechatService.getChatRoomInfo(chatRoomName).getMemberList();
			Contact member = members.stream().filter(x -> memberUserName.equals(x.getNickName())).findFirst().orElse(null);
			return getHostUrl() + member.getHeadImgUrl();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	  
	/**  
	* @Title: getChatRooms  
	* @Description: 获取群列表
	* @param @return    参数  
	* @return JSONString    返回类型  
	* @throws  
	*/  
	public void getChatRooms(JSFunction function) {
		new Thread(() -> {
			try {
				function.invoke(function, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(cacheService.getChatRooms())));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * 获取群成员列表
	 * @param chatRoomName
	 * @return
	 */
	public void getChatRoomMembers(String chatRoomName, JSFunction function) {
		new Thread(() -> {
			try {
				ConcurrentLinkedQueue<Contact> members = wechatService.getChatRoomInfo(chatRoomName).getMemberList();
				function.invoke(function, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(members)));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
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
			return new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(cacheService.getMediaPlatforms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	public void read(String userName) {
		new Thread(() -> {
			try {
				wechatService.notifyNecessary(userName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
