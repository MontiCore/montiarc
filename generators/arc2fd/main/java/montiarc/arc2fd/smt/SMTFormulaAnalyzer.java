/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.java_smt.api.*;
import org.sosy_lab.java_smt.api.visitors.BooleanFormulaVisitor;
import org.sosy_lab.java_smt.utils.PrettyPrinter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The SMTFormulaAnalyzer allows us to visit a BooleanFormula (actually
 * BooleanFormula mixed with Numerical
 * is also fine).
 * After we've completed the visit, we can then call the Analyzer-Instance
 * and ask for certain properties.
 * Examples for those Properties are:
 * - isNNF (is the Formula in Negation Normal Form)
 * - isCNF (is the Formula in Conjunctive Normal Form)
 * - getAllPositiveAtoms (returns a List of BooleanFormulas containing only
 * positive literals)
 * - Many more... (see below for a complete overview)
 */
public class SMTFormulaAnalyzer implements BooleanFormulaVisitor<BooleanFormula> {

  // Java-SMT Formula Manager
  private final FormulaManager fmgr;

  // Java-SMT Boolean Formula Manager
  private final BooleanFormulaManager bmgr;
  private boolean isAtomic = true;
  private boolean insideNegation = false; // Keep track of whether the
  // currently traversed formula is inside a negation
  private boolean isNegationPreceding = false;
  private boolean isNNF = true;
  private boolean isCNF = true;
  private boolean hasConjunctions = false;
  private boolean hasDisjunctions = false;
  private Set<BooleanFormula> allPositiveAtoms = new HashSet<>();
  // Shouldn't really be of our interest I guess
  private Set<BooleanFormula> allPositiveFormulas = new HashSet<>();
  private Set<BooleanFormula> allNegatedAtoms = new HashSet<>();
  // Shouldn't really be of our interest I guess
  private Set<BooleanFormula> allNegatedFormulas = new HashSet<>(); //
  // Negated Formulas (also includes negated atoms!)
  private Set<BooleanFormula> allConjunctions = new HashSet<>(); // Stores
  // all conjunctions (after simplifying / extracting things like simple or
  // and XOR)
  private Set<BooleanFormula> allDisjunctions = new HashSet<>(); // Stores
  // all conjunctions (after simplifying / extracting things like simple or
  // and XOR)

  public SMTFormulaAnalyzer(@NotNull FormulaManager fmgr,
                            @NotNull BooleanFormulaManager bmgr) {
    Preconditions.checkNotNull(fmgr);
    Preconditions.checkNotNull(bmgr);
    this.fmgr = fmgr;
    this.bmgr = bmgr;
  }

  protected SMTFormulaAnalyzer(FormulaManager fmgr) {
    this(fmgr, fmgr.getBooleanFormulaManager());
  }

  /**
   * Starts the Analysis of the given Formula (so after calling this method,
   * we can get the corresponding
   * properties from the formula analyzer).
   *
   * @param f Formula to Analyze
   */
  public void analyze(BooleanFormula f) {
    if (Objects.isNull(f)) {
      Log.error(SMTProcessingError.ANALYSIS_NO_FORMULA_FOUND.format());
      return;
    }

    clear_buffer();
    visit(f);
  }

  /**
   * Visit a BooleanFormula, analyzes it and writes the results into the
   * corresponding variables.
   * By this, we can just ask the analyzer instance things like "isNNF" and
   * get a direct answer.
   *
   * @param f Formula that should be analyzed
   * @return Original Formula (WARNING: Rewritten in terms of 'OR', 'AND' and
   * 'NOT', all other operands get eliminated!)
   */
  protected BooleanFormula visit(@NotNull BooleanFormula f) {
    Preconditions.checkNotNull(f);
    // For each visit, we must reset isAtomic to restart with the guess, that
    // the formula is atomic.
    isAtomic = true;
    return bmgr.visit(f, this);
  }

  /**
   * Small helper which iteratively visits a list of formulas
   *
   * @param formulas List of formulas to visit
   */
  private void visitAll(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);

