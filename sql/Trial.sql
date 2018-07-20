-- Table Definition ----------------------------------------------

CREATE TABLE trial (
    id integer DEFAULT nextval('"Trial_id_seq"'::regclass) PRIMARY KEY,
    created timestamp without time zone NOT NULL DEFAULT now(),
    user jsonb NOT NULL,
    info text
);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX "Trials_pkey" ON trial(id int4_ops);
