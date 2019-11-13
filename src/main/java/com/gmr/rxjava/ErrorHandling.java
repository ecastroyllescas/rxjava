package com.gmr.rxjava;


import io.reactivex.rxjava3.core.Observable;

/**
 * Example for demonstrating basic error handling in RxJava.
 */
public class ErrorHandling {

    public static void main(String[] args) {
        onErrorNotImplemented();
        onErrorImplemented();
        runtimeException();
        fallback();
    }

    private static void onErrorNotImplemented() {
        System.out.println("=== Entering onErrorNotImplemented");
        Observable.fromCallable(() -> divide(-1, -1))
                .blockingSubscribe(System.out::println);
    }

    private static void onErrorImplemented() {
        System.out.println("=== Entering onErrorImplemented");
        Observable.fromCallable(() -> divide(-1, -1))
                .blockingSubscribe(System.out::println, t -> System.out.println(String.format("onError called: %s: %s", t.getClass().getSimpleName(), t.getMessage())));
    }

    private static void runtimeException() {
        System.out.println("=== Entering runtimeException");
        Observable.fromCallable(() -> divide(2, 0))
                .blockingSubscribe(System.out::println, t -> System.out.println(String.format("onError called: %s: %s", t.getClass().getSimpleName(), t.getMessage())));
    }

    private static void fallback() {
        System.out.println("=== Entering fallback");
        Observable.fromCallable(() -> divide(2, 0))
                .onErrorResumeNext(ErrorHandling::fallbackData)
                .blockingSubscribe(System.out::println, t -> System.out.println(String.format("onError called: %s: %s", t.getClass().getSimpleName(), t.getMessage())));
    }

    private static int divide(int op1, int op2) throws Exception {
        if (op1 < 0 || op2 < 0) {
            throw new Exception("parameters must be positive integers");
        }
        return op1 / op2;
    }

    private static Observable<Integer> fallbackData(Throwable t) {
        return Observable.just(4);
    }
}
