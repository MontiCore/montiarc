/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
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
 * Tests the inc-check mechanics of the generation of the MA2JavaTool.
 */
class MA2JavaToolIncrementalityTest {

  @TempDir
  Path tempDir;

  Path modelDir;

  Path hwcDir;

  Path javaOutDir;

  Path reportOutDir;

  String usedPackage = "pack.age";

  Path usedPackageAsPath = Path.of("pack", "age");

  private void createBasicProjectStructure() throws IOException {
    modelDir = Files.createDirectory(tempDir.resolve("models"));
    hwcDir = Files.createDirectory(tempDir.resolve("hwc"));
    javaOutDir = Files.createDirectory(tempDir.resolve("out-java"));
    reportOutDir = Files.createDirectory(tempDir.resolve("out-report"));

    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    Files.createDirectories(javaOutDir.resolve(usedPackageAsPath));
  }

  private void invokeTool() {
    invokeToolWithVersion("1.0.0");
  }

  private void invokeTollWithArgs(@NotNull String[] args) {
    this.invokeToolWithArgsAndVersion(args, "1.0.0");
  }

  private void invokeToolWithArgsAndVersion(@NotNull String[] args, @NotNull String version) {
    Preconditions.checkNotNull(args);
    Preconditions.checkNotNull(version);

    MA2JavaTool tool = new MA2JavaTool();
    tool.init();
    tool.setMa2JavaVersionSupplier(() -> version);
    try {
      tool.run(args);
    } catch (Exception e) {
      Assertions.fail(e);
    }
  }

  private void invokeToolWithVersion(@NotNull String version) {
    this.invokeToolWithArgsAndVersion(
      new String[]{
        "--modelpath", modelDir.toString(),
        "--handwritten-code", hwcDir.toString(),
        "--output", javaOutDir.toString(),
        "--report", reportOutDir.toString()
      },
      version
    );
  }

  @Test
  void testGenerationCreatesFiles() throws IOException {
    // Given
    createBasicProjectStructure();
    addDeployModel("Foo", modelDir);
    addPortModel("Bar", modelDir);

    // When
    invokeTool();

    // Then
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployFoo.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Bar.java").toFile()).exists();
  }

