
package business.strategy;

public class RequisitiIngegneriaStrategy implements VerificaRequisitiStrategy {

    private static final int CFU_MINIMI_INGEGNERIA = 120;

    @Override
    public boolean verificaRequisiti(int cfuAcquisiti) {
        return cfuAcquisiti >= CFU_MINIMI_INGEGNERIA;
    }
}