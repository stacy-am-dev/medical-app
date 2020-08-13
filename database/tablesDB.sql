CREATE TABLE TABLE_PATIENT (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    patronymic VARCHAR(30) NOT NULL,
    phoneNumber VARCHAR(11) UNIQUE
);

CREATE TABLE TABLE_DOCTOR (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    patronymic VARCHAR(30) NOT NULL,
    specialization VARCHAR(100) NOT NULL
);

CREATE TABLE TABLE_PRESCRIPTION (
     id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY,
     description VARCHAR(1000) NOT NULL,
     patientId BIGINT NOT NULL,
     doctorId BIGINT NOT NULL,
     dataCreation DATE NOT NULL,
     validity DATE NOT NULL,
     priority VARCHAR(15) NOT NULL,

    CONSTRAINT FK_PATIENT_PRESCRIPTION FOREIGN KEY  (patientId) REFERENCES TABLE_PATIENT(id) ON DELETE RESTRICT,
    CONSTRAINT FK_DOCTOR_PRESCRIPTION FOREIGN KEY (doctorId) REFERENCES TABLE_DOCTOR(id) ON DELETE RESTRICT
);


