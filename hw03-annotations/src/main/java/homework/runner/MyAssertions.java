package homework.runner;

public class MyAssertions {

    private MyAssertions() {}

    public static void assertEquals(boolean value, boolean expected) {
        if (value != expected) {
            throw new IllegalStateException("Assertion exception");
        }
    }

    public static void assertEquals(int value, int expected) {
        if (value != expected) {
            throw new IllegalStateException("Assertion exception");
        }
    }
}
