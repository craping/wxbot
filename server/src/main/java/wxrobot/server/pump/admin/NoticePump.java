package wxrobot.server.pump.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.criteria.Data;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.NoticeServer;
import wxrobot.dao.entity.Notice;
import wxrobot.server.param.AdminTokenParam;
import wxrobot.server.utils.Tools;

@Pump("admin")
@Component
public class NoticePump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(NoticePump.class);
	
	@Autowired
	private NoticeServer noticeServer;
	
	@Pipe("noticeList")
	@BarScreen(
		desc="公告列表",
		params= {
			@Parameter(type=AdminTokenParam.class)
		}
	)
	public Errcode noticeList(JSONObject params) {
		return noticeServer.getNoticeList(params);
	}
	
	@Pipe("addNotice")
	@BarScreen(
		desc="新增公告",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="title",  desc="公告标题"),
			@Parameter(value="content",  desc="公告内容"),
			@Parameter(value="state",  desc="发布状态", required=false),
			@Parameter(value="sendTime",  desc="发布时间", required=false)
		}
	)
	public Errcode addNotice(JSONObject params) throws ErrcodeException {
		Notice notice = new Notice();
		notice.setTitle(params.getString("title"));
		notice.setContent(params.getString("content"));
		notice.setSendTime(Tools.dateUTCToStamp(params.getString("sendTime")));
		notice.setState(params.getBoolean("state"));
		notice = noticeServer.insert(notice);
		return new DataResult(Errors.OK);
	}
	
	@Pipe("updNotice")
	@BarScreen(
		desc="修改公告",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id",  desc="公告id"),
			@Parameter(value="title",  desc="公告标题"),
			@Parameter(value="content",  desc="公告内容"),
			@Parameter(value="state",  desc="发布状态"),
			@Parameter(value="sendTime",  desc="发布时间")
		}
	)
	public Errcode updNotice(JSONObject params) {
		noticeServer.update(params);
		return new DataResult(Errors.OK);
	}
	
	@Pipe("updNoticeState")
	@BarScreen(
		desc="修改状态",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id",  desc="公告id"),
			@Parameter(value="state",  desc="状态")
		}
	)
	public Errcode updNoticeState(JSONObject params) {
		noticeServer.updateState(params);
		return new DataResult(Errors.OK);
	}
	
	@Pipe("getNotice")
	@BarScreen(
		desc="获取公告",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id", desc="公告id"),
		}
	)
	public Errcode getNotice(JSONObject params) {
		Notice notice = noticeServer.find(params.getString("id"));
		return new DataResult(Errors.OK, new Data(notice));
	}
	
	@Pipe("delNotice")
	@BarScreen(
		desc="删除公告",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id", desc="公告id"),
		}
	)
	public Errcode delNotice(JSONObject params) {
		noticeServer.del(params.getString("id"));
		return new DataResult(Errors.OK);
	}
}