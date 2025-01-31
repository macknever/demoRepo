# UC MDS Cache Performance Tests

These tests are intended to verify UC MDS Cache's performance in isolation, without any external
dependencies. Any external dependencies are mocked.

## How To Run

You can run the tests either from the command line with Maven or within IntelliJ IDEA.

### Run Using Maven (Command Line)

> **NOTE:** You should run the maven command from the performance test module folder to limit the scope to only
> performance tests, otherwise you will run all the unit tests in all other modules of the project.

#### Test Selection

In order to run the tests from the command line with maven, you MUST activate the specific profile based on which tests
you want to run. For example, to run all tests, specify the `-Pperformance-test-all` parameter.

Example:
````sh
$ mvn verify -pl :mds-cache-performance-tests -Pperformance-test-all
````

To run a specific performance test (from the com.globalrelay.mdscache.performance.simulations package), you MUST
activate the single test profile with `-Pperformance-test-one` and provide the simulation class name with
`-Dgatling.simulationClass=<simulation_class_name>`.

Example:
````sh
$ mvn verify -pl :mds-cache-performance-tests -Pperformance-test-one -Dgatling.simulationClass=GetVersion
````

#### Test Configuration

You MAY specify a test configuration file by providing a `-Dconfig.path=<path>/application-<environment_name>.conf` parameter as
a system property. All available configuration files are located under the 'src/test/resources' directory.
The environment specific files contain overridden values from the default 'application.conf' file.

> **NOTE:** The path to the configuration must either be absolute or relative to the project root.

Example:
````sh
$ mvn verify -pl :mds-cache-performance-tests -Pperformance-test-all -Dtesting.target.env=perf -Dconfig.path=./mds-cache-tests/mds-cache-performance-tests/src/test/resources/application-perf.conf
````

> **NOTE:** If the '-Dconfig.path' parameter isn't provided, it will default to using the 'application.conf' which
> contains default configuration for a local environment.

Also, you MAY override any of the values in the default 'application.conf' file by passing a system property that
matches its JSON path, such as `-Dconfig.simulation.getVersion.rate=10` for example. This way you can change the
load of the performance test at execution time.

Example:
````sh
$ mvn verify -pl :mds-cache-performance-tests -Pperformance-test-one -Dgatling.simulationClass=GetVersion -Dconfig.simulation.getVersion.rate=10
````

### Running Within IntelliJ IDEA

You will need to create a run configuration to run the tests in the performance tests module and provide the desired
options as described above.

A basic run configuration can be created, that can then be customized, by following these steps:
* Navigate to src/test/scala/com/globalrelay/mds-cache/performance/Main
* Right-click on the Main class, then select "Run Main..." (or similar)
    * You may want to edit your run configuration to override configuration properties as described above
* You will then be prompted with all possible test simulations for which you need to type in the number of the desired
  simulation to run, and press Enter
    * Optionally, you can also hard code a specific simulation in the Main class if you want to run a particular simulation only:
  ````scala
  val properties = new GatlingPropertiesBuilder()
    ...
    .simulationClass("com.globalrelay.mdscache.performance.simulation.GetVersion");
  ````
