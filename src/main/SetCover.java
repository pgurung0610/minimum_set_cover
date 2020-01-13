package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *	<p>This class defines the functionality for solving the minimum set cover problem. In this case, the input is some
 *	integer (<code>numToCover</code>) and a set of subsets of integers between one and <code>numToCover</code>. The
 *	output is the smallest set of subsets that contains every integer between 1 and <code>numToCover</code>.</p>
 *
 *	<p>Finding a minimum set cover has been proven to be an NP-Complete problem; therefore, this class is an
 *	implementation of a backtracking algorithm which takes into account every single combination of the subsets by
 *	essentially performing a depth first search on the decision tree of inclusion of subsets. Using aggressive pruning
 *	techniques to disregard certain combinations of subsets, however, the practical running time of this algorithm is
 *	significantly improved (despite the still inefficient asymptotic running time).</p>
 *
 *  @author Pratik Gurung
 */
public class SetCover {

	/**
	 *  The number of elements to cover (the maximum integer to cover)
	 */
	public static int numToCover = 0;

	/**
	 *  The boolean at each index represents whether corresponding integer is covered in the current solution
	 */
	public static boolean[] intsCovered;
	/**
	 *  The number of true values in the intsCovered array (the number of integers covered in the current solution)
	 */
	public static int numOfIntsCovered = 0;

	/**
	 *  An array of subsets that are essential to any set cover of the given data (determined before calling backtrack())
	 */
	public static boolean[] necessarySubsets;

	/**
	 *  The number of subsets in the current solution
	 */
	public static int numOfTrues;
	/**
	 *  The number of subsets in the current optimal solution
	 */
	public static int opt;

	/**
	 *  The boolean represents whether the corresponding subset is included in the optimal solution
	 */
	public static boolean[] finalSolution;

	/**
	 *  The number of times that the backtrack() method was called
	 */
	public static int numberOfBacktrackCalls = 0;

	/**
	 *	Executes depth first traversal of the tree of all possible combinations of given subsets to determine the
	 *	optimal set of subsets which covers all of the integers from 1 to <code>numToCover</code>
	 *
	 *	@param solutionVector	The boolean value at each index represents whether the corresponding subset is included
	 *							in the current solution
	 *	@param k	The index that corresponds to the current subset being determined for inclusion in the solution
	 *	@param input	The set of subsets
	 */
	public static void backtrack(boolean[] solutionVector, int k, List<List<Integer>> input) {
		numberOfBacktrackCalls++;

		/*
			The candidates represent whether or not the subset at the current iteration of the algorithm is included in
			the current solution thus there can only be two candidates: true or false
		 */
		boolean[] candidates = new boolean[2];
		int i;

		/*
			Keeping track of the integers that are covered, the number of integers that are covered, and the number of
			subsets used in the current solution before considering the next subset
		 */
		boolean[] currentIntsCovered = Arrays.copyOf(intsCovered, intsCovered.length);
		int currentNumOfIntsCovered = numOfIntsCovered;
		int currentNumOfTrues = numOfTrues;
		
		if(isASolution(solutionVector, k, input)) {
			processSolution(solutionVector, k, input);
		} else {
			k = k + 1;
			if(k < input.size()) {
				candidates = constructCandidates(solutionVector, k, input, candidates);
				for(i = 0; i < candidates.length; i++) {
					solutionVector[k] = candidates[i];
					makeMove(solutionVector, k, input);
					backtrack(solutionVector, k, input);
					unmakeMove(solutionVector, k, input, currentIntsCovered, currentNumOfIntsCovered, currentNumOfTrues);
				}
			}
		}
	}

	/**
	 *	Determines whether the solutionVector as it currently is constitutes a valid solution by checking if the number
	 *	of integers covered by the current solution is equal to the number of integers that need to be covered
     *
	 *	@param solutionVector	The boolean value at each index represents whether the corresponding subset is included
	 *							in the current solution
	 *	@param k	The index that corresponds to the current subset being determined for inclusion in the solution
	 *	@param input	The set of subsets
     *
	 *	@return <code>true</code> if the <code>solutionVector</code> forms a valid solution, otherwise <code>false</code>
	 */
	public static boolean isASolution(boolean[] solutionVector, int k, List<List<Integer>> input) {
		return (numOfIntsCovered == numToCover);
	}

