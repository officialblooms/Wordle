import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

/*
 * Reads data from a file
 */
public final class FileReader {

    private static File myFile; // The File containing the data
    private static Scanner fileReader; // The Scanner object to read the file

    /*
     * Returns an ArrayList of Strings from a file
     */
    public static ArrayList<String> getStringData(String filename) {
        createFile(filename);

        ArrayList<String> tempList = new ArrayList<String>();

        while (fileReader.hasNextLine()) {
            tempList.add(fileReader.nextLine());
        }

        fileReader.close();
        return tempList;
    }

    /*
     * Returns an ArrayList of words from a file
     */
    public static ArrayList<String> getWords(String filename) {
        createFile(filename);

        ArrayList<String> tempList = new ArrayList<String>();

        while (fileReader.hasNext()) {
            tempList.add(fileReader.next());
        }

        fileReader.close();
        return tempList;
    }

    /*
     * Returns an ArrayList of ints from a file
     */
    public static ArrayList<Integer> getIntData(String filename) {
        createFile(filename);

        ArrayList<Integer> tempList = new ArrayList<Integer>();

        while (fileReader.hasNextInt()) {
            tempList.add(fileReader.nextInt());
        }

        fileReader.close();
        return tempList;
    }

    /*
     * Returns an ArrayList of doubles from a file
     */
    public static ArrayList<Double> getDoubleData(String filename) {
        createFile(filename);

        ArrayList<Double> tempList = new ArrayList<Double>();

        while (fileReader.hasNextDouble()) {
            tempList.add(fileReader.nextDouble());
        }

        fileReader.close();
        return tempList;
    }

    /*
     * Creates the File and Scanner to read the specified filename
     */
    public static void createFile(String filename) {
        myFile = new File(filename);
        fileReader = createScanner(myFile);
    }

    /*
     * Returns a Scanner object to read a file or notifies the
     * user if the file is not found
     */
    public static Scanner createScanner(File theFile) {
        Scanner tempScanner = null;

        try {
            tempScanner = new Scanner(theFile);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        return tempScanner;
    }
}