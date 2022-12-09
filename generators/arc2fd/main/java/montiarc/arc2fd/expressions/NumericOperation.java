/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

/**
 * Supported Operations to Convert (Boolean & Numerical Formulas):
 * - LogicalNotExpression
 * - EqualsExpression
 * - NotEqualsExpression
 * - LessEqualExpression
 * - GreaterEqualExpression
 * - LessThanExpression
 * - GreaterThanExpression
 * - MultExpression
 * - DivideExpression
 * - ModuloExpression
 * - PlusExpression
 * - MinusExpression
 * - PlusPrefixExpression
 * - MinusPrefixExpression
 */
public enum NumericOperation implements IOPERATION {
  GEQ,
  LEQ,
  GT,
  LT,
  MULTIPLY,
  DIVIDE,
  MODULO,
  ADD,
  SUBTRACT,
  // The next 5 are common with BOOLEAN_OPERATION...
  NOT,
  EQUALS,
  NOT_EQUALS,
  MINUS_PREFIX,
  PLUS_PREFIX,
}
