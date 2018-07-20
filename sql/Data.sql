-- Table Definition ----------------------------------------------

CREATE TABLE data (
    timestamp timestamp without time zone NOT NULL,
    reading jsonb NOT NULL,
    device integer NOT NULL,
    trial_id integer REFERENCES trial(id) ON DELETE CASCADE,
    id integer DEFAULT nextval('"Data_id_seq"'::regclass) PRIMARY KEY,
    algorithms jsonb
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX "Data_pkey" ON data(id int4_ops);
