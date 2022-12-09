/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

import com.google.common.base.Preconditions;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.common.configuration.InvalidConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * FullVisitor which is used as a sort of Wrapper to trigger the correct
 * Visitors when we traverse through
 * a ASTExpression which is made up from multiple ExpressionTypes (i.e.,
 * CommonLiterals + CommonExpressions).
 * Visitor which is used as a Helper to convert MontiArc CommonLiterals into
 * an SMT-Formula.
 */
public class MAExpressionsFullVisitor {

  protected MontiArcTraverser traverser;

  /**
   * Initialize all Visitors of MaExpressionsFullVisitor by creating a new
   * MA2SMTFormulaConverter.
   *
   * @throws InvalidConfigurationException MA2SMTFormulaConverter might throw
   *                                       Exceptions in case
   *                                       of invalid configurations
   */
  public MAExpressionsFullVisitor() throws InvalidConfigurationException {
    this(new MA2SMTFormulaConverter(), new HashMap<>());
  }

  /**
   * Initialize all Visitors of MaExpressionsFullVisitor by passing a
   * MA2SMTFormulaConverter.
   */
  public MAExpressionsFullVisitor(@NotNull MA2SMTFormulaConverter fc) {
    this(fc, new HashMap<>());
  }

  /**
   * Initialize all Visitors of MaExpressionsFullVisitor with the passed
   * Formula Converter.
   *
   * @param fc                Formula Converter used for Initialization
   * @param variableRemapping Stores a Map which maps ASTExpressions (as
   *                          Strings) to different ASTExpressions (as Strings).
   *                          The map then gets passed onto the corresponding
   *                          processing steps and will replace all occurrences
   *                          of (keys) by the corresponding (values).
   */
  public MAExpressionsFullVisitor(@NotNull MA2SMTFormulaConverter fc,
                                  @NotNull Map<String, String> variableRemapping) {
    Preconditions.checkNotNull(fc);
    Preconditions.checkNotNull(variableRemapping);
    traverser = MontiArcMill.traverser();

    // Initialize all Visitors for Expression & Literal languages
    initExpressionBasisVisitor(fc, variableRemapping);
    initCommonExpressionsVisitor(fc);
    initCommonLiteralsVisitor(fc);
  }

  /**
   * Initializes the Visitor for CommonExpressions with the passed Formula
   * Converter
   *
   * @param fc Formula Converter used for Initializing
   */
  protected void initCommonExpressionsVisitor(@NotNull MA2SMTFormulaConverter fc) {
    Preconditions.checkNotNull(fc);
    MACommonExpressionsVisitor prettyPrinter =
        new MACommonExpressionsVisitor(fc);
    prettyPrinter.setTraverser(traverser);
    traverser.setCommonExpressionsHandler(prettyPrinter);
    traverser.add4CommonExpressions(prettyPrinter);
  }

  /**
   * Initializes the Visitor for ExpressionBasis with the passed Formula
   * Converter
   *
   * @param fc                Formula Converter used for Initializing
   * @param variableRemapping Stores a Map which maps ASTExpressions (as
   *                          Strings) to different ASTExpressions (as Strings).
   *                          The map then gets passed onto the corresponding
   *                          processing steps and will replace all occurrences
   *                          of (keys) by the corresponding (values).
   */
  protected void initExpressionBasisVisitor(@NotNull MA2SMTFormulaConverter fc,
                                            @NotNull Map<String, String> variableRemapping) {
    Preconditions.checkNotNull(fc);
    Preconditions.checkNotNull(variableRemapping);
    MAExpressionsBasisVisitor prettyPrinter =
        new MAExpressionsBasisVisitor(fc, variableRemapping);
    prettyPrinter.setTraverser(traverser);
    traverser.setExpressionsBasisHandler(prettyPrinter);
    traverser.add4ExpressionsBasis(prettyPrinter);
  }

  /**
   * Initializes the Visitor for CommonLiterals with the passed Formula
   * Converter
   *
   * @param fc Formula Converter used for Initializing
   */
  protected void initCommonLiteralsVisitor(@NotNull MA2SMTFormulaConverter fc) {
    Preconditions.checkNotNull(fc);
    MACommonLiteralsVisitor prettyPrinter = new MACommonLiteralsVisitor(fc);
    prettyPrinter.setTraverser(traverser);
    traverser.setMCCommonLiteralsHandler(prettyPrinter);
    traverser.add4MCCommonLiterals(prettyPrinter);
  }

  /**
   * @return Returns the MontiArc Traverser
   */
  public MontiArcTraverser getTraverser() {
    return traverser;
  }
}
