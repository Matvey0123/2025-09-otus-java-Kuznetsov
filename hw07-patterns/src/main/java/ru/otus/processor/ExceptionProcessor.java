package ru.otus.processor;

import ru.otus.model.Message;

@SuppressWarnings("java:S112")
public class ExceptionProcessor implements Processor {

    private final DateTimeProvider timeProvider;

    public ExceptionProcessor(DateTimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public Message process(Message message) {
        if (timeProvider.getTime().getSecond() % 2 == 0) {
            throw new RuntimeException("Even second exception");
        }
        return message;
    }
}
