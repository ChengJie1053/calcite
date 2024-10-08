/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.sql;

import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlVisitor;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.util.ImmutableNullableList;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

import static java.util.Objects.requireNonNull;

/**
 * SqlNode for MATCH_RECOGNIZE clause.
 */
public class SqlMatchRecognize extends SqlCall {
  public static final int OPERAND_TABLE_REF = 0;
  public static final int OPERAND_PATTERN = 1;
  public static final int OPERAND_STRICT_START = 2;
  public static final int OPERAND_STRICT_END = 3;
  public static final int OPERAND_PATTERN_DEFINES = 4;
  public static final int OPERAND_MEASURES = 5;
  public static final int OPERAND_AFTER = 6;
  public static final int OPERAND_SUBSET = 7;
  public static final int OPERAND_ROWS_PER_MATCH = 8;
  public static final int OPERAND_PARTITION_BY = 9;
  public static final int OPERAND_ORDER_BY = 10;
  public static final int OPERAND_INTERVAL = 11;

  public static final SqlPrefixOperator SKIP_TO_FIRST =
      new SqlPrefixOperator("SKIP TO FIRST", SqlKind.SKIP_TO_FIRST, 20, null,
          null, null);

  public static final SqlPrefixOperator SKIP_TO_LAST =
      new SqlPrefixOperator("SKIP TO LAST", SqlKind.SKIP_TO_LAST, 20, null,
          null, null);

  //~ Instance fields -------------------------------------------

  private SqlNode tableRef;
  private SqlNode pattern;
  private SqlLiteral strictStart;
  private SqlLiteral strictEnd;
  private SqlNodeList patternDefList;
  private SqlNodeList measureList;
  private @Nullable SqlNode after;
  private SqlNodeList subsetList;
  private @Nullable SqlLiteral rowsPerMatch;
  private SqlNodeList partitionList;
  private SqlNodeList orderList;
  private @Nullable SqlLiteral interval;

  /** Creates a SqlMatchRecognize. */
  public SqlMatchRecognize(SqlParserPos pos, SqlNode tableRef, SqlNode pattern,
      SqlLiteral strictStart, SqlLiteral strictEnd, SqlNodeList patternDefList,
      SqlNodeList measureList, @Nullable SqlNode after, SqlNodeList subsetList,
      @Nullable SqlLiteral rowsPerMatch, SqlNodeList partitionList,
      SqlNodeList orderList, @Nullable SqlLiteral interval) {
    super(pos);
    this.tableRef = requireNonNull(tableRef, "tableRef");
    this.pattern = requireNonNull(pattern, "pattern");
    this.strictStart = strictStart;
    this.strictEnd = strictEnd;
    this.patternDefList = requireNonNull(patternDefList, "patternDefList");
    checkArgument(!patternDefList.isEmpty());
    this.measureList = requireNonNull(measureList, "measureList");
    this.after = after;
    this.subsetList = subsetList;
    checkArgument(rowsPerMatch == null
        || rowsPerMatch.value instanceof RowsPerMatchOption);
    this.rowsPerMatch = rowsPerMatch;
    this.partitionList = requireNonNull(partitionList, "partitionList");
    this.orderList = requireNonNull(orderList, "orderList");
    this.interval = interval;
  }

  // ~ Methods

  @Override public SqlOperator getOperator() {
    return SqlMatchRecognizeOperator.INSTANCE;
  }

  @Override public SqlKind getKind() {
    return SqlKind.MATCH_RECOGNIZE;
  }

  @SuppressWarnings("nullness")
  @Override public List<SqlNode> getOperandList() {
    return ImmutableNullableList.of(tableRef, pattern, strictStart, strictEnd,
        patternDefList, measureList, after, subsetList, rowsPerMatch, partitionList, orderList,
        interval);
  }

  @Override public void unparse(SqlWriter writer, int leftPrec,
      int rightPrec) {
    getOperator().unparse(writer, this, 0, 0);
  }

  @Override public void validate(SqlValidator validator, SqlValidatorScope scope) {
    validator.validateMatchRecognize(this);
  }

