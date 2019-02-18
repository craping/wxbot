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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.impl.BaseServer;
import wxrobot.biz.server.impl.KeywordServer;
import wxrobot.dao.entity.field.KeywordMap;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.TokenParam;

@Pump("keyword")
@Component
public class KeywordPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(KeywordPump.class);
	
	@Autowired
	private KeywordServer keywordServer;
	
	@Pipe("getKeyword")
	@BarScreen(
		desc="获取全部词库",
		params= {
			@Parameter(type=TokenParam.class)
		}
	)
	public Errcode getKeyword (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		List<KeywordMap> keywords = keywordServer.getKeywords(userInfo.getUserName());
		
		return new DataResult(Errors.OK, new Data(keywords));
	}
	
	@Pipe("addKeyword")
	@BarScreen(
		desc="添加词库",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="name", desc="词库名")
		}
	)
	public Errcode addKeyword (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		long mod = keywordServer.addKeyword(userInfo.getUserName(), params.getString("name"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("modKeyword")
	@BarScreen(
		desc="修改词库",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="id", desc="词库ID"),
			@Parameter(value="name", desc="词库名")
		}
	)
	public Errcode modKeyword (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		long mod = keywordServer.modKeyword(userInfo.getUserName(), params.getString("id"), params.getString("name"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("delKeyword")
	@BarScreen(
		desc="删除词库",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="id", desc="词库ID")
		}
	)
	public Errcode delKeyword (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		long mod = keywordServer.delKeyword(userInfo.getUserName(), params.getString("id"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	
	@Pipe("setGloba")
	@BarScreen(
		desc="设置全局群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="keyMap",  desc="关键词Map 格式：{key:value, ...}")
		}
	)
	public Errcode setGloba (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		Map<String, String> keyMap = null;
		try {
			keyMap = BaseServer.JSON_MAPPER.readValue(params.getString("keyMap"), new TypeReference<Map<String, String>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		long mod = keywordServer.addOrMod(userInfo.getUserName(), "globa", keyMap);
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("delGloba")
	@BarScreen(
		desc="删除全局群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="keyList",  desc="关键词ID列表 格式：[key,key,...]")
		}
	)
	public Errcode delGloba (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		List<String> keyList = null;
		try {
			keyList = BaseServer.JSON_MAPPER.readValue(params.getString("keyList"), new TypeReference<List<String>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		long mod = keywordServer.del(userInfo.getUserName(), "globa", keyList);
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("set")
	@BarScreen(
		desc="设置分群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="id",  desc="词库ID"),
			@Parameter(value="keyMap",  desc="关键词Map 格式：{key:value, ...}")
		}
	)
	public Errcode add (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		Map<String, String> keyMap = null;
		try {
			keyMap = BaseServer.JSON_MAPPER.readValue(params.getString("keyMap"), new TypeReference<Map<String, String>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		long mod = keywordServer.addOrMod(userInfo.getUserName(), params.getString("id"), keyMap);
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("del")
	@BarScreen(
		desc="删除分群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="id",  desc="词库ID"),
			@Parameter(value="keyList",  desc="关键词ID列表 格式：[key,key,...]")
		}
	)
	public Errcode del (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		List<String> keyList = null;
		try {
			keyList = BaseServer.JSON_MAPPER.readValue(params.getString("keyList"), new TypeReference<List<String>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		long mod = keywordServer.del(userInfo.getUserName(), params.getString("id"), keyList);
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
}