import Interfaces.DequePoint;
import Interfaces.DequeSubscriber;
import Interfaces.Sort;

import java.io.*;

public class Main {
    static Config config;

    public static void main(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("Не все аргументы перечислены!");
        }
        config = new Config(args);
        DequePoint dequePoint = new DequePointImpl();
        try (Sort sorter = new SortImpl(config.outputFile);
             ReadListFiles listFiles = new ReadListFiles(sorter, dequePoint)) {
            dequePoint.subscribe((DequeSubscriber) sorter);
            listFiles.toRead(config.inputFiles);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}


