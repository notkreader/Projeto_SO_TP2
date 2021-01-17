import java.util.Random;

public class Path {
    private int size;
    private int[] path;

    public Path(int size) {
        this.size = size;
        path = new int[size];
    }

    public Path(int size, int[] path) {
        this.size = size;
        this.path = path;
    }

    public int[] getPath() {
        return path;
    }

    public int distance(int matrix[][]) {
        if(path[0] == 0)
            return Integer.MAX_VALUE;

        int distance = 0;

        for (int i = 0; i < path.length-1; i++) {
            int curr = path[i] - 1;
            int next = path[i + 1] - 1;
            distance += matrix[curr][next];
        }

        int last = path[path.length - 1] - 1;
        int first = path[0] - 1;
        distance += matrix[last][first];

        return distance;
    }

    public void randomSwap() {
        Random random = new Random();
        boolean equals = random.nextInt(100) == 0;

        if(equals) {
            int a = random.nextInt(size);
            int b = random.nextInt(size);
            int tmp = path[a];
            path[a] = path[b];
            path[b] = tmp;
        }
    }

    @Override
    public String toString() {
        String str = "Path: < ";
        for (int i = 0; i < size; i++) {
            str += path[i] + " ";
        }
        str += ">";
        return str;
    }
}
