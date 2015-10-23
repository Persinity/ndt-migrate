/*******************************************************************************
 * Copyright (c) 2015 Persinity Inc.
 * Re/creates testapp and ndt src DB users. Execute as admin.
 *******************************************************************************/
BEGIN
	EXECUTE IMMEDIATE 'DROP USER testapp CASCADE';
	EXECUTE IMMEDIATE 'DROP USER ndtsrc CASCADE';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

CREATE USER testapp IDENTIFIED BY testapp
/
GRANT connect, resource TO testapp
/

CREATE USER ndtsrc BY ndtsrc
/
GRANT connect, resource TO ndtsrc
/