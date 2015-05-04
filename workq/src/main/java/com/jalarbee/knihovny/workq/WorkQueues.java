package com.jalarbee.knihovny.workq;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Abdoulaye Diallo
 */
public class WorkQueues {

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder<T> {

        private ThreadPoolBuilder<T> threadPoolBuilder;

        private Task<T> task;
        private Queue<T> queue;

        private Builder() {
        }

        public Builder<T> task(Task<T> task) {
            this.task = task;
            return this;
        }

        public Builder<T> queue(Queue<T> queue) {
            this.queue = queue;
            return this;
        }

        public ThreadPoolBuilder<T> threadPoolBuilder() {
            return this.setThreadPoolBuilder(new ThreadPoolBuilder<>(this));
        }

        public WorkQueue<T> build() {
            return new WorkQueueImpl<>(threadPoolBuilder.build(), queue, task);
        }

        private ThreadPoolBuilder<T> setThreadPoolBuilder(ThreadPoolBuilder<T> threadPoolBuilder) {
            this.threadPoolBuilder = threadPoolBuilder;
            return this.threadPoolBuilder;
        }
    }

    public static class ThreadPoolBuilder<T> {

        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;
        private TimeUnit unit;
        private BlockingQueue<Runnable> runnables;
        private ThreadFactory threadFactory;
        private RejectedExecutionHandler rejectedExecutionHandler;

        private Builder<T> builder;

        private ThreadPoolBuilder(Builder<T> builder) {
            this.builder = builder;
        }

        private WorkQueueImpl.WorkQueueThreadPool build() {
            return new WorkQueueImpl.WorkQueueThreadPool(corePoolSize, maximumPoolSize, keepAliveTime, unit, runnables, threadFactory, rejectedExecutionHandler);
        }

        public Builder<T> back() {
            return builder ;
        }

        public ThreadPoolBuilder<T> keepAliveTime(long keepAliveTime, TimeUnit unit) {
            this.keepAliveTime = keepAliveTime;
            this.unit = unit;
            return this;
        }

        public ThreadPoolBuilder<T> maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public ThreadPoolBuilder<T> corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public ThreadPoolBuilder<T> runnables(BlockingQueue<Runnable> runnables) {
            this.runnables = runnables;
            return this;
        }

        public ThreadPoolBuilder<T> threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public ThreadPoolBuilder<T> rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

    }
}
