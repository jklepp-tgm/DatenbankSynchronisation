CREATE TABLE m_animal (
 wname VARCHAR(255) NOT NULL
);

ALTER TABLE m_animal ADD CONSTRAINT PK_m_animal PRIMARY KEY (wname);

CREATE TABLE language (
 language VARCHAR(63) NOT NULL
);

ALTER TABLE language ADD CONSTRAINT PK_language PRIMARY KEY (language);

CREATE TABLE translation (
 wname VARCHAR(255) NOT NULL,
 language VARCHAR(63) NOT NULL,
 tname VARCHAR(255)
);

ALTER TABLE translation ADD CONSTRAINT PK_translation PRIMARY KEY (wname,language);

ALTER TABLE translation ADD CONSTRAINT FK_translation_0 FOREIGN KEY (wname) REFERENCES m_animal (wname);
ALTER TABLE translation ADD CONSTRAINT FK_translation_1 FOREIGN KEY (language) REFERENCES language (language);

-- TRIGGER
CREATE TABLE translationlog
(
    wnameold VARCHAR(255),
    languageold VARCHAR(64),
    tnameold VARCHAR(255),
    wnamenew VARCHAR(255),
    languagenew VARCHAR(64),
    tnamenew VARCHAR(255),
    action ENUM('new', 'update', 'delete'),
    lstamp INTEGER
)ENGINE=INNODB;

DELIMITER //
CREATE TRIGGER after_insert_translation AFTER INSERT ON translation
FOR EACH ROW
BEGIN
    INSERT INTO translationlog VALUES(NULL, NULL, NULL, NEW.wname, NEW.language, NEW.tname, 'new', UNIX_TIMESTAMP(NOW()));
END;//

CREATE TRIGGER after_update_translation AFTER UPDATE ON translation
FOR EACH ROW
BEGIN
    INSERT INTO translationlog VALUES(OLD.wname, OLD.language, OLD.tname, NEW.wname, NEW.language, NEW.tname, 'update', UNIX_TIMESTAMP(NOW()));
END;//

CREATE TRIGGER after_delete_translation AFTER DELETE ON translation
FOR EACH ROW
BEGIN
    INSERT INTO translationlog VALUES(OLD.wname, OLD.language, OLD.tname, NULL, NULL, NULL, 'delete', UNIX_TIMESTAMP(NOW()));
END;//

DELIMITER ;