import java.util.*;
import java.io.*;

public class main {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner solutions = new Scanner(new File("solutions.txt"));
        Scanner input = new Scanner(System.in);

        Wordle myGame = new Wordle(solutions);
        myGame.startWordle(input);

        solutions.close();
        input.close(); // closes all running Scanners
    }
}