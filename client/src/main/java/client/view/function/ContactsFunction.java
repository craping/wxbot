package client.view.function;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.utils.EmojiUtil;

@Component
public class ContactsFunction extends SettingFunction {
	
	public ContactsFunction() {
		super();
	}
	
	/**
	 * 解析 emoji表情
	 * @param str
	 * @return
	 */
	public String getEmoji(String str) {
		return EmojiUtil.getEmoji(str);
	}
	
	/**
	 * 过滤 emoji表情
	 * @param str
	 * @return
	 */
	public String rmEmoji(String str) {
		return EmojiUtil.rmEmoji(str);
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
			System.out.println(jsonMapper.writeValueAsString(cacheService.getIndividuals()));
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
		try {
			Set<Contact> members = wechatService.getChatRoomInfo(chatRoomName).getMemberList();
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
	public JSONString getChatRooms() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(cacheService.getChatRooms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	/**
	 * 获取群成员列表
	 * @param chatRoomName
	 * @return
	 */
	public JSONString getChatRoomMembers(String chatRoomName) {
		try {
			Set<Contact> members = wechatService.getChatRoomInfo(chatRoomName).getMemberList();
			return new JSONString(jsonMapper.writeValueAsString(members));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
