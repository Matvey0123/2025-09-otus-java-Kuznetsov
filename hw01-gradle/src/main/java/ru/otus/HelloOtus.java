package ru.otus;

import com.google.common.base.Joiner;

@SuppressWarnings("java:S106")
public class HelloOtus {
    public static void main(String... args) {
        var joiner = Joiner.on(',').skipNulls();
        var message = joiner.join("Hello", null, "otus!", 123);

        System.out.println(message);
    }
}
