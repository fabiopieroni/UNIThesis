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

-- ==========================================
-- AGGIUNTE PER COMPLETARE IL TEST
-- ==========================================

-- 4. Corsi di Laurea
INSERT INTO corsi_di_laurea (nome, dipartimento) VALUES
                                                     ('Informatica', 'DINFO'),
                                                     ('Ingegneria Informatica', 'DINFO'),
                                                     ('Economia e Commercio', 'DISEI')
    ON CONFLICT (nome) DO NOTHING;

-- 5. Tesi di Prova (collegata al Prof. Luigi Verdi appena inserito)
INSERT INTO tesi (titolo, descrizione, corso_laurea, stato, id_professore)
VALUES (
           'Sviluppo Applicazioni Web Java',
           'Progetto di tesi sull architettura layered con Java e PostgreSQL',
           'Informatica',
           'DISPONIBILE',
           (SELECT id_utente FROM utenti WHERE email = 'luigi.verdi@unifi.it')
       );