package wxrobot.server.pump;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
import org.crap.jrain.core.bean.result.criteria.Data;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.ContactServer;
import wxrobot.biz.server.KeywordServer;
import wxrobot.biz.server.SettingServer;
import wxrobot.biz.server.TimerServer;
import wxrobot.dao.entity.User;
import wxrobot.dao.entity.field.ContactInfo;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.dao.enums.SettingModule;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.TokenParam;
import wxrobot.server.utils.RedisUtil;

@Pump("contact")
@Component
public class ContactPump extends DataPump<FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(ContactPump.class);
	
	@Autowired
	private ContactServer contactServer;
	
	@Autowired
	private KeywordServer keywordServer;
	
	@Autowired
	private TimerServer timerServer;
	
	@Autowired
	private SettingServer settingServer;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Pipe("test")
	@BarScreen(
		desc="测试群聊",
		params= {
			@Parameter(type=TokenParam.class)
		}
	)
	public Errcode test(JSONObject params) throws ErrcodeException {
		String key = "user_" + params.getString("token");
		if (!(RedisUtil.exists(key))) 
			return new Result(CustomErrors.USER_NOT_LOGIN);
		
		Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
		String uid = userMap.get("uid").toString();
		List<ContactInfo> chatRooms = contactServer.getUserContact(uid).getChatRooms();
		
		ContactInfo contact = chatRooms.get(0);
		for (int i = 0; i <= 500; i++) {
			chatRooms.add(contact);
		}
		
		contactServer.addContact(uid, chatRooms);
		
		return new DataResult(Errors.OK, new Data(chatRooms));
	}
	
	@Pipe("getContacts")
	@BarScreen(
		desc="获取群聊",
		params= {
			@Parameter(type=TokenParam.class)
		}
	)
	public Errcode getContacts(JSONObject params) throws ErrcodeException {
		User user = contactServer.getUser(params);
		List<ContactInfo> chatRooms = contactServer.getUserContact(user.getId()).getChatRooms();
		return new DataResult(Errors.OK, new Data(chatRooms));
	}
	
	@Pipe("syncContacts")
	@BarScreen(
		desc="同步群聊",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="crs",  desc="群聊"),
		}
	)
	public Errcode syncContacts(JSONObject params) throws ErrcodeException {
		User user = contactServer.getUser(params);
		contactServer.syncContacts(user.getId(), params);
		return new DataResult(Errors.OK);
	}
	
	
	@Pipe("syncSeq")
	@BarScreen(
		desc="同步seq",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seqMap",  desc="Map集合{oldSeq:newSeq}"),
		}
	)
	public Errcode syncSeq(JSONObject params) throws ErrcodeException {
		UserInfo userInfo = contactServer.getUserInfo(params);
		Map<String, String> seqMap = null;
		try {
			seqMap = ContactServer.JSON_MAPPER.readValue(params.getString("seqMap"), new TypeReference<Map<String, String>>(){});
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		seqMap.forEach((k, v) -> {
			//更新关键词seq
			keywordServer.modKeyword(userInfo.getUserName(), k, v);
			//更新定时消息seq
			timerServer.modTimer(userInfo.getUserName(), k, v);
			//更新所有seq设置
			for (SettingModule module : SettingModule.values()) {
				settingServer.modSeq(userInfo.getUserName(), module, k, v);
			}
		});
		return new DataResult(Errors.OK);
	}
}