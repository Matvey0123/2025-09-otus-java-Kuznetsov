package homework.tests;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;
import homework.runner.MyAssertions;
import java.util.HashSet;
import java.util.Set;

public class Test4 {
    private final Set<Integer> set = new HashSet<>();

    @Before
    public void before() {
        set.add(1);
        set.add(2);
    }

    @Test
    public void test1() {
        set.add(3);
        set.add(4);

        MyAssertions.assertEquals(set.size(), 4);
        MyAssertions.assertEquals(set.contains(3), true);
        MyAssertions.assertEquals(set.contains(4), true);
        MyAssertions.assertEquals(set.contains(5), false);
    }

    @Test
    public void test2() {
        set.add(5);
        set.add(6);

        MyAssertions.assertEquals(set.size(), 4);
        MyAssertions.assertEquals(set.contains(5), true);
        MyAssertions.assertEquals(set.contains(6), true);
        MyAssertions.assertEquals(set.contains(7), false);
    }

    @After
    public void after() {
        throw new IllegalStateException("Exception in after");
    }
}
