/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import arcbasis.AbstractTest;
import montiarc.util.Error;
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
import org.sosy_lab.java_smt.api.SolverContext;

import java.util.Set;

// extends AbstractTest
class Disjunction2StringTest extends AbstractTest {
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;

  Disjunction2String d2s;

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
    this.d2s = new Disjunction2String(this.fmgr);

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
  void convertAllToString() {
    // Given
    Set<BooleanFormula> formulasToPass = Set.of(a, b, c, orFormula);
    Set<BooleanFormula> formulasToFail = Set.of(andFormula, noCNFFormula); //
    // we shouldn't be allowed to convert Conjunctions

    // Note, that the method converts all formulas to string and then splits
    // them up on the given separator
    Set<String> realOrFormulas = Set.of(
        this.a.toString(),
        this.b.toString(),
        this.c.toString(),
        (this.b.toString() + Separator.SIMPLE_OR_SEPARATOR.getSeparator() + this.c.toString())
    );

    // Ensure that the formula must be in CNF & without conjunctions
    this.d2s.convertAllToStrings(formulasToFail);
    this.checkExpectedErrorsPresent(new Error[]{FDConstructionError.NO_CONJUNCTIONS_ALLOWED});

    // Also ensure, that we're only allowed to convert disjunctions and not
    // conjunctions!
    // When
    Set<String> constructedOrFormulas =
        this.d2s.convertAllToStrings(formulasToPass);

    // Then
    Assertions.assertEquals(realOrFormulas, constructedOrFormulas);
  }


  /**
   * Method under test
   * {@link Disjunction2String#convertFormulaToString(BooleanFormula)}
   */
  @Test
  void convertFormulaToString() {
    // When
    Set<String> constructedStrings = this.d2s.convertFormulaToString(orFormula);

    // Ensure that Disjunction2String doesn't work with ANDs (conjunctions)
    this.d2s.convertFormulaToString(andFormula);
    this.checkExpectedErrorsPresent(new Error[]{FDConstructionError.NO_CONJUNCTIONS_ALLOWED});

    // Then
    Set<String> realStrings =
        Set.of((this.b.toString() + Separator.SIMPLE_OR_SEPARATOR.getSeparator() + this.c.toString()));
    Assertions.assertEquals(realStrings, constructedStrings);
  }


  /**
   * Ensure that all relevant methods throw exceptions if they are called
   * with null-values
   */
  @Test
  public void nullParametersShouldThrowException() {
    // When && Then
    Assertions.assertThrows(NullPointerException.class,
        () -> new Disjunction2String(null));
    Assertions.assertThrows(NullPointerException.class,
        () -> d2s.convertAllToStrings(null));
    Assertions.assertThrows(NullPointerException.class,
        () -> d2s.convertFormulaToString(null));
    Assertions.assertThrows(NullPointerException.class, () -> d2s.visit(null));
  }


  /**
   * Method under test {@link Disjunction2String#visit(BooleanFormula)}
   */
  @Test
  void visitValidFormula() {
    // When && Then
    Assertions.assertDoesNotThrow(() -> this.d2s.visit(orFormula));

    this.d2s.visit(andFormula);
    this.checkExpectedErrorsPresent(new Error[]{FDConstructionError.NO_CONJUNCTIONS_ALLOWED});
  }
}
