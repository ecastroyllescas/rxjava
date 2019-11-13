package com.gmr.rxjava;

import java.util.ArrayList;
import java.util.List;

/**
 * Example of a classic Observer design pattern implementation.
 */
public class ClassicObserver {

    public static void main(String[] args) {
        Observer observer = new DefaultObserver();
        DefaultObservable observable = new DefaultObservable();
        observable.addObserver(observer);
        observable.handleEvent("Hello world!");
    }

    private interface Observable {
        void addObserver(Observer observer);
        void removeObserver(Observer observer);
    }

    private interface Observer {
        void onEvent(Object anEvent);
    }

    private static class DefaultObservable implements Observable {
        private List<Observer> observers = new ArrayList<>();

        @Override
        public void addObserver(Observer observer) {
            observers.add(observer);
        }

        @Override
        public void removeObserver(Observer observer) {
            observers.remove(observer);
        }

        void handleEvent(Object event) {
            for (Observer observer : observers) {
                observer.onEvent(event);
            }
        }
    }

    private static class DefaultObserver implements Observer {
        @Override
        public void onEvent(Object anEvent) {
            System.out.println("Event received: " + anEvent);
        }
    }
}
