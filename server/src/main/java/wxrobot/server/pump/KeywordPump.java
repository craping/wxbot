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
import wxrobot.biz.server.BaseServer;
import wxrobot.biz.server.KeywordServer;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.TokenParam;

@Pump("keyword")
@Component
public class KeywordPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(KeywordPump.class);
	
	@Autowired
	private KeywordServer keywordServer;
	
	@Pipe("getKeywords")
	@BarScreen(
		desc="获取词库",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", required=false, desc="seq")
		}
	)
	public Errcode getKeywords (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		Map<String, Map<String, String>> keywords = keywordServer.getKeywords(userInfo.getUserName(), params.optString("seq", null));
		
		return new DataResult(Errors.OK, new Data(keywords));
	}
	
	@Pipe("addKeyword")
	@BarScreen(
		desc="添加词库",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq")
		}
	)
	public Errcode addKeyword (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		long mod = keywordServer.addKeyword(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("modKeyword")
	@BarScreen(
		desc="修改词库",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="oldSeq", desc="oldSeq"),
			@Parameter(value="newSeq", desc="newSeq")
		}
	)
	public Errcode modKeyword (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		long mod = keywordServer.modKeyword(userInfo.getUserName(), params.getString("oldSeq"), params.getString("newSeq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("delKeyword")
	@BarScreen(
		desc="删除词库",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq")
		}
	)
	public Errcode delKeyword (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		long mod = keywordServer.delKeyword(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	
	@Pipe("setGlobal")
	@BarScreen(
		desc="设置全局群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="keyMap",  desc="关键词Map 格式：{key:value, ...}")
		}
	)
	public Errcode setGlobal (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		Map<String, String> keyMap = null;
		try {
			keyMap = BaseServer.JSON_MAPPER.readValue(params.getString("keyMap"), new TypeReference<Map<String, String>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		keywordServer.addOrMod(userInfo.getUserName(), "global", keyMap);
		
		return new DataResult(Errors.OK);
	}
	
	@Pipe("delGlobal")
	@BarScreen(
		desc="删除全局群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="keyList",  desc="关键词ID列表 格式：[key,key,...]")
		}
	)
	public Errcode delGlobal (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = keywordServer.getUserInfo(params);
		
		List<String> keyList = null;
		try {
			keyList = BaseServer.JSON_MAPPER.readValue(params.getString("keyList"), new TypeReference<List<String>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		long mod = keywordServer.del(userInfo.getUserName(), "global", keyList);
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("set")
	@BarScreen(
		desc="设置分群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq",  desc="seq"),
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
		
		keywordServer.addOrMod(userInfo.getUserName(), params.getString("seq"), keyMap);
		
		return new DataResult(Errors.OK);
	}
	
	@Pipe("del")
	@BarScreen(
		desc="删除分群关键词",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq",  desc="seq"),
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
		
		long mod = keywordServer.del(userInfo.getUserName(), params.getString("seq"), keyList);
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
}