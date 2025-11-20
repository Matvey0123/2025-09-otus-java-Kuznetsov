package atm;

import java.util.Map;

interface Atm {

    void takeBanknotes(Nominal nominal, int count);

    Map<Nominal, Integer> giveRequestedAmount(int amount);

    int giveRestSum();
}
