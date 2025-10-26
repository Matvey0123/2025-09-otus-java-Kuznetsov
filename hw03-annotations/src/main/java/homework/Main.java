package homework;

import homework.runner.TestRunner;
import homework.tests.Test1;
import homework.tests.Test2;
import homework.tests.Test3;
import homework.tests.Test4;

public class Main {
    public static void main(String[] args) throws Exception {
        TestRunner.run(Test1.class);
        TestRunner.run(Test2.class);
        TestRunner.run(Test3.class);
        TestRunner.run(Test4.class);
    }
}
