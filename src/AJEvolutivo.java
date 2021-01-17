import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class AJEvolutivo extends Thread {
    static AJEStorage ajeStorage;
    int[][] paths;
    int[][] shortestPath;
    //long duration;

    public AJEvolutivo() {

    }

    public int distance(int path[]) {
        if(path[0] == 0)
            return Integer.MAX_VALUE;

        int distance = 0;

        for (int i = 0; i < path.length-1; i++) {
            int curr = path[i] - 1;
            int next = path[i + 1] - 1;
            distance += ajeStorage.matrix[curr][next];
        }

        int last = path[path.length - 1] - 1;
        int first = path[0] - 1;
        distance += ajeStorage.matrix[last][first];

        return distance;
    }

    public void randomSwap(int[] path) {
        Random random = new Random();
        boolean equals = random.nextInt(100) == 0;

        if(equals) {
            int a = random.nextInt(ajeStorage.size);
            int b = random.nextInt(ajeStorage.size);
            int tmp = path[a];
            path[a] = path[b];
            path[b] = tmp;
        }
    }

    public void shortestPaths() {
        shortestPath = new int[2][ajeStorage.size];
        for(int i=0 ; i<ajeStorage.size ; i++) {
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

        for(int i=0 ; i<ajeStorage.size ; i++) {
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
        for (int i = 0; i < ajeStorage.size; i++) {
            System.out.print(path[i] + " ");
        }
        System.out.println("> Distance: " + distance(path));
    }

    public void printPaths() {
        for (int i = 0; i < ajeStorage.size; i++) {
            System.out.print("Path-" + (i+1) + ": < ");
            for (int j = 0; j < ajeStorage.size; j++) {
                System.out.print(paths[i][j] + " ");
            }
            System.out.println("> Distance: " + distance(paths[i]));
        }
        System.out.println();
    }

    public void printShortestPaths() {
        for(int i=0 ; i < 2 ; i++) {
            System.out.print("Path-" + (i+1) + ": < ");
            for (int j = 0; j < ajeStorage.size; j++) {
                System.out.print(shortestPath[i][j] + " ");
            }
            System.out.println("> Distance: " + distance(shortestPath[i]));
        }
        System.out.println("\n");
    }



    static class AJEStorage {
        int size;
        int[][] matrix;

        AJEStorage(String filename) {
            readMatrix(filename);
        }

        int generateNumber(int size) {
            Random random = new Random();
            return random.nextInt(size);
        }

        int[] generatePath() {
            int[] newPath = new int[size];
            for (int i = 0; i < size; i++) {
                newPath[i] = i + 1;
            }
            for (int j = 0; j < size; j++) {
                int randomPos = generateNumber(size);
                int temp = newPath[j];
                newPath[j] = newPath[randomPos];
                newPath[randomPos] = temp;
            }
            return newPath;
        }

        boolean readMatrix(String filename) {
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

        void printMatrix() {
            System.out.println("Matrix:");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println();
            }
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

        AJEvolutivoOld aje = new AJEvolutivoOld(filename);
        AJEvolutivoOld.AJEvolutivoThread[] threads = new AJEvolutivoOld.AJEvolutivoThread[nThreads];

        for (int i = 0; i < nThreads; i++) {
            threads[i] = new AJEvolutivoOld.AJEvolutivoThread(aje, nSeconds);
            threads[i].start();
            //threads[i].sleep(750);
        }

        //Thread.sleep(nSeconds);
    }

}
