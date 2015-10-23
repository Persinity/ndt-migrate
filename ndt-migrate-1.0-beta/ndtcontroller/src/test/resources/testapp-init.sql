/*******************************************************************************
 * Copyright (c) 2015 Persinity Inc.
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