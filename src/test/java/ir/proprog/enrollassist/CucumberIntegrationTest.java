package ir.proprog.enrollassist;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@CucumberContextConfiguration
public class CucumberIntegrationTest {
}
