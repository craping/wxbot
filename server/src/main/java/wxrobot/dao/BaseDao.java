package wxrobot.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import wxrobot.dao.page.Page;

public interface BaseDao<T> {

	/** 插入 */
	T insert(T entity);
	
	/** 批量插入*/
	void batchInsert(List<T> batch);

	/** 根据ID查询 */
	T findOne(String id);

	/** 根据条件查询一个 */
	T findOne(Query query);

	/** 通过ID获取记录,并且指定了集合名 */
	T findById(String id, String collectionName);

	/** 按条件查找 */
	List<T> find(Query query);

	/** 获得所有该类型记录 */
	List<T> findAll();

	/** 获得所有该类型记录,并且指定了集合名 */
	List<T> findAll(String collectionName);

	/** 统计查找结果个数 */
	Long findCount(Query query);

	/** 分页查-按照创建时间升序排列 */
	List<T> findList(Integer skip, Integer limit, Query query);

	/** 按条件分页查询 */
	Page<T> findPage(Page<T> page, Query query);

	/** 查找并更新第一个 */
	Integer update(Query query, Update update) throws Exception;

	/** 按照主键更新第一个 */
	Integer update(T entity) throws Exception;

	/** 根据主键删除 */
	Integer remove(T entity);

	/** 根据条件删除给定表中的文档 */
	Integer remove(Query query);

	/** 根据条件删除给定表中的文档 */
	Integer remove(Query query, T entity);
}
