package atm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AtmImplTest {

    @Test
    void testAtm() {
        var atm = new AtmImpl(List.of(Nominal.RUB_500, Nominal.RUB_200, Nominal.RUB_1000));
        atm.takeBanknotes(Nominal.RUB_500, 5);
        atm.takeBanknotes(Nominal.RUB_200, 3);
        atm.takeBanknotes(Nominal.RUB_1000, 2);

        assertThat(atm.giveRestSum()).isEqualTo(5100);

        var requestedAmount = atm.giveRequestedAmount(2400);
        assertThat(requestedAmount).containsExactlyInAnyOrderEntriesOf(Map.of(Nominal.RUB_1000, 2, Nominal.RUB_200, 2));
        assertThat(atm.giveRestSum()).isEqualTo(2700);

        var throwable = catchThrowable(() -> atm.giveRequestedAmount(600));
        assertThat(throwable).isInstanceOf(AtmException.class).hasMessage("Atm cannot give requested sum");
        assertThat(atm.giveRestSum()).isEqualTo(2700);

        var requestedAmount2 = atm.giveRequestedAmount(2700);
        assertThat(requestedAmount2).containsExactlyInAnyOrderEntriesOf(Map.of(Nominal.RUB_500, 5, Nominal.RUB_200, 1));
        assertThat(atm.giveRestSum()).isZero();

        var throwable2 = catchThrowable(() -> atm.giveRequestedAmount(200));
        assertThat(throwable2).isInstanceOf(AtmException.class).hasMessage("Atm cannot give requested sum");
        assertThat(atm.giveRestSum()).isZero();
    }

    @Test
    void testAtmWithAllNominals() {
        var atm = new AtmImpl(List.of(Nominal.values()));
        atm.takeBanknotes(Nominal.RUB_100, 1);
        atm.takeBanknotes(Nominal.RUB_200, 1);
        atm.takeBanknotes(Nominal.RUB_500, 1);
        atm.takeBanknotes(Nominal.RUB_1000, 1);
        atm.takeBanknotes(Nominal.RUB_2000, 1);
        atm.takeBanknotes(Nominal.RUB_5000, 1);

        assertThat(atm.giveRestSum()).isEqualTo(8800);
        var requestedAmount = atm.giveRequestedAmount(8800);
        assertThat(requestedAmount)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        Nominal.RUB_100,
                        1,
                        Nominal.RUB_200,
                        1,
                        Nominal.RUB_500,
                        1,
                        Nominal.RUB_1000,
                        1,
                        Nominal.RUB_2000,
                        1,
                        Nominal.RUB_5000,
                        1));
        assertThat(atm.giveRestSum()).isZero();
    }
}
