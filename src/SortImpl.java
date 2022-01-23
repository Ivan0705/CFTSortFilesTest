import Interfaces.DequeSubscriber;
import Interfaces.Sort;

import javax.xml.ws.WebServiceException;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingDeque;


import static java.lang.System.out;
import static java.util.stream.Collectors.toList;

public class SortImpl implements Sort, DequeSubscriber {
    private PrintWriter outFiles;
    private List<BlockingDeque<String>> dequeList;

    SortImpl(String outputFileName) {
        dequeList = new ArrayList<>();
        try {
            outFiles = new PrintWriter(outputFileName, "utf-8");
        } catch (FileNotFoundException ignored) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private boolean failedSortOrder(BlockingDeque<String> deque) {
        if (deque.peekFirst() == null || deque.peekLast() == null) return false;
        if (Main.config.optionS) {
            if (Main.config.optionA) {
                return (deque.peekFirst()).compareTo(deque.peekLast()) > 0;
            } else {
                return (deque.peekFirst()).compareTo(deque.peekLast()) < 0;
            }
        } else {
            if (Main.config.optionA) {
                return stringToInteger(deque.peekFirst()) > stringToInteger(deque.peekLast());
            } else {
                return stringToInteger(deque.peekFirst()) < stringToInteger(deque.peekLast());
            }
        }
    }

    private BlockingDeque getActualDeque(List<BlockingDeque<String>> deques)
            throws IllegalArgumentException {
        if (deques.size() == 1) return deques.get(0);
        String[] arrayString = new String[deques.size()];
        BlockingDeque[] arrayDeque = new BlockingDeque[deques.size()];
        int i = 0;
        for (BlockingDeque<String> deque : deques) {
            if (deque.peekFirst() == null) continue;
            arrayString[i] = deque.peekFirst();
            arrayDeque[i] = deque;
            i++;
        }

        if (Arrays.stream(arrayString).anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Не прошла проверка на NULL! ");

        if (Main.config.optionS) {
            if (Main.config.optionA) {
                return arrayDeque[findIndexForStringsMinValue(arrayString)];//массив строк минимального значения
            } else {
                return arrayDeque[findIndexForStringsMaxValue(arrayString)];//массив строк максимального значения
            }
        } else {
            if (Main.config.optionA) {
                return arrayDeque[findIndexForIntegersMinValue(arrayString)];//массив чисел минимального значения
            } else {
                return arrayDeque[findIndexForIntegersMaxValue(arrayString)];//массив чисел максимального значения
            }
        }
    }

    private int findIndexForStringsMaxValue(String[] strings) {
        if (strings.length == 1) return 0;
        Optional<String> max = Arrays.stream(strings).max(Comparator.comparing(String::toString));
        return Arrays.stream(strings).collect(toList()).indexOf(max.get());
    }

    private int findIndexForStringsMinValue(String[] strings) {
        if (strings.length == 1) return 0;
        Optional<String> min = Arrays.stream(strings).min(Comparator.comparing(String::toString));
        return Arrays.stream(strings).collect(toList()).indexOf(min.get());
    }

    private int findIndexForIntegersMinValue(String[] numbers) throws NumberFormatException {
        if (numbers.length == 1) return 0;
        Optional<Integer> minNumber = Arrays.stream(numbers).map(this::stringToInteger).min(Comparator.comparingInt(Integer::intValue));
        Optional<String> min = Optional.of(minNumber.get().toString());
        return Arrays.stream(numbers).collect(toList()).indexOf(min.get());
    }

    private int findIndexForIntegersMaxValue(String[] numbers) throws NumberFormatException {
        if (numbers.length == 1) return 0;
        Optional<Integer> maxNumber = Arrays.stream(numbers).map(this::stringToInteger).max(Comparator.comparingInt(Integer::intValue));
        Optional<String> max = Optional.of(maxNumber.get().toString());
        return Arrays.stream(numbers).collect(toList()).indexOf(max.get());
    }

    private void cleanDeques(List<BlockingDeque<String>> deques) {
        for (BlockingDeque<String> el : deques) {
            if (dequeList.contains(el) && el.size() == 0) {
                deques.remove(el);
                deques.remove(el);
            }
        }
    }

    private Integer stringToInteger(String string) throws NumberFormatException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Невозможно переобразовать из строки в число по причине: " + e.getMessage());
        }
    }


    @Override
    public void notifyPoints(BlockingDeque<String> deque) {
        dequeList.add(deque);
    }


    @Override
    public void doSort(List<BlockingDeque<String>> deques) throws InterruptedException, IOException {
        while (true) {
            cleanDeques(deques);
            if (deques.size() == 0) {
                break;
            }

            BlockingDeque<String> actualDeque = null;
            try {
                actualDeque = getActualDeque(deques);
                if (failedSortOrder(actualDeque)) {
                    deques.remove(actualDeque);
                    out.println("Нарушен порядк сортировки в одном из входных файлов");
                    continue;
                }
            } catch (NumberFormatException e) {
                deques.remove(actualDeque);
                continue;
            } catch (IllegalArgumentException e) {
                continue;
            }

            if (actualDeque.size() > 0) {
                outFiles.write(actualDeque.takeFirst() + "\n");
            }
        }
    }

    @Override
    public void close() throws WebServiceException {
        if (outFiles != null)
            outFiles.close();
    }
}