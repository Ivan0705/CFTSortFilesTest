import Interfaces.DequePoint;
import Interfaces.DequeSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class DequePointImpl implements DequePoint {
    private List<DequeSubscriber> listReceivers = new ArrayList<>();

    @Override
    public void notifyPoints(BlockingDeque<String> deque) {
        for (DequeSubscriber el : listReceivers) {
            el.notifyPoints(deque);
        }
    }

    @Override
    public void subscribe(DequeSubscriber dequesubscriber) {
        listReceivers.add(dequesubscriber);
    }
}
