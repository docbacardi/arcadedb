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
/* Generated By:JJTree: Do not edit this line. OTraverseStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_USERTYPE_VISIBILITY_PUBLIC=true */
package com.arcadedb.query.sql.parser;

import com.arcadedb.database.Database;
import com.arcadedb.exception.CommandSQLParsingException;
import com.arcadedb.query.sql.executor.BasicCommandContext;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.executor.InternalExecutionPlan;
import com.arcadedb.query.sql.executor.ResultSet;
import com.arcadedb.query.sql.executor.TraverseExecutionPlanner;

import java.util.*;
import java.util.stream.*;

public class TraverseStatement extends Statement {
  public enum Strategy {
    DEPTH_FIRST, BREADTH_FIRST
  }

  protected List<TraverseProjectionItem> projections = new ArrayList<TraverseProjectionItem>();
  protected FromClause                   target;
  protected WhereClause                  whileClause;
  protected Skip                         skip;
  protected Strategy                     strategy;
  protected PInteger                     maxDepth;

  public TraverseStatement(final int id) {
    super(id);
  }

  public void validate() throws CommandSQLParsingException {
//    for(OTraverseProjectionItem projection:projections) {
//
//        projection. validate();
//        if (projection.isExpand() && groupBy != null) {
//          throw new OCommandSQLParsingException("expand() cannot be used together with GROUP BY");
//        }
//
//    }
    if (target.getItem().getStatement() != null) {
      target.getItem().getStatement().validate();
    }
  }

  @Override
  public ResultSet execute(final Database db, final Object[] args, final CommandContext parentcontext, final boolean usePlanCache) {
    final BasicCommandContext context = new BasicCommandContext();
    if (parentcontext != null) {
      context.setParentWithoutOverridingChild(parentcontext);
    }
    context.setDatabase(db);
    context.setInputParameters(args);
    final InternalExecutionPlan executionPlan = createExecutionPlan(context);

    return new LocalResultSet(executionPlan);
  }

  @Override
  public ResultSet execute(final Database db, final Map<String, Object> params, final CommandContext parentcontext,
      final boolean usePlanCache) {
    final BasicCommandContext context = new BasicCommandContext();
    if (parentcontext != null) {
      context.setParentWithoutOverridingChild(parentcontext);
    }
    context.setDatabase(db);
    context.setInputParameters(params);
    final InternalExecutionPlan executionPlan = createExecutionPlan(context);

    return new LocalResultSet(executionPlan);
  }

  public InternalExecutionPlan createExecutionPlan(final CommandContext context) {
    final TraverseExecutionPlanner planner = new TraverseExecutionPlanner(this);
    return planner.createExecutionPlan(context);
  }

  public void toString(final Map<String, Object> params, final StringBuilder builder) {
    builder.append("TRAVERSE ");
    boolean first = true;
    for (final TraverseProjectionItem item : projections) {
      if (!first) {
        builder.append(", ");
      }
      item.toString(params, builder);
      first = false;
    }

    if (target != null) {
      builder.append(" FROM ");
      target.toString(params, builder);
    }

    if (maxDepth != null) {
      builder.append(" MAXDEPTH ");
      maxDepth.toString(params, builder);
    }

    if (whileClause != null) {
      builder.append(" WHILE ");
      whileClause.toString(params, builder);
    }

    if (limit != null) {
      builder.append(" ");
      limit.toString(params, builder);
    }

    if (strategy != null) {
      builder.append(" strategy ");
      switch (strategy) {
      case BREADTH_FIRST:
        builder.append("breadth_first");
        break;
      case DEPTH_FIRST:
        builder.append("depth_first");
        break;
      default:
        throw new IllegalArgumentException("Strategy " + strategy + " not supported");
      }
    }

  }

  public boolean refersToParent() {
    if (projections != null && projections.stream().anyMatch(x -> x.refersToParent()))
      return true;

    if (this.target != null && this.target.refersToParent())
      return true;

    if (this.whileClause != null && this.whileClause.refersToParent())
      return true;

    return false;
  }

  @Override
  public Statement copy() {
    final TraverseStatement result = new TraverseStatement(-1);
    result.projections = projections == null ? null : projections.stream().map(x -> x.copy()).collect(Collectors.toList());
    result.target = target == null ? null : target.copy();
    result.whileClause = whileClause == null ? null : whileClause.copy();
    result.limit = limit == null ? null : limit.copy();
    result.strategy = strategy;
    result.maxDepth = maxDepth == null ? null : maxDepth.copy();
    return result;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    final TraverseStatement that = (TraverseStatement) o;

    if (!Objects.equals(projections, that.projections))
      return false;
    if (!Objects.equals(target, that.target))
      return false;
    if (!Objects.equals(whileClause, that.whileClause))
      return false;
    if (!Objects.equals(limit, that.limit))
      return false;
    if (strategy != that.strategy)
      return false;
    return Objects.equals(maxDepth, that.maxDepth);
  }

  @Override
  public int hashCode() {
    int result = projections != null ? projections.hashCode() : 0;
    result = 31 * result + (target != null ? target.hashCode() : 0);
    result = 31 * result + (whileClause != null ? whileClause.hashCode() : 0);
    result = 31 * result + (limit != null ? limit.hashCode() : 0);
    result = 31 * result + (strategy != null ? strategy.hashCode() : 0);
    result = 31 * result + (maxDepth != null ? maxDepth.hashCode() : 0);
    return result;
  }

  @Override
  public boolean isIdempotent() {
    return true;
  }

  public List<TraverseProjectionItem> getProjections() {
    return projections;
  }

  public void setProjections(final List<TraverseProjectionItem> projections) {
    this.projections = projections;
  }

  public FromClause getTarget() {
    return target;
  }

  public void setTarget(final FromClause target) {
    this.target = target;
  }

  public WhereClause getWhileClause() {
    return whileClause;
  }

  public void setWhileClause(final WhereClause whileClause) {
    this.whileClause = whileClause;
  }

  public Strategy getStrategy() {
    return strategy;
  }

  public void setStrategy(final Strategy strategy) {
    this.strategy = strategy;
  }

  public PInteger getMaxDepth() {
    return maxDepth;
  }

  public void setMaxDepth(final PInteger maxDepth) {
    this.maxDepth = maxDepth;
  }

  public Skip getSkip() {
    return skip;
  }

  public void setSkip(final Skip skip) {
    this.skip = skip;
  }
}
/* JavaCC - OriginalChecksum=47399a3a3d5a423768bbdc70ee957464 (do not edit this line) */
