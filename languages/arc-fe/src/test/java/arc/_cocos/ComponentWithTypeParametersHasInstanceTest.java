/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponent;
import arc._ast.ASTComponentBody;
import arc._ast.ASTComponentHead;
import arc._ast.ArcMill;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymbolTableCreator;
import arc.util.ArcError;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Holds tests for handwritten methods of {@link ComponentWithTypeParametersHasInstance}.
 */
public class ComponentWithTypeParametersHasInstanceTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldDetectNonInstantiatedComponentWithTypeParameter() {
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(new ArcScope());
    ASTComponent inner = ArcMill.componentBuilder().setName("B")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(ArcMill.componentHeadBuilder()
        .setTypeParameterList(
          Collections.singletonList(ArcMill.arcTypeParameterBuilder().setName("T").build()))
        .build())
      .build();
    ASTComponent outer = ArcMill.componentBuilder().setName("A")
      .setBody(ArcMill.componentBodyBuilder().addArcElement(inner).build())
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ComponentWithTypeParametersHasInstance coco = new ComponentWithTypeParametersHasInstance();
    symTab.handle(outer);
    coco.check(outer);
    this.checkExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.INNER_WITH_TYPE_PARAMETER_REQUIRES_INSTANCE });
  }
}