/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.head.parameters;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc.cocos.IdentifiersAreUnique;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.NamesCorrectlyCapitalized;
import montiarc.cocos.SubcomponentParametersCorrectlyAssigned;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class tests all context conditions related to parameters
 * 
 * @author Crispin Kirchner, Andreas Wortmann
 */
public class ParametersTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.head.parameters";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParameterNamesUniqueTestValid() {
    checkValid(PACKAGE + "." + "ParameterNamesUnique");
  }
  
  @Test
  public void testInvalidConfigArgs() {
    final MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new SubcomponentParametersCorrectlyAssigned());
    final ASTMontiArcNode node
        = loadComponentAST(PACKAGE + "." + "InvalidConfigArgs");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA065");
    checkInvalid(cocos, node, errors);
  }

  @Test
  public void testParameterAmbiguous() {
    final String modelName = PACKAGE + "." + "ParameterAmbiguous";
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA069");
    final MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void testComponentWithParameters() {
    String unqualifiedComponentName = "ComponentWithParameters";
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, unqualifiedComponentName);
    
    assertFalse(comp.isInnerComponent());
    assertEquals(1, comp.getConfigParameters().size());
    assertEquals(0, comp.getFormalTypeParameters().size());
  }
  
  @Test
  public void testEnumAsTypeArgument() {
    checkValid(PACKAGE+"."+"EnumAsTypeArg");
  }


  @Test
  public void testCompWithIntegerParameter() {
    checkValid(PACKAGE + "." + "CompWithIntegerParameter");
  }

  @Test
  public void testCompWithInterfaceParam() {
    checkValid(PACKAGE + "." + "CompWithInterfaceParam");
  }

  @Test
  public void testUseEnumAsTypeArg() {
    checkValid(PACKAGE + "." + "UseEnumAsTypeArg");
  }

  @Test
  @Ignore("Qualified enum type which is used as a component argument can" +
              "not be found.")
  /*
   * java.lang.AssertionError: 0xA1038 SymbolReference Could not load full
   * information of 'types' (Kind de.monticore.java.symboltable.JavaTypeSymbolKind).
   * StringReader:<10,40>: 0xMA065 Could not find type of argument no 0 of
   * subcomponent sub
   */
  public void testUseEnumAsTypeArgQualified() {
    checkValid(PACKAGE + "." + "UseEnumAsTypeArgQualified");
  }

  @Test
  @Ignore("TypeCompatibilityChecker.expressionType() returns Opt.empty() on CD Enum parameter ")
  public void testUseEnumAsTypeArgFromCD() {
    checkValid(PACKAGE + "." + "UseEnumAsTypeArgFromCD");
  }
  
 /*
  * Tests [Hab16] CV1: Instance names start with a lower-case letter. (pg. 71, Lst. 3.51)
  */
  @Test
  public void testParameterNameCapitalized() {
    final String modelName = PACKAGE + "." + "ParameterNameCapitalized";
    MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker()
              .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized());
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA045");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  /*
   * Tests [Hab16] CV2: Types start with an upper-case letter. (pg. 71, Lst. 3.51)
   */
  public void testComponentTypeGenericParameterIsUpperCase() {
    final String modelName = PACKAGE + "." + "ComponentWithLowerCaseGenType";
    MontiArcCoCoChecker cocos
      = new MontiArcCoCoChecker()
            .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA049");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }

  @Test
  public void testAssignsWrongParameters() {
    final String modelName = PACKAGE + "." + "AssignsWrongParameters";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(4, "xMA064", "xMA082");
    checkInvalid(checker, loadComponentAST(modelName), expectedErrorInfo);
  }

  @Test
  public void testHasProhibitedParameterName() {
    final String modelName = PACKAGE + "." + "HasProhibitedParameterName";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo expectedErrorInfo
        = new ExpectedErrorInfo(4, "xMA046");
    checkInvalid(checker, loadComponentAST(modelName), expectedErrorInfo);
  }
}
