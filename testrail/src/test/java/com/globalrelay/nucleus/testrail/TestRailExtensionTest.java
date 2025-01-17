package com.globalrelay.nucleus.testrail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestRailExtensionTest {
    private static final String TEST_RAIL_ID = "1234";
    private static final String VERSION = "1.0";
    private static final String DEFECTS_LABEL = "NUC-1234, NUC-4321";
    private static final String TEST_DURATION_IN_SECONDS = "1.234";

    private final String fileName = this.getClass().getSimpleName();

    @Mock
    private ExtensionContext extensionContext;
    @Mock
    private CsvFileWriter csvFileWriter;
    @Mock
    private AnnotatedElement annotatedElement;
    @Mock
    private TestRail testRail;
    @Mock
    private Map<String, TestResultData> testId2Result;

    @Captor
    private ArgumentCaptor<String> testIdCaptor;

    @Captor
    private ArgumentCaptor<TestResultData> testResultCaptor;

    private TestRailExtension testRailExtension;

    @BeforeEach
    void setup() {
        testRailExtension = new TestRailExtension(fileName, DummyTestClass.class);
    }

    @Nested
    class Instantiation {
        @Test
        void createsCsvFileWriterInstance() {
            try {
                CsvFileWriter result = (CsvFileWriter) FieldUtils.readField(testRailExtension, "csvFileWriter", true);
                assertThat(result).isNotNull();
                String filePath = (String) FieldUtils.readField(result, "filePath", true);
                assertThat(filePath).endsWith(String.format("/target/%s.csv", fileName));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    class BeforeTestExecution {
        @Test
        void throwsExpectedExceptionIfTestIsNotAnnotatedWithTestRailAnnotation() {
            when(extensionContext.getElement()).thenReturn(Optional.empty());
            assertThrows(TestRailAnnotationNotUsedException.class,
                    () -> testRailExtension.beforeTestExecution(extensionContext));
        }

        @Test
        void setsTestStartTimeBeforeTestExecution() {
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            mockAnnotationSupport(() -> {
                try {
                    long result = (long) FieldUtils.readField(testRailExtension, "startTime", true);
                    assertThat(result).isNotZero();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        private void mockAnnotationSupport(Runnable assertions) {
            try (MockedStatic<AnnotationSupport> annotationSupportMockedStatic = Mockito.mockStatic(
                    AnnotationSupport.class)) {

                annotationSupportMockedStatic.when(
                        () -> AnnotationSupport.isAnnotated(annotatedElement, TestRail.class)).thenReturn(true);

                testRailExtension.beforeTestExecution(extensionContext);

                assertions.run();
            }
        }
    }

    @Nested
    class AfterTestExecution {
        @Test
        void setsEndTimeAfterExecution() throws Exception {
            testRailExtension.afterTestExecution(extensionContext);
            long result = (long) FieldUtils.readField(testRailExtension, "endTime", true);
            assertThat(result).isNotZero();
        }
    }

    @Nested
    class TestDisabled {
        @BeforeEach
        void setup() throws Exception {
            setupTestRailExtension();
        }

        @Test
        void writesCorrectDataToTestId2ResultMap() throws Exception {
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            when(annotatedElement.getAnnotation(TestRail.class)).thenReturn(testRail);
            setupTestRailMock();

            testRailExtension.testDisabled(extensionContext, Optional.empty());
            verify(testId2Result).put(testIdCaptor.capture(), testResultCaptor.capture());

            String testId = testIdCaptor.getValue();
            TestResultData results = testResultCaptor.getValue();

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(testId).isEqualTo("C" + TEST_RAIL_ID);
                softly.assertThat(results.getTestrailId()).isEqualTo(TEST_RAIL_ID);
                softly.assertThat(results.getResultName()).isEqualTo(TestStatus.POSTPONED.name());
                softly.assertThat(results.getExceptionMsg()).isNull();
                softly.assertThat(results.getAnnotatedVersion()).isEqualTo(VERSION);
                softly.assertThat(results.getDuration()).isEqualTo(TEST_DURATION_IN_SECONDS);
                softly.assertThat(results.getDefects()).isEqualTo(DEFECTS_LABEL);
            });
        }
    }

    @Nested
    class TestSuccessful {
        @BeforeEach
        void setup() throws Exception {
            setupTestRailExtension();
        }

        @Test
        void writesCorrectDataToCsvFileWriter() throws Exception {
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            when(annotatedElement.getAnnotation(TestRail.class)).thenReturn(testRail);
            setupTestRailMock();

            testRailExtension.testSuccessful(extensionContext);
            verify(testId2Result).put(testIdCaptor.capture(), testResultCaptor.capture());

            TestResultData results = testResultCaptor.getValue();

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(results.getTestrailId()).isEqualTo(TEST_RAIL_ID);
                softly.assertThat(results.getResultName()).isEqualTo(TestStatus.PASSED.name());
                softly.assertThat(results.getExceptionMsg()).isNull();
                softly.assertThat(results.getAnnotatedVersion()).isEqualTo(VERSION);
                softly.assertThat(results.getDuration()).isEqualTo(TEST_DURATION_IN_SECONDS);
                softly.assertThat(results.getDefects()).isEqualTo(DEFECTS_LABEL);
            });
        }
    }

    @Nested
    class TestAborted {
        @BeforeEach
        void setup() throws Exception {
            setupTestRailExtension();
        }

        @Test
        void writesCorrectDataToCsvFileWriter() throws Exception {
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            when(annotatedElement.getAnnotation(TestRail.class)).thenReturn(testRail);
            setupTestRailMock();

            testRailExtension.testAborted(extensionContext, new Exception());

            verify(testId2Result).put(testIdCaptor.capture(), testResultCaptor.capture());

            String testId = testIdCaptor.getValue();
            TestResultData results = testResultCaptor.getValue();

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(testId).isEqualTo("C" + TEST_RAIL_ID);
                softly.assertThat(results.getTestrailId()).isEqualTo(TEST_RAIL_ID);
                softly.assertThat(results.getResultName()).isEqualTo(TestStatus.ABORTED.name());
                softly.assertThat(results.getExceptionMsg()).isNull();
                softly.assertThat(results.getAnnotatedVersion()).isEqualTo(VERSION);
                softly.assertThat(results.getDuration()).isEqualTo(TEST_DURATION_IN_SECONDS);
                softly.assertThat(results.getDefects()).isEqualTo(DEFECTS_LABEL);
            });
        }

    }

    @Nested
    class TestFailed {
        private static final String ERROR_MESSAGE = "errorMessage";

        @BeforeEach
        void setup() throws Exception {
            setupTestRailExtension();
        }

        @Test
        void writesCorrectDataToCsvFileWriter() {
            Throwable throwable = new Exception(ERROR_MESSAGE);
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            when(extensionContext.getExecutionException()).thenReturn(Optional.of(throwable));
            when(annotatedElement.getAnnotation(TestRail.class)).thenReturn(testRail);
            setupTestRailMock();

            testRailExtension.testFailed(extensionContext, new Exception());

            verify(testId2Result).put(testIdCaptor.capture(), testResultCaptor.capture());

            String testId = testIdCaptor.getValue();
            TestResultData results = testResultCaptor.getValue();

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(testId).isEqualTo("C" + TEST_RAIL_ID);
                softly.assertThat(results.getTestrailId()).isEqualTo(TEST_RAIL_ID);
                softly.assertThat(results.getResultName()).isEqualTo(TestStatus.FAILED.name());
                softly.assertThat(results.getExceptionMsg()).isEqualTo(ERROR_MESSAGE);
                softly.assertThat(results.getAnnotatedVersion()).isEqualTo(VERSION);
                softly.assertThat(results.getDuration()).isEqualTo(TEST_DURATION_IN_SECONDS);
                softly.assertThat(results.getDefects()).isEqualTo(DEFECTS_LABEL);
            });
        }
    }

    @Nested
    class MiscTests {
        @BeforeEach
        void setup() throws IllegalAccessException {
            setupTestRailExtension();
        }

        @Test
        void doesNotWriteToCsvFileIfAnnotationIsNotPresent() throws IOException {
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            when(annotatedElement.getAnnotation(TestRail.class)).thenReturn(null);

            testRailExtension.testSuccessful(extensionContext);

            verify(csvFileWriter, never()).writeToCsvFile(any());
        }

        @Test
        void doesNotWriteToCsvFileIfTestRailIdIsNotPresent() throws IOException {
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            when(annotatedElement.getAnnotation(TestRail.class)).thenReturn(testRail);

            testRailExtension.testSuccessful(extensionContext);

            verify(csvFileWriter, never()).writeToCsvFile(any());
        }

        @Test
        void doesNotWriteToCsvFileIfTestRailIdIsBlank() throws IOException {
            when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
            when(annotatedElement.getAnnotation(TestRail.class)).thenReturn(testRail);
            when(testRail.id()).thenReturn("");

            testRailExtension.testSuccessful(extensionContext);

            verify(csvFileWriter, never()).writeToCsvFile(any());
        }
    }

    private void setupTestRailExtension() throws IllegalAccessException {
        final long endTime = System.currentTimeMillis();
        final long startTime = endTime - (long) (Double.parseDouble(TEST_DURATION_IN_SECONDS) * 1000L);

        FieldUtils.writeField(testRailExtension, "csvFileWriter", csvFileWriter, true);
        FieldUtils.writeField(testRailExtension, "startTime", startTime, true);
        FieldUtils.writeField(testRailExtension, "endTime", endTime, true);
        FieldUtils.writeField(testRailExtension, "testId2Result", testId2Result, true);
    }

    private void setupTestRailMock() {
        when(testRail.id()).thenReturn("C" + TEST_RAIL_ID);
        when(testRail.version()).thenReturn(VERSION);
        when(testRail.defects()).thenReturn(DEFECTS_LABEL);
    }

    private static class DummyTestClass {
    }
}
