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
 * Re/creates procedure(s) for testing window generators.
 *******************************************************************************/
CREATE OR REPLACE PROCEDURE gen_test_trlog (i_rec_count IN pls_integer)
  -- Generates i_rec_count trlog entries, half of each are in 'R' status, the other in 'C'.
  -- The generated records are ordered reversely to test that the transactions are extracted in the right order.
IS
  j PLS_INTEGER;
  l_status CHAR(1);
BEGIN
  DELETE FROM trlog;
  FOR i IN 1..i_rec_count LOOP
    j :=  (i_rec_count - i + 1);
    IF MOD(j, 2) = 1 THEN
      l_status := 'R';
    ELSE
      l_status := 'C';
    END IF;
    INSERT INTO trlog (tid, tab_name, last_gid, status) VALUES ('T'||j, 'tab'||j, j, l_status);
  END LOOP;
  COMMIT;
END;
/
