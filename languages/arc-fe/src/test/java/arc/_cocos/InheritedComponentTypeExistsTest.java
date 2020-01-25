/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTComponentBody;
import arc._ast.ArcMill;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymTabMill;
import arc._symboltable.ArcSymbolTableCreator;
import arc.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link InheritedComponentTypeExists}.
 */
public class InheritedComponentTypeExistsTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType parent = ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
      ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build()).build();
    ASTComponent ast = ArcMill.componentBuilder().setName("B")
      .setHead(ArcMill.componentHeadBuilder().setParentComponent(parent).build())
      .setBody(Mockito.mock(ASTComponentBody.class)).build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(scope);
    symTab.handle(ast);
    InheritedComponentTypeExists coco = new InheritedComponentTypeExists();
    coco.check(ast);
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT });
  }
}