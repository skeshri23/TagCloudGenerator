import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Tag counts takes the file input and converts it into an alphabetical tag
 * cloud.
 *
 * @author Shristi Keshri and Divya Negi
 */
public final class TagCloudv3 {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private TagCloudv3() {
    }

    /**
     * Separators that could be used to separate words.
     */
    private static final String SEPARATORS = " \t\n\r\"()'!?{}-,.[]*";

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param pos
     *            the starting index
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
    * nextWordOrSeparator =
    * text[position, position + |nextWordOrSeparator|) and
    * if entries(text[position, position + 1)) intersection separators = {}
    * then
    * entries(nextWordOrSeparator) intersection separators = {} and
    * (position + |nextWordOrSeparator| = |text| or
    * entries(text[position, position + |nextWordOrSeparator| + 1))
    * intersection separators /= {})
    * else
    * entries(nextWordOrSeparator) is subset of separators and
    * (position + |nextWordOrSeparator| = |text| or
    * entries(text[position, position + |nextWordOrSeparator| + 1))
    * is not subset of separators)
    * </pre>
     */
    private static String nextWordOrSeparator(String text, int pos) {
        int c = 0;
        char retp = 'a'; //gets the first character in the text
        String ret = "";
        /*
         * the if-else block checks whether the charcter is a separator or not,
         * if it is - ret will be a string of seperators otherwise, ret will be
         * a word
         */
        if (SEPARATORS.indexOf(text.charAt(pos)) >= 0) {
            /*
             * while loop for concatenating the character and word or seperator
             * which is ret
             */
            while (c < text.substring(pos, text.length()).length()) {
                retp = text.charAt(pos + c);
                if (SEPARATORS.indexOf(text.charAt(pos + c)) >= 0) {
                    ret += retp;
                    c++;
                } else {
                    c = text.substring(pos, text.length()).length();
                }
            }
        } else {
            while (c < text.substring(pos, text.length()).length()) {
                retp = text.charAt(pos + c);
                if (!(SEPARATORS.indexOf(text.charAt(pos + c)) >= 0)) {
                    ret += retp;
                    c++;
                } else {
                    c = text.substring(pos, text.length()).length();
                }
            }
        }
        return ret;
    }

    /**
     * Comparator used to compare words.
     *
     */
    @SuppressWarnings("serial")
    private static class StringCompare implements Comparator<Pair> {
        /**
         * Compares two given strings and returns either a positive, negative or
         * zero integer, if the first argument is greater, less or equal to the
         * second argument.
         *
         * @param o1
         *            string in the input file
         * @param o2
         *            string in the input file
         * @return the integer resulting from the comparison
         */
        @Override
        public int compare(Pair o1, Pair o2) {
            int x = o1.key().compareTo(o2.key());
            return x;
        }
    }

    /**
     * Compares numbers using nested Comparator class.
     *
     */
    private static class NumberCompare implements Comparator<Pair> {
        /**
         * Compares two given integers and returns either a positive, negative
         * or zero integer, if the first argument is greater, less or equal to
         * the second argument.
         *
         * @param o1
         *            count of a number occurring in the input file
         * @param o2
         *            count of the second number occurring in the input file
         * @return the integer resulting from the comparison
         */
        @Override
        public int compare(Pair o1, Pair o2) {
            //compares the counts of the values and keys
            int x = o2.value().compareTo(o1.value());
            if (x == 0) {
                x = o1.key().compareTo(o2.key());
            }
            return x;
        }
    }

    /**
     * Creates the Pair class to act as the Map implentations with key and
     * value.
     *
     */
    private static class Pair {
        /**
         * The key.
         */
        private String key;
        /**
         * The value.
         */
        private Integer value;

        /**
         *
         * @param k
         *            the key
         * @param val
         *            the value
         */
        Pair(String k, Integer val) {
            this.key = k;
            this.value = val;
        }

        /**
         *
         * @return this.key
         */
        String key() {
            return this.key;
        }

        /**
         *
         * @return this.value
         */
        Integer value() {
            return this.value;
        }
    }

    /**
     * Builds the HTML file that stores the generated tag cloud, while also
     * sorting words in their respective font sizes.
     *
     *
     * @param outfile
     *            output stream
     * @param inputFile
     *            name of the file put in
     * @param wordmap
     *            words in alphabetic order
     * @param max
     *            largest number of words counted
     * @param min
     *            smallest number of words counted
     * @requires wordmap.size() != 0 && wordList.isInExtractionMode()
     * @ensures <pre>
     * The HTML page built is correctly formated, the words are in alphabetical order
     * and their font size is according to their respective counts
     * </pre>
     */