	/**
	 *	Determines the valid boolean candidates for inclusion of the subset at question
	 *
	 *	@param solutionVector	The boolean value at each index represents whether the corresponding subset is included
	 *							in the current solution
	 *	@param k	The index that corresponds to the current subset being determined for inclusion in the solution
	 *	@param input	The set of subsets
	 * @param candidates	The array of possible boolean values representing whether the subset at index <code>k</code>
	 *                      is or isn't included in the solution
	 *
	 *	@return An array of size one with only the value <code>true</code> if the subset is necessary in any set cover,
	 *			an array of size one with only the value <code>false</code> if the subset is not needed given the
	 *			current <code>solutionVector</code>, or an array of size two with the values <code>true</code> and
	 *			<code>false</code> if the subset might be necessary given the current <code>solutionVector</code>
	 */
	public static boolean[] constructCandidates(boolean[] solutionVector, int k, List<List<Integer>> input, boolean[] candidates) {
		if(numOfTrues >= opt) {
			return new boolean[0];
		}
		
		if(necessarySubsets[k]) {
			candidates = new boolean[1];
			candidates[0] = true;
			return candidates;
		}
		
		boolean interesting = false;
		List<Integer> list = input.get(k);

		//Determining whether the subset contains an integer that has not yet been covered in the current solution
		for(Integer i : list) {
			if(!intsCovered[i]) {
				interesting = true;
				break;
			}
		}
		
		if(interesting) {
			candidates[0] = true;
			candidates[1] = false;
		} else {
			candidates = new boolean[1];
			candidates[0] = false;
		}
		
		return candidates;
	}

	/**
	 * Sets the optimal solution equal to the current solution
	 *
	 *	@param solutionVector	The boolean value at each index represents whether the corresponding subset is included
	 *							in the current solution
	 *	@param k	The index that corresponds to the current subset being determined for inclusion in the solution
	 *	@param input	The set of subsets
	 */
	public static void processSolution(boolean[] solutionVector, int k, List<List<Integer>> input) {
		finalSolution = Arrays.copyOf(solutionVector, solutionVector.length);
		opt = numOfTrues;
	}

	/**
	 *	Updates which integers are covered, the number of integers covered, and the number of subsets used in the
	 *	current solution
	 *
	 *  @param solutionVector	The boolean value at each index represents whether the corresponding subset is included
	 *							in the current solution
	 *	@param k	The index that corresponds to the current subset being determined for inclusion in the solution
	 *	@param input	The set of subsets
	 */
	public static void makeMove(boolean[] solutionVector, int k, List<List<Integer>> input) {
		if(solutionVector[k]) {
			for(Integer i : input.get(k)) {
				if(!intsCovered[i]) {
					intsCovered[i] = true;
					numOfIntsCovered++;
				}
			}
			numOfTrues++;
		}
	}

	/**
	 *  Resets the integers covered, the number of integers covered, and the number of subsets in the solution prior
	 *  to adding the subset at index <code>k</code> (if the subset at index <code>k</code> was indeed added)
	 *
	 *  @param solutionVector	The boolean value at each index represents whether the corresponding subset is included
	 *							in the current solution
	 *	@param k	The index that corresponds to the current subset being determined for inclusion in the solution
	 *	@param input	The set of subsets
	 *  @param currentIntsCovered	The set of integers that were covered by the solution prior to considering the
	 *                              subset at index <code>k</code>
	 *  @param currentNumOfIntsCovered	The number of integers covered by the solution prior to considering the subset
	 *                                  at index <code>k</code>
	 *  @param currentNumOfTrues	The number of subsets in the solution prior to considering the subset at index
	 *                              <code>k</code>
	 */
	public static void unmakeMove(boolean[] solutionVector, int k, List<List<Integer>> input, boolean[] currentIntsCovered, int currentNumOfIntsCovered, int currentNumOfTrues) {
		if(solutionVector[k]) {
			intsCovered = Arrays.copyOf(currentIntsCovered, currentIntsCovered.length);
			numOfIntsCovered = currentNumOfIntsCovered;
			numOfTrues = currentNumOfTrues;
		}
	}

