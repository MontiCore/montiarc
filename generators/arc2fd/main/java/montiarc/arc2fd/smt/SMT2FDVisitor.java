/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.sosy_lab.java_smt.api.*;
import org.sosy_lab.java_smt.api.visitors.BooleanFormulaVisitor;
import org.sosy_lab.java_smt.utils.PrettyPrinter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Helper with which we can process a Java-SMT Formula so that we can then
 * extract Sets of different
 * Feature-Diagram Entries from it.
 * In particular, after processing a Formula we can get:
 * - Optionals (Optional in the Formula => Special Notation in Feature Diagrams)
 * - Simple OR (Simple OR with only positive, disjunctive atoms => Special
 * Notation)
 * - XOR (Special & Compact Notation)
 * - All Remaining Conjunctions (which are not part of the first three sets)
 * - Excludes (all Imply-Constraints in order to correctly translate
 * "allRemainingConjunctions")
 * - Requires (all Imply-Constraints in order to correctly translate
 * "allRemainingConjunctions")
 */
public class SMT2FDVisitor implements BooleanFormulaVisitor<BooleanFormula> {

  /**
   * Java-SMT Formula Manager
   */
  private final FormulaManager fmgr;

  /**
   * Java-SMT Boolean Formula Manager
   */
  private final BooleanFormulaManager bmgr;

  /**
   * SMTFormulaAnalyzer which allows us to easily analyze formulas & get
   * important characteristics
   */
  private final SMTFormulaAnalyzer analyzer;

  /**
   * Keep Track of the last "Required"-Relation we added inside the function
   * "extractRequiresAndExclude".
   * By this, we can remove this relation if we can simplify the resulting
   * FD-Relation.
   */
  private BooleanFormula lastRequiredRelationKeyAdded;

  /**
   * Keep track of the name of the root (to add / remove proper relations
   * from and to it, i.e. for epsilon relations)
   */
  private BooleanFormula rootName;

  /**
   * Keep track of all optional formulas
   */
  private FDRelation<BooleanFormula> optionals;

  /**
   * Keep track of all remaining conjunctions.
   * Remaining Conjunctions are all the conjunctions, which are not part of
   * the others (like XOR, optionals, ...)
   */
  private FDRelation<BooleanFormula> allRemainingConjunctions;

  /**
   * Keep track of all formulas which can be simply combined by an "or"
   * without anything more (= only positive atoms)
   */
  private FDRelation<BooleanFormula> simpleOrs;

  /**
   * Keep track of all formulas which can be combined by a "xor"
   */
  private FDRelation<BooleanFormula> xors;

  /**
   * Keep track of all Requires Relations
   */
  private FDRelation<BooleanFormula> requiresRelations;

  /**
   * Keep track of all Exclude Relations
   */
  private FDRelation<BooleanFormula> excludesRelations;

  // Stores all conjunctions (after simplifying / extracting things like
  // simple or and XOR)
  // Additionally, Conjunctions might appear twice, so we have a list here

  /**
   * Construct SMT2FDVisitor based on both, a Formula Manager and a Boolean
   * Formula Manager
   *
   * @param fmgr FormulaManager to use
   * @param bmgr BooleanFormulaManager to use
   */
  public SMT2FDVisitor(@NotNull FormulaManager fmgr,
                       @NotNull BooleanFormulaManager bmgr) {
    Preconditions.checkNotNull(fmgr);
    Preconditions.checkNotNull(bmgr);
    this.fmgr = fmgr;
    this.bmgr = bmgr;
    this.analyzer = new SMTFormulaAnalyzer(fmgr, bmgr);
  }

  /**
   * Removes the first Element from a Collection
   *
   * @param c Collection
   * @return The removed Element
   */
  @Nullable
  public static <T> T removeFirst(@NotNull Collection<? extends T> c) {
    Preconditions.checkNotNull(c);
    Iterator<? extends T> it = c.iterator();
    if (!it.hasNext()) {
      return null;
    }
    T removed = it.next();
    it.remove();
    return removed;
  }

