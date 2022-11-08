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
package com.arcadedb.query.sql.function.conversion;

import com.arcadedb.database.Identifiable;
import com.arcadedb.log.LogManager;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.method.misc.AbstractSQLMethod;

import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 * Transforms a value to datetime. If the conversion is not possible, null is returned.
 *
 * @author Johann Sorel (Geomatys)
 * @author Luca Garulli (l.garulli--(at)--gmail.com)
 */
public class SQLMethodAsDateTime extends AbstractSQLMethod {

  public static final String NAME = "asdatetime";

  public SQLMethodAsDateTime() {
    super(NAME, 0, 1);
  }

  @Override
  public String getSyntax() {
    return "asDatetime([<format>])";
  }

  @Override
  public Object execute(Object iThis, Identifiable iCurrentRecord, CommandContext iContext, Object ioResult, Object[] iParams) {
    if (iThis != null) {
      if (iThis instanceof Date) {
        return iThis;
      } else if (iThis instanceof Number) {
        return new Date(((Number) iThis).longValue());
      } else {
        try {
          final String format = iParams.length > 0 ? iParams[0].toString() : iContext.getDatabase().getSchema().getDateTimeFormat();
          return new SimpleDateFormat(format).parse(iThis.toString());
        } catch (ParseException e) {
          LogManager.instance().log(this, Level.SEVERE, "Error during %s method execution", e, NAME);
          // IGNORE IT: RETURN NULL
        }
      }
    }
    return null;
  }
}
