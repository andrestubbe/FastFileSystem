package fastfilesystem.benchmark;

import fastfilesystem.FastFileSystem;
import fastfilesearch.SearchResult;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Standard OpenJDK JMH Benchmark Suite for FastFileSystem vs Standard Java Stream API.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class FastFileSystemJmhBenchmark {

    private FastFileSystem fileSystem;
    private List<Path> jdkPaths;
    private static final String PREFIX_QUERY = "Fast";
    private static final String FUZZY_QUERY = "system";

    @Setup
    public void setup() throws IOException {
        String projectDir = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();
        if (!new File(projectDir).exists()) {
            projectDir = System.getProperty("user.dir");
        }

        // Initialize FastFileSystem
        fileSystem = FastFileSystem.mount(new String[]{ projectDir }, false);

        // Preload JDK paths for search comparison
        try (Stream<Path> stream = Files.walk(Path.of(projectDir))) {
            jdkPaths = stream.collect(Collectors.toList());
        }
    }

    @TearDown
    public void tearDown() {
        if (fileSystem != null) {
            fileSystem.close();
        }
    }

    @Benchmark
    public SearchResult[] benchmarkFastFileSystemPrefixSearch() {
        return fileSystem.searchPrefix(PREFIX_QUERY, 50);
    }

    @Benchmark
    public long benchmarkJdkPrefixFilter() {
        return jdkPaths.stream()
                .filter(p -> p.getFileName() != null && p.getFileName().toString().startsWith(PREFIX_QUERY))
                .limit(50)
                .count();
    }

    @Benchmark
    public SearchResult[] benchmarkFastFileSystemFuzzySearch() {
        return fileSystem.searchFuzzy(FUZZY_QUERY, 50);
    }

    @Benchmark
    public long benchmarkJdkSubstringFilter() {
        return jdkPaths.stream()
                .filter(p -> p.toString().contains(FUZZY_QUERY))
                .limit(50)
                .count();
    }
}