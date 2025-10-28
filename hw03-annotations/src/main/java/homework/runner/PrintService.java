package homework.runner;

import org.apache.commons.lang3.exception.ExceptionUtils;

@SuppressWarnings("java:S106")
class PrintService {

    private static final String TEST_SEPARATOR = "-----------------------------------------------------";

    private PrintService() {}

    static void printStartTest(String name) {
        System.out.println(TEST_SEPARATOR);
        System.out.println("Tests " + name + " running");
    }

    static void printTestRunning(String testMethodName) {
        System.out.println("Running test " + testMethodName + "...");
    }

    static void printError(Throwable throwable) {
        var stackTrace = ExceptionUtils.getStackTrace(throwable);
        System.out.println(stackTrace);
    }

    static void printStatistics(int total, int passed, int failed) {
        System.out.printf("%nTEST RESULTS: executed = %d, passed = %d, failed = %d%n", total, passed, failed);
        System.out.println(TEST_SEPARATOR);
    }
}
