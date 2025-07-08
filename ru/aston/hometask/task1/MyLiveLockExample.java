package aston.hometask.task1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyLiveLockExample {
    private static volatile int counter = 0;
    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();
    private static volatile boolean active = true;

    public static void main(String[] args) throws InterruptedException {
        MyLiveLockExample liveLock = new MyLiveLockExample();
        Thread thread1 = new Thread(() -> liveLock.operation(lock1, lock2, "Поток 1: ", "получил lock1, ", "не получил lock2, отпускаю lock1"));
        Thread thread2 = new Thread(() -> liveLock.operation(lock2, lock1, "Поток 2: ", "получил lock2, ", "не получил lock1, отпускаю lock2"));

        thread1.start();
        thread2.start();

        Thread.sleep(5000);

        if (active) {
            System.out.println("LIVELOCK обнаружен!");
            System.out.println("Финальное значение counter: " + counter);
            active = false;
        }
    }

    private void operation(Lock lock1, Lock lock2, String message1, String message2, String message3) {
        while (active && counter <= 10) {
            if (lock2.tryLock()) {
                try {
                    System.out.println(message1 + message2 + "counter = " + counter);
                    Thread.sleep(100);
                    if (lock1.tryLock()) {
                        counter++;
                        System.out.println(message1 + "counter = " + counter);
                    } else {
                        System.out.println(message1 + message3);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock2.unlock();
                }
            }
        }
        active = false;
    }
}