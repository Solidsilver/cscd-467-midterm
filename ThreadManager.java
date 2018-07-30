import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class ThreadManager extends Thread {
	private final int T1 = 10, T2 = 20;
	private int wait;
	private ThreadPool p;
	private boolean terminated;
	private ServerSocket term;

	public ThreadManager(ThreadPool p, ServerSocket term) {
		this.p = p;
		this.terminated = false;
		this.wait = 5000;
		this.term = term;
	}

	@Override
	public void run() {
		this.p.startPool();

		while(!terminated) {
			try {
				Thread.sleep(this.wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (p.jobCount() > T1 && p.jobCount() <= T2 &&  p.numThreadsRunning() == 5) {
				p.incresePool();
				while (p.jobCount() >= T1 && p.jobCount() <= T2) {}
				if (p.jobCount() < T1) {
					p.decreasePool();
				}
			}
			if (p.jobCount() > T2 && p.numThreadsRunning() <= 10) {
				p.incresePool();
				if (p.numThreadsRunning() == 10) {
					p.incresePool();
				}
				while (p.jobCount() > T2) {}
				p.decreasePool();
			}
			if (p.jobCount() < T1 && p.numThreadsRunning() == 10) {
				p.decreasePool();
			}
			
		}
		this.p.stopPool();
		try {
			term.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log("ThreadManager Closed");
	}

	public boolean isTerminated() {
		return this.terminated;
	}

	public void terminate() {
		this.terminated = true;
	}

	private void log(String message) {
		System.out.println("TM: " + message);
	}

}