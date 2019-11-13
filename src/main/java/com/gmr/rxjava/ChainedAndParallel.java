package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of executing the following sequence of requests:
 * - request-1
 * - request-2
 *   - request-3-1, request-3-2, request-3-3
 * - request-4
 */
public class ChainedAndParallel {

    public static void main(String[] args) {
        Observable.fromSupplier(ChainedAndParallel::request1)
                .map(ChainedAndParallel::request2)
                .flatMap(r -> {
                    Observable<String> res31Observable = Observable.just(r)
                            .map(ChainedAndParallel::request31)
                            .subscribeOn(Schedulers.io());
                    Observable<String> res32Observable = Observable.just(r)
                            .map(ChainedAndParallel::request32)
                            .subscribeOn(Schedulers.io());
                    Observable<String> res33Observable = Observable.just(r)
                            .map(ChainedAndParallel::request33)
                            .subscribeOn(Schedulers.io());
                    return Observable.zip(res31Observable, res32Observable, res33Observable, (r31, r32, r33) -> {
                        List<String> all = new ArrayList<>(r);
                        all.addAll(Arrays.asList(r31, r32, r33));
                        return all;
                    });
                })
                .map(ChainedAndParallel::request4)
                .blockingSubscribe(ChainedAndParallel::handleCombinedResponse, ChainedAndParallel::handleError);
    }

    private static String request1() throws IOException {
        log("request-1") ;
//        throw new IOException("Internal Server Error");
        return "response-1";
    }

    private static List<String> request2(String response1) throws IOException {
        log("request-2") ;
//        throw new IOException("Internal Server Error");
        return Arrays.asList(response1, "response-2");
    }

    private static String request31(List<String> response2) throws IOException {
        log("request-3-1") ;
//        throw new IOException("Internal Server Error");
        return "response-3-1";
    }

    private static String request32(List<String> response2) throws IOException {
        log("request-3-2") ;
//        throw new IOException("Internal Server Error");
        return "response-3-2";
    }

    private static String request33(List<String> response2) throws IOException {
        log("request-3-3") ;
//        throw new IOException("Internal Server Error");
        return "response-3-3";
    }

    private static List<String> request4(List<String> allResponses) throws IOException {
        log("request-4") ;
        List<String> resp = new ArrayList<>(allResponses);
        resp.add("response-4");
//        throw new IOException("Internal Server Error");
        return resp;
    }

    private static void handleCombinedResponse(List<String> combinedResponse) {
        log("handleCombinedResponse");
        System.out.println(combinedResponse);
    }

    private static void handleError(Throwable t) {
        log("handleError");
        System.out.println(String.format("Error: %s, message: %s", t.getClass().getSimpleName(), t.getMessage()));
    }

    private static void log(String msg) {
        System.out.println(String.format("[%-30s] %s", Thread.currentThread().getName(), msg));
    }
}
