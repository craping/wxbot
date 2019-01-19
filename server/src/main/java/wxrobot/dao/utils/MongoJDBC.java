package wxrobot.dao.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoJDBC {
	public static final Logger log = LogManager.getLogger(MongoJDBC.class);

	static final String DBName = "wxbot";
	static final String ServerAddress = "127.0.0.1";
	static final int PORT = 29017;

	public MongoJDBC() {
	}
	
	public static void main(String args[]) {
		MongoJDBC helper = new MongoJDBC();
		System.out.println(helper.getMongoDataBase(helper.getMongoClient()));
	}

	public MongoClient getMongoClient() {
		MongoClient mongoClient = null;
		try {
			// 连接到 mongodb 服务
			mongoClient = new MongoClient(ServerAddress, PORT);
			log.debug("Connect to mongodb successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		return mongoClient;
	}

	public MongoDatabase getMongoDataBase(MongoClient mongoClient) {
		MongoDatabase mongoDataBase = null;
		try {
			if (mongoClient != null) {
				// 连接到数据库
				mongoDataBase = mongoClient.getDatabase(DBName);
				log.debug("Connect to DataBase successfully");
			} else {
				throw new RuntimeException("MongoClient不能够为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mongoDataBase;
	}

	public MongoDatabase getMongoDataBase() {
		MongoDatabase mongoDataBase = null;
		try {
			// 连接到数据库
			mongoDataBase = getMongoDataBase(getMongoClient());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mongoDataBase;
	}

	public void closeMongoClient(MongoDatabase mongoDataBase, MongoClient mongoClient) {
		if (mongoDataBase != null) {
			mongoDataBase = null;
		}
		if (mongoClient != null) {
			mongoClient.close();
		}
		log.debug("CloseMongoClient successfully");
	}
}