    private static void pageHTML(PrintWriter outfile, List<Pair> wordmap,
            int max, int min, String inputFile) {

        int count = wordmap.size();
        final double maxFont = 48.0;
        final double minFont = 11.0;
        //builds the HTMl page
        outfile.println("<html>");
        outfile.println("<head>");
        outfile.println(
                "<link href=http://web.cse.ohio-state.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css rel=stylesheet type=text/css>");
        outfile.println("<title>" + "Top " + count + " words in " + inputFile
                + "</title>");
        outfile.println(
                "<link href=tagcloud.css rel=stylesheet type=text/css>");
        outfile.println("</head>");
        outfile.println("<body>");
        outfile.println(
                "<h2> Top " + count + " words in " + inputFile + "</h2>");
        outfile.println("<hr>");
        outfile.println("<div class=cdiv>");
        outfile.println("<p class=cbox>");
        //adds according font sizes to words based on their counts (values)
        while (wordmap.size() > 0) {
            Pair val = wordmap.remove(0);
            //formula used to calculate font size
            double font = (maxFont * (val.value() - min) / (max - min))
                    + minFont;
            outfile.println("<span style=\"cursor:default\" class=\"f"
                    + (int) font + "\" title=\"count: " + val.value() + "\">"
                    + val.key() + "</span>");
        }
        outfile.println("</p>");
        outfile.println("</div>");
        outfile.println("</body></html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        //prompts for the console
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a file name to be read: ");
        String inname = input.nextLine();
        System.out.println("Enter a file to be printed to: ");
        String outname = input.nextLine();
        System.out.println("How many words do you want? ");
        int count = input.nextInt();

        input.close();

        //opens infile and catches an exception if cannot open
        BufferedReader infile;
        try {
            infile = new BufferedReader(new FileReader(inname));
        } catch (IOException e) {
            System.err.println("Error opening infile: " + inname);
            return;
        }

        //reads infile and catches an exception if cannot read
        //creates a map of all words
        Map<String, Integer> map = new HashMap<String, Integer>();
        try {
            String temp = infile.readLine();
            //this loop reads all the lines until empty
            while (temp != null) {
                int pos = 0;
                //creates tokens for each line
                while (temp.length() > pos) {
                    String s = nextWordOrSeparator(temp, pos);
                    pos += s.length();
                    if (SEPARATORS.indexOf(s.charAt(0)) < 0) {
                        s = s.toLowerCase();
                        /*
                         * if-else to check which words to add or remove from
                         * the map
                         */
                        if (!map.containsKey(s)) {
                            map.put(s, 1);
                        } else {
                            int x = map.get(s);
                            map.remove(s);
                            map.put(s, x + 1);
                        }
                    }
                }
                temp = infile.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error adding infile to map: " + inname);
        }

        //closes infile and catches an exception if cannot close
        try {
            infile.close();
        } catch (IOException e) {
            System.err.println("Error closing infile: " + inname);
        }

        //builds a list of the top 100 sorted words in the map
        List<Pair> numlist = new LinkedList<Pair>();
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        //adds all of the entries in the map
        for (Map.Entry<String, Integer> e : entries) {
            Pair p = new Pair(e.getKey(), e.getValue());
            numlist.add(p);
        }

        //sorts the numbers
        Comparator<Pair> compnum = new NumberCompare();
        Collections.sort(numlist, compnum);

        //builds a list of alphabetically sorted words
        List<Pair> wordlist = new LinkedList<Pair>();

        //Add top *count*
        //Should remove top count from numlist but we don't use it again
        int max = numlist.get(0).value();
        int min = numlist.get(count - 1).value();
        for (int i = 0; i < count; i++) {
            if (numlist.size() > 0) {
                Pair e = numlist.remove(0);
                wordlist.add(e);
            } else {
                System.out.println("Count exceeded number of words");
            }
        }

        //sorts the words
        Comparator<Pair> compword = new StringCompare();
        Collections.sort(wordlist, compword);

        //opens the outfile and catches an exception if error opening
        PrintWriter outfile;
        try {
            outfile = new PrintWriter(
                    new BufferedWriter(new FileWriter(outname)));
        } catch (IOException e) {
            System.err.println("Error opening outfile: " + outname);
            return;
        }

        //prints to the HTML file
        pageHTML(outfile, wordlist, max, min, inname);

        //closes the outfile
        outfile.close();
        try {
            infile.close();
        } catch (IOException e) {
            System.err.println("Error closing file");
        }
    }

}
