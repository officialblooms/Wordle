import java.util.ArrayList;
import java.util.Scanner;

public class Wordle {

    private String wordSolution;
    private ArrayList<String> solutionList;
    private ArrayList<String> bannedList;
    private ArrayList<Integer> leaderboard;
    private static int gameNum = 0;

    public Wordle(ArrayList<String> solutionList, ArrayList<String> bannedList) {
        this.solutionList = solutionList;
        this.bannedList = bannedList;
        leaderboard = new ArrayList<Integer>();
    }

    public void getRandomWord() {
        String newWord = solutionList.get((int) (Math.random() * solutionList.size()));
        System.out.println(newWord);
        wordSolution = newWord;
    }

    public String checkWord(String inputWord) {

        for (String banned : bannedList) { // checks banned words list
            if (banned.equals(inputWord)) {
                return "You cannot input this banned word. Try again";
            }
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

    public void startWordle(Scanner input) {

        getRandomWord();

        gameNum++;
        int attempts = 0;
        System.out.println("Game " + gameNum + ":");

        while (true) {
            System.out.println("Guess the four-letter word by typing your guess below:");
            String inputWord = input.next().toLowerCase();
            if (inputWord.length() != 4) { // ensures input word is four letters
                System.out.println("Please type a four letter word.");
            }

            else {
                attempts++;
                System.out.println();
                System.out.println(checkWord(inputWord));
                System.out.println();
                if (inputWord.equals(wordSolution)) {
                    break;
                }
            }
        }

        System.out.println("You solved the word in " + attempts + " attempts!");
        leaderboard.add(attempts);

        System.out.println("Type Y for another round; N to stop and see leaderboard.");
        String newGame = input.next().toLowerCase();
        if (newGame.equals("y")) {
            startWordle(input);
        } else {
            System.out.println(sortLeaderboard());
            input.close();
            return;
        }
    }

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
}