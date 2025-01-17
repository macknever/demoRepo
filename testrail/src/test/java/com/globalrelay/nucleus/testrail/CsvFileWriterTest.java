package com.globalrelay.nucleus.testrail;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.io.TempDir;

class CsvFileWriterTest {
    private static final List<String> COLUMN_HEADINGS = List.of("heading_1", "heading_2");

    @TempDir
    private Path tempDir;
    private File csvFile;

    @BeforeEach
    void setup(TestInfo testInfo) {
        csvFile = new File(tempDir.resolve(testInfo.getDisplayName()).toString());
    }

    @AfterEach
    void cleanup() {
        if (csvFile.exists()) {
            csvFile.delete();
        }
    }

    @Nested
    class Instantiation {
        @Test
        void createsCsvFileIfDoesNotExist() throws IOException {
            assertThat(csvFile).doesNotExist();

            new CsvFileWriter(csvFile.getAbsolutePath(), COLUMN_HEADINGS);

            assertThat(csvFile).exists();
            assertThat(getFileContents(csvFile)).isEqualTo(
                    "\"" + COLUMN_HEADINGS.get(0) + "\"" + "," + "\"" + COLUMN_HEADINGS.get(1) + "\"" + "\n");
        }

        @Test
        void doesNotOverwriteFileIfAlreadyExists() throws IOException {
            final String initialData =
                    "\"" + COLUMN_HEADINGS.get(0) + "\"" + "," + "\"" + COLUMN_HEADINGS.get(1) + "\"" + "\n";

            writeFileContents(csvFile, initialData);

            assertThat(csvFile).exists();

            new CsvFileWriter(csvFile.getAbsolutePath(), COLUMN_HEADINGS);

            assertThat(csvFile).exists();
            assertThat(getFileContents(csvFile)).isEqualTo(initialData);
        }
    }

    @Nested
    class WriteToCsvFile {
        private static final String DATA_1 = "some";
        private static final String DATA_2 = "data";
        private static final String ADDITIONAL_DATA_1 = "alreadySome";
        private static final String ADDITIONAL_DATA_2 = "alreadyData";
        private CsvFileWriter csvFileWriter;

        @BeforeEach
        void setup() throws IOException {
            csvFileWriter = new CsvFileWriter(csvFile.getAbsolutePath(), COLUMN_HEADINGS);
        }

        @Test
        void writesData() throws IOException {
            final List<String[]> data = new ArrayList<>();
            data.add(new String[]{ DATA_1, DATA_2 });

            csvFileWriter.writeToCsvFile(data);

            assertThat(getFileContents(csvFile)).isEqualTo(
                    "\"" + COLUMN_HEADINGS.get(0) + "\"" + "," + "\"" + COLUMN_HEADINGS.get(1) + "\"" + "\n" + "\"" +
                            DATA_1 + "\"" + "," + "\"" + DATA_2 + "\"" + "\n");
        }

        @Test
        void writesDataMultipleTimes() throws IOException {
            final List<String[]> initialData = new ArrayList<>();
            initialData.add(new String[]{ DATA_1, DATA_2 });

            final List<String[]> additionalData = new ArrayList<>();
            additionalData.add(new String[]{ ADDITIONAL_DATA_1, ADDITIONAL_DATA_2 });

            csvFileWriter.writeToCsvFile(initialData);
            csvFileWriter.writeToCsvFile(additionalData);

            assertThat(getFileContents(csvFile)).isEqualTo(
                    "\"" + COLUMN_HEADINGS.get(0) + "\"" + "," + "\"" + COLUMN_HEADINGS.get(1) + "\"" + "\n" + "\"" +
                            DATA_1 + "\"" + "," + "\"" + DATA_2 + "\"" + "\n" + "\"" + ADDITIONAL_DATA_1 + "\"" + "," +
                            "\"" + ADDITIONAL_DATA_2 + "\"" + "\n");
        }

        @Test
        void concurrentWritesAreSuccessful() throws IOException, InterruptedException {
            final int numberOfThreads = 10;
            final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            final CountDownLatch latch = new CountDownLatch(numberOfThreads);

            IntStream.range(0, numberOfThreads).forEach(i -> executorService.execute(() -> {
                try {
                    csvFileWriter.writeToCsvFile(Collections.singletonList(generateDataForRow(i)));
                    latch.countDown();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
            latch.await();

            List<String[]> expectedContents = new ArrayList<>();
            expectedContents.add(
                    new String[]{ "\"" + COLUMN_HEADINGS.get(0) + "\"", "\"" + COLUMN_HEADINGS.get(1) + "\"" });
            IntStream.range(0, numberOfThreads).forEach(i -> expectedContents.add(
                    Arrays.stream(generateDataForRow(i)).map(x -> String.format("\"%s\"", x)).toArray(String[]::new)));

            assertThat(parseAsListOfArrays(getFileContents(csvFile))).containsExactlyInAnyOrderElementsOf(
                    expectedContents);
        }

        private String[] generateDataForRow(final int rowIndex) {
            return new String[]{ String.format("DATA_%sa", rowIndex), String.format("DATA_%sb", rowIndex) };
        }

        private List<String[]> parseAsListOfArrays(final String str) {
            return Arrays.stream(str.split("\n")).map(line -> line.split(",")).collect(Collectors.toList());
        }
    }

    private String getFileContents(final File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            return sb.toString();
        }
    }

    private void writeFileContents(final File file, final String content) throws IOException {
        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            out.print(content);
        }
    }
}
