/*******************************************************************************
 * Copyright (c) 2015 Persinity Inc.
 * Creates CDC related DB objects and logic.
 * Run as NDT DB user after mouning the clog db agent and pass as parameter the DB username.
 *******************************************************************************/

-- Optionally uncomment next lines to run from SQL*Plus
-- WHENEVER sqlerror EXIT sql.sqlcode;
-- PROMPT #### NDT DB agent pre install ####
-- SET VERIFY OFF
-- SET AUTOPRINT OFF

-- Global Change Log sequence
BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE seq_gid';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
CREATE SEQUENCE seq_gid
  START WITH 1
  -- important: request order must be preserved
  ORDER
  NOMINVALUE NOMAXVALUE -- think of cycle
  -- adjust according load (changes generated)
  CACHE 10
/

CREATE OR REPLACE PACKAGE clog
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   * CLOG related logic
   *******************************************************************************/
IS
  PROCEDURE trlog_new_entry_for(i_clog_name VARCHAR2, i_tid VARCHAR2);
END clog;
/
CREATE OR REPLACE PACKAGE BODY clog
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   *******************************************************************************/
IS
  PROCEDURE trlog_new_entry_for(i_clog_name VARCHAR2, i_tid VARCHAR2)
    -- Creates new transaction log entry
  IS
    l_trlog_rec trlog%ROWTYPE;
    l_trlog_sql VARCHAR2(1024);
    l_status CONSTANT VARCHAR2(1) := 'R';
  BEGIN
    -- Get last GID for given ulog and transaction id
    l_trlog_sql :=
    'MERGE INTO trlog d
       USING (SELECT tid, MAX(gid) AS last_gid FROM '||i_clog_name||' WHERE tid = :tid GROUP BY tid) s
       ON (d.tid = s.tid AND d.tab_name = :clog_name)
       WHEN MATCHED THEN
         UPDATE SET d.last_gid = s.last_gid
       WHEN NOT MATCHED THEN
         INSERT (d.tid, d.last_gid, d.tab_name, d.status)
         VALUES (s.tid, s.last_gid, :clog_name, :status)';
    --DBMS_OUTPUT.PUT_LINE(l_getmax_gid_sql);
    EXECUTE IMMEDIATE l_trlog_sql USING i_tid, i_clog_name, i_clog_name, l_status;
  EXCEPTION WHEN OTHERS THEN
    ndt_common.log(ndt_common.error, $$PLSQL_UNIT||':'||$$PLSQL_LINE, SQLCODE||':'||SQLERRM);
  END;
END clog;
/
