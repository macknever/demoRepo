package com.lawrence.kafka.vertxDemo;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRailExtension
        implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, TestWatcher,
        AfterAllCallback {
    private static final Logger LOG = LoggerFactory.getLogger(TestRailExtension.class);
    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final Pattern TESTCASE_PREFIX = Pattern.compile("([cC])");
    private static final String EMPTY_STRING = "";
    private static final List<String> COLUMN_HEADINGS = List.of("tcmsid", "failure", "note", "version", "time",
            "defects");

    private long startTime;
    private long endTime;
    private final CsvFileWriter csvFileWriter;
    private Map<String, TestResultData> testId2Result;

    public TestRailExtension(final String filename, final Class<?> targetClass) {
        try {
            csvFileWriter = new CsvFileWriter(getFilePath(filename, targetClass), COLUMN_HEADINGS);
        } catch (IOException e) {
            throw new TestRailCsvFileException("Unable to create CSV results file", e);
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        LOG.info("START TEST: {}", extensionContext.getDisplayName());

        AnnotatedElement annotatedElement = extensionContext.getElement().orElse(null);

        if (isNotAnnotated(annotatedElement)) {
            LOG.error("{} is NOT annotated with @TestRail annotation", extensionContext.getDisplayName());
            throw new TestRailAnnotationNotUsedException();
        }

        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        LOG.info("END TEST: {}", extensionContext.getDisplayName());
        endTime = System.currentTimeMillis();
    }

    @Override
    public void testDisabled(ExtensionContext extensionContext, Optional<String> reason) {
        LOG.info("TEST DISABLED: {}", extensionContext.getDisplayName());
        writeResultToMap(TestStatus.POSTPONED, extensionContext);
    }

    @Override
    public void testSuccessful(ExtensionContext extensionContext) {
        LOG.info("TEST SUCCESSFUL: {}", extensionContext.getDisplayName());
        writeResultToMap(TestStatus.PASSED, extensionContext);
    }

    @Override
    public void testAborted(ExtensionContext extensionContext, Throwable throwable) {
        LOG.info("TEST ABORTED: {}", extensionContext.getDisplayName());
        writeResultToMap(TestStatus.ABORTED, extensionContext);
    }

    @Override
    public void testFailed(ExtensionContext extensionContext, Throwable throwable) {
        LOG.info("TEST FAILED: {}", extensionContext.getDisplayName());
        writeResultToMap(TestStatus.FAILED, extensionContext);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        LOG.info("START TEST PLAN: {}", extensionContext.getDisplayName());
        testId2Result = new ConcurrentHashMap<>();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        writeResultsToFile(testId2Result);
        LOG.info("[LAWRENCE EXP] current test plan: {}, and current count of tests: {}", context.getDisplayName(),
                testId2Result.size());
        testId2Result.clear();
    }

    private String formatDurationInSeconds() {
        long duration = Math.abs(endTime - startTime);
        return String.valueOf(duration / 1000.0);
    }

    private void writeResultsToFile(final Map<String, TestResultData> result) {
        try {
            List<String[]> data = result.values().stream().map(TestResultData::toArray).collect(Collectors.toList());
            csvFileWriter.writeToCsvFile(data);
        } catch (IOException e) {
            LOG.error("Could not write results to CSV", e);
        }
    }

    private void writeResultToMap(final TestStatus result, final ExtensionContext extensionContext) {
        Optional<AnnotatedElement> annotatedElement = extensionContext.getElement();

        if (annotatedElement.isPresent()) {
            TestRail testRailAnnotation = annotatedElement.get().getAnnotation(TestRail.class);

            if (testRailAnnotation != null && isNotEmptyOrBlank(testRailAnnotation)) {
                final String testId = testRailAnnotation.id();
                final TestResultData serializedResult =
                        serializeData(testRailAnnotation, result, extensionContext);
                // Parameterized test will cause duplicated testIds
                if (testId2Result.containsKey(testId)) {
                    testId2Result.merge(testId, serializedResult, TestResultData::merge);
                } else {
                    testId2Result.put(testId, serializedResult);
                }
            }
        }
    }

    private boolean isNotAnnotated(final AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return true;
        } else {
            return !AnnotationSupport.isAnnotated(annotatedElement, TestRail.class);
        }
    }

    private boolean isNotEmptyOrBlank(final TestRail testRailAnnotation) {
        return !(testRailAnnotation.id() == null || StringUtils.isBlank(testRailAnnotation.id()));
    }

    private TestResultData serializeData(final TestRail testRailAnnotation, final TestStatus result,
            final ExtensionContext extensionContext) {
        if (LOG.isInfoEnabled()) {
            LOG.info("TestRail ID = {}", testRailAnnotation.id());
        }

        TestResultData testResultData = new TestResultData();
        testResultData.setTestrailId(sanitizeTestRailId(testRailAnnotation.id()));
        testResultData.setResultName(result.name());
        testResultData.setExceptionMsg(getExceptionMessage(extensionContext));
        testResultData.setAnnotatedVersion(getParameter(testRailAnnotation.version()));
        testResultData.setDuration(formatDurationInSeconds());
        testResultData.setDefects(testRailAnnotation.defects());

        return testResultData;
    }

    /**
     * Get the path to the CSV file by passing in a filename and a target class
     * to where the CSV file will be created.
     */
    private String getFilePath(final String filename, final Class<?> clazz) {
        File targetClassesDir = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        return createFilePathName(targetClassesDir.getParentFile().getPath(), filename);
    }

    private String createFilePathName(final String parentPath, final String filename) {
        return parentPath + File.separator + filename + CSV_FILE_EXTENSION;
    }

    private String getExceptionMessage(final ExtensionContext extensionContext) {
        Optional<Throwable> exception = extensionContext.getExecutionException();
        return exception.map(Throwable::getLocalizedMessage).orElse(null);
    }

    private String getParameter(final String parameter) {
        if (parameter == null) {
            return null;
        } else {
            return parameter.isEmpty() ? null : parameter;
        }
    }

    /**
     * Since the CSV file for the TestRail importer tool needs the testRailId without a prefix letter,
     * it is removed and trimmed.
     */
    private String sanitizeTestRailId(final String testRailId) {
        return TESTCASE_PREFIX.matcher(testRailId).replaceAll(EMPTY_STRING);
    }

}
