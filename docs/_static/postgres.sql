CREATE TABLE p_animal (
 sci_name VARCHAR(255) NOT NULL,
 ger_name VARCHAR(255),
 eng_name VARCHAR(255) NOT NULL
);

ALTER TABLE p_animal ADD CONSTRAINT PK_p_animal PRIMARY KEY (sci_name);


