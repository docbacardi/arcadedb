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
/* Generated By:JJTree: Do not edit this line. OCreatePropertyAttributeStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_USERTYPE_VISIBILITY_PUBLIC=true */
package com.arcadedb.query.sql.parser;

import com.arcadedb.database.Identifiable;
import com.arcadedb.exception.CommandExecutionException;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.schema.Property;

import java.util.*;

public class CreatePropertyAttributeStatement extends SimpleNode {
  public Identifier settingName;
  public Expression settingValue;

  public CreatePropertyAttributeStatement(final int id) {
    super(id);
  }

  @Override
  public void toString(final Map<String, Object> params, final StringBuilder builder) {
    settingName.toString(params, builder);
    if (settingValue != null) {
      builder.append(" ");
      settingValue.toString(params, builder);
    }
  }

  public CreatePropertyAttributeStatement copy() {
    final CreatePropertyAttributeStatement result = new CreatePropertyAttributeStatement(-1);
    result.settingName = settingName == null ? null : settingName.copy();
    result.settingValue = settingValue == null ? null : settingValue.copy();
    return result;
  }

  public Object setOnProperty(final Property internalProp, final CommandContext context) {
    final String attrName = settingName.getStringValue();
    final Object attrValue = this.settingValue == null ? true : this.settingValue.execute((Identifiable) null, context);
    try {
      if (attrName.equalsIgnoreCase("readonly")) {
        internalProp.setReadonly((boolean) attrValue);
      } else if (attrName.equalsIgnoreCase("mandatory")) {
        internalProp.setMandatory((boolean) attrValue);
      } else if (attrName.equalsIgnoreCase("notnull")) {
        internalProp.setNotNull((boolean) attrValue);
      } else if (attrName.equalsIgnoreCase("max")) {
        internalProp.setMax("" + attrValue);
      } else if (attrName.equalsIgnoreCase("min")) {
        internalProp.setMin("" + attrValue);
      } else if (attrName.equalsIgnoreCase("default")) {
        if (this.settingValue == null)
          throw new CommandExecutionException("Default value not set");
        internalProp.setDefaultValue(this.settingValue.toString());
      } else if (attrName.equalsIgnoreCase("regexp")) {
        internalProp.setRegexp("" + attrValue);
      } else {
        throw new CommandExecutionException("Invalid attribute definition: '" + attrName + "'");
      }
    } catch (final Exception e) {
      throw new CommandExecutionException("Cannot set attribute on property " + settingName.getStringValue() + " " + attrValue, e);
    }
    return attrValue;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    final CreatePropertyAttributeStatement that = (CreatePropertyAttributeStatement) o;

    if (!Objects.equals(settingName, that.settingName))
      return false;
    return Objects.equals(settingValue, that.settingValue);
  }

  @Override
  public int hashCode() {
    int result = settingName != null ? settingName.hashCode() : 0;
    result = 31 * result + (settingValue != null ? settingValue.hashCode() : 0);
    return result;
  }
}
/* JavaCC - OriginalChecksum=6a7964c2b9dad541ca962eecea00651b (do not edit this line) */
