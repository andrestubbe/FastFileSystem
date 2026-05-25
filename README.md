# FastFileSystem v0.1.0 [ALPHA] - Unified file search engine with JNI bindings for Java

[![Status](https://img.shields.io/badge/status-v0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastFileSystem/releases/tag/v0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe)

FastFileSystem is a high-performance Java file search engine that combines indexing, searching, and real-time file
monitoring through JNI bindings to native C++ code. Designed for Java applications requiring Everything-style file
search capabilities, it provides zero-copy memory-mapped access and incremental updates via USN Journal on Windows.

[![FastKeyboard Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

## Overview

FastFileSystem is a unified C++ module that encapsulates FastFileIndex, FastFileSearch, and FastFileWatch into a single
API, along with JNI bindings for FastJava integration.

## High-Level Architecture

Your three modules form a complete Everything-style file search engine:

- **FastFileIndex** - Full filesystem scan → produces a binary, mmap-capable index of all files.
- **FastFileSearch** - Builds Prefix Trie, N-Gram index, Exact Match map, and Ranking engine on top of the index.
- **FastFileWatch** - Uses USN Journal to keep the index + search structures live-updated with zero rescans.

This is exactly the architecture used by Everything, Spotlight, VSCode, and fsearch — but modular and embeddable.

## Features

- Unified C++ module encapsulating Index + Search + Watch
- Full mmap-based index access (no std::vector<FileEntry> primary structure)
- JNI bindings with zero-copy (DirectByteBuffer + Offsets instead of String[])
- Java reads directly from mmap without copies
- Minimalistic, modular, high-performance
- Zero overhead, no copies, no GC stress
- Hot-reload capable (double mmap buffer + atomic pointer swap)

## Combined System Summary

You now have:

- A full filesystem indexer
- A binary, mmap-capable index format
- A high-performance autocomplete engine
- A substring + fuzzy search engine
- A ranking engine
- A real-time file watcher with USN Journal
- A fully incremental search system with zero rescans
- JNI bindings for FastJava
- Unified C++ API

This is a complete, production-grade file search engine — modular, embeddable, and faster than most existing tools.

## Installation

### Building from Source

```bash
git clone https://github.com/andrestubbe/FastFileSystem.git
cd FastFileSystem
mkdir build && cd build
cmake ..
cmake --build .
```

## Usage

### C++ API

```cpp
#include "FastFileSystem.hpp"

FastFileSystem fs;
fs.build({"C:\\"});

std::vector<FileEntry> results;
fs.search("query", 100, results);

fs.recordOpen(results[0].id);
```

### Java API (JNI)

```java
FastFileSystem fs = new FastFileSystem();
fs.

loadIndex("files.idx");

SearchResult[] results = fs.search("fast", 64);

for(
SearchResult result :results){
        System.out.

println(result.path);
}

        fs.

close();
```

---

## Documentation

* **[COMPILE.md](COMPILE.md)**: Full compilation guide (MSVC C++17 build chain + JNI Setup).
* **[REFERENCE.md](REFERENCE.md)**: Full API descriptions, border configurations, and codepoint index.
* **[PHILOSOPHIE.md](PHILOSOPHIE.md)**: The engineering rationale for zero-allocation performance.
* **[ROADMAP.md](ROADMAP.md)**: Future milestones and planned features.

---

## Platform Support

| Platform      | Status            |
|---------------|-------------------|
| Windows 10/11 | ✅ Fully Supported |
| Linux         | 🚧 Planned        |
| macOS         | 🚧 Planned        |

---

## License

MIT License — See [LICENSE](LICENSE) for details.

---

## Related Projects

- [FastFileIndex](https://github.com/andrestubbe/FastFileIndex) - Binary file indexing with mmap support
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) - Prefix Trie, N-Gram index, and Ranking engine
- [FastFileWatch](https://github.com/andrestubbe/FastFileWatch) - USN Journal-based live file monitoring
- [FastCore](https://github.com/andrestubbe/FastCore) - Unified JNI loader and platform abstraction

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
