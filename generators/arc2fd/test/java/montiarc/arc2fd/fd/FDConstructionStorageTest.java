/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import montiarc.arc2fd.smt.FDRelation;
import montiarc.arc2fd.smt.SMT2FDVisitor;
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
import java.util.Set;

public class FDConstructionStorageTest {

  FormulaManager fmgr;

  BooleanFormulaManager bmgr;
  SMT2FDVisitor smt2FDVisitor;

  FDConstructionStorage<BooleanFormula> storage1;
  FDConstructionStorage<BooleanFormula> storage2;
  FDConstructionStorage<BooleanFormula> combinedStorage;

  BooleanFormula a, b, c, root;
  BooleanFormula a_and_b;

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
    this.smt2FDVisitor = new SMT2FDVisitor(fmgr, bmgr);

    this.storage1 = new FDConstructionStorage<>(fmgr, bmgr);
    this.storage2 = new FDConstructionStorage<>(fmgr, bmgr);
    this.combinedStorage = new FDConstructionStorage<>(fmgr, bmgr);

    this.root = bmgr.makeVariable("root");
    this.a = bmgr.makeVariable("a");
    this.b = bmgr.makeVariable("b");
    this.c = bmgr.makeVariable("c");
    this.a_and_b = bmgr.and(a, b);

    storage1.setSimpleOr(new FDRelation<>(root, root, Set.of(a, b)));
    storage2.setSimpleOr(new FDRelation<>(root, root, Set.of(c)));

    // Store the result which is expected if we combine storage1 and storage2
    combinedStorage.setSimpleOr(new FDRelation<>(root, root, Set.of(a, b, c)));

    // Process the Formula
    this.smt2FDVisitor.process(a_and_b, root);
  }

  /**
   * Method under test
   * {@link FDConstructionStorage#mergeWithStorage(FDConstructionStorage)}
   */
  @Test
  public void mergeWithStorage() {
    // Given
    FDConstructionStorage<BooleanFormula> copy = storage1;
    copy.mergeWithStorage(storage2);

    // When && Then
    Assertions.assertEquals(combinedStorage.getSimpleOr().getRelationsHashMap(),
        copy.getSimpleOr().getRelationsHashMap());
  }

  /**
   * Method under test
   * {@link FDConstructionStorage#extractDataFromVisitor(SMT2FDVisitor)}
   */
  @Test
  public void extractDataFromVisitor() {
    // Given
    FDConstructionStorage<BooleanFormula> copy = storage1;
    copy.extractDataFromVisitor(this.smt2FDVisitor);
    HashMap<BooleanFormula, Set<BooleanFormula>> conjunctionsHashMap =
        new HashMap<>();
    conjunctionsHashMap.put(root, Set.of(a, b));

//         When && Then
//         the only thing that should have something in it is the
//         conjunctions (since we add "a && b")
    Assertions.assertEquals(conjunctionsHashMap,
        copy.getRemainingConjunctions().getRelationsHashMap());
    Assertions.assertEquals(0,
        copy.getOptionals().getRelationsHashMap().size());
    Assertions.assertEquals(0, copy.getRequires().getRelationsHashMap().size());
    Assertions.assertEquals(0, copy.getExcludes().getRelationsHashMap().size());
    Assertions.assertEquals(0, copy.getSimpleOr().getRelationsHashMap().size());
    Assertions.assertEquals(0, copy.getXor().getRelationsHashMap().size());
  }
}
