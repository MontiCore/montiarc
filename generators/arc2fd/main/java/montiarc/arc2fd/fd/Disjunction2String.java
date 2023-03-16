/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.arc2fd.smt.SMTFormulaAnalyzer;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.FunctionDeclaration;
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager;
import org.sosy_lab.java_smt.api.visitors.BooleanFormulaVisitor;
import org.sosy_lab.java_smt.utils.PrettyPrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper which can convert a Disjunction of (positive & negative) Atoms into
 * a String Representation.
 * Note that Conjunctions are neither supported nor allowed!
 */
public class Disjunction2String implements BooleanFormulaVisitor<BooleanFormula> {

  /**
   * Java-SMT Formula Manager
   */
  private final FormulaManager fmgr;

  /**
   * Java-SMT Boolean Formula Manager
   */
  private final BooleanFormulaManager bmgr;

  /**
   * Formula Analyzer which can give us valuable information and properties
   * of the parsed formulas
   */
  private final SMTFormulaAnalyzer formulaAnalyzer;


  /**
   * Keep track of the current List of Strings we've already constructed
   */
  private List<String> constructedStringArray = new ArrayList<>();


  protected Disjunction2String(@NotNull FormulaManager fmgr,
                               @NotNull BooleanFormulaManager bmgr) {
    Preconditions.checkNotNull(fmgr);
    Preconditions.checkNotNull(bmgr);
    this.fmgr = fmgr;
    this.bmgr = bmgr;
    this.formulaAnalyzer = new SMTFormulaAnalyzer(fmgr, bmgr);
  }

  protected Disjunction2String(@NotNull FormulaManager fmgr) {
    this(fmgr, fmgr.getBooleanFormulaManager());
  }

  /**
   * Converts a Set of Boolean Formulas into a Set of String-Representations
   *
   * @param formulas Set of Boolean Formulas to Convert
   * @return Set of all Formula in String-Representation
   */
  public Set<String> convertAllToStrings(@NotNull Set<? extends Formula> formulas) {
    Preconditions.checkNotNull(formulas);
    Set<String> tmp = new HashSet<>();

    formulas.forEach(f -> {
      if (!(f instanceof BooleanFormula))
        return;
      // By applying "Optional.ofNullable().orElse()" we implicitly check for
      // null-values and replace them
      // by an empty HashSet
      tmp.addAll(Optional.ofNullable(convertFormulaToString((BooleanFormula) f)).orElse(new HashSet<>()));
    });

    // Remove empty elements from the set and return it
    return tmp.stream().filter(item -> !item.trim().isEmpty()).collect(Collectors.toSet());
  }

  /**
   * Converts a Boolean Formula into String-Representation
   *
   * @param f Formula to Convert
   * @return Formula in String-Representation
   */
  public Set<String> convertFormulaToString(@NotNull BooleanFormula f) {
    Preconditions.checkNotNull(f);

    constructedStringArray = new ArrayList<>();
    this.formulaAnalyzer.analyze(f);

    visit(f);

    // Sort the list (only for nice output)
    constructedStringArray.sort(String::compareTo);
    return new HashSet<>(constructedStringArray);
  }

  /**
   * Visit the given Formula
   *
   * @param f Formula to Visit
   */
  protected void visit(@NotNull BooleanFormula f) {
    Preconditions.checkNotNull(f);
    bmgr.visit(f, this);
  }