  /**
   * Triggers the analysis for a given formula (and clears the analysis
   * buffer in advance).
   * After executing the function, we can then use this.analyzer.getXYZ() to
   * get important characteristics.
   *
   * @param formula Formula to analyse
   */
  private void analyze(@NotNull BooleanFormula formula) {
    Preconditions.checkNotNull(formula);
    this.analyzer.clear_buffer();
    this.analyzer.visit(formula);
  }

  /**
   * Processes a Formula. Afterwards, the SMT2FDVisitor-Instance can return
   * valuable information for the
   * FD-Construction
   *
   * @param f        Formula to Process (BooleanFormula or NumeralFormula -
   *                 last one gets just
   *                 prettyprinted)
   * @param rootName Formula of the Root of the FD so that we can add new
   *                 relations from the root
   */
  public void process(BooleanFormula f, BooleanFormula rootName) {
    if (Objects.isNull(f)) {
      Log.error(SMTProcessingError.SMT2FD_PROCESSING_NO_FORMULA.format());
      return;
    } else if (Objects.isNull(rootName)) {
      Log.error(SMTProcessingError.ROOT_IS_NULL.format(f));
      return;
    }

    // Clear all Variables / Buffers
    clear_buffer(rootName);

    // Analyze the Formula
    analyze(f);
    if (!this.analyzer.isCNF()) {
      Log.error(SMTProcessingError.SMT2FD_VISITOR_NO_CNF.format(prettyPrintFormula(f)));
      return;
    } else if (!this.analyzer.hasConjunctions()) {
      // Since we only have disjunctions, we assume this means that we only
      // have one conjunction
      // For example, if we pass in "a || b", this is assumed to be our
      // conjunction => so manually add it
      this.allRemainingConjunctions.addRootRelation(f);
    }

    // Start the real visiting & processing
    visit(f);

    // Finally, do the last refinement step by re-visiting all remaining
    // conjunctions and extracting
    // the "required" and "exclude" information
    processRequiredAndExclude();
  }

  /**
   * Visits the given Formula (Formula MUST be in CNF)
   * You can convert the a Formula into CNF by applying CNFVisitor
   *
   * @param f Formula in CNF
   */
  private BooleanFormula visit(@NotNull BooleanFormula f) {
    Preconditions.checkNotNull(f);
    analyze(f);
    return bmgr.visit(f, this);
  }

  /**
   * Small helper which iteratively visits a list of formulas
   *
   * @param formulas List of formulas to visit
   */
  private List<BooleanFormula> visitAll(@NotNull List<BooleanFormula> formulas) {
    Preconditions.checkNotNull(formulas);
    return formulas.stream().map(this::visit).collect(Collectors.toList());
  }

  /**
   * @return A Set of all Remaining Conjunctions (= not part of any other Set)
   */
  public FDRelation<BooleanFormula> getAllRemainingConjunctions() {
    // Convert it to a Set, since we're not interested in duplicated formulas
    return allRemainingConjunctions;
  }

  /**
   * @return Set of all Simple Ors
   */
  public FDRelation<BooleanFormula> getSimpleOrs() {
    return simpleOrs;
  }

  /**
   * @return Set of all XORs
   */
  public FDRelation<BooleanFormula> getXors() {
    return xors;
  }

  /**
   * @return Set of all Optionals
   */
  public FDRelation<BooleanFormula> getOptionals() {
    return optionals;
  }

  /**
   * @return Set of all Requires-Relations
   */
  public FDRelation<BooleanFormula> getRequires() {
    return requiresRelations;
  }

  /**
   * @return Set of all Exclude-Relations
   */
  public FDRelation<BooleanFormula> getExcludes() {
    return excludesRelations;
  }

  /**
   * Clears the currently stored Formulas and reset them (for a new, fresh
   * run of the visit method)
   *
   * @param rootName Formula of the Root of the FD so that we can add new
   *                 relations from the root
   */
  public void clear_buffer(BooleanFormula rootName) {
    allRemainingConjunctions = new FDRelation<>(rootName);
    simpleOrs = new FDRelation<>(rootName);
    xors = new FDRelation<>(rootName);
    optionals = new FDRelation<>(rootName);
    requiresRelations = new FDRelation<>(rootName);
    excludesRelations = new FDRelation<>(rootName);
    this.rootName = rootName;
  }

