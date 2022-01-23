import Interfaces.DequePoint;
import Interfaces.Sort;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReadListFiles implements Closeable {
    //Чтение файлов
    private List<BlockingDeque<String>> deques = new CopyOnWriteArrayList<>();//Если использовать ArrayList, то будет ConcurrentModificationException
    private List<FileRead> fileReads = new ArrayList<>();
    private Sort sorter;
    private DequePoint dequePoint;

    ReadListFiles(Sort sorter, DequePoint dequePoint) {
        this.sorter = sorter;
        this.dequePoint = dequePoint;
    }

    void toRead(List<String> inputFiles) throws InterruptedException, IOException {

        for (String file : inputFiles) {
            FileRead worker = new FileRead(file, dequePoint);
            if (!worker.isFailed()) fileReads.add(worker);
        }
        if (fileReads.size() == 0) {
            throw new IllegalArgumentException("Нет доступных для обработки входных файлов.");
        } else {
            for (FileRead reader : fileReads) {
                BlockingDeque deque = reader.beginAsyncReading();
                if (deque != null) deques.add(deque);
            }
        }
        sorter.doSort(deques);
    }

    @Override
    public void close() {
        fileReads.forEach(FileRead::close);
    }
}