  /**
   * Small helper which iteratively visits a list of formulas
   *
   * @param formulas List of formulas to visit
   */
  private void visitAll(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);
    formulas.forEach(this::visit);
  }

  /**
   * Visit a Constant (= just add it to the constructedStringArray)
   *
   * @param value Boolean Value of the Constant
   * @return Input Formula
   */
  @Override
  public BooleanFormula visitConstant(boolean value) {
    // We don't want to display booleans in the final FD
    return bmgr.makeBoolean(value);
  }

  /**
   * Visit a Constant (= just add a "not" after visiting the sub-formulas)
   *
   * @param formula Boolean Value we're currently inspecting
   * @return Input Formula
   */
  @Override
  public BooleanFormula visitNot(@NotNull BooleanFormula formula) {
    Preconditions.checkNotNull(formula);

    // First continue visiting the Formula
    visit(formula);

    // Afterwards, we take the last entry from the list, and add a "NOT"...
    int lastIndex = constructedStringArray.size() - 1;
    String lastElement = constructedStringArray.get(lastIndex);
    constructedStringArray.remove(lastIndex);
    constructedStringArray.add(FDConfiguration.NEGATED_ATOM_PREPEND + lastElement);

    return bmgr.not(formula);
  }

  /**
   * Visit an Atom (= just add it)
   *
   * @param atom Atom we're currently inspecting
   * @return The Input Atom
   */
  @Override
  public BooleanFormula visitAtom(@NotNull BooleanFormula atom,
                                  FunctionDeclaration<BooleanFormula> funcDecl) {
    Preconditions.checkNotNull(atom);

    constructedStringArray.add(atom.toString());
    return atom;
  }

  /**
   * Visit an OR (= just continue with the sub-formulas)
   *
   * @param formulas List of Disjunctive Formulas we're currently inspecting
   * @return The Input (disjunctive)
   */
  @Override
  public BooleanFormula visitOr(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);
    visitAll(formulas);
    // Join all formulas via OR
    String joined =
      Joiner.on(Separator.SIMPLE_OR_SEPARATOR.getSeparator()).join(constructedStringArray);
    constructedStringArray = new ArrayList<>(List.of(joined));
    return bmgr.or(formulas);
  }

  /**
   * Visit AND
   *
   * @param formulas List of all Conjuncts
   * @return null (and throws error, since it is NOT supported!)
   */
  @Override
  public BooleanFormula visitAnd(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);
    Log.error(FDConstructionError.NO_CONJUNCTIONS_ALLOWED.format(prettyPrintFormula(bmgr.and(formulas))));
    return null;
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
    Log.error(FDConstructionError.NO_CONJUNCTIONS_ALLOWED.format(prettyPrintFormula(var)));
    return null;
  }

  /**
   * Visits a XOR-Formula and produces an error, since the formula must be in
   * CNF!
   *
   * @param operand1 Left Operand
   * @param operand2 Right Operand
   * @return null (and throws error, since it is NOT supported!)
   */
  @Override
  public BooleanFormula visitXor(@NotNull BooleanFormula operand1,
                                 @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);
    visit(operand1);
    visit(operand2);
    // Join all formulas via OR
    String joined =
      Joiner.on(Separator.XOR_SEPARATOR.getSeparator()).join(constructedStringArray);
    constructedStringArray = new ArrayList<>(List.of(joined));
    return bmgr.xor(operand1, operand2);
  }

  /**
   * Visits an Equivalence-Formula and produces an error, since the formula
   * must be in CNF!
   *
   * @param operand1 Left Operand
   * @param operand2 Right Operand
   * @return null (and throws error, since it is NOT supported!)
   */
  @Override
  public BooleanFormula visitEquivalence(@NotNull BooleanFormula operand1,
                                         @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);
    BooleanFormula f = bmgr.equivalence(operand1, operand2);
    Log.error(FDConstructionError.NO_CONJUNCTIONS_ALLOWED.format(prettyPrintFormula(f)));
    return null;
  }

  /**
   * Visits an Implication-Formula and produces an error, since the formula
   * must be in CNF
   *
   * @param operand1 Left Operand
   * @param operand2 Right Operand
   * @return null (and throws error, since it is NOT supported!)
   */
  @Override
  public BooleanFormula visitImplication(@NotNull BooleanFormula operand1,
                                         @NotNull BooleanFormula operand2) {
    Preconditions.checkNotNull(operand1);
    Preconditions.checkNotNull(operand2);
    BooleanFormula f = bmgr.implication(operand1, operand2);
    Log.error(FDConstructionError.NO_CONJUNCTIONS_ALLOWED.format(prettyPrintFormula(f)));
    return null;
  }

  /**
   * Visits an If-Then-Else-Formula and produces an error, since the formula
   * must be in CNF
   *
   * @param condition   Condition (= If)
   * @param thenFormula Then-Part (= Then)
   * @param elseFormula Else-Part (= Else)
   * @return null (and throws error, since it is NOT supported!)
   */
  @Override
  public BooleanFormula visitIfThenElse(@NotNull BooleanFormula condition,
                                        @NotNull BooleanFormula thenFormula,
                                        @NotNull BooleanFormula elseFormula) {
    Preconditions.checkNotNull(condition);
    Preconditions.checkNotNull(thenFormula);
    Preconditions.checkNotNull(elseFormula);
    BooleanFormula f = bmgr.ifThenElse(condition, thenFormula, elseFormula);
    Log.error(FDConstructionError.NO_CONJUNCTIONS_ALLOWED.format(prettyPrintFormula(f)));
    return null;
  }

  /**
   * Visits a Quantifier and produces an error, since the formula must be in CNF
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
  public BooleanFormula visitQuantifier(@NotNull QuantifiedFormulaManager.Quantifier quantifier, @NotNull BooleanFormula quantifiedAST, List<Formula> boundVars, BooleanFormula body) {
    Preconditions.checkNotNull(quantifier);
    Preconditions.checkNotNull(quantifiedAST);
    Log.error(FDConstructionError.NO_CONJUNCTIONS_ALLOWED.format(prettyPrintFormula(quantifiedAST)));
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
