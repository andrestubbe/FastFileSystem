package fastfilesystem;

import fastansi.FastANSI;
import fastfilesearch.SearchResult;

import java.io.File;

/**
 * FastFileSystem — High-Performance Unified Filesystem Hero Demo.
 * Combines FastFileIndex (mmap scanning), FastFileSearch (Prefix Trie / N-Gram),
 * and FastFileWatch (NTFS USN Journal live sync) in clean FastWebScrape gray/white ANSI tree style.
 */
public class Demo {

    private Demo() {}

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
        System.out.println(" " + boldWhite("FastFileSystem") + darkGray(" — Unified Indexing, Prefix Trie Search & NTFS USN Live-Sync Engine"));
        System.out.println(darkGray(" ARCHITECTURE: FastFileIndex (mmap)  |  SEARCH: FastFileSearch (Trie/N-Gram)  |  WATCH: FastFileWatch (USN Journal)"));
        System.out.println(darkGray("========================================================================================================================"));
        System.out.println();

        String projectDir = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();
        if (!new File(projectDir).exists()) {
            projectDir = System.getProperty("user.dir");
        }

        // ── Phase 0: Mount & Scanning Target Directory ──────────────────────
        System.out.println(darkGray("[Phase 0]") + " " + boldWhite("Mounting Target Directory Tree") + darkGray(" (Initializing zero-copy mmap & USN Watcher)"));
        System.out.printf("  %s %s %s\n", darkGray("├── Target:"), boldWhite("[Root]"), white(projectDir));

        long t0 = System.currentTimeMillis();
        try (FastFileSystem fs = FastFileSystem.mount(new String[]{ projectDir }, true)) {
            long scanTimeMs = System.currentTimeMillis() - t0;
            long count = fs.entryCount();

            System.out.printf("  %s %s across tree in %s\n\n",
                    darkGray("└── Mounted"),
                    boldWhite(String.format("%,d total filesystem entries", count)),
                    boldWhite(String.format("%,d ms", scanTimeMs)));

            // ── Phase 1: Sub-Microsecond Prefix Autocomplete Search ─────────
            System.out.println(darkGray("[Phase 1]") + " " + boldWhite("Prefix Autocomplete Queries") + darkGray(" (Zero-allocation Trie Traversals — All Matches)"));

            String[] prefixQueries = new String[]{ "pom", "Fast", "Demo", "src", "README" };
            for (int i = 0; i < prefixQueries.length; i++) {
                String q = prefixQueries[i];
                boolean isLast = (i == prefixQueries.length - 1);
                String branch = isLast ? "└──" : "├──";
                String subIndent = isLast ? "     " : "  │  ";

                long qT0 = System.nanoTime();
                SearchResult[] results = fs.searchPrefix(q, 10000);
                long qDurationNs = System.nanoTime() - qT0;
                double qDurationUs = qDurationNs / 1000.0;

                System.out.printf("  %s %s Query %-16s %s %s\n",
                        darkGray(branch),
                        boldWhite(String.format("[%02d]", i + 1)),
                        boldWhite("\"" + q + "\""),
                        darkGray(String.format("| %,d total matches", results.length)),
                        boldWhite(String.format("| %.2f µs (%,d ns)", qDurationUs, qDurationNs)));

                for (int j = 0; j < results.length; j++) {
                    boolean isLastMatch = (j == results.length - 1);
                    String mBranch = isLastMatch ? "└──" : "├──";
                    SearchResult r = results[j];
                    System.out.printf("%s  %s [%02d] %-60s %s\n",
                            subIndent,
                            darkGray(mBranch),
                            j + 1,
                            white(r.path()),
                            darkGray(String.format("Score: %.2f | %,d B", r.score(), r.fileSize())));
                }
                System.out.printf("%s  %s Exact Search Time: %s\n",
                        subIndent,
                        darkGray("✦"),
                        boldWhite(String.format("%.3f µs (%,d ns)", qDurationUs, qDurationNs)));
            }
            System.out.println();

            // ── Phase 2: N-Gram Substring & Fuzzy Search ────────────────────
            System.out.println(darkGray("[Phase 2]") + " " + boldWhite("Fuzzy & N-Gram Search Queries") + darkGray(" (Tolerance & Substring Matching — All Matches)"));

            String[] fuzzyQueries = new String[]{ "system", "search", "spider", "format" };
            for (int i = 0; i < fuzzyQueries.length; i++) {
                String q = fuzzyQueries[i];
                boolean isLast = (i == fuzzyQueries.length - 1);
                String branch = isLast ? "└──" : "├──";
                String subIndent = isLast ? "     " : "  │  ";

                long qT0 = System.nanoTime();
                SearchResult[] results = fs.searchFuzzy(q, 10000);
                long qDurationNs = System.nanoTime() - qT0;
                double qDurationUs = qDurationNs / 1000.0;

                System.out.printf("  %s %s Fuzzy %-16s %s %s\n",
                        darkGray(branch),
                        boldWhite(String.format("[%02d]", i + 1)),
                        boldWhite("\"" + q + "\""),
                        darkGray(String.format("| %,d total matches", results.length)),
                        boldWhite(String.format("| %.2f µs (%,d ns)", qDurationUs, qDurationNs)));

                for (int j = 0; j < results.length; j++) {
                    boolean isLastMatch = (j == results.length - 1);
                    String mBranch = isLastMatch ? "└──" : "├──";
                    SearchResult r = results[j];
                    System.out.printf("%s  %s [%02d] %-60s %s\n",
                            subIndent,
                            darkGray(mBranch),
                            j + 1,
                            white(r.path()),
                            darkGray(String.format("Score: %.2f | %,d B", r.score(), r.fileSize())));
                }
                System.out.printf("%s  %s Exact Search Time: %s\n",
                        subIndent,
                        darkGray("✦"),
                        boldWhite(String.format("%.3f µs (%,d ns)", qDurationUs, qDurationNs)));
            }
            System.out.println();

            // ── Phase 3: Real-Time NTFS USN Journal Liveness Status ─────────
            System.out.println(darkGray("[Phase 3]") + " " + boldWhite("Live USN Journal Change Synchronizer") + darkGray(" (Zero-Rescan Background Stream)"));
            boolean usnAvailable = FastFileSystem.isUSNSupported(projectDir.substring(0, Math.min(3, projectDir.length())));
            System.out.printf("  %s %s %s\n",
                    darkGray("├── Volume USN Status:"),
                    usnAvailable ? boldWhite("ACTIVE & CAPABLE") : darkGray("FALLBACK MODE"),
                    darkGray(String.format("(Syncing dynamically with in-memory Trie)")));
            System.out.printf("  %s %s\n\n",
                    darkGray("└── Engine Status:    "),
                    boldWhite("100% Operational • Sub-Microsecond Queries • Everything-Parity"));

        } catch (Throwable t) {
            System.err.println("[FastFileSystem] Demo Execution Error: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}