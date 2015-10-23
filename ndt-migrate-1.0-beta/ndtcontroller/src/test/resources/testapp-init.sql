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
/*******************************************************************************
 * Re/creates testapp schema. Execute as testapp user.
 *******************************************************************************/
BEGIN 
	EXECUTE IMMEDIATE 'DROP TABLE kid';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
	EXECUTE IMMEDIATE 'DROP TABLE emp CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
	EXECUTE IMMEDIATE 'DROP TABLE dept';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
	EXECUTE IMMEDIATE 'DROP TABLE test_skiptable1';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
	EXECUTE IMMEDIATE 'DROP TABLE test_skiptable2';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/


CREATE TABLE dept (
	id NUMBER(9) CONSTRAINT pk_dept PRIMARY KEY,
	name VARCHAR2(20),
	mngr_id NUMBER(9)
)
/

CREATE TABLE emp (
	id NUMBER(9) CONSTRAINT pk_emp PRIMARY KEY,
	bin_id BLOB,
	name VARCHAR2(20),
	dept_id NUMBER(9) NOT NULL
)
/

CREATE TABLE kid (
	id NUMBER(9),
	sid VARCHAR2(10),
	name VARCHAR2(20),
	emp_id NUMBER(9),
	CONSTRAINT pk_kid PRIMARY KEY (id, sid)
)
/

CREATE TABLE test_skiptable1 (ID NUMBER(1))
/

CREATE TABLE test_skiptable2 (ID NUMBER(1))
/

ALTER TABLE dept ADD CONSTRAINT fk_dept2emp FOREIGN KEY (mngr_id) REFERENCES emp (id)
/

ALTER TABLE emp ADD CONSTRAINT fk_emp2dept FOREIGN KEY (dept_id) REFERENCES dept (id)
/

ALTER TABLE kid ADD CONSTRAINT fk_kid2emp FOREIGN KEY (emp_id) REFERENCES emp (id)
/