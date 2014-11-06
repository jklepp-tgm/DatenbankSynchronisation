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