package com.globalrelay.nucleus.testrail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

/**
 * Writes test run results into a csv file. File format is according to requirements defined by the
 * <a href="https://wiki.globalrelay.net/x/4yyhBg">testrail-importer tool</a>.
 */
public class CsvFileWriter {
    private static final Logger LOG = LoggerFactory.getLogger(CsvFileWriter.class);

    private final String filePath;
    private final List<String> columnHeadings;

    public CsvFileWriter(final String filePath, final List<String> columnHeadings) throws IOException {
        this.filePath = filePath;
        this.columnHeadings = List.copyOf(columnHeadings);
        createCsvFileIfNotExists(filePath);
    }

    public void writeToCsvFile(final List<String[]> data) throws IOException {
        LOG.info("Writing to CSV file: {}", filePath);
        try (
                final FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8, true);
                final CSVWriter csvWriter = new CSVWriter(fileWriter)
        ) {
            csvWriter.writeAll(data);
        }
    }

    private void createCsvFileIfNotExists(final String filePath) throws IOException {
        if (doesFileNotExist(filePath)) {
            try (
                    final FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8, true);
                    final CSVWriter csvWriter = new CSVWriter(fileWriter)
            ) {
                csvWriter.writeNext(columnHeadings.toArray(new String[0]));
                LOG.info("CSV file created: {}", filePath);
            }
        }
    }

    private boolean doesFileNotExist(final String filePath) {
        File file = new File(filePath);
        return !file.exists();
    }
}
