package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Example implementation of a simple in memory cache with automatic scheduled refreshes.
 */
public class PeriodicCacheRefresh {

    public static void main(String[] args) throws InterruptedException {
        AtomicReference<ConnectableObservable<Integer>> cache = new AtomicReference<>();
        Observable.interval(0, 5, TimeUnit.SECONDS) // how often to refresh the cache
                .observeOn(Schedulers.io()) // observe on a separate thread to not block other threads
                .subscribe(n -> cache.set(buildCache(5, TimeUnit.SECONDS))); // set cache expiration to clean used memory

        System.out.println("Asking on first subscribe");
        while (cache.get() == null) {
            Thread.sleep(100);
        }
        cache.get().blockingSubscribe(System.out::println);
        System.out.println("Asking on second subscribe");
        cache.get().blockingSubscribe(System.out::println);
        System.out.println("Asking on third subscribe");
        cache.get().blockingSubscribe(System.out::println);

        Thread.sleep(7000);
        System.out.println("Asking on fourth subscribe");
        cache.get().blockingSubscribe(System.out::println);
        Thread.sleep(30000);
    }

    private static List<Integer> fetchData() {
        System.out.println(String.format("Fetching data from source. Thread: %s", Thread.currentThread().getName()));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return IntStream.range(1, 10).boxed().collect(Collectors.toList());
    }

    private static ConnectableObservable<Integer> buildCache(int expTime, TimeUnit timeUnit) {
        ConnectableObservable<Integer> observable = Observable.fromIterable(fetchData())
                .replay(expTime, timeUnit); // indicates that events should be cached for a specific period of time
        observable.connect(); // retrieves data
        return observable;
    }
}
