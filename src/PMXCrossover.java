import java.util.Random;

public class PMXCrossover {

    static void pmxCrossover(int parent1[], int parent2[],
                             int offSpring1[], int offSpring2[], int n, Random rand) {
        int replacement1[] = new int[n + 1];
        int replacement2[] = new int[n + 1];
        int i, n1, m1, n2, m2;
        int swap;

        for (i = 0; i < n; i++)
            System.out.printf("%2d ", parent1[i]);
        System.out.println();
        for (i = 0; i < n; i++)
            System.out.printf("%2d ", parent2[i]);
        System.out.println();

        int cuttingPoint1 = rand.nextInt(n);
        int cuttingPoint2 = rand.nextInt(n);

        //int cuttingPoint1 = 3;
        //int cuttingPoint2 = 5;

        while (cuttingPoint1 == cuttingPoint2) {
            cuttingPoint2 = rand.nextInt(n);
        }
        if (cuttingPoint1 > cuttingPoint2) {
            swap = cuttingPoint1;
            cuttingPoint1 = cuttingPoint2;
            cuttingPoint2 = swap;
        }

        System.out.printf("cp1 = %d cp2 = %d\n", cuttingPoint1, cuttingPoint2);

        for (i = 0; i < n + 1; i++) {
            replacement1[i] = -1;
            replacement2[i] = -1;
        }

        for (i = cuttingPoint1; i <= cuttingPoint2; i++) {
            offSpring1[i] = parent2[i];
            offSpring2[i] = parent1[i];
            replacement1[parent2[i]] = parent1[i];
            replacement2[parent1[i]] = parent2[i];
        }

        for (i = 0; i < n + 1; i++)
            System.out.printf("%2d ", replacement1[i]);
        System.out.println();
        for (i = 0; i < n + 1; i++)
            System.out.printf("%2d ", replacement2[i]);
        System.out.println();
        // fill in remaining slots with replacements
        for (i = 0; i < n; i++) {
            if ((i < cuttingPoint1) || (i > cuttingPoint2)) {
                n1 = parent1[i];
                m1 = replacement1[n1];
                n2 = parent2[i];
                m2 = replacement2[n2];
                while (m1 != -1) {
                    n1 = m1;
                    m1 = replacement1[m1];
                }
                while (m2 != -1) {
                    n2 = m2;
                    m2 = replacement2[m2];
                }
                offSpring1[i] = n1;
                offSpring2[i] = n2;
            }
        }
    }
}

