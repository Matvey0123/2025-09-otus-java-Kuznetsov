package homework;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    private final NavigableMap<Customer, String> store = new TreeMap<>(Comparator.comparing(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        var result = store.firstEntry();
        return copyOf(result);
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        var result = store.higherEntry(customer);
        return copyOf(result);
    }

    public void add(Customer customer, String data) {
        store.put(customer, data);
    }

    private Map.Entry<Customer, String> copyOf(Map.Entry<Customer, String> entry) {
        if (entry == null) {
            return null;
        }
        return Map.entry(entry.getKey().copy(), entry.getValue());
    }
}
