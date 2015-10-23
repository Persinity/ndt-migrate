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