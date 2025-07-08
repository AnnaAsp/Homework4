package aston.hometask.task1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyDeadLockExample {
    private static volatile int counter = 0;
    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        MyDeadLockExample deadLock = new MyDeadLockExample();
        Thread thread1 = new Thread(() -> deadLock.operation(lock1, lock2, "Поток 1: "));
        Thread thread2 = new Thread(() -> deadLock.operation(lock2, lock1, "Поток 2: "));

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

    private void operation(Lock lock1, Lock lock2, String message) {
        for (int i = 0; i <= 100; i++) {
            lock1.lock();
            try {
                Thread.sleep(10);
                lock2.lock();
                counter++;
                System.out.println(message + "counter = " + counter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock1.unlock();
            }
        }
    }
}