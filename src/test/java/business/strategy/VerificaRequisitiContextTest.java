package business.strategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VerificaRequisitiContextTest {

    @Test
    void informatica89CfuFail() {
        VerificaRequisitiStrategy s = VerificaRequisitiContext.getStrategy("Informatica");
        assertFalse(s.verificaRequisiti(89));
    }

    @Test
    void informatica90CfuPass() {
        VerificaRequisitiStrategy s = VerificaRequisitiContext.getStrategy("Informatica");
        assertTrue(s.verificaRequisiti(90));
    }

    @Test
    void economia59CfuFail() {
        VerificaRequisitiStrategy s = VerificaRequisitiContext.getStrategy("Economia");
        assertFalse(s.verificaRequisiti(59));
    }

    @Test
    void economia60CfuPass() {
        VerificaRequisitiStrategy s = VerificaRequisitiContext.getStrategy("Economia");
        assertTrue(s.verificaRequisiti(60));
    }

    @Test
    void medicina119CfuFail() {
        VerificaRequisitiStrategy s = VerificaRequisitiContext.getStrategy("Medicina");
        assertFalse(s.verificaRequisiti(119));
    }

    @Test
    void medicina120CfuPass() {
        VerificaRequisitiStrategy s = VerificaRequisitiContext.getStrategy("Medicina");
        assertTrue(s.verificaRequisiti(120));
    }

    @Test
    void ingegneriaElettronica89CfuFail() {
        VerificaRequisitiStrategy s = VerificaRequisitiContext.getStrategy("Ingegneria Elettronica");
        assertFalse(s.verificaRequisiti(89));
    }

    @Test
    void ingegneriaGestionale90CfuPass() {
        // verifica che tutte le sotto-varianti di Ingegneria condividano la stessa soglia
        VerificaRequisitiStrategy elettronica = VerificaRequisitiContext.getStrategy("Ingegneria Elettronica");
        VerificaRequisitiStrategy gestionale = VerificaRequisitiContext.getStrategy("Ingegneria Gestionale");

        assertTrue(gestionale.verificaRequisiti(90));
        assertEquals(elettronica.getClass(), gestionale.getClass());
    }

    @Test
    void corsoSconosciutoOrNullFallbackIngegneria() {
        VerificaRequisitiStrategy sNull = VerificaRequisitiContext.getStrategy(null);
        VerificaRequisitiStrategy sSconosciuto = VerificaRequisitiContext.getStrategy("Sociologia");

        assertInstanceOf(RequisitiIngegneriaStrategy.class, sNull);
        assertInstanceOf(RequisitiIngegneriaStrategy.class, sSconosciuto);
    }
}