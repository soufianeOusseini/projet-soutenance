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

--changeset sousseini:create_table_t_agency
CREATE TABLE IF NOT EXISTS T_AGENCY (
    ID BIGSERIAL PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL,
    CODE VARCHAR(255) UNIQUE,
    ADDRESS TEXT,
    PHONE VARCHAR(50),
    CITY VARCHAR(100),
    REGION VARCHAR(100),
    EMAIL VARCHAR(255),
    MANAGER_NAME VARCHAR(255),
    MANAGER_PHONE VARCHAR(50),
    STATUS VARCHAR(50) DEFAULT 'ACTIVE',
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    COMPANY_ID BIGINT NOT NULL,

    CONSTRAINT fk_agency_company FOREIGN KEY (COMPANY_ID)
    REFERENCES t_agency(ID)
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
    agency_id int8 NULL,
    CONSTRAINT t_permission_name_key UNIQUE (name),
    CONSTRAINT t_permission_pkey PRIMARY KEY (id),
    CONSTRAINT fk_permission_company FOREIGN KEY (agency_id) REFERENCES t_agency(id)
    );

-- Table Role
CREATE TABLE IF NOT EXISTS t_role (
    id bigserial NOT NULL,
    name varchar(255) NULL,
    agency_id int8 NULL,
    CONSTRAINT t_role_name_key UNIQUE (name),
    CONSTRAINT t_role_pkey PRIMARY KEY (id),
    CONSTRAINT fk_role_company FOREIGN KEY (agency_id) REFERENCES t_agency(id)
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
    CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES t_agency(id)
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
     id BIGSERIAL NOT NULL,
     plaque VARCHAR(255) NOT NULL,
    model VARCHAR(255),
    capacity INT,
    numero VARCHAR(50),
    image VARCHAR(255),
    type VARCHAR(100),
    status VARCHAR(50),
    space_available INT,
    agency_id BIGINT NOT NULL,
    CONSTRAINT t_bus_pkey PRIMARY KEY (id),
    CONSTRAINT uk_bus_numero_company UNIQUE (numero, agency_id),
    FOREIGN KEY (agency_id) REFERENCES t_agency(id)
    );


--changeset sousseini:create_table_t_trajet
CREATE TABLE IF NOT EXISTS t_trajet (
    id bigserial NOT NULL,
    nom VARCHAR(255),
    ville_depart VARCHAR(255) NOT NULL,
    ville_arrive VARCHAR(255) NOT NULL,
    km float8,
    heure TIME,
    status VARCHAR(50),
    bus_id BIGINT,
    CONSTRAINT t_trajet_pkey PRIMARY KEY (id),
    FOREIGN KEY (bus_id) REFERENCES t_bus(id)
);

--changeset sousseini:create_table_t_ticket
CREATE TABLE IF NOT EXISTS t_ticket (
    id bigserial NOT NULL,
    prix float8 NOT NULL,
    numero VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(50),
    date DATE,
    heure_depart TIME,
    user_id BIGINT,
    trajet BIGINT NOT NULL,
    MODE_PAIEMENT VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    CONSTRAINT t_ticket_pkey PRIMARY KEY (id),
    FOREIGN KEY (trajet) REFERENCES t_trajet(id)
);

--changeset sousseini:create_table_t_reservation
CREATE TABLE IF NOT EXISTS t_reservation (
    id bigserial NOT NULL,
     date DATE,
     status VARCHAR(50),
     nombre_place INT,
     prix float8,
     mode_paiement VARCHAR(50),
     trajet BIGINT NOT NULL,
     ticket BIGINT,
     FOREIGN KEY (trajet) REFERENCES t_trajet(id),
     FOREIGN KEY (ticket) REFERENCES t_ticket(id),
    CONSTRAINT t_reservation_pkey PRIMARY KEY (id)
);

--changeset sousseini:create_table_t_colis
CREATE TABLE IF NOT EXISTS t_colis (
    id bigserial NOT NULL,
   numero VARCHAR(50) NOT NULL UNIQUE,
   expediteur VARCHAR(255) NOT NULL,
   destinateur VARCHAR(255) NOT NULL,
   heure_envoi TIME,
   nombre INT,
   nature VARCHAR(255),
   prix float8,
   lieu_envoi VARCHAR(255),
   lieu_reception VARCHAR(255),
    agency_id BIGINT,
   trajet BIGINT,
   status VARCHAR(50),
   FOREIGN KEY (agency_id) REFERENCES t_agency(id),
   FOREIGN KEY (trajet) REFERENCES t_trajet(id),
    CONSTRAINT t_colis_pkey PRIMARY KEY (id)
);

