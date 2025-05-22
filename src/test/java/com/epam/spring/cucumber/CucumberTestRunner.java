package com.epam.spring.cucumber;

import cucumber.api.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@CucumberContextConfiguration
@SelectClasspathResource("features")
@CucumberOptions(glue = "com.epam.spring.cucumber")
public class CucumberTestRunner {
}
