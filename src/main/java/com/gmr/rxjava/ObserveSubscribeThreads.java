package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Examples of the effects of using subscribeOn and observeOn.
 */
public class ObserveSubscribeThreads {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Start sequence, no schedulers ---");
        createObservable("1")
                .map(ObserveSubscribeThreads::map)
                .subscribe(ObserveSubscribeThreads::observe);
        System.out.println("--- End sequence, no schedulers ---");

        System.out.println("--- Start sequence, subscribeOn Schedulers.computation() ---");
        createObservable("1")
                .map(ObserveSubscribeThreads::map)
                .subscribeOn(Schedulers.computation())
                .map(ObserveSubscribeThreads::map)
                .subscribe(ObserveSubscribeThreads::observe);
        Thread.sleep(2000);
        System.out.println("--- End sequence, subscribeOn Schedulers.computation() ---");

        System.out.println("--- Start sequence, subscribeOn Schedulers.computation(), observeOn Schedulers.newThread() ---");
        createObservable("1")
                .map(ObserveSubscribeThreads::map)
                .subscribeOn(Schedulers.io())
                .map(ObserveSubscribeThreads::map)
                .observeOn(Schedulers.newThread())
                .map(ObserveSubscribeThreads::map)
                .subscribe(ObserveSubscribeThreads::observe);
        Thread.sleep(2000);
        System.out.println("--- End sequence, subscribeOn Schedulers.computation(), observeOn Schedulers.newThread() ---");

        System.out.println("--- Start sequence, subscribeOn Schedulers.computation(), 2 events ---");
        createObservable("1", "2")
                .map(ObserveSubscribeThreads::map)
                .subscribeOn(Schedulers.computation())
                .map(ObserveSubscribeThreads::map)
                .subscribe(ObserveSubscribeThreads::observe);
        Thread.sleep(2000);
        System.out.println("--- End sequence, subscribeOn Schedulers.computation(), 2 events ---");
    }

    private static Observable<String> createObservable(String... events) {
        return new Observable<>() {
            @Override
            protected void subscribeActual(Observer observer) {
                for (String event : events) {
                    log("newEvent", event);
                    observer.onNext(event);
                }
                observer.onComplete();
            }
        };
    }

    private static String map(String anEvent) {
        log("map", anEvent);
        return anEvent.toUpperCase();
    }

    private static void observe(String anEvent) {
        log("observed", anEvent);
    }

    private static void log(String action, String event) {
        System.out.println(String.format("action: %-15s event: %-3s thread: %s", action, event, Thread.currentThread().getName()));
    }

}
