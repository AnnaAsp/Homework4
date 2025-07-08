package aston.hometask.task1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyLiveLockExample {
    private static int counter = 0;
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();
    private static volatile boolean active = true;

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(MyLiveLockExample::operation1);
        Thread thread2 = new Thread(MyLiveLockExample::operation2);

        thread1.start();
        thread2.start();

        Thread.sleep(10000);

        if (active) {
            System.out.println("LIVELOCK обнаружен!");
            System.out.println("Финальное значение counter: " + counter);
            active = false;
        }
    }

    private static void operation1() {
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
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock1.unlock();
                }
            }
        }
        active = false;
    }

    private static void operation2() {
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