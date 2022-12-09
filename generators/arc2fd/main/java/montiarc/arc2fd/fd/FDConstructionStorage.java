/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.arc2fd.smt.FDRelation;
import montiarc.arc2fd.smt.SMT2FDVisitor;
import montiarc.arc2fd.smt.SMTFormulaAnalyzer;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montiarc.arc2fd.fd.Separator.ASSIGNMENT_SEPARATOR;


/**
 * HelperClass which stores our values for the final FD-Construction.
 * Consequently, it has Objects to hold
 * AND-Relations, OR-Relations, XOR-Relations, Exclude-Relations, and
 * Requires-Relations.
 * In addition to that, we have some helper functions i.e., for converting
 * the Objects into string representations.
 *
 * @param <T> Generic Type of the Objects we want to store (must extend
 *            Formula and should be
 *            BooleanFormula at the momnent).
 */
public class FDConstructionStorage<T extends Formula> {

  /**
   * Boolean Formula Manager
   */
  private final BooleanFormulaManager bmgr;

  /**
   * Formula Manager
   */
  private final FormulaManager fmgr;

  /**
   * CNF2String which helps us to convert a Formula in CNF into a String
   * Representation
   */
  private final Disjunction2String cnf2string;

  /**
   * Formula Analyzer which can give us valuable information and properties
   * of the parsed formulas
   */
  private final SMTFormulaAnalyzer formulaAnalyzer;

  /**
   * All Optional Formulas
   */
  private FDRelation<T> optionals;

  /**
   * All Formulas we can simply Disjunct
   */
  private FDRelation<T> simpleOr;

  /**
   * All Formulas we can XOR together
   */
  private FDRelation<T> xor;

  /* ########################### */
  /* #  String Representation  # */
  /* ########################### */
  /**
   * All remaining Conjunctions (= AND)
   */
  private FDRelation<T> remainingConjunctions;
  /**
   * All Requires
   */
  private FDRelation<T> requires;
  /**
   * All Excludes
   */
  private FDRelation<T> excludes;
  /**
   * String Representation(only after buildStringRepresentation!) of Optional
   * Formulas
   */
  private Set<String> stringOptionals = new HashSet<>();
  /**
   * String Representation (only after buildStringRepresentation!) of
   * Formulas we can simply Disjunct
   */
  private Set<String> stringSimpleOr = new HashSet<>();
  /**
   * String Representation (only after buildStringRepresentation!) of
   * Formulas we can XOR together
   */
  private Set<String> stringXor = new HashSet<>();
  /**
   * String Representation (only after buildStringRepresentation!) of
   * remaining Conjunctions (= AND)
   */
  private Set<String> stringRemainingConjunctions = new HashSet<>();
  /**
   * String Representation (only after buildStringRepresentation!) of Requires
   */
  private Set<String> stringRequires = new HashSet<>();
  /**
   * String Representation (only after buildStringRepresentation!) of Excludes
   */
  private Set<String> stringExcludes = new HashSet<>();


  public FDConstructionStorage(@NotNull FormulaManager fmgr,
                               @NotNull BooleanFormulaManager bmgr) {
    Preconditions.checkNotNull(fmgr);
    Preconditions.checkNotNull(bmgr);
    this.fmgr = fmgr;
    this.bmgr = bmgr;
    cnf2string = new Disjunction2String(fmgr, bmgr);
    this.formulaAnalyzer = new SMTFormulaAnalyzer(fmgr, bmgr);
  }

  public FDConstructionStorage(FormulaManager fmgr) {
    this(fmgr, fmgr.getBooleanFormulaManager());
  }

