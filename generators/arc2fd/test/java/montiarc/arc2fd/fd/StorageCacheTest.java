/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import arcbasis.ArcBasisAbstractTest;
import montiarc.arc2fd.smt.FDRelation;
import montiarc.util.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.SolverContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StorageCacheTest extends ArcBasisAbstractTest {
  StorageCache<BooleanFormula> storageCache;
  FormulaManager fmgr;
  BooleanFormulaManager bmgr;

  BooleanFormula a, b, c, root;
  BooleanFormula orFormula;
  BooleanFormula andFormula;

  @BeforeEach
  public void setUp() throws InvalidConfigurationException {
    SolverContext context = SolverContextFactory.createSolverContext(
      Configuration.defaultConfiguration(),
      BasicLogManager.create(Configuration.defaultConfiguration()),
      ShutdownManager.create().getNotifier(),
      SolverContextFactory.Solvers.SMTINTERPOL
    );

    this.fmgr = context.getFormulaManager();
    this.bmgr = this.fmgr.getBooleanFormulaManager();
    this.storageCache =
      new StorageCache<>(new FDConstructionStorage<>(this.fmgr));

    this.a = bmgr.makeVariable("a");
    this.b = bmgr.makeVariable("b");
    this.c = bmgr.makeVariable("c");
    this.root = bmgr.makeVariable("root");
    this.orFormula = bmgr.or(b, c);
    this.andFormula = bmgr.and(a, this.orFormula);

    this.storageCache.getStorage().setRemainingConjunctions(new FDRelation<>(root));
  }

  /**
   * Method under test {@link StorageCache#getStorage()}
   */
  @Test
  public void emptyStorage() {
    // Given
    StorageCache<BooleanFormula> emptyStorageCache = new StorageCache<>();

    // When && Then
    Assertions.assertThrows(NullPointerException.class,
      () -> emptyStorageCache.getStorage().getRemainingConjunctions());
  }

  /**
   * Method under test {@link StorageCache#addRelation(Formula, Formula)}
   */
  @Test
  public void addRelation() {
    // Given
    Map<BooleanFormula, Set<BooleanFormula>> trueHashMap = new HashMap<>();
    trueHashMap.put(a, Set.of(b));
    trueHashMap.put(b, Set.of(c));

    // When
    this.storageCache.addRelation(a, b);
    this.storageCache.addRelation(b, c);

    // Then
    Assertions.assertEquals(trueHashMap,
      this.storageCache.getStorage().getRemainingConjunctions().getRelationsHashMap());
  }

  /**
   * Method under test
   * {@link StorageCache#mergeWithStorage(FDConstructionStorage)}
   */
  @Test
  public void mergeWithStorage() {
    // Given
    this.storageCache.addRelation(a, b);
    this.storageCache.addRelation(b, c);

    FDConstructionStorage<BooleanFormula> constructionStorage =
      new FDConstructionStorage<>(fmgr);
    constructionStorage.setRemainingConjunctions(new FDRelation<>(c));
    StorageCache<BooleanFormula> storageCacheCopy = this.storageCache;
    StorageCache<BooleanFormula> emptyStorage = new StorageCache<>();
    StorageCache<BooleanFormula> secondStorageCache =
      new StorageCache<>(constructionStorage);
    secondStorageCache.addRelation(c, b);

    Map<BooleanFormula, Set<BooleanFormula>> trueHashMap = new HashMap<>();
    trueHashMap.put(a, Set.of(b));
    trueHashMap.put(b, Set.of(c));
    trueHashMap.put(c, Set.of(b));

    // When && Then
    // Merge two empty storages
    emptyStorage.mergeWithStorage(emptyStorage);
    this.checkExpectedErrorsPresent(new Error[]{FDConstructionError.TRY_TO_MERGE_TWO_EMPTY_STORAGES});

    // When && Then
    // Merge non-empty storage with an empty one
    storageCacheCopy.mergeWithStorage(emptyStorage);
    Assertions.assertEquals(this.storageCache.getStorage(),
      storageCacheCopy.getStorage());

    // When && Then
    // Merge empty storage with a non-empty one
    emptyStorage.mergeWithStorage(storageCacheCopy);
    Assertions.assertEquals(storageCacheCopy.getStorage(),
      emptyStorage.getStorage());


    // When && Then
    // Merge two normal storages...
    storageCacheCopy.mergeWithStorage(secondStorageCache);
    Assertions.assertEquals(trueHashMap,
      storageCacheCopy.getStorage().getRemainingConjunctions().getRelationsHashMap());
  }
}
