package homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {

    private final Deque<Customer> store = new ArrayDeque<>();

    public void add(Customer customer) {
        store.push(customer);
    }

    public Customer take() {
        return store.pop();
    }
}
