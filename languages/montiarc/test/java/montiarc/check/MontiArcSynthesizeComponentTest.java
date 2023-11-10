/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.CompKindExpression;
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
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import variablearc._symboltable.IVariableArcScope;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MontiArcSynthesizeComponentTest extends MontiArcAbstractTest {

  @Test
  public void shouldSynthesizeFromMCQualifiedType() {
    // Given
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(compSym);
    MontiArcMill.globalScope().addSubScope(compSym.getSpannedScope());

    // Now build the qualified type
    ASTMCQualifiedType astComp = createQualifiedType(compName);
    astComp.setEnclosingScope(MontiArcMill.globalScope());

    MontiArcSynthesizeComponent synth = new MontiArcSynthesizeComponent();

    // When
    Optional<CompKindExpression> result = synth.synthesizeFrom(astComp);

    // Then
    Assertions.assertTrue(result.isPresent());
    Assertions.assertTrue(result.get().getTypeInfo().getSpannedScope() instanceof IVariableArcScope);
    Assertions.assertEquals(compSym, result.get().getTypeInfo());
  }

  @Test
  public void shouldSynthesizeFromMCBasicGenericType() {
    // Given
    // First, we build OOSymbols for String and List<T> and a ComponentTypeSymbol for Comp<K,V>.
    String compName = "Comp";
    ComponentTypeSymbol compSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(MontiArcMill.scope())
      .setTypeParameters(ImmutableList.of(
        MontiArcMill.typeVarSymbolBuilder().setName("K").build(),
        MontiArcMill.typeVarSymbolBuilder().setName("V").build()
      )).build();
    
    MontiArcMill.globalScope().add(compSym);
    MontiArcMill.globalScope().addSubScope(compSym.getSpannedScope());

    String stringName = "String";
    OOTypeSymbol stringSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(stringName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    MontiArcMill.globalScope().add(stringSym);
    MontiArcMill.globalScope().addSubScope(stringSym.getSpannedScope());

    String listName = "List";
    OOTypeSymbol listSym = MontiArcMill.oOTypeSymbolBuilder()
      .setName(listName)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    listSym.addTypeVarSymbol(MontiArcMill.typeVarSymbolBuilder().setName("T").build());
    MontiArcMill.globalScope().add(listSym);
    MontiArcMill.globalScope().addSubScope(listSym.getSpannedScope());

    // Now we build generic ast types Comp<String, List<String>> that lay in the global scope.
    ASTMCQualifiedType astString = createQualifiedType(stringName);
    astString.setEnclosingScope(MontiArcMill.globalScope());
    astString.getMCQualifiedName().setEnclosingScope(MontiArcMill.globalScope());
    ASTMCType astListOfString = createGenericType(ImmutableList.of(listName), MontiArcMill.globalScope(), astString);

    // Now build qualified and unqualified generic types
    ASTMCBasicGenericType astNormalComp = createGenericType(
      ImmutableList.of(compName),
      MontiArcMill.globalScope(),
      astString, astListOfString
    );
    astNormalComp.setEnclosingScope(MontiArcMill.globalScope());

    MontiArcSynthesizeComponent synth = new MontiArcSynthesizeComponent();

    // When
    Optional<CompKindExpression> result = synth.synthesizeFrom(astNormalComp);

    // Then
    Assertions.assertTrue(result.isPresent());
    Assertions.assertTrue(result.get() instanceof TypeExprOfGenericComponent);
    TypeExprOfGenericComponent resultAsGeneric = (TypeExprOfGenericComponent) result.get();

    Assertions.assertEquals(compSym, resultAsGeneric.getTypeInfo());
    Assertions.assertTrue(resultAsGeneric.getTypeBindingFor("K").get() instanceof SymTypeOfObject);
    Assertions.assertTrue(resultAsGeneric.getTypeBindingFor("V").get() instanceof SymTypeOfGenerics);
    Assertions.assertEquals(stringSym, resultAsGeneric.getTypeBindingFor("K").get().getTypeInfo());
    Assertions.assertEquals(listSym, resultAsGeneric.getTypeBindingFor("V").get().getTypeInfo());
    Assertions.assertEquals(stringSym,
      ((SymTypeOfGenerics) resultAsGeneric.getTypeBindingFor("V").get()).getArgument(0).getTypeInfo()
    );
  }
  
  @Test
  public void shouldNotSynthesizeFromUnresolvableType() {
    // Now build the qualified type
    ASTMCQualifiedType astComp = createQualifiedType("Unresolvable");
    astComp.setEnclosingScope(MontiArcMill.globalScope());

    MontiArcSynthesizeComponent synth = new MontiArcSynthesizeComponent();

    // When
    Optional<CompKindExpression> result = synth.synthesizeFrom(astComp);

    // Then
    Assertions.assertFalse(result.isPresent());
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
