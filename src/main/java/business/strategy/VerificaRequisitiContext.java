package business.strategy;

public class VerificaRequisitiContext {

    public static VerificaRequisitiStrategy getStrategy(String corsoDiLaurea) {
        if (corsoDiLaurea == null) {
            return new RequisitiIngegneriaStrategy();
        }

        String corso = corsoDiLaurea.trim().toLowerCase();

        if (corso.startsWith("ingegneria")) {
            return new RequisitiIngegneriaStrategy();
        }
        if (corso.equals("informatica")) {
            return new RequisitiInformaticaStrategy();
        }
        if (corso.equals("economia")) {
            return new RequisitiEconomiaStrategy();
        }
        if (corso.equals("medicina")) {
            return new RequisitiMedicinaStrategy();
        }

        return new RequisitiIngegneriaStrategy();
    }
}