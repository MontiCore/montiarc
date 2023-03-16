/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.arc2fd.smt.NumericPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


// Wrapper for Operations so that we don't have to do that much Method
// Overloading
interface IOPERATION {
}


/**
 * Converter which Allows us to Convert a MontiArc Expression into a Java-SMT
 * Formula
 */
public class MA2SMTFormulaConverter {
  /**
   * Separator which we can use to combine multiple formula parts
   */
  public static final String SEPARATOR = "_";

  /**
   * Prefix we prepend if the string representation starts with bogus
   * characters.
   */
  public static final String PREFIX = "$";
  /**
   * Java-SMT SolverContext
   */
  private final SolverContext context;
  /**
   * Java-SMT Formula Manager
   */
  private final FormulaManager fmgr;
  /**
   * Java-SMT Boolean Formula Manager (to build Boolean Formulas)
   */
  private final BooleanFormulaManager bmgr;
  /**
   * Java-SMT Boolean Integer Manager (to build Integer Formulas)
   */
  private final IntegerFormulaManager imgr;
  /**
   * Keep track of all formulas
   * Sort of Stack where new Formulas get added to, and old ones get removed
   * if we perform an Operation.
   * For example
   * [a, b, c] + "AND" could yield [a, and(b, c)]
   */
  protected List<Formula> formulaStack = new ArrayList<>();
  /**
   * Stores the final, resulting formula after conversion completed
   */
  protected BooleanFormula formula;
  MACommonExpressionsVisitor.TYPE formulaType =
    MACommonExpressionsVisitor.TYPE.VARIABLE;
  /**
   * Store MontiArc Expression Visitor to traverse through the Expression
   */
  private MAExpressionsFullVisitor montiArcExpVisitor;
  /**
   * Java-SMT Prover (to check whether constructed formula is SAT or UNSAT)
   */
  private ProverEnvironment prover;

  /**
   * Construct FormulaConverter with Default Solver Context (=
   * defaultConfiguration & SMTINTERPOL Solver)
   *
   * @throws InvalidConfigurationException Throws an Exception if the
   *                                       Solver-Configuration is wrong
   */
  public MA2SMTFormulaConverter() throws InvalidConfigurationException {
    this(org.sosy_lab.common.configuration.Configuration.defaultConfiguration(), SolverContextFactory.Solvers.SMTINTERPOL);
  }

  /**
   * Construct FormulaConverter with given Solver (and defaultConfiguration)
   *
   * @param solver Solver which should be used
   * @throws InvalidConfigurationException Throws an Exception if the
   *                                       Solver-Configuration is wrong
   */
  public MA2SMTFormulaConverter(@NotNull SolverContextFactory.Solvers solver) throws InvalidConfigurationException {
    this(org.sosy_lab.common.configuration.Configuration.defaultConfiguration(), solver);
  }

  /**
   * Construct FormulaConverter given Solver Configuration and Solver
   *
   * @param config Solver which should be used
   * @param solver Configuration which should be used
   * @throws InvalidConfigurationException Throws an Exception if the
   *                                       Solver-Configuration is wrong
   */
  public MA2SMTFormulaConverter(@NotNull org.sosy_lab.common.configuration.Configuration config,
                                @NotNull SolverContextFactory.Solvers solver) throws InvalidConfigurationException {
    this(SolverContextFactory.createSolverContext(
      config,
      BasicLogManager.create(config),
      ShutdownManager.create().getNotifier(),
      solver
    ));
  }


  /**
   * Construct FormulaConverter given SolverContext
   *
   * @param context SolverContext to use
   */
  public MA2SMTFormulaConverter(@NotNull SolverContext context) {
    Preconditions.checkNotNull(context);
    this.context = context;
    this.fmgr = context.getFormulaManager();
    this.bmgr = fmgr.getBooleanFormulaManager();
    this.imgr = fmgr.getIntegerFormulaManager();
    this.montiArcExpVisitor = new MAExpressionsFullVisitor(this);
  }

