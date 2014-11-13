CREATE TABLE p_animal (
 wname VARCHAR(255) NOT NULL,
 ger_name VARCHAR(255),
 eng_name VARCHAR(255) NOT NULL
);

ALTER TABLE p_animal ADD CONSTRAINT PK_p_animal PRIMARY KEY (wname);

INSERT INTO p_animal VALUES ('Bufo', 'Echte Kröte', 'bufo');
INSERT INTO p_animal VALUES ('Vulpes vulpes', 'Rotfuchs', 'red fox');
INSERT INTO p_animal VALUES ('Giraffa camelopardalis', 'Giraffe', 'giraffe');
INSERT INTO p_animal VALUES ('Elephas maximus', 'Asiatischer Elefant', 'asian elephant');
INSERT INTO p_animal VALUES ('Rattus norvegicus', 'Wanderratte', 'brown rat');
INSERT INTO p_animal VALUES ('Sciurus vulgaris', 'Eichhörnchen', 'red squirrel');
INSERT INTO p_animal VALUES ('Microchiroptera', 'Fledermaus', 'microbat');
INSERT INTO p_animal VALUES ('Felis silvestris f. catus', 'Hauskatze', 'cat');
INSERT INTO p_animal VALUES ('Anguis fragilis', 'Blindschleiche', 'slow worm');
INSERT INTO p_animal VALUES ('Morelia viridis', 'Grüner Baumpython', 'green tree python');

-- TRIGGER
CREATE TABLE animallog
(
    wnameold VARCHAR(255),
    ger_nameold VARCHAR(255),
    eng_nameold VARCHAR(255),
    wnamenew VARCHAR(255),
    ger_namenew VARCHAR(255),
    eng_namenew VARCHAR(255),
    action VARCHAR(16),
    lstamp INTEGER
);

CREATE OR REPLACE FUNCTION handle_delete() RETURNS trigger AS $handle_delete$
BEGIN
    INSERT INTO animallog VALUES(
        OLD.wname,
        OLD.ger_name,
        OLD.eng_name,
        NULL,
        NULL,
        NULL,
        'delete',
        extract(epoch from now()));
    RETURN OLD;
END;
$handle_delete$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION handle_update() RETURNS trigger AS $handle_update$
BEGIN
    INSERT INTO animallog VALUES(
        OLD.wname,
        OLD.ger_name,
        OLD.eng_name,
        NEW.wname,
        NEW.ger_name,
        NEW.eng_name,
        'update',
        extract(epoch from now()));
    RETURN NEW;
END;
$handle_update$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION handle_new() RETURNS trigger AS $handle_new$
BEGIN
    INSERT INTO animallog VALUES(
        NULL,
        NULL,
        NULL,
        NEW.wname,
        NEW.ger_name,
        NEW.eng_name,
        'new',
        extract(epoch from now()));
    RETURN NEW;
END;
$handle_new$ LANGUAGE plpgsql;

CREATE TRIGGER after_insert_animal AFTER INSERT ON p_animal
FOR EACH ROW EXECUTE PROCEDURE handle_new();

CREATE TRIGGER after_update_animal AFTER UPDATE ON p_animal
FOR EACH ROW EXECUTE PROCEDURE handle_update();

CREATE TRIGGER after_delete_animal AFTER DELETE ON p_animal
FOR EACH ROW EXECUTE PROCEDURE handle_delete();