--changeset a.rachad:add_email_admin_to_company
ALTER TABLE t_company ADD COLUMN email_admin varchar(255);

--changeset sousseini:drop_column_nombre_nature_and_from_t_colis
ALTER TABLE t_colis DROP COLUMN IF EXISTS nature;
ALTER TABLE t_colis DROP COLUMN IF EXISTS nombre;

--changeset sousseini:create_table_colis_items
CREATE TABLE IF NOT EXISTS t_colis_items (
    id bigserial NOT NULL,
     description TEXT,
    nombre INT,
    nature VARCHAR(255),
    colis_id BIGINT,
    FOREIGN KEY (colis_id) REFERENCES t_colis(id),
    CONSTRAINT t_colis_item_pkey PRIMARY KEY (id)
    );


--changeset sousseini:add_column_status_to_company
ALTER TABLE t_company ADD COLUMN IF NOT EXISTS status varchar(25);

--changeset sousseini:add_column_logo_to_compagny
ALTER TABLE t_company ADD COLUMN IF NOT EXISTS LOGO_PATH varchar(255);

--changeset sousseini:create_table_t_app_file
DROP TABLE IF EXISTS T_APP_FILE  CASCADE ;
CREATE TABLE IF NOT EXISTS t_app_file (
  id bigserial NOT NULL,
  create_at timestamp NULL,
  update_at timestamp NULL,
  display_name varchar(255) NULL,
    entity_id int8 NULL,
    "path" varchar(255) NULL,
    "size" int8 NULL,
    "type" varchar(255) NULL
    );
CREATE INDEX T_APP_FILE_ENTITY_AND_TYPE ON T_APP_FILE USING btree (entity_id, type);

--changeset sousseini:add_column_profil_path_to_user
ALTER TABLE t_user ADD COLUMN IF NOT EXISTS PROFILE_PATH varchar(255);

--changeset sousseini:add_column_company_to_trajet
ALTER TABLE t_trajet ADD COLUMN IF NOT EXISTS agency_id BIGINT;
ALTER TABLE t_trajet ADD CONSTRAINT fk_trajet_company FOREIGN KEY (agency_id) REFERENCES t_agency(id);


--changeset sousseini:add_column_amount_to_t_trajet
ALTER TABLE t_trajet ADD COLUMN IF NOT EXISTS amount float8;


--changeset sousseini:create_table_driver
CREATE TABLE IF NOT EXISTS t_driver (
    id bigserial NOT NULL,
    user_id int8 NOT NULL,
    driver_license_number varchar(50) NOT NULL,
    license_expiry_date date NULL,
    status varchar(20) NULL,
    is_available boolean DEFAULT true,
    CONSTRAINT pk_driver PRIMARY KEY (id),
    CONSTRAINT uk_driver_user_id UNIQUE (user_id),
    CONSTRAINT uk_driver_license_number UNIQUE (driver_license_number),
    CONSTRAINT fk_driver_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
    );


--changeset sousseini:add_column_to_t_user
ALTER TABLE T_USER ADD COLUMN IF NOT EXISTS BIRTH_DATE date null;
ALTER TABLE T_USER ADD COLUMN IF NOT EXISTS BIRTH_PLACE varchar(200) null;


--changeset sousseini:add_column_company_to_driver
ALTER TABLE t_driver ADD COLUMN IF NOT EXISTS agency_id BIGINT;
ALTER TABLE t_driver ADD CONSTRAINT fk_driver_company FOREIGN KEY (agency_id) REFERENCES t_agency(id);


