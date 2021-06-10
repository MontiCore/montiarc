/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cd2pojo.cocos.CDAssociationNamesUnique;
import de.monticore.cd2pojo.cocos.CDEllipsisParametersOnlyInLastPlace;
import de.monticore.cd2pojo.cocos.CDRoleNamesUnique;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class MultipleClassDiagramsTest {

  protected static final String MODEL_PATH = "src/test/resources/models/multipleDiagrams/";

  @BeforeAll
  public static void setupLog() {
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void clearLog() {
    Log.clearFindings();
  }

  @BeforeAll
  public static void init() {
    CD4CodeMill.init();
  }

  @Test
  public void multipleClassDiagramsShouldNotConfuseTool() {
    //Given
    CD4CodeMill.globalScope().setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
    CD4CodeCoCoChecker checker = new CD4CodeCoCos().getCheckerForAllCoCos();
    checker.addCoCo(new CDAssociationNamesUnique());
    checker.addCoCo(new CDRoleNamesUnique());
    checker.addCoCo(new CDEllipsisParametersOnlyInLastPlace());

    //When
    POJOGeneratorTool.processModels(CD4CodeMill.globalScope(), checker);

    //Then
    Assertions.assertEquals(0, Log.getFindings().stream().filter(Finding::isError).count());
  }
}