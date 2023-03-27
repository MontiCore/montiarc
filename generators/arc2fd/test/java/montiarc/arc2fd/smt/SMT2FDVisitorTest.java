/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import arcbasis.ArcBasisAbstractTest;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SMT2FDVisitorTest extends ArcBasisAbstractTest {
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;

  SMT2FDVisitor smt2fdVisitor;

  // Store relevant formulas for comparison
  BooleanFormula a, b, c, d, e, f, o, root, complexFormula;


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
    this.smt2fdVisitor = new SMT2FDVisitor(this.fmgr, this.bmgr);

    // Initialize all variables
    this.a = bmgr.makeVariable("a");
    this.b = bmgr.makeVariable("b");
    this.c = bmgr.makeVariable("c");
    this.d = bmgr.makeVariable("d");
    this.e = bmgr.makeVariable("e");
    this.f = bmgr.makeVariable("f");
    this.o = bmgr.makeVariable("o");
    this.root = bmgr.makeVariable("root");

    // Create relevant cases for the "complex" formula, where all things
    // (like xor, optional, ...) appear in one formula
    BooleanFormula conjunction = bmgr.and(a, b);
    BooleanFormula disjunction = bmgr.or(b, c);
    BooleanFormula xor = bmgr.and(bmgr.or(c, d), bmgr.or(bmgr.not(c),
        bmgr.not(d)));
    BooleanFormula optional = bmgr.or(o, bmgr.not(o));
    BooleanFormula simpleOr = bmgr.or(e, f);
    BooleanFormula requires = bmgr.or(bmgr.not(a), f);
    BooleanFormula excludes = bmgr.or(bmgr.not(c), bmgr.not(e));
    this.complexFormula = bmgr.and(conjunction, disjunction, xor, optional,
        simpleOr, requires, excludes);
  }


  /**
   * Method under test
   * {@link SMT2FDVisitor#process(BooleanFormula, BooleanFormula)}
   */
  @Test
  public void process() {
    // Output error if formula is null
    smt2fdVisitor.process(null, null);
    this.checkExpectedErrorsPresent(new Error[]{SMTProcessingError.SMT2FD_PROCESSING_NO_FORMULA});
    this.cleanUpLog();

    // Error if the root is null
    smt2fdVisitor.process(a, null);
    this.checkExpectedErrorsPresent(new Error[]{SMTProcessingError.ROOT_IS_NULL});
    this.cleanUpLog();

    // Error if formula is not in CNF
    BooleanFormula noCNFFormula = bmgr.or(bmgr.or(a, c), bmgr.and(a,
        bmgr.or(b, c)));
    smt2fdVisitor.process(noCNFFormula, root);
    this.checkExpectedErrorsPresent(new Error[]{SMTProcessingError.SMT2FD_VISITOR_NO_CNF});
  }


  /**
   * Method under test {@link SMT2FDVisitor#getAllRemainingConjunctions()}
   */
  @Test
  public void testAllRemainingConjunctions() {
    // EASY CASE
    // Given
    BooleanFormula trivialCase = bmgr.and(a, b);
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapTrivialCase =
        new HashMap<>();
    expectedMapTrivialCase.put(root, Set.of(a, b));

    // When
    smt2fdVisitor.process(trivialCase, root);

    // Then
    Assertions.assertEquals(expectedMapTrivialCase,
        smt2fdVisitor.getAllRemainingConjunctions().getRelationsHashMap());


    // COMPLEX CASE
    // Given
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapComplexCase =
        new HashMap<>();
    expectedMapComplexCase.put(root, Set.of(a, b));

    // When
    smt2fdVisitor.process(complexFormula, root);

    // Then
    Assertions.assertEquals(expectedMapComplexCase,
        smt2fdVisitor.getAllRemainingConjunctions().getRelationsHashMap());
  }


  /**
   * Method under test {@link SMT2FDVisitor#getSimpleOrs()}
   */
  @Test
  public void testSimpleOrs() {
    // EASY CASE
    // Given
    BooleanFormula trivialCase = bmgr.or(a, b);
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapTrivialCase =
        new HashMap<>();
    expectedMapTrivialCase.put(root, Set.of(trivialCase));

    // When
    smt2fdVisitor.process(trivialCase, root);

    // Then
    Assertions.assertEquals(expectedMapTrivialCase,
        smt2fdVisitor.getSimpleOrs().getRelationsHashMap());


    // COMPLEX CASE
    // Given
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapComplexCase =
        new HashMap<>();
    expectedMapComplexCase.put(root, Set.of(bmgr.or(b, c), bmgr.or(e, f)));

    // When
    smt2fdVisitor.process(complexFormula, root);

    // Then
    Assertions.assertEquals(expectedMapComplexCase,
        smt2fdVisitor.getSimpleOrs().getRelationsHashMap());
  }


  /**
   * Method under test {@link SMT2FDVisitor#getXors()}
   */
  @Test
  public void testXors() {
    // EASY CASE
    // Given
    BooleanFormula trivialCase = bmgr.and(bmgr.or(a, b), bmgr.or(bmgr.not(a),
        bmgr.not(b)));
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapTrivialCase =
        new HashMap<>();
    expectedMapTrivialCase.put(root, Set.of(bmgr.xor(a, b)));

    // When
    smt2fdVisitor.process(trivialCase, root);

    // Then
    Assertions.assertEquals(expectedMapTrivialCase,
        smt2fdVisitor.getXors().getRelationsHashMap());


    // COMPLEX CASE
    // Given
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapComplexCase =
        new HashMap<>();
    expectedMapComplexCase.put(root, Set.of(bmgr.xor(c, d)));

    // When
    smt2fdVisitor.process(complexFormula, root);

    // Then
    Assertions.assertEquals(expectedMapComplexCase,
        smt2fdVisitor.getXors().getRelationsHashMap());
  }


  /**
   * Method under test {@link SMT2FDVisitor#getOptionals()}
   */
  @Test
  public void testOptionals() {
    // EASY CASE
    // Given
    BooleanFormula trivialCase = bmgr.or(bmgr.not(a), a);
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapTrivialCase =
        new HashMap<>();
    expectedMapTrivialCase.put(root, Set.of(a));

    // When
    smt2fdVisitor.process(trivialCase, root);

    // Then
    Assertions.assertEquals(expectedMapTrivialCase,
        smt2fdVisitor.getOptionals().getRelationsHashMap());


    // COMPLEX CASE
    // Given
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapComplexCase =
        new HashMap<>();
    expectedMapComplexCase.put(root, Set.of(a, f, c, o, e));

    // When
    smt2fdVisitor.process(complexFormula, root);

    // Then
    Assertions.assertEquals(expectedMapComplexCase,
        smt2fdVisitor.getOptionals().getRelationsHashMap());
  }


  /**
   * Method under test {@link SMT2FDVisitor#getRequires()}
   */
  @Test
  public void testRequires() {
    // EASY CASE
    // Given
    BooleanFormula trivialCase = bmgr.or(bmgr.not(a), b);
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapTrivialCase =
        new HashMap<>();
    expectedMapTrivialCase.put(a, Set.of(b));

    // When
    smt2fdVisitor.process(trivialCase, root);

    // Then
    Assertions.assertEquals(expectedMapTrivialCase,
        smt2fdVisitor.getRequires().getRelationsHashMap());


    // COMPLEX CASE
    // Given
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapComplexCase =
        new HashMap<>();
    expectedMapComplexCase.put(a, Set.of(f));

    // When
    smt2fdVisitor.process(complexFormula, root);

    // Then
    Assertions.assertEquals(expectedMapComplexCase,
        smt2fdVisitor.getRequires().getRelationsHashMap());
  }


  /**
   * Method under test {@link SMT2FDVisitor#getExcludes()}
   */
  @Test
  public void testExcludes() {
    // EASY CASE
    // Given
    BooleanFormula trivialCase = bmgr.or(bmgr.not(a), bmgr.not(b));
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapTrivialCase =
        new HashMap<>();
    expectedMapTrivialCase.put(a, Set.of(b));

    // When
    smt2fdVisitor.process(trivialCase, root);

    // Then
    Assertions.assertEquals(expectedMapTrivialCase,
        smt2fdVisitor.getExcludes().getRelationsHashMap());


    // COMPLEX CASE
    // Given
    Map<BooleanFormula, Set<BooleanFormula>> expectedMapComplexCase =
        new HashMap<>();
    expectedMapComplexCase.put(c, Set.of(e));

    // When
    smt2fdVisitor.process(complexFormula, root);

    // Then
    Assertions.assertEquals(expectedMapComplexCase,
        smt2fdVisitor.getExcludes().getRelationsHashMap());
  }
}
