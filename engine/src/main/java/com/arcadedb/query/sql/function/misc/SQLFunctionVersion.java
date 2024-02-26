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
package com.arcadedb.query.sql.function.misc;

import com.arcadedb.Constants;
import com.arcadedb.database.Identifiable;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.function.SQLFunctionAbstract;

import java.util.*;

/**
 * Generates a UUID as a 128-bits value using the Leach-Salz variant. For more information look at:
 * http://docs.oracle.com/javase/6/docs/api/java/util/UUID.html.
 *
 * @author Luca Garulli (l.garulli--(at)--gmail.com)
 */
public class SQLFunctionVersion extends SQLFunctionAbstract {
  public static final String NAME = "version";

  /**
   * Get the date at construction to have the same date for all the iteration.
   */
  public SQLFunctionVersion() {
    super(NAME);
  }

  public Object execute(final Object iThis, final Identifiable iCurrentRecord, final Object iCurrentResult, final Object[] iParams,
      final CommandContext iContext) {
    return Constants.getVersion();
  }

  @Override
  public boolean aggregateResults() {
    return false;
  }

  public String getSyntax() {
    return "version()";
  }

  @Override
  public Object getResult() {
    return null;
  }
}
