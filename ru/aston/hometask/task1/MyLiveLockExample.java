package aston.hometask.task1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyLiveLockExample {
    private static int counter = 0;
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();
    private static volatile boolean active = true;

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            while (active && counter <= 100) {
                if (lock1.tryLock()) {
                    try {
                        System.out.println("Поток 1: получил lock1, counter = " + counter);
                        Thread.sleep(100);
                        if (lock2.tryLock()) {
                            counter++;
                            System.out.println("Поток 1: counter = " + counter);
                        } else {
                            System.out.println("Поток 1: не получил lock2, отпускаю lock1");
                            continue;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock1.unlock();
                    }
                }
            }
            active = false;
        });

        Thread thread2 = new Thread(() -> {
            while (active && counter <= 100) {
                if (lock2.tryLock()) {
                    try {
                        System.out.println("Поток 2: получил lock2, counter = " + counter);
                        Thread.sleep(100);
                        if (lock1.tryLock()) {
                            counter++;
                            System.out.println("Поток 2: counter = " + counter);
                        } else {
                            System.out.println("Поток 2: не получил lock1, отпускаю lock2");
                            continue;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock2.unlock();
                    }
                }
            }
            active = false;
        });

        thread1.start();
        thread2.start();

        Thread.sleep(5000);

        if (active) {
            System.out.println("LIVELOCK обнаружен!");
            System.out.println("Финальное значение counter: " + counter);
            active = false;
        }
    }
}