package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example comparison of parallel function execution with standard Java concurrency package and RxJava.
 */
public class JavaConcurrency {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("Starting Java concurrency library");
        System.out.println(String.format("Result: %s", squareJavaConcurrency(numbers)));
        System.out.println("Starting RxJava");
        System.out.println(String.format("Result: %s", squareRx(numbers)));
    }


    private static List<Integer> squareJavaConcurrency(List<Integer> input) throws InterruptedException {
        ExecutorService executor = Executors.newWorkStealingPool();
        List<Callable<Integer>> callables = new ArrayList<>();

        for (Integer number : input) {
            Callable<Integer> task = () -> {
                System.out.println(String.format("Processing item %d on thread %s", number, Thread.currentThread().getName()));
                Thread.sleep(3000);
                return number * number;
            };
            callables.add(task);
        }

        List<Integer> result = new ArrayList<>();
        executor.invokeAll(callables)
                .stream()
                // Standard Java concurrency code ends here, then we only have streams API methods to operate on elements of the stream
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                .forEach(result::add);

        return result;
    }

    private static List<Integer> squareRx(List<Integer> input) {
        List<Integer> result = new ArrayList<>();
        Observable.fromIterable(input)
                .flatMap(n -> Observable.just(n)
                        .map(number -> {
                            System.out.println(String.format("Processing item %d on thread %s", number, Thread.currentThread().getName()));
                            Thread.sleep(3000);
                            return number * number;
                        })
                        .subscribeOn(Schedulers.computation())) // subscribeOn called on every item individually to execute the function
                                                                // concurrently on separate threads
                // here we can chain other concurrency specific methods not available in standard Java concurrency package
//                .replay(1, TimeUnit.MINUTES)
                .blockingSubscribe(result::add);
        return result;
    }
}
