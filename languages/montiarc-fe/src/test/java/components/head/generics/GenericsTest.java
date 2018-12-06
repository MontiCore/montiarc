/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components.head.generics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.SymbolPrinter;

/**
 * This class checks all context conditions related to the definition of generic type parameters
 *
 * @author Crispin Kirchner, Andreas Wortmann
 */
public class GenericsTest extends AbstractCoCoTest {

  private static final String PACKAGE = "components.head.generics";

  @BeforeClass
  public static void setUp() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testTypeParameterNamesUniqueValid() {
    checkValid(PACKAGE + "." + "TypeParameterNamesUnique");
  }

  @Test
  /*
   * Checks that generic type parameters have to be unique.
   */
  public void testTypeParametersNotUnique() {
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new TypeParameterNamesUnique()),
    	loadComponentAST(PACKAGE + "." + "TypeParametersNotUnique"),
        new ExpectedErrorInfo(2, "xMA006"));
  }
  
  @Test
  public void testGarage() {
    checkValid(PACKAGE + "." + "Garage");
  }
  
  @Test
  public void testSubCompExtendsGenericComparableCompValid() {
    final String modelName = PACKAGE + "." + "SubCompExtendsGenericComparableCompValid";
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ProhibitGenericsWithBounds());
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA072");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  /*
   * Tests [Hab16] R15: Components that inherit from a generic component
   * have to assign concrete type arguments to all generic type parameters.
   * (p.69, lst. 3.50)
   */
  public void testComponentExtendsGenericComponent() {
    checkValid(PACKAGE + "." + "ComponentExtendsGenericComponent");

    final String modelName = PACKAGE + "." + "ComponentExtendsGenericComponent2";
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ProhibitGenericsWithBounds());
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA072");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
    checkValid(PACKAGE + "." + "ComponentExtendsGenericComponent3");

    String fqModelName = PACKAGE + "." + "ComponentExtendsGenericComponent5";
    errors = new ExpectedErrorInfo(1, "xMA088");
    cocos = new MontiArcCoCoChecker().addCoCo(new AllGenericParametersOfSuperClassSet());
    checkInvalid(cocos, loadComponentAST(fqModelName), errors);

    fqModelName = PACKAGE + "." + "ComponentExtendsGenericComponent6";
    errors = new ExpectedErrorInfo(1, "xMA089");
    checkInvalid(cocos, loadComponentAST(fqModelName), errors);
  }
  
  @Test
  /*
   * Tests [Hab16] R15
   */
  public void testSubCompExtendsGenericCompInvalid0(){
    final String qualifiedModelName = PACKAGE + "." + "SubCompExtendsGenericCompInvalid0";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA087");
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  /*
   * Tests [Hab16] R15
   */
  public void testSubCompExtendsGenericCompInvalid1(){
    final String qualifiedModelName = PACKAGE + "." + "SubCompExtendsGenericCompInvalid1";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA088");
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  /*
   * Tests [Hab16] R15
   */
  public void testSubCompExtendsGenericCompInvalid2(){
    final String qualifiedModelName = PACKAGE + "." + "SubCompExtendsGenericCompInvalid2";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo expectedErrors = new ExpectedErrorInfo(1, "xMA089");
    checkInvalid(checker, loadComponentAST(qualifiedModelName), expectedErrors);
  }

  @Test
  public void testAssignsWrongComplexTypeArgToSuperComp() {
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final String qualifiedModelName = PACKAGE + "." + "AssignsWrongComplexTypeArgToSuperComp";
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(2, "xMA089", "xMA072");
    checkInvalid(checker, loadComponentAST(qualifiedModelName) , errors);
  }


  @Test
  public void testSubCompExtendsGenericCompValid() {
    checkValid(PACKAGE + "." + "SubCompExtendsGenericCompValid");
  }

  @Test
  public void testSuperGenericComparableComp() {
    final String modelName = PACKAGE + "." + "SuperGenericComparableComp";
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ProhibitGenericsWithBounds());
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA072");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }

  @Test
  public void testSuperGenericComparableComp2() {
    final String modelName = PACKAGE + "." + "SuperGenericComparableComp2";
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ProhibitGenericsWithBounds());
    ExpectedErrorInfo errors = new ExpectedErrorInfo(1, "xMA072");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }
  
  @Test
  public void SubSubCompExtendsGenericComparableCompValid() {
    checkValid(PACKAGE + "." + "SubSubCompExtendsGenericComparableCompValid"); 
  }
  
  @Test
  public void testUsingComplexGenericParams() {
    ComponentSymbol comp = this.loadComponentSymbol(PACKAGE, "UsingComplexGenericParams");
    
    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    
    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("cp", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("cp", delay.getName());
    
    assertEquals(2, delay.getConfigArguments().size());
    assertEquals("new int[] {1, 2, 3}",
        SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(0)));
    
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(0).getKind());
    // assertEquals("1",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(0).getValue());
    // assertEquals("2",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(1).getValue());
    // assertEquals("3",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(2).getValue());
    
    assertEquals("new HashMap<List<K>, List<V>>()",
        SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(1)));
    
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(1).getKind());
    // ArcdTypeReferenceEntry typeRef = delay.getConfigArguments().get(1).getType();
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(0).getType().getName());
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(1).getType().getName());
    // assertEquals("K",
    // typeRef.getTypeParameters().get(0).getTypeParameters().get(0).getType().getName());
    // assertEquals("V",
    // typeRef.getTypeParameters().get(1).getTypeParameters().get(0).getType().getName());
  }

  @Test
  public void testDefaultParameterForPurelyGenericType() {
    final String modelName = PACKAGE + ".DefaultParameterForPurelyGenericType";
    MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new DefaultParametersCorrectlyAssigned());
    ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA062");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }

  @Test
  public void testAssignsTypeParamsToSuperCompWithoutFormalParams() {
    final String modelName
        = PACKAGE + ".AssignsTypeParamsToSuperCompWithoutFormalParams";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA071");
    checkInvalid(checker, loadComponentAST(modelName), errors);
  }

  @Test
  @Ignore("TODO Implement checking whether types exist.")
  public void testAssignsNonExistingTypeToSuperComp() {
    final String modelName
        = PACKAGE + ".AssignsNonExistingTypeToSuperComp";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA102");
    checkInvalid(checker, loadComponentAST(modelName), errors);
  }
}
