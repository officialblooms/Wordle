// IDEA: add a hard mode, figure out what makes a word valid(?), wordle bot

// Wordle program is a game played on console, where the user tries to guess a word that is
// selected at random through every game. After the user word is inputted, the program will tell the
// user if any of the letters are contained in the solution word, and if it is in the right position.
// The user can do as many games as they want, and once they exit, a leaderboard will display their
// best games based on the number of words they inputted.

import java.util.*;

public class Wordle {

    private Map<Integer, ArrayList<String>> wordList; // list of legal words sorted by word length
    private Map<Integer, ArrayList<String>> leaderboard; // leaderboard of best games by least amount of guesses
    public static final int MIN_LENGTH = 3; // minimum word solution length
    public static final int MAX_LENGTH = 6; // maximum word solution length

    /*
     * pre: words file has each word in a new line
     * post: stores given word bank into the program for later reference, as well as
     * initializing leaderboard
     * 
     * @param words - file of valid words
     */
    public Wordle(Scanner words) {
        wordList = new HashMap<>();
        for (int len = MIN_LENGTH; len <= MAX_LENGTH; len++) {
            wordList.put(len, new ArrayList<>());
        }

        while (words.hasNextLine()) {
            String word = words.nextLine();
            wordList.get(word.length()).add(word);
        }

        leaderboard = new TreeMap<>();
    }

    /*
     * post: checks user-inputted word for any letters that are contained in the
     * solution word, and if it is in correct position.
     * 
     * @param inputWord - user-inputted word
     * 
     * @param solutionWord - word solution that will be compared with user word
     */
    public String checkWord(String inputWord, String solutionWord) {

        String[] inputLetters = inputWord.split("");
        String tempSolution = solutionWord; // this will be modified later so its best to make a copy
        String ret = "";

        for (int i = 0; i < inputLetters.length; i++) { // checks if letter is in correct position
            if (inputLetters[i].equals(Character.toString(tempSolution.charAt(i)))) {
                ret += inputLetters[i];
                // letter is in correct position, so set to "-" to not be compared again
                inputLetters[i] = "-";
                tempSolution = replaceAt(tempSolution, i, "-");
            } else {
                ret += "_";
            }
        }

        // checks if letter is in word (and isn't in correct position already)
        for (int i = 0; i < inputLetters.length; i++) {
            int inputLetterPos = tempSolution.indexOf(inputLetters[i]);
            // if letter is in right position or is not found in solution word, do nothing
            if (!inputLetters[i].equals("-") && inputLetterPos != -1) {
                ret = replaceAt(ret, i, "*");
                // temporarily removes letter so if same letter is found in different position,
                // should not be accounted for if that letter is not found anywhere else
                // in solution word besides this one
                tempSolution = replaceAt(tempSolution, inputLetterPos, " ");
            }
        }
        return ret;
    }

