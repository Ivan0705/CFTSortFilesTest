
import java.util.ArrayList;
import java.util.List;

class Config {
    boolean optionS;
    boolean optionI;
    boolean optionA;
    boolean optionD;
    List<String> inputFiles = new ArrayList<>();
    String outputFile;

    public Config(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-")) {
                switch (arg) {
                    case "-s":
                        optionS = true;
                        break;
                    case "-i":
                        optionI = true;
                        break;
                    case "-a":
                        optionA = true;
                        break;
                    case "-d":
                        optionD = true;
                        break;
                }
            } else {
                if (outputFile == null) {
                    outputFile = arg;

                } else {
                    inputFiles.add(arg);
                }
            }
        }
    }


}