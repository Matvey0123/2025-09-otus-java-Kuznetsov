package homework;

import java.time.LocalDateTime;

public class TestObject {
    private final String name;
    private final LocalDateTime date;

    public TestObject(String name, LocalDateTime date) {
        this.name = name;
        this.date = date;
    }

    @Override
    public String toString() {
        return "TestObject{" + "name='" + name + '\'' + ", date=" + date + '}';
    }
}