  /**
   * Function handels all Conjunctions. In order to get a nice and clean
   * solution (= nice and clear Feature Diagram)
   * we additionally try to find "xor" and "simple Ors" to make the resulting
   * FD simpler.
   *
   * @param operands List of all conjunction formulas
   * @return The Input Formula (= AND over all operands)
   */
  @Override
  public BooleanFormula visitAnd(@NotNull List<BooleanFormula> operands) {
    Preconditions.checkNotNull(operands);

    // Create new Candidate Set for current "AND" (is used later on to find
    // potential XORs)
    List<Set<BooleanFormula>> xorCandidateSet = new ArrayList<>();

    List<XorHelper> possibleXORs = new ArrayList<>();

    // Iterate over all sub-formulas and try to find simple ors and possible
    // XORs
    for (BooleanFormula o : operands) {
      // Trigger Analysis of the formula to get important characteristic
      // information
      analyze(o);

      // Add the Formula to the remaining Conjunctions (since we haven't used
      // it properly yet)
      allRemainingConjunctions.addRootRelation(o);
      if (this.analyzer.hasDisjunctionsOnly() && this.analyzer.hasPositiveAtomsOnly()) {
        // "and"-formula with only disjunctions & positive atoms (=> simple
        // or case, but could also be XOR)
        // First add it to both, later check if we have to remove it because
        // we've found better XORs
        this.simpleOrs.addRootRelation(o);
//                analyzer.getPositiveAtoms().forEach(p -> this.simpleOrs
//                .addRootRelation(p));

        // If we have simple or, we don't want it to be part of
        // remainingConjunctions
        allRemainingConjunctions.removeRootRelation(o);

        // Create a new Helper Instance storing all positive Atoms and also
        // the original formula
        // (since all positive Atoms is just a list, but we also need the
        // original formula eventually)
        possibleXORs.add(new XorHelper(this.analyzer.getPositiveAtoms(), o));
      } else if (this.analyzer.hasDisjunctionsOnly() && this.analyzer.hasNegatedAtomsOnly()) {
        // "and"-Formula with only disjunctions & negative atoms (=> possible
        // candidates for merging into an XOR)
        xorCandidateSet.add(this.analyzer.getNegatedAtoms());
      }
    }

    // Test if we can combine Formulas into XORs (if yes, the function
    // automatically removes them from the corresponding sets)
    testForXOR(possibleXORs, xorCandidateSet);

    // After the Preprocessing is done, we can continue with our visiting (=
    // all conjunctions have been added)
    // Visit all Sub-Formulas
    return bmgr.and(visitAll(operands));
  }

