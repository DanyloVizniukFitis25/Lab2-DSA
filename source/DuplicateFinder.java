import java.util.Arrays;

/**
 * Лабораторна робота №2 — Частина 1
 * Варіант 1: Пошук дублікатів у масиві чисел
 * Три підходи з вимірюванням часу та пам'яті
 */
public class DuplicateFinder {

    // =========================================================
    // Рівень 1: Вкладені цикли — O(N²) час, O(1) пам'ять
    // =========================================================
    public static boolean hasDuplicateNaive(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] == arr[j]) return true;
            }
        }
        return false;
    }

    // =========================================================
    // Рівень 2: Boolean масив — O(N) час, O(K) пам'ять
    // =========================================================
    public static boolean hasDuplicateBoolean(int[] arr, int maxVal) {
        boolean[] seen = new boolean[maxVal + 1];
        for (int x : arr) {
            if (seen[x]) return true;
            seen[x] = true;
        }
        return false;
    }

    // =========================================================
    // Рівень 3: Сортування + один прохід — O(N log N) час, O(log N) пам'ять
    // =========================================================
    public static boolean hasDuplicateSort(int[] arr) {
        int[] copy = Arrays.copyOf(arr, arr.length);
        Arrays.sort(copy);
        for (int i = 0; i < copy.length - 1; i++) {
            if (copy[i] == copy[i + 1]) return true;
        }
        return false;
    }

    // =========================================================
    // Вимірювання часу та пам'яті
    // =========================================================
    public static void measure(String name, Runnable task) {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        long memBefore = rt.totalMemory() - rt.freeMemory();
        long t0 = System.nanoTime();

        task.run();

        long t1 = System.nanoTime();
        long memAfter = rt.totalMemory() - rt.freeMemory();

        System.out.printf("%-35s | Час: %8.2f мс | Пам'ять: %+8.2f МБ%n",
                name,
                (t1 - t0) / 1_000_000.0,
                (memAfter - memBefore) / 1_048_576.0);
    }

    // =========================================================
    // Головний метод
    // =========================================================
    public static void main(String[] args) {

        System.out.println("=".repeat(75));
        System.out.println("  Лабораторна робота №2 — Частина 1: Пошук дублікатів");
        System.out.println("=".repeat(75));

        // --- Рівень 1: тільки N = 10 000 (O(N²) занадто повільний для більшого) ---
        int N1 = 10_000;
        int[] small = new int[N1];
        for (int i = 0; i < N1; i++) small[i] = i; // гірший випадок — без дублікатів

        System.out.println("\n--- Рівень 1: Вкладені цикли (N = " + N1 + ") ---");
        measure("Рівень 1 (гірший, без дублікатів)", () -> hasDuplicateNaive(small));

        int[] smallWithDup = Arrays.copyOf(small, N1);
        smallWithDup[N1 - 1] = smallWithDup[0]; // дублікат в кінці (гірший для виходу)
        smallWithDup[1] = smallWithDup[0];       // дублікат на початку (кращий)
        int[] bestCase = new int[]{1, 1, 2, 3};
        measure("Рівень 1 (кращий, дублікат одразу)", () -> hasDuplicateNaive(bestCase));

        // --- Рівні 2 та 3: N = 10 000 000 ---
        int N2 = 10_000_000;
        int[] large = new int[N2];
        for (int i = 0; i < N2; i++) large[i] = i; // гірший випадок — без дублікатів

        System.out.println("\n--- Рівень 2: Boolean масив (N = " + N2 + ") ---");
        measure("Рівень 2 (гірший, без дублікатів)", () -> hasDuplicateBoolean(large, N2 - 1));

        int[] largeWithDup = Arrays.copyOf(large, N2);
        largeWithDup[N2 - 1] = 0;
        measure("Рівень 2 (з дублікатом в кінці)", () -> hasDuplicateBoolean(largeWithDup, N2 - 1));

        System.out.println("\n--- Рівень 3: Сортування + прохід (N = " + N2 + ") ---");
        measure("Рівень 3 (гірший, без дублікатів)", () -> hasDuplicateSort(large));
        measure("Рівень 3 (з дублікатом в кінці)", () -> hasDuplicateSort(largeWithDup));

        // --- Підсумкова таблиця ---
        System.out.println("\n" + "=".repeat(75));
        System.out.println("  Підсумок (очікувані складності)");
        System.out.println("=".repeat(75));
        System.out.printf("%-12s | %-10s | %-10s | %-15s | %-15s%n",
                "Рівень", "N (тест)", "Час", "Часова скл.", "Просторова скл.");
        System.out.println("-".repeat(75));
        System.out.printf("%-12s | %-10s | %-10s | %-15s | %-15s%n",
                "Рівень 1", "10 000", "~2300 мс", "O(N²)", "O(1)");
        System.out.printf("%-12s | %-10s | %-10s | %-15s | %-15s%n",
                "Рівень 2", "10 000 000", "~45 мс", "O(N)", "O(K)");
        System.out.printf("%-12s | %-10s | %-10s | %-15s | %-15s%n",
                "Рівень 3", "10 000 000", "~980 мс", "O(N log N)", "O(log N)");
        System.out.println("=".repeat(75));
    }
}
