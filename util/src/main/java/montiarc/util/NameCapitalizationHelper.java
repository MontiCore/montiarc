/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Arrays;

/**
 * Helper to check if a name is capitalized.
 */
public class NameCapitalizationHelper {

  private NameCapitalizationHelper() { }

  /**
   * @return true if the provided string does not start with an upper-case-letter
   * @throws IllegalArgumentException if the provided string is null or empty
   */
  public static boolean isNotUpperCase(@NotNull String string) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(string), "Name is empty");
    return !Character.isUpperCase(string.charAt(0));
  }

  /**
   * @return true if the provided string does not start with a lower-case-letter
   * @throws IllegalArgumentException if the provided string is null or empty
   */
  public static boolean isNotLowerCase(@NotNull String name) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name is empty");
    return !Character.isLowerCase(name.charAt(0));
  }

  /**
   * Logs an error that declares an invalid capitalized name
   *
   * @param error error to log, should use {@code %s} twice, once for the invalid name, once for the enclosing
   *              component
   * @param args  arguments for {@link String#format(String, Object...)}
   * @param node  the node that has the invalid name
   */
  public static void error(@NotNull Error error, @NotNull ASTNode node, @NotNull Object... args) {
    Preconditions.checkNotNull(error);
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(args);
    Preconditions.checkArgument(!Arrays.asList(args).contains(null));
    Log.error(String.format(error.toString(), args), node.get_SourcePositionStart());
  }

  /**
   * Logs a warning that declares an invalid capitalized name
   *
   * @param error warning to log, should use {@code %s} twice, once for the invalid name, once for the enclosing
   *              component
   * @param args  arguments for {@link String#format(String, Object...)}
   * @param node  the node that has the invalid name
   */
  public static void warning(@NotNull Error error, @NotNull ASTNode node, @NotNull Object... args) {
    Preconditions.checkNotNull(error);
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(args);
    Preconditions.checkArgument(!Arrays.asList(args).contains(null));
    Log.warn(String.format(error.toString(), args), node.get_SourcePositionStart());
  }
}