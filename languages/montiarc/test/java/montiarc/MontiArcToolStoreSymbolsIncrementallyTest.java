/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the inc-check mechanics of the serialization of symbols of the MontiArcTool.
 */
class MontiArcToolStoreSymbolsIncrementallyTest extends MontiArcAbstractTest {

  @TempDir
  Path tempDir;

  Path modelDir;

  Path symbolsOutDir;

  Path reportOutDir;

  String usedPackage = "pack.age";

  Path usedPackageAsPath = Path.of("pack", "age");

  private void createBasicProjectStructure() throws IOException {
    modelDir = Files.createDirectory(tempDir.resolve("models"));
    symbolsOutDir = Files.createDirectory(tempDir.resolve("out-symbols"));
    reportOutDir = Files.createDirectory(tempDir.resolve("out-report"));

    Files.createDirectories(symbolsOutDir.resolve(usedPackageAsPath));
  }

  private void invokeTool() {
    this.invokeToolWithVersion("1.0.0");
  }

  private void invokeTollWithArgs(@NotNull String[] args) {
    this.invokeToolWithArgsAndVersion(args, "1.0.0");
  }

  private void invokeToolWithArgsAndVersion(@NotNull String[] args, @NotNull String version) {
    Preconditions.checkNotNull(args);
    Preconditions.checkNotNull(version);

    MontiArcTool tool = new MontiArcTool();
    tool.init();
    tool.setMa2JavaVersionSupplier(() -> version);
    tool.run(args);
  }

  private void invokeToolWithVersion(@NotNull String version) {
    this.invokeToolWithArgsAndVersion(
      new String[]{
        "--modelpath", modelDir.toString(),
        "--symboltable", symbolsOutDir.toString(),
        "--report", reportOutDir.toString()
      },
      version
    );
  }

  @Test
  void testStoreSymbolsCreatesFiles() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel( "Foo", modelDir);
    addModel("Bar", modelDir);

    // When
    invokeTool();

