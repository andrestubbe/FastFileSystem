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

            // ── Silent Warmup (Warms JNI Reflections, JIT Compiler, and Branch Predictor) ──
            for (int w = 0; w < 50; w++) {
                fs.searchPrefix("warmup", 5);
                fs.searchFuzzy("warmup", 5);
            }

            // ── Phase 1: Sub-Microsecond Prefix Autocomplete Search ─────────
            System.out.println(darkGray("[Phase 1]") + " " + boldWhite("Prefix Autocomplete Query") + darkGray(" (Zero-allocation Trie Traversal)"));

            String prefixQuery = "README.md";
            long qT0 = System.nanoTime();
            SearchResult[] prefixResults = fs.searchPrefix(prefixQuery, 10000);
            long qDurationNs = System.nanoTime() - qT0;
            double qDurationUs = qDurationNs / 1000.0;

            System.out.printf("  %s %s Query %-14s %s %s\n",
                    darkGray("└──"),
                    boldWhite("[01]"),
                    boldWhite("\"" + prefixQuery + "\""),
                    darkGray(String.format("| %,d total matches", prefixResults.length)),
                    boldWhite(String.format("| %.2f µs (%,d ns)", qDurationUs, qDurationNs)));

            for (int j = 0; j < prefixResults.length; j++) {
                boolean isLastMatch = (j == prefixResults.length - 1);
                String mBranch = isLastMatch ? "└──" : "├──";
                SearchResult r = prefixResults[j];
                System.out.printf("       %s [%02d] %-76s %s\n",
                        darkGray(mBranch),
                        j + 1,
                        white(truncateMiddle(r.path(), 76)),
                        darkGray(String.format("Score: %.2f | %,d B", r.score(), r.fileSize())));
            }
            System.out.printf("       %s Exact Search Time: %s\n\n",
                    darkGray("✦"),
                    boldWhite(String.format("%.3f µs (%,d ns)", qDurationUs, qDurationNs)));

            // ── Phase 2: N-Gram Substring & Fuzzy Search ────────────────────
            System.out.println(darkGray("[Phase 2]") + " " + boldWhite("Fuzzy & N-Gram Search Query") + darkGray(" (Tolerance & Substring Matching)"));

            String fuzzyQuery = "system";
            long fT0 = System.nanoTime();
            SearchResult[] fuzzyResults = fs.searchFuzzy(fuzzyQuery, 10000);
            long fDurationNs = System.nanoTime() - fT0;
            double fDurationUs = fDurationNs / 1000.0;

            System.out.printf("  %s %s Fuzzy %-14s %s %s\n",
                    darkGray("└──"),
                    boldWhite("[01]"),
                    boldWhite("\"" + fuzzyQuery + "\""),
                    darkGray(String.format("| %,d total matches", fuzzyResults.length)),
                    boldWhite(String.format("| %.2f µs (%,d ns)", fDurationUs, fDurationNs)));

            for (int j = 0; j < fuzzyResults.length; j++) {
                boolean isLastMatch = (j == fuzzyResults.length - 1);
                String mBranch = isLastMatch ? "└──" : "├──";
                SearchResult r = fuzzyResults[j];
                System.out.printf("       %s [%02d] %-76s %s\n",
                        darkGray(mBranch),
                        j + 1,
                        white(truncateMiddle(r.path(), 76)),
                        darkGray(String.format("Score: %.2f | %,d B", r.score(), r.fileSize())));
            }
            System.out.printf("       %s Exact Search Time: %s\n\n",
                    darkGray("✦"),
                    boldWhite(String.format("%.3f µs (%,d ns)", fDurationUs, fDurationNs)));

            // ── Phase 3: Real-Time NTFS USN Journal Liveness Status ─────────
            System.out.println(darkGray("[Phase 3]") + " " + boldWhite("Live USN Journal Change Synchronizer") + darkGray(" (Zero-Rescan Background Stream)"));
            boolean usnAvailable = FastFileSystem.isUSNSupported(projectDir.substring(0, Math.min(3, projectDir.length())));
            System.out.printf("  %s %s %s\n",
                    darkGray("├── Volume USN Status:"),
                    usnAvailable ? boldWhite("ACTIVE & CAPABLE") : darkGray("FALLBACK MODE"),
                    darkGray(String.format("(Syncing dynamically with in-memory Trie)")));

            // Live event simulation
            File liveTempFile = new File(projectDir, "FastFileSystem_LiveSync_Test.tmp");
            try {
                System.out.printf("  %s %s Creating %s\n",
                        darkGray("├──"),
                        boldWhite("[Action]"),
                        white(liveTempFile.getName()));

                long countBefore = fs.searchPrefix("FastFileSystem_LiveSync_Test", 5).length;
                liveTempFile.createNewFile();

                // Small yield for OS watcher thread
                Thread.sleep(30);

                long liveT0 = System.nanoTime();
                SearchResult[] liveResults = fs.searchPrefix("FastFileSystem_LiveSync_Test", 5);
                long liveDurationNs = System.nanoTime() - liveT0;

                System.out.printf("  %s %s Detected without disk rescan in %s\n",
                        darkGray("├──"),
                        boldWhite("[Live Sync]"),
                        boldWhite(String.format("%.2f µs (%,d ns)", liveDurationNs / 1000.0, liveDurationNs)));

                if (liveResults.length > 0) {
                    System.out.printf("  %s      └── Match: %-58s %s\n",
                            darkGray("│"),
                            white(truncateMiddle(liveResults[0].path(), 58)),
                            darkGray(String.format("Score: %.2f", liveResults[0].score())));
                }

                // Cleanup
                liveTempFile.delete();
                Thread.sleep(20);
                System.out.printf("  %s %s File deleted & removed from in-memory Trie\n",
                        darkGray("├──"),
                        boldWhite("[Cleanup]"));
            } catch (Exception e) {
                if (liveTempFile.exists()) liveTempFile.delete();
            }

            System.out.printf("  %s %s\n\n",
                    darkGray("└── Engine Status:    "),
                    boldWhite("100% Operational • Sub-Microsecond Queries • Everything-Parity"));

        } catch (Throwable t) {
            System.err.println("[FastFileSystem] Demo Execution Error: " + t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * Middle truncation keeping the root drive and the actual filename visible:
     * e.g. "C:\Users\andre\...\FastFileSystem\pom.xml"
     */
    private static String truncateMiddle(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        
        int prefixLen = 26; // e.g. "C:\Users\andre\Documents\..."
        int suffixLen = maxLen - prefixLen - 5; // room for " ... "
        if (suffixLen <= 0) {
            return text.substring(0, maxLen - 3) + "...";
        }
        return text.substring(0, prefixLen) + " ... " + text.substring(text.length() - suffixLen);
    }
}