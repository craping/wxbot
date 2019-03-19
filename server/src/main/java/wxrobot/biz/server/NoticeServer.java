package wxrobot.biz.server;

import java.util.regex.Pattern;

import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.bean.result.criteria.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import wxrobot.dao.entity.Notice;
import wxrobot.server.utils.Tools;

@Service
public class NoticeServer extends BaseServer {

	/**
	 * 新增公告
	 * 
	 * @param notic
	 * @return
	 * @throws ErrcodeException
	 */
	public Notice insert(Notice notic) throws ErrcodeException {
		return mongoTemplate.insert(notic);
	}

	/**
	 * 获取公告
	 * 
	 * @param id
	 * @return
	 */
	public Notice find(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), Notice.class);
	}

	/**
	 * 删除公告
	 * 
	 * @param id
	 * @return
	 */
	public long del(String id) {
		return mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), Notice.class).getDeletedCount();
	}

	/**
	 * 修改公告状态
	 * 
	 * @param params
	 * @return
	 */
	public long updateState(JSONObject params) {
		Query query = new Query(Criteria.where("_id").is(params.getString("id")));
		Update update = Update.update("state", params.getBoolean("state"));
		return mongoTemplate.updateFirst(query, update, Notice.class).getModifiedCount();
	}

	/**
	 * 修改发布时间
	 * 
	 * @param params
	 * @return
	 */
	public long updateSendTime(JSONObject params) {
		Query query = new Query(Criteria.where("_id").is(params.getString("id")));
		Update update = Update.update("sendTime", Tools.dateUTCToStamp(params.getString("sendTime")));
		return mongoTemplate.updateFirst(query, update, Notice.class).getModifiedCount();
	}

	/**
	 * 获取公告列表
	 * 
	 * @param params
	 * @return
	 */
	public DataResult getNoticeList(JSONObject params) {
		Page page = new Page(params.optInt("curPage", 1), params.optInt("pageSize", 10));
		Query query = new Query();
		if (!Tools.isStrEmpty(params.optString("title"))) {
			Pattern pattern = Pattern.compile("^.*" + params.optString("title") + ".*$", Pattern.CASE_INSENSITIVE);
			query.addCriteria(Criteria.where("title").regex(pattern));
		}
		if (!Tools.isStrEmpty(params.optString("state"))) {
			query.addCriteria(Criteria.where("state").is(params.optBoolean("state")));
		}
		return findPage(page, query, Notice.class);
	}

	/**
	 * 更新公告信息
	 * 
	 * @param user
	 * @return
	 */
	public long update(JSONObject params) {
		Query query = new Query(Criteria.where("_id").is(params.getString("id")));
		Update update = new Update();
		update.set("title", params.getString("title"));
		update.set("content", params.getString("content"));
		update.set("state", params.getBoolean("state"));
		update.set("sendTime", Tools.dateUTCToStamp(params.getString("sendTime")));
		return mongoTemplate.upsert(query, update, Notice.class).getModifiedCount();
	}
}