  @SuppressWarnings("assignment.type.incompatible")
  @Override public void setOperand(int i, @Nullable SqlNode operand) {
    switch (i) {
    case OPERAND_TABLE_REF:
      tableRef = requireNonNull(operand, "operand");
      break;
    case OPERAND_PATTERN:
      pattern = operand;
      break;
    case OPERAND_STRICT_START:
      strictStart = (SqlLiteral) operand;
      break;
    case OPERAND_STRICT_END:
      strictEnd = (SqlLiteral) operand;
      break;
    case OPERAND_PATTERN_DEFINES:
      patternDefList = requireNonNull((SqlNodeList) operand);
      checkArgument(!patternDefList.isEmpty());
      break;
    case OPERAND_MEASURES:
      measureList = requireNonNull((SqlNodeList) operand);
      break;
    case OPERAND_AFTER:
      after = operand;
      break;
    case OPERAND_SUBSET:
      subsetList = (SqlNodeList) operand;
      break;
    case OPERAND_ROWS_PER_MATCH:
      rowsPerMatch = (SqlLiteral) operand;
      checkArgument(rowsPerMatch == null
          || rowsPerMatch.value instanceof RowsPerMatchOption);
      break;
    case OPERAND_PARTITION_BY:
      partitionList = (SqlNodeList) operand;
      break;
    case OPERAND_ORDER_BY:
      orderList = (SqlNodeList) operand;
      break;
    case OPERAND_INTERVAL:
      interval = (SqlLiteral) operand;
      break;
    default:
      throw new AssertionError(i);
    }
  }

  public SqlNode getTableRef() {
    return tableRef;
  }

  public SqlNode getPattern() {
    return pattern;
  }

  public SqlLiteral getStrictStart() {
    return strictStart;
  }

  public SqlLiteral getStrictEnd() {
    return strictEnd;
  }

  public SqlNodeList getPatternDefList() {
    return patternDefList;
  }

  public SqlNodeList getMeasureList() {
    return measureList;
  }

  public @Nullable SqlNode getAfter() {
    return after;
  }

  public SqlNodeList getSubsetList() {
    return subsetList;
  }

  public @Nullable SqlLiteral getRowsPerMatch() {
    return rowsPerMatch;
  }

  public SqlNodeList getPartitionList() {
    return partitionList;
  }

  public SqlNodeList getOrderList() {
    return orderList;
  }

  public @Nullable SqlLiteral getInterval() {
    return interval;
  }

  /**
   * Options for {@code ROWS PER MATCH}.
   */
  public enum RowsPerMatchOption {
    ONE_ROW("ONE ROW PER MATCH"),
    ALL_ROWS("ALL ROWS PER MATCH");

    private final String sql;

    RowsPerMatchOption(String sql) {
      this.sql = sql;
    }

    @Override public String toString() {
      return sql;
    }

    public SqlLiteral symbol(SqlParserPos pos) {
      return SqlLiteral.createSymbol(this, pos);
    }
  }

  /**
   * Options for {@code AFTER MATCH} clause.
   */
  public enum AfterOption implements Symbolizable {
    SKIP_TO_NEXT_ROW("SKIP TO NEXT ROW"),
    SKIP_PAST_LAST_ROW("SKIP PAST LAST ROW");

    private final String sql;

    AfterOption(String sql) {
      this.sql = sql;
    }

    @Override public String toString() {
      return sql;
    }
  }

  /**
   * An operator describing a MATCH_RECOGNIZE specification.
   */
  public static class SqlMatchRecognizeOperator extends SqlOperator {
    public static final SqlMatchRecognizeOperator INSTANCE =
        new SqlMatchRecognizeOperator();

    private SqlMatchRecognizeOperator() {
      super("MATCH_RECOGNIZE", SqlKind.MATCH_RECOGNIZE, 2, true, null, null, null);
    }

    @Override public SqlSyntax getSyntax() {
      return SqlSyntax.SPECIAL;
    }

    @SuppressWarnings("argument.type.incompatible")
    @Override public SqlCall createCall(
        @Nullable SqlLiteral functionQualifier,
        SqlParserPos pos,
        @Nullable SqlNode... operands) {
      assert functionQualifier == null;
      assert operands.length == 12;

      return new SqlMatchRecognize(pos, operands[0], operands[1],
          (SqlLiteral) operands[2], (SqlLiteral) operands[3],
          (SqlNodeList) operands[4], (SqlNodeList) operands[5], operands[6],
          (SqlNodeList) operands[7], (SqlLiteral) operands[8],
          (SqlNodeList) operands[9], (SqlNodeList) operands[10], (SqlLiteral) operands[11]);
    }

