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
