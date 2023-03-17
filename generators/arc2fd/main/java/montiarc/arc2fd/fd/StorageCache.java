/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.java_smt.api.Formula;

import java.util.Objects;

import static montiarc.arc2fd.fd.MAExtractionHelper.ALWAYS_TRUE_EXPRESSION;

/**
 * Helper Class which stores the current ConstructionStorage and an
 * ASTConstraint and can also merge multiple storages.
 *
 * @param <H> Type of the FDConstructionStorage
 */
public class StorageCache<H extends Formula> {

  /**
   * Total Expression we've combined so far
   */
  private ASTExpression totalExpression;

  /**
   * Total Construction Storage
   */

  private FDConstructionStorage<H> storage;

  StorageCache() {}

  /**
   * Creates a new StorageCache from the given ASTExpression and Storage
   *
   * @param storage Storage to add
   */
  StorageCache(FDConstructionStorage<H> storage) {
    this.storage = storage;
  }

  /**
   * Adds a new Relation from the Root (String)
   *
   * @param from Left Side of the Relation
   * @param to   Right Side of the Relation
   */
  protected void addRelation(H from, H to) {
    if (Objects.isNull(this.getStorage()))
      return;
    this.getStorage().getRemainingConjunctions().addRelation(from, to);
  }

  /**
   * Merges one StorageCache with another StorageCache (if both exist,
   * otherwise it just uses the one
   * which exists and saves it). The result is saved in the current storage.
   *
   * @param newStorage Storage which should be merged with the current one
   */
  protected void mergeWithStorage(StorageCache<H> newStorage) {
    mergeWithStorage(newStorage.getStorage());
  }

  /**
   * Merges one FDConstructionStorage with the current StorageCache's
   * construction storage.
   * If both exist, otherwise it just uses the one which exists and saves it.
   * The result is saved in the current storage.
   *
   * @param newStorage Storage which should be merged with the current one
   */
  protected void mergeWithStorage(FDConstructionStorage<H> newStorage) {
    if (Objects.isNull(this.getStorage()) && Objects.isNull(newStorage)) {
      Log.error(FDConstructionError.TRY_TO_MERGE_TWO_EMPTY_STORAGES.format());
    } else if (Objects.nonNull(this.getStorage()) && Objects.nonNull(newStorage)) {
      // We have two non-null storages => combine them
      this.storage.mergeWithStorage(newStorage);
    } else if (Objects.isNull(this.getStorage())) {
      // Current Storage is empty, just use the new one
      this.storage = newStorage;
    }
    // In all other cases (new storage is empty), leave it unchanged
  }


  /**
   * @return The total expression stored
   */
  protected ASTExpression getTotalExpression() {
    return (Objects.isNull(totalExpression)) ? ALWAYS_TRUE_EXPRESSION :
      totalExpression;
  }

  /**
   * Sets the TotalExpression to a new Expression
   *
   * @param totalExpression New Total Expression
   */
  protected void setTotalExpression(@NotNull ASTExpression totalExpression) {
    Preconditions.checkNotNull(totalExpression);
    this.totalExpression = totalExpression;
  }

  /**
   * @return The Construction Storage which contains all things we've
   * constructed so far
   */
  protected FDConstructionStorage<H> getStorage() {
    return storage;
  }
}
