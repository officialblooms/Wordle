// IDEA: wordle bot (ok maybe not)

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

    // HARD MODE VARIABLES
    private Set<String> absentLetters; // list of user-inputted letters not present in the word
    private Set<String> mustGuess; // list of user-inputted letters that must show on next guess
    private String wordBuild; // used to check that user guesses uses hints from previous guesses (green-case)

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

        setupGame(input, 1);
    }

    /*
     * post: helper function for setting up the game mode and word solution length.
     * 
     * @param input - Scanner for user input
     * 
     * @param gameNum - current game number user is on for the program's runtime
     */
    private void setupGame(Scanner input, int gameNum) {
        System.out.print("How long would you like the word solution to be? Choose between " + MIN_LENGTH + " and "
                + MAX_LENGTH + ": ");
        String length = input.next();
        try {
            if (isValidLength(Integer.parseInt(length))) {
                boolean hardMode = false;
                System.out.print("Would you like to play in hard mode? This forces you to use hints from\n"
                        + "previous guesses into your current guess. (y/n): ");
                if (input.next().toLowerCase().equals("y")) {
                    hardMode = true;

                    // setup hard mode variables
                    wordBuild = new String(new char[Integer.parseInt(length)]).replace("\0", "-");
                    absentLetters = new TreeSet<>();
                    mustGuess = new TreeSet<>();

                    System.out.println("Good luck :)\n");
                }

                printGuide();
                startWordle(input, gameNum, Integer.parseInt(length), hardMode);
            } else {
                setupGame(input, gameNum);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please type in a number.");
            setupGame(input, gameNum);
        }
    }

    /*
     * helper method for startWordle to track game number for leaderboard tracking
     * 
     * @param input - Scanner for user input
     * 
     * @param gameNum - game number user is currently on; default is 1
     */
    private void startWordle(Scanner input, int gameNum, int wordLength, boolean hardMode) {
        int attempts = 0;
        int randIndex = (int) (Math.random() * wordList.get(wordLength).size());
        String solutionWord = wordList.get(wordLength).get(randIndex).toLowerCase();
        // String solutionWord = "piano";
        // System.out.println(solutionWord);

        System.out.println("Hard mode: " + (hardMode ? "ENABLED" : "DISABLED"));
        String progressWord = "";

        while (true) {
            System.out.print("Type your guess: ");
            String inputWord = input.next().toLowerCase().trim();

            attempts++;

            if (inputWord.length() != wordLength || inputWord.matches("[^a-zA-Z]+")) {
                System.out.println("Please type in a valid word.");
                attempts--; // removes an attempt for invalid guesses
            } else {
                String guessResult = (hardMode ? checkWordHard(inputWord, solutionWord)
                        : checkWord(inputWord, solutionWord));
                if (guessResult.length() <= wordLength) { // checks error message (useless if not on hard mode)
                    progressWord += guessResult + " (" + inputWord + ")\n";
                } else {
                    System.out.println(guessResult);
                }
                System.out.println("\n" + progressWord);
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
        leaderboard.get(attempts)
                .add("Game #" + gameNum + " (" + solutionWord + ")" + (hardMode ? " - HARD MODE" : ""));

        System.out.print("Play another round? (y/n): ");
        if (Character.toString(input.next().toLowerCase().charAt(0)).equals("y")) { // checks first char of answer
            setupGame(input, gameNum + 1);
        } else { // print out leaderboard
            for (int attemptNum : leaderboard.keySet()) {
                System.out.println(
                        leaderboard.get(attemptNum) + ": " + attemptNum + " attempt" + (attemptNum > 1 ? "s" : ""));
            }
        }
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
     * post: checks the user-inputted word if it satisfies all the hard mode rules.
     * returns a certain message if one of the rules is broken and does not show any
     * new clues to the user
     * 
     * @param inputWord - user inputted word
     * 
     * @param solutionWord - word solution that will be compared with user word
     */
    private String checkWordHard(String inputWord, String solutionWord) {
        String ret = checkWord(inputWord, solutionWord);

        // checks if inputWord satisfies hard mode parameters
        // this looks ugly but i literally have no other idea on how to make this work
        Iterator<String> absentItr = absentLetters.iterator();
        while (absentItr.hasNext()) {
            String absentLetter = absentItr.next();
            if (inputWord.contains(absentLetter)) {
                return "Please include letters from previous hints. (The letter " + absentLetter
                        + " is not in the word.)";
            }
        }

        Iterator<String> guessItr = mustGuess.iterator();
        while (guessItr.hasNext()) {
            String guessLetter = guessItr.next();
            if (!mustGuess.isEmpty() && !inputWord.contains(guessLetter)) {
                return "Please include letters from previous hints. (The letter " + guessLetter
                        + " must be in your guess.)";
            }
        }

        for (int i = 0; i < wordBuild.length(); i++) {
            if (!Character.toString(wordBuild.charAt(i)).equals("-") && wordBuild.charAt(i) != inputWord.charAt(i)) {
                return "Please include letters from previous hints. (The letter " + wordBuild.charAt(i)
                        + " must be in the right spot in your guess.)";
            }
        }

        // puts letters of inputWord into its respective list(s)
        for (int i = 0; i < inputWord.length(); i++) {
            String symbol = Character.toString(ret.charAt(i));
            String currLetter = Character.toString(inputWord.charAt(i));
            // second conditional is so repeat letters in solution are not considered absent
            if (symbol.equals("_") && solutionWord.indexOf(currLetter) == -1) {
                absentLetters.add(currLetter);
            } else if (symbol.equals("*")) {
                mustGuess.add(currLetter);
            } else { // is a letter
                mustGuess.add(currLetter);
                wordBuild = replaceAt(wordBuild, i, currLetter);
            }
        }

        return ret;
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
     * and returns the result. this method is similar to the String library's
     * replace() function except that it does not replace every occurence of the
     * given input. rather, it only replaces at the provided index and returns a
     * string.
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
        System.out.println("After inputting each guess, you will see symbols on each letter position\n" +
                "of your word that says how close your guess is to the solution, along with your inputted\n"
                + "word beside the symbols:\n" +
                "\"_\" means that the letter is not found in the solution. (grey)\n" +
                "\"*\" means that the letter is found in the solution but in a different position. (yellow) \n" +
                "If the letter shows up in the result, it is in the correct position. (green) \n" +
                "NOTE: letter with a symbol \"*\" does not necessarily mean that it does not appear\n" +
                "appear somewhere else in the word (i.e. it can appear more than once). Good luck!\n");
    }
}
