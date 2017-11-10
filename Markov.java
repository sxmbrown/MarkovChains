/**
 * Samuel Brown
 * November 6, 2017
 * UNM CS251 Lab 7 - Markov Text Generation
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Markov {
    /**
     * member variables that use regular expressions to parse up words/chars.
     */
    private static final String WORD_REGEX = "(?<=\\b\\s)";
    private static final String CHAR_REGEX = "(?<=.)";
    private static final Random rand = new Random();
    /**
     * USAGE:
     * args[0] order of the markov chain
     * args[1] how many words of text to produce.
     * args[2] markov chain based on words/char. CASE IS IRRELEVANT.
     * args[3...n] text files to produce markov text from.
     * @param args
     */
    public static void main(String[] args) {
        int order = Integer.parseInt(args[0]);
        int count = Integer.parseInt(args[1]);
        String regex = WORD_REGEX;
        if (args[2].equalsIgnoreCase("char")) {
            regex = CHAR_REGEX;
        }

        StringChain stringChain = new StringChain(order);

        Scanner strScan = null;
        for (int i = 3; i < args.length; i++) {
            try {
                strScan = new Scanner(new FileReader(args[i]));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
            strScan.useDelimiter(regex);
            stringChain.addItems(strScan);
        }

        List<String> strList = stringChain.generate(count, rand);

        for (String str : strList) {
            System.out.print(str);
        }
        System.out.println();
    }
}