  /**
   * Function which processes a Boolean Formula with only disjunctions > 0
   * negative literals
   * For this, we need to extract Requires and Exclude Relations recursively.
   * <p>
   * Idea behind the algorithm:
   * - First note, that we only need to consider negatedAtoms, since
   * positiveAtoms are basically just a simple OR
   * - Also note, that depending on the Mode, we have to handle the Base Case
   * differently. The rest is equally
   * for both, Excludes and Requires.
   * - We have two main patterns:
   * Pattern 1: (!a || !b) corresponds to "optional AND" and mutual "excludes"
   * Pattern 2: (!a || b) corresponds to "optional AND" and "a requires b"
   * <p>
   * Case 1: Only Negated Literals => Recursively apply First pattern and
   * construct "epsilon-helpers"
   * So for example we rewrite (!a || !b || !c) into the constraints:
   * (!a || X) with X=(!b || !c), (a implies X) and (b excludes c) and ,
   * This gives us a correct transformation for the case
   * <p>
   * Case 2: Negated & Positive Literals => Recursively apply Second pattern
   * and construct "epsilon-helpers"
   * So for example, we rewrite (!a || !b || c) into the constraints:
   * (!a || X) with X=(!b || c), (a implies X) and (b implies c)
   * This gives us a correct transformation for the case
   *
   * @param negatedAtoms  Set of Negated Atoms to analyze (= contained in the
   *                      disjunction)
   *                      NOTE: The list may still ONLY CONTAIN POSITIVE
   *                      ATOM-FORMULAS (the List just
   *                      resembles the negated atoms, but the negation must
   *                      still be removed in advance, i.e.
   *                      by using the FormulaAnalyzer)
   * @param positiveAtoms Set of Positive Atoms to analyze (= contained in
   *                      the disjunction)
   * @param depth         Current Recursion Depth
   * @param mode          Determine whether we have negations only or
   *                      combined (= positive + negated atoms)
   */
  private void extractRequiresAndExclude(@NotNull Set<BooleanFormula> negatedAtoms,
                                         @NotNull Set<BooleanFormula> positiveAtoms,
                                         int depth,
                                         @NotNull MODE mode
  ) {
    Preconditions.checkNotNull(negatedAtoms);
    Preconditions.checkNotNull(positiveAtoms);
    Preconditions.checkNotNull(mode);

    // Create Lists so we can access the values easier
    List<BooleanFormula> negatedAtomsList = new ArrayList<>(negatedAtoms);

    // If we have depth == 0, want to use the root relation. Otherwise, we
    // want to use the previous placeholder
    BooleanFormula previousPlaceholder = (depth == 0) ? rootName :
        bmgr.makeVariable(FDRelation.getEpsilon(depth - 1));
    BooleanFormula currentPlaceholder =
        bmgr.makeVariable(FDRelation.getEpsilon(depth));

        /*
        Keep track of all features we used inside simpleOrs and xors. This
        comes in handy in the following part,
        where we only want to add optionals, if the features haven't already
        appeared in simpleOrs or XORs (since or/xor
        has higher importance than optionals)
         */
    Set<String> allFeaturesUsed = new HashSet<>();
    this.simpleOrs.getRelationsHashMap().values().forEach(val -> val.forEach(f -> {
      analyzer.analyze(f);
      analyzer.getAllAtoms().forEach(a -> allFeaturesUsed.add(a.toString()));
    }));
    this.xors.getRelationsHashMap().values().forEach(val -> val.forEach(f -> {
      analyzer.analyze(f);
      analyzer.getAllAtoms().forEach(a -> allFeaturesUsed.add(a.toString()));
    }));

    if (negatedAtoms.size() > 0) {
      if (mode == MODE.NEGATED_ONLY && negatedAtomsList.size() == 2) {
        // First Base Case (We only have two negated atoms left => Optionals
        // & Mutual Exclude)
        this.optionals.addRelations(previousPlaceholder, negatedAtoms);
        this.excludesRelations.addRelation(negatedAtomsList.get(0),
            negatedAtomsList.get(1));
      } else {
        // Else we're not at the end => Recurse all remaining formulas...
        BooleanFormula firstElement = removeFirst(negatedAtoms);
        assert firstElement != null; // Should never happen since we check
        // negatedAtoms.size() > 0

        // Note: We only add optionals, if they haven't appeared in a
        // "or/xor" before
        if (allFeaturesUsed.stream().noneMatch(f -> f.contains(negatedAtomsList.get(0).toString())))
          this.optionals.addRelation(previousPlaceholder,
              negatedAtomsList.get(0));

        if (allFeaturesUsed.stream().noneMatch(f -> f.contains(currentPlaceholder.toString())))
          this.optionals.addRelation(previousPlaceholder, currentPlaceholder);

        // Add Requires Relation between current negated element and current
        // placeholder
        this.requiresRelations.addRelation(firstElement, currentPlaceholder);
        lastRequiredRelationKeyAdded = firstElement;
        extractRequiresAndExclude(negatedAtoms, positiveAtoms, ++depth, mode);
      }
    } else if (positiveAtoms.size() == 1) {
      // If we only have one positive atom, we can directly append it and
      // don't have to use the "Epsilon"-Way...
      BooleanFormula secondPreviousPlaceholder = (depth < 2) ? rootName :
          bmgr.makeVariable(FDRelation.getEpsilon(depth - 2));
      if (depth > 0) {
        this.optionals.removeRelation(secondPreviousPlaceholder,
            previousPlaceholder);
        this.requiresRelations.removeRelation(lastRequiredRelationKeyAdded,
            previousPlaceholder);
        positiveAtoms.forEach(p -> this.requiresRelations.addRelation(lastRequiredRelationKeyAdded, p));
      }
      positiveAtoms.forEach(p -> this.optionals.addRelation(secondPreviousPlaceholder, p));
    } else if (positiveAtoms.size() > 0) {
      // Second Base Case (We only have X positive Atoms Left => simple or
      // relation (from current placeholder))
      this.simpleOrs.addRootRelation(bmgr.or(positiveAtoms));
    }
  }

