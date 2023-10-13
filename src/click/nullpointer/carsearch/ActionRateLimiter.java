package click.nullpointer.carsearch;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class ActionRateLimiter {

	private final int actionsPerTimeunit;
	private final TimeUnit timeunit;
	private Queue<FutureTask<?>> theQ = new LinkedList<>();
	private Object syncPoint = new Object();
	private volatile Thread qTask;
	private volatile long startMs, requests;

	public ActionRateLimiter(int actionsPerTimeunit, TimeUnit timeunit) {
		if (timeunit == TimeUnit.MICROSECONDS || timeunit == TimeUnit.NANOSECONDS)
			throw new IllegalStateException("Too fine granuality.");
		this.actionsPerTimeunit = actionsPerTimeunit;
		this.timeunit = timeunit;
	}

	public TimeUnit getTimeunit() {
		return timeunit;
	}

	public int getActionsPerTimeunit() {
		return actionsPerTimeunit;
	}

	public Future<?> submitAction(Callable<?> task) {
		FutureTask<?> ft = new FutureTask<>(task);
		theQ.add(ft);
		synchronized (syncPoint) {
			if (qTask == null) {
				qTask = new Thread(() -> processQueue());
				qTask.start();
			}
		}
		return ft;
	}

	public void submitAction(Runnable task) {
		submitAction(() -> {
			task.run();
			return null;
		});
	}

	private void processQueue() {
		startMs = System.currentTimeMillis();
		while (!theQ.isEmpty()) {
			Thread.yield();
			if (requests >= actionsPerTimeunit) {
				long wait = (startMs + timeunit.toMillis(1)) - System.currentTimeMillis();
				if (wait > 0) {
					try {
//						System.err.println("Rate limit hit. Wait " + wait + " ms");
						Thread.sleep(wait);
						requests = 0;
						startMs = System.currentTimeMillis();
					} catch (InterruptedException e) {
						e.printStackTrace();
						continue; // If an exception occurs, re-run hoping we'll wait this time.
					}
				}
			}
			++requests;
			theQ.poll().run();
		}
		// Kill self.
		synchronized (syncPoint) {
			qTask = null;
		}
	}

	public void waitForAll() throws InterruptedException {
		Thread toJoin = qTask;
		if (toJoin != null)
			toJoin.join();
	}

//	public static void main(String[] args) throws Exception {
//		ActionRateLimiter limit = new ActionRateLimiter(10, TimeUnit.SECONDS);
//		for (int i = 0; i < 9; ++i) {
//			final int x = i;
//			Future<String> s = (Future<String>) limit.submitAction(() -> {
//				System.out.println("Proc " + x);
//				return "";
//			});
//		}
//
//		Thread.sleep(4000);
//		System.out.println("round 2");
//		for (int i = 0; i < 100; ++i) {
//			final int x = i;
//			limit.submitAction(() -> {
//				System.out.println("R2 Proc " + x);
//				return null;
//			});
//		}
//
//	}

}
