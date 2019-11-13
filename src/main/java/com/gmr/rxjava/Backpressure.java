package com.gmr.rxjava;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposables;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.DefaultSubscriber;

import java.util.stream.IntStream;

/**
 * Example for demonstrating backpressure concepts. Recommended to run with low memory limits
 * (like -Xms64M -Xmx64M) to see OutOfMemoryErrors.
 * Interesting values to play with are indicated with CHANGEME comment.
 */
public class Backpressure {

    public static void main(String[] args) throws InterruptedException {
        // Run a Flowable created from a regular Observable
        sourceFromObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation()) // CHANGEME: This could be AndroidAchedulers.mainThread(). Also there's
                                                     // an overloaded version indicating internal buffer size.
                .subscribe(Backpressure::consume, Throwable::printStackTrace, () -> System.out.println("Completed!"));

        Thread.sleep(40000);


        // Run a Flowable created from a generator. Events are requested on demand
        sourceFromGenerator()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation()) // CHANGEME: This could be AndroidAchedulers.mainThread(). Also there's
                                                     // an overloaded version indicating internal buffer size.
                .subscribe(new DefaultSubscriber<>() {
                    @Override
                    protected void onStart() {
                        request(1);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        consume(integer);
                        request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete!");
                    }
                });

        Thread.sleep(40000);
    }

    private static Flowable<Integer> sourceFromObservable() {
        return new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? super Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                System.out.println(String.format("Starting events, thread: %s", Thread.currentThread().getName()));
                IntStream.range(1, 1_000_000_000)
                        .forEach(observer::onNext);
                System.out.println(String.format("Finished sending events, thread: %s", Thread.currentThread().getName()));
                observer.onComplete();
            }
        }
                .toFlowable(BackpressureStrategy.BUFFER); // CHANGEME: This controls different backpressure strategies
    }

    private static Flowable<Integer> sourceFromGenerator() {
        return Flowable.generate(
                () -> { // initial state
                    System.out.println(String.format("Starting events, thread: %s", Thread.currentThread().getName()));
                    return 0;
                },
                (current, output) -> { // generator
                    System.out.println(String.format("Sending event for %d, thread: %s", ++current, Thread.currentThread().getName()));
                    if (current < 1_000_000_000) {
                        output.onNext(current);
                        return current;
                    } else {
                        output.onComplete();
                        return current;
                    }
                },
                d -> {
                } // dispose state
        );
    }

    private static void consume(int input) {
        System.out.println(String.format("Consuming %3d thread: %s", input, Thread.currentThread().getName()));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
