package transactionservice.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import transactionservice.tests.exporter.AllExporterTests;
import transactionservice.tests.service.AllServiceTests;

/**
 * @author Ekaterina Lobanova
 */
@RunWith(Suite.class)
@SuiteClasses({ AllExporterTests.class, AllServiceTests.class })
public class AllTests {

}