  /**
   * Merges all relations of given FDConstructionStorage into the currently
   * already existent relations b
   * adding the relations into the correct variables.
   *
   * @param newStorage The new storage where we want to extract our values
   *                   from and insert them
   *                   into this storage instance.
   */
  public void mergeWithStorage(FDConstructionStorage<T> newStorage) {
    if (Objects.nonNull(newStorage.getOptionals()))
      this.optionals.addRelation(newStorage.getOptionals());

    if (Objects.nonNull(newStorage.getSimpleOr()))
      this.simpleOr.addRelation(newStorage.getSimpleOr());

    if (Objects.nonNull(newStorage.getXor()))
      this.xor.addRelation(newStorage.getXor());

    if (Objects.nonNull(newStorage.getRemainingConjunctions()))
      this.remainingConjunctions.addRelation(newStorage.getRemainingConjunctions());

    if (Objects.nonNull(newStorage.getRequires()))
      this.requires.addRelation(newStorage.getRequires());

    if (Objects.nonNull(newStorage.getExcludes()))
      this.excludes.addRelation(newStorage.getExcludes());
  }

  /**
   * Extracts relevant Data from the passed visitor and saves it in the
   * correct fields.
   *
   * @param visitor Visitor to extract the data from
   */
  @SuppressWarnings("unchecked")
  public void extractDataFromVisitor(@NotNull SMT2FDVisitor visitor) {
    // Since T extends "Formula" and "BooleanFormula" extends "Formula", we
    // can perform the typecast here!
    Preconditions.checkNotNull(visitor);
    this.setOptionals((FDRelation<T>) visitor.getOptionals());
    this.setXor((FDRelation<T>) visitor.getXors());
    this.setSimpleOr((FDRelation<T>) visitor.getSimpleOrs());
    this.setRemainingConjunctions((FDRelation<T>) visitor.getAllRemainingConjunctions());
    this.setExcludes((FDRelation<T>) visitor.getExcludes());
    this.setRequires((FDRelation<T>) visitor.getRequires());
  }

  /**
   * Converts the stored Boolean-Formula Representation into corresponding
   * String Representations
   * For this, we process each FD-Relation and concatenate the Entries &
   * Formulas inside it
   */
  public void buildStringRepresentation(T root) {
    removeUnusedRelations(root);

    stringRemainingConjunctions = convertAllFDRelation(remainingConjunctions,
      Separator.ASSIGNMENT_SEPARATOR, Separator.AND_SEPARATOR);
    stringOptionals = convertAllFDRelation(optionals,
      Separator.ASSIGNMENT_SEPARATOR,
      Separator.OPTIONALS_SEPARATOR, Separator.AND_SEPARATOR);
    stringOptionals = appendSeparator(stringOptionals,
      Separator.OPTIONALS_SEPARATOR);
    stringSimpleOr = convertAllFDRelation(simpleOr,
      Separator.ASSIGNMENT_SEPARATOR,
      Separator.SIMPLE_OR_SEPARATOR);
    stringXor = convertAllFDRelation(xor, Separator.ASSIGNMENT_SEPARATOR,
      Separator.XOR_SEPARATOR);
    stringRequires = convertAllFDRelation(requires,
      Separator.REQUIRES_SEPARATOR,
      Separator.DOUBLE_AND_SEPARATOR);
    stringExcludes = convertAllFDRelation(excludes,
      Separator.EXCLUDES_SEPARATOR,
      Separator.DOUBLE_AND_SEPARATOR);
  }

  /**
   * Remove all unused Relations (those who cannot be reached)
   * For this, we construct a graph and perform a BFS from the root node
   *
   * @param root Node where we want to start the Graph-BFS
   */
  private void removeUnusedRelations(T root) {
    Set<T> keysToDrop =
      new RelationsGraph<>(Arrays.asList(remainingConjunctions,
        optionals, simpleOr, xor, requires, excludes)).getUnusedNodes(root);
    removeUnusedValues(keysToDrop);
  }

  /**
   * Removes a list of keys from all relation stores we have
   *
   * @param keysToDrop Set of keys which should be dropped
   */
  private void removeUnusedValues(Set<T> keysToDrop) {
    Arrays.asList(remainingConjunctions, optionals, simpleOr, xor, requires,
      excludes).forEach(fdRelation -> fdRelation.dropKeys(keysToDrop));
  }

