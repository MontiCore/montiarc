/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import com.google.common.base.Joiner;
import montiarc.arc2fd.fd.Disjunction2String;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.SolverContext;

import java.util.List;
import java.util.Set;

import static montiarc.arc2fd.expressions.MA2SMTFormulaConverter.PREFIX;
import static montiarc.arc2fd.expressions.MA2SMTFormulaConverter.SEPARATOR;

public class NumericPrettyPrinterTest {
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;
  IntegerFormulaManager imgr;

  NumericPrettyPrinter npp;

  BooleanFormula a, b, c;
  BooleanFormula orFormula;
  BooleanFormula andFormula;
  BooleanFormula noCNFFormula;

  @BeforeEach
  public void setUp() throws InvalidConfigurationException {
    SolverContext context = SolverContextFactory.createSolverContext(
        org.sosy_lab.common.configuration.Configuration.defaultConfiguration(),
        BasicLogManager.create(org.sosy_lab.common.configuration.Configuration.defaultConfiguration()),
        ShutdownManager.create().getNotifier(),
        SolverContextFactory.Solvers.SMTINTERPOL
    );

    this.fmgr = context.getFormulaManager();
    this.bmgr = this.fmgr.getBooleanFormulaManager();
    this.imgr = this.fmgr.getIntegerFormulaManager();
    this.npp = new NumericPrettyPrinter(this.fmgr);

    this.a = bmgr.makeVariable("a");
    this.b = bmgr.makeVariable("b");
    this.c = bmgr.makeVariable("c");
    this.orFormula = bmgr.or(b, c);
    this.andFormula = bmgr.and(a, this.orFormula);
    this.noCNFFormula = bmgr.or(this.orFormula, this.andFormula);
  }

  /**
   * Method under test {@link Disjunction2String#convertAllToStrings(Set)}
   */
  @Test
  public void prettyPrint() {
    String expectedString;

    expectedString = "a";
    Assertions.assertEquals(expectedString, this.npp.prettyPrint(a));

    expectedString = "b";
    Assertions.assertEquals(expectedString, this.npp.prettyPrint(b));

    expectedString = "c";
    Assertions.assertEquals(expectedString, this.npp.prettyPrint(c));

    expectedString = Joiner.on(SEPARATOR).join(List.of("b", "OR", "c"));
    Assertions.assertEquals(expectedString, this.npp.prettyPrint(orFormula));

    expectedString = Joiner.on(SEPARATOR).join(List.of("a", "AND", "b", "OR",
        "c"));
    Assertions.assertEquals(expectedString, this.npp.prettyPrint(andFormula));

    expectedString = "4";
    Assertions.assertEquals(expectedString,
        this.npp.prettyPrint(imgr.makeNumber(4)));

    expectedString = PREFIX + "4";
    Assertions.assertEquals(expectedString,
        this.npp.prettyPrint(imgr.makeNumber(4), true));

    expectedString = Joiner.on(SEPARATOR).join(List.of("4", "GTE", "5"));
    Assertions.assertEquals(expectedString,
        this.npp.prettyPrint(imgr.greaterOrEquals(imgr.makeNumber(4),
            imgr.makeNumber(5))));

    expectedString = PREFIX + expectedString;
    Assertions.assertEquals(expectedString,
        this.npp.prettyPrint(imgr.greaterOrEquals(imgr.makeNumber(4),
            imgr.makeNumber(5)), true));

    expectedString = Joiner.on(SEPARATOR).join(List.of("4", "EQ", "abc"));
    Assertions.assertEquals(expectedString,
        this.npp.prettyPrint(imgr.equal(imgr.makeNumber(4),
            imgr.makeVariable("abc"))));

    // Pretty-Printing is only supported for up to 2 formulas at once (at the
    // moment, maybe this changes later)
    Assertions.assertThrows(Exception.class,
        () -> this.npp.prettyPrint(noCNFFormula));
  }
}
