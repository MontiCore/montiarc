package components.body;

import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.java.JavaDSLTool;
import de.monticore.java.javadsl._parser.JavaDSLParser;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.se_rwth.commons.logging.Log;
import infrastructure.AbstractCoCoTest;
import infrastructure.ExpectedErrorInfo;
import junit.framework.Assert;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc.cocos.IdentifiersAreUnique;
import montiarc.cocos.MontiArcCoCos;
import montiarc.cocos.MultipleBehaviorImplementation;
import montiarc.cocos.NamesCorrectlyCapitalized;

/**
 * This class checks all context conditions related the combination of elements
 * in component bodies
 *
 * @author Andreas Wortmann
 */
public class BodyTest extends AbstractCoCoTest {
  
  private static final String PACKAGE = "components.body";
  
  @BeforeClass
  public static void setUp() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComponentWithAJavaAndAutomaton() {
    final String modelName = PACKAGE + "." + "ComponentWithAJavaAndAutomaton";
    final MontiArcCoCoChecker checker
        = new MontiArcCoCoChecker().addCoCo(new MultipleBehaviorImplementation());
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(1, "xMA050");
    checkInvalid(checker, loadComponentAST(modelName), errors);
  }

  @Test
  public void testWrongCapitalization() {
    final ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "wrongCapitalization");
    final ExpectedErrorInfo errors = new ExpectedErrorInfo(2, "xMA055", "xMA077"); // Add error info
    final MontiArcCoCoChecker checker = new MontiArcCoCoChecker().addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized());
    checkInvalid(checker, node, errors);
  }

  @Test
  /*
   * Tests
   * [Wor16] AU3: The names of all inputs, outputs, and variables
   *  are unique. (p. 98. Lst. 5.10)
   * [Hab16] B1: All names of model elements within a component
   *  namespace have to be unique. (p. 59. Lst. 3.31)
   * [Wor16] MU1: The name of each component variable is unique
   *  among ports, variables, and configuration parameters. (p. 54 Lst. 4.5)
   */
  public void testAmbiguousPortAndVariableNames() {
    final String qualifiedModelName = PACKAGE + "." + "AmbiguousPortAndVariableNames";
    final MontiArcCoCoChecker checker = MontiArcCoCos.createChecker();
    final ExpectedErrorInfo errors
        = new ExpectedErrorInfo(14,
        "xMA035", "xMA053");
    checkInvalid(checker, loadComponentAST(qualifiedModelName), errors);
  }
  
  @Test
  public void testAmbiguousIdentifiers() {
    ASTMontiArcNode node = loadComponentAST(PACKAGE + "." + "AmbiguousIdentifiers");
    checkInvalid(new MontiArcCoCoChecker().addCoCo(new IdentifiersAreUnique()), node,
        new ExpectedErrorInfo(3, "xMA061", "xMA053"));
  }
  
}
