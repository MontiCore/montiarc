/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._visitor.ArcBasisTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SynthesizeComponentFromMCBasicTypesTest extends ArcBasisAbstractTest {

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
      .setFullName("A") // Workaround since the hash depends on full name and resolve only returns symbols with different hashes
      .setSpannedScope(ArcBasisMill.scope())
      .build();
    ArcBasisMill.globalScope().add(multipleNormalComp1);
    ArcBasisMill.globalScope().addSubScope(multipleNormalComp1.getSpannedScope());
    ComponentTypeSymbol multipleNormalComp2 = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(multipleNormalCompName)
      .setFullName("B") // Workaround since the hash depends on full name and resolve only returns symbols with different hashes
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
    ASTMCQualifiedType astNormalComp = createQualifiedType(normalCompName);
    ASTMCQualifiedType astQualComp = createQualifiedType(nameOfQualCompScope, qualifiedCompName);
    ASTMCQualifiedType astMultiNormalComp = createQualifiedType(multipleNormalCompName);
    astNormalComp.setEnclosingScope(ArcBasisMill.globalScope());
    astQualComp.setEnclosingScope(ArcBasisMill.globalScope());
    astMultiNormalComp.setEnclosingScope(ArcBasisMill.globalScope());

    SynthCompTypeResult result4normal = new SynthCompTypeResult();
    SynthCompTypeResult result4qual = new SynthCompTypeResult();
    SynthCompTypeResult result4multi = new SynthCompTypeResult();
    SynthesizeComponentFromMCBasicTypes synth4normal = new SynthesizeComponentFromMCBasicTypes(result4normal);
    SynthesizeComponentFromMCBasicTypes synth4qual = new SynthesizeComponentFromMCBasicTypes(result4qual);
    SynthesizeComponentFromMCBasicTypes synth4multi = new SynthesizeComponentFromMCBasicTypes(result4multi);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);
    synth4multi.handle(astMultiNormalComp);

    // Then
    Assertions.assertTrue(result4normal.getResult().isPresent());
    Assertions.assertTrue(result4qual.getResult().isPresent());
    Assertions.assertTrue(result4multi.getResult().isPresent());
    Assertions.assertTrue(result4normal.getResult().get() instanceof TypeExprOfComponent);
    Assertions.assertTrue(result4qual.getResult().get() instanceof TypeExprOfComponent);
    Assertions.assertTrue(result4multi.getResult().get() instanceof TypeExprOfComponent);
    Assertions.assertEquals(normalComp, result4normal.getResult().get().getTypeInfo());
    Assertions.assertEquals(qualifiedComp, result4qual.getResult().get().getTypeInfo());
    Assertions.assertTrue(result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp1) || result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp2));
    checkOnlyExpectedErrorsPresent(ArcError.AMBIGUOUS_REFERENCE);
  }

  @Test
  public void shouldNotHandleMCQualifiedType() {
    // Given
    ASTMCQualifiedType astNormalComp = createQualifiedType("Foo");
    ASTMCQualifiedType astQualComp = createQualifiedType("qual", "Foo");
    astNormalComp.setEnclosingScope(ArcBasisMill.globalScope());
    astQualComp.setEnclosingScope(ArcBasisMill.globalScope());

    SynthCompTypeResult result4normal = new SynthCompTypeResult();
    SynthCompTypeResult result4qual = new SynthCompTypeResult();
    SynthesizeComponentFromMCBasicTypes synth4normal = new SynthesizeComponentFromMCBasicTypes(result4normal);
    SynthesizeComponentFromMCBasicTypes synth4qual = new SynthesizeComponentFromMCBasicTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertFalse(result4normal.getResult().isPresent());
    Assertions.assertFalse(result4qual.getResult().isPresent());
    checkOnlyExpectedErrorsPresent(ArcError.MISSING_COMPONENT, ArcError.MISSING_COMPONENT);
  }

  @Test
  public void shouldNotHandleVoidType() {
    // Given
    ASTMCVoidType voidType = ArcBasisMill.mCVoidTypeBuilder().build();
    SynthCompTypeResult resultWrapper = new SynthCompTypeResult();
    SynthesizeComponentFromMCBasicTypes synth = new SynthesizeComponentFromMCBasicTypes(resultWrapper);

    // Attach a traverser to the synth, as we do not override the handle method and thus the synth tries to traverse the
    // AST. In the end this should result in an empty synth result, however, if we do not attach a traverser, this will
    // Result in an error instead.
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setMCBasicTypesHandler(synth);

    // When
    synth.handle(voidType);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
    checkOnlyExpectedErrorsPresent();
  }
}
