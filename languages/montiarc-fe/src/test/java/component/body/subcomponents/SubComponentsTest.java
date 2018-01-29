/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package component.body.subcomponents;

import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import contextconditions.AbstractCoCoTest;
import contextconditions.AbstractCoCoTestExpectedErrorInfo;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.ComponentInstanceNamesAreUnique;
import montiarc.cocos.ComponentWithTypeParametersHasInstance;
import montiarc.cocos.SubcomponentParametersCorrectlyAssigned;

/**
 * This class checks all context conditions related to the definition of
 * subcomponents
 *
 * @author Andreas Wortmann
 */
public class SubComponentsTest extends AbstractCoCoTest {
  
  private static final String MP = "";
  
  private static final String PACKAGE = "component.body.subcomponents";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSubcomponentParametersOfWrongType() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "SubcomponentParametersOfWrongType");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new SubcomponentParametersCorrectlyAssigned()),
        node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA064"));
  }
  
  @Test
  public void testComponentInstanceNamesAmbiguous() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ComponentInstanceNamesAmbiguous");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentInstanceNamesAreUnique()),
        node,
        new AbstractCoCoTestExpectedErrorInfo(2, "xMA061"));
  }
  
  @Test
  public void testComponentWithTypeParametersLacksInstance() {
    ASTMontiArcNode node = getAstNode(MP, PACKAGE + "." + "ComponentWithTypeParametersLacksInstance");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new ComponentWithTypeParametersHasInstance()),
        node, new AbstractCoCoTestExpectedErrorInfo(1, "xMA009"));
  }
  
  @Test
  public void testReferencedSubComponentsExists() {
    checkValid(MP, PACKAGE + "." + "ReferencedSubComponentsExists");
  }
  
  @Test
  /**
   * Symbol table already throws an exception, therefore the coco is never
   * checked. A fix" + would be to stop the symbol table from throwing the
   * exception, in order to have a" + better error message. For now we just
   * check that we give out the rudimentary error xA1038, which tells us that
   * the non-existent component could not be loaded, but doesn't provide more
   * detail.
   */
  public void testInexistingSubComponent() {
    Log.getFindings().clear();
    getAstNode(MP, PACKAGE + "." + "InexistingSubComponent");
    AbstractCoCoTestExpectedErrorInfo.setERROR_CODE_PATTERN(Pattern.compile("x[0-9A-F]{5}"));
    AbstractCoCoTestExpectedErrorInfo errors = new AbstractCoCoTestExpectedErrorInfo(2, "xA1038");
    errors.checkExpectedPresent(Log.getFindings(), "No errors found!");
    AbstractCoCoTestExpectedErrorInfo.reset();
  }
}