    // Then
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile()).exists();
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Bar.arcsym").toFile()).exists();
  }

  @Test
  void testNoModification() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified());

    // When
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("Foo.arcsym"));
  }

  @Test
  void testAddedFile() throws IOException {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified());

    Preconditions.checkState(!symbolsOutDir.resolve(usedPackageAsPath).resolve("AddedComp.arcsym").toFile().exists());

    // When
    addModel("AddedComp", modelDir);
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("Foo.arcsym"));

    // Should generate files for new the new input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("AddedComp.arcsym").toFile()).exists();
  }

  @Test
  void testRemoval() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("CompToRemove", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified());

    Preconditions.checkState(symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToRemove.arcsym").toFile().exists());

    // When
    Files.delete(modelDir.resolve(usedPackageAsPath).resolve("CompToRemove.arc"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("Foo.arcsym"));

    // Should delete generated files of the missing input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToRemove.arcsym").toFile()).doesNotExist();
  }

  @Test
  void testUpdate() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified());
    lastModified.put("CompToUpdate.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arcsym").toFile().lastModified());

    // When
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arc"),
      "package " + usedPackage + "; component CompToUpdate { port out int i; }"
    );
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("Foo.arcsym"));

    // Should regenerate files of modified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arcsym").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdate.arcsym"));
  }

  @Test
  void testRemovalOfGeneratedFile() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified());
    lastModified.put("CompToUpdate.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arcsym").toFile().lastModified());

    // When
    Files.delete(symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arcsym"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("Foo.arcsym"));

    // Should regenerate files of modified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arcsym").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdate.arcsym"));
  }

  @Test
  void testMovingOfModelDir() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("Modified", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified());
    lastModified.put("Modified.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Modified.arcsym").toFile().lastModified());

    // When
    Path newModelDir = Files.createDirectory(tempDir.resolve("new-models"));
    Files.createDirectories(newModelDir.resolve(usedPackageAsPath));
    Files.copy(
      modelDir.resolve(usedPackageAsPath).resolve("Foo.arc"),
      newModelDir.resolve(usedPackageAsPath).resolve("Foo.arc")
    );
    modelDir = newModelDir;
    // Instead of copying "Modified", give it new contents:
    Files.writeString(newModelDir.resolve(usedPackageAsPath).resolve("Modified.arc"),
      "package " + usedPackage + "; component Modified { port out int i; }"
    );
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("Foo.arcsym"));

    // Should regenerate files of modified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Modified.arcsym").toFile().lastModified()).isNotEqualTo(lastModified.get("Modified.arcsym"));
  }

  @Test
  void testMultipleModelPaths() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel("Unchanged", modelDir);
    addModel("UnchangedButMoved", modelDir);
    addModel("ChangedAndMoved", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Unchanged.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("Unchanged.arcsym").toFile().lastModified());
    lastModified.put("UnchangedButMoved.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arcsym").toFile().lastModified());
    lastModified.put("ChangedAndMoved.arcsym", symbolsOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arcsym").toFile().lastModified());

    // When adding a new model path, moving a model, and changing another one
    Path oldModelDir = modelDir;
    Path newModelDir = Files.createDirectory(tempDir.resolve("new-models"));

    Files.createDirectories(newModelDir.resolve(usedPackageAsPath));
    Files.copy(
      oldModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc"),
      newModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc")
    );
    Files.delete(oldModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc"));

    Files.writeString(newModelDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arc"),
        "package " + usedPackage + "; component ChangedAndMoved { port out int i; }"
    );
    Files.delete(oldModelDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arc"));

    invokeTollWithArgs(new String[] {
        "--modelpath", oldModelDir + File.pathSeparator + newModelDir,
        "--symboltable", symbolsOutDir.toString(),
        "--report", reportOutDir.toString()
    });

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Unchanged.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("Unchanged.arcsym"));
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedButMoved.arcsym"));

    // Should regenerate files of modified input model
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arcsym").toFile().lastModified()).isNotEqualTo(lastModified.get("ChangedAndMoved.arcsym"));
  }

  @Test
  void testNoPackage() throws IOException  {
    // Given
    createBasicProjectStructure();
    Files.writeString(modelDir.resolve("NoPackage.arc"), "component NoPackage { port in int i; }");
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port in int i; }");
    invokeTool();


    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("NoPackage.arcsym", symbolsOutDir.resolve("NoPackage.arcsym").toFile().lastModified());
    lastModified.put("NoPackageModified.arcsym", symbolsOutDir.resolve("NoPackageModified.arcsym").toFile().lastModified());

    // When
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port out int i; }");
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(symbolsOutDir.resolve("NoPackage.arcsym").toFile().lastModified()).isEqualTo(lastModified.get("NoPackage.arcsym"));

    // Should regenerate files of modified input model
    assertThat(symbolsOutDir.resolve("NoPackageModified.arcsym").toFile().lastModified()).isNotEqualTo(lastModified.get("NoPackageModified.arcsym"));
  }

  @Test
  void testVersionChange() throws IOException  {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    invokeToolWithVersion("1.0.0");

    long lastModified = symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified();


    // When
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arc"),
      "package " + usedPackage + "; component CompToUpdate { port out int i; }"
    );
    invokeToolWithVersion("1.0.1");

    // Then
    // Should regenerate files
    assertThat(symbolsOutDir.resolve(usedPackageAsPath).resolve("Foo.arcsym").toFile().lastModified()).isNotEqualTo(lastModified);
  }

  private void addModel(String modelName, Path modelDirToUse) throws IOException {
    Path packagePathQualified = modelDirToUse.resolve(usedPackageAsPath);

    Files.createDirectories(packagePathQualified);
    Files.writeString(packagePathQualified.resolve(modelName + ".arc"),
      String.format("package %s; component %s { port in int i; }", usedPackage, modelName)
    );
  }
}
