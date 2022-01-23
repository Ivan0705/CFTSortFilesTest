package Interfaces;

import java.util.concurrent.BlockingDeque;

public interface DequeSubscriber {
    void notifyPoints(BlockingDeque<String> deque);
}
