/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ArcBasisSynthesizeComponentTest extends ArcBasisTestBase {

  @Test
  public void shouldSynthesizeFromMCQualifiedType() {
    // Given
    String compName = "Comp";
    ArcComponentTypeSymbol compSym = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(compSym);
    ArcBasisMill.globalScope().addSubScope(compSym.getSpannedScope());

    // Now build the qualified type
    ASTMCQualifiedType astComp = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .addParts(compName)
        .build())
      .build();
    astComp.setEnclosingScope(ArcBasisMill.globalScope());

    ArcBasisSynthesizeComponent synth = new ArcBasisSynthesizeComponent();

    // When
    Optional<CompKindExpression> result = synth.synthesize(astComp);

    // Then
    Assertions.assertTrue(result.isPresent());
    Assertions.assertTrue(result.get() instanceof TypeExprOfComponent);
    Assertions.assertEquals(compSym, result.get().getTypeInfo());
  }

  @Test
  public void shouldNotSynthesizeFromUnresolvableType() {
    // Now build the qualified type
    ASTMCQualifiedType astComp = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .addParts("Unresolvable")
        .build())
      .build();
    astComp.setEnclosingScope(ArcBasisMill.globalScope());

    ArcBasisSynthesizeComponent synth = new ArcBasisSynthesizeComponent();

    // When
    Optional<CompKindExpression> result = synth.synthesize(astComp);

    // Then
    Assertions.assertFalse(result.isPresent());
  }
}
