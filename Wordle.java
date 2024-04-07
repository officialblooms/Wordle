// IDEA: every letter has to be a letter, let the user decide how long the word can be, add a hard mode
// figure out what makes a word valid(?)

// IMPROVE: traverse the word list only once by having a word length be the key to words that fit that length in a map

// Wordle program is a game played on console, where the user tries to guess a word that is
// selected at random through every game. After the user word is inputted, the program will tell the
// user if any of the letters are contained in the solution word, and if it is in the right position.
// The user can do as many games as they want, and once they exit, a leaderboard will display their
// best games based on the number of words they inputted.

import java.util.*;

public class Wordle {

    private String solutionWord; // word solution picked at random
    private List<String> wordList; // list of legal words
    private List<String> solutionList; // word solutions to choose from
    private Map<Integer, ArrayList<String>> leaderboard; // leaderboard of best games by least amount of guesses

    /*
     * pre: words file has each word in a new line
     * post: updates necessary instance variables by transferring contents of
     * solutions and banned files into the program
     * 
     * @param solutions - Scanner that reads contents of word solutions
     * 
     * @param banned - Scanner that reads contents of banned words
     */
    public Wordle(Scanner words) {
        wordList = new ArrayList<>();
        solutionList = new ArrayList<>();

        while (words.hasNextLine()) {
            wordList.add(words.nextLine());
        }

        leaderboard = new TreeMap<>();
    }

    /*
     * post: checks user-inputted word for any letters that are contained in the
     * solution word, and if it is in correct position. returns a string in which _
     * signifies the letter is not found in the solution, * signifying the letter is
     * in the word but not in the correct position, and the letter itself signifying
     * the letter is
     * in the correct position
     * 
     * @param inputWord - user-inputted word
     */
    public String checkWord(String inputWord) {

        if (inputWord.length() != solutionWord.length()) {
            return "Please type in a valid word.";
        }

        String[] inputLetters = inputWord.split("");
        String tempSolution = solutionWord; // this will be modified later so its best to make a copy
        String ret = "";

        for (int i = 0; i < inputLetters.length; i++) { // checks if letter is in correct position
            if (inputLetters[i].equals(Character.toString(tempSolution.charAt(i)))) {
                ret += inputLetters[i];
                // letter is in correct position, so set to "-" to not be compared again
                inputLetters[i] = "-";
                replaceAt(tempSolution, i, "-");
            } else {
                ret += "_";
            }
        }

        // checks if letter is in word (and isn't in correct position already)
        for (int i = 0; i < inputLetters.length; i++) {
            int inputLetterPos = tempSolution.indexOf(inputLetters[i]);
            // if letter is in right position or is not found in solution word, do nothing
            if (!inputLetters[i].equals("-") && inputLetterPos != -1) {
                replaceAt(ret, i, "*");
                // temporarily removes letter so if same letter is found in different position,
                // may not be accounted for if input word already has enough of that letter that
                // solution word contains
                replaceAt(tempSolution, inputLetterPos, " ");
            }
        }
        return ret;
    }

    /*
     * post: sets up the Wordle program by prompting the user to specify solution
     * word length to guess
     * 
     * @param input - Scanner for user input
     */
    public void startWordle(Scanner input) {
        System.out.println(
                "Welcome to Wordle! Your objective is to guess the mystery word in the\n" +
                        "least amount of guesses. Play as many games as you'd like! At the end,\n" +
                        "a leaderboard will display your best games by the number of guesses you used\n" +
                        "to get the mystery word!\n\n");
        while (true) {
            System.out.print("How long would you like the word solution to be? Choose between 4 and 8: ");
            int length = input.nextInt(); // catch "not an integer" case later
            if (length < 4 || length > 8) {
                System.out.println("Please type a number between 4 and 8");
            } else {
                // this for loop adds all words in wordList with specified length into solution
                // list
                for (String word : wordList) {
                    if (word.length() == length) {
                        solutionList.add(word);
                    }
                }
                break;
            }
        }

        System.out.println("After inputting each guess, you will see symbols on each letter position\n" +
                "of your word that says how close your guess is to the solution:");
        startWordle(input, 1);
    }

    /*
     * helper method for startWordle to track game number for leaderboard tracking
     * 
     * @param input - Scanner for user input
     * 
     * @param gameNum - game number user is currently on; default is 1
     */
    private void startWordle(Scanner input, int gameNum) {
        int attempts = 0;
        solutionWord = solutionList.get((int) (Math.random() * solutionList.size()));
        System.out.println(solutionWord);
        printGuide();

        while (true) {
            System.out.print("Type your guess: ");
            String inputWord = input.next().toLowerCase().trim();

            attempts++;
            System.out.println("\n" + checkWord(inputWord) + "\n");
            if (inputWord.equals(solutionWord)) {
                break;
            }
        }

        System.out.println(
                "Congraulations! You got the word in " + attempts + " attempt" + (attempts > 1 ? "s" : "") + "!");
        if (!leaderboard.containsKey(attempts)) { // if no other game with same num of attempts exist
            leaderboard.put(attempts, new ArrayList<>()); // create a new array of game number strings
        }
        leaderboard.get(attempts).add("Game #" + gameNum); // add game number to amount of attempts

        System.out.print("Play another round? (y/n): ");
        String newGame = input.next().toLowerCase();
        if (newGame.equals("y")) {
            startWordle(input, gameNum + 1);
        } else { // print out leaderboard
            for (int attemptNum : leaderboard.keySet()) {
                System.out.println(
                        leaderboard.get(attemptNum) + ": " + attemptNum + " attempt" + (attemptNum > 1 ? "s" : ""));
            }
        }
    }

    /*
     * pre: index is within bounds of given String (throw IndexOutOfBoundsException
     * otherwise)
     * post: replaces the given string with the given character at the given index.
     * this method is similar to the String library's replace() function except that
     * it does not replace every occurence of the given input. rather, it only
     * replaces at the provided index
     * 
     * @param toReplace - String to have its contents modified
     * 
     * @param index - character position to be replaced
     * 
     * @param character - character to replace at the given index
     */
    private void replaceAt(String toReplace, int index, String character) {
        if (index < 0 || index >= toReplace.length()) {
            throw new IndexOutOfBoundsException();
        }
        toReplace = toReplace.substring(0, index) + character + toReplace.substring(index + 1);
    }

    /*
     * post: prints out instructions for the user to refer to
     */
    private void printGuide() {
        System.out.println("\"_\" means that the letter is not found in the solution.\n" +
                "\"*\" means that the letter is found in the solution but in a different position.\n" +
                "If the letter shows up in the result, it is in the correct position.\n" +
                "NOTE: letter with a symbol \"*\" does not necessarily mean that it does not appear\n" +
                "appear somewhere else in the word (i.e. it can appear more than once). Good luck!\n");
    }
}
