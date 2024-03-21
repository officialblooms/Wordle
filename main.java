import java.util.*;
import java.io.*;

public class main {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner solutions = new Scanner(new File("solution.txt"));
        Scanner banned = new Scanner(new File("bannedwords.txt"));
        Scanner input = new Scanner(System.in);

        Wordle myGame = new Wordle(solutions, banned);
        myGame.startWordle(input);

        solutions.close();
        banned.close();
        input.close(); // closes all running Scanners
    }
}