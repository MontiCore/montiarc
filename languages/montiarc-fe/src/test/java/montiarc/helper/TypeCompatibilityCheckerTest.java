package montiarc.helper;

import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TypeCompatibilityCheckerTest extends AbstractCoCoTest {

  @Test
  @Ignore
  public void hasNestedGenerics() {
    ComponentSymbol componentSymbol
        = loadComponentSymbol("components.body.ports", "PortCompatibilityWithGenerics");

    final Optional<PortSymbol> listListBoolIn = componentSymbol.getPort("listListBoolIn");
    assertTrue(listListBoolIn.isPresent());

    final boolean result = TypeCompatibilityChecker.hasNestedGenerics(listListBoolIn.get().getTypeReference());
    assertFalse("Port should have no nested generics", result);
  }

  @Test
  @Ignore
  public void doTypesMatch() {

    JTypeReference<? extends JTypeSymbol> sourceType = null;
    List<JTypeSymbol> sourceTypeFormalParams = null;
    List<JTypeReference<? extends JTypeSymbol>> sourceTypeTypeArguments = null;
    JTypeReference<? extends JTypeSymbol> targetType = null;
    List<JTypeSymbol> targetTypeFormalParams = null;
    List<JTypeReference<? extends JTypeSymbol>> targetTypeActualArguments = null;
    boolean result = false;

    result = TypeCompatibilityChecker.doTypesMatch(
        sourceType,
        sourceTypeFormalParams,
        sourceTypeTypeArguments,
        targetType,
        targetTypeFormalParams,
        targetTypeActualArguments);
    assertTrue("Matching types are not recognized as such.", result);
  }
}