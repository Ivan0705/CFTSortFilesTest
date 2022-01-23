import Interfaces.DequePoint;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class FileRead implements Runnable, Closeable {

    private String filename;
    private FileInputStream inputStream;
    private Scanner scanner;
    private BlockingDeque<String> queue;
    private ExecutorService service;
    private boolean failed;
    private DequePoint dequePoint;


    FileRead(String filename, DequePoint dequePoint) {
        this.filename = filename;
        this.dequePoint = dequePoint;

        try {
            inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            failed = true;
        }
        scanner = new Scanner(inputStream);
        queue = new LinkedBlockingDeque<>();
        service = Executors.newSingleThreadExecutor();
    }


    boolean isFailed() {
        return failed;
    }

    BlockingDeque beginAsyncReading() {
        if (!failed) {
            service.submit(this);
            return queue;
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        if (scanner != null) scanner.close();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
        dequePoint.notifyPoints(queue);
        service.shutdownNow();
    }

    @Override
    public void run() {
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null) continue;
                String value = line.replaceAll("\\s+", "");
                try {
                    queue.putLast(value);
                } catch (InterruptedException e) {
                    System.out.println("Прервано чтение файла");
                }
            }
        } catch (Exception e) {
            System.out.println("Не удалось прочитать файл " + filename);
        } finally {
            close();
        }
    }
}