package atm;

enum Nominal {
    RUB_100(100),
    RUB_200(200),
    RUB_500(500),
    RUB_1000(1000),
    RUB_2000(2000),
    RUB_5000(5000);

    private final int value;

    Nominal(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}
