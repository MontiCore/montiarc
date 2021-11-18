/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.SynthCompTypeResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericTypeBuilder;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import genericarc.check.TypeExprOfGenericComponent;
import montiarc.AbstractTest;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SynthCompTypeExprFromMCSimpleGenericTypesTest extends AbstractTest {

  @Test
  public void shouldHandleMCBasicGenericType() {
    // Given
    // First, we build OOSymbols for String and List<T> and a ComponentTypeSymbol for Comp<K,V>. We put them in a
    // common sub scope of the global scope.
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .setTypeParameters(ImmutableList.of(
        MontiArcMill.typeVarSymbolBuilder().setName("K").build(),
        MontiArcMill.typeVarSymbolBuilder().setName("V").build()
      )).build();

    String nameOfCompScope = "scoop";
    IMontiArcScope scopeOfComp = MontiArcMill.scope();
    scopeOfComp.setName(nameOfCompScope);
    scopeOfComp.add(compSym);
    scopeOfComp.addSubScope(compSym.getSpannedScope());
    MontiArcMill.globalScope().addSubScope(scopeOfComp);

    String stringName = "String";
    OOTypeSymbol stringSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    scopeOfComp.add(stringSym);
    scopeOfComp.addSubScope(stringSym.getSpannedScope());

    String listName = "List";
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    scopeOfComp.add(listSym);
    scopeOfComp.addSubScope(listSym.getSpannedScope());

    // Now we build generic ast types Comp<String, List<String>> and scoop.Comp<scoop.List<scoop.String>, scoop.String>
    // That lay a) in the scope where the symbols lay and b) in the global scope.
    ASTMCQualifiedType astString = createQualifiedType(stringName);
    ASTMCQualifiedType astQualString = createQualifiedType(nameOfCompScope, stringName);
    astString.setEnclosingScope(scopeOfComp);
    astString.getMCQualifiedName().setEnclosingScope(scopeOfComp);
    astQualString.setEnclosingScope(MontiArcMill.globalScope());
    astQualString.getMCQualifiedName().setEnclosingScope(scopeOfComp);

    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), scopeOfComp, astString);
    ASTMCType astQualListOfString = createGenericType(
      ImmutableList.of(nameOfCompScope, listName), MontiArcMill.globalScope(), astQualString);

    // Now build qualified and unqualified generic types
    ASTMCBasicGenericType astNormalComp = createGenericType(
      ImmutableList.of(compName),
      scopeOfComp,
      astString, astListOfString
    );
    ASTMCBasicGenericType astQualComp = createGenericType(
      ImmutableList.of(nameOfCompScope, compName),
      MontiArcMill.globalScope(),
      astQualListOfString, astQualString
    );

    astNormalComp.setEnclosingScope(scopeOfComp);
    astQualComp.setEnclosingScope(MontiArcMill.globalScope());

    SynthCompTypeResult result4normal = new SynthCompTypeResult();
    SynthCompTypeResult result4qual = new SynthCompTypeResult();
    SynthCompTypeExprFromMCSimpleGenericTypes synth4normal = new SynthCompTypeExprFromMCSimpleGenericTypes(result4normal);
    SynthCompTypeExprFromMCSimpleGenericTypes synth4qual = new SynthCompTypeExprFromMCSimpleGenericTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertTrue(result4normal.getCurrentResult().isPresent());
    Assertions.assertTrue(result4qual.getCurrentResult().isPresent());
    Assertions.assertTrue(result4normal.getCurrentResult().get() instanceof TypeExprOfGenericComponent);
    Assertions.assertTrue(result4qual.getCurrentResult().get() instanceof TypeExprOfGenericComponent);

    TypeExprOfGenericComponent result4normalAsGeneric =
      (TypeExprOfGenericComponent) result4normal.getCurrentResult().get();
    TypeExprOfGenericComponent result4qualAsGeneric =
      (TypeExprOfGenericComponent) result4qual.getCurrentResult().get();

    Assertions.assertEquals(compSym, result4normal.getCurrentResult().get().getTypeInfo());
    Assertions.assertEquals(compSym, result4qual.getCurrentResult().get().getTypeInfo());

    Assertions.assertTrue(result4normalAsGeneric.getBindingFor("K").get() instanceof SymTypeOfObject);
    Assertions.assertTrue(result4normalAsGeneric.getBindingFor("V").get() instanceof SymTypeOfGenerics);
    Assertions.assertEquals(stringSym, result4normalAsGeneric.getBindingFor("K").get().getTypeInfo());
    Assertions.assertEquals(listSym, result4normalAsGeneric.getBindingFor("V").get().getTypeInfo());
    Assertions.assertEquals(stringSym,
      ((SymTypeOfGenerics) result4normalAsGeneric.getBindingFor("V").get()).getArgument(0).getTypeInfo()
    );

    Assertions.assertTrue(result4qualAsGeneric.getBindingFor("K").get() instanceof SymTypeOfGenerics);
    Assertions.assertTrue(result4qualAsGeneric.getBindingFor("V").get() instanceof SymTypeOfObject);
    Assertions.assertEquals(stringSym, result4qualAsGeneric.getBindingFor("V").get().getTypeInfo());
    Assertions.assertEquals(listSym, result4qualAsGeneric.getBindingFor("K").get().getTypeInfo());
    Assertions.assertEquals(stringSym,
      ((SymTypeOfGenerics) result4qualAsGeneric.getBindingFor("K").get()).getArgument(0).getTypeInfo()
    );
  }

  @Test
  public void shouldNotHandleMCBasicGenericTypeBecauseCompTypeUnresolvable() {
    // Given
    String stringName = "String"; // Opposed to the component type, the type argument is present.
    OOTypeSymbol stringSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(stringSym);
    MontiArcMill.globalScope().addSubScope(stringSym.getSpannedScope());

    ASTMCQualifiedType astString = createQualifiedType(stringName);
    astString.setEnclosingScope(MontiArcMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(MontiArcMill.globalScope());

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      MontiArcMill.globalScope(),
      astString
    );

    SynthCompTypeResult resultWrapper = new SynthCompTypeResult();
    SynthCompTypeExprFromMCSimpleGenericTypes synth = new SynthCompTypeExprFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getCurrentResult().isPresent());
  }

  @Test
  public void shouldHandleMCBasicGenericTypeBecauseTypeArgumentUnresolvable() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .setTypeParameters(ImmutableList.of(
        MontiArcMill.typeVarSymbolBuilder().setName("T").build()
      )).build();
    MontiArcMill.globalScope().add(compSym);
    MontiArcMill.globalScope().addSubScope(compSym.getSpannedScope());

    ASTMCQualifiedType astString = createQualifiedType("String");
    astString.setEnclosingScope(MontiArcMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(MontiArcMill.globalScope());

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      MontiArcMill.globalScope(),
      astString
    );

    SynthCompTypeResult resultWrapper = new SynthCompTypeResult();
    SynthCompTypeExprFromMCSimpleGenericTypes synth = new SynthCompTypeExprFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getCurrentResult().isPresent());
  }

  @Test
  public void shouldHandleMCBasicGenericTypeBecauseNestedTypeArgumentUnresolvable() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .setTypeParameters(ImmutableList.of(
        MontiArcMill.typeVarSymbolBuilder().setName("T").build()
      )).build();
    MontiArcMill.globalScope().add(compSym);
    MontiArcMill.globalScope().addSubScope(compSym.getSpannedScope());

    String listName = "List";
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());

    ASTMCQualifiedType astString = createQualifiedType("String");
    astString.setEnclosingScope(MontiArcMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(MontiArcMill.globalScope());

    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), MontiArcMill.globalScope(), astString);

    ASTMCBasicGenericType astComp = createGenericType(
      ImmutableList.of("Unresolvable"),
      MontiArcMill.globalScope(),
      astListOfString
    );

    SynthCompTypeResult resultWrapper = new SynthCompTypeResult();
    SynthCompTypeExprFromMCSimpleGenericTypes synth = new SynthCompTypeExprFromMCSimpleGenericTypes(resultWrapper);

    // When
    synth.handle(astComp);

    // Then
    Assertions.assertFalse(resultWrapper.getCurrentResult().isPresent());
  }

  /**
   * Returns a {@link ASTMCBasicGenericType} whose format is {@code name.parts<typeArg[0], typeArg[1], ...>}.
   * All newly created AST objects are enclosed by {@code enclScope}.
   */
  protected static ASTMCBasicGenericType createGenericType(@NotNull List<String> nameParts,
                                                           @NotNull IMontiArcScope enclScope,
                                                           @NotNull ASTMCType... typeArgs) {
    Preconditions.checkNotNull(nameParts);
    Preconditions.checkNotNull(enclScope);
    Preconditions.checkNotNull(typeArgs);
    Preconditions.checkArgument(Arrays.stream(typeArgs).allMatch(Objects::nonNull));

    ASTMCBasicGenericTypeBuilder builder = MontiArcMill.mCBasicGenericTypeBuilder()
      .setNamesList(nameParts);

    for(ASTMCType typeArg : typeArgs) {
      if(typeArg instanceof ASTMCPrimitiveType) {
        ASTMCPrimitiveType asPrimitiveType = (ASTMCPrimitiveType) typeArg;
        ASTMCPrimitiveTypeArgument asArg = MontiArcMill.mCPrimitiveTypeArgumentBuilder()
          .setMCPrimitiveType(asPrimitiveType).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);

      } else if(typeArg instanceof ASTMCQualifiedType) {
        ASTMCQualifiedType asQualType = (ASTMCQualifiedType) typeArg;
        ASTMCBasicTypeArgument asArg = MontiArcMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(asQualType).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);

      } else {
        ASTMCCustomTypeArgument asArg = MontiArcMill.mCCustomTypeArgumentBuilder().setMCType(typeArg).build();
        asArg.setEnclosingScope(enclScope);
        builder.addMCTypeArgument(asArg);
      }
    }

    ASTMCBasicGenericType type = builder.build();
    type.setEnclosingScope(enclScope);
    return type;
  }
}