    /*
     * post: sets up program by prompting the user to specify solution word length
     * to guess
     * 
     * @param input - Scanner for user input
     */
    public void startWordle(Scanner input) {
        System.out.println(
                "Welcome to Wordle! Your objective is to guess the mystery word in the\n" +
                        "least amount of guesses. Play as many games as you'd like! At the end,\n" +
                        "a leaderboard will display your best games by the number of guesses you used\n" +
                        "to get the mystery word! (UNLIMITED GUESSES)\n");

        while (true) {
            System.out.print("How long would you like the word solution to be? Choose between " + MIN_LENGTH + " and "
                    + MAX_LENGTH + ": ");
            String length = input.nextLine();
            try {
                if (isValidLength(Integer.parseInt(length))) {
                    try {
                        Thread.sleep(1500); // wait abit before printing output
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("After inputting each guess, you will see symbols on each letter position\n" +
                            "of your word that says how close your guess is to the solution:\n");
                    startWordle(input, 1, Integer.parseInt(length));
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please type in a number.");
            }
        }
    }

    /*
     * helper method for startWordle to track game number for leaderboard tracking
     * 
     * @param input - Scanner for user input
     * 
     * @param gameNum - game number user is currently on; default is 1
     */
    private void startWordle(Scanner input, int gameNum, int wordLength) {
        int attempts = 0;
        int randIndex = (int) (Math.random() * wordList.get(wordLength).size());
        String solutionWord = wordList.get(wordLength).get(randIndex);
        // System.out.println(solutionWord);

        printGuide();

        while (true) {
            System.out.print("Type your guess: ");
            String inputWord = input.next().toLowerCase().trim();

            attempts++;
            // tests invalid guesses
            if (inputWord.length() != wordLength || inputWord.matches("[^a-zA-Z]+")) {
                System.out.println("Please type in a valid word.");
                attempts--; // removes an attempt for invalid guesses
            } else {
                System.out.println("\n" + checkWord(inputWord, solutionWord) + "\n");
                if (inputWord.equals(solutionWord)) {
                    break;
                }
            }
        }

        System.out.println(
                "Congraulations! You got the word in " + attempts + " attempt" + (attempts > 1 ? "s" : "") + "!");
        if (!leaderboard.containsKey(attempts)) { // if no other game with same num of attempts exist
            leaderboard.put(attempts, new ArrayList<>()); // create a new array of game number strings
        }
        // add game number to amount of attempts + word length selection
        leaderboard.get(attempts).add("Game #" + gameNum + " w/ length " + wordLength);
        System.out.print("Play another round? (y/n): ");
        String newGame = input.next().toLowerCase();
        if (newGame.equals("y")) {
            while (true) {
                System.out.print("How long would you like the word solution to be? Choose between " + MIN_LENGTH
                        + " and " + MAX_LENGTH + ": ");
                int length = input.nextInt();
                if (isValidLength(length)) {
                    startWordle(input, gameNum + 1, length);
                    break;
                }
            }
        } else { // print out leaderboard
            for (int attemptNum : leaderboard.keySet()) {
                System.out.println(
                        leaderboard.get(attemptNum) + ": " + attemptNum + " attempt" + (attemptNum > 1 ? "s" : ""));
            }
        }
    }

    /*
     * post: checks if given number is within bounds of potential length of word
     * solution
     * 
     * @param num - number the user inputted for word solution length
     */
    private boolean isValidLength(int num) {
        boolean inRange = num >= MIN_LENGTH && num <= MAX_LENGTH;
        if (!inRange) {
            System.out.println("Please type a number between " + MIN_LENGTH + " and " + MAX_LENGTH);
        }
        return inRange;
    }

    /*
     * pre: index is within bounds of given String (throw IndexOutOfBoundsException
     * otherwise)
     * post: replaces the given string with the given character at the given index
     * and
     * returns the result. this method is similar to the String library's replace()
     * function except that it does not replace every occurence of the given input.
     * rather, it only replaces at the provided index and returns a string.
     * 
     * @param toReplace - String to have its contents modified
     * 
     * @param index - character position to be replaced
     * 
     * @param character - character to replace at the given index
     */
    private String replaceAt(String toReplace, int index, String character) {
        if (index < 0 || index >= toReplace.length()) {
            throw new IndexOutOfBoundsException();
        }
        toReplace = toReplace.substring(0, index) + character + toReplace.substring(index + 1);
        return toReplace;
    }

    /*
     * post: prints out representation of each symbol for the program
     */
    private void printGuide() {
        System.out.println("\"_\" means that the letter is not found in the solution.\n" +
                "\"*\" means that the letter is found in the solution but in a different position.\n" +
                "If the letter shows up in the result, it is in the correct position.\n" +
                "NOTE: letter with a symbol \"*\" does not necessarily mean that it does not appear\n" +
                "appear somewhere else in the word (i.e. it can appear more than once). Good luck!\n");
    }
}
