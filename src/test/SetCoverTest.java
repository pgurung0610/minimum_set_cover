package test;

import main.SetCover;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *  This class reads in test files to form certain input with known outputs to ensure that the <code>main.SetCover</code>
 *  class works properly
 *
 *  @author Pratik Gurung
 */
public class SetCoverTest {

    /**
     *  Each key is the name of a test file and each value is the number of subsets in the minimum set cover of the set
     *  and set of subsets defined by the data from the corresponding test file
     */
    final Map<String, Integer> testValues = Map.ofEntries(
            Map.entry("s-x-12-6", 3),
            Map.entry("s-rg-8-10", 4),
            Map.entry("s-rg-31-15", 9),
            Map.entry("s-rg-40-20", 10),
            Map.entry("s-rg-63-25", 16),
            Map.entry("s-k-20-30", 6),
            Map.entry("s-k-20-35", 6),
            Map.entry("s-k-30-50", 9),
            Map.entry("s-k-30-55", 9),
            Map.entry("s-k-40-60", 14),
            Map.entry("s-k-35-65", 10),
            Map.entry("s-k-40-80", 9),
            Map.entry("s-rg-118-30", 20)
    );

    /**
     *  Iterates through <code>testValues</code> and tests whether the file defined by the key at each entry produces
     *  the number of subsets defined by the corresponding value when running the backtracking algorithm define in the
     *  <code>main.SetCover</code> class
     */
    @Test
    public void testBacktrack() {
        Iterator<Map.Entry<String, Integer>> iterator = testValues.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            assertEquals(runBacktrack(entry.getKey()), entry.getValue());
        }
    }

    /**
     *  Runs the backtracking algorithm on the data defined by the test file with the name <code>fileName</code>
     *
     *  @param fileName    The name of the test file from which to extract data
     *
     *  @return The number of subsets in the minimum set cover given by the backtracking algorithm when run on the data
     *          data extracted from the test file
     */
    public static Integer runBacktrack(String fileName) {
        List<List<Integer>> input = readTestFile(fileName);

        SetCover.removeUnnecessarySubsets(input);
        SetCover.initializeNecessarySubsets(input);

        //Initializing class variables
        SetCover.opt = input.size() + 1;
        SetCover.numOfTrues = 0;
        SetCover.intsCovered = new boolean[SetCover.numToCover + 1];
        Arrays.fill(SetCover.intsCovered, false);
        boolean[] solutionVector = new boolean[input.size()];
        Arrays.fill(solutionVector, false);

        //Actual execution of the backtracking algorithm
        SetCover.backtrack(solutionVector, -1, input);

        return SetCover.opt;
    }

    /**
     *  Reads data from a test file
     *
     *  @param fileName    The name of the test file to be read
     *
     *  @return    The set of subsets specified by the test file
     */
    public static List<List<Integer>> readTestFile(String fileName) {
        try {
            List<List<Integer>> input = new ArrayList<List<Integer>>();
            BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/" + fileName));

            SetCover.numToCover = Integer.valueOf(reader.readLine());
            int inputSize = Integer.valueOf(reader.readLine());

            for (int i = 0; i < inputSize; i++) {
                String[] arr = reader.readLine().trim().split(" ");
                List<Integer> list = new ArrayList<Integer>();
                for (String s : arr) {
                    if (!(s.length() == 0))
                        list.add(Integer.valueOf(s));
                }
                input.add(list);
            }

            return input;
        } catch (IOException e) {
            System.out.println("Invalid file name: " + fileName);
        }

        return null;
    }
}
