package business.strategy;

public class RequisitiMedicinaStrategy implements VerificaRequisitiStrategy {

    private static final int CFU_MINIMI_MEDICINA = 150;

    @Override
    public boolean verificaRequisiti(int cfuAcquisiti) {
        return cfuAcquisiti >= CFU_MINIMI_MEDICINA;
    }
}