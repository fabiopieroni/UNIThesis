-- 1. Disabilita temporaneamente i vincoli o pulisci (opzionale)
-- Se vuoi che lo script sia rieseguibile senza errori di duplicati

-- 2. Dati di base (es. Dipartimenti / Corsi di Laurea)
INSERT INTO dipartimento (id, nome) VALUES
                                        (1, 'Informatica'),
                                        (2, 'Ingegneria dell''Informazione'),
                                        (3, 'Economia');

-- 3. Utenti di prova (Professori e Studenti)
INSERT INTO utente (id, nome, cognome, email, password, ruolo) VALUES
                                                                   (1, 'Mario', 'Rossi', 'mario.rossi@unithesis.it', 'password123', 'PROFESSORE'),
                                                                   (2, 'Elena', 'Bianchi', 'elena.bianchi@unithesis.it', 'password123', 'PROFESSORE'),
                                                                   (3, 'Luigi', 'Verdi', 'luigi.verdi@studenti.unithesis.it', 'password123', 'STUDENTE');

-- 4. Tesi / Proposte di Tesi di prova
INSERT INTO tesi (id, titolo, descrizione, relatore_id, dipartimento_id, stato) VALUES
                                                                                    (1, 'Sviluppo di un''applicazione JavaFX con Architettura DAO', 'Progetto incentrato su Java, JDBC e pattern DAO.', 1, 1, 'DISPONIBILE'),
                                                                                    (2, 'Analisi dei Big Data in ambiente Cloud', 'Studio comparativo su AWS e Azure.', 2, 2, 'DISPONIBILE');