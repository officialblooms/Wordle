// Wordle program is a game played on console, where the user tries to guess a 4-letter word that is
// selected at random through every game. After the user word is inputted, the program will tell the
// user if any of the letters are contained in the solution word, and if it is in the right position.
// The user can do as many games as they want, and once they exit, a leaderboard will display their
// best games based on the number of words they inputted.

import java.util.*;

public class Wordle {

    private String wordSolution; // 4-letter word solution picked at random
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
     * signifies the letter is not found in the solution, x signifying the letter is
     * in the word but not in the correct position, and o signifying the letter is
     * in the correct position
     * 
     * @param inputWord - 4-letter user-inputted word
     */
    public String checkWord(String inputWord) {

        if (bannedList.contains(inputWord)) {
            return "Please type in a valid word.";
        }

        String ret = "";
        String temp = wordSolution;
        String[] wordStatus = new String[temp.length()];

        // checks if letter is in the correct position
        for (int i = 0; i < inputWord.length(); i++) {

            wordStatus[i] = "notinWord";
            String inputLetter = inputWord.substring(i, i + 1);

            for (int j = 0; j < temp.length(); j++) {

                String solutionLetter = temp.substring(j, j + 1);

                /*
                 * checks if letter is in correct pos; solution word (temp) will
                 * remove that letter so if the input word has the same letter in
                 * a different position it won't say "inWord".
                 */
                if (solutionLetter.equals(inputLetter) && i == j) {
                    wordStatus[i] = "inPosition";
                    if (i == 3) { // prevents out of bounds error
                        temp = temp.substring(0, i) + "-";
                    } else {
                        temp = temp.substring(0, i) + "-" + temp.substring(i + 1);
                    }
                }
            }
        }

        for (int i = 0; i < inputWord.length(); i++) {

            if (wordStatus[i].equals("inPosition")) {
                continue;
            }

            String inputLetter = inputWord.substring(i, i + 1);

            for (int j = 0; j < temp.length(); j++) {

                String solutionLetter = temp.substring(j, j + 1);

                if (solutionLetter.equals(inputLetter)) { // if letters match
                    wordStatus[i] = "inWord";
                }
            }
        }

        for (String status : wordStatus) {
            ret += status + ", ";
        }
        return ret;
    }

    /*
     * post: initiates Wordle program and outputs information about the _, x, and o
     * symbols before prompting the user to type a valid 4-letter guess
     */
    public void startWordle(Scanner input) {
        gameNum++;
        wordSolution = solutionList.get((int) (Math.random() * solutionList.size()));
        int attempts = 0;

        if (gameNum == 1) { // this only prints out when program is started
            System.out.println(
                    "Welcome to Wordle! Your objective is to guess the mystery 4-letter word\n" +
                            "in the least amount of guesses. Play as many games as you'd like! At the end,\n" +
                            "a leaderboard will display your best games by the number of guesses you used\n" +
                            "to get the mystery word!\n");
        }

        printGuide();

        while (true) {
            System.out.println("Guess the four-letter word by typing your guess below:");
            String inputWord = input.next().toLowerCase().trim();

            if (inputWord.length() != 4) { // ensures input word is four letters
                System.out.println("Please type a four letter word.");
            }

            else {
                attempts++;
                System.out.println("\n" + checkWord(inputWord) + "\n");
                if (inputWord.equals(wordSolution)) {
                    break;
                }
            }
        }

        System.out.println("Congraulations! You solved the word in " + attempts + " attempts!");
        leaderboard.add(attempts);

        System.out.println("Type Y for another round; N to stop playing and view the leaderboard.");
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
        System.out.println("After inputting each guess, you will see symbols on each letter position\n" +
                "of your word that says how close your guess is to the solution.\n" +
                "\"_\" means that the letter is not found in the solution.\n" +
                "\"x\" means that the letter is found in the solution but in a different position.\n" +
                "\"o\" means that the letter is in the correct position in the solution.\n" +
                "Note that a letter with a symbol \"o\" does not necessarily mean that it does not appear\n" +
                "appear somewhere else in the word (i.e. it can appear more than once). Good luck!");
    }
}