package com.jalarbee.knihovny.workq;


import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author Abdoulaye Diallo
 */

public interface WorkQueue<T> {
    boolean submit(T unit);
}

final class WorkQueueImpl<T> implements WorkQueue<T> {

    private final ExecutorService threadPool;
    private final Queue<T> queue;
    private final Task<T> task;

    WorkQueueImpl(ExecutorService threadPool, Queue<T> queue, Task<T> task) {
        this.threadPool = threadPool;
        this.queue = queue;
        this.task = task;
    }

    boolean moreWork() {
        return !queue.isEmpty();
    }

    T take() {
        return queue.poll();
    }

    @Override
    public boolean submit(T unit) {
        synchronized (queue) {
            if (queue.offer(unit)) {
                threadPool.execute(new Work(task));
                return true;
            }
            return false;

        }
    }

    final class Work implements Runnable {

        private final Task<T> task;

        Work(Task<T> task) {
            this.task = task;
        }

        @Override
        public void run() {
            int reps = 0;
            while (WorkQueueImpl.this.moreWork() && task.doIt(WorkQueueImpl.this.take())) {
                reps++;
            }
        }
    }

    public static class WorkQueueThreadPool extends ThreadPoolExecutor {

        public WorkQueueThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }
    }
}
