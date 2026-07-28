package ui;

import business.AuthService;
import business.Sessione;

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
        System.out.println("            TEST COMPLETATI               ");
        System.out.println("==========================================");
    }
}