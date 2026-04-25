# FastFileSystem

[![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![JNI](https://img.shields.io/badge/JNI-Native-orange.svg)]()
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## About

FastFileSystem is a Java-native file search engine that combines high-performance C++ indexing, searching, and real-time file monitoring through JNI bindings. Designed for Java applications requiring Everything-style file search capabilities, it provides zero-copy memory-mapped access and incremental updates via USN Journal on Windows. The native C++ core delivers maximum performance while the Java API offers seamless integration with JVM applications.

**Tags:** java, jni, file-search, filesystem, indexing, mmap, usn-journal, cpp, windows, autocomplete, fuzzy-search

## Overview

FastFileSystem is a Java library that provides a unified API for high-performance file search. It uses native C++ code (via JNI) to handle indexing, searching, and real-time file monitoring, delivering native performance while maintaining a clean Java interface. The library integrates FastFileIndex, FastFileSearch, and FastFileWatch into a cohesive Java API for FastJava applications.

## High-Level Architecture

Your three modules form a complete Everything-style file search engine:

- **FastFileIndex** - Full filesystem scan → produces a binary, mmap-capable index of all files.
- **FastFileSearch** - Builds Prefix Trie, N-Gram index, Exact Match map, and Ranking engine on top of the index.
- **FastFileWatch** - Uses USN Journal to keep the index + search structures live-updated with zero rescans.

This is exactly the architecture used by Everything, Spotlight, VSCode, and fsearch — but modular and embeddable.

## Features

- Java library with native C++ core for maximum performance
- JNI bindings with zero-copy (DirectByteBuffer + Offsets instead of String[])
- Java reads directly from mmap without copies
- Unified Java API combining Index + Search + Watch modules
- Full mmap-based index access (no std::vector<FileEntry> primary structure)
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
fs.loadIndex("files.idx");

SearchResult[] results = fs.search("fast", 64);

for (SearchResult result : results) {
    System.out.println(result.path);
}

fs.close();
```

## Platform Support

- **Windows 10+** (x86_64) - Fully supported with native implementation
- **Linux** - Planned
- **macOS** - Planned

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Related Projects

- [FastFileIndex](https://github.com/andrestubbe/FastFileIndex) - Binary file indexing with mmap support
- [FastFileSearch](https://github.com/andrestubbe/FastFileSearch) - Prefix Trie, N-Gram index, and Ranking engine
- [FastFileWatch](https://github.com/andrestubbe/FastFileWatch) - USN Journal-based live file monitoring
- [FastCore](https://github.com/andrestubbe/FastCore) - Unified JNI loader and platform abstraction
