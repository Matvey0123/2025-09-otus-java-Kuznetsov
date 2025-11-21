package atm;

interface Cell {

    Nominal getBanknoteNominal();

    int getBanknoteNominalValue();

    int getCurrentSum();

    int getBanknotesCount();

    void addBanknotes(int countToAdd);

    void removeBanknotes(int countToRemove);
}
