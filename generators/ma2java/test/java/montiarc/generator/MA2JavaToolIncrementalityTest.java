/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * Tests the inc-check mechanics of the generation of the MA2JavaTool.
 */
class MA2JavaToolIncrementalityTest {
  @TempDir Path tempDir;
  WatchService fileWatcher;
  Path modelDir;
  Path hwcDir;
  Path javaOutDir;
  Path reportOutDir;

  String usedPackage = "pack.age";
  Path usedPackageAsPath = Path.of("pack", "age");

  private static final WatchEvent.Kind<?>[] eventsToMonitor = {
    ENTRY_CREATE, ENTRY_DELETE
  };

  @BeforeEach
  void setUpFileWatcher() throws IOException {
    fileWatcher = FileSystems.getDefault().newWatchService();
  }

  @AfterEach
  void tearDownFileWatcher() throws IOException {
    fileWatcher.close();
  }

  private void createBasicProjectStructure() throws IOException {
    modelDir = Files.createDirectory(tempDir.resolve("models"));
    hwcDir = Files.createDirectory(tempDir.resolve("hwc"));
    javaOutDir = Files.createDirectory(tempDir.resolve("out-java"));
    reportOutDir = Files.createDirectory(tempDir.resolve("out-report"));

    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    Files.createDirectories(javaOutDir.resolve(usedPackageAsPath));
    javaOutDir.resolve(usedPackageAsPath).register(fileWatcher, eventsToMonitor);
  }

  private void invokeTool() {
    invokeTool(false);
  }

  private void invokeTool(boolean withDse) {
    MA2JavaTool.main(new String[] {
      "--modelpath", modelDir.toString(),
      "--handwritten-code", hwcDir.toString(),
      "--output", javaOutDir.toString(),
      "--report", reportOutDir.toString(),
      withDse ? "-dse" : ""
    });
  }

  @Test
  void testGenerationCreatesFiles() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addDeployModel( "Foo", modelDir);
    addPortModel("Bar", modelDir);

