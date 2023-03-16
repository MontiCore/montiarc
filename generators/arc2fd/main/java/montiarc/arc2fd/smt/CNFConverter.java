/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.arc2fd.expressions.MAProcessingError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.FunctionDeclaration;
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager;
import org.sosy_lab.java_smt.api.Tactic;
import org.sosy_lab.java_smt.api.visitors.BooleanFormulaVisitor;
import org.sosy_lab.java_smt.utils.PrettyPrinter;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.cartesianProduct;
import static java.util.Collections.singletonList;

/**
 * Converter which Allows us to Convert a Java-SMT Formula into an equivalent
 * Formula in Conjunctive Normal Form (CNF)
 */
public class CNFConverter implements BooleanFormulaVisitor<List<BooleanFormula>> {

  // Java-SMT Formula Manager
  private final FormulaManager fmgr;

  // Java-SMT Boolean Formula Manager
  private final BooleanFormulaManager bmgr;

  private final SMTFormulaAnalyzer analyzer;

  /**
   * Constructor to instantiate a CnfFormulaVisitor with an FormulaManager
   * and a BooleanFormulaManager
   *
   * @param fmgr Java-SMT Formula Manager
   * @param bmgr Java-SMT Boolean FormulaManager
   */
  public CNFConverter(@NotNull FormulaManager fmgr,
                      @NotNull BooleanFormulaManager bmgr) {
    Preconditions.checkNotNull(fmgr);
    Preconditions.checkNotNull(bmgr);
    this.fmgr = fmgr;
    this.bmgr = bmgr;
    this.analyzer = new SMTFormulaAnalyzer(fmgr, bmgr);
  }

  /**
   * Constructor to instantiate a CnfFormulaVisitor with an FormulaManager
   * (BooleanFormulaManager will be
   * created based on the formula manager)
   *
   * @param fmgr Java-SMT Formula Manager
   */
  protected CNFConverter(@NotNull FormulaManager fmgr) {
    this(fmgr, fmgr.getBooleanFormulaManager());
  }

  /**
   * Converts the given formula into a Formula in CNF and returns it
   *
   * @param f Formula to Convert
   * @return Equivalent Formula in CNF
   */
  public BooleanFormula convertToCNF(BooleanFormula f) {
    // Ensure that we've got a valid formula
    if (Objects.isNull(f)) {
      Log.error(SMTProcessingError.CNF_CONVERSION_NO_FORMULA_FOUND.format());
      return null;
    }

    // Convert to NNF (makes our live easier)
    BooleanFormula nnf;
    try {
      nnf = fmgr.applyTactic(f, Tactic.NNF);
    } catch (InterruptedException e) {
      Log.error(SMTProcessingError.CNF_CONVERSION_FAILED_CONVERTING_TO_NNF.format(prettyPrintFormula(f)));
      throw new RuntimeException(e);
    }

    // Visit the NNF (= Converting the Formula into
    BooleanFormula cnf = bmgr.and(visit(nnf));

    // Before returning the resulting cnf, ensure that we really have
    // constructed a formula in CNF
    this.analyzer.analyze(cnf);
    if (!this.analyzer.isCNF()) {
      Log.error(SMTProcessingError.CNF_CONVERSION_FAILED.format(prettyPrintFormula(f), prettyPrintFormula(cnf)));
    }

    return cnf;
  }

  /**
   * Start visiting the given Formula and computing the equivalent formula in
   * CNF.
   * <p>
   * Please note, that we don't return a single formula but rather a List of
   * Formulas, where each list element
   * resembles one conjunction (in CNF) of the final CNF.
   * This makes the conversion much easier, since for the OR-Case (=
   * distributive law) we have to combine
   * all combinations of all existing CNF-Formulas. If we're using this
   * construction, this boils down to simply
   * computing the Cartesian Product between a double-nested List which makes
   * this step pretty easy.
   * <p>
   * This also means, that we MUST apply and "AND" to the final result of
   * visit() to get the real CNf.
   *
   * @param f BooleanFormula to Visit
   * @return List of Boolean Formulas (= conjunctions (in CNF) of the final CNF
   * => AND-them for obtaining the real, final CNF)
   */
  private List<BooleanFormula> visit(@NotNull BooleanFormula f) {
    Preconditions.checkNotNull(f);

    // Ensure we really got an NNF
    this.analyzer.analyze(f);
    if (!this.analyzer.isNNF()) {
      Log.error(SMTProcessingError.CNF_CONVERSION_FAILED_CONVERTING_TO_NNF.format(prettyPrintFormula(f)));
    }

    // Visit the formula and return the result...
    return bmgr.visit(f, this);
  }

  /**
   * Small helper which iteratively visits a list of formulas
   *
   * @param formulas List of formulas to visit
   * @return List of List of (visited) formulas in CNF
   */
  private List<List<BooleanFormula>> visitAll(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);

