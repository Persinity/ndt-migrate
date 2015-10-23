DELETE FROM kid;
DELETE FROM emp;
DELETE FROM dept;

BEGIN
  FOR i IN 1..500 LOOP
    INSERT INTO dept (ID, NAME) VALUES (i, 'dept'||i);
    INSERT INTO emp (ID, NAME, dept_id) VALUES (i, 'emp'||i, i);
    IF MOD(i, 5) = 0 THEN
      COMMIT;
    END IF;
  END LOOP;
END;
/

SELECT COUNT(1) FROM dept;
SELECT COUNT(1) FROM emp;

