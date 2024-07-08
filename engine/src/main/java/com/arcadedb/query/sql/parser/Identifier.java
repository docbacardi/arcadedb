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
/* Generated By:JJTree: Do not edit this line. OIdentifier.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_USERTYPE_VISIBILITY_PUBLIC=true */
package com.arcadedb.query.sql.parser;

import com.arcadedb.query.sql.executor.CommandContext;

import java.util.*;

/**
 * This class is used to represent all the identifies in the SQL grammar, ie. class names, property names, index names, variables
 * and so on so forth.
 * <p>
 * Instances of this class are immutable and can be recycled multiple times in the same or in different queries.
 */
public class Identifier extends SimpleNode {
  private   String  value;
  protected boolean quoted = false;

  /**
   * set to true by the query executor/optimizer for internally generated aliases for query optimization
   */
  protected boolean internalAlias = false;

  public Identifier(final Identifier copyFrom, final boolean quoted) {
    this(-1);
    this.value = copyFrom.value;
    this.quoted = quoted;
  }

  public Identifier(final String content) {
    this(-1);
    setStringValue(content);
  }

  protected Identifier(final int id) {
    super(id);
  }

  /**
   * returns the value as is, with back-ticks quoted with backslash
   *
   * @return
   */
  public String getValue() {
    return value;
  }

  /**
   * returns the plain string representation of this identifier, with quoting removed from back-ticks
   *
   * @return
   */
  public String getStringValue() {
    return value;
  }

  /**
   * sets the value of the identifier. It can contain any values, this method can manage back-ticks (internally quote them), so
   * back-ticks have not to be quoted when passed as a parameter
   *
   * @param s
   */
  public void setStringValue(final String s) {
    if (s == null)
      value = null;
    else if (s.contains("`"))
      value = s.replace("`", "\\`");
    else
      value = s;
  }

  public void setQuotedStringValue(final String s) {
    quoted = true;
    setStringValue(s.substring(1, s.length() - 1));
  }

  @Override
  public String toString(final String prefix) {
    if (quoted)
      return '`' + value + '`';
    return value;
  }

  public String toString() {
    return toString("");
  }

  public void toString(final Map<String, Object> params, final StringBuilder builder) {
    if (quoted)
      builder.append('`' + value + '`');
    else
      builder.append(value);
  }

  public Identifier copy() {
    return this;
  }

  @Override
  public boolean refersToParent() {
    return "$parent".equals(value);
  }

  public boolean isEarlyCalculated(CommandContext ctx) {
    if (internalAlias)
      return true;

    String stringVal = getStringValue();
    if (ctx.isScriptVariableDeclared(stringVal))
      return true; // context variable, for batch scripts

    return false;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    final Identifier that = (Identifier) o;

    if (quoted != that.quoted)
      return false;
    if (internalAlias != that.internalAlias)
      return false;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (quoted ? 1 : 0);
    result = 31 * result + (internalAlias ? 1 : 0);
    return result;
  }
}
/* JavaCC - OriginalChecksum=691a2eb5096f7b5e634b2ca8ac2ded3a (do not edit this line) */
