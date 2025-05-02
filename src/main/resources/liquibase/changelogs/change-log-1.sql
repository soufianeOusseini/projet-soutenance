--liquibase formatted sql

--changeset a.rachad:init_schema

-- Table Compagnie
CREATE TABLE IF NOT EXISTS t_company (
    id bigserial NOT NULL,
    name varchar(255) NOT NULL,
    address text NULL,
    phone varchar(255) NULL,
    postal_code varchar(255) NULL,
    city varchar(255) NULL,
    region varchar(255) NULL,
    email varchar(255) NULL,
    CONSTRAINT t_company_pkey PRIMARY KEY (id)
    );

-- Table Profile
CREATE TABLE IF NOT EXISTS t_profile (
                                         id bigserial NOT NULL,
                                         name varchar(255) NULL,
    CONSTRAINT t_profile_name_key UNIQUE (name),
    CONSTRAINT t_profile_pkey PRIMARY KEY (id)
    );

-- Table Permission
CREATE TABLE IF NOT EXISTS t_permission (
    id bigserial NOT NULL,
    name varchar(255) NULL,
    company_id int8 NULL,
    CONSTRAINT t_permission_name_key UNIQUE (name),
    CONSTRAINT t_permission_pkey PRIMARY KEY (id),
    CONSTRAINT fk_permission_company FOREIGN KEY (company_id) REFERENCES t_company(id)
    );

-- Table Role
CREATE TABLE IF NOT EXISTS t_role (
    id bigserial NOT NULL,
    name varchar(255) NULL,
    company_id int8 NULL,
    CONSTRAINT t_role_name_key UNIQUE (name),
    CONSTRAINT t_role_pkey PRIMARY KEY (id),
    CONSTRAINT fk_role_company FOREIGN KEY (company_id) REFERENCES t_company(id)
    );

-- Table User
CREATE TABLE IF NOT EXISTS t_user (
    id bigserial NOT NULL,
    first_name varchar(255) NULL,
    last_name varchar(255) NULL,
    email varchar(255) NULL,
    phone varchar(255) NULL,
    username varchar(255) NOT NULL,
    password varchar(255) NULL,
    password_reseted bool NULL,
    password_reset_code int4 NULL,
    password_reset_code_expiry_date timestamptz(6) NULL,
    default_language int4 NULL,
    last_connection_at timestamp(6) NULL,
    company_id int8 NULL,
    CONSTRAINT t_user_email_key UNIQUE (email),
    CONSTRAINT t_user_phone_key UNIQUE (phone),
    CONSTRAINT t_user_username_key UNIQUE (username),
    CONSTRAINT t_user_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES t_company(id)
    );

-- Table RefreshToken
CREATE TABLE IF NOT EXISTS t_refresh_token (
    id bigserial NOT NULL,
    token varchar(255) NULL,
    expiry_date timestamptz(6) NULL,
    user_id int8 NULL,
    CONSTRAINT t_refresh_token_pkey PRIMARY KEY (id),
    CONSTRAINT t_refresh_token_user_id_key UNIQUE (user_id),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES t_user(id)
    );

-- Table Role_Permission
CREATE TABLE IF NOT EXISTS role_permission (
    role_id int8 NOT NULL,
    permission_id int8 NOT NULL,
    CONSTRAINT role_permission_pkey PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES t_role(id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES t_permission(id)
    );

-- Table User_Role
CREATE TABLE IF NOT EXISTS user_role (
    user_id int8 NOT NULL,
    role_id int8 NOT NULL,
    CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES t_user(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES t_role(id)
    );

-- Table User_Profile
CREATE TABLE IF NOT EXISTS user_profile (
    user_id int8 NOT NULL,
    profile_id int8 NOT NULL,
    CONSTRAINT user_profile_pkey PRIMARY KEY (user_id, profile_id),
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES t_user(id),
    CONSTRAINT fk_user_profile_profile FOREIGN KEY (profile_id) REFERENCES t_profile(id)
    );

-- Insertion des profils par défaut
INSERT INTO t_profile (name) VALUES ('ADMIN');
INSERT INTO t_profile (name) VALUES ('USER');
INSERT INTO t_profile (name) VALUES ('COMPANY');
INSERT INTO t_profile (name) VALUES ('DRIVER');

-- Insertion des rôles par défaut
INSERT INTO t_role (name) VALUES ('ROLE_ADMIN');
INSERT INTO t_role (name) VALUES ('ROLE_USER');
INSERT INTO t_role (name) VALUES ('ROLE_SUPER_ADMIN');



--changeset sousseini:create_table_t_bus
CREATE TABLE IF NOT EXISTS t_bus (
     id BIGINT  PRIMARY KEY,
     plaque VARCHAR(255) NOT NULL,
     model VARCHAR(255),
     capacite INT,
     numero VARCHAR(50) UNIQUE,
     image VARCHAR(255),
     type VARCHAR(100),
     status VARCHAR(50),
     place_dispo INT,
     compagnie_id BIGINT NOT NULL,
     FOREIGN KEY (compagnie_id) REFERENCES T_COMPANY(id)
);

--changeset sousseini:create_table_t_trajet
CREATE TABLE IF NOT EXISTS t_trajet (
    id BIGINT  PRIMARY KEY,
    nom VARCHAR(255),
    ville_depart VARCHAR(255) NOT NULL,
    ville_arrive VARCHAR(255) NOT NULL,
    km float8,
    heure TIME,
    status VARCHAR(50),
    bus_id BIGINT,
    FOREIGN KEY (bus_id) REFERENCES t_bus(id)
);

--changeset sousseini:create_table_t_ticket
CREATE TABLE IF NOT EXISTS t_ticket (
    id BIGINT PRIMARY KEY,
    prix float8 NOT NULL,
    numero VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(50),
    date DATE,
    heure_depart TIME,
    user_id BIGINT,
    trajet_id BIGINT NOT NULL,
    mode_paiement VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (trajet_id) REFERENCES t_trajet(id)
);

--changeset sousseini:create_table_t_reservation
CREATE TABLE IF NOT EXISTS t_reservation (
     id BIGINT PRIMARY KEY,
     date DATE,
     status VARCHAR(50),
     nombre_place INT,
     prix float8,
     mode_paiement VARCHAR(50),
     trajet_id BIGINT NOT NULL,
     ticket_id BIGINT,
     FOREIGN KEY (trajet_id) REFERENCES t_trajet(id),
     FOREIGN KEY (ticket_id) REFERENCES t_ticket(id)
);

--changeset sousseini:create_table_t_colis
CREATE TABLE IF NOT EXISTS t_colis (
   id BIGINT PRIMARY KEY,
   numero VARCHAR(50) NOT NULL UNIQUE,
   expediteur VARCHAR(255) NOT NULL,
   destinateur VARCHAR(255) NOT NULL,
   heure_envoi TIME,
   nombre INT,
   nature VARCHAR(255),
   prix float8,
   lieu_envoi VARCHAR(255),
   lieu_reception VARCHAR(255),
   compagnie_id BIGINT,
   trajet_id BIGINT,
   status VARCHAR(50),
   FOREIGN KEY (compagnie_id) REFERENCES T_COMPANY(id),
   FOREIGN KEY (trajet_id) REFERENCES t_trajet(id)
);
