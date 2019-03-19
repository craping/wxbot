package wxrobot.server.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import wxrobot.server.sync.pojo.SyncMsg;
import wxrobot.server.utils.RedisUtil;

@Component
@EnableScheduling
public class SyncContext implements SchedulingConfigurer {
    
	public static final LinkedTransferQueue<SyncSession> CONTEXT = new LinkedTransferQueue<>();
	
//	public static final Map<String, TransferQueue<SyncMsg>> MSGS = new ConcurrentHashMap<>();
	
	protected static ObjectMapper MAPPER = new ObjectMapper();
	
	private static RedisUtil redisUtil = new RedisUtil();
	
	static{
		MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}
	
	public SyncContext() {
	}
	
	public static void putMsg(String token, SyncMsg msg){
//		try {
//			redisUtil.rpush("queue_"+token, MAPPER.writeValueAsString(msg));
//		} catch (JsonProcessingException e1) {
//			e1.printStackTrace();
//		}
		token = token.contains("_m")?token.split("_")[0]:token+"_m";
		try {
			redisUtil.rpush("queue_"+token, MAPPER.writeValueAsString(msg));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(fixedDelay=3000)
    private void run() {
		long currentTime;
		
//			System.out.println("当前连接数："+CONTEXT.size());
		SyncSession session = null;
		while ((session = CONTEXT.peek()) != null) {
			currentTime = System.currentTimeMillis();
			String msg = null;
			List<SyncMsg> msgs = new ArrayList<>();
			for (int i = 0; i < 100 && (msg = redisUtil.lpop("queue_"+session.getToken())) != null; i++) {
				try {
					msgs.add(MAPPER.readValue(msg, SyncMsg.class));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(msgs.size() > 0 || currentTime - session.getTime() > 30000){
				String json = "[]";
				try {
					json = MAPPER.writeValueAsString(msgs);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				writeResponse(session, json);
				CONTEXT.poll();
			}
		}
//			List<SyncMsg> msgs = new ArrayList<>();
//			int count = session.getMsg().drainTo(msgs, 100);
//			
//			if(count > 0 || currentTime - session.getTime() > 30000){
//				String json = null;
//				try {
//					json = MAPPER.writeValueAsString(msgs);
//				} catch (JsonProcessingException e) {
//					e.printStackTrace();
//				}
//				writeResponse(session, json);
//				CONTEXT.remove(token);
//			}
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}
	
	@Bean(destroyMethod="shutdown")
	private Executor taskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }
	
	protected static void writeResponse(SyncSession session, String msg){
		
		if(session != null) {
			ByteBuf byteBuf = Unpooled.copiedBuffer(msg == null?"":msg, CharsetUtil.UTF_8);
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
			response.headers().add(HttpHeaderNames.CONTENT_TYPE, NettySyncRender.DEFAULT_CONTENT_TYPE);
			response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
			
			boolean close = (session.getRequest().headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true)
					|| session.getRequest().protocolVersion().equals(HttpVersion.HTTP_1_0)
					&& !session.getRequest().headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true));
			
			if(!close){
				response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
			}
			
			ChannelFuture future = session.getResponse().writeAndFlush(response);
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

}
