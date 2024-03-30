// IDEA: every letter has to be a letter, let the user decide how long the word can be, add a hard mode

// Wordle program is a game played on console, where the user tries to guess a 4-letter word that is
// selected at random through every game. After the user word is inputted, the program will tell the
// user if any of the letters are contained in the solution word, and if it is in the right position.
// The user can do as many games as they want, and once they exit, a leaderboard will display their
// best games based on the number of words they inputted.

import java.util.*;

public class Wordle {

    private String solutionWord; // 4-letter word solution picked at random
    private List<String> solutionList; // 4-letter word solutions to choose from
    private List<String> bannedList; // words not eligible for user to guess
    private List<Integer> leaderboard; // leaderboard of best games by least amount of guesses
    private static int gameNum = 0; // game number user is currently on

    /*
     * pre: solutions and banned have each 4-letter word in diff lines
     * post: updates necessary instance variables by transferring contents of
     * solutions and banned files into the program
     * 
     * @param solutions - Scanner that reads contents of word solutions
     * 
     * @param banned - Scanner that reads contents of banned words
     */
    public Wordle(Scanner solutions, Scanner banned) {
        solutionList = new ArrayList<String>();
        bannedList = new ArrayList<String>();

        while (solutions.hasNextLine()) {
            solutionList.add(solutions.nextLine());
        }

        while (banned.hasNextLine()) {
            bannedList.add(banned.nextLine());
        }

        leaderboard = new ArrayList<Integer>();
    }

    /*
     * post: checks user-inputted word for any letters that are contained in the
     * solution word, and if it is in correct position. returns a string in which _
     * signifies the letter is not found in the solution, * signifying the letter is
     * in the word but not in the correct position, and the letter itself signifying
     * the letter is
     * in the correct position
     * 
     * @param inputWord - 4-letter user-inputted word
     */
    public String checkWord(String inputWord) {

        if (bannedList.contains(inputWord)) {
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
                tempSolution = tempSolution.substring(0, i) + "-" + tempSolution.substring(i + 1);
            } else {
                ret += "_";
            }
        }

        // checks if letter is in word (and isn't in correct position already)
        for (int i = 0; i < inputLetters.length; i++) {
            int inputLetterPos = tempSolution.indexOf(inputLetters[i]);
            // if letter is in right position or is not found in solution word, do nothing
            if (!inputLetters[i].equals("-") && inputLetterPos != -1) {
                ret = ret.substring(0, i) + "*" + ret.substring(i + 1);
                // temporarily removes letter so if same letter is found in different position,
                // may not be accounted for if input word already has enough of that letter that
                // solution word contains
                tempSolution = tempSolution.substring(0, inputLetterPos) + " "
                        + tempSolution.substring(inputLetterPos + 1);
            }
        }
        return ret;
    }

    /*
     * post: initiates Wordle program and outputs information about the _, *, and
     * letter
     * symbols before prompting the user to type a valid 4-letter guess
     */
    public void startWordle(Scanner input) {
        gameNum++;
        solutionWord = solutionList.get((int) (Math.random() * solutionList.size()));
        int attempts = 0;

        if (gameNum == 1) { // this only prints out when program is started
            System.out.println(
                    "Welcome to Wordle! Your objective is to guess the mystery 4-letter word\n" +
                            "in the least amount of guesses. Play as many games as you'd like! At the end,\n" +
                            "a leaderboard will display your best games by the number of guesses you used\n" +
                            "to get the mystery word!\n\n" +
                            "After inputting each guess, you will see symbols on each letter position\n" +
                            "of your word that says how close your guess is to the solution:");
        }

        printGuide();

        while (true) {
            System.out.print("Type your guess: ");
            String inputWord = input.next().toLowerCase().trim();

            if (inputWord.length() != 4) { // ensures input word is four letters
                System.out.println("Please type a four letter word.");
            } else {
                attempts++;
                System.out.println("\n" + checkWord(inputWord) + "\n");
                if (inputWord.equals(solutionWord)) {
                    break;
                }
            }
        }

        System.out.println("Congraulations! You got the word in " + attempts + " attempts!");
        leaderboard.add(attempts);

        System.out.print("Play another round? (y/n): ");
        String newGame = input.next().toLowerCase();
        if (newGame.equals("y")) {
            startWordle(input);
        } else {
            System.out.println(sortLeaderboard());
        }
    }

    /*
     * post: Returns a leaderboard sorted by the number of guesses the user used to
     * get the solution word, in increasing order.
     */
    public String sortLeaderboard() {

        String ret = "High Score Leaderboard: \n\n";
        // reference array to track which game solved with how many attempts
        int[] games = new int[leaderboard.size()];

        for (int i = 0; i < leaderboard.size(); i++) {
            games[i] = i + 1; // assigns numbers to the games array
        }

        for (int i = 0; i < leaderboard.size(); i++) {
            int current = leaderboard.get(i);
            int currentInd = i;

            for (int j = i + 1; j < leaderboard.size(); j++) {
                if (leaderboard.get(j) < current) {
                    current = leaderboard.get(j);
                    currentInd = j;
                }
            }

            // swaps the attempts in the leaderboard
            int temp = leaderboard.get(i);
            leaderboard.set(i, current);
            leaderboard.set(currentInd, temp);

            // swaps the game nums in the array
            temp = games[i];
            games[i] = games[currentInd];
            games[currentInd] = temp;
        }

        for (int i = 0; i < leaderboard.size(); i++) {
            ret += "Game " + games[i] + ": " + leaderboard.get(i) + " attempts\n";
        }

        return ret;
    }

    /*
     * post: prints out instructions for the user to refer to
     */
    private void printGuide() {
        System.out.println("\"_\" means that the letter is not found in the solution.\n" +
                "\"*\" means that the letter is found in the solution but in a different position.\n" +
                "If the letter you typed in shows up, it means it is in the correct position.\n" +
                "Note that a letter with a symbol \"*\" does not necessarily mean that it does not appear\n" +
                "appear somewhere else in the word (i.e. it can appear more than once). Good luck!\n");
    }
}