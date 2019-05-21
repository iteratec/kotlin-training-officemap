CREATE SEQUENCE hibernate_sequence
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

CREATE TABLE workplace (
    id bigint NOT NULL PRIMARY KEY,
    name text UNIQUE,
    map_id text NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL
);

CREATE TABLE reservation (
    id bigint NOT NULL PRIMARY KEY,
    end_date bigint NOT NULL,
    start_date bigint NOT NULL,
    username text NOT NULL,
    workplace_id bigint NOT NULL,
    CONSTRAINT reservation_workplace_id_foreign_key FOREIGN KEY (workplace_id) REFERENCES workplace(id) ON DELETE CASCADE
);
