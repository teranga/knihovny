package com.jalarbee.knihovny.workq;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Abdoulaye Diallo
 */
public class WorkQeueTest {

    private WorkQueue<String> workQueue;
    private TestTask task;
    private CountDownLatch latch;

    @Before
    public void setUp() {
        latch = new CountDownLatch(4);
        task = new TestTask(new AtomicInteger(0), latch);
        workQueue = WorkQueues.builder()
                .queue(new LinkedBlockingQueue<String>(10))
                .threadPoolBuilder()
                .corePoolSize(1)
                .maximumPoolSize(2)
                .keepAliveTime(10, TimeUnit.SECONDS)
                .runnables(new LinkedBlockingQueue<>(10))
                .threadFactory(r -> new Thread(r))
                .rejectedExecutionHandler((r, executor) -> System.out.println("rejected..."))
                .back()
                .task(task)
                .build();
    }

    @Test
    public void sumbit() throws InterruptedException {
        workQueue.submit("zeroth");
        workQueue.submit("first");
        workQueue.submit("second");
        workQueue.submit("third");
        latch.await();
        Assert.assertEquals(task.reps.get(), 4);
    }

    static class TestTask implements Task<String> {

        public final AtomicInteger reps;
        private final CountDownLatch latch;

        public TestTask(AtomicInteger reps, CountDownLatch latch) {
            this.reps = reps;
            this.latch = latch;
        }

        @Override
        public boolean doIt(String unit) {
            try {
                System.out.println("consuming " + reps.getAndIncrement() + "." + unit);
                return true;
            } finally {
                latch.countDown();
            }
        }
    }
}