	/**
	 *  Runs the program
	 */
	public static void runProgram() {
		List<List<Integer>> input = generateInput();;

		//Used to display how long the algorithm took to execute
		long startTime = System.currentTimeMillis();
		long endTime;

		removeUnnecessarySubsets(input);
		initializeNecessarySubsets(input);

		//Initializing class variables
		opt = input.size() + 1;
		numOfTrues = 0;
		intsCovered = new boolean[numToCover + 1];
		Arrays.fill(intsCovered, false);
		boolean[] solutionVector = new boolean[input.size()];
		Arrays.fill(solutionVector, false);

		//Actual execution of the backtracking algorithm
		backtrack(solutionVector, -1, input);

		//Printing the solution
		System.out.println("\nMinimum Number of Subsets: " + opt);
		System.out.print("Minimum Set Cover: ");
		for(int i = 0; i < input.size(); i++) {
			if(finalSolution[i]) {
				System.out.print("[");
				for(int j = 0; j < input.get(i).size(); j++) {
					System.out.print(input.get(i).get(j));
					if(!(j == input.get(i).size() - 1)) {
						System.out.print(", ");
					}
				}
				System.out.print("] ");
			}
		}

		//Printing information about running time
		endTime = System.currentTimeMillis();
		System.out.println("\n\nNumber of Seconds Elapsed: " + ((endTime - startTime) / 1000));
		System.out.println("Number of Backtrack Calls: " + numberOfBacktrackCalls  + "\n");
	}

	/**
	 * 	Generates a random set of subsets that contains a viable set cover using a user-specified maximum integer and
	 * 	a user-specified number of subsets
	 *
	 *  @return    A random set of subsets
	 */
	public static List<List<Integer>> generateInput() {
		try {
			System.out.print("Choose the number of integers to be covered (must be greater than 0): ");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			numToCover = Integer.valueOf(reader.readLine());
			System.out.print("Choose the number of subsets to be generated (must be greater than 0): ");
			int numOfSubsets = Integer.valueOf(reader.readLine());

			//Limiting the maximum size of a subset to prevent constantly getting very small and fast solutions
			int maxSubsetSize;
			if(numOfSubsets >= numToCover) {
				maxSubsetSize = Math.min(numToCover, ((numOfSubsets / numToCover) + 1) * ((numOfSubsets / numToCover) + 1));
			} else {
				maxSubsetSize = Math.min(numToCover, ((numToCover / numOfSubsets) + 1) * 2);
			}

		/*
			The boolean value at index i represents whether or not integer i+1 is included in a subset. This array
			is used to ensure that a set cover exists
		*/
			boolean[] intsCovered = new boolean[numToCover];

			System.out.println("\nGenerating a random set of subsets...\n");
			List<List<Integer>> input = generateRandomSubsets(numToCover, numOfSubsets, maxSubsetSize, intsCovered);

			//If the set of subsets does not form a set cover then add the unaccounted-for integers to random subsets
			for(int i = 0; i < intsCovered.length; i++) {
				if(!intsCovered[i]) {
					int index = (int) (Math.random() * ((double) (numOfSubsets)));
					input.get(index).add(i + 1);
				}
			}

			for(int i = 0; i < input.size(); i++) {
				for(int j = 0; j < input.get(i).size(); j++) {
					System.out.print(input.get(i).get(j) + " ");
				}
				System.out.println();
			}

			System.out.println("\nRandom set of subsets generated!" +
					"\nFinding a minimum set cover...");

			return input;
		} catch(Exception e) {
			System.out.println("\nYour input must be a positive integer\n");
			return generateInput();
		}
	}

