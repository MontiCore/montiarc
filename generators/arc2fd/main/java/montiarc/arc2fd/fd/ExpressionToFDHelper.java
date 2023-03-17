/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.arc2fd.expressions.MA2SMTFormulaConverter;
import montiarc.arc2fd.smt.CNFConverter;
import montiarc.arc2fd.smt.SMT2FDVisitor;
import montiarc.arc2fd.smt.SMTFormulaAnalyzer;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverException;
import org.sosy_lab.java_smt.utils.PrettyPrinter;

import java.util.Map;
import java.util.Optional;

/**
 * Helper / Wrapper class, which combines all the other functionality to
 * convert a ASTExpression into
 * a Feature Diagram (FDStorage) by only calling one method.
 */
public class ExpressionToFDHelper<T extends Formula> {

  /**
   * Converter to convert MontiArc ASTExpression into an SMT Formula
   */
  protected MA2SMTFormulaConverter ma2smtFormulaConverter;

  /**
   * Converter to convert a SMT-Formula into an equivalent CNF-Representation
   */
  protected CNFConverter cnfConverter;

  /**
   * Converter to finally evaluate a CNF-Representation of an SMT-formula to
   * convert it into an FD-Storage
   */
  protected SMT2FDVisitor smt2fdVisitor;

  /**
   * SolverContext (Java-SMT)
   */
  protected SolverContext context;

  /**
   * Formula Manager (Java-SMT)
   */
  protected FormulaManager fmgr;

  /**
   * Formula Analyzer
   */
  protected SMTFormulaAnalyzer analyzer;

  /**
   * Boolean Formula Manager (Java-SMT)
   */
  protected BooleanFormulaManager bmgr;

  /**
   * Integer Formula Manager (Java-SMT)
   */
  protected IntegerFormulaManager imgr;

  public ExpressionToFDHelper() throws InvalidConfigurationException {
    this(SolverContextFactory.Solvers.SMTINTERPOL);
  }

  public ExpressionToFDHelper(@NotNull SolverContextFactory.Solvers solver) throws InvalidConfigurationException {
    this(Configuration.defaultConfiguration(), solver);
  }

  public ExpressionToFDHelper(@NotNull Configuration config, @NotNull SolverContextFactory.Solvers solver) throws InvalidConfigurationException {
    this(SolverContextFactory.createSolverContext(config,
      BasicLogManager.create(config),
      ShutdownManager.create().getNotifier(),
      solver)
    );
  }

  public ExpressionToFDHelper(@NotNull SolverContext context) {
    Preconditions.checkNotNull(context);
    this.context = context;
    this.fmgr = context.getFormulaManager();
    this.bmgr = this.fmgr.getBooleanFormulaManager();
    this.imgr = this.fmgr.getIntegerFormulaManager();
    this.ma2smtFormulaConverter = new MA2SMTFormulaConverter(context);
    this.cnfConverter = new CNFConverter(fmgr, bmgr);
    this.smt2fdVisitor = new SMT2FDVisitor(fmgr, bmgr);
    this.analyzer = new SMTFormulaAnalyzer(fmgr, bmgr);
  }

  /**
   * Converts a MontiArc ASTExpression into a FDConstructionStorage (from
   * which we can then easily generate
   * an FD-representation of the Expression)
   *
   * @param expr              MontiArc ASTExpression to convert
   * @param rootName          BooleanFormula which represents the root of the
   *                          current AST (and thus all extracted
   *                          relations refer to this root)
   * @param variableRemapping Stores a Map which maps ASTExpressions (as
   *                          Strings) to different ASTExpressions (as Strings).
   *                          The map then gets passed onto the corresponding
   *                          processing steps and will replace all occurrences
   *                          of (keys) by the corresponding (values).
   * @return FDConstructionStorage containing all relevant information
   * @throws InterruptedException Gets thrown if the SAT-Checking fails
   */
  public Optional<FDConstructionStorage<T>> convertASTExpressionToFD(@NotNull ASTExpression expr,
                                                                     @NotNull T rootName,
                                                                     @NotNull Map<String, String> variableRemapping) throws InterruptedException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(rootName);
    Preconditions.checkNotNull(variableRemapping);

    // 1. Convert Expression into BooleanFormula (Java-SMT)
    BooleanFormula constraint_formula =
      this.ma2smtFormulaConverter.convert(expr, variableRemapping);

    // 2. Check if Formula is Unsat (and output error)
    if (isUnsat(constraint_formula))
      return Optional.empty();

    // 3. Convert Formula into CNF-Representation
    BooleanFormula cnf = this.cnfConverter.convertToCNF(constraint_formula);

    // 4. Simplify the Formula
    // Simplify the Formula to eliminate Tautologies, Redundancies, ...
    cnf = this.fmgr.simplify(cnf);

    // 5. Re-Check if CNF-Formula and Simplified Formula is unsat (and output
    // error)
    if (isUnsat(cnf))
      return Optional.empty();

    // 6. Visit the simplified CNF to extract the final information for our
    // feature diagram
    this.smt2fdVisitor.process(cnf, (BooleanFormula) rootName);

    // 7. Store the Relevant Information for our Feature Diagram
    FDConstructionStorage<T> storage = new FDConstructionStorage<>(this.fmgr,
      this.bmgr);
    storage.extractDataFromVisitor(this.smt2fdVisitor);

    // Return the Generated Storage Instance
    return Optional.of(storage);
  }

  /**
   * Checks if the given formula is unsatisfiable. If so, we output an error
   *
   * @param formula Formula to check
   * @return True if Formula is unsat, else false
   */
  public boolean isUnsat(BooleanFormula formula) {
    String prettyPrintFormula = CharMatcher.whitespace().trimAndCollapseFrom(
      new PrettyPrinter(this.fmgr).formulaToString(formula)
        .replaceAll("[\\t\\n\\r]+", " "), ' ');
    try {
      if (this.ma2smtFormulaConverter.isUnsat(formula)) {
        Log.error(FDConstructionError.FORMULA_IS_UNSAT.format(prettyPrintFormula));
        return true;
      }
    } catch (
      SolverException |
      InterruptedException e) {
      Log.error(FDConstructionError.ERROR_CHECKING_SAT.format(prettyPrintFormula));
      throw new RuntimeException(e);
    }
    return false;
  }
}

