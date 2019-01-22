package client;

public class Test {
	public static void main(String[] args) throws Exception {
		Thread thread = new Thread(() -> {
			Thread daemonThread = new Thread(() -> {
				while(true)
					System.out.println(System.currentTimeMillis());
			});
			daemonThread.setDaemon(true);
			daemonThread.start();
			Thread.currentThread().isInterrupted();
		});
		thread.start();
		thread.interrupt();
		
		Thread.sleep(50000);
	}
}
