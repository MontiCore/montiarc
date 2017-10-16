package contextconditions;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.adapters.CDTypeSymbol2JavaType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class AutomatonConventionsTest extends AutomatonAbstractCocoTest {
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMultipleBehaviorsImplemented() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.MultipleBehaviors");
    checkInvalid(node, new ExpectedErrorInfo(2, "xAB140"));
  }

  @Test
  public void testLowerCaseAutomatonName() {
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.LowerCaseAutomaton");
    checkInvalid(node, new ExpectedErrorInfo(1, "xAB130"));
  }

  @Test
  public void testLowerCaseEnumeration() {
    Log.getFindings().clear();
    Scope symTab = createSymTab(MODEL_PATH);
    symTab.<CDFieldSymbol> resolve("contextconditions.invalid.LowerCaseEnumeration.lowerCaseEnumeration.A", CDFieldSymbol.KIND).orElse(null);
    new ExpectedErrorInfo(1, "xC4A05").checkFindings(Log.getFindings());
  }

  @Test
  public void testImplementationInNonAtomicComponent() {
    checkInvalid(getAstNode("src/test/resources", "contextconditions.invalid.ImplementationInNonAtomicComponent"), new ExpectedErrorInfo(1, "xAB141"));
  }
  
  @Ignore
  @Test
  public void testInvalidCDImplicit() {
    // TODO symbols are only loaded if they are resolved. Therefore it is not
    // easily possible to perform CoCo checks to detect errors in CDs that are
    // imported in the given MAA model.
    //Todo: Just rewrite this. Contextconditions/invalid/InvalidCD.arc
    ASTMontiArcNode node = getAstNode(MODEL_PATH, "contextconditions.invalid.InvalidCD");
    ComponentSymbol sym = (ComponentSymbol) node.getSymbol().orElse(null);
    Scope symTab = createSymTab(MODEL_PATH);
    if (sym == null){
      System.out.println("NULL OH NO!");
    } else{
      List<ImportStatement> imports = sym.getImports();


        Optional<JTypeSymbol> symbol = symTab.<JTypeSymbol> resolve("Car", JTypeSymbol.KIND);
        if (symbol.isPresent()){
          System.out.println("yay");
        }
        symbol = symTab.<JTypeSymbol> resolve("MyError", JTypeSymbol.KIND);
        if (symbol.isPresent()){
          System.out.println("yay2");
        }

      Optional<CDTypeSymbol2JavaType> data = symTab.<CDTypeSymbol2JavaType> resolve("symboltable.aggregation.Types.",CDTypeSymbol2JavaType.KIND);
      if (data.isPresent()){
        System.out.println("bla");
      }

       Optional<CDTypeSymbol2JavaType> data2 = symTab.<CDTypeSymbol2JavaType> resolve("Types", CDTypeSymbol2JavaType.KIND);

        if (data2.isPresent()){
          System.out.println("bla2");
        }


    }

  }
}