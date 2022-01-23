package Interfaces;

import java.util.concurrent.BlockingDeque;

public interface DequePoint {
    void notifyPoints(BlockingDeque<String> deque);

    void subscribe(DequeSubscriber dequeSubscriber);
}
