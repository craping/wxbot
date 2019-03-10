package client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.activation.MimetypesFileTypeMap;

import com.cherry.jeeves.domain.shared.ChatRoomDescription;

public class Test {
	public static void main0(String[] args) throws Exception {
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
		
		while(true){
			String[] dateTime = new SimpleDateFormat("M,d,H,m,s").format(new Date()).split(",");
			System.out.println(String.join(",",dateTime));
			Thread.sleep(1000);
		}
		
	}
	
	private static Queue<Integer> list1 = new ConcurrentLinkedQueue<>();
	private static Queue<Integer> list2 = new ConcurrentLinkedQueue<>();
	private static Queue<Integer> list3 = new ConcurrentLinkedQueue<>();
	private static Lock lock = new ReentrantLock();

	public static void maindf(String[] args) {
	    IntStream.range(0, 10000).forEach(list1::add);

	    IntStream.range(0, 10000).parallel().forEach(list2::add);

	    IntStream.range(0, 10000).parallel().forEach(i -> {
	    lock.lock();
	    try {
	        list3.add(i);
	    }finally {
	        lock.unlock();
	    }
	    });

	    System.out.println("串行执行的大小：" + list1.size());
	    System.out.println("并行执行的大小：" + list2.size());
	    System.out.println("加锁并行执行的大小：" + list3.size());
	}
	
	public static void main3(String[] args) throws Exception {
		Map<String, Long> map = new ConcurrentHashMap<>();
		map.put("a", 1L);
		map.put("b", 2L);
		map.remove(null);
		AtomicInteger count = new AtomicInteger(0);
		map.forEach((k, v) -> {
			System.out.println(k+"="+v);
			map.put(k+count.getAndIncrement(), map.remove(k));
		});
	}
	
	static Queue<Integer> queue = new ConcurrentLinkedQueue<>();
	public static void main(String[] args) throws Exception {
		new Thread(() -> {
			while (true) {
				queue.stream().filter(i -> i>-1).forEach(i -> {
					System.out.println(i);
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("获取完成");
			}
		}).start();
		
		int i = 0;
		while (true) {
			queue = new ConcurrentLinkedQueue<>();
			queue.add(i);
			i++;
			Thread.sleep(50);
			if(i ==50){
				System.out.println("添加完成");
				Thread.sleep(5000000);
			}
		}
	}
	
	public static void main1sdf3(String[] args) throws Exception {
		Set<Long> set = Collections.synchronizedSet(new LinkedHashSet<>());
		new Thread(() -> {
			while (true) {
//				Object[] arr = map.entrySet().toArray();
//				for (Object entry : arr) {
//					System.out.println(entry);
//				}
				
//				for (String key : map.keySet()) {
//					System.out.println(key+"="+map.get(key));
//				}
				set.forEach(v -> {
					System.out.println(v);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				System.out.println("遍历完成:"+set.size());
			}
		}).start();
		
		long i = 0;
		while (true) {
			set.add(i);
			i++;
			Thread.sleep(50);
			if(i ==50){
				System.out.println("添加完成");
				Thread.sleep(5000000);
			}
		}
	}
	
	public static void main123(String[] args) throws Exception {
		Queue<Integer> queue = new ConcurrentLinkedQueue<>();
		new Thread(() -> {
			while (true) {
				for (Integer i : queue) {
					System.out.println(i);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
//				queue.forEach(i -> {
//					System.out.println(i);
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				});
				System.out.println("遍历完成:"+queue.size());
			}
		}).start();
		
		int i = 0;
		while (true) {
			queue.add(i);
			i++;
			Thread.sleep(50);
			if(i ==50){
				System.out.println("添加完成");
				Thread.sleep(5000000);
			}
		}
	}
	
	public static void main1(String[] args) throws Exception {
		TransferQueue<Integer> queue = new LinkedTransferQueue<>();
		new Thread(() -> {
			while (true) {
				List<Integer> list = new ArrayList<>();
				System.out.println("poll:"+queue.drainTo(list, 100));
				
			}
		}).start();
		
		int i = 0;
		while (true) {
			queue.put(i);
			System.out.println("put:"+i);
		}
	}
	
	public static void main2(String[] args) throws Exception {
		Queue<Integer> queue = new LinkedTransferQueue<>();
		new Thread(() -> {
			while (true) {
				
//				for (Integer i : list) {
//					System.out.println("remove:"+i);
//					list.remove(i);
//				}
				Integer i = null;
				while ((i = queue.peek()) != null) {
					System.out.println("remove:"+i);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					queue.poll();
				}
//				list.forEach(i -> {
//					System.out.println("remove:"+i);
//					list.remove(i);
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				});
				System.out.println("遍历完成");
			}
		}).start();
		
		new Thread(() -> {
			int i = 0;
			while (true) {
				queue.offer(i);
				System.out.println("add:"+i);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
		}).start();
		
		int i = 0;
		while (true) {
			queue.offer(i);
			System.out.println("add:"+i);
			Thread.sleep(500);
			i++;
		}
	}
}