  /**
   * Function re-visits all Remaining Conjunctions (= couldn't be sorted into
   * another set like XOR or Simple OR)
   * And populates the Required and Excludes Sets accordingly.
   * All Formulas inside "allRemainingConjunctions" should be Boolean Formula
   * with only disjunctions
   * and > 0 positive and > 0 negative literals
   */
  private void processRequiredAndExclude() {
    // Loop over all REMAINING conjunctions (again) to check whether we have
    // some open cases left which need to be considered
    // This is necessary, since we first try to find optimizations and only
    // then take care of all the remaining cases

    // Create Copy of the Conjunctions so that we can modify it inside the loop
    FDRelation<BooleanFormula> newAllRemainingConjunctions =
        allRemainingConjunctions.getDeepCopy();

    // Query each Conjunction Entry (should only be 1,b ut still)
    for (Map.Entry<BooleanFormula, Set<BooleanFormula>> entry :
        allRemainingConjunctions.getRelationsHashMap().entrySet()) {
      // For each Conjunction Entry, query all Formulas
      for (BooleanFormula o :
          allRemainingConjunctions.getRelationByKey(entry.getKey())) {
        // Trigger Analysis of the formula to get important characteristic
        // information
        analyze(o);

        if (this.analyzer.hasDisjunctionsOnly() && this.analyzer.hasPositiveAtomsOnly()) {
//                    this.simpleOrs.addRootRelations(analyzer.getAllAtoms());
//                    analyzer.getAllAtoms().forEach(atom -> this.simpleOrs
//                    .addRelation(entry.getKey(), atom));
          this.simpleOrs.addRelation(entry.getKey(), o);
//                    simpleOrs.addRelation(entry.getKey(), o);
          newAllRemainingConjunctions.removeRelation(entry.getKey(), o);
        } else if (this.analyzer.hasDisjunctionsOnly() && this.analyzer.hasNegatedAtomsOnly()) {
          // Only Negated Atoms => Use NEGATED_ONLY Mode
          newAllRemainingConjunctions.removeRelation(entry.getKey(), o);
          extractRequiresAndExclude(this.analyzer.getNegatedAtoms(),
              this.analyzer.getPositiveAtoms(), 0, MODE.NEGATED_ONLY);
        } else if (this.analyzer.hasDisjunctionsOnly() && this.analyzer.getNegatedAtoms().size() > 0) {
          // Negated & Positive Atoms => COMBINED Mode
          newAllRemainingConjunctions.removeRelation(entry.getKey(), o);
          extractRequiresAndExclude(this.analyzer.getNegatedAtoms(),
              this.analyzer.getPositiveAtoms(), 0, MODE.COMBINED);
        }
      }
    }

    // Update the variable so that we only store the new remaining
    // conjunctions after processing
    allRemainingConjunctions = newAllRemainingConjunctions;
  }

  /**
   * Function handels all Disjunctions. Here we don't have many cases to
   * consider.
   * The most important case is, that we check if we have optionals (like (a
   * ||!a)), since they should be displayed
   * differently in the resulting FD.
   *
   * @param operands List of all conjunction formulas
   * @return The Input Formula (= AND over all operands)
   */
  @Override
  public BooleanFormula visitOr(@NotNull List<BooleanFormula> operands) {
    Preconditions.checkNotNull(operands);

    // Visit al Sub-Formulas
    List<BooleanFormula> res = visitAll(operands);

    // Analyze the given (complete) Formula
    analyze(bmgr.or(operands));

    // If we have exactly one positive and one negative atom and the atoms
    // are the same
    // => The Atom must be optional!
    Optional<BooleanFormula> optionalFormula =
        this.analyzer.isFormulaOptionalAtom();
    if (optionalFormula.isPresent()) {
      BooleanFormula f = optionalFormula.get();
      // Add it to the list of optionals
      optionals.addRootRelation(f);

      // Remove all occurrences of both, (a || !a) and (!a || a) form
      // conjunctions
      allRemainingConjunctions.removeRootRelation(bmgr.or(f, bmgr.not(f)));
      allRemainingConjunctions.removeRootRelation(bmgr.or(bmgr.not(f), f));
    }

    return bmgr.or(res);
  }

