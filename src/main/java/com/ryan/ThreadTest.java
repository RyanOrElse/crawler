package com.ryan;

public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new ThreadDemo());
        Thread thread1 = new Thread(new ThreadDe());
        thread.start();
        thread1.start();
        thread.join();
        thread1.join();
        System.out.println("1");

    }
}

class ThreadDemo implements Runnable{
    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("demo"+i);
        }
    }
}

class ThreadDe implements Runnable{
    @Override
    public void run() {
        for(int i = 0; i < 100; i++){
            System.out.println("de"+i);
        }
    }
}
