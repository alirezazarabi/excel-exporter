CREATE TABLE employee
(
    id              INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255) DEFAULT NULL,
    salary          DOUBLE       DEFAULT NULL,
    employment_date DATE         DEFAULT NULL
);