  /**
   * Note that this is not really guaranteed to find all possible XORs, but
   * only the obvious 2-XOR ones.
   *
   * @param helper          List of XorHelper = Set of Atoms & the
   *                        corresponding Formula.
   * @param xorCandidateSet Set of Sets of Possible Atoms we could use for
   *                        merging into an XOR.
   *                        The Set should contain all formulas which only
   *                        have disjunctive, negated atoms.
   *                        One examples would be: or(!b, !a)
   */
  private void testForXOR(@NotNull List<XorHelper> helper,
                          @NotNull List<Set<BooleanFormula>> xorCandidateSet) {
    Preconditions.checkNotNull(helper);
    Preconditions.checkNotNull(xorCandidateSet);

    // No need for further testing if either helper or candidate set is empty
    if (helper.size() == 0 || xorCandidateSet.size() == 0)
      return;

    // Sort Helper by Number of Atoms so that we first try to find the
    // "biggest" XOR possible
    helper.sort(Comparator.comparing(XorHelper::getNumberOfAtoms,
        Comparator.reverseOrder()));

    AtomicInteger index = new AtomicInteger();
    helper.forEach((t) -> {
      Set<Set<BooleanFormula>> required_atoms = new HashSet<>();
      Set<Set<BooleanFormula>> required_formulas = new HashSet<>();

      // Construct List of Required Formulas
      for (BooleanFormula atom1 : t.getXorAtoms()) {
        for (BooleanFormula atom2 : t.getXorAtoms()) {
          // Ignore same Indices
          if (atom1.equals(atom2))
            continue;

          // Add the required Atom
          required_atoms.add(new HashSet<>(Arrays.asList(atom1, atom2)));

          // Add the required formulas (could be (atom1 || atom2) or (atom2
          // || atom1)) => add both
          required_formulas.add(new HashSet<>(Arrays.asList(
              bmgr.or(bmgr.not(atom1), bmgr.not(atom2)),
              bmgr.or(bmgr.not(atom2), bmgr.not(atom1)))));
        }
      }


      // If all of our required_atoms are included in the candidate set,
      // we've found a XOR
      if (new HashSet<>(xorCandidateSet).containsAll(required_atoms)) {
        // For each required atom, remove exactly one instance (and NOT use
        // removeAll)
        required_atoms.forEach(xorCandidateSet::remove);

        BooleanFormula formula = t.getCorrespondingFormula();
        analyze(formula);

        // Add XOR Relations
        BooleanFormula lastFormula = null;
        for (BooleanFormula atom : t.getXorAtoms()) {
          if (lastFormula == null)
            lastFormula = atom;
          else
            lastFormula = bmgr.xor(lastFormula, atom);
        }
        // Add the XOR formula
        this.xors.addRootRelation(lastFormula);

        // Remove exactly one occurrence of each required formula (not
        // removeAll!), since we only
        // "consumed" one formula of those formulas each
        required_formulas.forEach(formulaSet -> {
          for (BooleanFormula f : formulaSet) {
            this.simpleOrs.removeRootRelation(f);
            this.simpleOrs.removeRootRelation(t.correspondingFormula);
            allRemainingConjunctions.removeRootRelation(f);
            allRemainingConjunctions.removeRootRelation(t.correspondingFormula);
          }
        });
      }
      index.getAndIncrement();
    });
  }

  /**
   * Visits an Atom
   *
   * @param atom     Atom to Process
   * @param funcDecl Function Declaration
   * @return The Input (an Atom)
   */
  @Override
  public BooleanFormula visitAtom(@NotNull BooleanFormula atom,
                                  FunctionDeclaration<BooleanFormula> funcDecl) {
    Preconditions.checkNotNull(atom);
    return atom;
  }

