import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AJEvolutivoPlus extends Thread implements Comparator<Path> {
    int id;
    static AJEStoragePlus ajeStorage;
    List<Path> paths;

    public AJEvolutivoPlus(int id) {
        paths = new ArrayList<>();
        this.id = id;
    }

    @Override
    public int compare(Path p1, Path p2) {
        return p1.distance(ajeStorage.matrix) - p2.distance(ajeStorage.matrix);
    }

    static class AJEStoragePlus implements Comparator<Path> {
        int size; // Size que está na 1ª coordenada de qualquer matriz, e esta variável é inicializada no método readMatrix()
        int[][] matrix;
        int numberOfThreads;
        long duration;
        int populationSize;
        float probability;
        float totalTimePercent;
        Path bestPath;
        List<Path> allPopulation;
        int ciclesToReachBestPath;
        long msToReachBestPath;
        int count;


        AJEStoragePlus(String filename, int numberOfThreads, long duration, int populationSize, float probability, float totalTimePercent) {
            readMatrix(filename);
            this.numberOfThreads = numberOfThreads;
            this.duration = duration;
            this.populationSize = populationSize;
            this.probability = probability;
            this.totalTimePercent = totalTimePercent;
            bestPath = new Path(size);
            allPopulation = new ArrayList<>();
            this.ciclesToReachBestPath = 0;
            this.msToReachBestPath = 0;
            count = 0;
        }

        int generateNumber(int size) {
            Random random = new Random();
            return random.nextInt(size);
        }

        Path generatePath() {
            Path newPath = new Path(size);
            for (int i = 0; i < size; i++) {
                newPath.getPath()[i] = i + 1;
            }
            for (int j = 0; j < size; j++) {
                int randomPos = generateNumber(size);
                int temp = newPath.getPath()[j];
                newPath.getPath()[j] = newPath.getPath()[randomPos];
                newPath.getPath()[randomPos] = temp;
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

        synchronized void updateInfoToBestPath(Path actualBestPath, Path toCheckPath, int count, long msTime) {
            if (actualBestPath.distance(matrix) > toCheckPath.distance(matrix)) {
                ciclesToReachBestPath = count;
                msToReachBestPath = msTime;
            }
        }

        synchronized void updateBestPath(Path pathFromThread) {
            if (bestPath == null || bestPath.distance(ajeStorage.matrix) > pathFromThread.distance(ajeStorage.matrix))
                bestPath = pathFromThread;
        }

        synchronized boolean addAllToPopulation(List<Path> threadPaths) {
            this.allPopulation.addAll(threadPaths);
            Collections.sort(allPopulation, this::compare);
            count++;
            if (count == numberOfThreads) {
                count = 0;
                notifyAll();
                return true;
            }
            return false;
        }

        synchronized List<Path> updateThreadPath(int popSize, boolean result) throws InterruptedException {
            if(!result)
                wait();
            List<Path> temp = new ArrayList<>();
            for (int i = 0; i < popSize; i++) {
                temp.add(allPopulation.get(i));
            }
            return temp;
        }

        @Override
        public int compare(Path p1, Path p2) {
            return p1.distance(ajeStorage.matrix) - p2.distance(ajeStorage.matrix);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String filename = "";
        int nThreads = -1;
        int execTime = -1;
        int populationSize = -1;
        float probability = -1;
        float totalTimePercent = -1;

        Scanner scan = new Scanner(System.in);
        System.out.println("Insira um comando no formato: 'Ficheiro' 'NúmeroProcessos' 'DuraçãoSegundos' 'TamanhoPopulação' 'ProbabilidadeMutação' 'PercentagemTempoTotal'");
        System.out.println("ex: ex6.txt 10 15 50 0.05 30\n-> ");
        String command = scan.nextLine();
        String[] commandSplit = command.split(" ");
        try {
            filename = commandSplit[0];
            nThreads = Integer.parseInt(commandSplit[1]);
            execTime = Integer.parseInt(commandSplit[2]) * 1000;
            populationSize = Integer.parseInt(commandSplit[3]);
            probability = Float.parseFloat(commandSplit[4]) * 100;
            totalTimePercent = Float.parseFloat(commandSplit[5]) / 100;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        AJEStoragePlus ajeStorage = new AJEStoragePlus(filename, nThreads, execTime, populationSize, probability, totalTimePercent);
        AJEvolutivoPlus threads[] = new AJEvolutivoPlus[nThreads];
        AJEvolutivoPlus.ajeStorage = ajeStorage;

        System.out.println("Executing...");
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new AJEvolutivoPlus(i);
            threads[i].start();
        }
        sleep(execTime + 1000);

        System.out.println("Executed!\n");
        System.out.println("--------------- All Data Information ---------------");
        System.out.println("Filename: " + filename + " | Execution Time: " + execTime + "ms | Population Size: " + populationSize + " | Mutation Probability: " + probability + "% | Best Path: " + ajeStorage.bestPath.toString() + " | Distance: " + ajeStorage.bestPath.distance(ajeStorage.matrix) + " | Iterations To Best Path: " + ajeStorage.ciclesToReachBestPath + " | Time to Best Path: " + ajeStorage.msToReachBestPath + "ms");
    }

    @Override
    public void run() {
        for (int i = 0; i < ajeStorage.populationSize; i++) {
            paths.add(ajeStorage.generatePath());
        }
        Collections.sort(paths, this::compare);

        long cicleTime = 0;
        long startTime = System.currentTimeMillis();

        int count = 0;
        float eachTimePercent = (float) ajeStorage.duration * ajeStorage.totalTimePercent;
        float timePercent = eachTimePercent;

        while (cicleTime < ajeStorage.duration) {
            count++;

            int[] offSpring1 = new int[ajeStorage.size];
            int[] offSpring2 = new int[ajeStorage.size];
            Random random = new Random();

            PMXCrossover.pmxCrossover(paths.get(0).getPath(), paths.get(1).getPath(), offSpring1, offSpring2, ajeStorage.size, random);

            Path off1 = new Path(ajeStorage.size, offSpring1);
            Path off2 = new Path(ajeStorage.size, offSpring2);

            off1.randomSwap(ajeStorage.probability);
            paths.add(off1);
            cicleTime = System.currentTimeMillis() - startTime;
            ajeStorage.updateInfoToBestPath(paths.get(0), off1, count, cicleTime);

            off2.randomSwap(ajeStorage.probability);
            paths.add(off2);
            cicleTime = System.currentTimeMillis() - startTime;
            ajeStorage.updateInfoToBestPath(paths.get(0), off2, count, cicleTime);

            Collections.sort(paths, this::compare);

            paths.remove(paths.size() - 1); // Remove o pior, que é a última posição
            paths.remove(paths.size() - 1); // Remove o pior, que é a última posição, pois já foi removido um pior que este anteriormente

            cicleTime = System.currentTimeMillis() - startTime;
            if (timePercent <= cicleTime && (timePercent + eachTimePercent) <= ajeStorage.duration) {
                timePercent += eachTimePercent;

                int popSize = paths.size();
                boolean result = ajeStorage.addAllToPopulation(this.paths);
                paths.clear();

                try {
                    paths.addAll(ajeStorage.updateThreadPath(popSize, result));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                Collections.sort(paths, this::compare);
            }
            cicleTime = System.currentTimeMillis() - startTime;
        }

        ajeStorage.updateBestPath(paths.get(0));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        interrupt();
    }
}
