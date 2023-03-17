/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

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

import java.util.HashSet;
import java.util.Set;

public class SMTFormulaAnalyzerTest {
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;
  SMTFormulaAnalyzer analyzer;

  BooleanFormula a, b, c;
  BooleanFormula orFormula, andFormula;
  BooleanFormula cnfFormula, noCnfFormula;
  BooleanFormula nnfFormula, noNnfFormula;
  BooleanFormula negatedAtomsOnlyFormula, optionalFormula;


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
    this.analyzer = new SMTFormulaAnalyzer(this.fmgr, this.bmgr);

    this.a = bmgr.makeVariable("a");
    this.b = bmgr.makeVariable("b");
    this.c = bmgr.makeVariable("c");
    this.orFormula = bmgr.or(a, b, c);
    this.andFormula = bmgr.and(a, b, c);
    this.cnfFormula = bmgr.and(bmgr.or(a, b), bmgr.or(b, c));
    this.nnfFormula = bmgr.or(bmgr.and(bmgr.not(a), b), bmgr.not(c));
    this.noCnfFormula = bmgr.or(bmgr.and(a, b), c);
    this.noNnfFormula = bmgr.not(bmgr.or(a, b));
    this.negatedAtomsOnlyFormula = bmgr.or(bmgr.not(a), bmgr.not(b));
    this.optionalFormula = bmgr.or(bmgr.not(a), a);
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#analyze(BooleanFormula)}
   */
  @Test
  public void analyze() {
    // When
    analyzer.analyze(orFormula);

        /*
        Then
        (Just check some features here, so that we can be sure that analyze
        works. The features itself
        will be tested in their own test cases)
         */
    Assertions.assertNotEquals(0, analyzer.getPositiveAtoms().size());
    Assertions.assertTrue(analyzer.hasDisjunctionsOnly());
    Assertions.assertFalse(analyzer.hasConjunctions());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#isAtomic()}
   */
  @Test
  public void isAtomic() {
    // When && Then (positive test)
    analyzer.analyze(a);
    Assertions.assertTrue(analyzer.isAtomic());

    // When && Then (negative test)
    analyzer.analyze(orFormula);
    Assertions.assertFalse(analyzer.isAtomic());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#hasDisjunctionsOnly()} ()}
   */
  @Test
  public void hasDisjunctionsOnly() {
    // When && Then (positive test)
    analyzer.analyze(orFormula);
    Assertions.assertTrue(analyzer.hasDisjunctionsOnly());

    // When && Then (negative test)
    analyzer.analyze(andFormula);
    Assertions.assertFalse(analyzer.hasDisjunctionsOnly());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#hasConjunctionsOnly()} ()}
   */
  @Test
  public void hasConjunctionsOnly() {
    // When && Then (positive test)
    analyzer.analyze(andFormula);
    Assertions.assertTrue(analyzer.hasConjunctionsOnly());

    // When && Then (negative test)
    analyzer.analyze(orFormula);
    Assertions.assertFalse(analyzer.hasConjunctionsOnly());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#hasConjunctions()}
   */
  @Test
  public void hasConjunctions() {
    // When && Then (positive test)
    analyzer.analyze(andFormula);
    Assertions.assertTrue(analyzer.hasConjunctions());

    // When && Then (negative test)
    analyzer.analyze(orFormula);
    Assertions.assertFalse(analyzer.hasConjunctions());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#hasDisjunctions()}
   */
  @Test
  public void hasDisjunctions() {
    // When && Then (positive test)
    analyzer.analyze(orFormula);
    Assertions.assertTrue(analyzer.hasDisjunctions());

    // When && Then (negative test)
    analyzer.analyze(andFormula);
    Assertions.assertFalse(analyzer.hasDisjunctions());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#isNNF()}
   */
  @Test
  public void isNNF() {
    // When && Then (positive test)
    analyzer.analyze(nnfFormula);
    Assertions.assertTrue(analyzer.isNNF());

    // When && Then (negative test)
    analyzer.analyze(noNnfFormula);
    Assertions.assertFalse(analyzer.isNNF());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#isCNF()}
   */
  @Test
  public void isCNF() {
    // When && Then (positive test)
    analyzer.analyze(cnfFormula);
    Assertions.assertTrue(analyzer.isCNF());

    // When && Then (negative test)
    analyzer.analyze(noCnfFormula);
    Assertions.assertFalse(analyzer.isCNF());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#numberOfNegatedAtoms()}
   */
  @Test
  public void numberOfNegatedAtoms() {
    // When && Then (positive test)
    analyzer.analyze(nnfFormula);
    Assertions.assertEquals(2, analyzer.numberOfNegatedAtoms());

    // When && Then (negative test)
    analyzer.analyze(cnfFormula);
    Assertions.assertEquals(0, analyzer.numberOfNegatedAtoms());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#numberOfPositiveAtoms()} ()}
   */
  @Test
  public void numberOfPositiveAtoms() {
    // When && Then (positive test)
    analyzer.analyze(cnfFormula);
    Assertions.assertEquals(3, analyzer.numberOfPositiveAtoms());

    // When && Then (negative test)
    analyzer.analyze(nnfFormula);
    Assertions.assertEquals(1, analyzer.numberOfPositiveAtoms());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#numberOfNegatedFormulas()} ()}
   */
  @Test
  public void numberOfNegatedFormulas() {
    // When && Then (positive test)
    analyzer.analyze(noNnfFormula);
    Assertions.assertEquals(1, analyzer.numberOfNegatedFormulas());

    // When && Then (negative test)
    analyzer.analyze(cnfFormula);
    Assertions.assertEquals(0, analyzer.numberOfNegatedFormulas());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#numberOfPositiveFormulas()}
   */
  @Test
  public void numberOfPositiveFormulas() {
    // When && Then (positive test)
    analyzer.analyze(cnfFormula);
    Assertions.assertEquals(2, analyzer.numberOfPositiveFormulas());

    // When && Then (negative test)
    analyzer.analyze(noNnfFormula);
    Assertions.assertEquals(0, analyzer.numberOfPositiveFormulas());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#getAllAtoms()} ()}
   */
  @Test
  public void getAllAtoms() {
    // When && Then (positive test)
    analyzer.analyze(cnfFormula);
    Assertions.assertEquals(Set.of(a, b, c), analyzer.getAllAtoms());

    // When && Then (positive test)
    analyzer.analyze(andFormula);
    Assertions.assertEquals(Set.of(a, b, c), analyzer.getAllAtoms());

    // When && Then (negative test)
    analyzer.analyze(noNnfFormula);
    Assertions.assertNotEquals(Set.of(a, b, c), analyzer.getAllAtoms());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#getNegatedAtoms()}
   */
  @Test
  public void getNegatedAtoms() {
    // When && Then (positive test)
    analyzer.analyze(nnfFormula);
    Assertions.assertEquals(Set.of(a, c), analyzer.getNegatedAtoms());

    // When && Then (negative test)
    analyzer.analyze(noNnfFormula);
    Assertions.assertEquals(new HashSet<>(), analyzer.getNegatedAtoms());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#getPositiveAtoms()}
   */
  @Test
  public void getPositiveAtoms() {
    // When && Then (positive test)
    analyzer.analyze(cnfFormula);
    Assertions.assertEquals(Set.of(a, b, c), analyzer.getPositiveAtoms());

    // When && Then (negative test)
    analyzer.analyze(nnfFormula);
    Assertions.assertEquals(Set.of(b), analyzer.getPositiveAtoms());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#isFormulaOptionalAtom()}
   */
  @Test
  public void isFormulaOptionalAtom() {
    // When && Then (positive test)
    analyzer.analyze(optionalFormula);
    Assertions.assertTrue(analyzer.isFormulaOptionalAtom().isPresent());
    Assertions.assertEquals(a, analyzer.isFormulaOptionalAtom().get());

    // When && Then (negative test)
    analyzer.analyze(nnfFormula);
    Assertions.assertTrue(analyzer.isFormulaOptionalAtom().isEmpty());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#hasPositiveAtomsOnly()}
   */
  @Test
  public void hasPositiveAtomsOnly() {
    // When && Then (positive test)
    analyzer.analyze(cnfFormula);
    Assertions.assertTrue(analyzer.hasPositiveAtomsOnly());

    // When && Then (negative test)
    analyzer.analyze(nnfFormula);
    Assertions.assertFalse(analyzer.hasPositiveAtomsOnly());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#hasNegatedAtomsOnly()}
   */
  @Test
  public void hasNegatedAtomsOnly() {
    // When && Then (positive test)
    analyzer.analyze(negatedAtomsOnlyFormula);
    Assertions.assertTrue(analyzer.hasNegatedAtomsOnly());

    // When && Then (negative test)
    analyzer.analyze(nnfFormula);
    Assertions.assertFalse(analyzer.hasNegatedAtomsOnly());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#getAllFormulas()}
   */
  @Test
  public void getAllFormulas() {
    // When && Then (positive test)
    analyzer.analyze(cnfFormula);
    Assertions.assertEquals(Set.of(bmgr.or(b, c), bmgr.or(a, b)),
            analyzer.getAllFormulas());

    // When && Then (positive test)
    analyzer.analyze(nnfFormula);
    Assertions.assertEquals(Set.of(bmgr.and(bmgr.not(a), b)),
            analyzer.getAllFormulas());

    // When && Then (negative test)
    analyzer.analyze(orFormula);
    Assertions.assertNotEquals(Set.of(a, b), analyzer.getAllFormulas());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#getAllNegatedFormulas()}
   */
  @Test
  public void getAllNegatedFormulas() {
    // When && Then (positive test)
    analyzer.analyze(noNnfFormula);
    Assertions.assertEquals(Set.of(bmgr.or(a, b)),
            analyzer.getAllNegatedFormulas());

    // When && Then (negative test)
    analyzer.analyze(orFormula);
    Assertions.assertEquals(new HashSet<>(), analyzer.getAllNegatedFormulas());
  }

  /**
   * Method under test {@link SMTFormulaAnalyzer#getAllPositiveFormulas()}
   */
  @Test
  public void getAllPositiveFormulas() {
    // When && Then (positive test)
    analyzer.analyze(orFormula);
    Assertions.assertEquals(Set.of(bmgr.or(a, b, c)),
            analyzer.getAllPositiveFormulas());

    // When && Then (negative test)
    analyzer.analyze(noNnfFormula);
    Assertions.assertEquals(new HashSet<>(), analyzer.getAllPositiveFormulas());
  }
}
