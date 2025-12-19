/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._visitor.ArcBasisTraverser;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindCheckResult;
import de.monticore.types.check.SynthesizeCompKindFromMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.MCError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SynthesizeComponentFromMCBasicTypesTest extends ArcBasisTestBase {

  @Test
  public void shouldHandleMCQualifiedType() {
    // Given
    // First build some component type symbols which we refer to with the qualified type
    String normalCompName = "Comp1";
    ComponentTypeSymbol normalComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(normalCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(normalComp);
    ArcBasisMill.globalScope().addSubScope(normalComp.getSpannedScope());

    String qualifiedCompName = "Comp2";
    ComponentTypeSymbol qualifiedComp = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(qualifiedCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    String multipleNormalCompName = "Comp3";
    ComponentTypeSymbol multipleNormalComp1 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(multipleNormalCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(multipleNormalComp1);
    ArcBasisMill.globalScope().addSubScope(multipleNormalComp1.getSpannedScope());
    ComponentTypeSymbol multipleNormalComp2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(multipleNormalCompName)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(multipleNormalComp2);
    ArcBasisMill.globalScope().addSubScope(multipleNormalComp2.getSpannedScope());

    String nameOfQualCompScope = "scoop";
    IArcBasisScope scopeOfQualComp = ArcBasisMill.scope();
    scopeOfQualComp.setName(nameOfQualCompScope);
    scopeOfQualComp.add(qualifiedComp);
    scopeOfQualComp.addSubScope(qualifiedComp.getSpannedScope());
    ArcBasisMill.globalScope().addSubScope(scopeOfQualComp);

    // Now build the qualified type
    ASTMCQualifiedType astNormalComp = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .addParts(normalCompName)
        .build())
      .build();
    ASTMCQualifiedType astQualComp = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .addParts(nameOfQualCompScope)
        .addParts(qualifiedCompName)
        .build())
      .build();
    ASTMCQualifiedType astMultiNormalComp = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .addParts(multipleNormalCompName)
        .build())
      .build();
    astNormalComp.setEnclosingScope(ArcBasisMill.globalScope());
    astQualComp.setEnclosingScope(ArcBasisMill.globalScope());
    astMultiNormalComp.setEnclosingScope(ArcBasisMill.globalScope());

    CompKindCheckResult result4normal = new CompKindCheckResult();
    CompKindCheckResult result4qual = new CompKindCheckResult();
    CompKindCheckResult result4multi = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth4normal = new SynthesizeCompKindFromMCBasicTypes(result4normal);
    SynthesizeCompKindFromMCBasicTypes synth4qual = new SynthesizeCompKindFromMCBasicTypes(result4qual);
    SynthesizeCompKindFromMCBasicTypes synth4multi = new SynthesizeCompKindFromMCBasicTypes(result4multi);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);
    synth4multi.handle(astMultiNormalComp);

    // Then
    Assertions.assertTrue(result4normal.getResult().isPresent());
    Assertions.assertTrue(result4qual.getResult().isPresent());
    Assertions.assertTrue(result4multi.getResult().isPresent());

    Assertions.assertEquals(normalComp, result4normal.getResult().get().getTypeInfo());
    Assertions.assertEquals(qualifiedComp, result4qual.getResult().get().getTypeInfo());
    Assertions.assertTrue(
      result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp1)
        || result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp2));
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(MCError.AMBIGUOUS_COMPONENT_REFERENCE));

    assertThat(result4normal.getResult().get().getSourceNode()).contains(astNormalComp);
    assertThat(result4qual.getResult().get().getSourceNode()).contains(astQualComp);
    assertThat(result4multi.getResult().get().getSourceNode()).contains(astMultiNormalComp);

  }

  @Test
  public void shouldNotHandleMCQualifiedType() {
    // Given
    ASTMCQualifiedType astNormalComp = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
        .addParts("Foo")
        .build())
      .build();
    ASTMCQualifiedType astQualComp =
      ArcBasisMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ArcBasisMill.mCQualifiedNameBuilder()
          .addParts("qual")
          .addParts("Foo")
          .build())
        .build();
    astNormalComp.setEnclosingScope(ArcBasisMill.globalScope());
    astQualComp.setEnclosingScope(ArcBasisMill.globalScope());

    CompKindCheckResult result4normal = new CompKindCheckResult();
    CompKindCheckResult result4qual = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth4normal = new SynthesizeCompKindFromMCBasicTypes(result4normal);
    SynthesizeCompKindFromMCBasicTypes synth4qual = new SynthesizeCompKindFromMCBasicTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertFalse(result4normal.getResult().isPresent());
    Assertions.assertFalse(result4qual.getResult().isPresent());
    assertThat(Log.getFindings()).isEmpty(); // Error is only logged by ISynthesizeComponent
  }

  @Test
  public void shouldNotHandleVoidType() {
    // Given
    ASTMCVoidType voidType = ArcBasisMill.mCVoidTypeBuilder().build();
    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth = new SynthesizeCompKindFromMCBasicTypes(resultWrapper);

    // Attach a traverser to the synth, as we do not override the handle method and thus the synth tries to traverse the
    // AST. In the end this should result in an empty synth result, however, if we do not attach a traverser, this will
    // Result in an error instead.
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setMCBasicTypesHandler(synth);

    // When
    synth.handle(voidType);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
    assertThat(Log.getFindings()).isEmpty();
  }
}
