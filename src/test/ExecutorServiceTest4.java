package test;

import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class ExecutorServiceTest4 {
    public static void main(String args[]) {
        ParallelExcutorService service = new ParallelExcutorService();
        service.submit("job1");
        service.submit("job2");
        service.submit("job3");
        service.submit("job4");

        for (int i = 0 ; i < 4; i++) {
            String result = service.take();
            System.out.println(result);
        }

        System.out.println("end");
        service.close();
    }

    private static class ParallelExcutorService {
        private final int maxCore = Runtime.getRuntime().availableProcessors();
        private final ExecutorService executor = Executors.newFixedThreadPool(maxCore);
        private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

        public ParallelExcutorService() {
        }

        public void submit(String job) {
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("finished " + job);
                String result = job + ", " + threadName;
                try {
                    queue.put(result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }


        public String take() {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }

        public void close() {
            List<Runnable> unfinishedTasks = executor.shutdownNow();
            if (!unfinishedTasks.isEmpty()) {
                System.out.println("Not all tasks finished before calling close: " + unfinishedTasks.size());
            }
        }
    }
}