--changeset sousseini:create_table_trip_schedule
CREATE TABLE IF NOT EXISTS t_trip_schedule (
   id bigserial not null,
   trajet_id BIGINT NOT NULL,
   bus_id BIGINT NOT NULL,
   driver_id BIGINT NOT NULL,
   agency_id BIGINT NOT NULL,
   date_depart DATE NOT NULL,
   heure_depart TIME NOT NULL,
   nombre_places_totales INT NOT NULL,
   nombre_places_disponibles INT NOT NULL,
   prix DECIMAL(10, 2) NOT NULL,
   status VARCHAR(20) DEFAULT 'ACTIVE',
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT pk_trip_schedule PRIMARY KEY (id),
   CONSTRAINT fk_trip_schedule_trajet FOREIGN KEY (trajet_id) REFERENCES t_trajet(id),
   CONSTRAINT fk_trip_schedule_bus FOREIGN KEY (bus_id) REFERENCES t_bus(id),
   CONSTRAINT fk_trip_schedule_driver FOREIGN KEY (driver_id) REFERENCES t_driver(id),
   CONSTRAINT fk_trip_schedule_company FOREIGN KEY (agency_id) REFERENCES t_agency(id),

   CONSTRAINT chk_places_totales CHECK (nombre_places_totales > 0),
   CONSTRAINT chk_places_disponibles CHECK (nombre_places_disponibles >= 0),
   CONSTRAINT chk_places_coherence CHECK (nombre_places_disponibles <= nombre_places_totales),
   CONSTRAINT chk_prix CHECK (prix >= 0),
   CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'CANCELLED', 'COMPLETED', 'FULL'))
);


--changeset sousseini:add_column_company_to_ticket
ALTER TABLE t_ticket ADD COLUMN IF NOT EXISTS agency_id BIGINT;
ALTER TABLE t_ticket ADD CONSTRAINT fk_ticket_company FOREIGN KEY (agency_id) REFERENCES t_agency(id);


--changeset sousseini:drop_constraint
ALTER TABLE t_trip_schedule DROP CONSTRAINT chk_places_totales;

ALTER TABLE t_trip_schedule DROP CONSTRAINT chk_places_disponibles;

ALTER TABLE t_trip_schedule DROP CONSTRAINT chk_places_coherence;

ALTER TABLE t_trip_schedule DROP CONSTRAINT chk_prix;

ALTER TABLE t_trip_schedule DROP CONSTRAINT chk_status;

ALTER TABLE t_trip_schedule DROP COLUMN nombre_places_totales;

--changeset sousseini:add_column_to_t_ticket
ALTER TABLE T_TICKET ADD COLUMN CLIENT_NOM VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE T_TICKET ADD COLUMN CLIENT_PRENOM VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE T_TICKET ADD COLUMN CLIENT_CONTACT VARCHAR(20) NOT NULL DEFAULT '';

-- Ajouter les colonnes pour la gestion des réservations
ALTER TABLE T_TICKET ADD COLUMN TYPE_TRANSACTION VARCHAR(20) DEFAULT 'ACHAT';
ALTER TABLE T_TICKET ADD COLUMN DATE_LIMITE_PAIEMENT TIMESTAMP NULL;


CREATE INDEX idx_ticket_type_transaction ON T_TICKET(TYPE_TRANSACTION);
CREATE INDEX idx_ticket_date_limite ON T_TICKET(DATE_LIMITE_PAIEMENT);
CREATE INDEX idx_ticket_client_contact ON T_TICKET(CLIENT_CONTACT);
CREATE INDEX idx_ticket_status_type ON T_TICKET(STATUS, TYPE_TRANSACTION);

--changeset sousseini:create_table_t_company_account
CREATE TABLE IF NOT EXISTS T_COMPANY_ACCOUNT (
     ID BIGSERIAL PRIMARY KEY,
     ACCOUNT_NUMBER VARCHAR(255) UNIQUE,
    ACCOUNT_NAME VARCHAR(255),
    BALANCE DECIMAL(15, 2) DEFAULT 0,
    CREDIT_LIMIT DECIMAL(15, 2) DEFAULT 0,
    TYPE VARCHAR(50) DEFAULT 'PRINCIPAL',
    STATUS VARCHAR(50) DEFAULT 'ACTIVE',
    CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    NOTES TEXT,
    agency_id BIGINT NOT NULL,

    CONSTRAINT fk_company_account_company FOREIGN KEY (agency_id)
    REFERENCES t_agency(ID)
    );

--changeset a.rachad:add_trips_permissions
INSERT INTO t_permission ("name") VALUES ('TRIPS_READ');
INSERT INTO t_permission ("name") VALUES ('TRIPS_ADD');
INSERT INTO t_permission ("name") VALUES ('TRIPS_EDIT');
INSERT INTO t_permission ("name") VALUES ('TRIPS_DELETE');

