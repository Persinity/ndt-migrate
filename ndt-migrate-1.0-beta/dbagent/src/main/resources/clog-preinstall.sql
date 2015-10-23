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
 * Creates CLOG DB objects and logic.
 * Run as NDT DB user before mounting the clog db agent.
 *******************************************************************************/

-- Transaction Log
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE trlog CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
CREATE TABLE trlog (
  tid VARCHAR2(40), 
  last_gid NUMBER(20,0) NOT NULL, 
  tab_name VARCHAR2(30) NOT NULL,
  status CHAR(1)
)
/
ALTER TABLE trlog ADD CONSTRAINT pk_trlog PRIMARY KEY (tid, tab_name)
/
