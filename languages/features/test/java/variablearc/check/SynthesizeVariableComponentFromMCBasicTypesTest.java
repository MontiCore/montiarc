/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.check.SynthCompTypeResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc.AbstractTest;
import variablearc.VariableArcMill;
import variablearc._symboltable.IVariableArcScope;

/**
 * Tests for {@link SynthesizeVariableComponentFromMCBasicTypes}
 */
public class SynthesizeVariableComponentFromMCBasicTypesTest extends AbstractTest {

  @Test
  public void shouldHandleMCQualifiedType() {
    // Given
    // First build some component type symbols which we refer to with the qualified type
    String normalCompName = "Comp1";
    ComponentTypeSymbol normalComp = VariableArcMill.componentTypeSymbolBuilder()
      .setName(normalCompName).setSpannedScope(VariableArcMill.scope()).build();
    VariableArcMill.globalScope().add(normalComp);
    VariableArcMill.globalScope().addSubScope(normalComp.getSpannedScope());

    String qualifiedCompName = "Comp2";
    ComponentTypeSymbol qualifiedComp = VariableArcMill.componentTypeSymbolBuilder()
      .setName(qualifiedCompName).setSpannedScope(VariableArcMill.scope())
      .build();

    String nameOfQualCompScope = "scoop";
    IArcBasisScope scopeOfQualComp = VariableArcMill.scope();
    scopeOfQualComp.setName(nameOfQualCompScope);
    scopeOfQualComp.add(qualifiedComp);
    scopeOfQualComp.addSubScope(qualifiedComp.getSpannedScope());
    VariableArcMill.globalScope().addSubScope(scopeOfQualComp);

    // Now build the qualified type
    ASTMCQualifiedType astNormalComp = createQualifiedType(normalCompName);
    ASTMCQualifiedType astQualComp = createQualifiedType(nameOfQualCompScope, qualifiedCompName);
    astNormalComp.setEnclosingScope(VariableArcMill.globalScope());
    astQualComp.setEnclosingScope(VariableArcMill.globalScope());

    SynthCompTypeResult result4normal = new SynthCompTypeResult();
    SynthCompTypeResult result4qual = new SynthCompTypeResult();
    SynthesizeVariableComponentFromMCBasicTypes synth4normal = new SynthesizeVariableComponentFromMCBasicTypes(result4normal);
    SynthesizeVariableComponentFromMCBasicTypes synth4qual = new SynthesizeVariableComponentFromMCBasicTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertTrue(result4normal.getResult().isPresent());
    Assertions.assertTrue(result4qual.getResult().isPresent());
    Assertions.assertTrue(result4normal.getResult()
      .get().getTypeInfo().getSpannedScope() instanceof IVariableArcScope);
    Assertions.assertTrue(result4qual.getResult()
      .get().getTypeInfo().getSpannedScope() instanceof IVariableArcScope);
    Assertions.assertEquals(normalComp, result4normal.getResult().get()
      .getTypeInfo());
    Assertions.assertEquals(qualifiedComp, result4qual.getResult().get()
      .getTypeInfo());
  }

  @Test
  public void shouldNotHandleMCQualifiedType() {
    // Given
    ASTMCQualifiedType astNormalComp = createQualifiedType("Foo");
    ASTMCQualifiedType astQualComp = createQualifiedType("qual", "Foo");
    astNormalComp.setEnclosingScope(VariableArcMill.globalScope());
    astQualComp.setEnclosingScope(VariableArcMill.globalScope());

    SynthCompTypeResult result4normal = new SynthCompTypeResult();
    SynthCompTypeResult result4qual = new SynthCompTypeResult();
    SynthesizeVariableComponentFromMCBasicTypes synth4normal = new SynthesizeVariableComponentFromMCBasicTypes(result4normal);
    SynthesizeVariableComponentFromMCBasicTypes synth4qual = new SynthesizeVariableComponentFromMCBasicTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertFalse(result4normal.getResult().isPresent());
    Assertions.assertFalse(result4qual.getResult().isPresent());
  }

  @Test
  public void shouldNotHandleVoidType() {
    // Given
    ASTMCVoidType voidType = VariableArcMill.mCVoidTypeBuilder().build();
    SynthCompTypeResult resultWrapper = new SynthCompTypeResult();
    SynthesizeVariableComponentFromMCBasicTypes synth = new SynthesizeVariableComponentFromMCBasicTypes(resultWrapper);

    // Attach a traverser to the synth, as we do not override the handle method and thus the synth tries to traverse the
    // AST. In the end this should result in an empty synth result, however, if we do not attach a traverser, this will
    // Result in an error instead.
    ArcBasisTraverser traverser = VariableArcMill.traverser();
    traverser.setMCBasicTypesHandler(synth);

    // When
    synth.handle(voidType);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
  }
}
