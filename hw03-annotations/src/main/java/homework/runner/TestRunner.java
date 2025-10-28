package homework.runner;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    private TestRunner() {}

    public static void run(Class<?> testClass)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        PrintService.printStartTest(testClass.getName());
        List<Method> beforeMethods = new ArrayList<>();
        List<TestWithResult> testMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();

        scanMethods(testClass, beforeMethods, testMethods, afterMethods);
        invokeTestMethods(testClass, beforeMethods, testMethods, afterMethods);
        getStatistics(testMethods);
    }

    private static void scanMethods(
            Class<?> testClass,
            List<Method> beforeMethods,
            List<TestWithResult> testMethods,
            List<Method> afterMethods) {
        for (Method method : testClass.getDeclaredMethods()) {
            var annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Before) {
                    beforeMethods.add(method);
                }
                if (annotation instanceof Test) {
                    testMethods.add(new TestWithResult(method));
                }
                if (annotation instanceof After) {
                    afterMethods.add(method);
                }
            }
        }
    }

    private static void invokeMethods(List<Method> methods, Object instance)
            throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            method.invoke(instance);
        }
    }

    private static void invokeTestMethods(
            Class<?> testClass, List<Method> beforeMethods, List<TestWithResult> testMethods, List<Method> afterMethods)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (TestWithResult test : testMethods) {
            var instance = testClass.getDeclaredConstructor().newInstance();
            try {
                invokeMethods(beforeMethods, instance);
                var testMethod = test.getTestMethod();
                PrintService.printTestRunning(testMethod.getName());
                testMethod.invoke(instance);
            } catch (InvocationTargetException e) {
                failTestAndRunAfterMethods(test, afterMethods, instance, e);
                continue;
            }
            try {
                invokeMethods(afterMethods, instance);
                test.setResult(TestResult.PASSED);
            } catch (InvocationTargetException e) {
                failTest(test, e);
            }
        }
    }

    private static void failTestAndRunAfterMethods(
            TestWithResult test, List<Method> afterMethods, Object instance, InvocationTargetException e) {
        PrintService.printError(e.getCause());
        test.setResult(TestResult.FAILED);
        try {
            invokeMethods(afterMethods, instance);
        } catch (Exception e1) {
            PrintService.printError(e1.getCause());
        }
    }

    private static void failTest(TestWithResult test, InvocationTargetException e) {
        PrintService.printError(e.getCause());
        test.setResult(TestResult.FAILED);
    }

    private static void getStatistics(List<TestWithResult> testMethods) {
        int passed = 0;
        int failed = 0;
        for (TestWithResult test : testMethods) {
            var testResult = test.getResult();
            if (testResult == TestResult.PASSED) {
                passed++;
            }
            if (testResult == TestResult.FAILED) {
                failed++;
            }
        }
        PrintService.printStatistics(testMethods.size(), passed, failed);
    }

    private static class TestWithResult {

        private final Method testMethod;

        private TestResult result;

        public TestWithResult(Method testMethod) {
            this.testMethod = testMethod;
            this.result = TestResult.NOT_RUN;
        }

        public void setResult(TestResult result) {
            this.result = result;
        }

        public Method getTestMethod() {
            return testMethod;
        }

        public TestResult getResult() {
            return result;
        }
    }

    private enum TestResult {
        PASSED,
        FAILED,
        NOT_RUN
    }
}
