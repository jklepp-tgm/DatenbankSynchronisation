CREATE TABLE p_animal (
 sci_name VARCHAR(255) NOT NULL,
 ger_name VARCHAR(255),
 eng_name VARCHAR(255) NOT NULL
);

ALTER TABLE p_animal ADD CONSTRAINT PK_p_animal PRIMARY KEY (sci_name);

INSERT INTO p_animal VALUES ("Bufo", "Echte Kröte", "bufo");
INSERT INTO p_animal VALUES ("Vulpes vulpes", "Rotfuchs", "red fox");
INSERT INTO p_animal VALUES ("Giraffa camelopardalis", "Giraffe", "giraffe");
INSERT INTO p_animal VALUES ("Elephas maximus", "Asiatischer Elefant", "asian elephant");
INSERT INTO p_animal VALUES ("Rattus norvegicus", "Wanderratte", "brown rat");
INSERT INTO p_animal VALUES ("Sciurus vulgaris", "Eichhörnchen", "red squirrel");
INSERT INTO p_animal VALUES ("Microchiroptera", "Fledermaus", "microbat");
INSERT INTO p_animal VALUES ("Felis silvestris f. catus", "Hauskatze", "cat");
INSERT INTO p_animal VALUES ("Anguis fragilis", "Blindschleiche", "slow worm");
INSERT INTO p_animal VALUES ("Morelia viridis", "Grüner Baumpython", "green tree python");
