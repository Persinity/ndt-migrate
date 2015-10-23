/*******************************************************************************
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
CREATE OR REPLACE PACKAGE ndt_schema
  AUTHID CURRENT_USER
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   *******************************************************************************/
IS

  TYPE varchar2_tab_typ IS TABLE OF VARCHAR2(4000);

  -- Stores table(s) contents into tmp table(s)
  PROCEDURE back_up_tab(i_tab_name VARCHAR2);
  PROCEDURE back_up(i_tab_names varchar2_tab_typ);
  -- Restores table(s) contents from tmp table(s) (see back_up_tab)
  PROCEDURE restore_tab(i_tab_name VARCHAR2);
  PROCEDURE restore(i_tab_names varchar2_tab_typ);
  -- Truncates table
  PROCEDURE truncate_tab(i_tab_name VARCHAR2);
  -- Enable/disable referential integrity constraints
  PROCEDURE integrity_enable;
  PROCEDURE integrity_disable;

END;
/

CREATE OR REPLACE PACKAGE BODY ndt_schema
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   *******************************************************************************/
IS
  CONS_TYPE_REF CONSTANT CHAR(1) := 'R';
  CONS_ENABLED CONSTANT CHAR(7) := 'ENABLED';
  CONS_DISABLED CONSTANT CHAR(8) := 'DISABLED';

  PROCEDURE integrity_enable IS
  BEGIN
    FOR i IN
      (SELECT table_name, constraint_name -- enable the FKs
      FROM user_constraints
      WHERE constraint_type = CONS_TYPE_REF
      AND status            = CONS_DISABLED)
    LOOP
      --DBMS_OUTPUT.PUT_LINE('alter table '||i.table_name||' enable constraint ' ||i.constraint_name);
      EXECUTE IMMEDIATE 'alter table '||i.table_name||' enable constraint ' ||i.constraint_name;
    END LOOP i;
  END;

  PROCEDURE integrity_disable IS
  BEGIN
    FOR i IN
      (SELECT table_name, constraint_name -- disable the FKs
      FROM user_constraints
      WHERE constraint_type = CONS_TYPE_REF
      AND status            = CONS_ENABLED)
    LOOP
      --DBMS_OUTPUT.PUT_LINE('alter table '||i.table_name||' disable constraint ' ||i.constraint_name);
      EXECUTE IMMEDIATE 'alter table '||i.table_name||' disable constraint ' ||i.constraint_name;
    END LOOP i;
  END;

  PROCEDURE restore_tab(i_tab_name VARCHAR2) IS
  BEGIN
    EXECUTE IMMEDIATE 'INSERT INTO '||i_tab_name||' SELECT * FROM '||i_tab_name||'_';
  END;

  PROCEDURE back_up_tab(i_tab_name VARCHAR2) IS
  BEGIN
    BEGIN
      EXECUTE IMMEDIATE 'DROP TABLE '||i_tab_name||'_';
    EXCEPTION WHEN OTHERS THEN NULL;
    END;
    EXECUTE IMMEDIATE 'CREATE TABLE '||i_tab_name||'_ AS SELECT * FROM '||i_tab_name;
  END;

  PROCEDURE truncate_tab(i_tab_name VARCHAR2) IS
  BEGIN
    --DBMS_OUTPUT.PUT_LINE('DELETE FROM '||i_tab_name);
    EXECUTE IMMEDIATE 'DELETE FROM '||i_tab_name;
  END;

  PROCEDURE restore(i_tab_names varchar2_tab_typ) IS
  -- restores BL data (customer related tables)
  BEGIN
    ndt_schema.integrity_disable; -- to enable restore of subset of the tab graph
    FOR i IN REVERSE 1..i_tab_names.COUNT LOOP -- clear child first
      truncate_tab(i_tab_names(i));
    END LOOP;
    FOR i IN 1..i_tab_names.COUNT LOOP -- populate parent first
      restore_tab(i_tab_names(i));
    END LOOP;
    ndt_schema.integrity_enable;
  END;

  PROCEDURE back_up(i_tab_names varchar2_tab_typ) IS
  -- saves BL data (customer related tables)
  BEGIN
    FOR i IN 1..i_tab_names.COUNT LOOP
      back_up_tab(i_tab_names(i));
    END LOOP;
  END;
END;
/
