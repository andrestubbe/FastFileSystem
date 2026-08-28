package fastfilesystem;

import fastfileindex.FileIndex;
import fastfileindex.FileEntry;
import fastfileindex.BuildOptions;
import fastfilesearch.FastFileSearch;
import fastfilesearch.SearchQuery;
import fastfilesearch.SearchOptions;
import fastfilesearch.SearchResult;
import fastfilesearch.SearchBuildOptions;
import fastfilewatch.WatchService;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * FastFileSystem — Unified High-Performance Filesystem Engine for Java.
 * Combines FastFileIndex (mmap scanning), FastFileSearch (Prefix Trie / N-Gram),
 * and FastFileWatch (NTFS USN Journal live synchronization) into a single zero-copy API.
 */
public final class FastFileSystem implements Closeable {

    private final FileIndex index;
    private final FastFileSearch searchEngine;
    private final WatchService watchService;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private FastFileSystem(FileIndex index, FastFileSearch searchEngine, WatchService watchService) {
        this.index = index;
        this.searchEngine = searchEngine;
        this.watchService = watchService;
    }

    /**
     * Mounts and indexes root paths, initializes the search trie, and starts the USN Journal watcher.
     *
     * @param roots Directory or volume root paths (e.g. "C:\\" or "C:\\Projects")
     * @return An active, live-synchronized FastFileSystem instance
     */
    public static FastFileSystem mount(String... roots) {
        return mount(roots, true);
    }

    /**
     * Mounts roots with optional real-time USN Journal watching.
     */
    public static FastFileSystem mount(String[] roots, boolean enableLiveWatch) {
        BuildOptions buildOpts = BuildOptions.defaults();
        FileIndex idx = FileIndex.build(roots, buildOpts);

        SearchBuildOptions searchOpts = SearchBuildOptions.defaults();
        FastFileSearch search = FastFileSearch.fromIndex(idx, searchOpts);

        WatchService watcher = null;
        if (enableLiveWatch) {
            watcher = WatchService.start(roots, update -> {
                if (search != null) {
                    search.applyUpdate(update);
                }
            });
        }

        return new FastFileSystem(idx, search, watcher);
    }

    /**
     * Executes an instant prefix/autocomplete search across all indexed files.
     */
    public SearchResult[] searchPrefix(String query, int maxResults) {
        ensureOpen();
        SearchQuery q = new SearchQuery(query);
        SearchOptions opts = SearchOptions.defaults().limit(maxResults);
        return searchEngine.prefix(q, opts);
    }

    /**
     * Executes a high-speed fuzzy search using character N-Grams.
     */
    public SearchResult[] searchFuzzy(String query, int maxResults) {
        ensureOpen();
        SearchQuery q = new SearchQuery(query);
        SearchOptions opts = SearchOptions.defaults().limit(maxResults);
        return searchEngine.fuzzy(q, opts);
    }

    /**
     * Executes an exact filename match lookup.
     */
    public SearchResult[] searchExact(String filename) {
        ensureOpen();
        SearchQuery q = new SearchQuery(filename);
        SearchOptions opts = SearchOptions.defaults();
        return searchEngine.exact(q, opts);
    }

    /**
     * Returns total number of indexed files and directories.
     */
    public long entryCount() {
        ensureOpen();
        return index.entryCount();
    }

    /**
     * Retrieves file entry by ID.
     */
    public FileEntry getEntry(long id) {
        ensureOpen();
        return index.get(id);
    }

    /**
     * Checks if NTFS USN Journal monitoring is supported on the given volume.
     */
    public static boolean isUSNSupported(String volume) {
        return WatchService.isUSNAvailable(volume);
    }

    private void ensureOpen() {
        if (closed.get()) {
            throw new IllegalStateException("FastFileSystem instance is closed.");
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            if (searchEngine != null) {
                searchEngine.close();
            }
            if (index != null) {
                index.close();
            }
        }
    }
}