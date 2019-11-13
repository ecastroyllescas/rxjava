package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Example comparison of parallel function execution with standard Java streams and RxJava.
 */
public class ParallelProcessing {

    public static void main(String[] args) {
        List<String> inputList = Arrays.asList("1", "2", "3", "4", "5");
        System.out.println("--- Start processing with java streams ---");
        List<Integer> result = processBackgroundStreams(inputList);
        System.out.println("--- Result with streams: " + result + " thread: " + Thread.currentThread().getName());

        System.out.println("--- Start processing with rx ---");
        result = processBackgroundRx(inputList);
        System.out.println("--- Result with Rx: " + result + " thread: " + Thread.currentThread().getName());
    }

    private static List<Integer> processBackgroundStreams(List<String> inputList) {
        return inputList.stream()
                .parallel() // this single line of code handles all parallelism.
                            // There are no options to change the number of threads, how long they live, etc.
                .map(ParallelProcessing::toInt)
                .collect(Collectors.toList());
    }

    private static List<Integer> processBackgroundRx(List<String> inputList) {
        List<Integer> results = new ArrayList<>();
        Observable.fromIterable(inputList)
                .flatMap(n -> Observable.just(n)
                        .map(ParallelProcessing::toInt)
                        .subscribeOn(Schedulers.computation())) // subscribeOn called on every item individually to execute the function
                                                                // concurrently on separate threads
                .blockingSubscribe(n -> { // blockingSubscribe waits until all events are processed and onComplete is called on the subscriber
                    System.out.println(String.format("Event received: %d in thread: %s", n, Thread.currentThread().getName()));
                    results.add(n);
                });
        return results;
    }

    private static Integer toInt(String item) {
        System.out.println(String.format("Processing item %s in thread: %s", item, Thread.currentThread().getName()));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(item);
    }
}