	/**
	 *  Generates a random set of subsets of integers between one and <code>numToCover</code>
	 *
	 * @param numToCover    The number of integers to be covered (the maximum integer)
	 * @param numOfSubsets    The number of subsets to be generated
	 * @param maxSubsetSize    The maximum size of a subset (created to prevent getting very small and fast solutions)
	 * @param intsCovered    The boolean value at index i represents whether or not integer i+1 is included in a subset
	 *
	 * @return    A random set of subsets
	 */
	public static List<List<Integer>> generateRandomSubsets(int numToCover, int numOfSubsets, int maxSubsetSize, boolean[] intsCovered) {
		List<List<Integer>> input = new ArrayList<List<Integer>>();
		for (int i = 0; i < numOfSubsets; i++) {
			List<Integer> subset = new ArrayList<Integer>();
			/*
				The size of the subset must be less than or equal to the maximum integer because a set does not contain
				any duplicates
			 */
			int size = (int) (Math.random() * ((double) (maxSubsetSize)));

			/*
				The boolean value at index i represents whether integer i+1 is included in the subset or not. This array
				is used to ensure that there are no duplicates in any subsets
			 */
			boolean[] isInSubset = new boolean[numToCover];
			for (int j = 0; j < size; j++) {
				int value = (int) (Math.random() * ((double) (numToCover + 1)));
				while ((value < 1) || (value > numToCover) || (isInSubset[value - 1])) {
					value = (int) (Math.random() * ((double) (numToCover + 1)));
				}
				isInSubset[value - 1] = true;
				subset.add(value);

				if (!intsCovered[value - 1]) {
					intsCovered[value - 1] = true;
				}
			}

			input.add(subset);
		}

		return input;
	}

	/**
	 *  Preliminary Pruning: This method removes the subsets that are completely encompassed by another subset from
	 *  <code>input</code> because they are never needed in a minimum set cover
	 *
	 *  @param input    The set of subsets
	 */
	public static void removeUnnecessarySubsets(List<List<Integer>> input) {
		for(int i = 0; i < input.size(); i++) {
			List<Integer> list1 = input.get(i);
			boolean interesting = true;

			for(int j = 0; j < input.size(); j++) {
				if(j != i) {
					List<Integer> list2 = input.get(j);

					if(list2.size() >= list1.size() && list2.containsAll(list1)) {
						interesting = false;
						break;
					}
				}
			}

			if(!interesting) {
				input.remove(i);
				i--;
			}
		}
	}

	/**
	 *  Preliminary Pruning: Determines whether a subset is needed in any set cover based on whether or not it contains
	 *  an integer that is not contained in any other subset
	 *
	 *  @param input    The set of subsets
	 */
	public static void initializeNecessarySubsets(List<List<Integer>> input) {
		trackFewestOccurrences(input);

		//Preliminary Pruning: sorting the subsets according to the smallest occurrence of an integer in each subset
		input.sort((l1, l2) -> l1.get(l1.size() - 1) - l2.get(l2.size() - 1));

		necessarySubsets = new boolean[input.size()];
		Arrays.fill(necessarySubsets, false);
		for(int i = 0; i < input.size(); i++) {
			List<Integer> list = input.get(i);
			if(list.get(list.size() - 1) == 0) {
				necessarySubsets[i] = true;
			}

			/*
				Removing the last integer of each subset because it was only a tracker of the smallest occurrence of an
				integer in that subset and not an actual value in the subset
			 */
			list.remove(list.size() - 1);
		}
	}

	/**
	 *  Determining the fewest occurrence of an integer in each subset and keeping track of that value at the end of
	 *  each subset
	 *
	 *  @param input    The set of subsets
	 */
	public static void trackFewestOccurrences(List<List<Integer>> input) {
		for(int i = 0; i < input.size(); i++) {
			List<Integer> list1 = input.get(i);
			int minCount = input.size();

			for(int j = 0; j < list1.size(); j++) {
				int toFind = list1.get(j);
				int count = 0;

				for(int k = 0; k < input.size(); k++) {
					if(k != i) {
						List<Integer> list2 = input.get(k);

						if(list2.contains(toFind)) {
							count++;
						}
					}
				}

				if(count < minCount) {
					minCount = count;
				}
			}

			list1.add(minCount);
		}
	}

	/**
	 *	Where the application is actually run
	 *
	 *  @param args    Arguments
	 */
	public static void main(String[] args) {
		runProgram();
	}
}
