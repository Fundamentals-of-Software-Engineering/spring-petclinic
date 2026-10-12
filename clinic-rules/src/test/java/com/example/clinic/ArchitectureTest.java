package com.example.clinic;

import com.tngtech.archunit.junit.AnalyzeClasses;

/**
 * Your architecture rules go here. See README.md for the two rules to write.
 *
 * ArchUnit imports every compiled class under com.example.clinic and checks each
 * rule you add to this class.
 */
@AnalyzeClasses(packages = "com.example.clinic")
class ArchitectureTest {

    // Rule 1: controllers never talk to repositories directly.

    // Rule 2: the domain depends on nothing else in the app.

    // Stretch: every class named *Repository lives in the repository package.

}
