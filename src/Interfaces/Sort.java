package Interfaces;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public interface Sort extends Closeable {
    void doSort(List<BlockingDeque<String>> deque) throws InterruptedException, IOException;
}