  // Get all distinct atoms on the left side of all relations
  protected Set<T> getAllLeftSideAtoms() {
    return getAllLeftSideAtoms(Arrays.asList(remainingConjunctions, optionals
      , simpleOr, xor, requires, excludes));
  }

  // Get all distinct atoms on the left side of all (passed) relations
  private Set<T> getAllLeftSideAtoms(List<FDRelation<T>> relations) {
    Set<T> distinctValues = new HashSet<>();
    relations.forEach(r -> distinctValues.addAll(r.getRelationsHashMap().keySet()));
    return getAllVariables(distinctValues);
  }


  // Get all distinct atoms on the right side of all relations
  protected Set<T> getAllRightSideAtoms() {
    return getAllRightSideAtoms(Arrays.asList(remainingConjunctions,
      optionals, simpleOr, xor, requires, excludes));
  }

  // Get all distinct atoms on the right side of all (passed) relations
  private Set<T> getAllRightSideAtoms(List<FDRelation<T>> relations) {
    Set<T> distinctValues = new HashSet<>();
    relations.forEach(r -> r.getRelationsHashMap().values().forEach(distinctValues::addAll));
    return getAllVariables(distinctValues);
  }

  @SuppressWarnings("unchecked")
  private Set<T> getAllVariables(Set<T> relations) {
    Set<T> variables = new HashSet<>();

    // Explicit Typecast casts from "Formula" to "<T extends Formula>" => no
    // problem!
    relations.forEach(r -> variables.addAll(fmgr.extractVariables(r).values().stream().map(t -> (T) t).collect(Collectors.toSet())));
    return variables;
  }

  /**
   * Appends the given Separator to each String-Element of the passed Set
   * (useful for the cases where a
   * string representation must end with a given separator, like an optional
   * formula which must end with "?").
   *
   * @param strings    Set of Strings to append the Selector to
   * @param separators Selector(s) to Append
   * @return Set of Strings with Selectors appended
   */
  private Set<String> appendSeparator(@NotNull Set<String> strings,
                                      @NotNull Separator... separators) {
    Preconditions.checkNotNull(strings);
    Preconditions.checkNotNull(separators);
    Set<String> modifiedSet =
      strings.stream().map(a -> a + getJointSeparatorString(separators)).collect(Collectors.toSet());
    return sortSetByAssignmentsWithValueAtEnd(modifiedSet,
      ASSIGNMENT_SEPARATOR.getSeparator());
  }

  /**
   * Converts the given FDRelation into a Set of String-Representations. Each
   * Key of the Hash-Map Relation
   * gets converted into its own Set-Entry (since different keys mean
   * different "starting" points of the relations).
   * For same keys we combine all entries.
   *
   * @param relations           Relation to process and convert
   * @param assignmentSeparator Separator which should be written between the
   *                            left and right side of the Relation
   * @param separators          Separator which should be written between
   *                            each Atom of a Formula (and also between
   *                            multiple Formulas in case of more than one
   *                            formula for the same key)
   * @return Sorted Set of String-Representations for the given relation
   */
  private Set<String> convertAllFDRelation(@NotNull FDRelation<T> relations,
                                           @NotNull Separator assignmentSeparator,
                                           @NotNull Separator... separators) {
    Preconditions.checkNotNull(relations);
    Preconditions.checkNotNull(assignmentSeparator);
    Preconditions.checkNotNull(separators);
    Set<String> finalJoin = new HashSet<>();

    // Iterate over all keys (= one "AND" Formula in the final result)
    for (Map.Entry<T, Set<T>> entry :
      relations.getRelationsHashMap().entrySet()) {
      T k = entry.getKey();
      Set<T> formulas = relations.getRelationByKey(k);

      if (formulas.size() == 0)
        continue;
      boolean atomicOnly = true;
      for (T f : formulas) {
        formulaAnalyzer.analyze((BooleanFormula) f);
        if (!formulaAnalyzer.isAtomic())
          atomicOnly = false;
      }

      // Join each Formula individually
      Set<String> sortedFormulas = sortSetByAssignmentsWithValueAtEnd(
        cnf2string.convertAllToStrings(formulas),
        assignmentSeparator.getSeparator());

      // If we have only atomic formulas => simply join them
      if (atomicOnly) {
        String res =
          Joiner.on(getJointSeparatorString(separators)).join(sortedFormulas);

        // Make sure that we don't add any empty formulas / strings
        if (res.trim().isEmpty() || sortedFormulas.size() == 0)
          continue;
        finalJoin.add(prependKey(res, k, assignmentSeparator));
      } else {
        // Otherwise, we add them one by one (so that it doesn't get too
        // complicated)
        // Make sure that we don't add any empty formulas / strings
        sortedFormulas.forEach(formula -> {
          if (!formula.trim().isEmpty())
            finalJoin.add(prependKey(formula, k, assignmentSeparator));
        });
      }
    }

    return sortSetByAssignmentsWithValueAtEnd(finalJoin,
      assignmentSeparator.getSeparator());
  }

