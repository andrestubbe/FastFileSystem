# FastFileSystem 0.1.0 [ALPHA] — Unified File Search & USN Journal Watch Engine for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastFileSystem/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastFileSystem)

---

**⚡ High-performance unified file search and monitoring engine combining FastFileIndex, FastFileSearch, and FastFileWatch into a single zero-copy Java API.**

FastFileSystem delivers *Everything*-style instantaneous search across millions of files:
- **Instant Initial Scanning**: Zero-copy `mmap` binary index via [FastFileIndex](https://github.com/andrestubbe/FastFileIndex).
- **Sub-Microsecond Querying**: Prefix Trie autocomplete and N-Gram fuzzy search via [FastFileSearch](https://github.com/andrestubbe/FastFileSearch).
- **Zero-Rescan Live Sync**: Continuous background synchronization using the NTFS USN Journal via [FastFileWatch](https://github.com/andrestubbe/FastFileWatch).

---

## Quick Start

```java
import fastfilesystem.FastFileSystem;
import fastfilesearch.SearchResult;

// 1. Mount directory / volume with real-time USN Journal watching
try (FastFileSystem fs = FastFileSystem.mount("C:\\Projects")) {
    System.out.println("Total files indexed: " + fs.entryCount());

    // 2. Instant prefix search
    SearchResult[] results = fs.searchPrefix("pom", 10);
    for (SearchResult r : results) {
        System.out.println("Match: " + r.id() + " (Score: " + r.score() + ")");
    }

    // 3. High-speed fuzzy search
    SearchResult[] fuzzyMatches = fs.searchFuzzy("system", 5);
}
```

---

## Installation

### Option 1: Maven (Recommended)
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastFileSystem</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastFileSystem:0.1.0'
}
```

---

## Architecture

| Component | Responsibility | Tech |
|---|---|---|
| **FastFileIndex** | Full filesystem scan & binary index format | `mmap`, FastPointer |
| **FastFileSearch** | Prefix Trie, N-Gram index, Exact Match map | In-Memory Trie |
| **FastFileWatch** | Real-time file system change detection | NTFS USN Journal |

---

## Platform Support

| Platform | Status |
|---|---|
| Windows 10/11 | ✅ Fully Supported |
| Linux | 🚧 Planned |
| macOS | 🚧 Planned |

---

## License

MIT License — See [LICENSE](LICENSE) for details.