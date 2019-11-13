package com.gmr.rxjava;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Example for how to implement a simple event bus with RxJava.
 */
public class EventBus {

    public static void main(String[] args) {
        Bus bus = new Bus();

        // These components simulate different UI sections with their own lifecycle
        NavBarComponent navBar = new NavBarComponent(bus);
        UserProfileComponent usrProf = new UserProfileComponent(bus);
        SelectedLanguageComponent langComp = new SelectedLanguageComponent(bus);

        bus.post(new UsernameChangedEvent("John"));
        bus.post(new LanguageChangedEvent("french"));
        bus.post(new UsernameChangedEvent("Maria"));
        bus.post("random unrelated event");

        navBar.onDestroy();
        usrProf.onDestroy();
        langComp.onDestroy();

        bus.post(new UsernameChangedEvent("Peter"));
    }

    private static class Bus {
        private PublishSubject<Object> subject = PublishSubject.create();

        <T> Disposable register(Consumer<T> onNext, Class<T> eventType) {
            return subject
                    .filter(event -> event.getClass().equals(eventType))
                    .map(obj -> (T) obj)
                    .subscribe(onNext);
        }

        void post(Object event) {
            subject.onNext(event);
        }
    }

    private static class NavBarComponent {
        Disposable register;
        NavBarComponent(Bus bus) {
            register = bus.register(this::handleUsernameChanged, UsernameChangedEvent.class);
        }

        void handleUsernameChanged(UsernameChangedEvent event) {
            System.out.println(String.format("%-30s new username: %-6s thread: %s", this.getClass().getSimpleName(), event.newName, Thread.currentThread().getName()));
        }

        void onDestroy() {
            register.dispose();
        }
    }

    private static class UserProfileComponent {
        Disposable register;

        UserProfileComponent(Bus bus) {
            register = bus.register(this::handleUsernameChanged, UsernameChangedEvent.class);
        }

        void handleUsernameChanged(UsernameChangedEvent event) {
            System.out.println(String.format("%-30s new username: %-6s thread: %s", this.getClass().getSimpleName(), event.newName, Thread.currentThread().getName()));
        }

        void onDestroy() {
            register.dispose();
        }
    }

    private static class SelectedLanguageComponent {
        Disposable register;
        SelectedLanguageComponent(Bus bus) {
            register = bus.register(this::handleLanguageChanged, LanguageChangedEvent.class);
        }

        void handleLanguageChanged(LanguageChangedEvent event) {
            System.out.println(String.format("%-30s new language: %-6s thread: %s", this.getClass().getSimpleName(), event.newName, Thread.currentThread().getName()));
        }

        void onDestroy() {
            register.dispose();
        }
    }

    public static class UsernameChangedEvent {
        String newName;
        UsernameChangedEvent(String newName) {
            this.newName = newName;
        }
    }

    public static class LanguageChangedEvent {
        String newName;
        LanguageChangedEvent(String newName) {
            this.newName = newName;
        }
    }
}
