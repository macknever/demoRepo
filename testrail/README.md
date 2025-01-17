# TestRail CSV Reporting

## Usage

You must register the TestRailExtension in order to use it.
You will need to provide a test result filename and a target class.

> **Note:** The target class is necessary to locate the class path
> to determine where to create the results file.

For example:

```java
class SampleIT {
    private static String testResultFilename = "test-results";

    @RegisterExtension
    static TestRailExtension testRailExtension = new TestRailExtension(testResultFilename, SampleIT.class);

    @Test
    @TestRail(id = "C1234", version = "1.0", requirement = "NUC-1234", defects = "NUC-4321, NUC-2345")
    void someTest() {
        //...
    }
}
```

## Test Results

Once the tests have run, it will generate a csv result file in:
`target/<testResultFilename>.csv`.

The contents of the result file will be formatted as follows:

```
"tcmsid","failure","note","version","time","defects"
"1234","PASSED",,"1.0","0.015","NUC-4321, NUC-2345"
```

### Parameterized tests

Junit interprets a parameterized test as multiple tests instead of a singular one. Running parameterized tests with
Junit will generate as many results as the number of parameter variations. Results writing strategy will combine those
results into a unified one to be able to publish it into TestRail server.

If the tests are running in parallel, make sure to configure
`junit.jupiter.execution.parallel.mode.classes.default=same_thread`.
This ensures that multiple tests associated with a single parameter set do not run concurrently in separate test plans.
Consequently, the writing strategy remains effective.
