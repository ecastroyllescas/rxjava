package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 * Example for looking how threads are created and reused depending on the scheduler used.
 */
public class SchedulersExample {

    public static void main(String[] args) throws InterruptedException {
        // basic implementation of a working queue, like the one present in Android
        LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

        Observable.fromArray(createMessages())
                .flatMap(n -> Observable.just(n)
                        .map(SchedulersExample::process)
                        .subscribeOn(Schedulers.newThread())) // change the scheduler here to observe different thread behavior
                .observeOn(mainThreadScheduler(tasks))
                .subscribe(SchedulersExample::printResults);

        while (true) {
            tasks.take().run();
        }
    }

    private static String[] createMessages() {
        return IntStream.range(1, 50)
                .mapToObj(i -> String.format("message-%d", i))
                .toArray(String[]::new);
    }

    private static String process(String n) {
        System.out.println(String.format("Message: %-15s thread: %s", n, Thread.currentThread().getName()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return n;
    }

    private static void printResults(String message) {
        System.out.println(String.format("Final result message: %-15s thread: %s", message, Thread.currentThread().getName()));
    }

    /**
     * Creates a Scheduler working on a queue of tasks. This would be the equivalent of AndroidSchedulers.mainThread
     * of the JVM world.
     *
     * @param tasks queue of tasks.
     * @return rx Scheduler.
     */
    private static Scheduler mainThreadScheduler(LinkedBlockingQueue<Runnable> tasks) {
        return Schedulers.from(new Executor() {
            @Override
            public void execute(Runnable runnable) {
                tasks.add(runnable);
            }
        });
    }
}
