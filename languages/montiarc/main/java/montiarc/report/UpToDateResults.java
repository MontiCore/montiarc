/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Data store for the results of the up-to-date check (see {@link UpToDateCalculator}).
 * Records which models are deleted, added, updated, or untouched.
 * For models that are not newly added, the deserialized IncCheckData is associated with them.
 */
public final class UpToDateResults {

  /**
   * All models which where deleted since the last run. They are represented by their qualified name and
   * are associated with their corresponding IncCheckData. The collection is unmodifiable.
   */
  public final Map<String, IncCheckData> deletedModels;

  /**
   * All models which where added since the last run or where IncCheckData is missing. They are represented by their
   * qualified name. The collection is unmodifiable.
   */
  public final Set<String> addedModels;

  /**
   * All models which where updated since the last run. Addition / removal of HWC classes and removal of generated files
   * are also considered as update, as a regeneration based on these models is necessary. Models are represented by
   * their qualified name and are associated with their corresponding IncCheckData. The collection is unmodifiable.
   */
  public final Map<String, IncCheckData> updatedModels;

  /**
   * All models which where not changed since the last run. They are represented by their qualified name and
   * are associated with their corresponding IncCheckData. The collection is unmodifiable.
   */
  public final Map<String, IncCheckData> untouchedModels;

  public UpToDateResults(@NotNull Map<String, IncCheckData> deletedModels,
                         @NotNull Set<String> addedModels,
                         @NotNull Map<String, IncCheckData> modifiedModels,
                         @NotNull Map<String, IncCheckData> untouchedModels) {
    Preconditions.checkNotNull(deletedModels);
    Preconditions.checkNotNull(addedModels);
    Preconditions.checkNotNull(modifiedModels);
    Preconditions.checkNotNull(untouchedModels);

    this.deletedModels = Map.copyOf(deletedModels);
    this.addedModels = Set.copyOf(addedModels);
    this.updatedModels = Map.copyOf(modifiedModels);
    this.untouchedModels = Map.copyOf(untouchedModels);
  }
}
