CREATE TABLE m_animal (
 Wissenschaftlicher Name VARCHAR(255) NOT NULL
);

ALTER TABLE m_animal ADD CONSTRAINT PK_m_animal PRIMARY KEY (Wissenschaftlicher Name);


CREATE TABLE Sprache (
 language VARCHAR(63) NOT NULL
);

ALTER TABLE Sprache ADD CONSTRAINT PK_Sprache PRIMARY KEY (language);


CREATE TABLE translation (
 Wissenschaftlicher Name VARCHAR(255) NOT NULL,
 language VARCHAR(63) NOT NULL,
 tname VARCHAR(255)
);

ALTER TABLE translation ADD CONSTRAINT PK_translation PRIMARY KEY (Wissenschaftlicher Name,language);


ALTER TABLE translation ADD CONSTRAINT FK_translation_0 FOREIGN KEY (Wissenschaftlicher Name) REFERENCES m_animal (Wissenschaftlicher Name);
ALTER TABLE translation ADD CONSTRAINT FK_translation_1 FOREIGN KEY (language) REFERENCES Sprache (language);


