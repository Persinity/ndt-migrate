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

CREATE OR REPLACE PACKAGE ndt_security
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   *******************************************************************************/
  -- Common security utils
  -- This package should be self sufficient to avoid deadlock while trying to encrypt package it depends on
IS
  -- Obfuscate sensitive NDT logic so that it is not exposed to the public
  PROCEDURE ndt_obfuscate_packages;
END ndt_security;
/

CREATE OR REPLACE PACKAGE BODY ndt_security
  /*******************************************************************************
   * Copyright (c) 2015 Persinity Inc.
   *******************************************************************************/
IS

  TYPE varchar2_tab_typ IS TABLE OF VARCHAR2(4000);
  -- It is safe to list the union of src and target packages, as if package is not found, it is not encrypted
  g_ndt_package_names varchar2_tab_typ := varchar2_tab_typ('ETL', 'MIGR8', 'NDT', 'NDT_SRC', 'NDT_COMMON', 'NDT_DEMO', 'ULOG', 'ULOG_COMMON');

  PROCEDURE wrap(i_package_name VARCHAR2, i_package_type VARCHAR2, i_object_type VARCHAR2)
  IS
    l_ddl_sql VARCHAR2(32767);
    l_cnt NUMBER;
  BEGIN
    SELECT COUNT(1) INTO l_cnt
      FROM user_objects 
      WHERE object_type  = i_object_type AND object_name = i_package_name;
    IF l_cnt > 0 THEN
      dbms_output.put_line('Wrapping '||i_package_type||' '||i_package_name);
      l_ddl_sql := dbms_metadata.get_ddl(i_package_type, i_package_name);
      EXECUTE IMMEDIATE dbms_ddl.wrap(l_ddl_sql);
    END IF;
  EXCEPTION 
    WHEN OTHERS THEN
      -- Wrap attempt on already wrapped packages causes "Insufficient privs error"
      NULL;
  END;
  
  PROCEDURE wrap_package(i_package_name VARCHAR2)
  IS
  BEGIN
    wrap(i_package_name, 'PACKAGE_SPEC', 'PACKAGE');
    wrap(i_package_name, 'PACKAGE_BODY', 'PACKAGE BODY');
  END;

  PROCEDURE ndt_obfuscate_packages(i_package_names varchar2_tab_typ) IS
  BEGIN
    FOR i IN 1..i_package_names.COUNT LOOP
      wrap_package(i_package_names(i));
    END LOOP;
  END;

  PROCEDURE ndt_obfuscate_packages IS
  BEGIN
    ndt_obfuscate_packages(g_ndt_package_names);
  END;

END ndt_security;
/