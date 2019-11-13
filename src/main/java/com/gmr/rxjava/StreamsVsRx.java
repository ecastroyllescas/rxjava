package com.gmr.rxjava;

import io.reactivex.rxjava3.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Example comparison of Java streams and RxJava operators.
 */
public class StreamsVsRx {

    public static void main(String[] args) {
        List<String> inputList = Arrays.asList("1", "2", "3", "4", "5");
        List<Integer> results = processWithStreams(inputList);
        System.out.println("Results with streams: " + results);

        results = processWithRx(inputList);
        System.out.println("Results with rx: " + results);
    }

    private static List<Integer> processWithStreams(List<String> inputList) {
        return inputList.stream()
                .map(Integer::parseInt)
                .map(n -> n * n)
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
    }

    private static List<Integer> processWithRx(List<String> inputList) {
        List<Integer> results = new ArrayList<>();
        Observable.fromIterable(inputList)
                .map(Integer::parseInt)
                .map(n -> n * n)
                .filter(n -> n % 2 == 0)
                .blockingSubscribe(results::add);
        return results;
    }
}
