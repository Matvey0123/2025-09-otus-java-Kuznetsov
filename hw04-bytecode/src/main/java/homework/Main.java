package homework;

import java.time.LocalDateTime;

/*
   java -javaagent:loggerDemo.jar -jar loggerDemo.jar
*/
public class Main {
    public static void main(String[] args) {
        var testLogging = new TestLogging();
        testLogging.calculation(5);
        testLogging.calculation(6, 7, "abcd");
        testLogging.calculation('1', (byte) 2, true, (short) 4, 5, 6.0f);
        testLogging.printTestObject(new TestObject("Otus", LocalDateTime.now()));
        testLogging.calculation(1, 2);
    }
}
