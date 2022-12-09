/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.stream.Stream;

/**
 * Visitor which is used as a Helper to convert MontiArc CommonExpressions
 * into an SMT-Formula.
 * For this, after the end of each visit (endVisit), we apply the
 * corresponding operation by
 * calling it from the MA2SMTFormulaConverter.
 * By using endVisit(@NotNull ) instead of Visit() we first add the relevant
 * Formulas / Expressions / Atoms to the stack,
 * and can then pop them form the Stack and apply the corresponding
 * Operations accordingly.
 */
public class MACommonExpressionsVisitor implements CommonExpressionsVisitor2,
                                                   CommonExpressionsHandler {
  private final MA2SMTFormulaConverter fc;
  private CommonExpressionsTraverser traverser;

  public MACommonExpressionsVisitor(@NotNull MA2SMTFormulaConverter fc) {
    Preconditions.checkNotNull(fc);
    this.fc = fc;
  }

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull CommonExpressionsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public void handle(@NotNull ASTBooleanAndOpExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.and();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTBooleanOrOpExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.or();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTEqualsExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.equals();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTNotEqualsExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.notEquals();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTLogicalNotExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getExpression());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.not();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTMultExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.multiply();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTDivideExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.divide();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTModuloExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.modulo();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTPlusExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.add();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTMinusExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.subtract();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTLessEqualExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.leq();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTGreaterEqualExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.geq();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTLessThanExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.lt();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTGreaterThanExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getLeft(), node.getRight());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.gt();
    fc.formulaType = TYPE.VARIABLE;
  }

  /**
   * Gets a number of expressions and analyzes which formula type we have
   * (see {@link TYPE}
   * for a description formula types)
   *
   * @param expressions 1 to n expressions to analyze
   * @return The determined {@link TYPE} of the formula.
   */
  public TYPE getFormulaType(ASTExpression... expressions) {
//        boolean isAtomic = Stream.of(expressions).allMatch(f -> f
//        instanceof ASTLiteralExpression);
    boolean isComplex =
      Stream.of(expressions).anyMatch(f -> f instanceof ASTCommonExpressionsNode);
    boolean isVariable =
      Stream.of(expressions).anyMatch(f -> f instanceof ASTNameExpression);
    return (isComplex) ? TYPE.COMPLEX : ((isVariable) ? TYPE.VARIABLE :
      TYPE.ATOMIC);
  }

  @Override
  public void handle(@NotNull ASTMinusPrefixExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getExpression());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.minusPrefix();
    fc.formulaType = TYPE.VARIABLE;
  }

  @Override
  public void handle(@NotNull ASTPlusPrefixExpression node) {
    Preconditions.checkNotNull(node);

    // Determine the correct formula type (used for converting into correct
    // SMT-Instances)
    fc.formulaType = getFormulaType(node.getExpression());

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.plusPrefix();
    fc.formulaType = TYPE.VARIABLE;
  }

  public void handle(@NotNull ASTConditionalExpression node) {
    Preconditions.checkNotNull(node);

    // If-Then-Else is defined for Integer & Boolean => No problems here
    fc.formulaType = TYPE.VARIABLE;

    // Continue visiting
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);

    // Execute the correct expression & reset the formula type
    fc.ifThenElse();
  }

  /**
   * Defines the different Formula Types, which are necessary so that we can
   * correctly convert them into
   * Java-SMT Formulas (since Java-SMT has different formula types for
   * Integer & Boolean expressions)
   * <p>
   * Examples:
   * ATOMIC = {"1", "0", "123"}
   * VARIABLE = {"a", "b", "abc"}
   * COMPLEX = {"a && b", "a || b", "!(a && b) || c"}
   */
  enum TYPE {
    ATOMIC,
    VARIABLE,
    COMPLEX,
  }
}
