/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

import com.google.common.base.Preconditions;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsHandler;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Visitor which is used as a Helper to convert MontiArc CommonLiterals into
 * an SMT-Formula.
 * For this, after the end of each visit (endVisit), we apply the
 * corresponding operation by
 * calling it from the MA2SMTFormulaConverter.
 * By using endVisit(@NotNull ) instead of Visit() we first add the relevant
 * Formulas / Expressions / Atoms to the stack,
 * and can then pop them form the Stack and apply the corresponding
 * Operations accordingly.
 */
public class MACommonLiteralsVisitor implements MCCommonLiteralsVisitor2,
                                                MCCommonLiteralsHandler {

  private final MA2SMTFormulaConverter fc;
  private MCCommonLiteralsTraverser traverser;

  public MACommonLiteralsVisitor(@NotNull MA2SMTFormulaConverter fc) {
    Preconditions.checkNotNull(fc);
    this.fc = fc;
  }

  @Override
  public MCCommonLiteralsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull MCCommonLiteralsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  /**
   * Adds a boolean literal
   *
   * @param a boolean literal
   */
  @Override
  public void endVisit(@NotNull ASTBooleanLiteral a) {
    Preconditions.checkNotNull(a);
    fc.addBoolean(a.getValue());
  }

  /**
   * Adds a char literal
   *
   * @param a char literal
   */
  @Override
  public void endVisit(@NotNull ASTCharLiteral a) {
    Preconditions.checkNotNull(a);
    Log.error(MAProcessingError.EXPRESSION_TYPE_NOT_SUPPORTED_FOR_CONVERSION.format("ASTCharLiteral"));
  }

  /**
   * Adds a string literal
   *
   * @param a string literal
   */
  @Override
  public void endVisit(@NotNull ASTStringLiteral a) {
    Preconditions.checkNotNull(a);
    Log.error(MAProcessingError.EXPRESSION_TYPE_NOT_SUPPORTED_FOR_CONVERSION.format("ASTStringLiteral"));
  }

  /**
   * Adds a natural literal
   *
   * @param a Nat literal
   */
  @Override
  public void endVisit(@NotNull ASTNatLiteral a) {
    Preconditions.checkNotNull(a);
    fc.addInt(a.getValue());
  }

  /**
   * Adds a natural literal
   *
   * @param a SignedNat literal
   */
  @Override
  public void endVisit(@NotNull ASTSignedNatLiteral a) {
    Preconditions.checkNotNull(a);
    fc.addInt(a.getValue());
  }

  /**
   * Adds a natural literal
   *
   * @param a long literal
   */
  @Override
  public void endVisit(@NotNull ASTBasicLongLiteral a) {
    Preconditions.checkNotNull(a);
    fc.addLong(a.getValue());
  }

  /**
   * Adds a natural literal
   *
   * @param a SignedLong literal
   */
  @Override
  public void endVisit(@NotNull ASTSignedBasicLongLiteral a) {
    Preconditions.checkNotNull(a);
    fc.addLong(a.getValue());
  }

  /**
   * Adds a natural literal
   *
   * @param a double literal
   */
  @Override
  public void endVisit(@NotNull ASTBasicDoubleLiteral a) {
    Preconditions.checkNotNull(a);
    Log.warn(MAProcessingError.CONVERTED_TO_LONG.format(
      "ASTBasicDoubleLiteral"));
    fc.addDouble(a.getValue());
  }

  /**
   * Adds a natural literal
   *
   * @param a SignedDourble literal
   */
  @Override
  public void endVisit(@NotNull ASTSignedBasicDoubleLiteral a) {
    Preconditions.checkNotNull(a);
    Log.warn(MAProcessingError.CONVERTED_TO_LONG.format(
      "ASTSignedBasicDoubleLiteral"));
    fc.addDouble(a.getValue());
  }

  /**
   * Adds a natural literal
   *
   * @param a float literal
   */
  @Override
  public void endVisit(@NotNull ASTBasicFloatLiteral a) {
    Preconditions.checkNotNull(a);
    Log.warn(MAProcessingError.CONVERTED_TO_LONG.format("ASTBasicFloatLiteral"
    ));
    fc.addFloat(a.getValue());
  }

  /**
   * Adds a natural literal
   *
   * @param a SignedNat literal
   */
  @Override
  public void endVisit(@NotNull ASTSignedBasicFloatLiteral a) {
    Preconditions.checkNotNull(a);
    Log.warn(MAProcessingError.CONVERTED_TO_LONG.format(
      "ASTSignedBasicFloatLiteral"));
    fc.addFloat(a.getValue());
  }
}