    formulas.forEach(this::visit);
    // If we need the return value at some point in the future, use the
    // following
    // List<BooleanFormula> cnf_formulas = new ArrayList<>();
    // formulas.forEach(f -> cnf_formulas.add(visit(f)));
  }

  /**
   * @return Is the passed Formula atomic
   */
  public boolean isAtomic() {
    return this.isAtomic;
  }

  /**
   * @return Does the Formula only consists of Disjunctions (and Negations &
   * Atoms)?
   */
  public boolean hasDisjunctionsOnly() {
    return hasDisjunctions() && !hasConjunctions();
  }

  /**
   * @return Does the Formula only consists of Conjunctions (and Negations &
   * Atoms)?
   */
  public boolean hasConjunctionsOnly() {
    return !hasDisjunctions() && hasConjunctions();
  }

  /**
   * @return True if the Formula has conjunctions, otherwise False
   */
  public boolean hasConjunctions() {
    return hasConjunctions;
  }

  /**
   * @return True if the Formula has disjunctions, otherwise False
   */
  public boolean hasDisjunctions() {
    return hasDisjunctions;
  }

  /**
   * @return Is the visited Formula in NNF (Negation Normal Form)
   */
  public boolean isNNF() {
    return isNNF;
  }

  /**
   * @return Is the visited Formula in CNF (Conjunctive Normal Form)
   */
  public boolean isCNF() {
    return isCNF;
  }

  /**
   * @return Total Number of Negated Atoms
   */
  public int numberOfNegatedAtoms() {
    return allNegatedAtoms.size();
  }

  /* ############################################# */
  /* #  Private Variables used for the Analysis  # */
  /* ############################################# */

  /**
   * @return Total Number of Positive Atoms
   */
  public int numberOfPositiveAtoms() {
    return allPositiveAtoms.size();
  }

  /**
   * @return Total Number of Negated Formulas (without single atoms like !a
   * which aren't a "real" formula)
   */
  public int numberOfNegatedFormulas() {
    return allNegatedFormulas.size();
  }

  /**
   * @return Total Number of Positive Formulas (without single atoms like a
   * which aren't a "real" formula)
   */
  public int numberOfPositiveFormulas() {
    return allPositiveFormulas.size();
  }

  /**
   * @return A Set of all (positive & negated) Atoms inside the Formula.
   */
  public Set<BooleanFormula> getAllAtoms() {
    return Sets.union(allPositiveAtoms, allNegatedAtoms);
  }

  /**
   * @return A Set of all negated Atoms inside the Formula.
   */
  public Set<BooleanFormula> getNegatedAtoms() {
    return allNegatedAtoms.stream().sorted(Comparator.comparing(BooleanFormula::toString)).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * @return A Set of all positive Atom inside the Formula.
   */
  protected Set<BooleanFormula> getPositiveAtoms() {
//        return allPositiveAtoms.stream().sorted().collect(Collectors
//        .toCollection(LinkedHashSet::new));
    return allPositiveAtoms.stream().sorted(Comparator.comparing(BooleanFormula::toString)).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * Checks whether the analyzed formula only consists of one atom and this
   * is positive and negated, like (a || !a)
   * => Redundant / Optional Atom
   *
   * @return Positive Version of the Optional Atom (in the example form
   * above, just "a")
   */
  public Optional<BooleanFormula> isFormulaOptionalAtom() {
    if (getPositiveAtoms().size() == 1 && getNegatedAtoms().size() == 1) {
      Optional<BooleanFormula> posAtom =
          getPositiveAtoms().stream().findFirst();
      Optional<BooleanFormula> negAtom = getNegatedAtoms().stream().findFirst();
      if (posAtom.isPresent() && negAtom.isPresent() && posAtom.get().equals(negAtom.get())) {
        return posAtom;
      }
    }
    return Optional.empty();
  }

  /**
   * @return True if the analyzed formula only contains positive atoms and no
   * negated ones
   */
  public boolean hasPositiveAtomsOnly() {
    return getPositiveAtoms().size() > 0 && getNegatedAtoms().size() == 0;
  }

  /**
   * @return True if the analyzed formula only contains negative atoms and no
   * positive ones
   */
  public boolean hasNegatedAtomsOnly() {
    return getPositiveAtoms().size() == 0 && getNegatedAtoms().size() > 0;
  }

  /**
   * @return A Set of all (positive & negated) Atoms inside the Formula.
   */
  public Set<BooleanFormula> getAllFormulas() {
    return Sets.union(allPositiveFormulas, allNegatedFormulas);
  }

  /**
   * @return A Set of all negated Formulas inside the Formula.
   */
  public Set<BooleanFormula> getAllNegatedFormulas() {
    return allNegatedFormulas;
  }

  /**
   * @return A Set of all positive Atom inside the Formula.
   */
  public Set<BooleanFormula> getAllPositiveFormulas() {
    return allPositiveFormulas;
  }

  /**
   * Clears the Buffer (= stored analyze results) and by this, makes it ready
   * for another, fresh visit.
   * Should be done prior to each initial visit() of a Formula!
   */
  public void clear_buffer() {
    // Reset all Booleans
    isAtomic = true;
    isNNF = true;
    isCNF = true;
    hasConjunctions = false;
    hasDisjunctions = false;
    insideNegation = false;
    isNegationPreceding = false;

    // Reset all Sets
    allPositiveAtoms = new HashSet<>();
    allPositiveFormulas = new HashSet<>();
    allNegatedAtoms = new HashSet<>();
    allNegatedFormulas = new HashSet<>();
    allConjunctions = new HashSet<>();
    allDisjunctions = new HashSet<>();
  }

  /* ############################################# */
  /* #    Visit Methods for all Formula Types    # */
  /* ############################################# */

  /**
   * Visits a Constant
   *
   * @param value Boolean Value of the Constant
   * @return Original Formula (BooleanFormula with the Constant)
   */
  @Override
  public BooleanFormula visitConstant(boolean value) {
    return bmgr.makeBoolean(value);
  }

  /**
   * Visit Bounded Var
   *
   * @param var         Variable
   * @param deBruijnIdx Bounding
   * @return Variable
   */
  @Override
  public BooleanFormula visitBoundVar(@NotNull BooleanFormula var,
                                      int deBruijnIdx) {
    Preconditions.checkNotNull(var);
    visit(var);
    return var;
  }

  /**
   * Analyzes an Atom
   *
   * @param atom     Atom to Process
   * @param funcDecl Function Declaration
   * @return The Input (an Atom)
   */
  @Override
  public BooleanFormula visitAtom(@NotNull BooleanFormula atom,
                                  FunctionDeclaration<BooleanFormula> funcDecl) {
    Preconditions.checkNotNull(atom);
        /*
            If our last operation was a negation (= negationPreceding), we
            want to add the atom to the list of
            negated atoms. Otherwise, we want to add it to the list of
            positive Atoms.
         */
    if (isNegationPreceding)
      allNegatedAtoms.add(atom);
    else
      allPositiveAtoms.add(atom);

    // Return the Input-Atom
    return atom;
  }

  /**
   * Analyzes a NOT-Formula
   *
   * @param operand Negated term.
   * @return The Input (a negated Term)
   */
  @Override
  public BooleanFormula visitNot(@NotNull BooleanFormula operand) {
    Preconditions.checkNotNull(operand);

    // Backup current values (locally!)
    // We do this, so that we can evaluate them freshly inside visit() and by
    // this get the values from the term
    // inside the not(...)
    boolean backup_isAtomic = isAtomic;
    boolean backup_insideNegation = insideNegation;
    boolean backup_isNegationPreceding = isNegationPreceding;

    // Reset current values (locally!)
    isAtomic = true;
    insideNegation = true;
    isNegationPreceding = true;

    // Visit the Operand
    visit(operand);

    // If the (visited) Formula is not atomic, we cannot have an NNF
    if (!isAtomic) {
      allNegatedFormulas.add(operand);
      isNNF = false;
    }

    // Restore current value (from local values)
    isAtomic = backup_isAtomic;
    insideNegation = backup_insideNegation;
    isNegationPreceding = backup_isNegationPreceding;

    return bmgr.not(operand);
  }


  /**
   * Analyzes an AND-Formula
   *
   * @param operands List of all conjuncts of the AND
   * @return The Input (one SINGLE AND-Formula)
   */
  @Override
  public BooleanFormula visitAnd(@NotNull List<BooleanFormula> operands) {
    Preconditions.checkNotNull(operands);

    // If we're inside an AND, the formula cannot be atomic anymore
    isAtomic = false;

    // Reset isNegationPreceding to false (since we want to freshly evaluate
    // each conjunct)
    boolean backupIsNegationPreceding = isNegationPreceding;
    isNegationPreceding = false;

    // Backup hasDisjunctions (locally!)
    // We do this, so that we can evaluate them freshly inside visit() and by
    // this we can determine
    // whether any of the conjuncts has disjunctions (= nested disjunctions)
    boolean backup_hasDisjunctions = hasDisjunctions;

    // Reset both booleans (for freshly evaluating all conjuncts)
    hasConjunctions = false;
    hasDisjunctions = false;

    // Visit all conjuncts
    visitAll(operands);

    // If we're in a negation and find an AND => We cannot have a CNF!
    if (insideNegation) {
      isCNF = false;
      isNNF = false;
    }

    // If we have no conjuncts and no disjuncts, we don't have any deeper
    // nesting (= only atomic / negated atomic
    // formulas) and can thus safely add the operands to the list of positive
    // formulas.
    if (!hasConjunctions && !hasDisjunctions && !backupIsNegationPreceding) {
      allPositiveFormulas.add(bmgr.and(operands));
    }


    // Restore hasDisjunctions (and OR it with current value, so that if one
    // of our conjunct has disjunctions,
    // the final value will also be true = has disjunctions);
    hasDisjunctions |= backup_hasDisjunctions;

    // Since we're in "visitAnd", we must have conjunctions
    allConjunctions.addAll(operands);
    hasConjunctions = true;

    // Reset to false, since it might have been changed during inner "visit"
    // processes
    isAtomic = false;
    return bmgr.and(operands);
  }

  /**
   * Analyzes an OR-Formula
   *
   * @param operands List of all conjuncts of the OR
   * @return The Input (one SINGLE OR-Formula)
   */
  @Override
  public BooleanFormula visitOr(@NotNull List<BooleanFormula> operands) {
    Preconditions.checkNotNull(operands);

    // If we're inside an OR, the formula cannot be atomic anymore
    isAtomic = false;

    // Reset isNegationPreceding to false (since we want to freshly evaluate
    // each conjunct)
    boolean backupIsNegationPreceding = isNegationPreceding;
    isNegationPreceding = false;

    // Backup hasConjunctions (locally!)
    // We do this, so that we can evaluate them freshly inside visit() and by
    // this we can determine
    // whether any of the disjunctions also has conjunctions (= nested
    // conjunctions)
    boolean backup_hasConjunctions = hasConjunctions;

    // Reset both booleans (for freshly evaluating all conjuncts)
    hasConjunctions = false;
    hasDisjunctions = false;

    // Visit all conjuncts
    visitAll(operands);

    // After we've visited all OR-Formulas, and we found conjunctions => we
    // cannot have a CNF!
    if (hasConjunctions) {
      isCNF = false;
    }

    // If we're in a negation and find an OR => We cannot have a CNF!
    if (insideNegation) {
      isCNF = false;
      isNNF = false;
    }

    // If we have no conjuncts and no disjuncts, we don't have any deeper
    // nesting (= only atomic / negated atomic
    // formulas) and can thus safely add the operands to the list of positive
    // formulas.
    if (!hasConjunctions && !hasDisjunctions && !backupIsNegationPreceding) {
      allPositiveFormulas.add(bmgr.or(operands));
    }

    // Restore hasConjunctions (and OR it with current value, so that if one
    // of our disjunctions has conjunctions,
    // the final value will also be true = has conjunctions);
    hasConjunctions |= backup_hasConjunctions;

    // Since we're in "visitAnd", we must have disjunctions
    allDisjunctions.addAll(operands);
    hasDisjunctions = true;

    // Reset to false, since it might have been changed during inner "visit"
    // processes
    isAtomic = false;
    return bmgr.or(operands);
  }


  /**
   * Analyzes an XOR-Formula
   * (just rewrites XOR into "plain" formula and visits the converted formula.
   *
   * @param operand1 Left Operand
   * @param operand2 Right Operand
   * @return Original Formula (Rewritten without XOR)
   */
  @Override
  public BooleanFormula visitXor(@NotNull BooleanFormula operand1,
                                 @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);

    // XOR => neither atomic, nor NNF nor CNf
    isAtomic = false;
    isNNF = false;
    isCNF = false;

    // Visit & return the rewritten Formula (since we can only analyze
    // "plain" formulas)
    return visit(rewriteXor(operand1, operand2));
  }

  /**
   * Analyzes an Equivalences-Formula
   * (just rewrites Equivalences into "plain" formula and visits the
   * converted formula.
   *
   * @param operand1 Left Operand
   * @param operand2 Right Operand
   * @return Original Formula (Rewritten without Equivalence)
   */
  @Override
  public BooleanFormula visitEquivalence(@NotNull() BooleanFormula operand1,
                                         @NotNull() BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);

    // Equivalence => neither atomic, nor NNF nor CNf
    isAtomic = false;
    isNNF = false;
    isCNF = false;

    // Visit & return the rewritten Formula (since we can only analyze
    // "plain" formulas)
    return visit(rewriteEquivalence(operand1, operand2));
  }

  /**
   * Analyzes an Implication-Formula
   * (just rewrites Implication into "plain" formula and visits the converted
   * formula.
   *
   * @param operand1 Left Operand
   * @param operand2 Right Operand
   * @return Original Formula (Rewritten without Implication)
   */
  @Override
  public BooleanFormula visitImplication(@NotNull() BooleanFormula operand1,
                                         @NotNull() BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);

    // Implication => neither atomic, nor NNF nor CNF
    isAtomic = false;
    isNNF = false;
    isCNF = false;

    // Visit & return the rewritten Formula (since we can only analyze
    // "plain" formulas)
    return visit(rewriteImplication(operand1, operand2));
  }

  /**
   * Analyzes an If-Then-Else-Formula
   * (just rewrites If-Then-Else into "plain" formula and visits the
   * converted formula.
   *
   * @param condition   Condition (= If)
   * @param thenFormula Then-Part (= Then)
   * @param elseFormula Else-Part (= Else)
   * @return Original Formula (Rewritten without If-Then-Else)
   */
  @Override
  public BooleanFormula visitIfThenElse(@NotNull() BooleanFormula condition,
                                        @NotNull() BooleanFormula thenFormula
      , @NotNull() BooleanFormula elseFormula) {
    Preconditions.checkNotNull(condition);
    Preconditions.checkNotNull(thenFormula);
    Preconditions.checkNotNull(elseFormula);

    // If-Then-Else => neither atomic, nor NNF nor CNf
    isAtomic = false;
    isNNF = false;
    isCNF = false;

    // Visit & return the rewritten Formula (since we can only analyze
    // "plain" formulas)
    return visit(rewriteIfThenElse(condition, thenFormula, elseFormula));
  }

  /**
   * Analysis of Quantifier is NOT SUPPORTED!
   *
   * @param quantifier    Quantifier type: FORALL- or EXISTS-
   * @param quantifiedAST AST of the quantified node. Provided because it is
   *                      difficult to re-create
   *                      from the parameters.
   * @param boundVars     Variables bound by this quantifier.
   * @param body          Body of the quantified expression.
   * @return null (and throws error, since it is NOT supported!)
   */
  @Override
  public BooleanFormula visitQuantifier(@NotNull QuantifiedFormulaManager.Quantifier quantifier,
                                        @NotNull BooleanFormula quantifiedAST,
                                        List<Formula> boundVars,
                                        BooleanFormula body) {
    Preconditions.checkNotNull(quantifier);
    Preconditions.checkNotNull(quantifiedAST);

    Log.error(SMTProcessingError.FORMULA_ANALYZER_QUANTIFIER_NOT_SUPPORTED.format(prettyPrintFormula(quantifiedAST), prettyPrintFormula(body)));
    return null;
  }


  /* ############################################# */
  /* #    Private Helpers used for Conversion    # */
  /* ############################################# */

  // Rewrite an Equivalence Relation by just using AND, OR and NOT
  private BooleanFormula rewriteEquivalence(@NotNull BooleanFormula operand1,
                                            @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);

    return bmgr.or(bmgr.and(operand1, operand2), bmgr.and(bmgr.not(operand1),
        bmgr.not(operand2)));
  }

  // Rewrite an Implication by just using AND, OR and NOT
  private BooleanFormula rewriteImplication(@NotNull BooleanFormula operand1,
                                            @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);

    return bmgr.or(bmgr.not(operand1), operand2);
  }

  // Rewrite an XOR by just using AND, OR and NOT
  private BooleanFormula rewriteXor(@NotNull BooleanFormula operand1,
                                    @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);

    return bmgr.or(bmgr.and(operand1, bmgr.not(operand2)),
        bmgr.and(bmgr.not(operand1), operand2));
  }

  // Rewrite If-Then-Else by just using ANd, OR and NOT
  private BooleanFormula rewriteIfThenElse(@NotNull BooleanFormula condition,
                                           @NotNull BooleanFormula thenFormula, @NotNull BooleanFormula elseFormula) {
    Preconditions.checkNotNull(condition);
    Preconditions.checkNotNull(thenFormula);
    Preconditions.checkNotNull(elseFormula);

    return bmgr.or(bmgr.and(condition, thenFormula),
        bmgr.and(bmgr.not(condition), elseFormula));
  }


  /**
   * Returns pretty printed version of the Formula
   */
  private String prettyPrintFormula(BooleanFormula f) {
    return CharMatcher.whitespace().trimAndCollapseFrom(
        new PrettyPrinter(this.fmgr).formulaToString(f)
            .replaceAll("[\\t\\n\\r]+", " "), ' ');
  }
}
