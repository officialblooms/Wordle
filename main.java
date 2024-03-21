import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {

        ArrayList<String> solutionWords = FileReader.getStringData("solution.txt");
        ArrayList<String> bannedWords = FileReader.getStringData("bannedwords.txt");

        Wordle myGame = new Wordle(solutionWords, bannedWords);
        Scanner input = new Scanner(System.in);

        myGame.startWordle(input);
    }
}