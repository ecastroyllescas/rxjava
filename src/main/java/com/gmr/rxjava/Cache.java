package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Example for how to build a simple cache with RxJava.
 */
public class Cache {

    public static void main(String[] args) throws InterruptedException {
        ConnectableObservable<String> source = getList()
                .map(Cache::getItemDetails)
                .replay(99, 2000, TimeUnit.SECONDS);
        System.out.println("Warming up cache...");
        source.connect(); // actual processing is executed with this line

        System.out.println("\nFirst subscriber...");
        source.blockingSubscribe(i -> System.out.print(i + ", "));
        System.out.println("\nFinished first subscriber");

        System.out.println("\nSecond subscriber...");
        source.blockingSubscribe(i -> System.out.print(i + ", "));
        System.out.println("\nFinished second subscriber");

        Thread.sleep(10000);
        System.out.println("\nThird subscriber...");
        source.blockingSubscribe(i -> System.out.print(i + ", "));
        System.out.println("\nFinished third subscriber");
    }

    private static Observable<String> getList() {
        return new Observable<>() {
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                System.out.println(String.format("Getting list, thread: %s", Thread.currentThread().getName()));
                IntStream.range(1, 10)
                        .forEach(i -> observer.onNext("item-" + i));
                observer.onComplete();
            }
        };
    }

    private static String getItemDetails(String item) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return item + "-details";
    }
}
