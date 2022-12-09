/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import com.google.common.base.Joiner;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.FunctionDeclaration;
import org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor;

import java.util.ArrayList;
import java.util.List;

import static montiarc.arc2fd.expressions.MA2SMTFormulaConverter.PREFIX;
import static montiarc.arc2fd.expressions.MA2SMTFormulaConverter.SEPARATOR;
import static montiarc.arc2fd.expressions.MAExpressionsBasisVisitor.NumberToWord.isNumeric;

/**
 * Pretty-Printer which helps to convert a Numeral Formula into a String
 * describing it
 */
public class NumericPrettyPrinter extends DefaultFormulaVisitor<Void> {
  private final FormulaManager fmgr;

  List<String> subStrings = new ArrayList<>();

  /**
   * Keep track of whether we're currently visiting the first formula or if
   * it is another one (since the
   * combined string may not start with certain characters, we have to keep
   * that in mind)
   */
  private boolean escapeLeadingDigit = true;

  public NumericPrettyPrinter(FormulaManager fmgr) {
    this.fmgr = fmgr;
  }

  public String prettyPrint(Formula f) {
    return prettyPrint(f, false);
  }

  public String prettyPrint(Formula f, boolean escapeLeadingDigit) {
    subStrings = new ArrayList<>();
    this.escapeLeadingDigit = escapeLeadingDigit;
    fmgr.visit(f, this);
    return Joiner.on(SEPARATOR).join(subStrings);
  }

  @Override
  protected Void visitDefault(Formula f) {
    // If this is the first formula we want to combine, we may not start with
    // number => prepend a "$"
    String prefix = (escapeLeadingDigit && isNumeric(f.toString())) ? PREFIX
            : "";
    subStrings.add(prefix + f.toString());
    return null;
  }

  @Override
  public Void visitFunction(Formula pF, List<Formula> pArgs,
                            FunctionDeclaration<?> pFunctionDeclaration) {
    if (pArgs.size() == 1) {
      subStrings.add(pFunctionDeclaration.getKind().toString());
      fmgr.visit(pArgs.get(0), this);
    } else if (pArgs.size() == 2) {
      fmgr.visit(pArgs.get(0), this);
      subStrings.add(pFunctionDeclaration.getKind().toString());
      escapeLeadingDigit = false;
      fmgr.visit(pArgs.get(1), this);
    } else {
      try {
        throw new Exception("Visit-Function for >= 3 formula arguments must " +
                "be implemented!");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }
}
