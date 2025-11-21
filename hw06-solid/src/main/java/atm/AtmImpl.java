package atm;

import java.util.*;

class AtmImpl implements Atm {

    private final Map<Nominal, CellImpl> cells;

    AtmImpl(List<Nominal> nominals) {
        cells = new TreeMap<>(Comparator.comparingInt(Nominal::getValue).reversed());
        nominals.forEach(n -> cells.computeIfAbsent(n, CellImpl::new));
    }

    @Override
    public void takeBanknotes(Nominal nominal, int count) {
        var cell = cells.get(nominal);
        if (cell != null) {
            cell.addBanknotes(count);
        } else {
            throw new AtmException("Atm cannot store such banknotes");
        }
    }

    @Override
    public Map<Nominal, Integer> giveRequestedAmount(int amount) {
        Map<Nominal, Integer> banknotesToGive = new EnumMap<>(Nominal.class);
        var values = cells.values();
        for (Cell cell : values) {
            int count = 0;
            int nominalValue = cell.getBanknoteNominalValue();
            while (amount >= nominalValue && cell.getBanknotesCount() > 0) {
                count++;
                amount -= nominalValue;
            }
            if (count > 0) {
                banknotesToGive.put(cell.getBanknoteNominal(), count);
                cell.removeBanknotes(count);
            }
        }
        if (amount > 0) {
            returnBanknotesToCells(banknotesToGive);
            throw new AtmException("Atm cannot give requested sum");
        }
        return banknotesToGive;
    }

    @Override
    public int giveRestSum() {
        return cells.values().stream().mapToInt(CellImpl::getCurrentSum).reduce(0, Integer::sum);
    }

    private void returnBanknotesToCells(Map<Nominal, Integer> banknotes) {
        banknotes.forEach((key, value) -> {
            var cell = cells.get(key);
            cell.addBanknotes(value);
        });
    }
}