    @Override public <R> void acceptCall(
        SqlVisitor<R> visitor,
        SqlCall call,
        boolean onlyExpressions,
        SqlBasicVisitor.ArgHandler<R> argHandler) {
      if (onlyExpressions) {
        List<SqlNode> operands = call.getOperandList();
        for (int i = 0; i < operands.size(); i++) {
          SqlNode operand = operands.get(i);
          if (operand == null) {
            continue;
          }
          argHandler.visitChild(visitor, call, i, operand);
        }
      } else {
        super.acceptCall(visitor, call, onlyExpressions, argHandler);
      }
    }

    @Override public void validateCall(
        SqlCall call,
        SqlValidator validator,
        SqlValidatorScope scope,
        SqlValidatorScope operandScope) {
      validator.validateMatchRecognize(call);
    }

    @Override public void unparse(
        SqlWriter writer,
        SqlCall call,
        int leftPrec,
        int rightPrec) {
      final SqlMatchRecognize pattern = (SqlMatchRecognize) call;

      pattern.tableRef.unparse(writer, 0, 0);
      final SqlWriter.Frame mrFrame = writer.startFunCall("MATCH_RECOGNIZE");

      if (!pattern.partitionList.isEmpty()) {
        writer.newlineAndIndent();
        writer.sep("PARTITION BY");
        final SqlWriter.Frame partitionFrame = writer.startList("", "");
        pattern.partitionList.unparse(writer, 0, 0);
        writer.endList(partitionFrame);
      }

      if (!pattern.orderList.isEmpty()) {
        writer.newlineAndIndent();
        writer.sep("ORDER BY");
        writer.list(SqlWriter.FrameTypeEnum.ORDER_BY_LIST, SqlWriter.COMMA,
            pattern.orderList);
      }

      if (!pattern.measureList.isEmpty()) {
        writer.newlineAndIndent();
        writer.sep("MEASURES");
        final SqlWriter.Frame measureFrame = writer.startList("", "");
        pattern.measureList.unparse(writer, 0, 0);
        writer.endList(measureFrame);
      }

      SqlLiteral rowsPerMatch = pattern.rowsPerMatch;
      if (rowsPerMatch != null) {
        writer.newlineAndIndent();
        rowsPerMatch.unparse(writer, 0, 0);
      }

      SqlNode after = pattern.after;
      if (after != null) {
        writer.newlineAndIndent();
        writer.sep("AFTER MATCH");
        after.unparse(writer, 0, 0);
      }

      writer.newlineAndIndent();
      writer.sep("PATTERN");

      SqlWriter.Frame patternFrame = writer.startList("(", ")");
      if (pattern.strictStart.booleanValue()) {
        writer.sep("^");
      }
      pattern.pattern.unparse(writer, 0, 0);
      if (pattern.strictEnd.booleanValue()) {
        writer.sep("$");
      }
      writer.endList(patternFrame);
      SqlLiteral interval = pattern.interval;
      if (interval != null) {
        writer.sep("WITHIN");
        interval.unparse(writer, 0, 0);
      }

      if (!pattern.subsetList.isEmpty()) {
        writer.newlineAndIndent();
        writer.sep("SUBSET");
        SqlWriter.Frame subsetFrame = writer.startList("", "");
        pattern.subsetList.unparse(writer, 0, 0);
        writer.endList(subsetFrame);
      }

      writer.newlineAndIndent();
      writer.sep("DEFINE");

      final SqlWriter.Frame patternDefFrame = writer.startList("", "");
      final SqlNodeList newDefineList = new SqlNodeList(SqlParserPos.ZERO);
      for (SqlNode node : pattern.getPatternDefList()) {
        final SqlCall call2 = (SqlCall) node;
        // swap the position of alias position in AS operator
        newDefineList.add(
            call2.getOperator().createCall(SqlParserPos.ZERO, call2.operand(1),
                call2.operand(0)));
      }
      newDefineList.unparse(writer, 0, 0);
      writer.endList(patternDefFrame);
      writer.endList(mrFrame);
    }
  }
}
