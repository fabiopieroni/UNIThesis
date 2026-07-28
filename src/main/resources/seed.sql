-- ==========================================
-- DATI DI PROVA PER IL TEST
-- ==========================================

-- 1. Studente
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('mario.rossi@studenti.it', 'password123', 'STUDENTE', 'Mario', 'Rossi');

INSERT INTO studenti (id_utente, matricola, cfu_totali, corso_laurea)
VALUES (currval('utenti_id_utente_seq'), '123456', 120, 'Informatica');

-- 2. Professore
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('luigi.verdi@unifi.it', 'prof123', 'PROFESSORE', 'Luigi', 'Verdi');

INSERT INTO professori (id_utente, matricola_docente, corso_laurea, num_tesisti_attivi)
VALUES (currval('utenti_id_utente_seq'), 'DOC999', 'Informatica', 2);

-- 3. Segreteria
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('segreteria@unifi.it', 'admin123', 'SEGRETERIA', 'Anna', 'Bianchi');