    // Visit all Formulas in the list and combine the results in a List
    return formulas.stream().map(this::visit).collect(Collectors.toList());
  }

  // Visit a Constant
  @Override
  public List<BooleanFormula> visitConstant(boolean value) {
    return singletonList(bmgr.makeBoolean(value));
  }

  // Visit an Atom (like true, false, a Variable, ...)
  @Override
  public List<BooleanFormula> visitAtom(@NotNull BooleanFormula atom,
                                        FunctionDeclaration<BooleanFormula> funcDecl) {
    return singletonList(atom);
  }

  // Visit a Not
  @Override
  public List<BooleanFormula> visitNot(BooleanFormula formula) {
    // Since traversed formula is assumed to be in NNF, we can just return
    // the (negated) operand
    return singletonList(bmgr.not(formula));
  }

  /**
   * Visit an And
   * Converting ANDs is the easy case, since we just have to visit the
   * "sub-formulas" and in the end
   * just conjunct all together.
   *
   * @param formulas List of formulas which we want to visit and ultimately
   *                 convert to CNF
   * @return List of multiple Formulas each of which is in CNF
   */
  @Override
  public List<BooleanFormula> visitAnd(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);

        /*
        visit() returns a List of Boolean Formulas (which are all in CNF)
        visitAll() returns a List of List of Boolean Formulas (which are all
        in CNF by itself)
        Since we're in an AND, we have to conjunct them.
        If we conjunct a List of BooleanFormulas which are all in CNF, we
        still have a CNF
        => We can flatten the List of List of Boolean Formulas to a List of
        Boolean Formulas without problems
         */
    Supplier<Stream<BooleanFormula>> sup =
        () -> visitAll(formulas).stream().flatMap(List::stream);

    if (sup.get().anyMatch(bmgr::isFalse))
      return singletonList(bmgr.makeBoolean(false));

    // Otherwise, just get the stream again and convert it into a list
    return sup.get().collect(Collectors.toList());
  }

  /**
   * Visit an OR
   * <p>
   * MAIN GOAL: First we convert each disjunction into a CNF (= Set of
   * Boolean Formulas, where each element resembles
   * one conjunction in the CNF-Form of the Formula)
   * Then we compute all necessary Combinations for correctly applying the
   * Distributive Property (to propagate
   * the AND outwards and OR inwards). Basic Form: a || (b && c) => (a || b)
   * && (a ||c)
   * <p>
   * Note: 'operands' gives us a List of List of converted CNF-Formulas and
   * all Formulas inside one
   * List<BooleanFormula> are basically the conjunctive parts of one
   * CNF-Formula (i.e., [["a"], ["b", "c"]]).
   * So what remains to do, is to apply the distributive property over and
   * over again (combine each set with each other).
   * For this, we start with the left-most list, and combine it with all
   * entries from the second-left-most list.
   * Then we continue and combine the result from previous operation to the
   * third-most element, etc...
   * <p>
   * For this, we can apply the
   * <a href="https://en.wikipedia.org/wiki/Cartesian_product">Cartesian Product</a>
   * (= combine each set with each other),
   * DRAWBACK: Combinatorial Explosion!
   *
   * @param formulas List of formulas to convert
   * @return Set of Formulas in CNF (which in the end form the final
   * conjunction)
   */
  @Override
  public List<BooleanFormula> visitOr(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);

    // Visit all Sub-Formulas (and thus, convert all of them into CNFs, so
    // operands only includes Formulas in CNF)
    List<List<BooleanFormula>> operands = visitAll(formulas);

    // Compute Cartesian Product and map each resulting list-entry with an
    // "OR"-Relation to correctly apply
    // the distributive law. Then we can return the result.
    return cartesianProduct(operands).stream().map(bmgr::or).collect(Collectors.toList());
  }

  @Override
  public List<BooleanFormula> visitBoundVar(@NotNull BooleanFormula var,
                                            int deBruijnIdx) {
    Preconditions.checkNotNull(var);
    visit(var);
    return singletonList(var);
  }

  @Override
  public List<BooleanFormula> visitXor(@NotNull BooleanFormula operand1,
                                       @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);
    Log.error(MAProcessingError.SMT2FD_VISITOR_NO_CNF.format("XOR"));
    return null;
  }

  @Override
  public List<BooleanFormula> visitEquivalence(@NotNull BooleanFormula operand1, @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);
    Log.error(MAProcessingError.SMT2FD_VISITOR_NO_CNF.format("equivalences"));
    return null;
  }

  @Override
  public List<BooleanFormula> visitImplication(@NotNull BooleanFormula operand1, @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);
    Log.error(MAProcessingError.SMT2FD_VISITOR_NO_CNF.format("implications"));
    return null;
  }

  @Override
  public List<BooleanFormula> visitIfThenElse(@NotNull BooleanFormula condition,
                                              @NotNull BooleanFormula thenFormula,
                                              @NotNull BooleanFormula elseFormula) {
    Preconditions.checkNotNull(condition);
    Preconditions.checkNotNull(thenFormula);
    Preconditions.checkNotNull(elseFormula);
    Log.error(MAProcessingError.SMT2FD_VISITOR_NO_CNF.format("If-Else"));
    return null;
  }

  @Override
  public List<BooleanFormula> visitQuantifier(@NotNull QuantifiedFormulaManager.Quantifier quantifier,
                                              @NotNull BooleanFormula quantifiedAST,
                                              List<Formula> boundVars,
                                              BooleanFormula body) {
    Preconditions.checkNotNull(quantifier);
    Preconditions.checkNotNull(quantifiedAST);
    Log.error(MAProcessingError.SMT2FD_VISITOR_NO_CNF.format("Quantifiers"));
    return null;
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
