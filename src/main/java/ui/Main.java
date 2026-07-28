package ui;

import business.AuthService;
import business.Sessione;
import business.strategy.RequisitiIngegneriaStrategy;
import business.strategy.VerificaRequisitiContext;
import business.strategy.VerificaRequisitiStrategy;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   TEST AUTENTICAZIONE E CONTROLLO RUOLI  ");
        System.out.println("==========================================\n");

        AuthService authService = new AuthService();

        // --- TEST 1: Studente ---
        System.out.println("📌 [TEST 1] Login Studente...");
        if (authService.login("mario.rossi@studenti.it", "password123")) {
            System.out.println("✅ Login RIUSCITO!");
            System.out.println("   Utente: " + Sessione.getInstance().getUtenteCorrente().getNome() + " " + Sessione.getInstance().getUtenteCorrente().getCognome());
            System.out.println("   Ruolo Studente? " + Sessione.getInstance().isStudente());
        } else {
            System.err.println("❌ Login FALLITO.");
        }
        authService.logout();

        System.out.println("\n------------------------------------------\n");

        // --- TEST 2: Professore ---
        System.out.println("📌 [TEST 2] Login Professore...");
        if (authService.login("luigi.verdi@unifi.it", "prof123")) {
            System.out.println("✅ Login RIUSCITO!");
            System.out.println("   Utente: " + Sessione.getInstance().getUtenteCorrente().getNome() + " " + Sessione.getInstance().getUtenteCorrente().getCognome());
            System.out.println("   Ruolo Professore? " + Sessione.getInstance().isProfessore());
        } else {
            System.err.println("❌ Login FALLITO.");
        }
        authService.logout();

        System.out.println("\n------------------------------------------\n");

        // --- TEST 3: Segreteria ---
        System.out.println("📌 [TEST 3] Login Segreteria...");
        if (authService.login("segreteria@unifi.it", "admin123")) {
            System.out.println("✅ Login RIUSCITO!");
            System.out.println("   Utente: " + Sessione.getInstance().getUtenteCorrente().getNome() + " " + Sessione.getInstance().getUtenteCorrente().getCognome());
            System.out.println("   Ruolo Segreteria? " + Sessione.getInstance().isSegreteria());
        } else {
            System.err.println("❌ Login FALLITO.");
        }
        authService.logout();

        System.out.println("\n------------------------------------------\n");

        // --- TEST 4: Password Errata ---
        System.out.println("📌 [TEST 4] Tentativo password errata...");
        if (!authService.login("mario.rossi@studenti.it", "sbagliata")) {
            System.out.println("✅ Comportamento corretto: Accesso negato!");
        } else {
            System.err.println("❌ Errore: Ha fatto entrare l'utente con password sbagliata!");
        }

        System.out.println("\n==========================================");
        System.out.println("   TEST STRATEGY VERIFICA REQUISITI CFU   ");
        System.out.println("==========================================\n");

        // --- TEST 5: Strategy Ingegneria (120 CFU) ---
        System.out.println("📌 [TEST 5] Strategy Ingegneria (Soglia >= 120 CFU)...");
        VerificaRequisitiStrategy stratIng = new RequisitiIngegneriaStrategy();

        if (stratIng.verificaRequisiti(125)) {
            System.out.println("✅ Studente con 125 CFU: Idoneo!");
        } else {
            System.err.println("❌ Errore Strategy Ingegneria (125 CFU dovrebbero bastare).");
        }

        if (!stratIng.verificaRequisiti(100)) {
            System.out.println("✅ Studente con 100 CFU: Correttamente respinto!");
        } else {
            System.err.println("❌ Errore Strategy Ingegneria (100 CFU non devono bastare).");
        }

        System.out.println("\n------------------------------------------\n");

        // --- TEST 6: Context Strategy dinamico ---
        System.out.println("📌 [TEST 6] Factory Context Strategy (Economia)...");
        VerificaRequisitiStrategy stratEco = VerificaRequisitiContext.getStrategy("economia");

        if (stratEco.verificaRequisiti(105)) {
            System.out.println("✅ Studente Economia con 105 CFU: Idoneo!");
        } else {
            System.err.println("❌ Errore Context Economia.");
        }

        System.out.println("\n==========================================");
        System.out.println("            TEST COMPLETATI               ");
        System.out.println("==========================================");
    }
}