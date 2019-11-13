package com.gmr.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RxTest {

    @Test
    public void testSimpleStream() {
        squareObservable(Arrays.asList(1, 2, 3, 4))
                .test()
                .assertResult(1, 4, 9, 16)
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void testTime() {
        TestScheduler scheduler = new TestScheduler();
        RxJavaPlugins.setComputationSchedulerHandler(ignore -> scheduler);
        TestObserver<String> testObservable = timeObservable(Arrays.asList("one", "two", "three"))
                .test();
        testObservable.assertNoValues();

        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
        testObservable.assertValueAt(0, "Event: one, time: 0");

        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
        testObservable.assertValueAt(1, "Event: two, time: 1");

        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
        testObservable.assertValueAt(2, "Event: three, time: 2");

        testObservable.assertComplete();
    }

    private Observable<Integer> squareObservable(List<Integer> numbers) {
        return Observable.fromIterable(numbers)
                .map(n -> n * n);
    }

    private Observable<String> timeObservable(List<String> events) {
        return Observable.fromIterable(events)
                .zipWith(Observable.interval(1, TimeUnit.MINUTES), (e, t) -> String.format("Event: %s, time: %d", e, t));
    }
}
