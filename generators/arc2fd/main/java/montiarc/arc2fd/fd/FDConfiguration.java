/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.arc2fd.expressions.BooleanOperation;
import montiarc.arc2fd.expressions.NumericOperation;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static montiarc.arc2fd.expressions.MA2SMTFormulaConverter.SEPARATOR;
import static montiarc.arc2fd.fd.Separator.*;

/**
 * Enum containing all possible Operation Types
 */
enum Operation {
  OPTIONALS,
  SIMPLE_OR,
  XOR,
  REMAINING_CONJUNCTIONS,
  REQUIRES,
  EXCLUDES
}

/**
 * Enum containing the Separator for each Operation
 */
enum Separator {
  OPTIONALS_SEPARATOR("?"),
  SIMPLE_OR_SEPARATOR(" | "),
  XOR_SEPARATOR(" ^ "),
  AND_SEPARATOR(" & "),
  DOUBLE_AND_SEPARATOR(" && "),
  REQUIRES_SEPARATOR(" requires "),
  EXCLUDES_SEPARATOR(" excludes "),
  ASSIGNMENT_SEPARATOR(" -> ");

  private final String separator;

  Separator(String separator) {
    this.separator = separator;
  }

  public String getSeparator() {
    return separator;
  }
}

/**
 * Stores the Configuration for Converting to Feature Diagram
 */
public class FDConfiguration {

  /**
   * Mapping between Operation Type and a nice, meaningful name (for comments)
   */
  public static final Map<Operation, String> MAP_TYPE_MAPPING =
      Map.ofEntries(
          entry(Operation.OPTIONALS, "Optionals"),
          entry(Operation.SIMPLE_OR, "Simple OR"),
          entry(Operation.XOR, "XOR"),
          entry(Operation.REMAINING_CONJUNCTIONS,
              "AND (Remaining Conjunctions)"),
          entry(Operation.REQUIRES, "Requires"),
          entry(Operation.EXCLUDES, "Excludes")
      );
  /**
   * Should the Types be displayed after the generation?
   */
  public static final boolean DISPLAY_TYPES = true;
  /**
   * Sometimes it can happen that we must negate one literal (i.e., if the
   * only constraint is !a).
   * Since Feature Diagrams don't allow for something like "!a", we prepend a
   * "not_a".
   */
  public static final String NEGATED_ATOM_PREPEND = "not_";

  public static String GET_SPLITTING_REGEX() {
    // Construct a RegEx for detecting optional features (for more details
    // see OPTIONALS_SPLITTING_REGEX or addOptionals)
    Set<String> completeRegex =
        Stream.of(BooleanOperation.values()).map(Enum::toString).collect(Collectors.toSet());
    completeRegex.addAll(Stream.of(NumericOperation.values()).map(Enum::toString).collect(Collectors.toSet()));
    completeRegex.addAll(Stream.of(OPTIONALS_SEPARATOR, SIMPLE_OR_SEPARATOR,
        XOR_SEPARATOR, AND_SEPARATOR,
        REQUIRES_SEPARATOR, EXCLUDES_SEPARATOR, ASSIGNMENT_SEPARATOR).map(Enum::toString).collect(Collectors.toSet()));
    completeRegex.add(SEPARATOR);
    return Joiner.on("|").join(completeRegex.stream().map(t -> Pattern.quote(String.valueOf(t))).collect(Collectors.toSet()));
  }

  /**
   * Maps an Operation Type to its corresponding Name
   *
   * @param type Operation Type to Map
   * @return New Name
   */
  public String mapTypeToDisplayedName(@NotNull Operation type) {
    Preconditions.checkNotNull(type);
    if (MAP_TYPE_MAPPING.containsKey(type)) {
      return MAP_TYPE_MAPPING.get(type);
    }
    // No Mapping Found => Throw Error
    Log.error(FDConstructionError.TYPE_HAS_NO_NAME_MAPPING.format(type));
    return "";
  }

  // Getters for ENUM (Since we cannot really access them in Freemarker
  // otherwise)
  public Operation getOptionalsEnum() {
    return Operation.OPTIONALS;
  }

  public Operation getSimpleOrEnum() {
    return Operation.SIMPLE_OR;
  }

  public Operation getXOREnum() {
    return Operation.XOR;
  }

  public Operation getRemainingConjunctionsEnum() {
    return Operation.REMAINING_CONJUNCTIONS;
  }

  public Operation getRequiresEnum() {
    return Operation.REQUIRES;
  }

  public Operation getExcludesEnum() {
    return Operation.EXCLUDES;
  }

  /**
   * @return True if we want to print out / generate the Feature-Diagram
   * Operation Types (in Comments)
   */
  public boolean shouldDisplayTypes() {
    return DISPLAY_TYPES;
  }
}
