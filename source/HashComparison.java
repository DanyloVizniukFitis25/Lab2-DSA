import java.util.*;
import java.util.function.ToIntFunction;

/**
 * Лабораторна робота №2 — Частина 2
 * Порівняння двох алгоритмів хешування рядків
 *   Варіант 1: Хешування сумою ASCII-кодів
 *   Варіант 2: Поліноміальне хешування (Метод Горнера)
 *
 * Метод hashCode() не використовується!
 */
public class HashComparison {

    // --- Константи ---
    static final int M1 = 997;          // просте число для ASCII-суми
    static final int P  = 31;           // множник для методу Горнера
    static final int M2 = 1_000_003;    // велике просте число для поліному

    // =========================================================
    // Варіант 1: Хешування сумою ASCII-кодів
    // H(S) = (Σ S[i]) mod M
    // Недолік: анаграми ("cat" і "act") дають однаковий хеш
    // =========================================================
    public static int asciiHash(String s) {
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            sum += (int) s.charAt(i);
        }
        return sum % M1;
    }

    // =========================================================
    // Варіант 2: Поліноміальне хешування (Метод Горнера)
    // H(S) = (S[0]*P^(L-1) + S[1]*P^(L-2) + ... + S[L-1]) mod M
    // Перевага: враховує порядок символів → анаграми не колізують
    // =========================================================
    public static int polynomialHash(String s) {
        long h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = (h * P + (int) s.charAt(i)) % M2;
        }
        return (int) h;
    }

    // =========================================================
    // Підрахунок колізій
    // Колізія — коли два різних слова дають однаковий хеш
    // =========================================================
    public static int countCollisions(String[] words, ToIntFunction<String> hashFn) {
        Map<Integer, List<String>> table = new HashMap<>();
        for (String w : words) {
            int h = hashFn.applyAsInt(w);
            table.computeIfAbsent(h, k -> new ArrayList<>()).add(w);
        }

        int collisions = 0;
        int bucketsWith2Plus = 0;
        for (List<String> bucket : table.values()) {
            if (bucket.size() > 1) {
                collisions += bucket.size() - 1;
                bucketsWith2Plus++;
            }
        }

        System.out.printf("  Заповнених відер: %d / %d%n",
                table.size(), words.length);
        System.out.printf("  Відер з колізіями: %d%n", bucketsWith2Plus);
        System.out.printf("  Загальна кількість колізій: %d%n", collisions);
        return collisions;
    }

    // =========================================================
    // Генератор 1000 унікальних випадкових слів
    // =========================================================
    public static String[] generateWords(int n) {
        Random rnd = new Random(42);
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Set<String> set = new LinkedHashSet<>();
        while (set.size() < n) {
            int len = 3 + rnd.nextInt(8); // слова довжиною 3–10 символів
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append(alphabet.charAt(rnd.nextInt(26)));
            }
            set.add(sb.toString());
        }
        return set.toArray(new String[0]);
    }

    // =========================================================
    // Демонстрація анаграм — слабке місце ASCII-хешування
    // =========================================================
    public static void demonstrateAnagramCollisions() {
        System.out.println("\n--- Демонстрація колізій на анаграмах ---");
        String[][] anagrams = {
            {"listen", "silent", "enlist"},
            {"cat", "act", "tac"},
            {"abc", "bca", "cab", "bac", "acb", "cba"}
        };
        for (String[] group : anagrams) {
            System.out.printf("  Група анаграм: %s%n", Arrays.toString(group));
            System.out.printf("    ASCII хеші:      %s%n",
                    Arrays.stream(group)
                          .map(w -> String.valueOf(asciiHash(w)))
                          .reduce((a, b) -> a + ", " + b).orElse(""));
            System.out.printf("    Поліном хеші:    %s%n",
                    Arrays.stream(group)
                          .map(w -> String.valueOf(polynomialHash(w)))
                          .reduce((a, b) -> a + ", " + b).orElse(""));
        }
    }

    // =========================================================
    // Головний метод
    // =========================================================
    public static void main(String[] args) {

        System.out.println("=".repeat(65));
        System.out.println("  Лабораторна робота №2 — Частина 2: Алгоритми хешування");
        System.out.println("=".repeat(65));

        String[] words = generateWords(1000);
        System.out.println("\nЗгенеровано " + words.length + " унікальних слів.");

        // --- Варіант 1: ASCII-сума ---
        System.out.println("\n[Варіант 1] ASCII-хешування (M = " + M1 + "):");
        int col1 = countCollisions(words, HashComparison::asciiHash);

        // --- Варіант 2: Поліном Горнера ---
        System.out.println("\n[Варіант 2] Поліноміальне хешування (P = " + P + ", M = " + M2 + "):");
        int col2 = countCollisions(words, HashComparison::polynomialHash);

        // --- Підсумок ---
        System.out.println("\n" + "=".repeat(65));
        System.out.println("  Підсумок");
        System.out.println("=".repeat(65));
        System.out.printf("  ASCII-сума:        %d колізій%n", col1);
        System.out.printf("  Метод Горнера:     %d колізій%n", col2);
        double improvement = col1 > 0 ? (double) col1 / Math.max(col2, 1) : 0;
        System.out.printf("  Покращення:        у %.1f разів менше колізій%n", improvement);

        // --- Демонстрація на анаграмах ---
        demonstrateAnagramCollisions();

        System.out.println("\n" + "=".repeat(65));
        System.out.println("  Висновок:");
        System.out.println("  ASCII-сума ігнорує порядок символів — анаграми завжди");
        System.out.println("  колізують. Метод Горнера враховує позицію кожного");
        System.out.println("  символу — значно менше колізій на реальних даних.");
        System.out.println("=".repeat(65));
    }
}
