package fastfilesystem;

import fastansi.FastANSI;
import fastfilesearch.SearchResult;

public class Demo {
    public static void main(String[] args) {
        System.out.println(FastANSI.boldCyan("================================================================"));
        System.out.println(FastANSI.boldWhite(" FastFileSystem — Unified Index, Search & USN Watch Engine"));
        System.out.println(FastANSI.darkGray(" Zero-Copy mmap Index • Prefix Trie • Real-time NTFS USN Stream"));
        System.out.println(FastANSI.boldCyan("================================================================"));
        System.out.println();

        String projectDir = System.getProperty("user.dir");
        System.out.println(FastANSI.darkGray("├── Mounting & scanning directory: ") + FastANSI.boldWhite(projectDir));

        long t0 = System.currentTimeMillis();
        try (FastFileSystem fs = FastFileSystem.mount(new String[]{ projectDir }, true)) {
            long scanTime = System.currentTimeMillis() - t0;
            System.out.println(FastANSI.darkGray("├── Index initialized in: ") + FastANSI.boldGreen(scanTime + " ms"));
            System.out.println(FastANSI.darkGray("├── Total entries indexed: ") + FastANSI.boldWhite(String.valueOf(fs.entryCount())));

            // 1. Prefix Autocomplete Search
            System.out.println(FastANSI.darkGray("│"));
            System.out.println(FastANSI.boldWhite("[Phase 1] Prefix Autocomplete Search: 'pom'"));
            SearchResult[] prefixResults = fs.searchPrefix("pom", 5);
            for (SearchResult res : prefixResults) {
                System.out.println(FastANSI.darkGray("  ├── ID: ") + FastANSI.white(String.valueOf(res.id())) +
                                   FastANSI.darkGray(" | Score: ") + FastANSI.cyan(String.format("%.2f", res.score())));
            }

            // 2. Fuzzy N-Gram Search
            System.out.println(FastANSI.darkGray("│"));
            System.out.println(FastANSI.boldWhite("[Phase 2] Fuzzy Search: 'system'"));
            SearchResult[] fuzzyResults = fs.searchFuzzy("system", 5);
            for (SearchResult res : fuzzyResults) {
                System.out.println(FastANSI.darkGray("  ├── ID: ") + FastANSI.white(String.valueOf(res.id())) +
                                   FastANSI.darkGray(" | Score: ") + FastANSI.cyan(String.format("%.2f", res.score())));
            }

            System.out.println(FastANSI.darkGray("│"));
            System.out.println(FastANSI.boldGreen("└── FastFileSystem Operational & Live-Synchronized via USN Journal ✅"));
        } catch (Throwable t) {
            System.err.println("[FastFileSystem] Demo Error: " + t.getMessage());
            t.printStackTrace();
        }
    }
}