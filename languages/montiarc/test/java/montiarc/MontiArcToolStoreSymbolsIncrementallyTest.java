/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * Tests the inc-check mechanics of the serialization of symbols of the MontiArcTool.
 */
class MontiArcToolStoreSymbolsIncrementallyTest extends MontiArcAbstractTest {

  @TempDir Path tempDir;
  WatchService fileWatcher;
  Path modelDir;
  Path symbolsOutDir;
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
    symbolsOutDir = Files.createDirectory(tempDir.resolve("out-symbols"));
    reportOutDir = Files.createDirectory(tempDir.resolve("out-report"));

    Files.createDirectories(symbolsOutDir.resolve(usedPackageAsPath));
    symbolsOutDir.resolve(usedPackageAsPath).register(fileWatcher, eventsToMonitor);
  }

  private void invokeTool() {
    MontiArcTool.main(new String[] {
      "--modelpath", modelDir.toString(),
      "--symboltable", symbolsOutDir.toString(),
      "--report", reportOutDir.toString()
    });
  }

  @Test
  void testStoreSymbolsCreatesFiles() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addModel( "Foo", modelDir);
    addModel("Bar", modelDir);

    // When
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.arcsym", List.of(ENTRY_CREATE),
      "Bar.arcsym", List.of(ENTRY_CREATE)
    ));
  }

  @Test
  void testNoModification() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    invokeTool();

    // When
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.arcsym", List.of(ENTRY_CREATE)  // Only one create event from the first run
    ));
  }

  @Test
  void testAddedFile() throws IOException, InterruptedException{
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    invokeTool();

    // When
    addModel("AddedComp", modelDir);
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.arcsym", List.of(ENTRY_CREATE),  // Only one create event from the first run
      "AddedComp.arcsym", List.of(ENTRY_CREATE)  // Create during second run
    ));
  }

  @Test
  void testRemoval() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("CompToRemove", modelDir);
    invokeTool();

    // When
    Files.delete(modelDir.resolve(usedPackageAsPath).resolve("CompToRemove.arc"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.arcsym", List.of(ENTRY_CREATE),  // Should stay
      "CompToRemove.arcsym", List.of(ENTRY_CREATE, ENTRY_DELETE)
    ));
  }

  @Test
  void testUpdate() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.writeString(modelDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arc"),
      "package " + usedPackage + "; component CompToUpdate { port out int i; }"
    );
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.arcsym", List.of(ENTRY_CREATE),  // Should stay
      "CompToUpdate.arcsym", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testRemovalOfGeneratedFile() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("CompToUpdate", modelDir);
    invokeTool();

    // When
    Files.delete(symbolsOutDir.resolve(usedPackageAsPath).resolve("CompToUpdate.arcsym"));
    invokeTool();

    // Then
    assertFileEventsToBeExactly(fileWatcher, Map.of(
      "Foo.arcsym", List.of(ENTRY_CREATE),  // Should stay
      "CompToUpdate.arcsym", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testMovingOfModelDir() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addModel("Foo", modelDir);
    addModel("Modified", modelDir);
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
      "Foo.arcsym", List.of(ENTRY_CREATE),  // Should not re-generate
      "Modified.arcsym", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  @Disabled
  void testMultipleModelPaths() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    addModel("Unchanged", modelDir);
    addModel("UnchangedButMoved", modelDir);
    addModel("ChangedAndMoved", modelDir);
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
      "Unchanged.arcsym", List.of(ENTRY_CREATE),  // Not changed by second run
      "UnchangedButMoved.arcsym", List.of(ENTRY_CREATE),  // Not changed by second run
      "ChangedAndMoved.arcsym", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
    ));
  }

  @Test
  void testNoPackage() throws IOException, InterruptedException {
    // Given
    createBasicProjectStructure();
    WatchService packageLessWatcher = FileSystems.getDefault().newWatchService();
    symbolsOutDir.register(packageLessWatcher, eventsToMonitor);

    Files.writeString(modelDir.resolve("NoPackage.arc"), "component NoPackage { port in int i; }");
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port in int i; }");
    invokeTool();

    // When
    Files.writeString(modelDir.resolve("NoPackageModified.arc"), "component NoPackageModified { port out int i; }");
    invokeTool();

    // Then
    assertFileEventsToBeExactly(packageLessWatcher, Map.of(
      "NoPackage.arcsym", List.of(ENTRY_CREATE),
      "NoPackageModified.arcsym", List.of(ENTRY_CREATE, ENTRY_DELETE, ENTRY_CREATE)
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
    // First get actual events
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
    Assertions.assertThat(actualEvents.keySet())
      .as("Actually changed files")
      .containsExactlyInAnyOrderElementsOf(expectedEvents.keySet());

    Assertions.assertThat(expectedEvents.entrySet()).allSatisfy(
      entry -> Assertions.assertThat(actualEvents.get(entry.getKey()))
        .as("File " + entry.getKey() + " events")
        .containsExactlyElementsOf(entry.getValue()));
  }

  private void addModel(String modelName, Path modelDirToUse) throws IOException {
    Path packagePathQualified = modelDirToUse.resolve(usedPackageAsPath);

    Files.createDirectories(packagePathQualified);
    Files.writeString(packagePathQualified.resolve(modelName + ".arc"),
      String.format("package %s; component %s { port in int i; }", usedPackage, modelName)
    );
  }
}
