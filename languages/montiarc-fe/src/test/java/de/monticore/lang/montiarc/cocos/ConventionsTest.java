/**
 *
 */
package de.monticore.lang.montiarc.cocos;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Arne Haber
 * @date 08.02.2010
 */
public class ConventionsTest extends AbstractCoCoTest {

  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }

  @Ignore("implement coco")
  @Test
  public void testComponentConventions()
      throws RecognitionException, IOException {
    // runChecker("arc/coco/conventions/conv/violatesComponentNaming.arc");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
    // runChecker("arc/coco/conventions/conv/InnerViolatesComponentNaming.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testReferenceConventions() {
    runCheckerWithSymTab("arc/coco/conventions", "conv.ReferencesViolateNamingConventions");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testPortConvention() {
    // runChecker("arc/coco/conventions/conv/PortViolatesNamingConventions.arc");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testImportConvention() {
    // runChecker("arc/coco/conventions/conv/UnuniqueImports.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testWrongConnector() {
    // runChecker("arc/coco/conventions/conv/WrongConnector.arc");
    assertEquals(4, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testConnectorSourceAndTargetDifferentComponent() {
    // runChecker("arc/coco/conventions/conv/ConnectorSourceAndTargetSameComponent.arc");
    assertEquals(2, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Ignore("implement coco")
  @Test
  public void testMissingSourceAndTargetDefinition() {
    runCheckerWithSymTab("arc/coco/conventions", "conv.MissingSourceTargetDefinition");
    assertEquals(4, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

  @Test
  public void testUnusedPorts() {
    runCheckerWithSymTab("arc/coco/conventions", "conv.UnusedPorts");
    Collection<String> findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xAC006") || s.contains("xAC007"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());

    findings = Log.getFindings().stream().map(f -> f.buildMsg())
        .filter(s -> s.contains("xAC008") || s.contains("xAC009"))
        .collect(Collectors.toList());
    assertEquals(findings.stream().collect(Collectors.joining("\n")), 3, findings.size());
  }

  @Ignore("implement coco")
  @Test
  public void testOuterComponentWithInstanceName() {
    // runChecker("arc/coco/conventions/conv/OuterComponentWithInstanceName.arc");
    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains("xTODO"))
        .count());
  }

}
