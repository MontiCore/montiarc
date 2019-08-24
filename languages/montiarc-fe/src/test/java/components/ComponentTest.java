/* (c) https://github.com/MontiCore/monticore */
package components;

import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.HCJavaDSLTypeResolver;
import de.monticore.symboltable.Symbol;
import freemarker.template.utility.StringUtil;
import montiarc._ast.ASTPort;
import montiarc._symboltable.PortSymbol;
import montiarc.cocos.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;

import static org.junit.Assert.*;

/**
 * This class checks all context conditions related the combination of elements in component bodies
 *
 * @author Andreas Wortmann
 */
public class ComponentTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  /*
   * Checks whether there is a redundant import statements.
   * For example:
   *  import a.*;
   *  import a.*;
   */
  public void testRedundantImports() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "."
                                                + "RedundantImports");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
                                    .addCoCo(new ImportsAreUnique());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2,
        "xMA074"));
  }

  @Test
  /*
   * Checks whether there is a redundant import statements.
   * For example:
   *  import a.*;
   *  import a.*;
   */
  public void testRedundantImports2() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "."
                                                + "RedundantImports2");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker()
                                    .addCoCo(new ImportsAreUnique());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2,
        "xMA074"));
  }

  @Test
  @Ignore("Reenable with new JavaDSL version. Type Resolver is broken and throws an exception on astNode.getType().accept(typeResolver). Tests also appears to be incomplete.")
  /* Checks whether the namespace hiding is working */
  public void testNameSpaceHiding() {
    checkValid(PACKAGE + "." + "NameSpaceHiding"); // Components are valid

    // Check that resolving 'foo' and 'sIn' result in the correct Symbols
    // of type 'java.lang.Boolean'
    final ComponentSymbol nameSpaceHiding = loadComponentSymbol(PACKAGE, "NameSpaceHiding");
    final Optional<PortSymbol> sIn = nameSpaceHiding.getSpannedScope().resolve("sIn", PortSymbol.KIND);
    assertTrue(sIn.isPresent());
    final ASTPort astNode = (ASTPort) sIn.get().getAstNode().get();
    final HCJavaDSLTypeResolver typesVisitor = new HCJavaDSLTypeResolver();
    assertNotNull(astNode.getType());
    astNode.getType().accept(typesVisitor);
    assertTrue(typesVisitor.getResult().isPresent());
    final JavaTypeSymbolReference javaTypeSymbolReference = typesVisitor.getResult().get();
  }

  @Test
  /**
   * This test should check whether the Context condition PackageLowerCase is working.
   */
  public void testPackageUpperCase() {
    final String modelName = PACKAGE + ".UpperCasePackageName." + "UpperCasePackageName";
    MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new PackageLowerCase());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA054");
    checkInvalid(cocos, loadCompilationUnitAST(modelName), errors);
  }

  /**
   * This test should check whether the Context condition IdentifiersAreUnique is working.
   */
  @Test
  public void testUniqueNamesDifferentSymboltypes() {
    final String modelName = PACKAGE + "." + "UniqueNamesDifferentSymboltypes";
    MontiArcCoCoChecker cocos
        = new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique());
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(4, "xMA052", "xMA053", "xMA061", "xMA069");
    checkInvalid(cocos, loadComponentAST(modelName), errors);
  }

  @Test
  public void testImportComponentFromJar() {
     Scope symtab = MONTIARCTOOL.initSymbolTable(Paths.get(MODEL_PATH).toFile(),
        Paths.get(FAKE_JAVA_TYPES_PATH).toFile(), Paths.get(MODEL_PATH+"components/componentInJar.jar").toFile());
     Optional<ComponentSymbol> comp = symtab.<ComponentSymbol> resolve("components.ComponentFromJar", ComponentSymbol.KIND);
     assertTrue(comp.isPresent());
     assertTrue(comp.get().getSubComponent("cmp").isPresent());
  }

  @Test
  public void testCDType2JavaType() {
    Scope symTab = this.loadDefaultSymbolTable();
    CDTypeSymbol cdType = symTab
        .<CDTypeSymbol> resolve("types.Datatypes.MotorCommand", CDTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(cdType);
    JavaTypeSymbol javaType = symTab
        .<JavaTypeSymbol> resolve("types.Datatypes.MotorCommand", JavaTypeSymbol.KIND)
        .orElse(null);
    assertNotNull(javaType);
  }
  
  @Test
  public void testCDField2JavaField() {
    Scope symTab = this.loadDefaultSymbolTable();
    CDFieldSymbol cdField = symTab
        .<CDFieldSymbol> resolve("types.Datatypes.MotorCommand.STOP", CDFieldSymbol.KIND)
        .orElse(null);
    assertNotNull(cdField);
    JavaFieldSymbol javaField = symTab.<JavaFieldSymbol> resolve(
        "types.Datatypes.MotorCommand.STOP", JavaFieldSymbol.KIND).orElse(null);
    assertNotNull(javaField);
  }

  @Test
  public void testStereotypes() {
    checkValid(PACKAGE + "." + "Stereotypes");
  }
  
  @Test
  public void testTopLevelComponentHasInstanceName() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "TopLevelComponentHasInstanceName");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new TopLevelComponentHasNoInstanceName()),
        node,
        new ExpectedErrorInfo(1, "xMA007"));
  }
  
  @Test
  public void testResolveJavaDefaultTypes() {
    Scope symTab = this.loadDefaultSymbolTable();
    
    Optional<JTypeSymbol> javaType = symTab.resolve("String", JTypeSymbol.KIND);
    assertFalse(
        "java.lang types may not be resolvable without qualification in general (e.g., global scope).",
        javaType.isPresent());
    
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        PACKAGE + ".body.subcomponents." + "ComponentWithNamedInnerComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    
    // java.lang.*
    javaType = comp.getSpannedScope().resolve("String", JTypeSymbol.KIND);
    assertTrue("java.lang types must be resolvable without qualification within components.",
        javaType.isPresent());
    
    // java.util.*
    javaType = comp.getSpannedScope().resolve("Set", JTypeSymbol.KIND);
    assertTrue("java.util types must be resolvable without qualification within components.",
        javaType.isPresent());
    
  }

  @Test
  public void testUnusedImports() {
    final ASTMontiArcNode node = loadCompilationUnitAST(PACKAGE + "." + "UnusedImports");
    ExpectedErrorInfo errors = new ExpectedErrorInfo(3,"xMA011");
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker().addCoCo(new UnusedImports());

    checkInvalid(checker, node, errors);
  }
  
  @Test
  public void testAmbiguousPortAndComponentTypes() {
    final ASTMontiArcNode node = loadCompilationUnitAST(PACKAGE +"." + "AmbiguousPortAndComponentTypes");
    ExpectedErrorInfo errorInfo = new ExpectedErrorInfo(4, "xMA040");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AmbiguousTypes()),node, errorInfo);
  }
  
  @Test
  public void testAmbiguousNamedCD(){
    final ASTMontiArcNode node = loadCompilationUnitAST(PACKAGE +"." + "AmbiguousNamedCD.AmbiguousClass");
    ExpectedErrorInfo errorInfo = new ExpectedErrorInfo(1, "xMA012");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AmbiguousTypes()),node, errorInfo);
  }
  
  @Test
  public void testAmbiguousModel(){
    final ASTMontiArcNode node = loadCompilationUnitAST(PACKAGE +"." + "AmbiguousModel");
    ExpectedErrorInfo errorInfo = new ExpectedErrorInfo(1, "xMA012");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new AmbiguousTypes()),node, errorInfo);
  }
}
