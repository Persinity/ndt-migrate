/*******************************************************************************
 * Copyright (c) 2015 Persinity Inc.
 * Creates CLOG DB objects and logic.
 * Run as NDT DB user before mounting the clog db agent.
 *******************************************************************************/

-- Transaction Log
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE trlog CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
CREATE TABLE trlog (
  tid VARCHAR2(40), 
  last_gid NUMBER(20,0) NOT NULL, 
  tab_name VARCHAR2(30) NOT NULL,
  status CHAR(1)
)
/
ALTER TABLE trlog ADD CONSTRAINT pk_trlog PRIMARY KEY (tid, tab_name)
/
