/*******************************************************************************
 * Copyright (c) 2015 Persinity Inc.
 * Re/creates testapp and ndt dst DB users. Execute as admin.
 *******************************************************************************/
BEGIN
	EXECUTE IMMEDIATE 'DROP USER testapp1 CASCADE';
	EXECUTE IMMEDIATE 'DROP USER ndtdst CASCADE';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE USER testapp1 IDENTIFIED BY testapp1
/
GRANT connect, resource TO testapp1
/

CREATE USER ndtdst IDENTIFIED BY ndtdst
/
GRANT connect, resource TO ndtdst
/