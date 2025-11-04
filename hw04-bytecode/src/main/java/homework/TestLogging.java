package homework;

@SuppressWarnings("java:S106")
public class TestLogging {

    @Log
    public void calculation(int param1) {
        System.out.println("Calculation with 1 param: " + param1);
    }

    public void calculation(int param1, int param2) {
        System.out.println("Calculation without logging with params: " + param1 + ", " + param2);
    }

    @Log
    public void calculation(int param1, int param2, String param3) {
        System.out.println("Calculation with 3 params: " + param1 + ", " + param2 + ", " + param3);
    }

    @Log
    public void calculation(char param1, byte param2, boolean param3, short param4, int param5, float param6) {
        System.out.println("Calculation with 6 params: " + param1 + ", " + param2 + ", " + param3 + ", " + param4 + ", "
                + param5 + ", " + param6);
    }

    @Log
    public void printTestObject(TestObject testObject) {
        System.out.println("Test object = " + testObject);
    }
}
