
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class AJEvolutivo {
    private int size;
    private int[] path;
    private int[][] matrix;

    public AJEvolutivo(String filename) {
        start(filename);
    }

    static class AJEvolutivoThread extends Thread {
        AJEvolutivo aje;
        public AJEvolutivoThread(AJEvolutivo aje) {
            this.aje = aje;
        }

        @Override
        public void run() {

        }

        public static void main(String[] args) {

        }
    }

    /*public int[] getPath() {
        return this.path;
    }

    public int[][] getMatrix() {
        return this.matrix;
    }*/

    private void start(String filename) {
        readMatrix(filename);
    }

    public int distance() {
        int distance = 0;

        for (int i = 0; i < path.length - 1; i++) {
            int curr = path[i] - 1;
            int next = path[i + 1] - 1;
            distance += matrix[curr][next];
        }

        int last = path[path.length - 1] - 1;
        int first = path[0] - 1;
        distance += matrix[last][first];

        return distance;
    }

    public int generateNumber(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    public void swap() {
        int a = generateNumber(path.length);
        int b = generateNumber(path.length);
        int tmp = path[a];
        path[a] = path[b];
        path[b] = tmp;
    }

    public void generatePath() {
        boolean full = false;
        int pos = 0;

        //Fill the path to size
        for (int i = 0; i < path.length; i++) {
            path[i] = i + 1;
        }

        //Shuffle the path
        for (int j = 0; j < path.length; j++) {
            int randomPos = generateNumber(path.length);

            int temp = path[j];
            path[j] = path[randomPos];
            path[randomPos] = temp;
        }
    }

    public boolean readMatrix(String filename) {
        String dir = "tsp_testes\\" + filename;
        File file = new File(dir);

        try {
            Scanner scann = new Scanner(file);
            size = scann.nextInt();
            matrix = new int[size][size];
            path = new int[size];

            while (scann.hasNextInt()) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = scann.nextInt();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void printPath() {
        System.out.print("Path: <");
        for (int i = 0; i < path.length - 1; i++) {
            System.out.print(path[i] + "-");
        }
        System.out.println(">");
    }

    public void printMatrix() {
        System.out.println("Matrix:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }


}
