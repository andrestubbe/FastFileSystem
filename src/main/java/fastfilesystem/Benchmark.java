package fastfilesystem;

import fastansi.FastANSI;
import fastfilesearch.SearchResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Multi-Tier Comparative Benchmark Suite for FastFileSystem vs Standard Java Files.walk() / Stream API.
 * Evaluates Full Tree Scan, Autocomplete Prefix Search, and Substring Search with direct Head-to-Head throughput metrics.
 */
public class Benchmark {

    private Benchmark() {}

    private static String darkGray(String text) {
        return FastANSI.fg(240) + text + FastANSI.RESET;
    }

    private static String white(String text) {
        return FastANSI.FG_BRIGHT_WHITE + text + FastANSI.RESET;
    }

    private static String boldWhite(String text) {
        return FastANSI.BOLD + FastANSI.FG_BRIGHT_WHITE + text + FastANSI.RESET;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println(" " + boldWhite("FastFileSystem & FastJava") + darkGray(" — Comprehensive Multi-Tier 120-Column Benchmark Suite"));
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println();

        String projectDir = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();
        if (!new File(projectDir).exists()) {
            projectDir = System.getProperty("user.dir");
        }

        // ─────────────────────────────────────────────────────────────────────
        // Tier 1: Full-Tree Scan & Indexing Benchmark (Files.walk vs mmap Index)
        // ─────────────────────────────────────────────────────────────────────
        System.out.println(darkGray("[Tier 1]") + " " + boldWhite("Filesystem Scan & Ingestion Benchmark") + darkGray(" (Entire Workspace Directory Tree)"));

        // 1a. Standard Java Files.walk()
        long jdkWalkT0 = System.currentTimeMillis();
        List<Path> jdkPaths;
        try (Stream<Path> stream = Files.walk(Path.of(projectDir))) {
            jdkPaths = stream.collect(Collectors.toList());
        }
        long jdkWalkDuration = System.currentTimeMillis() - jdkWalkT0;
        int totalFiles = jdkPaths.size();

        // 1b. FastFileSystem mmap & Trie Ingestion
        long fastMountT0 = System.currentTimeMillis();
        FastFileSystem fs = FastFileSystem.mount(new String[]{ projectDir }, false);
        long fastMountDuration = System.currentTimeMillis() - fastMountT0;

        double scanSpeedup = (double) Math.max(jdkWalkDuration, 1) / Math.max(fastMountDuration, 1);

        System.out.printf("  %s %-32s: %s (%s)\n",
                darkGray("├──"),
                white("Standard Java Files.walk()"),
                boldWhite(String.format("%,d ms", jdkWalkDuration)),
                darkGray(String.format("%,d entries traversed", totalFiles)));
        System.out.printf("  %s %-32s: %s (%s)\n",
                darkGray("├──"),
                boldWhite("FastFileSystem (mmap + Trie)"),
                boldWhite(String.format("%,d ms", fastMountDuration)),
                darkGray(String.format("%,d entries indexed", fs.entryCount())));
        System.out.printf("  %s Speedup                   : %s\n\n",
                darkGray("└──"),
                boldWhite(String.format("%.1fx faster ingestion", scanSpeedup)));

        // ─────────────────────────────────────────────────────────────────────
        // Tier 2: Sub-Microsecond Prefix Autocomplete Search Benchmark
        // ─────────────────────────────────────────────────────────────────────
        System.out.println(darkGray("[Tier 2]") + " " + boldWhite("Prefix Autocomplete Search Benchmark") + darkGray(" (Query: 'Fast', 5,000 Iterations)"));

        int searchIterations = 5000;
        String prefixQuery = "Fast";

        // JDK Linear Stream Search Warmup & Benchmark
        for (int i = 0; i < 500; i++) {
            jdkPaths.stream().filter(p -> p.getFileName() != null && p.getFileName().toString().startsWith(prefixQuery)).limit(50).count();
        }
        long jdkSearchT0 = System.nanoTime();
        long jdkMatchCount = 0;
        for (int i = 0; i < searchIterations; i++) {
            jdkMatchCount += jdkPaths.stream().filter(p -> p.getFileName() != null && p.getFileName().toString().startsWith(prefixQuery)).limit(50).count();
        }
        long jdkSearchNanos = System.nanoTime() - jdkSearchT0;
        double jdkSearchAvgUs = (jdkSearchNanos / 1000.0) / searchIterations;
        double jdkSearchOpsPerMs = searchIterations / (jdkSearchNanos / 1_000_000.0);

        // FastFileSystem Trie Search Warmup & Benchmark
        for (int i = 0; i < 500; i++) {
            fs.searchPrefix(prefixQuery, 50);
        }
        long fastSearchT0 = System.nanoTime();
        long fastMatchCount = 0;
        for (int i = 0; i < searchIterations; i++) {
            fastMatchCount += fs.searchPrefix(prefixQuery, 50).length;
        }
        long fastSearchNanos = System.nanoTime() - fastSearchT0;
        double fastSearchAvgUs = (fastSearchNanos / 1000.0) / searchIterations;
        double fastSearchOpsPerMs = searchIterations / (fastSearchNanos / 1_000_000.0);

        double searchSpeedup = (double) jdkSearchNanos / fastSearchNanos;

        System.out.printf("  %s %-32s: %s | %s\n",
                darkGray("├──"),
                white("Standard Java Stream Filter"),
                boldWhite(String.format("%,8.2f µs/op", jdkSearchAvgUs)),
                darkGray(String.format("%,8.1f ops/ms", jdkSearchOpsPerMs)));
        System.out.printf("  %s %-32s: %s | %s\n",
                darkGray("├──"),
                boldWhite("FastFileSystem Prefix Trie"),
                boldWhite(String.format("%,8.2f µs/op", fastSearchAvgUs)),
                boldWhite(String.format("%,8.1f ops/ms", fastSearchOpsPerMs)));
        System.out.printf("  %s Speedup                   : %s\n\n",
                darkGray("└──"),
                boldWhite(String.format("%.1fx faster autocomplete", searchSpeedup)));

        // ─────────────────────────────────────────────────────────────────────
        // Tier 3: Fuzzy / Substring Matching Benchmark
        // ─────────────────────────────────────────────────────────────────────
        System.out.println(darkGray("[Tier 3]") + " " + boldWhite("Substring & Fuzzy Query Benchmark") + darkGray(" (Query: 'system', 3,000 Iterations)"));

        String subQuery = "system";
        int subIterations = 3000;

        for (int i = 0; i < 300; i++) {
            jdkPaths.stream().filter(p -> p.toString().contains(subQuery)).limit(50).count();
        }
        long jdkSubT0 = System.nanoTime();
        for (int i = 0; i < subIterations; i++) {
            jdkPaths.stream().filter(p -> p.toString().contains(subQuery)).limit(50).count();
        }
        long jdkSubNanos = System.nanoTime() - jdkSubT0;
        double jdkSubAvgUs = (jdkSubNanos / 1000.0) / subIterations;

        for (int i = 0; i < 300; i++) {
            fs.searchFuzzy(subQuery, 50);
        }
        long fastSubT0 = System.nanoTime();
        for (int i = 0; i < subIterations; i++) {
            fs.searchFuzzy(subQuery, 50);
        }
        long fastSubNanos = System.nanoTime() - fastSubT0;
        double fastSubAvgUs = (fastSubNanos / 1000.0) / subIterations;

        double subSpeedup = (double) jdkSubNanos / fastSubNanos;

        System.out.printf("  %s %-32s: %s\n",
                darkGray("├──"),
                white("Standard Java contains()"),
                boldWhite(String.format("%,8.2f µs/op", jdkSubAvgUs)));
        System.out.printf("  %s %-32s: %s\n",
                darkGray("├──"),
                boldWhite("FastFileSystem N-Gram Trie"),
                boldWhite(String.format("%,8.2f µs/op", fastSubAvgUs)));
        System.out.printf("  %s Speedup                   : %s\n\n",
                darkGray("└──"),
                boldWhite(String.format("%.1fx faster substring search", subSpeedup)));

        fs.close();
    }
}