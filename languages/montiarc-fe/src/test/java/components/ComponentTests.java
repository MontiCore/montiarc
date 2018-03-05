package components;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Optional;

import montiarc.cocos.PackageLowerCase;
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
import montiarc.cocos.ImportsAreUnique;
import montiarc.cocos.TopLevelComponentHasNoInstanceName;

/**
 * This class checks all context conditions related the combination of elements in component bodies
 *
 * @author Andreas Wortmann
 */
public class ComponentTests extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  /* Checks whether there is a redundant import statements. For example import a.*; import a.*; */
  public void testRedundantImports() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "RedundantImports");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new ImportsAreUnique());
    checkInvalid(cocos, node, new ExpectedErrorInfo(2, "xMA074"));
  }

  @Test
  @Ignore("Components with upper case packages can not be resolved by the symbol table.")
  /**
   * This test should check whether the Context condition PackageLowerCase is working.
   */
  public void testPackageUpperCase() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "UpperCasePackageName");
    MontiArcCoCoChecker cocos = new MontiArcCoCoChecker().addCoCo(new PackageLowerCase());
    checkInvalid(cocos, node, new ExpectedErrorInfo(1, "xMA054"));
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
  public void testTopLevelComponentHasNoInstanceName() {
    checkValid("components.TopLevelComponentHasNoInstanceName");
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
        PACKAGE + "." + "ComponentWithNamedInnerComponent", ComponentSymbol.KIND).orElse(null);
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
  
}