    // When
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "DeployFoo.java", List.of(ENTRY_CREATE),
      "Foo.java", List.of(ENTRY_CREATE),
      "Bar.java", List.of(ENTRY_CREATE)
    ));
  }

  @Test
  void testNoModification() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    invokeTool();

    // When
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE)  // Only one create event from the first run
    ));
  }

  @Test
  void testAddedFile() throws IOException, InterruptedException{
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    invokeTool();

    // When
    addPortModel("AddedComp", modelDir);
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE),  // Only one create event from the first run
      "AddedComp.java", List.of(ENTRY_CREATE)  // Create during second run
    ));
  }

  @Test
  void testRemoval() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToRemove", modelDir);
    invokeTool();

    // When
    Files.delete(modelDir.resolve(usedPackageAsPath).resolve("CompToRemove.arc"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE),  // Should stay
      "CompToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "DeployCompToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE)
    ));
  }

  @Test
  void testUpdate() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arc"),
      "package " + usedPackage + "; component CompToUpdate { port in int i; }"
    );
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE),  // Should stay
      "CompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE)  // New component version is not deployed anymore
    ));
  }

  @Test
  void testHwcAddition() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE),  // Should stay
      "CompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE),  // New component version is not deployed anymore
      "DeployCompToUpdateTOP.java", List.of(ENTRY_CREATE)  // New TOP file
    ));
  }

  @Test
  void testHwcRemoval() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java"));
    invokeTool();

    // When
    Files.delete(hwcDir.resolve(usedPackageAsPath).resolve("CompToUpdate.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE),  // Should stay
      "CompToUpdateTOP.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "CompToUpdate.java", List.of(ENTRY_CREATE),
      "DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testRemovalOfGeneratedFile() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.delete(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE),  // Should stay
      "CompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testMovingOfModelDir() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addPortModel("Modified", modelDir);
    invokeTool();

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
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE),  // Should not re-generate
      "Modified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testMovingOfHwcDir() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Foo", modelDir);
    addDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    hwcDir = Files.createDirectory(tempDir.resolve("new-hwc"));
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    // Also creating a hwc file for foo:
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("Foo.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "FooTOP.java", List.of(ENTRY_CREATE),
      "CompToUpdate.java", List.of(ENTRY_CREATE),
      "DeployCompToUpdate.java", List.of(ENTRY_CREATE)
    ));
  }

  @Test
  @Disabled
  void testMultipleHwcPaths() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addDeployModel("HasUnchangedHwc", modelDir);
    addDeployModel("HasMovingHwc", modelDir);
    addPortModel("WillGetNewHwc", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwc.java"));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("HasMovingHwc.java"));
    invokeTool();

    // When adding a hwc path, moving one model, and adding another hwc extension
    Path oldHwc = hwcDir;
    Path newHwc = Files.createDirectory(tempDir.resolve("new-hwc"));
    hwcDir = Path.of(hwcDir.toString() + File.pathSeparator + newHwc.toString());

    Files.createDirectories(newHwc.resolve(usedPackageAsPath));
    Files.createFile(newHwc.resolve(usedPackageAsPath).resolve("WillGetNewHwc.java"));
    Files.copy(
      oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwc.java"),
      newHwc.resolve(usedPackageAsPath).resolve("HasMovingHwc.java")
    );
    Files.delete(oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwc.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "HasUnchangedHwc.java", List.of(ENTRY_CREATE),  // Not changed by second run
      "DeployHasUnchangedHwcTOP.java", List.of(ENTRY_CREATE),  // Not changed by second run
      "HasMovingHwcTOP.java", List.of(ENTRY_CREATE),  // Not changed by second run
      "DeployHasMovingHwc.java", List.of(ENTRY_CREATE),  // Not changed by second run
      "WillGetNewHwc.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "WillGetNewHwcTOP.java", List.of(ENTRY_CREATE)
    ));
  }

  @Test
  @Disabled
  void testMultipleModelPaths() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Unchanged", modelDir);
    addPortModel("UnchangedButMoved", modelDir);
    addPortModel("ChangedAndMoved", modelDir);
    invokeTool();

    // When adding a new model path, moving a model, and changing another one
    Path oldModelDir = modelDir;
    Path newModelDir = Files.createDirectory(tempDir.resolve("new-models"));
    modelDir = Path.of(oldModelDir.toString() + File.pathSeparator + newModelDir.toString());

    Files.createDirectories(newModelDir.resolve(usedPackageAsPath));
    Files.writeString(newModelDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arc"),
      "package " + usedPackage + "; component ChangedAndMoved { port out int i; }"
    );
    Files.delete(oldModelDir.resolve(usedPackageAsPath).resolve("ChangedAndMoved.arc"));

    Files.copy(
      oldModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc"),
      newModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc")
    );
    Files.delete(oldModelDir.resolve(usedPackageAsPath).resolve("UnchangedButMoved.arc"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Unchanged.java", List.of(ENTRY_CREATE),  // Not changed by second run
      "UnchangedButMoved.java", List.of(ENTRY_CREATE),  // Not changed by second run
      "ChangedAndMoved.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testNoPackage() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    WatchService packageLessWatcher = FileSystems.getDefault().newWatchService();
    javaOutDir.register(packageLessWatcher, eventsToMonitor);

    Files.writeString(modelDir.resolve("NoPackage.arc"), "component NoPackage { port in int i; }");
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port in int i; }");
    invokeTool();

    // When
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port out int i; }");
    invokeTool();

    // Then
    assertFileEventsToBeExactly(packageLessWatcher, Map.of(
      "NoPackage.java", List.of(ENTRY_CREATE),
      "NoPackageModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testRemoveHwcDir() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("WithoutHwc", modelDir);
    addPortModel("WithHwc", modelDir);
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("WithHwc.java"));
    invokeTool();

    // When invoking the tool without hwc dir
    MA2JavaTool.main(new String[] {
      "--modelpath", modelDir.toString(),
      "--output", javaOutDir.toString(),
      "--report", reportOutDir.toString()
    });

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "WithHwcTOP.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "WithHwc.java", List.of(ENTRY_CREATE),
      "WithoutHwc.java", List.of(ENTRY_CREATE)
    ));
  }

  @Test
  void testWithDse() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addPortModel("Untouched", modelDir);
    addPortModel("ToRemove", modelDir);
    addPortModel("Modified", modelDir);
    addPortModel("WithHwcChange", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DSEUntouched.java"));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DSEToRemove.java"));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DSEModified.java"));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DSEWithHwcChange.java"));
    invokeTool(true);

    // When
    addPortModel("Added", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DSEAdded.java"));
    Files.delete(modelDir.resolve(usedPackageAsPath).resolve("ToRemove.arc"));
    Files.delete(hwcDir.resolve(usedPackageAsPath).resolve("DSEWithHwcChange.java"));
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("Modified.arc"),
      "package " + usedPackage + "; component Modified { port out int i; }"
    );
    invokeTool(true);

    // Then
    Map<String, List<WatchEvent.Kind<?>>> expectedEvents4Untouched = Map.of(
      "Untouched.java", List.of(ENTRY_CREATE),
      "DSEUntouchedTOP.java", List.of(ENTRY_CREATE),
      "DSEMainUntouched.java", List.of(ENTRY_CREATE),
      "ListerExpressionUntouched.java", List.of(ENTRY_CREATE),
      "ListerExprOutUntouched.java", List.of(ENTRY_CREATE),
      "ListerInUntouched.java", List.of(ENTRY_CREATE),
      "ListerOutUntouched.java", List.of(ENTRY_CREATE),
      "ListerParameterUntouched.java", List.of(ENTRY_CREATE)
    );

    Map<String, List<WatchEvent.Kind<?>>> expectedEvents4ToRemove = Map.of(
      "ToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "DSEToRemoveTOP.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "DSEMainToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "ListerExpressionToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "ListerExprOutToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "ListerInToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "ListerOutToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "ListerParameterToRemove.java", List.of(ENTRY_CREATE, ENTRY_DELETE)
    );

    Map<String, List<WatchEvent.Kind<?>>> expectedEvents4Modified = Map.of(
      "Modified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "DSEModifiedTOP.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "DSEMainModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerExpressionModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerExprOutModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerInModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerOutModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerParameterModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    );

    Map<String, List<WatchEvent.Kind<?>>> expectedEvents4WithHwcChanges = Map.of(
      "WithHwcChange.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "DSEWithHwcChangeTOP.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "DSEWithHwcChange.java", List.of(ENTRY_CREATE),
      "DSEMainWithHwcChange.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerExpressionWithHwcChange.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerExprOutWithHwcChange.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerInWithHwcChange.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerOutWithHwcChange.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ListerParameterWithHwcChange.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    );

    Map<String, List<WatchEvent.Kind<?>>> expectedEvents4Added = Map.of(
      "Added.java", List.of(ENTRY_CREATE),
      "DSEAddedTOP.java", List.of(ENTRY_CREATE),
      "DSEMainAdded.java", List.of(ENTRY_CREATE),
      "ListerExpressionAdded.java", List.of(ENTRY_CREATE),
      "ListerExprOutAdded.java", List.of(ENTRY_CREATE),
      "ListerInAdded.java", List.of(ENTRY_CREATE),
      "ListerOutAdded.java", List.of(ENTRY_CREATE),
      "ListerParameterAdded.java", List.of(ENTRY_CREATE)
    );

    Map<String, List<WatchEvent.Kind<?>>> allExpectedEvents = new HashMap<>();
    allExpectedEvents.putAll(expectedEvents4Untouched);
    allExpectedEvents.putAll(expectedEvents4ToRemove);
    allExpectedEvents.putAll(expectedEvents4Modified);
    allExpectedEvents.putAll(expectedEvents4WithHwcChanges);
    allExpectedEvents.putAll(expectedEvents4Added);

    assertFileEventsToBeExactly(fileWatcher, allExpectedEvents);
  }

  // target is moved

  private void assertFileEventsToBeExactly(WatchService fileWatch,
                                           Map<String, List<WatchEvent.Kind<?>>> expectedEvents
  ) throws InterruptedException {
    this.assertFileEventsToBeExactlyByPath(fileWatch,
      expectedEvents.entrySet().stream().collect(Collectors.toMap(
        entry -> Path.of(entry.getKey()),
        Map.Entry::getValue
      )));
  }
  private void assertFileEventsToBeExactlyByPath(WatchService fileWatch,
                                           Map<Path, List<WatchEvent.Kind<?>>> expectedEvents
  ) throws InterruptedException {
    // First get actual events from the file watcher and insert them into the multi-map
    Map<Path, List<WatchEvent.Kind<?>>> actualEvents = new HashMap<>();

    WatchKey key = fileWatch.take();
    for (WatchEvent<?> event : key.pollEvents()) {
      if (Path.class.isAssignableFrom(event.kind().type())) {
        Path fileOfEvent = (Path) event.context();

        if (!actualEvents.containsKey(event.context())) {
          actualEvents.put(fileOfEvent, new ArrayList<>());
        }

        actualEvents.get(fileOfEvent).add(event.kind());

      } else {
        throw new IllegalStateException("Unexpected event type: " + event.kind().type());
      }
    }

    // Compare actual events to expected events
    // 1) assert that (any) events occurred for exactly the specified files
    Assertions.assertThat(actualEvents.keySet())
      .as("Actually changed files")
      .containsExactlyInAnyOrderElementsOf(expectedEvents.keySet());

    // Check that for every file the exactly expected eevents occured
    Assertions.assertThat(expectedEvents.entrySet()).allSatisfy(
      entry -> Assertions.assertThat(actualEvents.get(entry.getKey()))
        .as("File " + entry.getKey() + " events")
        .containsExactlyElementsOf(entry.getValue()));
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
