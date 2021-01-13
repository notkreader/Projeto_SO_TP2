
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class AJEvolutivo {
    private int size;
    private int[][] matrix;

    public AJEvolutivo(String filename) {
        readMatrix(filename);
    }

    static class AJEvolutivoThread extends Thread {
        AJEvolutivo aje;
        int[][] paths;
        int[][] shortestPath;

        public AJEvolutivoThread(AJEvolutivo aje) {
            this.aje = aje;
            paths = new int[aje.size][aje.size];
            shortestPath = new int[2][aje.size];
        }

        @Override
        public void run() {
            System.out.println(getName());

            for (int i = 0; i < aje.size; i++) {
                paths[i] = aje.generatePath();
            }

            printPaths();

            System.out.println("\n");

            shortestPaths();

            printShortestPaths();
        }

        public int distance(int path[]) {
            if(path[0] == 0)
                return Integer.MAX_VALUE;

            int distance = 0;

            for (int i = 0; i < path.length-1; i++) {
                int curr = path[i] - 1;
                int next = path[i + 1] - 1;
                distance += aje.matrix[curr][next];
            }

            int last = path[path.length - 1] - 1;
            int first = path[0] - 1;
            distance += aje.matrix[last][first];

            return distance;
        }

        public void shortestPaths() {
            for(int i=0 ; i<aje.size ; i++) {
                if(distance(shortestPath[1]) > distance(paths[i])) {
                    if(distance(shortestPath[0]) > distance(paths[i])) {
                        int[] temp = shortestPath[0];
                        shortestPath[0] = paths[i];
                        shortestPath[1] = temp;
                    }
                    else {
                        shortestPath[1] = paths[i];
                    }
                }
            }
        }

        public void printPath(int[] path) {
            System.out.print("Path: < ");
            for (int i = 0; i < aje.size; i++) {
                System.out.print(path[i] + " ");
            }
            System.out.println("> Distance: " + distance(path));
        }

        public void printPaths() {
            for (int i = 0; i < aje.size; i++) {
                System.out.print("Path-" + (i+1) + ": < ");
                for (int j = 0; j < aje.size; j++) {
                    System.out.print(paths[i][j] + " ");
                }
                System.out.println("> Distance: " + distance(paths[i]));
            }
            System.out.println();
        }

        public void printShortestPaths() {
            for(int i=0 ; i < 2 ; i++) {
                System.out.print("Path-" + (i+1) + ": < ");
                for (int j = 0; j < aje.size; j++) {
                    System.out.print(shortestPath[i][j] + " ");
                }
                System.out.println("> Distance: " + distance(shortestPath[i]));
            }
            System.out.println();
        }

    }

    public int[][] getMatrix() {
        return this.matrix;
    }




    public int generateNumber(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    /*public void swap() {
        int a = generateNumber(path.length);
        int b = generateNumber(path.length);
        int tmp = path[a];
        path[a] = path[b];
        path[b] = tmp;
    }*/

    public int[] generatePath() {
        int[] newPath = new int[size];

        //Fill the path to size
        for (int i = 0; i < size; i++) {
            newPath[i] = i + 1;
        }

        //Shuffle the path
        for (int j = 0; j < size; j++) {
            int randomPos = generateNumber(size);

            int temp = newPath[j];
            newPath[j] = newPath[randomPos];
            newPath[randomPos] = temp;
        }

        return newPath;
    }

    public boolean readMatrix(String filename) {
        String dir = "tsp_testes\\" + filename;
        File file = new File(dir);

        try {
            Scanner scann = new Scanner(file);
            size = scann.nextInt();
            matrix = new int[size][size];

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


    public void printMatrix() {
        System.out.println("Matrix:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        AJEvolutivo aje = new AJEvolutivo("burma14.txt");
        int nThreads = 10;

        AJEvolutivoThread[] threads = new AJEvolutivoThread[nThreads];

        for (int i = 0; i < nThreads; i++) {
            threads[i] = new AJEvolutivoThread(aje);
            threads[i].start();
            threads[i].sleep(750);
        }
    }

}
