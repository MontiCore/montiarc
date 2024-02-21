/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static java.util.Map.entry;

/**
 * Tests the inc-check mechanics of the generation of the MA2JSimTool.
 */
class MA2JSimToolIncrementalityTest {

  @TempDir
  Path tempDir;
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
    MA2JSimTool.main(new String[]{
      "--modelpath", modelDir.toString(),
      "--handwritten-code", hwcDir.toString(),
      "--output", javaOutDir.toString(),
      "--report", reportOutDir.toString()
    });
  }

  @Test
  void testGenerationCreatesFiles() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    addAtomicDeployModel("Bar", modelDir);

    // When
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      entry("FooComp.java", List.of(ENTRY_CREATE)),
      entry("FooCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooContext.java", List.of(ENTRY_CREATE)),
      entry("FooAutomaton.java", List.of(ENTRY_CREATE)),
      entry("FooAutomatonBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooStates.java", List.of(ENTRY_CREATE)),
      entry("FooEvents.java", List.of(ENTRY_CREATE)),

      entry("DeployBar.java", List.of(ENTRY_CREATE)),
      entry("BarComp.java", List.of(ENTRY_CREATE)),
      entry("BarCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("BarContext.java", List.of(ENTRY_CREATE)),
      entry("BarEvents.java", List.of(ENTRY_CREATE))
    ));
  }

  @Test
  void testNoModification() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    invokeTool();

    // When
    invokeTool();

    // Then
    // Only expect one create event from the first run
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "FooComp.java", List.of(ENTRY_CREATE),
      "FooCompBuilder.java", List.of(ENTRY_CREATE),
      "FooContext.java", List.of(ENTRY_CREATE),
      "FooAutomaton.java", List.of(ENTRY_CREATE),
      "FooAutomatonBuilder.java", List.of(ENTRY_CREATE),
      "FooStates.java", List.of(ENTRY_CREATE),
      "FooEvents.java", List.of(ENTRY_CREATE)
    ));
  }

  @Test
  void testAddedFile() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    invokeTool();

    // When
    addAutomatonModel("Added", modelDir);
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Expect only one create event from the first run
      entry("FooComp.java", List.of(ENTRY_CREATE)),
      entry("FooCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooContext.java", List.of(ENTRY_CREATE)),
      entry("FooAutomaton.java", List.of(ENTRY_CREATE)),
      entry("FooAutomatonBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooStates.java", List.of(ENTRY_CREATE)),
      entry("FooEvents.java", List.of(ENTRY_CREATE)),

      // Expect only one create event from the second run
      entry("AddedComp.java", List.of(ENTRY_CREATE)),
      entry("AddedCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("AddedContext.java", List.of(ENTRY_CREATE)),
      entry("AddedAutomaton.java", List.of(ENTRY_CREATE)),
      entry("AddedAutomatonBuilder.java", List.of(ENTRY_CREATE)),
      entry("AddedStates.java", List.of(ENTRY_CREATE)),
      entry("AddedEvents.java", List.of(ENTRY_CREATE))
    ));
  }

  @Test
  void testRemoval() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    addAutomatonModel("CompToRemove", modelDir);
    invokeTool();

    // When
    Files.delete(modelDir.resolve(usedPackageAsPath).resolve("CompToRemove.arc"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should stay
      entry("FooComp.java", List.of(ENTRY_CREATE)),
      entry("FooCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooContext.java", List.of(ENTRY_CREATE)),
      entry("FooAutomaton.java", List.of(ENTRY_CREATE)),
      entry("FooAutomatonBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooStates.java", List.of(ENTRY_CREATE)),
      entry("FooEvents.java", List.of(ENTRY_CREATE)),

      // Should be deleted
      entry("CompToRemoveComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToRemoveCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToRemoveContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToRemoveAutomaton.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToRemoveAutomatonBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToRemoveStates.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToRemoveEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE))
    ));
  }

  @Test
  void testUpdate() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAutomatonModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arc"),
      "package " + usedPackage + "; component CompToUpdate { port in int i; }"
    );
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should stay
      entry("FooComp.java", List.of(ENTRY_CREATE)),
      entry("FooCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooContext.java", List.of(ENTRY_CREATE)),
      entry("FooAutomaton.java", List.of(ENTRY_CREATE)),
      entry("FooAutomatonBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooStates.java", List.of(ENTRY_CREATE)),
      entry("FooEvents.java", List.of(ENTRY_CREATE)),

      // Should be updated + it is not deployed anymore -> delete deploy file
      entry("DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToUpdateComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
    ));
  }

  @Test
  void testHwcAddition() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should stay unaffected
      entry("DeployFoo.java", List.of(ENTRY_CREATE)),
      entry("FooComp.java", List.of(ENTRY_CREATE)),
      entry("FooCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooContext.java", List.of(ENTRY_CREATE)),
      entry("FooEvents.java", List.of(ENTRY_CREATE)),

      // Should regenerate everything, but for the deploy class, delete the old and generate a TOP class
      entry("DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("DeployCompToUpdateTOP.java", List.of(ENTRY_CREATE)),
      entry("CompToUpdateComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
    ));
  }

  @Test
  void testHwcRemoval() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // When
    Files.delete(hwcDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should stay unaffected
      entry("DeployFoo.java", List.of(ENTRY_CREATE)),
      entry("FooComp.java", List.of(ENTRY_CREATE)),
      entry("FooCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooContext.java", List.of(ENTRY_CREATE)),
      entry("FooEvents.java", List.of(ENTRY_CREATE)),

      // Should regenerate everything, but for the deploy class, delete the TOP class and generate a normal one
      entry("DeployCompToUpdateTOP.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("DeployCompToUpdate.java", List.of(ENTRY_CREATE)),
      entry("CompToUpdateComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
    ));
  }

  @Test
  void testRemovalOfGeneratedFile() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.delete(javaOutDir.resolve(usedPackageAsPath).resolve("DeployCompToUpdate.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      // Should stay unaffected
      "DeployFoo.java", List.of(ENTRY_CREATE),
      "FooComp.java", List.of(ENTRY_CREATE),
      "FooCompBuilder.java", List.of(ENTRY_CREATE),
      "FooContext.java", List.of(ENTRY_CREATE),
      "FooEvents.java", List.of(ENTRY_CREATE),

      // Should regenerate everything
      "DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "CompToUpdateComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "CompToUpdateCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "CompToUpdateContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "CompToUpdateEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testMovingOfModelDir() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("Modified", modelDir);
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
      // Should remain unaffected:
      "DeployFoo.java", List.of(ENTRY_CREATE),
      "FooComp.java", List.of(ENTRY_CREATE),
      "FooCompBuilder.java", List.of(ENTRY_CREATE),
      "FooContext.java", List.of(ENTRY_CREATE),
      "FooEvents.java", List.of(ENTRY_CREATE),

      // Should be deleted and regenerated (as content changed). Moreover, Deploy should be removed
      "DeployModified.java", List.of(ENTRY_CREATE, ENTRY_DELETE),
      "ModifiedComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ModifiedCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ModifiedContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE),
      "ModifiedEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testMovingOfHwcDir() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Foo", modelDir);
    addAtomicDeployModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    hwcDir = Files.createDirectory(tempDir.resolve("new-hwc"));
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    // Also creating a hwc file for foo:
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("CompToUpdateComp.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should remain unaffected:
      entry("DeployFoo.java", List.of(ENTRY_CREATE)),
      entry("FooComp.java", List.of(ENTRY_CREATE)),
      entry("FooCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("FooContext.java", List.of(ENTRY_CREATE)),
      entry("FooEvents.java", List.of(ENTRY_CREATE)),

      // Should be deleted and regenerated (as hwc status changed). Moreover, TOP has to be respected
      entry("DeployCompToUpdate.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("CompToUpdateCompTOP.java", List.of(ENTRY_CREATE)),
      entry("CompToUpdateCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("CompToUpdateEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
    ));
  }

  @Test
  void testMultipleHwcPaths() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("HasUnchangedHwc", modelDir);
    addAtomicDeployModel("HasMovingHwc", modelDir);
    addAtomicDeployModel("WillGetNewHwc", modelDir);
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("DeployHasUnchangedHwc.java"));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java"));
    invokeTool();

    // When adding a hwc path, moving one model, and adding another hwc extension
    Path oldHwc = hwcDir;
    Path newHwc = Files.createDirectory(tempDir.resolve("new-hwc"));
    hwcDir = Path.of(hwcDir.toString() + File.pathSeparator + newHwc.toString());

    Files.createDirectories(newHwc.resolve(usedPackageAsPath));
    Files.createFile(newHwc.resolve(usedPackageAsPath).resolve("WillGetNewHwcComp.java"));
    Files.copy(
      oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java"),
      newHwc.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java")
    );
    Files.delete(oldHwc.resolve(usedPackageAsPath).resolve("HasMovingHwcComp.java"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should remain unaffected:
      entry("DeployHasUnchangedHwcTOP.java", List.of(ENTRY_CREATE)),
      entry("HasUnchangedHwcComp.java", List.of(ENTRY_CREATE)),
      entry("HasUnchangedHwcCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("HasUnchangedHwcContext.java", List.of(ENTRY_CREATE)),
      entry("HasUnchangedHwcEvents.java", List.of(ENTRY_CREATE)),

      // Should remain unaffected:
      entry("DeployHasMovingHwc.java", List.of(ENTRY_CREATE)),
      entry("HasMovingHwcCompTOP.java", List.of(ENTRY_CREATE)),
      entry("HasMovingHwcCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("HasMovingHwcContext.java", List.of(ENTRY_CREATE)),
      entry("HasMovingHwcEvents.java", List.of(ENTRY_CREATE)),

      // Changed due to change in hwc state:
      entry("DeployWillGetNewHwc.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("WillGetNewHwcComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("WillGetNewHwcCompTOP.java", List.of(ENTRY_CREATE)),
      entry("WillGetNewHwcCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("WillGetNewHwcContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("WillGetNewHwcEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
    ));
  }

  @Test
  void testMultipleModelPaths() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("Unchanged", modelDir);
    addAtomicDeployModel("UnchangedButMoved", modelDir);
    addAtomicDeployModel("ChangedAndMoved", modelDir);
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
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should remain unaffected:
      entry("DeployUnchanged.java", List.of(ENTRY_CREATE)),
      entry("UnchangedComp.java", List.of(ENTRY_CREATE)),
      entry("UnchangedCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("UnchangedContext.java", List.of(ENTRY_CREATE)),
      entry("UnchangedEvents.java", List.of(ENTRY_CREATE)),

      // Should remain unaffected:
      entry("DeployUnchangedButMoved.java", List.of(ENTRY_CREATE)),
      entry("UnchangedButMovedComp.java", List.of(ENTRY_CREATE)),
      entry("UnchangedButMovedCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("UnchangedButMovedContext.java", List.of(ENTRY_CREATE)),
      entry("UnchangedButMovedEvents.java", List.of(ENTRY_CREATE)),

      // Changed -> regeneration necessary. Moreover remove deploy class (as new model has ports)
      entry("DeployChangedAndMoved.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("ChangedAndMovedComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("ChangedAndMovedCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("ChangedAndMovedContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("ChangedAndMovedEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
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
    assertFileEventsToBeExactly(packageLessWatcher, Map.ofEntries(
      entry("NoPackageComp.java", List.of(ENTRY_CREATE)),
      entry("NoPackageCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("NoPackageContext.java", List.of(ENTRY_CREATE)),
      entry("NoPackageEvents.java", List.of(ENTRY_CREATE)),

      entry("NoPackageModifiedComp.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("NoPackageModifiedCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("NoPackageModifiedContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("NoPackageModifiedEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
    ));
  }

  @Test
  void testRemoveHwcDir() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addAtomicDeployModel("WithoutHwc", modelDir);
    addAtomicDeployModel("WithHwc", modelDir);
    Files.createDirectories(hwcDir.resolve(usedPackageAsPath));
    Files.createFile(hwcDir.resolve(usedPackageAsPath).resolve("WithHwcComp.java"));
    invokeTool();

    // When invoking the tool without hwc dir
    MA2JSimTool.main(new String[]{
      "--modelpath", modelDir.toString(),
      "--output", javaOutDir.toString(),
      "--report", reportOutDir.toString()
    });

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.ofEntries(
      // Should remain unaffected
      entry("DeployWithoutHwc.java", List.of(ENTRY_CREATE)),
      entry("WithoutHwcComp.java", List.of(ENTRY_CREATE)),
      entry("WithoutHwcCompBuilder.java", List.of(ENTRY_CREATE)),
      entry("WithoutHwcContext.java", List.of(ENTRY_CREATE)),
      entry("WithoutHwcEvents.java", List.of(ENTRY_CREATE)),

      // Should change due to changed hwc state
      entry("DeployWithHwc.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("WithHwcCompTOP.java", List.of(ENTRY_CREATE, ENTRY_DELETE)),
      entry("WithHwcComp.java", List.of(ENTRY_CREATE)),
      entry("WithHwcCompBuilder.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("WithHwcContext.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)),
      entry("WithHwcEvents.java", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE))
    ));
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
