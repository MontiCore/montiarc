/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

/**
 * Supported Operations to Convert (Boolean & Numerical Formulas):
 * - BooleanAndOpExpression
 * - BooleanOrOpExpression
 * - IfThenElse
 * - LogicalNotExpression
 * - EqualsExpression
 * - NotEqualsExpression
 * - PlusPrefixExpression
 * - MinusPrefixExpression
 */
public enum BooleanOperation implements IOPERATION {
  AND,
  OR,
  IF_THEN_ELSE,
  // The next 5 are common with numeric operation...
  NOT,
  EQUALS,
  NOT_EQUALS,
  MINUS_PREFIX,
  PLUS_PREFIX,
}
