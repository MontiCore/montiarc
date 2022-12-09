/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Map;

/**
 * Visitor which is used as a Helper to convert MontiArc ExpressionBasis into
 * an SMT-Formula.
 * For this, we add the corresponding Variable-Name to the Variables in each
 * visit()
 * (Here we don't have to use endVisit() since we're already on the most
 * atomic level)
 */
public class MAExpressionsBasisVisitor implements ExpressionsBasisVisitor2,
                                                  ExpressionsBasisHandler {

  protected final MA2SMTFormulaConverter fc;

  /**
   * Stores a Map which maps ASTExpressions (as Strings) to different
   * ASTExpressions (as Strings).
   * The map then gets passed onto the corresponding processing steps and
   * will replace all occurrences
   * of (keys) by the corresponding (values).
   */
  private final Map<String, String> variableRemapping;

  protected ExpressionsBasisTraverser traverser;

  /**
   * @param fc                FormulaConverter which is responsible for
   *                          postprocessing the extracted formulas
   * @param variableRemapping Stores a Map which maps ASTExpressions (as
   *                          Strings) to different ASTExpressions (as Strings).
   *                          The map then gets passed onto the corresponding
   *                          processing steps and will replace all occurrences
   *                          of (keys) by the corresponding (values).
   */
  public MAExpressionsBasisVisitor(@NotNull MA2SMTFormulaConverter fc,
                                   @NotNull Map<String, String> variableRemapping) {
    Preconditions.checkNotNull(fc);
    Preconditions.checkNotNull(variableRemapping);
    this.fc = fc;
    this.variableRemapping = variableRemapping;
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public void visit(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    String value = variableRemapping.getOrDefault(node.getName(),
      node.getName());
    fc.addVariable(value);
  }

  /**
   * Helper-Class to convert a number into an English word representation
   */
  public static class NumberToWord {
    private static final String[] specialNames = {
      "",
      " thousand",
      " million",
      " billion",
      " trillion",
      " quadrillion",
      " quintillion"
    };

    private static final String[] tensNames = {
      "",
      " ten",
      " twenty",
      " thirty",
      " fourty",
      " fifty",
      " sixty",
      " seventy",
      " eighty",
      " ninety"
    };

    private static final String[] numNames = {
      "",
      " one",
      " two",
      " three",
      " four",
      " five",
      " six",
      " seven",
      " eight",
      " nine",
      " ten",
      " eleven",
      " twelve",
      " thirteen",
      " fourteen",
      " fifteen",
      " sixteen",
      " seventeen",
      " eighteen",
      " nineteen"
    };

    /**
     * Test if the given string is a numeric value or not
     *
     * @param string String to analyze
     * @return True if the string is numeric, else False
     */
    public static boolean isNumeric(String string) {
      if (string == null || string.equals(""))
        return false;

      try {
        Integer.parseInt(string);
        return true;
      } catch (
        NumberFormatException ignored) {
      }
      return false;
    }

    /**
     * Handles the conversion of all numbers that are less than 1000
     *
     * @param number Number to Convert
     * @return String-Representation of the input number
     */
    private static String convertLessThanOneThousand(int number) {
      String current;

      if (number % 100 < 20) {
        current = numNames[number % 100];
        number /= 100;
      } else {
        current = numNames[number % 10];
        number /= 10;

        current = tensNames[number % 10] + current;
        number /= 10;
      }
      if (number == 0)
        return current;
      return numNames[number] + " hundred" + current;
    }

    /**
     * Convert the given number into a string word representation and returns it
     *
     * @param number Number to convert
     * @return String-representation of the number
     */
    public static String convert(int number) {
      if (number == 0)
        return "zero";

      String prefix = "";
      if (number < 0) {
        number = -number;
        prefix = "negative";
      }

      StringBuilder current = new StringBuilder();
      int place = 0;

      // Iteratively process the parts that are "< 1000" (and just merge all
      // of them at the end)
      do
      {
        int n = number % 1000;
        if (n != 0) {
          String s = convertLessThanOneThousand(n);
          current.insert(0, s + specialNames[place]);
        }
        place++;
        number /= 1000;
      } while (number > 0);

      return (prefix + current).trim();
    }
  }
}
