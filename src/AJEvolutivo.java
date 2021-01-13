
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
        long duration;

        public AJEvolutivoThread(AJEvolutivo aje, long duration) {
            this.aje = aje;
            paths = new int[aje.size][aje.size];
            this.duration = duration;
        }

        @Override
        public void run() {

            for (int i = 0; i < aje.size; i++) {
                paths[i] = aje.generatePath();
            }

            while(true) {
                System.out.println(getName());

                shortestPaths();

                int[] offSpring1 = new int[aje.size];
                int[] offSpring2 = new int[aje.size];
                Random random = new Random();

                PMXCrossover.pmxCrossover(shortestPath[0], shortestPath[1], offSpring1, offSpring2, aje.size, random);

                randomSwap(offSpring1);
                randomSwap(offSpring2);

                changeWorstPaths(offSpring1, offSpring2);
            }
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

        public void randomSwap(int[] path) {
            Random random = new Random();
            boolean equals = random.nextInt(100) == 0;

            if(equals) {
                int a = random.nextInt(aje.size);
                int b = random.nextInt(aje.size);
                int tmp = path[a];
                path[a] = path[b];
                path[b] = tmp;
            }
        }

        public void shortestPaths() {
            shortestPath = new int[2][aje.size];
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

        public void changeWorstPaths(int[] path1, int[] path2) {
            int[] worstPath0 = paths[0];
            int[] worstPath1 = paths[0];
            int pos0 = 0;
            int pos1 = 0;

            for(int i=0 ; i<aje.size ; i++) {
                if(distance(paths[i]) > distance(worstPath0)) {
                    worstPath1 = worstPath0;
                    pos1 = pos0;
                    worstPath0 = paths[i];
                    pos0 = i;
                }
                if(distance(paths[i]) > distance(worstPath1) && distance(paths[i]) != distance(worstPath0)) {
                    worstPath1 = paths[i];
                    pos1 = i;
                }
            }

            paths[pos0] = path1;
            paths[pos1] = path2;
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
            System.out.println("\n");
        }

    }

    public int generateNumber(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

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
            Scanner scan = new Scanner(file);
            size = scan.nextInt();
            matrix = new int[size][size];

            while (scan.hasNextInt()) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = scan.nextInt();
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
        String filename = "";
        int nThreads = -1;
        int nSeconds = -1;

        Scanner scan = new Scanner(System.in);
        System.out.println("Insira um comando no formato: 'Ficheiro' 'NúmeroProcessos' 'DuraçãoSegundos'");
        System.out.println("ex: ex6.txt 10 15\n-> ");
        String command = scan.nextLine();
        String[] commandSplit = command.split(" ");
        try {
            filename = commandSplit[0];
            nThreads = Integer.parseInt(commandSplit[1]);
            nSeconds = Integer.parseInt(commandSplit[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        AJEvolutivo aje = new AJEvolutivo(filename);
        AJEvolutivoThread[] threads = new AJEvolutivoThread[nThreads];

        for (int i = 0; i < nThreads; i++) {
            threads[i] = new AJEvolutivoThread(aje, nSeconds);
            threads[i].start();
            //threads[i].sleep(750);
        }

        //Thread.sleep(nSeconds);
    }

}