  /**
   * Sorts the given Set of Strings alphanumerically with the exception, that
   * all values containing "epsilon"
   * will be sorted to the end for easier readability. (and also filters out
   * empty values)
   *
   * @param set                 Set to Sort
   * @param assignmentSeparator Strings get split on the separator and only
   *                            the first part will be
   *                            inspected and compared (if we want to compare
   *                            all, just pass "")
   * @return Sorted Set
   */
  public Set<String> sortSetByAssignmentsWithValueAtEnd(@NotNull Set<String> set,
                                                        @NotNull String assignmentSeparator) {
    Preconditions.checkNotNull(set);
    Preconditions.checkNotNull(assignmentSeparator);
    return set.stream().sorted(Comparator.comparing(String::valueOf,
        sortAssignmentWithValueAtEnd(FDRelation.EPSILON_RELATION,
          assignmentSeparator)))
      .filter(item -> !item.trim().isEmpty())
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * Custom Comparator for Comparing a Collection and Sort those containing
   * (not equal to, only containing!)
   * a certain value at the end.
   * For Example, [LAMBDA2, z, LAMBDA1, a] -> sortWithValueAtEnd(LAMBDA) ->
   * [a, z, LAMBDA1, LAMBDA2]
   *
   * @param atEnd               Substring - if a String contains this
   *                            Substring, we sort it ad the end
   * @param assignmentSeparator Strings get split on the separator and only
   *                            the first part will be
   *                            inspected and compared (if we want to compare
   *                            all, just pass "")
   * @return Sorted Collection
   */
  private Comparator<String> sortAssignmentWithValueAtEnd(@NotNull String atEnd,
                                                          @NotNull String assignmentSeparator) {
    Preconditions.checkNotNull(atEnd);
    Preconditions.checkNotNull(assignmentSeparator);
    return sortAssignmentWithValueAtEnd(atEnd, Comparator.naturalOrder(),
      assignmentSeparator);
  }

  /**
   * Compares two Strings alphanumerically (and those who contain the String
   * "atEnd" will get sorted
   * to the end of the collection)
   *
   * @param atEnd               String (if Strings contain this sequence,
   *                            they will be sorted to the end)
   * @param c                   Comparator
   * @param assignmentSeparator Strings get split on the separator and only
   *                            the first part will be
   *                            inspected and compared (if we want to compare
   *                            all, just pass "")
   * @return A negative integer, zero, or a positive integer as the first
   * argument
   * is less than, equal to, or greater than the second.
   */
  private Comparator<String> sortAssignmentWithValueAtEnd(@NotNull String atEnd,
                                                          @NotNull Comparator<String> c,
                                                          @NotNull String assignmentSeparator) {
    Preconditions.checkNotNull(atEnd);
    Preconditions.checkNotNull(c);
    Preconditions.checkNotNull(assignmentSeparator);
    return (a, b) -> {
      if (a.split(assignmentSeparator)[0].contains(atEnd))
        return 1;
      if (b.split(assignmentSeparator)[0].contains(atEnd))
        return -1;
      return c.compare(a, b);
    };
  }

  /**
   * Prepends the key (= left side of relation) to the given string and
   * separates them by the given separator
   *
   * @param string              String to which we want to prepend the key (=
   *                            right side of relation)
   * @param key                 Key to prepend
   * @param assignmentSeparator Separator between the key and the string (i.e
   *                            ., "->")
   * @return Combined String
   */
  private String prependKey(@NotNull String string, @NotNull T key,
                            @NotNull Separator assignmentSeparator) {
    Preconditions.checkNotNull(string);
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(assignmentSeparator);
    if (key instanceof BooleanFormula) {
      return Joiner.on("").join(cnf2string.convertFormulaToString((BooleanFormula) key)) + assignmentSeparator.getSeparator() + string;
    }

    // Throw Not-Supported Error
    Log.error(FDConstructionError.UNSUPPORTED_KEY_TYPE.format(key.getClass().getSimpleName()));
    return string;
  }

  /**
   * Joins the passed Separators into one (and extracts the String Value out
   * of the ENUMs)
   *
   * @param separators List of Separators
   * @return One Single separator, which has all the input operators appended
   */
  private String getJointSeparatorString(@NotNull Separator[] separators) {
    Preconditions.checkNotNull(separators);
    List<String> separatorList =
      Stream.of(separators).map(Separator::getSeparator).collect(Collectors.toList());
    return Joiner.on("").join(separatorList);
  }

  /* ############################ */
  /* #    GETTERS & SETTERS     # */
  /* ############################ */

  public FDRelation<T> getSimpleOr() {
    return simpleOr;
  }

  public void setSimpleOr(@NotNull FDRelation<T> simpleOr) {
    Preconditions.checkNotNull(simpleOr);
    this.simpleOr = simpleOr;
  }

  public FDRelation<T> getXor() {
    return xor;
  }

  public void setXor(@NotNull FDRelation<T> xor) {
    Preconditions.checkNotNull(xor);
    this.xor = xor;
  }

  public FDRelation<T> getOptionals() {
    return optionals;
  }

  public void setOptionals(@NotNull FDRelation<T> optionals) {
    Preconditions.checkNotNull(optionals);
    this.optionals = optionals;
  }

  public void addToOptionals(@NotNull FDRelation<T> newOptionals) {
    Preconditions.checkNotNull(newOptionals);
    this.optionals.addRelation(newOptionals);
  }

  public void addToRemainingConjunctions(@NotNull FDRelation<T> newConjunctions) {
    Preconditions.checkNotNull(newConjunctions);
    this.remainingConjunctions.addRelation(newConjunctions);
  }

  public FDRelation<T> getRemainingConjunctions() {
    return remainingConjunctions;
  }

  public void setRemainingConjunctions(@NotNull FDRelation<T> remainingConjunctions) {
    Preconditions.checkNotNull(remainingConjunctions);
    this.remainingConjunctions = remainingConjunctions;
  }

  public FDRelation<T> getRequires() {
    return requires;
  }

  public void setRequires(@NotNull FDRelation<T> requires) {
    Preconditions.checkNotNull(requires);
    this.requires = requires;
  }

  public FDRelation<T> getExcludes() {
    return excludes;
  }

  public void setExcludes(@NotNull FDRelation<T> excludes) {
    Preconditions.checkNotNull(excludes);
    this.excludes = excludes;
  }

  public Set<String> getStringOptionals() {
    return stringOptionals;
  }

  public Set<String> getStringSimpleOr() {
    return stringSimpleOr;
  }

  public Set<String> getStringXor() {
    return stringXor;
  }

  public Set<String> getStringRemainingConjunctions() {
    return stringRemainingConjunctions;
  }

  public Set<String> getStringRequires() {
    return stringRequires;
  }

  public Set<String> getStringExcludes() {
    return stringExcludes;
  }
}
