/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import org.junit.jupiter.api.Disabled;
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
 * Tests the inc-check mechanics of the generation of the MA2JSimTool.
 */
class MA2JSimToolIncrementalityTest {

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
    MA2JSimTool.main(new String[]{
        "--modelpath", modelDir.toString(),
        "--handwritten-code", hwcDir.toString(),
        "--output", javaOutDir.toString(),
        "--report", reportOutDir.toString()
    });
  }

  @Test
  void testGenerationCreatesFiles() throws IOException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    addAtomicDeployModel("Bar", modelDir);

    // When
    invokeTool();

    // Then
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile()).exists();

    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployBar.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("BarComp.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("BarCompBuilder.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("BarContext.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("BarEvents.java").toFile()).exists();
  }

  @Test
  void testNoModification() throws IOException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooAutomaton.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified());
    lastModified.put("FooAutomatonBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified());
    lastModified.put("FooStates.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    // When
    invokeTool();

    // Then
    // Should not modify generated files of the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomaton.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomatonBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified()).isEqualTo(lastModified.get("FooStates.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));
  }

  @Test
  void testAddedFile() throws IOException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooAutomaton.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified());
    lastModified.put("FooAutomatonBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified());
    lastModified.put("FooStates.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedComp.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedCompBuilder.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedContext.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedAutomaton.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedAutomatonBuilder.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedStates.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("AddedEvents.java").toFile().exists());

    // When
    addAutomatonModel("Added", modelDir);
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomaton.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomatonBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified()).isEqualTo(lastModified.get("FooStates.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    // Should generate files for new the new input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedComp.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedCompBuilder.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedContext.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedAutomaton.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedAutomatonBuilder.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedStates.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("AddedEvents.java").toFile()).exists();
  }

  @Test
  void testRemoval() throws IOException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    addAutomatonModel("CompToRemove", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooAutomaton.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified());
    lastModified.put("FooAutomatonBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified());
    lastModified.put("FooStates.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveComp.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveCompBuilder.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveContext.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveAutomaton.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveAutomatonBuilder.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveStates.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveEvents.java").toFile().exists());

    // When
    Files.delete(modelDir.resolve(usedPackageAsPath).resolve("CompToRemove.arc"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomaton.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomatonBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified()).isEqualTo(lastModified.get("FooStates.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    // Should delete generated files of the missing input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveComp.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveCompBuilder.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveContext.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveAutomaton.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveAutomatonBuilder.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveStates.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToRemoveEvents.java").toFile()).doesNotExist();
  }

  @Test
  void testUpdate() throws IOException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooAutomaton.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified());
    lastModified.put("FooAutomatonBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified());
    lastModified.put("FooStates.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    lastModified.put("DeployCompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().lastModified());
    lastModified.put("CompToUpdateComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified());
    lastModified.put("CompToUpdateCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified());
    lastModified.put("CompToUpdateContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified());
    lastModified.put("CompToUpdateEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().exists());

    // When
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arc"),
        "package " + usedPackage + "; component CompToUpdate { port in int i; }"
    );
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomaton.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomaton.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooAutomatonBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooAutomatonBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooStates.java").toFile().lastModified()).isEqualTo(lastModified.get("FooStates.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdateComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdateCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdateContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified()).isNotEqualTo(lastModified.get("CompToUpdateEvents.java"));

    // Should delete generated deploy class, modified input model is no longer a deploy component
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile()).doesNotExist();
  }

  @Test
  void testHwcAddition() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployFoo.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployFoo.java").toFile().lastModified());
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    lastModified.put("CompToUpdateComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified());
    lastModified.put("CompToUpdateCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified());
    lastModified.put("CompToUpdateContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified());
    lastModified.put("CompToUpdateEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdateTOP.java").toFile().exists());

    // When
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateComp.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateCompBuilder.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateContext.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateEvents.java"));

    // Should delete generated deploy class, generate top extension point
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdateTOP.java").toFile()).exists();
  }

  @Test
  void testHwcRemoval() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployFoo.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployFoo.java").toFile().lastModified());
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    lastModified.put("CompToUpdateComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified());
    lastModified.put("CompToUpdateCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified());
    lastModified.put("CompToUpdateContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified());
    lastModified.put("CompToUpdateEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified());

    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdateTOP.java").toFile().exists());

    // When
    Files.delete(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateComp.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateCompBuilder.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateContext.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateEvents.java"));

    // Should delete generated top extension point, generate deploy class
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdateTOP.java").toFile()).doesNotExist();
  }

  @Test
  void testRemovalOfGeneratedFile() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployFoo.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployFoo.java").toFile().lastModified());
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    lastModified.put("CompToUpdateComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified());
    lastModified.put("CompToUpdateCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified());
    lastModified.put("CompToUpdateContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified());
    lastModified.put("CompToUpdateEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().exists());

    // When
    Files.delete(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateComp.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateCompBuilder.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateContext.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateEvents.java"));

    // Should regenerate deploy class
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile()).exists();
  }

  @Test
  void testMovingOfModelDir() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("Modified", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployFoo.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployFoo.java").toFile().lastModified());
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    lastModified.put("ModifiedComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedComp.java").toFile().lastModified());
    lastModified.put("ModifiedCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedCompBuilder.java").toFile().lastModified());
    lastModified.put("ModifiedContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedContext.java").toFile().lastModified());
    lastModified.put("ModifiedEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployModified.java").toFile().exists());

    // When
    Path newModelDir = Files.createDirectory(tempDir.resolve("new-models"));
    Files.createDirectories(newModelDir.resolve(usedPackageAsPath));
    Files.copy(
        modelDir.resolve(usedPackageAsPath).resolve("Foo.arc"),
        newModelDir.resolve(usedPackageAsPath).resolve("Foo.arc")
    );
    modelDir = newModelDir;

    // Instead of copying component "Modified", give it new contents:
    Files.writeString(newModelDir.resolve(usedPackageAsPath).resolve("Modified.arc"),
        "package " + usedPackage + "; component Modified { port out int i; }"
    );
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedComp.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ModifiedComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedCompBuilder.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ModifiedCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedContext.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ModifiedContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ModifiedEvents.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ModifiedEvents.java"));

    // Should delete generated deploy class, modified input model is no longer a deploy component
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployModified.java").toFile()).doesNotExist();
  }

  @Test
  void testMovingOfHwcDir() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployFoo.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployFoo.java").toFile().lastModified());
    lastModified.put("FooComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified());
    lastModified.put("FooCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified());
    lastModified.put("FooContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified());
    lastModified.put("FooEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified());

    lastModified.put("DeployCompToUpdate.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().lastModified());
    lastModified.put("CompToUpdateCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified());
    lastModified.put("CompToUpdateContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified());
    lastModified.put("CompToUpdateEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompTOP.java").toFile().exists());

    // When
    hwcDir = Files.createDirectory(tempDir.resolve("new-hwc"));
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));

    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooComp.java").toFile().lastModified()).isEqualTo(lastModified.get("FooComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("FooCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooContext.java").toFile().lastModified()).isEqualTo(lastModified.get("FooContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("FooEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("FooEvents.java"));

    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateComp.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateCompBuilder.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateContext.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateContext.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("CompToUpdateEvents.java"));

    // Should deleted generated component class, generate top extension point
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("CompToUpdateCompTOP.java").toFile()).exists();
  }

  @Test
  @Disabled
  void testMultipleHwcPaths() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("HasUnchangedHwc", modelDir);
    addAtomicDeployModel("HasMovingHwc", modelDir);
    addAtomicDeployModel("WillGetNewHwc", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwc.java"));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java"));
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployHasUnchangedHwcTOP.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwcTOP.java").toFile().lastModified());
    lastModified.put("HasUnchangedHwcComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcComp.java").toFile().lastModified());
    lastModified.put("HasUnchangedHwcCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcCompBuilder.java").toFile().lastModified());
    lastModified.put("HasUnchangedHwcContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcContext.java").toFile().lastModified());
    lastModified.put("HasUnchangedHwcEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcEvents.java").toFile().lastModified());

    lastModified.put("DeployHasMovingHwc.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasMovingHwc.java").toFile().lastModified());
    lastModified.put("HasMovingHwcCompTOP.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcCompTOP.java").toFile().lastModified());
    lastModified.put("HasMovingHwcCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcCompBuilder.java").toFile().lastModified());
    lastModified.put("HasMovingHwcContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcContext.java").toFile().lastModified());
    lastModified.put("HasMovingHwcEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcEvents.java").toFile().lastModified());

    lastModified.put("DeployWillGetNewHwc.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployWillGetNewHwc.java").toFile().lastModified());
    lastModified.put("WillGetNewHwcComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcComp.java").toFile().lastModified());
    lastModified.put("WillGetNewHwcCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcCompBuilder.java").toFile().lastModified());
    lastModified.put("WillGetNewHwcContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcContext.java").toFile().lastModified());
    lastModified.put("WillGetNewHwcEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcComp.java").toFile().exists());
    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcCompTOP.java").toFile().exists());

    // When adding a hwc path, moving one model, and adding another hwc extension
    Path oldHwc = hwcDir;
    Path newHwc = Files.createDirectory(tempDir.resolve("new-hwc"));
    hwcDir = Path.of(hwcDir.toString() + File.pathSeparator + newHwc);

    Files.createDirectories(newHwc.resolve(usedPackageAsPath));
    Files.createFile(newHwc.resolve(usedPackageAsPath).resolve("WillGetNewHwcComp.java"));
    Files.copy(
        oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java"),
        newHwc.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java")
    );
    Files.delete(oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java"));
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwcTOP.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployHasUnchangedHwcTOP.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcComp.java").toFile().lastModified()).isEqualTo(lastModified.get("HasUnchangedHwcComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("HasUnchangedHwcCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcContext.java").toFile().lastModified()).isEqualTo(lastModified.get("HasUnchangedHwcContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasUnchangedHwcEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("HasUnchangedHwcEvents.java"));

    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployHasMovingHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployHasMovingHwc.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcCompTOP.java").toFile().lastModified()).isEqualTo(lastModified.get("HasMovingHwcCompTOP.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("HasMovingHwcCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcContext.java").toFile().lastModified()).isEqualTo(lastModified.get("HasMovingHwcContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("HasMovingHwcEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("HasMovingHwcEvents.java"));

    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployWillGetNewHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployWillGetNewHwc.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("WillGetNewHwcCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcContext.java").toFile().lastModified()).isEqualTo(lastModified.get("WillGetNewHwcContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("WillGetNewHwcEvents.java"));

    // Should delete generated component class, generate top extension point
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcComp.java").toFile()).doesNotExist();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WillGetNewHwcCompTOP.java").toFile()).exists();
  }

  @Test
  @Disabled
  void testMultipleModelPaths() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Unchanged", modelDir);
    addAtomicDeployModel("UnchangedButMoved", modelDir);
    addAtomicDeployModel("ChangedAndMoved", modelDir);
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployUnchanged.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployUnchanged.java").toFile().lastModified());
    lastModified.put("UnchangedComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedComp.java").toFile().lastModified());
    lastModified.put("UnchangedCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedCompBuilder.java").toFile().lastModified());
    lastModified.put("UnchangedContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedContext.java").toFile().lastModified());
    lastModified.put("UnchangedEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedEvents.java").toFile().lastModified());

    lastModified.put("DeployUnchangedButMoved.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployUnchangedButMoved.java").toFile().lastModified());
    lastModified.put("UnchangedButMovedComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedComp.java").toFile().lastModified());
    lastModified.put("UnchangedButMovedCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedCompBuilder.java").toFile().lastModified());
    lastModified.put("UnchangedButMovedContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedContext.java").toFile().lastModified());
    lastModified.put("UnchangedButMovedEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedEvents.java").toFile().lastModified());

    lastModified.put("ChangedAndMovedComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedComp.java").toFile().lastModified());
    lastModified.put("ChangedAndMovedCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedCompBuilder.java").toFile().lastModified());
    lastModified.put("ChangedAndMovedContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedContext.java").toFile().lastModified());
    lastModified.put("ChangedAndMovedEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedEvents.java").toFile().lastModified());

    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("DeployChangedAndMoved.java").toFile().exists());

    // When adding a new model path, moving a model, and changing another one
    Path oldModelDir = modelDir;
    Path newModelDir = Files.createDirectory(tempDir.resolve("new-models"));
    modelDir = Path.of(oldModelDir.toString() + File.pathSeparator + newModelDir);

    Files.createDirectories(newModelDir.resolve(usedPackageAsPath));
    Files.copy(
        oldModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc"),
        newModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc")
    );
    Files.delete(oldModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc"));

    Files.delete(oldModelDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arc"));
    Files.writeString(newModelDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arc"),
        "package " + usedPackage + "; component ChangedAndMoved { port out int i; }"
    );
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployUnchanged.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployUnchanged.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedComp.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedContext.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedEvents.java"));

    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployUnchangedButMoved.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployUnchangedButMoved.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedComp.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedButMovedComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedButMovedCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedContext.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedButMovedContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("UnchangedButMovedEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("UnchangedButMovedEvents.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedComp.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ChangedAndMovedComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedCompBuilder.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ChangedAndMovedCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedContext.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ChangedAndMovedContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("ChangedAndMovedEvents.java").toFile().lastModified()).isNotEqualTo(lastModified.get("ChangedAndMovedEvents.java"));

    // Should delete generated deploy class, modified input model is no longer a deploy component
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployChangedAndMoved.java").toFile()).doesNotExist();
  }

  @Test
  void testNoPackage() throws IOException {
    // Given
    createBasicProjectStructure();
    Files.writeString(modelDir.resolve("NoPackage.arc"), "component NoPackage { port in int i; }");
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port in int i; }");
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("NoPackageComp.java", javaOutDir.resolve("NoPackageComp.java").toFile().lastModified());
    lastModified.put("NoPackageCompBuilder.java", javaOutDir.resolve("NoPackageCompBuilder.java").toFile().lastModified());
    lastModified.put("NoPackageContext.java", javaOutDir.resolve("NoPackageContext.java").toFile().lastModified());
    lastModified.put("NoPackageEvents.java", javaOutDir.resolve("NoPackageEvents.java").toFile().lastModified());

    lastModified.put("NoPackageModifiedComp.java", javaOutDir.resolve("NoPackageModifiedComp.java").toFile().lastModified());
    lastModified.put("NoPackageModifiedCompBuilder.java", javaOutDir.resolve("NoPackageModifiedCompBuilder.java").toFile().lastModified());
    lastModified.put("NoPackageModifiedContext.java", javaOutDir.resolve("NoPackageModifiedContext.java").toFile().lastModified());
    lastModified.put("NoPackageModifiedEvents.java", javaOutDir.resolve("NoPackageModifiedEvents.java").toFile().lastModified());

    // When
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port out int i; }");
    invokeTool();

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve("NoPackageComp.java").toFile().lastModified()).isEqualTo(lastModified.get("NoPackageComp.java"));
    assertThat(javaOutDir.resolve("NoPackageCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("NoPackageCompBuilder.java"));
    assertThat(javaOutDir.resolve("NoPackageContext.java").toFile().lastModified()).isEqualTo(lastModified.get("NoPackageContext.java"));
    assertThat(javaOutDir.resolve("NoPackageEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("NoPackageEvents.java"));

    // Should regenerate files of modified input model
    assertThat(javaOutDir.resolve("NoPackageModifiedComp.java").toFile().lastModified()).isNotEqualTo(lastModified.get("NoPackageModifiedComp.java"));
    assertThat(javaOutDir.resolve("NoPackageModifiedCompBuilder.java").toFile().lastModified()).isNotEqualTo(lastModified.get("NoPackageModifiedCompBuilder.java"));
    assertThat(javaOutDir.resolve("NoPackageModifiedContext.java").toFile().lastModified()).isNotEqualTo(lastModified.get("NoPackageModifiedContext.java"));
    assertThat(javaOutDir.resolve("NoPackageModifiedEvents.java").toFile().lastModified()).isNotEqualTo(lastModified.get("NoPackageModifiedEvents.java"));
  }

  @Test
  void testRemoveHwcDir() throws IOException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("WithoutHwc", modelDir);
    addAtomicDeployModel("WithHwc", modelDir);
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("WithHwcComp.java"));
    invokeTool();

    Map<String, Long> lastModified = new HashMap<>();
    lastModified.put("DeployWithoutHwc.java", javaOutDir.resolve(usedPackageAsPath).resolve("DeployWithoutHwc.java").toFile().lastModified());
    lastModified.put("WithoutHwcComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcComp.java").toFile().lastModified());
    lastModified.put("WithoutHwcCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcCompBuilder.java").toFile().lastModified());
    lastModified.put("WithoutHwcContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcContext.java").toFile().lastModified());
    lastModified.put("WithoutHwcEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcEvents.java").toFile().lastModified());

    lastModified.put("WithHwcComp.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcComp.java").toFile().lastModified());
    lastModified.put("WithHwcCompBuilder.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcCompBuilder.java").toFile().lastModified());
    lastModified.put("WithHwcContext.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcContext.java").toFile().lastModified());
    lastModified.put("WithHwcEvents.java", javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcEvents.java").toFile().lastModified());

    Preconditions.checkState(!javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcComp.java").toFile().exists());
    Preconditions.checkState(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcCompTOP.java").toFile().exists());

    // When invoking the tool without hwc dir
    MA2JSimTool.main(new String[]{
        "--modelpath", modelDir.toString(),
        "--output", javaOutDir.toString(),
        "--report", reportOutDir.toString()
    });

    // Then
    // Should not modify generated files for the unmodified input model
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployWithoutHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployWithoutHwc.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcComp.java").toFile().lastModified()).isEqualTo(lastModified.get("WithoutHwcComp.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("WithoutHwcCompBuilder.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcContext.java").toFile().lastModified()).isEqualTo(lastModified.get("WithoutHwcContext.java"));
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithoutHwcEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("WithoutHwcEvents.java"));

    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("DeployWithHwc.java").toFile().lastModified()).isEqualTo(lastModified.get("DeployWithHwc.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcCompBuilder.java").toFile().lastModified()).isEqualTo(lastModified.get("WithHwcCompBuilder.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcContext.java").toFile().lastModified()).isEqualTo(lastModified.get("WithHwcContext.java"));
    //assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcEvents.java").toFile().lastModified()).isEqualTo(lastModified.get("WithHwcEvents.java"));

    // Should delete top extension point, generate component class
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcComp.java").toFile()).exists();
    assertThat(javaOutDir.resolve(usedPackageAsPath).resolve("WithHwcCompTOP.java").toFile()).doesNotExist();
  }

  private void addAutomatonModel(String modelName, Path modelDirToUse) throws IOException {
    Path packagePathQualified = modelDirToUse.resolve(usedPackageAsPath);

    String model = String.format(
        "package %s; component %s {" +
            "port in int i;" +
            "port in int o;" +
            "<<timed>> automaton {" +
            "initial state S1;" +
            "}" +
            "}",
        usedPackage, modelName);

    Files.createDirectories(packagePathQualified);
    Files.writeString(packagePathQualified.resolve(modelName + ".arc"), model);
  }

  private void addAtomicDeployModel(String modelname, Path modelDirToUse) throws IOException {
    Path packagePathQualified = modelDirToUse.resolve(usedPackageAsPath);

    String model = String.format(
        "package %s; component %s { }",
        usedPackage, modelname);

    Files.createDirectories(packagePathQualified);
    Files.writeString(packagePathQualified.resolve(modelname + ".arc"), model);
  }
}
