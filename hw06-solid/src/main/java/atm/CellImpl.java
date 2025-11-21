package atm;

class CellImpl implements Cell {

    private final Nominal nominal;
    private int banknoteCount;

    CellImpl(Nominal nominal) {
        this.nominal = nominal;
    }

    @Override
    public Nominal getBanknoteNominal() {
        return nominal;
    }

    @Override
    public int getBanknoteNominalValue() {
        return nominal.getValue();
    }

    @Override
    public int getCurrentSum() {
        return nominal.getValue() * banknoteCount;
    }

    @Override
    public int getBanknotesCount() {
        return banknoteCount;
    }

    public void addBanknotes(int count) {
        this.banknoteCount += count;
    }

    @Override
    public void removeBanknotes(int countToRemove) {
        if (countToRemove <= banknoteCount) {
            banknoteCount -= countToRemove;
        } else {
            throw new AtmException("Not enough banknotes with nominal = " + nominal.getValue());
        }
    }
}