--changeset a.rachad:add_menu_read_permissions
INSERT INTO t_permission ("name") VALUES ('DASHBOARD_READ');
INSERT INTO t_permission ("name") VALUES ('COMPANIES_READ');
INSERT INTO t_permission ("name") VALUES ('BUS_READ');
INSERT INTO t_permission ("name") VALUES ('COLIS_READ');
INSERT INTO t_permission ("name") VALUES ('TICKETS_READ');
INSERT INTO t_permission ("name") VALUES ('DRIVERS_READ');
INSERT INTO t_permission ("name") VALUES ('PLANNING_READ');
INSERT INTO t_permission ("name") VALUES ('COMPTES_READ');
INSERT INTO t_permission ("name") VALUES ('ROLES_READ');
INSERT INTO t_permission ("name") VALUES ('PERMISSIONS_READ');
INSERT INTO t_permission ("name") VALUES ('USERS_READ');
INSERT INTO t_permission ("name") VALUES ('CONFIGURATIONS_READ');
INSERT INTO t_permission ("name") VALUES ('MY_COMPANY_READ');

--changeset sousseini:drop_constraint_bus
ALTER TABLE t_bus DROP CONSTRAINT IF EXISTS t_bus_numero_key;

--changeset a.rachad:add_agency_id_to_user
ALTER TABLE T_USER ADD COLUMN IF NOT EXISTS AGENCY_ID BIGINT;
ALTER TABLE T_USER ADD CONSTRAINT fk_user_agency FOREIGN KEY (AGENCY_ID) REFERENCES T_AGENCY(ID);


--changeset sousseini:add_user_to_colis
ALTER TABLE T_COLIS ADD COLUMN IF NOT EXISTS USER_ID BIGINT;
ALTER TABLE T_COLIS ADD CONSTRAINT fk_user_colis FOREIGN KEY (USER_ID) REFERENCES T_COLIS(ID);

--changeset sousseini:drop_constraint_user
ALTER TABLE T_USER DROP CONSTRAINT IF EXISTS fk_user_company;
ALTER TABLE T_USER ADD CONSTRAINT fk_user_companies FOREIGN KEY (company_id) REFERENCES t_company(id);

--changeset sousseini:drop_constraint_agency
ALTER TABLE t_agency DROP CONSTRAINT IF EXISTS fk_agency_company;
ALTER TABLE t_agency ADD CONSTRAINT fk_agency_companies FOREIGN KEY (company_id) REFERENCES t_company(id);

--changeset soussein:insert_profile
INSERT INTO t_profile (name) VALUES ('ADMIN_SYSTEM');
INSERT INTO t_profile (name) VALUES ('AGENCY');

--changeset sousseini:add_company_admin_role
INSERT INTO t_role (name) VALUES ('ROLE_COMPANY_ADMIN');

--changeset sousseini:fix_user_fk_colis
ALTER TABLE T_COLIS DROP CONSTRAINT IF EXISTS fk_user_colis;

ALTER TABLE T_COLIS
    ADD CONSTRAINT fk_user_colis
        FOREIGN KEY (USER_ID)
            REFERENCES T_USER(ID);

--changeset sousseini:add_create_at_to_colis
ALTER TABLE T_COLIS ADD COLUMN IF NOT EXISTS CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE T_COLIS ADD COLUMN IF NOT EXISTS UPDATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;


--changeset sousseini:add_ticket_permissions
INSERT INTO t_permission ("name") VALUES ('TICKET_READ');
INSERT INTO t_permission ("name") VALUES ('TICKET_ADD');
INSERT INTO t_permission ("name") VALUES ('TICKET_EDIT');
INSERT INTO t_permission ("name") VALUES ('TICKET_DELETE');

--changeset sousseini:add_bus_permissions
INSERT INTO t_permission ("name") VALUES ('BUS_ADD');
INSERT INTO t_permission ("name") VALUES ('BUS_EDIT');
INSERT INTO t_permission ("name") VALUES ('BUS_DELETE');

--changeset sousseini:add_colis_permissions
INSERT INTO t_permission ("name") VALUES ('COLIS_ADD');
INSERT INTO t_permission ("name") VALUES ('COLIS_EDIT');
INSERT INTO t_permission ("name") VALUES ('COLIS_DELETE');


--changeset sousseini:add_driver_permissions
INSERT INTO t_permission ("name") VALUES ('DRIVER_READ');
INSERT INTO t_permission ("name") VALUES ('DRIVER_ADD');
INSERT INTO t_permission ("name") VALUES ('DRIVER_EDIT');
INSERT INTO t_permission ("name") VALUES ('DRIVER_DELETE');

