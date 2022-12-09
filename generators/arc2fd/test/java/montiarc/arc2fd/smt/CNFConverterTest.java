/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

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

class CNFConverterTest extends AbstractTest {
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;
  CNFConverter cnfConverter;

  BooleanFormula a, b, c, d, e;
  BooleanFormula orFormula, andFormula, noCNFFormula, complexFormulaNoCNF;

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
    this.cnfConverter = new CNFConverter(fmgr, bmgr);

    this.a = bmgr.makeVariable("a");
    this.b = bmgr.makeVariable("b");
    this.c = bmgr.makeVariable("c");
    this.d = bmgr.makeVariable("c");
    this.e = bmgr.makeVariable("c");
    this.orFormula = bmgr.or(b, c);
    this.andFormula = bmgr.and(a, this.orFormula);
    this.noCNFFormula = bmgr.or(this.orFormula, this.andFormula);
    this.complexFormulaNoCNF =
            bmgr.not(bmgr.or(bmgr.or(bmgr.not(bmgr.and(this.orFormula,
                    this.andFormula))), bmgr.and(a, b)));
  }

  /**
   * Method under test {@link CNFConverter#convertToCNF(BooleanFormula)}
   */
  @Test
  void convertNullToCNF() {
    this.cnfConverter.convertToCNF(null);
    this.checkExpectedErrorsPresent(new Error[]{SMTProcessingError.CNF_CONVERSION_NO_FORMULA_FOUND});
  }

  /**
   * Method under test {@link CNFConverter#convertToCNF(BooleanFormula)}
   */
  @Test
  void convertSimpleFormulasToCNF() {
    BooleanFormula cnf;
    cnf = this.cnfConverter.convertToCNF(a);
    Assertions.assertEquals(a, cnf);

    cnf = this.cnfConverter.convertToCNF(orFormula);
    Assertions.assertEquals(orFormula, cnf);

    cnf = this.cnfConverter.convertToCNF(andFormula);
    Assertions.assertEquals(andFormula, cnf);

    cnf = this.cnfConverter.convertToCNF(noCNFFormula);
    BooleanFormula expectedCNF = bmgr.and(bmgr.or(b, c, a), bmgr.or(b, c));
    Assertions.assertEquals(expectedCNF, cnf);
  }

  /**
   * Method under test {@link CNFConverter#convertToCNF(BooleanFormula)}
   */
  @Test
  void convertComplexFormulaToCNF() {
    // Given
    BooleanFormula expectedCNF = bmgr.and(bmgr.or(b, c), a,
            bmgr.or(bmgr.not(a), bmgr.not(b)));

    // When
    BooleanFormula cnf = this.cnfConverter.convertToCNF(complexFormulaNoCNF);

    // Then
    Assertions.assertEquals(expectedCNF, cnf);
  }
}
