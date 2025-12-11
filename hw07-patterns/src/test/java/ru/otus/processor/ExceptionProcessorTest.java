package ru.otus.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

@SuppressWarnings({"java:S108", "java:S2925"})
class ExceptionProcessorTest {

    @Test
    void shouldCatchEvenSecondException() {
        // given
        var processor = new ExceptionProcessor(() -> LocalDateTime.of(2025, 12, 19, 23, 51, 2));
        // when
        var throwable = catchThrowable(() -> processor.process(new Message.Builder(1L).build()));
        // then
        assertThat(throwable).isNotNull().hasMessage("Even second exception");
    }

    @Test
    void shouldNotCatchEvenSecondException() {
        // given
        var processor = new ExceptionProcessor(() -> LocalDateTime.of(2025, 12, 19, 23, 51, 1));
        // when
        var throwable = catchThrowable(() -> processor.process(new Message.Builder(1L).build()));
        // then
        assertThat(throwable).isNull();
    }
}