  /**
   * Visits a NOT-Formula
   *
   * @param operand Negated term.
   * @return The Input (a negated Term)
   */
  @Override
  public BooleanFormula visitNot(@NotNull BooleanFormula operand) {
    Preconditions.checkNotNull(operand);
    return bmgr.not(visit(operand));
  }

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
    return visit(var);
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

    BooleanFormula f = bmgr.xor(operand1, operand2);
    Log.error(SMTProcessingError.SMT2FD_VISITOR_NO_CNF.format(prettyPrintFormula(f)));
    return null;
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
    Log.error(SMTProcessingError.SMT2FD_VISITOR_NO_CNF.format(prettyPrintFormula(f)));
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
    Log.error(SMTProcessingError.SMT2FD_VISITOR_NO_CNF.format(prettyPrintFormula(f)));
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
    Log.error(SMTProcessingError.SMT2FD_VISITOR_NO_CNF.format(prettyPrintFormula(f)));
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
  public BooleanFormula visitQuantifier(@NotNull QuantifiedFormulaManager.Quantifier quantifier,
                                        @NotNull BooleanFormula quantifiedAST,
                                        List<Formula> boundVars,
                                        BooleanFormula body) {
    Preconditions.checkNotNull(quantifier);
    Preconditions.checkNotNull(quantifiedAST);
    Log.error(SMTProcessingError.SMT2FD_VISITOR_NO_CNF.format(prettyPrintFormula(quantifiedAST)));
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


  /**
   * Enum which is only used by extractRequiresAndExclude to distinguish
   * between whether our formula
   * only has negated atoms (= NEGATED_ONLY) or a combination of negated and
   * positive atoms (= COMBINED).
   */
  enum MODE {
    COMBINED,
    NEGATED_ONLY
  }

  /**
   * Private Helper Class which is used for making the checking for
   * Xor-Formulas a lot easier
   * The class can store xorAtoms and the corresponding Formula.
   * Example:
   * - xorAtoms: [a, b, c]
   * - corresponding : or(b, a, c)
   * Note: Since the Set is unordered, we must save the corresponding formula
   * in order to easily
   * obtain the right formula.
   */
  private static class XorHelper {
    /**
     * Set of Atoms (should/must correspond to the atoms used in
     * correspondingFormula)
     */
    private Set<BooleanFormula> xorAtoms;

    /**
     * Corresponding Formula (whose atoms are stored in xorAtoms)
     */
    private BooleanFormula correspondingFormula;

    /**
     * Create a new Object
     *
     * @param xorAtoms             Atoms of the Formula
     * @param correspondingFormula The Formula itself
     */
    public XorHelper(@NotNull Set<BooleanFormula> xorAtoms,
                     @NotNull BooleanFormula correspondingFormula) {
      Preconditions.checkNotNull(xorAtoms);
      Preconditions.checkNotNull(correspondingFormula);
      this.xorAtoms = xorAtoms;
      this.correspondingFormula = correspondingFormula;
    }

    /**
     * @return Number of XOR Atoms
     */
    public int getNumberOfAtoms() {
      return xorAtoms.size();
    }

    /**
     * @return Set of XOR-Atoms
     */
    public Set<BooleanFormula> getXorAtoms() {
      return xorAtoms;
    }

    /**
     * Updates the stored XOR-Atoms
     *
     * @param xorAtoms New Set of XOR-Atoms
     */
    public void setXorAtoms(@NotNull Set<BooleanFormula> xorAtoms) {
      Preconditions.checkNotNull(xorAtoms);
      this.xorAtoms = xorAtoms;
    }

    /**
     * @return Formula (Corresponding to the set of atoms)
     */
    public BooleanFormula getCorrespondingFormula() {
      return correspondingFormula;
    }

    /**
     * Updates the stored Formula
     *
     * @param correspondingFormula New Formula
     */
    public void setCorrespondingFormula(@NotNull BooleanFormula correspondingFormula) {
      Preconditions.checkNotNull(correspondingFormula);
      this.correspondingFormula = correspondingFormula;
    }
  }
}
