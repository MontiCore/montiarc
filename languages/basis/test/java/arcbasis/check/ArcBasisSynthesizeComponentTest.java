/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ArcBasisSynthesizeComponentTest extends ArcBasisAbstractTest {

  @Test
  public void shouldSynthesizeFromMCQualifiedType() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(compSym);
    ArcBasisMill.globalScope().addSubScope(compSym.getSpannedScope());

    // Now build the qualified type
    ASTMCQualifiedType astComp = createQualifiedType(compName);
    astComp.setEnclosingScope(ArcBasisMill.globalScope());

    ArcBasisSynthesizeComponent synth = new ArcBasisSynthesizeComponent();

    // When
    Optional<CompKindExpression> result = synth.synthesizeFrom(astComp);

    // Then
    Assertions.assertTrue(result.isPresent());
    Assertions.assertTrue(result.get() instanceof TypeExprOfComponent);
    Assertions.assertEquals(compSym, result.get().getTypeInfo());
  }

  @Test
  public void shouldNotSynthesizeFromUnresolvableType() {
    // Now build the qualified type
    ASTMCQualifiedType astComp = createQualifiedType("Unresolvable");
    astComp.setEnclosingScope(ArcBasisMill.globalScope());

    ArcBasisSynthesizeComponent synth = new ArcBasisSynthesizeComponent();

    // When
    Optional<CompKindExpression> result = synth.synthesizeFrom(astComp);

    // Then
    Assertions.assertFalse(result.isPresent());
  }
}
