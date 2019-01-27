package client;

import java.io.File;
import java.util.stream.Stream;

import javax.activation.MimetypesFileTypeMap;

import com.cherry.jeeves.domain.shared.ChatRoomDescription;

public class Test {
	public static void main(String[] args) throws Exception {
//		Thread thread = new Thread(() -> {
//			Thread daemonThread = new Thread(() -> {
//				while(true)
//					System.out.println(System.currentTimeMillis());
//			});
//			daemonThread.setDaemon(true);
//			daemonThread.start();
//			Thread.currentThread().isInterrupted();
//		});
//		thread.start();
//		thread.interrupt();
		
		System.out.println(new MimetypesFileTypeMap().getContentType(new File("C:\\Users\\Crap\\Pictures\\Camera Roll\\QRcode.png")));
		
		ChatRoomDescription[] a = Stream.of(1,2,3,4,5).skip(50).limit(50).map(x -> {
			ChatRoomDescription description = new ChatRoomDescription();
			description.setUserName(String.valueOf(x));
			return description;
		}).toArray(ChatRoomDescription[]::new);
		System.out.println(a.length);
	}
}
