package wxrobot.dao.support;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.crap.jrain.core.bean.result.criteria.Data;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.bean.result.criteria.Page;
import org.crap.jrain.core.error.support.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import wxrobot.dao.BaseDao;
import wxrobot.dao.utils.MongoUtil;

public class BaseDaoImpl<T> implements BaseDao<T> {

	private Class<T> clazz;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoUtil mongoUtil;

	@SuppressWarnings("unchecked")
	public BaseDaoImpl() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}

	@Override
	public T insert(T entity) {
		return mongoTemplate.insert(entity);
	}

	@Override
	public void batchInsert(List<T> l) {
		mongoTemplate.insert(l, clazz);
	}

	@Override
	public T findOne(String id) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(id));
		return getMongoTemplate().findOne(query, clazz);
	}

	@Override
	public T findById(String id, String collectionName) {
		return mongoTemplate.findById(id, clazz, collectionName);
	}

	@Override
	public T findOne(Query query) {
		return getMongoTemplate().findOne(query, clazz);
	}

	@Override
	public List<T> findAll() {
		return mongoTemplate.findAll(clazz);
	}

	@Override
	public List<T> findAll(String collectionName) {
		return mongoTemplate.findAll(clazz, collectionName);
	}

	@Override
	public List<T> find(Query query) {
		return getMongoTemplate().find(query, clazz);
	}

	@Override
	public Long findCount(Query query) {
		return getMongoTemplate().count(query, clazz);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<T> findList(Integer skip, Integer limit, Query query) {
		query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "createTime")));
		query.skip(skip).limit(limit);
		return find(query);
	}

	@Override
	public Integer update(Query query, Update update) {
		UpdateResult result = getMongoTemplate().updateFirst(query, update, clazz);
		return (int) result.getModifiedCount();
	}

	@Override
	public Integer update(T entity) throws Exception {
		Map<String, Object> map = mongoUtil.converObjectToParams(entity);
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(map.get("id")));
		Update update = (Update) map.get("update");
		return this.update(query, update);
	}

	@Override
	public Integer remove(T entity) {
		DeleteResult result = getMongoTemplate().remove(entity);
		return (int) (null == result ? 0 : result.getDeletedCount());
	}

	@Override
	public Integer remove(Query query, T entity) {
		DeleteResult result = getMongoTemplate().remove(query, entity.getClass());
		return (int) (null == result ? 0 : result.getDeletedCount());
	}

	@Override
	public DataResult findPage(Page page, Query query) {
		DataResult dataResult = new DataResult(Errors.EXCEPTION_UNKNOW);
		query = query == null ? new Query(Criteria.where("_id").exists(true)) : query;
		long count = this.findCount(query);
		page.setTotalnum((int) count);
		
		int currentPage = page.getPage();
		int pageSize = page.getNum();
		
		query.skip((currentPage - 1) * pageSize).limit(pageSize);
		List<T> rows = this.find(query);
		
		dataResult.setErrcode(Errors.OK);
		dataResult.setData(new Data(rows, page));
		return dataResult;
	}

	@Override
	public Integer remove(Query query) {
		DeleteResult result = getMongoTemplate().remove(query);
		return (int) (null == result ? 0 : result.getDeletedCount());
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}