CREATE SEQUENCE hibernate_sequence
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

CREATE TABLE workplace (
    id bigint NOT NULL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    map_id VARCHAR(255) NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL
);

CREATE TABLE reservation (
    id bigint NOT NULL PRIMARY KEY,
    end_date date NOT NULL,
    start_date date NOT NULL,
    username VARCHAR(255) NOT NULL,
    workplace_id bigint NOT NULL,
    CONSTRAINT reservation_workplace_id_foreign_key FOREIGN KEY (workplace_id) REFERENCES workplace(id) ON DELETE CASCADE
);
