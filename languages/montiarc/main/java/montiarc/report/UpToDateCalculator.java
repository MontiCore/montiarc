/* (c) https://github.com/MontiCore/monticore */
package montiarc.report;

import com.google.common.base.Preconditions;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Determines which model files are up-to-date, changed, removed, or newly created, based on the current configuration
 * of the modelpath, hwc, and target.
 */
public final class UpToDateCalculator {

  private final Collection<Path> modelpath;
  private final Collection<Path> hwc;
  private final Path target;

  public UpToDateCalculator(@NotNull Collection<Path> modelpath,
                            @NotNull Collection<Path> hwc,
                            @NotNull Path target) {
    this.modelpath = Preconditions.checkNotNull(modelpath);
    this.hwc = Preconditions.checkNotNull(hwc);
    this.target = Preconditions.checkNotNull(target);
  }

  /**
   * @param astByQName Parsed compilation units, indexed by their qualified name.
   * @param incDataByQName Deserialized inc check data, indexed by the qualified name of the corresponding model.
   */
  public UpToDateResults checkUpToDateness(@NotNull Map<String, ASTMACompilationUnit> astByQName,
                                           @NotNull Map<String, IncCheckData> incDataByQName) {
    return checkUpToDateness(astByQName.keySet(), incDataByQName);
  }

  /**
   * @param namesOfParsedModels Qualified names of the top-level component of all parsed compilation units.
   * @param incDataByQName Deserialized inc check data, indexed by the qualified name of the corresponding model.
   */
  public UpToDateResults checkUpToDateness(@NotNull Collection<String> namesOfParsedModels,
                                           @NotNull Map<String, IncCheckData> incDataByQName) {
    Preconditions.checkNotNull(namesOfParsedModels);
    Preconditions.checkNotNull(incDataByQName);

    // Will be built:
    // We will collect go through the incCheckData and when we realize that it relates to a deleted / modified /
    // untouched model, then we will add it to one of the following collections
    Map<String, IncCheckData> deletedModels = new HashMap<>();
    Map<String, IncCheckData> modifiedModels = new HashMap<>();
    Map<String, IncCheckData> untouchedModels = new HashMap<>();

    // Will be reduced:
    // First we assume that every parsed model is new and when we encounter incCheckData
    // that belongs to one of the parsed models, we will remove the model from this collection.
    Set<String> addedModels = new HashSet<>(namesOfParsedModels);

    for (Map.Entry<String, IncCheckData> incDataOfModel : incDataByQName.entrySet()) {
      String qualifiedModelName = incDataOfModel.getKey();
      IncCheckData incData = incDataOfModel.getValue();

      addedModels.remove(qualifiedModelName);

      Optional<Path> modelLocation =  modelpath.stream()
        .map(mp -> mp.resolve(incData.getModelFile()))
        .filter(modelLoc -> modelLoc.toFile().isFile())
        .findFirst();

      if (modelLocation.isEmpty()) {
        deletedModels.put(qualifiedModelName, incData);
      } else if (this.isOutdated(modelLocation.get(), incData)) {
        modifiedModels.put(qualifiedModelName, incData);
      } else {
        untouchedModels.put(qualifiedModelName, incData);
      }
    }

    return new UpToDateResults(deletedModels, addedModels, modifiedModels, untouchedModels);
  }

  private boolean isOutdated(Path modelLocation, IncCheckData incData) {
    Preconditions.checkArgument(modelLocation.toFile().isFile());

    // We will be checking the following Conditions
    // 1: If the checksum changed, the model is modified
    // 2: If an old HWC file is deleted, the model is marked as modified (so that generation is re-triggered)
    // 3: If a new HWC file is found, the model is marked as modified (so that generation is re-triggered)
    // 4: If a previously generated file is missing, the model is marked as modified (so that the mising file can be
    //    re-generated)

    String modelLocationAsStr = modelLocation.toAbsolutePath().toString();
    String currentCheckSum = ReportingHelper.getChecksum(modelLocationAsStr);
    String oldCheckSum = incData.getModelHash();

    if (!currentCheckSum.equals(oldCheckSum)) {
      return true;
    }

    // all hwc file is in any hwc
    for (Path expectedHwcFile : incData.getUsedHwcFiles()) {
      if (hwc.stream().noneMatch(h -> h.resolve(expectedHwcFile).toFile().isFile())) {
        return true;
      }
    }

    for (Path unexpectedHwcFile : incData.getAbsentHwcFiles()) {
      if (hwc.stream().anyMatch(h -> h.resolve(unexpectedHwcFile).toFile().isFile())) {
        return true;
      }
    }

    for (Path expectedOutFile : incData.getOutFiles()) {
      if (!target.resolve(expectedOutFile).toFile().isFile()) {
        return true;
      }
    }

    return false;
  }

}
