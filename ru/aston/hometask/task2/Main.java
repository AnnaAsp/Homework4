package aston.hometask.task2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    private static boolean isThread1Turn = true;

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> printNumber(1, true));
        Thread thread2 = new Thread(() -> printNumber(2, false));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printNumber(int number, boolean isMyTurn) {
        while (true) {
            lock.lock();
            try {
                while (isThread1Turn != isMyTurn) {
                    condition.await();
                }
                System.out.println(number);
                isThread1Turn = !isThread1Turn;
                condition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}