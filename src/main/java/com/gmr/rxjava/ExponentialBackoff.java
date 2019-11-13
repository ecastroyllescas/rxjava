package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Example for how to implement a retry request with exponential backoff strategy. Useful for rate limiting APIs.
 */
public class ExponentialBackoff {

    public static void main(String[] args) {
        final int retries = 2;
        String result = executeRequest()
                .retryWhen(e -> Observable.range(1, retries)
                        .flatMap(retryCount -> Observable.timer((long) Math.pow(2.0, retryCount), TimeUnit.SECONDS)))
                .lastOrError()  // Comment this line to not receive exceptions when retry limit is reached, only an onComplete call will be executed on subscriber
                .blockingGet();
        System.out.println(result);
    }

    private static Observable<String> executeRequest() {
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        return Observable.create(emitter -> {
            System.out.println(String.format("Executing request. Thread: %s", Thread.currentThread().getName()));
            // Simulate exceptions executing the request up to a limit
            counter.set(counter.get() + 1);
            if (counter.get() < 20) {
                emitter.onError(new IOException("Rate limit reached"));
            }
            emitter.onNext("a response");
            emitter.onComplete();
        });
    }
}