  @Test
  void testNoModification() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());

    // When
    invokeTool();

    // Then
    // Should not modify generated files of the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));
  }

  @Test
  void testAddedFile() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());

    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedComp.java").toFile().exists());

    // When
    addPortModel("AddedComp", modelDir);
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));

    // Should generate files for new the new input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedComp.java").toFile()).exists();
  }

  @Test
  void testRemoval() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToRemove", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToRemove.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemove.java").toFile().exists());

    // When
    Files.delete(modelDir.resolve(usedPackageAsPath).resolve("CompToRemove.arc"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));

    // Should delete generated files of the missing input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToRemove.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemove.java").toFile()).doesNotExist();
  }

  @Test
  void testUpdate() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());
    lastModified.put("CompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().exists());

    // When
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arc"),
        "package " + usedPackage + "; component CompToUpdate { port in int i; }"
    );
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdate.java"));

    // Should delete generated deploy class, modified input model is no longer a deploy component
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile()).doesNotExist();
  }

  @Test
  void testHwcAddition() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());
    lastModified.put("CompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdateTOP.java").toFile().exists());

    // When
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdate.java"));

    // Should delete generated deploy class, generate top extension point
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdateTOP.java").toFile()).exists();
  }

  @Test
  void testHwcRemoval() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java"));
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());
    lastModified.put("CompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified());
    lastModified.put("DeployCompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().lastModified());

    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateTOP.java").toFile().exists());

    // When
    Files.delete(hwcDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().lastModified()).isNotEqualTo(lastModified.get("DeployCompToUpdate.java"));

    // Should delete generated component class, generate top extension point
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateTOP.java").toFile()).doesNotExist();
  }

  @Test
  void testRemovalOfGeneratedFile() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());
    lastModified.put("CompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified());
    lastModified.put("DeployCompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified());

    // When
    Files.delete(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdate.java"));

    // Should regenerate deleted file
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().lastModified()).isNotEqualTo(lastModified.get("DeployCompToUpdate.java"));
  }

  @Test
  void testMovingOfModelDir() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addPortModel("Modified", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());
    lastModified.put("Modified.java", javaOutDir.resolve(usedPackageAsPath).resolve("Modified.java").toFile().lastModified());

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
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Modified.java").toFile().lastModified()).isNotEqualTo(lastModified.get("Modified.java"));
  }

  @Test
  void testMovingOfHwcDir() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addPortModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Foo.java", javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified());
    lastModified.put("CompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateTOP.java").toFile().exists());

    // When
    hwcDir = Files.createDirectory(tempDir.resolve("new-hwc"));
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    // Also creating a hwc file for foo:
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified()).isEqualTo(lastModified.get("Foo.java"));

    // Should delete generated component class, generate top extension point
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateTOP.java").toFile()).exists();
  }

  @Test
  void testMultipleHwcPaths() throws IOException {
    // Given
    createBasicProjectStructure();
    addDeployModel("HasUnchangedHwc", modelDir);
    addDeployModel("HasMovingHwc", modelDir);
    addPortModel("WillGetNewHwc", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwc.java"));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("HasMovingHwc.java"));
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployHasUnchangedHwcTOP.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwcTOP.java").toFile().lastModified());
    lastModified.put("HasUnchangedHwc.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwc.java").toFile().lastModified());

    lastModified.put("DeployHasMovingHwc.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasMovingHwc.java").toFile().lastModified());

    lastModified.put("DeployWillGetNewHwc.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployWillGetNewHwc.java").toFile().lastModified());

    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwc.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcTOP.java").toFile().exists());

    // When adding a hwc path, moving one model, and adding another hwc extension
    Path oldHwc = hwcDir;
    Path newHwc = Files.createDirectory(tempDir.resolve("new-hwc"));

    Files.createDirectories(newHwc.resolve(usedPackageAsPath));
    Files.createFile(newHwc.resolve(usedPackageAsPath).resolve("WillGetNewHwc.java"));
    Files.copy(
        oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwc.java"),
        newHwc.resolve(usedPackageAsPath).resolve("HasMovingHwc.java")
    );
    Files.delete(oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwc.java"));

    invokeTollWithArgs(new String[] {
      "--modelpath", modelDir.toString(),
      "--handwritten-code", oldHwc + File.pathSeparator + newHwc,
      "--output", javaOutDir.toString(),
      "--report", reportOutDir.toString()
    });

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwcTOP.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployHasUnchangedHwcTOP.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("HasUnchangedHwc.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasMovingHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployHasMovingHwc.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployWillGetNewHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployWillGetNewHwc.java"));

    // Should delete generated component class, generate top extension point
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwc.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcTOP.java").toFile()).exists();
  }

  @Test
  void testMultipleModelPaths() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Unchanged", modelDir);
    addPortModel("UnchangedButMoved", modelDir);
    addPortModel("ChangedAndMoved", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("Unchanged.java", javaOutDir.resolve(usedPackageAsPath).resolve("Unchanged.java").toFile().lastModified());
    lastModified.put("UnchangedButMoved.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.java").toFile().lastModified());
    lastModified.put("ChangedAndMoved.java", javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.java").toFile().lastModified());


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
        "--handwritten-code", hwcDir.toString(),
        "--output", javaOutDir.toString(),
        "--report", reportOutDir.toString()
    });

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Unchanged.java").toFile().lastModified()).isEqualTo(lastModified.get("Unchanged.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedButMoved.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ChangedAndMoved.java"));
  }

  @Test
  void testNoPackage() throws IOException {
    // Given
    createBasicProjectStructure();

    Files.writeString(modelDir.resolve("NoPackage.arc"), "component NoPackage { port in int i; }");
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port in int i; }");
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("NoPackage.java", javaOutDir.resolve("NoPackage.java").toFile().lastModified());
    lastModified.put("NoPackageModified.java", javaOutDir.resolve("NoPackageModified.java").toFile().lastModified());

    // When
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port out int i; }");
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve("NoPackage.java").toFile().lastModified()).isEqualTo(lastModified.get("NoPackage.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve("NoPackageModified.java").toFile().lastModified()).isNotEqualTo(lastModified.get("NoPackageModified.java"));
  }

  @Test
  void testRemoveHwcDir() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("WithoutHwc", modelDir);
    addPortModel("WithHwc", modelDir);
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("WithHwc.java"));
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("WithoutHwc.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwc.java").toFile().lastModified());

    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("WithHwc.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcTOP.java").toFile().exists());

    // When invoking the tool without hwc dir
    invokeTollWithArgs(new String[]{
        "--modelpath", modelDir.toString(),
        "--output", javaOutDir.toString(),
        "--report", reportOutDir.toString()
    });

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("WithoutHwc.java"));

    // Should delete top extension point, generate component class
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwc.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcTOP.java").toFile()).doesNotExist();
  }

  @Test
  void testChangedVersion() throws IOException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    invokeToolWithVersion("1.0.0");

    long lastModified = javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified();

    // When
    invokeToolWithVersion("1.0.1");

    // Then
    // Should regenerate files
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("Foo.java").toFile().lastModified())
      .isNotEqualTo(lastModified);
  }

  private void addDeployModel(String modelName, Path modelDirToUse) throws IOException {
    Path packagePathQualified = modelDirToUse.resolve(usedPackageAsPath);

    Files.createDirectories(packagePathQualified);
    Files.writeString(packagePathQualified.resolve(modelName + ".arc"),
        String.format("package %s; component %s { }", usedPackage, modelName)
    );
  }

  private void addPortModel(String modelName, Path modelDirToUse) throws IOException {
    Path packagePathQualified = modelDirToUse.resolve(usedPackageAsPath);

    Files.createDirectories(packagePathQualified);
    Files.writeString(packagePathQualified.resolve(modelName + ".arc"),
        String.format("package %s; component %s { port in int i; }", usedPackage, modelName)
    );
  }
}