  /**
   * Convert the given ASTExpression into a Java-SMT Formula and return it.
   *
   * @param expr              ASTExpression to Convert
   * @param variableRemapping Stores a Map which maps ASTExpressions (as
   *                          Strings) to different ASTExpressions (as Strings).
   *                          The map then gets passed onto the corresponding
   *                          processing steps and will replace all occurrences
   *                          of (keys) by the corresponding (values).
   * @return (converted) Java-SMT Formula
   */
  public BooleanFormula convert(@NotNull ASTExpression expr,
                                @NotNull Map<String, String> variableRemapping) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(variableRemapping);

    clearFormulaBuffer();
    // Traverse through the whole formula (= actual conversion)
    this.montiArcExpVisitor = new MAExpressionsFullVisitor(this,
      variableRemapping);
    expr.accept(this.montiArcExpVisitor.getTraverser());

    // Build the formula by extracting it from the stack and return it
    return buildFormula();
  }

  /**
   * Tests whether the given Formula is Unsatisfiable
   *
   * @param formula Boolean Formula to Test
   * @return True if Formula is UNSAT, else False
   * @throws SolverException      Thrown if something is wrong with the
   *                              Formula (for addConstraint)
   * @throws InterruptedException Thrown if the Solving was unexpectedly
   *                              interrupted
   */
  public boolean isUnsat(@NotNull BooleanFormula formula) throws SolverException, InterruptedException {
    Preconditions.checkNotNull(formula);

    // Generate fresh prover
    this.prover =
      context.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS);
    prover.addConstraint(formula);
    return prover.isUnsat();
  }

  /**
   * Builds the Final Formula (after conversion) by extracting it from the
   * Stack.
   * If everything went right, it should be the first and only formula left
   * in the list (= Stack).
   *
   * @return Converted, final SMT-Formula
   */
  public BooleanFormula buildFormula() {
    if (formulaStack.size() == 1 && (formulaStack.get(0) instanceof BooleanFormula)) {
      formula = (BooleanFormula) formulaStack.get(0);
    } else {
      Log.warn(MAProcessingError.FORMULA_MIGHT_BE_CORRUPTED.format(formulaStack.toString()));
    }
    return formula;
  }


  /* ######################################### */
  /* #          Conversion Functions         # */
  /* ######################################### */

  /**
   * Clears / Resets the current Formula Buffer
   */
  public void clearFormulaBuffer() {
    this.formulaStack = new ArrayList<>();
  }

  /**
   * Combines the last two Formulas from Stack using "AND"
   */
  public void and() {
    combineLastTwoFormulas(BooleanOperation.AND);
  }

  /**
   * Combines the last two Formulas from Stack using "OR"
   */
  public void or() {
    combineLastTwoFormulas(BooleanOperation.OR);
  }

  /**
   * Combines the last two Formulas from Stack using "EQUALS"
   */
  public void equals() {
    combineLastTwoFormulas((formulaType == MACommonExpressionsVisitor.TYPE.ATOMIC) ?
      NumericOperation.EQUALS : BooleanOperation.EQUALS);
  }

  /**
   * Combines the last two Formulas from Stack using "NOT EQUALS"
   */
  public void notEquals() {
    combineLastTwoFormulas((formulaType == MACommonExpressionsVisitor.TYPE.ATOMIC) ?
      NumericOperation.NOT_EQUALS : BooleanOperation.NOT_EQUALS);
  }

  /**
   * Prepends an AND to the last Formula from Stack
   */
  public void not() {
    applyToLastFormula((formulaType == MACommonExpressionsVisitor.TYPE.ATOMIC) ?
      NumericOperation.NOT : BooleanOperation.NOT);
  }

  /**
   * Combines the last two Formulas from Stack using "MULTIPLY"
   */
  public void multiply() {
    combineLastTwoFormulas(NumericOperation.MULTIPLY);
  }

  /**
   * Combines the last two Formulas from Stack using "DIVIDE"
   */
  public void divide() {
    combineLastTwoFormulas(NumericOperation.DIVIDE);
  }

  /**
   * Combines the last two Formulas from Stack using "MODULO"
   */
  public void modulo() {
    combineLastTwoFormulas(NumericOperation.MODULO);
  }

  /**
   * Combines the last two Formulas from Stack using "ADD"
   */
  public void add() {
    combineLastTwoFormulas(NumericOperation.ADD);
  }

  /**
   * Combines the last two Formulas from Stack using "SUBTRACT"
   */
  public void subtract() {
    combineLastTwoFormulas(NumericOperation.SUBTRACT);
  }

  /**
   * Combines the last two Formulas from Stack using "GREATER THAN"
   */
  public void gt() {
    combineLastTwoFormulas(NumericOperation.GT);
  }

  /**
   * Combines the last two Formulas from Stack using "LESS THAN"
   */
  public void lt() {
    combineLastTwoFormulas(NumericOperation.LT);
  }

  /**
   * Combines the last two Formulas from Stack using "GREATER OR EQUAL THAN"
   */
  public void geq() {
    combineLastTwoFormulas(NumericOperation.GEQ);
  }

  /**
   * Combines the last two Formulas from Stack using "LESS OR EQUAL THAN"
   */
  public void leq() {
    combineLastTwoFormulas(NumericOperation.LEQ);
  }

  /**
   * Prepends a Minus to the last Formula from Stack
   */
  public void minusPrefix() {
    applyToLastFormula((formulaType == MACommonExpressionsVisitor.TYPE.ATOMIC) ?
      NumericOperation.MINUS_PREFIX : BooleanOperation.MINUS_PREFIX);
  }

  /**
   * Prepends a Plus to the last Formula from Stack
   */
  public void plusPrefix() {
    applyToLastFormula((formulaType == MACommonExpressionsVisitor.TYPE.ATOMIC) ?
      NumericOperation.PLUS_PREFIX : BooleanOperation.PLUS_PREFIX);
  }

  /**
   * Combines the last three Formulas from Stack using "IF [form1] THEN
   * [form2] ELSE [form3]"
   */
  public void ifThenElse() {
    combineLastThreeFormulas(BooleanOperation.IF_THEN_ELSE);
  }

  /**
   * Adds a single Variable to the Stack
   *
   * @param name Name of the Variable to Add
   */
  public void addVariable(String name) {
    formulaStack.add((formulaType == MACommonExpressionsVisitor.TYPE.ATOMIC)
      ? imgr.makeVariable(name) : bmgr.makeVariable(name));
  }

  /**
   * Adds a Boolean to the Stack
   *
   * @param var boolean value
   */
  public void addBoolean(boolean var) {
    formulaStack.add(bmgr.makeBoolean(var));
  }

  /**
   * Adds an Integer to the Stack
   *
   * @param number value
   */
  public void addInt(int number) {
    formulaStack.add(imgr.makeNumber(number));
  }

  /**
   * Adds a Long to the Stack
   *
   * @param number value
   */
  public void addLong(long number) {
    formulaStack.add(imgr.makeNumber(number));
  }

  /**
   * Doubles are NOT SUPPORTED! Thus, it converts it into a float and adds it
   * on the stack
   *
   * @param number value
   */
  public void addDouble(double number) {
    // (For this we would need 'FloatingPointFormulaManager, which has no
    // implementation)
    Log.warn(MAProcessingError.UNSUPPORTED_DECIMAL_DATATYPE.format("DOUBLE"));
    addLong((long) number);
  }

  /**
   * Floats are NOT SUPPORTED! Thus, it converts it into a float and adds it
   * on the stack
   *
   * @param number value
   */
  public void addFloat(float number) {
    // (For this we would need 'FloatingPointFormulaManager, which has no
    // implementation)
    Log.warn(MAProcessingError.UNSUPPORTED_DECIMAL_DATATYPE.format("FLOAT"));
    addLong((long) number);
  }

  /**
   * Converts the varargs input formulas into a string representation for
   * nicely printing it (used for pretty-printing errors)
   *
   * @param formulas One to Many formulas we want to join and convert to strings
   * @return String-Representation of all formulas separated by a separator
   */
  private String formulasToString(Formula... formulas) {
    return "\"" + Joiner.on("\", \"").join(formulas) + "\"";
  }


  /**
   * Applies the given Operation to the last Formula on the Stack
   *
   * @param op Operation to Apply
   */
  private void applyToLastFormula(@NotNull IOPERATION op) {
    Preconditions.checkNotNull(op);

    int numFormulas = formulaStack.size();
    if (numFormulas == 0) {
      Log.error(MAProcessingError.NO_FORMULA_FOUND.format());
      return;
    }

    // Get the last Formula
    Formula formula = formulaStack.get(numFormulas - 1);

    // Apply Correct Operation Function (depending on DataType)
    boolean res = false;
    if (formula instanceof BooleanFormula && op instanceof BooleanOperation) {
      removeFormulasFromStack(1);
      res = combineFormulasByOperation((BooleanOperation) op,
        (BooleanFormula) formula);
    } else if (formula instanceof IntegerFormula && op instanceof NumericOperation) {
      removeFormulasFromStack(1);
      res = combineFormulasByOperation((NumericOperation) op,
        (IntegerFormula) formula);
    } else {
      Log.error("Tried to apply an operation to neither boolean nor integer " +
        "formula!");
    }

    // Something went wrong...
    if (!res)
      Log.error(MAProcessingError.ERROR_WHILE_CONVERTING_FORMULA
        .format(op, formulaStack, formulasToString(formula)));
  }


  /**
   * Applies the given Operation to the last two Formulas on the Stack
   *
   * @param op Operation to Apply
   */
  private void combineLastTwoFormulas(@NotNull IOPERATION op) {
    Preconditions.checkNotNull(op);

    int numFormulas = formulaStack.size();
    if (numFormulas < 2) {
      Log.error(MAProcessingError.ERROR_NOT_ENOUGH_FORMULAS.format(2,
        numFormulas));
      return;
    }

    // Get last two formulas
    Formula rightFormula = formulaStack.get(numFormulas - 1);
    Formula leftFormula = formulaStack.get(numFormulas - 2);

    boolean res = false;
    if ((leftFormula instanceof BooleanFormula) && (rightFormula instanceof BooleanFormula) && (op instanceof BooleanOperation)) {
      removeFormulasFromStack(2);
      res = combineFormulasByOperation((BooleanOperation) op,
        (BooleanFormula) leftFormula, (BooleanFormula) rightFormula);
    } else if ((leftFormula instanceof IntegerFormula) && (rightFormula instanceof IntegerFormula) && (op instanceof NumericOperation)) {
      removeFormulasFromStack(2);
      res = combineFormulasByOperation((NumericOperation) op,
        (IntegerFormula) leftFormula, (IntegerFormula) rightFormula);
    } else if (op instanceof NumericOperation || op instanceof BooleanOperation) {
      // At least one formula is NOT IntegerFormula, but we have an integer
      // operation
      // => Create a new Variable and make its content the string
      // representation of the current expression
      removeFormulasFromStack(2);

      // Create String-Representations of both Formulas
      NumericPrettyPrinter npp = new NumericPrettyPrinter(fmgr);
      String leftFormulaString = npp.prettyPrint(leftFormula, true);
      String rightFormulaString = npp.prettyPrint(rightFormula);

      // Make a new Formula from the String-Representation (like ((a > 3) &&
      // b) ===> (a_GT_3_AND_b))
      BooleanFormula newFormula =
        bmgr.makeVariable(leftFormulaString + SEPARATOR + op + SEPARATOR + rightFormulaString);

      // Add the new Formula
      formulaStack.add(newFormula);

      // Only one is of type NumeralFormula => Give a Warning
      Log.warn(MAProcessingError.INTEGER_OPERATION_WITHOUT_INTEGER_FORMULA.format(leftFormula, rightFormula, op));
      res = true;
    } else {
      Log.error("Unknown error appeared while combining two formulas!");
    }

    // In all other cases, just return false
    if (!res)
      Log.error(MAProcessingError.ERROR_WHILE_CONVERTING_FORMULA
        .format(op, formulaStack, formulasToString(leftFormula,
          rightFormula)));
  }


  /**
   * Applies the given Operation to the last three Formula
   *
   * @param op Operation to Apply
   */
  private void combineLastThreeFormulas(@NotNull IOPERATION op) {
    Preconditions.checkNotNull(op);

    int numFormulas = formulaStack.size();
    if (numFormulas < 3) {
      Log.error(MAProcessingError.ERROR_NOT_ENOUGH_FORMULAS.format(3,
        numFormulas));
      return;
    }

    // Get the last three Formulas from Stack
    Formula left = formulaStack.get(numFormulas - 3);
    Formula center = formulaStack.get(numFormulas - 2);
    Formula right = formulaStack.get(numFormulas - 1);

    boolean res = false;
    // Condition must be of Type Boolean Formula, otherwise something went wrong
    if ((left instanceof BooleanFormula) && op instanceof BooleanOperation) {
      removeFormulasFromStack(3);
      res = combineFormulasByOperation((BooleanOperation) op,
        (BooleanFormula) left, center, right);
    } else {
      System.out.println("Something went wrong...");
    }

    // Something went wrong...
    if (!res)
      Log.error(MAProcessingError.ERROR_WHILE_CONVERTING_FORMULA
        .format(op, formulaStack, formulasToString(left, center, right)));
  }

  /**
   * Removes a certain number of formulas from the stack
   *
   * @param numberOfFormulas Number of Fromulas to drop from Stack
   */
  void removeFormulasFromStack(int numberOfFormulas) {
    removeFormulasFromStack(numberOfFormulas, 0);
  }

  /**
   * Remove a certain number of formulas from stack (and consider endOffset)
   * I.e., [a, b] => removeFormulasFromStack(1, 1) => [b]
   *
   * @param numberOfFormulas Number of Formulas to drop form Stack
   * @param endOffset        Offset from the end of the list (= Skip the last
   *                         X items before dropping the (x-1)-last item)
   */
  private void removeFormulasFromStack(int numberOfFormulas, int endOffset) {
    if (formulaStack.size() < endOffset + numberOfFormulas - 1)
      return;
    // Remove the last item from Stack "numberOfFormula"-times (and also
    // consider the endOffset)
    IntStream.range(0, numberOfFormulas).forEach($ -> formulaStack.remove(formulaStack.size() - endOffset - 1));
  }


  /**
   * Combines the last Boolean Formula by applying the specified Operation
   *
   * @param op   Operation for Combining
   * @param form Formula which should be processed by the specific operation
   * @return whether the combination was successful or not
   */
  private boolean combineFormulasByOperation(@NotNull BooleanOperation op,
                                             @NotNull BooleanFormula form) {
    Preconditions.checkNotNull(op);
    Preconditions.checkNotNull(form);

    boolean addedFormula = true;
    switch (op) {
      case NOT:
      case MINUS_PREFIX:
        // Minus Prefix or Not == Negate Form...
        formulaStack.add(bmgr.not(form));
        break;
      case PLUS_PREFIX:
        // Boolean Formula with "+" => don't do anything
        break;
      default:
        addedFormula = false;
        break;
    }

    // Assure that we really added a formula to the stack (so we found
    // something in the if-case...
    return addedFormula;
  }

  /**
   * Combines the last Integer Formula by applying the specified Operation
   *
   * @param op   Operation for Combining
   * @param form Formula which should be processed by the specific operation
   * @return whether the combination was successful or not
   */
  private boolean combineFormulasByOperation(@NotNull NumericOperation op,
                                             @NotNull IntegerFormula form) {
    Preconditions.checkNotNull(op);
    Preconditions.checkNotNull(form);

    boolean addedFormula = true;
    switch (op) {
      case NOT:
      case MINUS_PREFIX:
        // Minus Prefix or Not == Negate Form...
        formulaStack.add(imgr.negate(form));
        break;
      case PLUS_PREFIX:
        // Plus Prefix = Multiply the Number with +1 (a bit pointless, just
        // for sake of completion)
        formulaStack.add(imgr.multiply(imgr.makeNumber(1), form));
        break;
      default:
        addedFormula = false;
        break;
    }

    // Assure that we really added a formula to the stack (so we found
    // something in the if-case...
    return addedFormula;

  }


  /**
   * Combines the last two Boolean Formulas by applying the specified Operation
   *
   * @param op        Operation for Combining
   * @param leftForm  Left Formula used for Combining
   * @param rightForm Right Formula used for Combining
   * @return whether the combination was successful or not
   */
  private boolean combineFormulasByOperation(@NotNull BooleanOperation op,
                                             @NotNull BooleanFormula leftForm,
                                             @NotNull BooleanFormula rightForm) {
    Preconditions.checkNotNull(op);
    Preconditions.checkNotNull(leftForm);
    Preconditions.checkNotNull(rightForm);

    boolean addedFormula = true;
    switch (op) {
      case OR:
        formulaStack.add(bmgr.or(leftForm, rightForm));
        break;
      case AND:
        formulaStack.add(bmgr.and(leftForm, rightForm));
        break;
      case EQUALS:
        formulaStack.add(bmgr.equivalence(leftForm, rightForm));
        break;
      case NOT_EQUALS:
        formulaStack.add((bmgr.equivalence(leftForm, rightForm)));
        not();
        break;
      default:
        addedFormula = false;
        break;
    }

    // Assure that we really added a formula to the stack (so we found
    // something in the if-case...
    return addedFormula;
  }


  /**
   * Combines the last two Integer Formulas by applying the specified Operation
   *
   * @param op        Operation for Combining
   * @param leftForm  Left Formula used for Combining
   * @param rightForm Right Formula used for Combining
   * @return whether the combination was successful or not
   */
  private boolean combineFormulasByOperation(@NotNull NumericOperation op,
                                             @NotNull IntegerFormula leftForm,
                                             @NotNull IntegerFormula rightForm) {
    Preconditions.checkNotNull(op);
    Preconditions.checkNotNull(leftForm);
    Preconditions.checkNotNull(rightForm);

    boolean addedFormula = true;
    switch (op) {
      case GEQ:
        formulaStack.add((imgr.greaterOrEquals(leftForm, rightForm)));
        break;
      case LEQ:
        formulaStack.add((imgr.lessOrEquals(leftForm, rightForm)));
        break;
      case GT:
        formulaStack.add((imgr.greaterThan(leftForm, rightForm)));
        break;
      case LT:
        formulaStack.add((imgr.lessThan(leftForm, rightForm)));
        break;
      case MULTIPLY:
        formulaStack.add((imgr.multiply(leftForm, rightForm)));
        break;
      case DIVIDE:
        formulaStack.add((imgr.divide(leftForm, rightForm)));
        break;
      case MODULO:
        formulaStack.add((imgr.modulo(leftForm, rightForm)));
        break;
      case ADD:
        formulaStack.add((imgr.add(leftForm, rightForm)));
        break;
      case SUBTRACT:
        formulaStack.add((imgr.subtract(leftForm, rightForm)));
        break;
      case EQUALS:
        formulaStack.add(imgr.equal(leftForm, rightForm));
        break;
      case NOT_EQUALS:
        formulaStack.add((imgr.equal(leftForm, rightForm)));
        not();
        break;
      default:
        addedFormula = false;
        break;
    }

    // Assure that we really added a formula to the stack (so we found
    // something in the if-case...
    return addedFormula;
  }


  /**
   * Combines the last three Formulas by applying the specified Operation
   *
   * @param op         Operation for Combining
   * @param leftForm   Left Formula used for Combining
   * @param centerForm Center Formula used for Combining
   * @param rightForm  Right-most Formula used for Combining
   * @return whether the combination was successful or not
   */
  private boolean combineFormulasByOperation(@NotNull BooleanOperation op,
                                             @NotNull BooleanFormula leftForm,
                                             @NotNull Formula centerForm,
                                             @NotNull Formula rightForm) {
    Preconditions.checkNotNull(op);
    Preconditions.checkNotNull(leftForm);
    Preconditions.checkNotNull(centerForm);
    Preconditions.checkNotNull(rightForm);

    boolean addedFormula = true;
    switch (op) {
      case IF_THEN_ELSE:
        // condition = leftForm, ifForm = centerForm, elseForm = rightForm
        formulaStack.add(bmgr.ifThenElse(leftForm, centerForm, rightForm));
        break;
      default:
        addedFormula = false;
        break;
    }

    // Assure that we really added a formula to the stack (so we found
    // something in the if-case...
    return addedFormula;
  }


  public SolverContext getContext() {
    return context;
  }

  public FormulaManager getFmgr() {
    return fmgr;
  }

  public BooleanFormulaManager getBmgr() {
    return bmgr;
  }

  public IntegerFormulaManager getImgr() {
    return imgr;
  }

  public ProverEnvironment getProver() {
    return prover;
  }
}
