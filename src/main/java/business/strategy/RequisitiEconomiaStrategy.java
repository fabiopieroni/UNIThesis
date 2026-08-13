package business.strategy;

public class RequisitiEconomiaStrategy implements VerificaRequisitiStrategy {

    private static final int CFU_MINIMI_ECONOMIA = 60;

    @Override
    public boolean verificaRequisiti(int cfuAcquisiti) {
        return cfuAcquisiti >= CFU_MINIMI_ECONOMIA;
    }
}