# FastFileSystem Reference Guide

Complete API documentation, memory model specifications, and usage patterns for FastFileSystem.

---

## 1. Core Architecture & Guarantees

* **Unified Storage Pipeline**: Directly links `FastFileIndex` (binary mmap file scan), `FastFileSearch` (in-memory prefix Trie & N-Gram search), and `FastFileWatch` (NTFS USN Journal live stream).
* **Zero-Copy Memory-Mapped Access**: Initial indexing produces a continuous binary buffer that is mapped into memory (`MapViewOfFile`), allowing instant lookups without object hydration.
* **Sub-Microsecond Traversal**: Trie autocompletion and exact match lookups bypass disk reads entirely.
* **Zero-Rescan Incremental Updates**: Background USN Journal watcher captures NTFS volume change records (`USN_RECORD_V2` / `V3`) and updates the search Trie directly.

---

## 2. API Reference

### `FastFileSystem.mount(String... roots)`
* **Description**: Recursively scans and indexes the target roots, constructs the prefix Trie, and attaches real-time NTFS USN Journal monitoring.
* **Parameters**: `roots` - List of directory or drive root paths (e.g. `C:\`, `D:\Projects`).
* **Returns**: An active, live-synchronized `FastFileSystem` instance.

### `searchPrefix(String query, int maxResults)`
* **Description**: Performs a sub-microsecond prefix autocomplete search across all indexed files and directories.
* **Parameters**:
  * `query`: Prefix string to match (e.g. `pom`, `Fast`).
  * `maxResults`: Maximum number of results to return.
* **Returns**: Array of `SearchResult` objects containing matching paths and ranking scores.

### `searchFuzzy(String query, int maxResults)`
* **Description**: Executes an N-gram substring fuzzy search across indexed filenames with fault tolerance.
* **Parameters**:
  * `query`: Substring or fuzzy pattern.
  * `maxResults`: Maximum result limit.
* **Returns**: Array of `SearchResult` matching the fuzzy criteria.

### `searchExact(String filename)`
* **Description**: $O(1)$ exact filename hash map lookup.
* **Parameters**: `filename` - Exact filename with extension.
* **Returns**: Array of exact match `SearchResult` entries.

### `entryCount()`
* **Description**: Returns the total number of files and directories currently held in the live index.

### `isUSNSupported(String volume)`
* **Description**: Verifies if NTFS USN Journal streaming is available on the target storage volume.

---

## 3. Platform & Hardware Support

| Platform | Capabilities | Status |
|---|---|---|
| **Windows 10/11 (x64)** | Full mmap + Win32 USN Journal (`FSCTL_READ_USN_JOURNAL`) | ✅ Fully Supported |
| **Linux (x64 / AArch64)** | Inotify / fanotify tree watcher | 🚧 Planned |
| **macOS (Apple Silicon / x64)** | FSEvents watcher | 🚧 Planned |

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.*