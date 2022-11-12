package ru.myitschool.lab23

import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.junit.runners.Suite


@RunWith(Suite::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Suite.SuiteClasses(
    EditPropertyTest::class,
    IntermediateTest::class,
)
class InstrumentedTestSuite {

}
