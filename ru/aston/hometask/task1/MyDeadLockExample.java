package aston.hometask.task1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyDeadLockExample {
    private static int counter = 0;
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                lock1.lock();
                try {
                    Thread.sleep(10);
                    lock2.lock();
                    counter++;
                    System.out.println("Поток 1: counter = " + counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock1.unlock();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                lock2.lock();
                try {
                    Thread.sleep(10);
                    lock1.lock();
                    counter++;
                    System.out.println("Поток 2: counter = " + counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock2.unlock();
                }
            }
        });

        thread1.start();
        thread2.start();

        Thread.sleep(1000);

        if (thread1.isAlive() || thread2.isAlive()) {
            System.out.println("DEADLOCK обнаружен!");
            System.out.println("Последнее значение counter: " + counter);
            System.out.println("Состояние Потока 1: " + thread1.getState());
            System.out.println("Состояние Потока 2: " + thread2.getState());
        } else {
            System.out.println("Потоки завершились успешно");
            System.out.println("Финальное значение counter: " + counter);
        }
    }
}