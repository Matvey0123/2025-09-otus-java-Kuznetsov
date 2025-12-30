package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> store = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        if (msg.getField13() != null) {
            store.put(msg.getId(), getCopyWithField13(msg));
        } else {
            store.put(msg.getId(), getCopy(msg));
        }
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    private Message getCopy(Message message) {
        return message.toBuilder().build();
    }

    private Message getCopyWithField13(Message message) {
        var clonedField13 = new ObjectForMessage();
        clonedField13.setData(List.copyOf(message.getField13().getData()));
        return message.toBuilder().field13(clonedField13).build();
    }
}
