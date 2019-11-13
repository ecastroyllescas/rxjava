package com.gmr.rxjava;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Example implementation of a simulated autocomplete text search, that only executes a search after the user
 * stops typing a specified period of time.
 */
public class IncrementalTextSearch {

    private static Map<String, List<String>> AUTOCOMPLETE_OPTIONS = new HashMap<>();
    static {
        AUTOCOMPLETE_OPTIONS.put("r", Arrays.asList("razor", "run", "restriction", "rework", "real", "reactor", "reach"));
        AUTOCOMPLETE_OPTIONS.put("re", Arrays.asList("resistance", "renaissance", "rework", "reaction", "read", "reach"));
        AUTOCOMPLETE_OPTIONS.put("rea", Arrays.asList("reaction", "real", "read", "reach", "reactor"));
        AUTOCOMPLETE_OPTIONS.put("reac", Arrays.asList("reaction", "reach", "react", "reactor"));
        AUTOCOMPLETE_OPTIONS.put("react", Arrays.asList("reaction", "reactor", "react"));
        AUTOCOMPLETE_OPTIONS.put("reacti", Arrays.asList("reactive", "reaction"));
        AUTOCOMPLETE_OPTIONS.put("reactiv", Arrays.asList("reactiv", "reactive"));
        AUTOCOMPLETE_OPTIONS.put("reactive", Collections.singletonList("reactive"));
    }

    public static void main(String[] args) throws InterruptedException {
        textObservable("reac", 3000, "tive")
                .subscribeOn(Schedulers.computation())
                .debounce(250, TimeUnit.MILLISECONDS) // the period of time the user needs to stop typing after an event is propagated downstream
                .observeOn(Schedulers.io())
                .map(IncrementalTextSearch::autocomplete)
                .subscribe(System.out::println);
        Thread.sleep(15000);
    }

    private static Observable<String> textObservable(String prefix, long delay, String suffix) {
        return new Observable<>() {
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                // Simulate user is typing...
                String sent = "";
                sent = sendText(observer, sent, prefix);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendText(observer, sent, suffix);
                observer.onComplete();
            }
        };
    }

    private static String sendText(Observer<? super String> observer, String prefix, String text) {
        String sent = prefix;
        for (char aChar : text.toCharArray()) {
            sent += aChar;
            System.out.println(String.format("Input: %s... thread: %s", sent, Thread.currentThread().getName()));
            observer.onNext(sent);
            try {
                Thread.sleep(50); // Typing speed...
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sent;
    }

    private static List<String> autocomplete(String prefix) {
        System.out.println(String.format("Searching %s thread: %s", prefix, Thread.currentThread().getName()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return AUTOCOMPLETE_OPTIONS.get(prefix);
    }
}