--changeset sousseini:add_trip_schedule_permissions
INSERT INTO t_permission ("name") VALUES ('TRIP_SCHEDULE_READ');
INSERT INTO t_permission ("name") VALUES ('TRIP_SCHEDULE_ADD');
INSERT INTO t_permission ("name") VALUES ('TRIP_SCHEDULE_EDIT');
INSERT INTO t_permission ("name") VALUES ('TRIP_SCHEDULE_DELETE');

--changeset sousseini:add_user_permissions
INSERT INTO t_permission ("name") VALUES ('USERS_ADD');
INSERT INTO t_permission ("name") VALUES ('USERS_EDIT');
INSERT INTO t_permission ("name") VALUES ('USERS_DELETE');

--changeset sousseini:add_agency_permissions
INSERT INTO t_permission ("name") VALUES ('AGENCY_READ');
INSERT INTO t_permission ("name") VALUES ('AGENCY_ADD');
INSERT INTO t_permission ("name") VALUES ('AGENCY_EDIT');
INSERT INTO t_permission ("name") VALUES ('AGENCY_DELETE');


--changeset sousseini:create_table_t_subscription_plans
CREATE TABLE IF NOT EXISTS t_subscription_plan (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration_in_days INT NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );



--changeset sousseini:create_table_t_subscriptions
CREATE TABLE IF NOT EXISTS t_subscription (
                                              id BIGSERIAL PRIMARY KEY,
                                              company_id BIGINT NOT NULL,
                                              plan_id BIGINT NOT NULL,
                                              start_date DATE NOT NULL,
                                              end_date DATE NOT NULL,
                                              active BOOLEAN DEFAULT true,
                                              auto_renew BOOLEAN DEFAULT false,
                                              cancelled_at DATE,
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                              CONSTRAINT fk_subscription_company FOREIGN KEY (company_id) REFERENCES t_company(id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_plan FOREIGN KEY (plan_id) REFERENCES t_subscription_plan(id)
    );


--changeset sousseini:create_table_t_invoices
CREATE TABLE IF NOT EXISTS t_invoice (
                                         id BIGSERIAL PRIMARY KEY,
                                         company_id BIGINT NOT NULL,
                                         subscription_id BIGINT,
                                         invoice_number VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    payment_date DATE,
    payment_method VARCHAR(50),
    status VARCHAR(30) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_invoice_company FOREIGN KEY (company_id) REFERENCES t_company(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoice_subscription FOREIGN KEY (subscription_id) REFERENCES t_subscription(id)
    );

--changeset sousseini:add_column_created_by_to_colis
ALTER TABLE T_COLIS ADD COLUMN IF NOT EXISTS CREATED_BY BIGINT NULL;
ALTER TABLE T_COLIS ADD CONSTRAINT fk_created_by_colis FOREIGN KEY (CREATED_BY) REFERENCES T_USER(id);

--changeset sousseini:add_column_seat_number_to_t_ticket
ALTER TABLE T_TICKET ADD COLUMN IF NOT EXISTS SEAT_NUMBER BIGINT NULL;

--changeset sousseini:create_table_t_deposit_request_paygate
CREATE TABLE IF NOT EXISTS T_DEPOSIT_REQUEST_PAYGATE(
    id varchar(254) PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    phone_number varchar(20),
    identifier varchar(254),
    network varchar(254),
    tx_reference varchar(254),
    status int,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset sousseini:create_table_t_deposit_response_paygateS
CREATE TABLE IF NOT EXISTS T_DEPOSIT_RESPONSE_PAYGATE(
    id varchar(254) PRIMARY KEY,
    tx_reference varchar(254),
    status varchar(254),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    request varchar(254),
    CONSTRAINT fk_request_deposit_response FOREIGN KEY (request) REFERENCES T_DEPOSIT_REQUEST_PAYGATE(id)

    );


--changeset sousseini:add_column_cancellation_reason_to_t_ticket
ALTER TABLE T_TICKET ADD COLUMN IF NOT EXISTS CANCELLATION_REASON VARCHAR(254) NULL;

--changeset sousseini:add_column_comment_to_t_ticket
ALTER TABLE T_TICKET ADD COLUMN IF NOT EXISTS COMMENT VARCHAR(254) NULL;
