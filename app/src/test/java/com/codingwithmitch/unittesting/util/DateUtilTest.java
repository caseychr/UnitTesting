package com.codingwithmitch.unittesting.util;

import static com.codingwithmitch.unittesting.util.DateUtil.GET_MONTH_ERROR;
import static com.codingwithmitch.unittesting.util.DateUtil.getMonthFromNumber;
import static com.codingwithmitch.unittesting.util.DateUtil.monthNumbers;
import static com.codingwithmitch.unittesting.util.DateUtil.months;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

/**
 * JUNIT5 user guide
 * https://junit.org/junit5/docs/current/user-guide/
 */
public class DateUtilTest {
    public static final String TODAY = "09-2019";

    @Test
    public void getCurrentTimeStamp_returnTimeStamp() {
        /**
         * Executable is like a packaged test that needs to be executed
         */
        Assertions.assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                assertEquals(TODAY, DateUtil.getCurrentTimeStamp());
                System.out.println("Timestamp is generated correctly");
            }
        });
    }

    /**
     * Parameterized Tests -> feed it an array of objects to a method and watch it test each element in the array
     *
     * TestInfo & TestReporter are objects that get passed through each parameter as they're run and return information
     * about each test
     */
    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11})
    public void getMonthFromNumber_returnSuccess(int monthNumber, TestInfo testInfo, TestReporter testReporter) {
        assertEquals(months[monthNumber], getMonthFromNumber(monthNumbers[monthNumber]));
        System.out.println(monthNumbers[monthNumber]+" : "+months[monthNumber]);
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10,11})
    public void testGetMonthFromNumber_returnError(int monthNumber, TestInfo testInfo, TestReporter testReporter) {
        int randomInt = new Random().nextInt(90)+13;
        assertEquals(getMonthFromNumber(String.valueOf(monthNumber*randomInt)), GET_MONTH_ERROR);
        System.out.println(monthNumbers[monthNumber]+" : "+GET_MONTH_ERROR);
    }
}
