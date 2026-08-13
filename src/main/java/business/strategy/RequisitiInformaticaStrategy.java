package business.strategy;

public class RequisitiInformaticaStrategy implements VerificaRequisitiStrategy {

    private static final int CFU_MINIMI_INFORMATICA = 90;

    @Override
    public boolean verificaRequisiti(int cfuAcquisiti) {
        return cfuAcquisiti >= CFU_MINIMI_INFORMATICA;
    }
}