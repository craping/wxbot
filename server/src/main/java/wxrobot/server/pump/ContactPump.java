package wxrobot.server.pump;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.ContactServer;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.utils.RedisUtil;

@Pump("contact")
@Component
public class ContactPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(ContactPump.class);
	
	@Autowired
	private ContactServer contactServer;
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Pipe("syncContacts")
	@BarScreen(
		desc="同步联系人",
		params= {
			@Parameter(value="idis",  desc="联系人"),
			@Parameter(value="crs",  desc="群聊"),
		}
	)
	public Errcode syncContacts(JSONObject params) throws ErrcodeException {
		String key = "user_" + params.getString("token");
		if (!(new RedisUtil().exists(key))) 
			return new Result(CustomErrors.USER_NOT_LOGIN);
		
		Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
		contactServer.syncContacts(userMap.get("uid").toString(), params);
		return new DataResult(Errors.OK);
	}
}