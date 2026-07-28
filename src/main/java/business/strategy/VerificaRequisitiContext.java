package business.strategy;

public class VerificaRequisitiContext {

    public static VerificaRequisitiStrategy getStrategy(String corsoDiLaurea) {
        if (corsoDiLaurea == null) {
            return new RequisitiIngegneriaStrategy(); // Default
        }

        switch (corsoDiLaurea.toLowerCase()) {
            case "economia":
                return new RequisitiEconomiaStrategy();
            case "informatica":
                return new RequisitiInformaticaStrategy();
            case "medicina":
                return new RequisitiMedicinaStrategy();
            case "ingegneria":
            default:
                return new RequisitiIngegneriaStrategy();
        }
    }
}