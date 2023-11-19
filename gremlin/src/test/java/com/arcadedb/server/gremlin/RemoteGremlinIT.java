/*
 * Copyright 2023 Arcade Data Ltd
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.arcadedb.server.gremlin;

import com.arcadedb.gremlin.ArcadeGraph;
import com.arcadedb.query.sql.executor.ResultSet;
import com.arcadedb.remote.RemoteDatabase;
import com.arcadedb.remote.RemoteServer;
import com.arcadedb.server.BaseGraphServerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.in;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class RemoteGremlinIT extends BaseGraphServerTest {
  private static final String DATABASE_NAME = "remote-database";

  @Override
  protected boolean isCreateDatabases() {
    return false;
  }

  @Test
  public void insert() throws Exception {
    testEachServer((serverIndex) -> {
      Assertions.assertTrue(
          new RemoteServer("127.0.0.1", 2480 + serverIndex, "root", BaseGraphServerTest.DEFAULT_PASSWORD_FOR_TESTS).exists(
              DATABASE_NAME));

      final RemoteDatabase database = new RemoteDatabase("127.0.0.1", 2480 + serverIndex, DATABASE_NAME, "root",
          BaseGraphServerTest.DEFAULT_PASSWORD_FOR_TESTS);

      try (final ArcadeGraph graph = ArcadeGraph.open(database)) {
        graph.getDatabase().getSchema().createVertexType("inputstructure");

        //long beginTime = System.currentTimeMillis();

        for (int i = 0; i < 1_000; i++) {
          var v = graph.addVertex(org.apache.tinkerpop.gremlin.structure.T.label, "inputstructure", "json", "{\"name\": \"Elon\"}");
        }

        //System.out.println("TOTAL INSERT: " + (System.currentTimeMillis() - beginTime));
        //beginTime = System.currentTimeMillis();

        try (final ResultSet list = graph.gremlin("g.V().hasLabel(\"inputstructure\")").execute()) {
          //System.out.println("TOTAL QUERY " + list.stream().count() + ": " + (System.currentTimeMillis() - beginTime));
        }
      }
    });
  }

  @Test
  public void dropVertex() throws Exception {
    testEachServer((serverIndex) -> {
      final RemoteDatabase database = new RemoteDatabase("127.0.0.1", 2480 + serverIndex, DATABASE_NAME, "root",
          BaseGraphServerTest.DEFAULT_PASSWORD_FOR_TESTS);

      try (final ArcadeGraph graph = ArcadeGraph.open(database)) {

        graph.traversal().addV("RG").//
            property("name", "r1").//
            addV("RG").//
            property("name", "r2").as("member2").//
            V().//
            has("name", "r1").//
            addE(":TEST").to("member2").//
            addV("RG").//
            property("name", "r3").as("member3").//
            V().//
            has("name", "r1").//
            addE(":TEST").to("member3").//
            addV("RG").//
            property("name", "r4").as("member4").//
            V().//
            has("name", "r2").//
            addE(":TEST").to("member4").//
            addV("RG").//
            property("name", "r5").as("member5").//
            V().//
            has("name", "r3").//
            addE(":TEST").to("member5").//
            V().//
            has("name", "r4").//
            addE(":TEST").to("member5").//
            addV("P").//
            property("name", "p1").as("member6").//
            V().//
            has("name", "r5").//
            addE(":TEST").from("member6").next();

        graph.traversal().V().//
            has("RG", "name", "r4").//
            out().//
            has("RG", "name", "r5").//
            as("deleteEntry").//
            select("deleteEntry").///
            sideEffect(in().hasLabel("P").drop()).//
            sideEffect(select("deleteEntry").drop()).//
            constant("deleted").next();
      }
    });
  }

  @BeforeEach
  public void beginTest() {
    super.beginTest();
    final RemoteServer server = new RemoteServer("127.0.0.1", 2480, "root", BaseGraphServerTest.DEFAULT_PASSWORD_FOR_TESTS);
    if (!server.exists(DATABASE_NAME))
      server.create(DATABASE_NAME);
  }

  @AfterEach
  public void endTest() {
    final RemoteServer server = new RemoteServer("127.0.0.1", 2480, "root", BaseGraphServerTest.DEFAULT_PASSWORD_FOR_TESTS);
    if (server.exists(DATABASE_NAME))
      server.drop(DATABASE_NAME);
    super.endTest();
  }
}
