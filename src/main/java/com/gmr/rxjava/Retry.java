package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Example implementation of a retry and abort behaviors.
 */
public class Retry {

    public static void main(String[] args) {
        try {
            System.out.println(String.format("Weather: %s", requestWeather()));
        } catch (Exception e) {
            System.out.println(String.format("Got an error requesting weather: %s", e.getMessage()));
        }
    }

    private static String requestWeather() throws Exception {
        Response response = new Response();
        new Observable<String>() {
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                System.out.println(String.format("Requesting weather, thread: %s... ", Thread.currentThread().getName()));
                response.counter++;
                // simulate delays and errors
                switch (response.counter) {
                    case 1:
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            return;
                        }
                        System.out.println("got weather (delayed), notifying");
                        observer.onNext("30 degrees (delayed)");
                        observer.onComplete();
                        break;
                    case 2:
                        System.out.println("failed");
                        observer.onError(new IOException("Internal Server Error"));
                        break;
                    default:
                        System.out.println("got weather, notifying");
                        observer.onNext("30 degrees");
                        observer.onComplete();
                        break;
                }
            }
        }
        .subscribeOn(Schedulers.io()) // execute the request on a separate thread
        .timeout(1, TimeUnit.SECONDS) // automatically abort the task and leave it running until it dies
        .retry(1) // re-subscribe to execute again the request if onError was called
        .blockingSubscribe(r -> response.result = r, e -> response.error = e); // blockingSubscribe for getting the result on the calling thread
        if (response.error == null) {
            return response.result;
        } else {
            throw new Exception(response.error);
        }
    }

    private static class Response {
        String result;
        Throwable error;
        int counter;
    }
}
