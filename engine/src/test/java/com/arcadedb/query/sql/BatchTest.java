/*
 * Copyright © 2021-present Arcade Data Ltd (info@arcadedata.com)
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
 *
 * SPDX-FileCopyrightText: 2021-present Arcade Data Ltd (info@arcadedata.com)
 * SPDX-License-Identifier: Apache-2.0
 */
package com.arcadedb.query.sql;

import com.arcadedb.TestHelper;
import com.arcadedb.exception.CommandSQLParsingException;
import com.arcadedb.query.sql.executor.Result;
import com.arcadedb.query.sql.executor.ResultSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BatchTest extends TestHelper {
  @Test
  public void testReturnArrayOnDeprecated() {
    database.transaction(() -> {
      final ResultSet rs = database.command("SQLSCRIPT", "let a = select 1 as result;let b = select 2 as result;return [$a,$b];");

      Assertions.assertTrue(rs.hasNext());
      Result record = rs.next();
      Assertions.assertNotNull(record);
      Assertions.assertEquals("{\"value\":[{\"result\":1}]}", record.toJSON().toString());

      record = rs.next();
      Assertions.assertEquals("{\"value\":[{\"result\":2}]}", record.toJSON().toString());
      Assertions.assertNotNull(record);

      Assertions.assertFalse(rs.hasNext());
    });
  }

  @Test
  public void testReturnArray() {
    database.transaction(() -> {
      final ResultSet rs = database.command("SQLScript", "let a = select 1 as result;let b = select 2 as result;return [$a,$b];");

      Assertions.assertTrue(rs.hasNext());
      Result record = rs.next();
      Assertions.assertNotNull(record);
      Assertions.assertEquals("{\"value\":[{\"result\":1}]}", record.toJSON().toString());

      record = rs.next();
      Assertions.assertEquals("{\"value\":[{\"result\":2}]}", record.toJSON().toString());
      Assertions.assertNotNull(record);

      Assertions.assertFalse(rs.hasNext());
    });
  }

  @Test
  public void testWhile() {
    database.command("sql", "CREATE DOCUMENT TYPE TestWhile");

    String script = "BEGIN;\n" +//
        "LET $i = 0;\n" +//
        "WHILE ($i < 10){\n" +//
        "  INSERT INTO TestWhile SET id = $i;\n" +//
        "  LET $i = $i + 1;\n" +//
        "}" + //
        "COMMIT;";

    database.command("sqlscript", script);

    final ResultSet result = database.query("sql", "select from TestWhile order by id");
    for (int i = 0; i < 10; i++) {
      final Result record = result.next();
      Assertions.assertEquals(i, (int) record.getProperty("id"));
    }
  }

  @Test
  public void testWhileWithReturn() {
    database.command("sql", "CREATE DOCUMENT TYPE TestWhileWithReturn");

    database.transaction(() -> {
      String script = "LET $i = 0;\n" +//
          "WHILE ($i < 10){\n" +//
          "  INSERT INTO TestWhileWithReturn SET id = $i;\n" +//
          "  IF ($i = 4) {" + //
          "    RETURN;" + //
          "  }" + //
          "  LET $i = $i + 1;\n" +//
          "}";

      database.command("sqlscript", script);
    });

    final ResultSet result = database.query("sql", "select from TestWhileWithReturn order by id");
    for (int i = 0; i < 5; i++) {
      final Result record = result.next();
      Assertions.assertEquals(i, (int) record.getProperty("id"));
    }
    Assertions.assertFalse(result.hasNext());
  }

  @Test
  public void testForeach() {
    database.command("sql", "CREATE DOCUMENT TYPE TestForeach");

    String script = "BEGIN;\n" +//
        "FOREACH ($i IN [1, 2, 3]){\n" +//
        "  INSERT INTO TestForeach SET id = $i;\n" +//
        "}" + //
        "COMMIT;";

    database.command("sqlscript", script);

    final ResultSet result = database.query("sql", "select from TestForeach order by id");
    for (int i = 1; i <= 3; i++) {
      final Result record = result.next();
      Assertions.assertEquals(i, (int) record.getProperty("id"));
    }
  }

  @Test
  public void testForeachWithReturn() {
    database.command("sql", "CREATE DOCUMENT TYPE TestForeachWithReturn");

    database.transaction(() -> {
      String script = "FOREACH ($i IN [1, 2, 3]){\n" +//
          "  INSERT INTO TestForeachWithReturn SET id = $i;\n" +//
          "  IF ($i = 1) {" + //
          "    RETURN;" + //
          "  }" + //
          "}";

      database.command("sqlscript", script);
    });

    final ResultSet result = database.query("sql", "select from TestForeachWithReturn order by id");
    for (int i = 1; i <= 1; i++) {
      final Result record = result.next();
      Assertions.assertEquals(i, (int) record.getProperty("id"));
    }
    Assertions.assertFalse(result.hasNext());
  }

  /**
   * Issue https://github.com/ArcadeData/arcadedb/issues/1646
   */
  @Test
  public void testLetUSeRightScope() {
    String script = "LET $list = [];\n"
        + "\n"
        + "FOREACH ($i IN [1, 2, 3]) {\n"
        + "    IF ($i = 3) {\n"
        + "        LET $list = ['HELLO'];\n"
        + "    }\n"
        + "    \n"
        + "}\n"
        + "\n"
        + "IF ($list.size() > 0) {\n"
        + "  RETURN \"List element detected\";\n"
        + "}\n"
        + "\n"
        + "RETURN \"List is empty\";";

    final ResultSet result = database.command("sqlscript", script);
    Assertions.assertTrue(result.hasNext());
    Assertions.assertEquals("List element detected", result.next().getProperty("value"));
  }

  /**
   * Issue https://github.com/ArcadeData/arcadedb/issues/1647
   */
  @Test
  public void testBreakInsideForeach() {
    String script = "LET result = \"Return statement 0\";\n"
        + "FOREACH ($i IN [1, 2, 3]) {\n"
        + "\tLET result = \"Return statement \" + $i;\n"
        + "\tIF( $i = 2 ) {\n"
        + "\t\tBREAK;\n"
        + "\t}\n"
        + "}\n"
        + "\n"
        + "RETURN $result;";

    final ResultSet result = database.command("sqlscript", script);
    Assertions.assertTrue(result.hasNext());
    Assertions.assertEquals("Return statement 2", result.next().getProperty("value"));
  }

  @Test
  public void testUsingReservedVariableNames() {
    try {
      database.command("sqlscript", "FOREACH ($parent IN [1, 2, 3]){\nRETURN;\n}");
      Assertions.fail();
    } catch (CommandSQLParsingException e) {
      // EXPECTED
    }

    try {
      database.command("sqlscript", "LET parent = 33;");
      Assertions.fail();
    } catch (CommandSQLParsingException e) {
      // EXPECTED
    }
  }
}
