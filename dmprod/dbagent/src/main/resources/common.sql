/*******************************************************************************
 * Copyright (c) 2015 Persinity Inc.
 *******************************************************************************/
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE ndt_log';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE TABLE ndt_log (
  log_date DATE NOT NULL,
  log_level VARCHAR2(20) NOT NULL,
  originator VARCHAR2(128),
  log_msg VARCHAR2(512)
)
/

CREATE OR REPLACE PACKAGE ndt_common
  AUTHID DEFINER
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   *******************************************************************************/
IS

  -- Logs a message. Does not affect caller on logging failure.
  ERROR CONSTANT PLS_INTEGER := 1;
  INFO CONSTANT PLS_INTEGER := 2;
  DEBUG CONSTANT PLS_INTEGER := 3;
  SUBTYPE log_level IS PLS_INTEGER RANGE 1..3 NOT NULL;
  PROCEDURE log(i_level log_level, i_originator VARCHAR2, i_msg VARCHAR2);

END;
/

CREATE OR REPLACE PACKAGE BODY ndt_common
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   *******************************************************************************/
IS
  CONS_TYPE_REF CONSTANT CHAR(1) := 'R';
  CONS_ENABLED CONSTANT CHAR(7) := 'ENABLED';
  CONS_DISABLED CONSTANT CHAR(8) := 'DISABLED';

  PROCEDURE log(i_level log_level, i_originator VARCHAR2, i_msg VARCHAR2) IS
    PRAGMA AUTONOMOUS_TRANSACTION;
    l_msg VARCHAR2(512);
    l_originator VARCHAR2(128);
  BEGIN
    l_originator := SUBSTR(i_originator, 1, 128);
    l_msg := SUBSTR(i_msg, 1, 512);
    -- TODO get rid of the hardcoded schema ndt as this package can be used on destination or custom env.
    INSERT INTO ndt_log (log_date, log_level, originator, log_msg) VALUES (SYSDATE, DECODE(i_level, 1, 'ERROR', 2, 'INFO', 3, 'DEBUG'), l_originator, l_msg);
    COMMIT;
  EXCEPTION WHEN OTHERS THEN
    ROLLBACK;
  END;

END;
/
