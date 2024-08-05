/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import arcbasis._symboltable.TransitiveScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BOOLEAN;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BYTE;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.CHAR;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.DOUBLE;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.FLOAT;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.INT;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.LONG;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.SHORT;
import static de.monticore.types.check.SymTypeExpressionFactory.createFromSymbol;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;

/**
 * This class provides tests for type checking expressions over collection
 * types like Set and List.
 */
public class TypeCheckCollectionsTest extends MontiArcAbstractTest {

  /**
   * The enclosing scope of the symbols of the test setup
   */
  protected IArcBasisScope scope;

  @BeforeAll
  public static void log() {
    Log.enableFailQuick(false);
  }

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    this.initSymbols();
  }

  protected void initSymbols() {
    // Enable collection types
    MCCollectionSymTypeRelations.init();

    // Initialize the expression's enclosing scope
    this.scope = MontiArcMill.scope();
    this.scope.setEnclosingScope(MontiArcMill.globalScope());
    MontiArcMill.globalScope().addSubScope(this.scope);

    // Initialize the available symbols and add them to the scope
    TypeVarSymbol collTypeVar = MontiArcMill.typeVarSymbolBuilder()
      .setName("C")
      .build();
    IOOSymbolsScope collScope = MontiArcMill.scope();
    collScope.setEnclosingScope(MontiArcMill.globalScope());
    SymbolService.link(collScope, collTypeVar);
    OOTypeSymbol collType = MontiArcMill.oOTypeSymbolBuilder()
      .setName("Collection")
      .setSpannedScope(collScope)
      .build();
    SymbolService.link(MontiArcMill.globalScope(), collType);
    TypeVarSymbol setTypeVar = MontiArcMill.typeVarSymbolBuilder()
        .setName("S")
        .build();
    IOOSymbolsScope setScope = MontiArcMill.scope();
    setScope.setEnclosingScope(MontiArcMill.globalScope());
    SymbolService.link(setScope, setTypeVar);
    OOTypeSymbol setType = MontiArcMill.oOTypeSymbolBuilder()
        .setName("Set")
        .addSuperTypes(createGenerics("Collection", setScope, List.of(createFromSymbol(setTypeVar))))
        .setSpannedScope(setScope)
        .build();
    SymbolService.link(MontiArcMill.globalScope(), setType);
    TypeVarSymbol listTypeVar = MontiArcMill.typeVarSymbolBuilder()
        .setName("L")
        .build();
    IOOSymbolsScope listScope = MontiArcMill.scope();
    listScope.setEnclosingScope(MontiArcMill.globalScope());
    SymbolService.link(listScope, listTypeVar);
    OOTypeSymbol listType = MontiArcMill.oOTypeSymbolBuilder()
        .setName("List")
        .addSuperTypes(createGenerics("Collection", listScope, List.of(createFromSymbol(listTypeVar))))
        .setSpannedScope(listScope)
        .build();
    SymbolService.link(MontiArcMill.globalScope(), listType);
    FieldSymbol aBoolean = MontiArcMill.fieldSymbolBuilder()
      .setName("aBool")
      .setType(createPrimitive(BOOLEAN))
      .build();
    SymbolService.link(this.scope, aBoolean);
    FieldSymbol aChar = MontiArcMill.fieldSymbolBuilder()
      .setName("aChar")
      .setType(createPrimitive(CHAR))
      .build();
    SymbolService.link(this.scope, aChar);
    FieldSymbol aByte = MontiArcMill.fieldSymbolBuilder()
      .setName("aByte")
      .setType(createPrimitive(BYTE))
      .build();
    SymbolService.link(this.scope, aByte);
    FieldSymbol aShort = MontiArcMill.fieldSymbolBuilder()
      .setName("aShort")
      .setType(createPrimitive(SHORT))
      .build();
    SymbolService.link(this.scope, aShort);
    FieldSymbol anInt = MontiArcMill.fieldSymbolBuilder()
      .setName("anInt")
      .setType(createPrimitive(INT))
      .build();
    SymbolService.link(this.scope, anInt);
    FieldSymbol aLong = MontiArcMill.fieldSymbolBuilder()
      .setName("aLong")
      .setType(createPrimitive(LONG))
      .build();
    SymbolService.link(this.scope, aLong);
    FieldSymbol aFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("aFloat")
      .setType(createPrimitive(FLOAT))
      .build();
    SymbolService.link(this.scope, aFloat);
    FieldSymbol aDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("aDouble")
      .setType(createPrimitive(DOUBLE))
      .build();
    SymbolService.link(this.scope, aDouble);
    FieldSymbol listOfBool = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfBool")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(BOOLEAN))))
      .build();
    SymbolService.link(this.scope, listOfBool);
    FieldSymbol setOfBool = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfBool")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(BOOLEAN))))
      .build();
    SymbolService.link(this.scope, setOfBool);
    FieldSymbol listOfChar = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfChar")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(CHAR))))
      .build();
    SymbolService.link(this.scope, listOfChar);
    FieldSymbol setOfChar = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfChar")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(CHAR))))
      .build();
    SymbolService.link(this.scope, setOfChar);
    FieldSymbol listOfByte = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfByte")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(BYTE))))
      .build();
    SymbolService.link(this.scope, listOfByte);
    FieldSymbol setOfByte = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfByte")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(BYTE))))
      .build();
    SymbolService.link(this.scope, setOfByte);
    FieldSymbol listOfShort = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfShort")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(SHORT))))
      .build();
    SymbolService.link(this.scope, listOfShort);
    FieldSymbol setOfShort = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfShort")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(SHORT))))
      .build();
    SymbolService.link(this.scope, setOfShort);
    FieldSymbol listOfInt = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfInt")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(INT))))
      .build();
    SymbolService.link(this.scope, listOfInt);
    FieldSymbol setOfInt = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfInt")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(INT))))
      .build();
    SymbolService.link(this.scope, setOfInt);
    FieldSymbol listOfLong = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfLong")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(LONG))))
      .build();
    SymbolService.link(this.scope, listOfLong);
    FieldSymbol setOfLong = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfLong")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(LONG))))
      .build();
    SymbolService.link(this.scope, setOfLong);
    FieldSymbol listOfFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfFloat")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(FLOAT))))
      .build();
    SymbolService.link(this.scope, listOfFloat);
    FieldSymbol setOfFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfFloat")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(FLOAT))))
      .build();
    SymbolService.link(this.scope, setOfFloat);
    FieldSymbol listOfDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("listOfDouble")
      .setType(createGenerics("List", this.scope, List.of(createPrimitive(DOUBLE))))
      .build();
    SymbolService.link(this.scope, listOfDouble);
    FieldSymbol setOfDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("setOfDouble")
      .setType(createGenerics("Set", this.scope, List.of(createPrimitive(DOUBLE))))
      .build();
    SymbolService.link(this.scope, setOfDouble);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "aBool isin listOfBool", // isin applicable to boolean, List<boolean>
    "aBool isin setOfBool", // isin applicable to boolean, Set<boolean>
    "aChar isin listOfInt", // isin applicable to char, List<int>
    "aChar isin setOfInt", // isin applicable to char, Set<int>
    "aChar isin listOfLong", // isin applicable to char, List<long>
    "aChar isin setOfLong", // isin applicable to char, Set<long>
    "aChar isin listOfFloat", // isin applicable to char, List<float>
    "aChar isin setOfFloat", // isin applicable to char, Set<float>
    "aChar isin listOfDouble", // isin applicable to char, List<double>
    "aChar isin setOfDouble", // isin applicable to char, Set<double>
    "aChar isin listOfChar", // isin applicable to char, List<char>
    "aChar isin setOfChar", // isin applicable to char, Set<char>
    "aByte isin listOfByte", // isin applicable to byte, List<byte>
    "aByte isin setOfByte", // isin applicable to byte, Set<byte>
    "aByte isin listOfShort", // isin applicable to byte, List<short>
    "aByte isin setOfShort", // isin applicable to byte, Set<short>
    "aByte isin listOfInt", // isin applicable to byte, List<int>
    "aByte isin setOfInt", // isin applicable to byte, Set<int>
    "aByte isin listOfLong", // isin applicable to byte, List<long>
    "aByte isin setOfLong", // isin applicable to byte Set<long>
    "aByte isin listOfFloat", // isin applicable to byte, List<float>
    "aByte isin setOfFloat", // isin applicable to byte, Set<float>
    "aByte isin listOfDouble", // isin applicable to byte, List<double>
    "aByte isin setOfDouble", // isin applicable to byte, Set<double>
    "aShort isin listOfByte", // isin applicable to short, List<byte>
    "aShort isin setOfByte", // isin applicable to short, Set<byte>
    "aShort isin listOfShort", // isin applicable to short, List<short>
    "aShort isin setOfShort", // isin applicable to short, Set<short>
    "aShort isin listOfInt", // isin applicable to short, List<int>
    "aShort isin setOfInt", // isin applicable to short, Set<int>
    "aShort isin listOfLong", // isin applicable to short, List<long>
    "aShort isin setOfLong", // isin applicable to short Set<long>
    "aShort isin listOfFloat", // isin applicable to short, List<float>
    "aShort isin setOfFloat", // isin applicable to short, Set<float>
    "aShort isin listOfDouble", // isin applicable to short, List<double>
    "aShort isin setOfDouble", // isin applicable to short, Set<double>
    "anInt isin listOfChar", // isin applicable to int, List<char>
    "anInt isin setOfChar", // isin applicable to int, Set<char>
    "anInt isin listOfByte", // isin applicable to int, List<byte>
    "anInt isin setOfByte", // isin applicable to int, Set<byte>
    "anInt isin listOfShort", // isin applicable to int, List<short>
    "anInt isin setOfShort", // isin applicable to int, Set<short>
    "anInt isin listOfInt", // isin applicable to int, List<int>
    "anInt isin setOfInt", // isin applicable to int, Set<int>
    "anInt isin listOfLong", // isin applicable to int, List<long>
    "anInt isin setOfLong", // isin applicable to int Set<long>
    "anInt isin listOfFloat", // isin applicable to int, List<float>
    "anInt isin setOfFloat", // isin applicable to int, Set<float>
    "anInt isin listOfDouble", // isin applicable to int, List<double>
    "anInt isin setOfDouble", // isin applicable to int, Set<double>
    "aLong isin listOfChar", // isin applicable to long, List<char>
    "aLong isin setOfChar", // isin applicable to long, Set<char>
    "aLong isin listOfByte", // isin applicable to long, List<byte>
    "aLong isin setOfByte", // isin applicable to long, Set<byte>
    "aLong isin listOfShort", // isin applicable to long, List<short>
    "aLong isin setOfShort", // isin applicable to long, Set<short>
    "aLong isin listOfInt", // isin applicable to long, List<int>
    "aLong isin setOfInt", // isin applicable to long, Set<int>
    "aLong isin listOfLong", // isin applicable to long, List<long>
    "aLong isin setOfLong", // isin applicable to long Set<long>
    "aLong isin listOfFloat", // isin applicable to long, List<float>
    "aLong isin setOfFloat", // isin applicable to long, Set<float>
    "aLong isin listOfDouble", // isin applicable to long, List<double>
    "aLong isin setOfDouble", // isin applicable to long, Set<double>
    "aFloat isin listOfChar", // isin applicable to float, List<char>
    "aFloat isin setOfChar", // isin applicable to float, Set<char>
    "aFloat isin listOfByte", // isin applicable to float, List<byte>
    "aFloat isin setOfByte", // isin applicable to float, Set<byte>
    "aFloat isin listOfShort", // isin applicable to float, List<short>
    "aFloat isin setOfShort", // isin applicable to float, Set<short>
    "aFloat isin listOfInt", // isin applicable to float, List<int>
    "aFloat isin setOfInt", // isin applicable to float, Set<int>
    "aFloat isin listOfLong", // isin applicable to float, List<long>
    "aFloat isin setOfLong", // isin applicable to float, Set<long>
    "aFloat isin listOfFloat", // isin applicable to float, List<float>
    "aFloat isin setOfFloat", // isin applicable to float, Set<float>
    "aFloat isin listOfDouble", // isin applicable to float, List<double>
    "aFloat isin setOfDouble", // isin applicable to float, Set<double>
    "aDouble isin listOfChar", // isin applicable to double, List<char>
    "aDouble isin setOfChar", // isin applicable to double, Set<char>
    "aDouble isin listOfByte", // isin applicable to double, List<byte>
    "aDouble isin setOfByte", // isin applicable to double, Set<byte>
    "aDouble isin listOfShort", // isin applicable to double, List<short>
    "aDouble isin setOfShort", // isin applicable to double, Set<short>
    "aDouble isin listOfInt", // isin applicable to double, List<int>
    "aDouble isin setOfInt", // isin applicable to double, Set<int>
    "aDouble isin listOfLong", // isin applicable to double, List<long>
    "aDouble isin setOfLong", // isin applicable to double, Set<long>
    "aDouble isin listOfFloat", // isin applicable to double, List<float>
    "aDouble isin setOfFloat", // isin applicable to double, Set<float>
    "aDouble isin listOfDouble", // isin applicable to double, List<double>
    "aDouble isin setOfDouble", // isin applicable to double, Set<double>
    "aBool notin listOfBool", // notin applicable to boolean, List<boolean>
    "aBool notin setOfBool", // notin applicable to boolean, Set<boolean>
    "aChar notin listOfInt", // notin applicable to char, List<int>
    "aChar notin setOfInt", // notin applicable to char, Set<int>
    "aChar notin listOfLong", // notin applicable to char, List<long>
    "aChar notin setOfLong", // notin applicable to char, Set<long>
    "aChar notin listOfFloat", // notin applicable to char, List<float>
    "aChar notin setOfFloat", // notin applicable to char, Set<float>
    "aChar notin listOfDouble", // notin applicable to char, List<double>
    "aChar notin setOfDouble", // notin applicable to char, Set<double>
    "aChar notin listOfChar", // notin applicable to char, List<char>
    "aChar notin setOfChar", // notin applicable to char, Set<char>
    "aByte notin listOfByte", // notin applicable to byte, List<byte>
    "aByte notin setOfByte", // notin applicable to byte, Set<byte>
    "aByte notin listOfShort", // notin applicable to byte, List<short>
    "aByte notin setOfShort", // notin applicable to byte, Set<short>
    "aByte notin listOfInt", // notin applicable to byte, List<int>
    "aByte notin setOfInt", // notin applicable to byte, Set<int>
    "aByte notin listOfLong", // notin applicable to byte, List<long>
    "aByte notin setOfLong", // notin applicable to byte Set<long>
    "aByte notin listOfFloat", // notin applicable to byte, List<float>
    "aByte notin setOfFloat", // notin applicable to byte, Set<float>
    "aByte notin listOfDouble", // notin applicable to byte, List<double>
    "aByte notin setOfDouble", // notin applicable to byte, Set<double>
    "aShort notin listOfByte", // notin applicable to short, List<byte>
    "aShort notin setOfByte", // notin applicable to short, Set<byte>
    "aShort notin listOfShort", // notin applicable to short, List<short>
    "aShort notin setOfShort", // notin applicable to short, Set<short>
    "aShort notin listOfInt", // notin applicable to short, List<int>
    "aShort notin setOfInt", // notin applicable to short, Set<int>
    "aShort notin listOfLong", // notin applicable to short, List<long>
    "aShort notin setOfLong", // notin applicable to short Set<long>
    "aShort notin listOfFloat", // notin applicable to short, List<float>
    "aShort notin setOfFloat", // notin applicable to short, Set<float>
    "aShort notin listOfDouble", // notin applicable to short, List<double>
    "aShort notin setOfDouble", // notin applicable to short, Set<double>
    "anInt notin listOfChar", // notin applicable to int, List<char>
    "anInt notin setOfChar", // notin applicable to int, Set<char>
    "anInt notin listOfByte", // notin applicable to int, List<byte>
    "anInt notin setOfByte", // notin applicable to int, Set<byte>
    "anInt notin listOfShort", // notin applicable to int, List<short>
    "anInt notin setOfShort", // notin applicable to int, Set<short>
    "anInt notin listOfInt", // notin applicable to int, List<int>
    "anInt notin setOfInt", // notin applicable to int, Set<int>
    "anInt notin listOfLong", // notin applicable to int, List<long>
    "anInt notin setOfLong", // notin applicable to int Set<long>
    "anInt notin listOfFloat", // notin applicable to int, List<float>
    "anInt notin setOfFloat", // notin applicable to int, Set<float>
    "anInt notin listOfDouble", // notin applicable to int, List<double>
    "anInt notin setOfDouble", // notin applicable to int, Set<double>
    "aLong notin listOfChar", // notin applicable to long, List<char>
    "aLong notin setOfChar", // notin applicable to long, Set<char>
    "aLong notin listOfByte", // notin applicable to long, List<byte>
    "aLong notin setOfByte", // notin applicable to long, Set<byte>
    "aLong notin listOfShort", // notin applicable to long, List<short>
    "aLong notin setOfShort", // notin applicable to long, Set<short>
    "aLong notin listOfInt", // notin applicable to long, List<int>
    "aLong notin setOfInt", // notin applicable to long, Set<int>
    "aLong notin listOfLong", // notin applicable to long, List<long>
    "aLong notin setOfLong", // notin applicable to long Set<long>
    "aLong notin listOfFloat", // notin applicable to long, List<float>
    "aLong notin setOfFloat", // notin applicable to long, Set<float>
    "aLong notin listOfDouble", // notin applicable to long, List<double>
    "aLong notin setOfDouble", // notin applicable to long, Set<double>
    "aFloat notin listOfChar", // notin applicable to float, List<char>
    "aFloat notin setOfChar", // notin applicable to float, Set<char>
    "aFloat notin listOfByte", // notin applicable to float, List<byte>
    "aFloat notin setOfByte", // notin applicable to float, Set<byte>
    "aFloat notin listOfShort", // notin applicable to float, List<short>
    "aFloat notin setOfShort", // notin applicable to float, Set<short>
    "aFloat notin listOfInt", // notin applicable to float, List<int>
    "aFloat notin setOfInt", // notin applicable to float, Set<int>
    "aFloat notin listOfLong", // notin applicable to float, List<long>
    "aFloat notin setOfLong", // notin applicable to float, Set<long>
    "aFloat notin listOfFloat", // notin applicable to float, List<float>
    "aFloat notin setOfFloat", // notin applicable to float, Set<float>
    "aFloat notin listOfDouble", // notin applicable to float, List<double>
    "aFloat notin setOfDouble", // notin applicable to float, Set<double>
    "aDouble notin listOfChar", // notin applicable to double, List<char>
    "aDouble notin setOfChar", // notin applicable to double, Set<char>
    "aDouble notin listOfByte", // notin applicable to double, List<byte>
    "aDouble notin setOfByte", // notin applicable to double, Set<byte>
    "aDouble notin listOfShort", // notin applicable to double, List<short>
    "aDouble notin setOfShort", // notin applicable to double, Set<short>
    "aDouble notin listOfInt", // notin applicable to double, List<int>
    "aDouble notin setOfInt", // notin applicable to double, Set<int>
    "aDouble notin listOfLong", // notin applicable to double, List<long>
    "aDouble notin setOfLong", // notin applicable to double, Set<long>
    "aDouble notin listOfFloat", // notin applicable to double, List<float>
    "aDouble notin setOfFloat", // notin applicable to double, Set<float>
    "aDouble notin listOfDouble", // notin applicable to double, List<double>
    "aDouble notin setOfDouble", // notin applicable to double, Set<double>
    "listOfBool union listOfBool", // union applicable to List<boolean>, List<boolean>
    "listOfBool union setOfBool", // union applicable to List<boolean>, Set<boolean>
    "setOfBool union listOfBool", // union applicable to Set<Boolean>, List<Boolean>
    "setOfBool union setOfBool", // union applicable to Set<Boolean>, Set<Boolean>
    "listOfChar union listOfChar", // union applicable to List<char>, List<char>
    "listOfChar union setOfChar", // union applicable to List<char>, Set<char>
    "setOfChar union listOfChar", // union applicable to Set<char>, List<char>
    "setOfChar union setOfChar", // union applicable to Set<char>, Set<char>
    "listOfByte union listOfByte", // union applicable to List<byte>, List<byte>
    "listOfByte union setOfByte", // union applicable to List<byte>, Set<byte>
    "setOfByte union listOfByte", // union applicable to Set<byte>, List<byte>
    "setOfByte union setOfByte", // union applicable to Set<byte>, Set<byte>
    //"listOfByte union listOfShort", // union applicable to List<byte>, List<short>
    //"listOfByte union setOfShort", // union applicable to List<byte>, Set<short>
    //"setOfByte union listOfShort", // union applicable to Set<byte>, List<short>
    //"setOfByte union setOfShort", // union applicable to Set<byte>, Set<short>
    //"listOfByte union listOfInt", // union applicable to List<byte>, List<int>
    //"listOfByte union setOfInt", // union applicable to List<byte>, Set<int>
    //"setOfByte union listOfInt", // union applicable to Set<byte>, List<int>
    //"setOfByte union setOfInt", // union applicable to Set<byte>, Set<int>
    //"listOfByte union listOfLong", // union applicable to List<byte>, List<long>
    //"listOfByte union setOfLong", // union applicable to List<byte>, Set<long>
    //"setOfByte union listOfLong", // union applicable to Set<byte>, List<long>
    //"setOfByte union setOfLong", // union applicable to Set<byte>, Set<long>
    //"listOfByte union listOfFloat", // union applicable to List<byte>, List<float>
    //"listOfByte union setOfFloat", // union applicable to List<byte>, Set<float>
    //"setOfByte union listOfFloat", // union applicable to Set<byte>, List<float>
    //"setOfByte union setOfFloat", // union applicable to Set<byte>, Set<float>
    //"listOfByte union listOfDouble", // union applicable to List<byte>, List<double>
    //"listOfByte union setOfDouble", // union applicable to List<byte>, Set<double>
    //"setOfByte union listOfDouble", // union applicable to Set<byte>, List<double>
    //"setOfByte union setOfDouble", // union applicable to Set<byte>, Set<double>
    //"listOfShort union listOfByte", // union applicable to List<short>, List<byte>
    //"listOfShort union setOfByte", // union applicable to List<short>, Set<byte>
    //"setOfShort union listOfByte", // union applicable to Set<short>, List<byte>
    //"setOfShort union setOfByte", // union applicable to Set<short>, Set<byte>
    "listOfShort union listOfShort", // union applicable to List<short>, List<short>
    "listOfShort union setOfShort", // union applicable to List<short>, Set<short>
    "setOfShort union listOfShort", // union applicable to Set<short>, List<short>
    "setOfShort union setOfShort", // union applicable to Set<short>, Set<short>
    //"listOfShort union listOfInt", // union applicable to List<short>, List<int>
    //"listOfShort union setOfInt", // union applicable to List<short>, Set<int>
    //"setOfShort union listOfInt", // union applicable to Set<short>, List<int>
    //"setOfShort union setOfInt", // union applicable to Set<short>, Set<int>
    //"listOfShort union listOfLong", // union applicable to List<short>, List<long>
    //"listOfShort union setOfLong", // union applicable to List<short>, Set<long>
    //"setOfShort union listOfLong", // union applicable to Set<short>, List<long>
    //"setOfShort union setOfLong", // union applicable to Set<short>, Set<long>
    //"listOfShort union listOfFloat", // union applicable to List<short>, List<float>
    //"listOfShort union setOfFloat", // union applicable to List<short>, Set<float>
    //"setOfShort union listOfFloat", // union applicable to Set<short>, List<float>
    //"setOfShort union setOfFloat", // union applicable to Set<short>, Set<float>
    //"listOfShort union listOfDouble", // union applicable to List<short>, List<double>
    //"listOfShort union setOfDouble", // union applicable to List<short>, Set<double>
    //"setOfShort union listOfDouble", // union applicable to Set<short>, List<double>
    //"setOfShort union setOfDouble", // union applicable to Set<short>, Set<double>
    //"listOfInt union listOfByte", // union applicable to List<int>, List<byte>
    //"listOfInt union setOfByte", // union applicable to List<int>, Set<byte>
    //"setOfInt union listOfByte", // union applicable to Set<int>, List<byte>
    //"setOfInt union setOfByte", // union applicable to Set<int>, Set<byte>
    //"listOfInt union listOfShort", // union applicable to List<int>, List<short>
    //"listOfInt union setOfShort", // union applicable to List<int>, Set<short>
    //"setOfInt union listOfShort", // union applicable to Set<int>, List<short>
    //"setOfInt union setOfShort", // union applicable to Set<int>, Set<short>
    "listOfInt union listOfInt", // union applicable to List<int>, List<int>
    "listOfInt union setOfInt", // union applicable to List<int>, Set<int>
    "setOfInt union listOfInt", // union applicable to Set<int>, List<int>
    "setOfInt union setOfInt", // union applicable to Set<int>, Set<int>
    //"listOfInt union listOfLong", // union applicable to List<int>, List<long>
    //"listOfInt union setOfLong", // union applicable to List<int>, Set<long>
    //"setOfInt union listOfLong", // union applicable to Set<int>, List<long>
    //"setOfInt union setOfLong", // union applicable to Set<int>, Set<long>
    //"listOfInt union listOfFloat", // union applicable to List<int>, List<float>
    //"listOfInt union setOfFloat", // union applicable to List<int>, Set<float>
    //"setOfInt union listOfFloat", // union applicable to Set<int>, List<float>
    //"setOfInt union setOfFloat", // union applicable to Set<int>, Set<float>
    //"listOfInt union listOfDouble", // union applicable to List<int>, List<double>
    //"listOfInt union setOfDouble", // union applicable to List<int>, Set<double>
    //"setOfInt union listOfDouble", // union applicable to Set<int>, List<double>
    //"setOfInt union setOfDouble", // union applicable to Set<int>, Set<double>
    //"listOfLong union listOfByte", // union applicable to List<long>, List<byte>
    //"listOfLong union setOfByte", // union applicable to List<long>, Set<byte>
    //"setOfLong union listOfByte", // union applicable to Set<long>, List<byte>
    //"setOfLong union setOfByte", // union applicable to Set<long>, Set<byte>
    //"listOfLong union listOfShort", // union applicable to List<long>, List<short>
    //"listOfLong union setOfShort", // union applicable to List<long>, Set<short>
    //"setOfLong union listOfShort", // union applicable to Set<long>, List<short>
    //"setOfLong union setOfShort", // union applicable to Set<long>, Set<short>
    //"listOfLong union listOfInt", // union applicable to List<long>, List<int>
    //"listOfLong union setOfInt", // union applicable to List<long>, Set<int>
    //"setOfLong union listOfInt", // union applicable to Set<long>, List<int>
    //"setOfLong union setOfInt", // union applicable to Set<long>, Set<int>
    "listOfLong union listOfLong", // union applicable to List<long>, List<long>
    "listOfLong union setOfLong", // union applicable to List<long>, Set<long>
    "setOfLong union listOfLong", // union applicable to Set<long>, List<long>
    "setOfLong union setOfLong", // union applicable to Set<long>, Set<long>
    //"listOfLong union listOfFloat", // union applicable to List<long>, List<float>
    //"listOfLong union setOfFloat", // union applicable to List<long>, Set<float>
    //"setOfLong union listOfFloat", // union applicable to Set<long>, List<float>
    //"setOfLong union setOfFloat", // union applicable to Set<long>, Set<float>
    //"listOfLong union listOfDouble", // union applicable to List<long>, List<double>
    //"listOfLong union setOfDouble", // union applicable to List<long>, Set<double>
    //"setOfLong union listOfDouble", // union applicable to Set<long>, List<double>
    //"setOfLong union setOfDouble", // union applicable to Set<long>, Set<double>
    //"listOfFloat union listOfByte", // union applicable to List<float>, List<byte>
    //"listOfFloat union setOfByte", // union applicable to List<float>, Set<byte>
    //"setOfFloat union listOfByte", // union applicable to Set<float>, List<byte>
    //"setOfFloat union setOfByte", // union applicable to Set<float>, Set<byte>
    //"listOfFloat union listOfShort", // union applicable to List<float>, List<short>
    //"listOfFloat union setOfShort", // union applicable to List<float>, Set<short>
    //"setOfFloat union listOfShort", // union applicable to Set<float>, List<short>
    //"setOfFloat union setOfShort", // union applicable to Set<float>, Set<short>
    //"listOfFloat union listOfInt", // union applicable to List<float>, List<int>
    //"listOfFloat union setOfInt", // union applicable to List<float>, Set<int>
    //"setOfFloat union listOfInt", // union applicable to Set<float>, List<int>
    //"setOfFloat union setOfInt", // union applicable to Set<float>, Set<int>
    //"listOfFloat union listOfLong", // union applicable to List<float>, List<long>
    //"listOfFloat union setOfLong", // union applicable to List<float>, Set<long>
    //"setOfFloat union listOfLong", // union applicable to Set<float>, List<long>
    //"setOfFloat union setOfLong", // union applicable to Set<float>, Set<long>
    "listOfFloat union listOfFloat", // union applicable to List<float>, List<float>
    "listOfFloat union setOfFloat", // union applicable to List<float>, Set<float>
    "setOfFloat union listOfFloat", // union applicable to Set<float>, List<float>
    "setOfFloat union setOfFloat", // union applicable to Set<float>, Set<float>
    //"listOfFloat union listOfDouble", // union applicable to List<float>, List<double>
    //"listOfFloat union setOfDouble", // union applicable to List<float>, Set<double>
    //"setOfFloat union listOfDouble", // union applicable to Set<float>, List<double>
    //"setOfFloat union setOfDouble", // union applicable to Set<float>, Set<double>
    //"listOfDouble union listOfByte", // union applicable to List<double>, List<byte>
    //"listOfDouble union setOfByte", // union applicable to List<double>, Set<byte>
    //"setOfDouble union listOfByte", // union applicable to Set<double>, List<byte>
    //"setOfDouble union setOfByte", // union applicable to Set<double>, Set<byte>
    //"listOfDouble union listOfShort", // union applicable to List<double>, List<short>
    //"listOfDouble union setOfShort", // union applicable to List<double>, Set<short>
    //"setOfDouble union listOfShort", // union applicable to Set<double>, List<short>
    //"setOfDouble union setOfShort", // union applicable to Set<double>, Set<short>
    //"listOfDouble union listOfInt", // union applicable to List<double>, List<int>
    //"listOfDouble union setOfInt", // union applicable to List<double>, Set<int>
    //"setOfDouble union listOfInt", // union applicable to Set<double>, List<int>
    //"setOfDouble union setOfInt", // union applicable to Set<double>, Set<int>
    //"listOfDouble union listOfLong", // union applicable to List<double>, List<long>
    //"listOfDouble union setOfLong", // union applicable to List<double>, Set<long>
    //"setOfDouble union listOfLong", // union applicable to Set<double>, List<long>
    //"setOfDouble union setOfLong", // union applicable to Set<double>, Set<long>
    //"listOfDouble union listOfFloat", // union applicable to List<double>, List<float>
    //"listOfDouble union setOfFloat", // union applicable to List<double>, Set<float>
    //"setOfDouble union listOfFloat", // union applicable to Set<double>, List<float>
    //"setOfDouble union setOfFloat", // union applicable to Set<double>, Set<float>
    "listOfDouble union listOfDouble", // union applicable to List<double>, List<double>
    "listOfDouble union setOfDouble", // union applicable to List<double>, Set<double>
    "setOfDouble union listOfDouble", // union applicable to Set<double>, List<double>
    "setOfDouble union setOfDouble", // union applicable to Set<double>, Set<double>
    "setOfBool intersect setOfBool", // intersect applicable to Set<Boolean>, Set<Boolean>
    "setOfChar intersect setOfChar", // intersect applicable to Set<char>, Set<char>
    "setOfByte intersect setOfByte", // intersect applicable to Set<byte>, Set<byte>
    //"setOfByte intersect setOfShort", // intersect applicable to Set<byte>, Set<short>
    //"setOfByte intersect setOfInt", // intersect applicable to Set<byte>, Set<int>
    //"setOfByte intersect setOfLong", // intersect applicable to Set<byte>, Set<long>
    //"setOfByte intersect setOfFloat", // intersect applicable to Set<byte>, Set<float>
    //"setOfByte intersect setOfDouble", // intersect applicable to Set<byte>, Set<double>
    //"setOfShort intersect setOfByte", // intersect applicable to Set<short>, Set<byte>
    "setOfShort intersect setOfShort", // intersect applicable to Set<short>, Set<short>
    //"setOfShort intersect setOfInt", // intersect applicable to Set<short>, Set<int>
    //"setOfShort intersect setOfLong", // intersect applicable to Set<short>, Set<long>
    //"setOfShort intersect setOfFloat", // intersect applicable to Set<short>, Set<float>
    //"setOfShort intersect setOfDouble", // intersect applicable to Set<short>, Set<double>
    //"setOfInt intersect setOfByte", // intersect applicable to Set<int>, Set<byte>
    //"setOfInt intersect setOfShort", // intersect applicable to Set<int>, Set<short>
    "setOfInt intersect setOfInt", // intersect applicable to Set<int>, Set<int>
    //"setOfInt intersect setOfLong", // intersect applicable to Set<int>, Set<long>
    //"setOfInt intersect setOfFloat", // intersect applicable to Set<int>, Set<float>
    //"setOfInt intersect setOfDouble", // intersect applicable to Set<int>, Set<double>
    //"setOfLong intersect setOfByte", // intersect applicable to Set<long>, Set<byte>
    //"setOfLong intersect setOfShort", // intersect applicable to Set<long>, Set<short>
    //"setOfLong intersect setOfInt", // intersect applicable to Set<long>, Set<int>
    "setOfLong intersect setOfLong", // intersect applicable to Set<long>, Set<long>
    //"setOfLong intersect setOfFloat", // intersect applicable to Set<long>, Set<float>
    //"setOfLong intersect setOfDouble", // intersect applicable to Set<long>, Set<double>
    //"setOfFloat intersect setOfByte", // intersect applicable to Set<float>, Set<byte>
    //"setOfFloat intersect setOfShort", // intersect applicable to Set<float>, Set<short>
    //"setOfFloat intersect setOfInt", // intersect applicable to Set<float>, Set<int>
    //"setOfFloat intersect setOfLong", // intersect applicable to Set<float>, Set<long>
    "setOfFloat intersect setOfFloat", // intersect applicable to Set<float>, Set<float>
    //"setOfFloat intersect setOfDouble", // intersect applicable to Set<float>, Set<double>
    //"setOfDouble intersect setOfByte", // intersect applicable to Set<double>, Set<byte>
    //"setOfDouble intersect setOfShort", // intersect applicable to Set<double>, Set<short>
    //"setOfDouble intersect setOfInt", // intersect applicable to Set<double>, Set<int>
    //"setOfDouble intersect setOfLong", // intersect applicable to Set<double>, Set<long>
    //"setOfDouble intersect setOfFloat", // intersect applicable to Set<double>, Set<float>
    "setOfDouble intersect setOfDouble", // intersect applicable to Set<double>, Set<double>
    "setOfBool \\ setOfBool", // \\ applicable to Set<Boolean>, Set<Boolean>
    "setOfChar \\ setOfChar", // \\ applicable to Set<char>, Set<char>
    "setOfByte \\ setOfByte", // \\ applicable to Set<byte>, Set<byte>
    //"setOfByte \\ setOfShort", // \\ applicable to Set<byte>, Set<short>
    //"setOfByte \\ setOfInt", // \\ applicable to Set<byte>, Set<int>
    //"setOfByte \\ setOfLong", // \\ applicable to Set<byte>, Set<long>
    //"setOfByte \\ setOfFloat", // \\ applicable to Set<byte>, Set<float>
    //"setOfByte \\ setOfDouble", // \\ applicable to Set<byte>, Set<double>
    //"setOfShort \\ setOfByte", // \\ applicable to Set<short>, Set<byte>
    "setOfShort \\ setOfShort", // \\ applicable to Set<short>, Set<short>
    //"setOfShort \\ setOfInt", // \\ applicable to Set<short>, Set<int>
    //"setOfShort \\ setOfLong", // \\ applicable to Set<short>, Set<long>
    //"setOfShort \\ setOfFloat", // \\ applicable to Set<short>, Set<float>
    //"setOfShort \\ setOfDouble", // \\ applicable to Set<short>, Set<double>
    //"setOfInt \\ setOfByte", // \\ applicable to Set<int>, Set<byte>
    //"setOfInt \\ setOfShort", // \\ applicable to Set<int>, Set<short>
    "setOfInt \\ setOfInt", // \\ applicable to Set<int>, Set<int>
    //"setOfInt \\ setOfLong", // \\ applicable to Set<int>, Set<long>
    //"setOfInt \\ setOfFloat", // \\ applicable to Set<int>, Set<float>
    //"setOfInt \\ setOfDouble", // \\ applicable to Set<int>, Set<double>
    //"setOfLong \\ setOfByte", // \\ applicable to Set<long>, Set<byte>
    //"setOfLong \\ setOfShort", // \\ applicable to Set<long>, Set<short>
    //"setOfLong \\ setOfInt", // \\ applicable to Set<long>, Set<int>
    "setOfLong \\ setOfLong", // \\ applicable to Set<long>, Set<long>
    //"setOfLong \\ setOfFloat", // \\ applicable to Set<long>, Set<float>
    //"setOfLong \\ setOfDouble", // \\ applicable to Set<long>, Set<double>
    //"setOfFloat \\ setOfByte", // \\ applicable to Set<float>, Set<byte>
    //"setOfFloat \\ setOfShort", // \\ applicable to Set<float>, Set<short>
    //"setOfFloat \\ setOfInt", // \\ applicable to Set<float>, Set<int>
    //"setOfFloat \\ setOfLong", // \\ applicable to Set<float>, Set<long>
    "setOfFloat \\ setOfFloat", // \\ applicable to Set<float>, Set<float>
    //"setOfFloat \\ setOfDouble", // \\ applicable to Set<float>, Set<double>
    //"setOfDouble \\ setOfByte", // \\ applicable to Set<double>, Set<byte>
    //"setOfDouble \\ setOfShort", // \\ applicable to Set<double>, Set<short>
    //"setOfDouble \\ setOfInt", // \\ applicable to Set<double>, Set<int>
    //"setOfDouble \\ setOfLong", // \\ applicable to Set<double>, Set<long>
    //"setOfDouble \\ setOfFloat", // \\ applicable to Set<double>, Set<float>
    "setOfDouble \\ setOfDouble", // \\ applicable to Set<double>, Set<double>
    "listOfBool = [ ]", // expected List<boolean> and provided List<>
    "listOfBool = [ true ]", // expected List<boolean> and provided List<boolean>
    "listOfBool = [ false ]", // expected List<boolean> and provided List<boolean>
    "listOfBool = [ aBool ]", // expected List<boolean> and provided List<boolean>
    "listOfBool = [ true, false ]", // expected List<boolean> and provided List<boolean>
    "listOfBool = [ aBool, aBool ]", // expected List<boolean> and provided List<boolean>
    "setOfBool = { }", // expected Set<boolean> and provided Set<>
    "setOfBool = { true }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = { false }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = { aBool }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = { true, false }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = { aBool, aBool }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = Set { }", // expected Set<boolean> and provided Set<>
    "setOfBool = Set { true }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = Set { false }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = Set { aBool }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = Set { true, false }", // expected Set<boolean> and provided Set<boolean>
    "setOfBool = Set { aBool, aBool }", // expected Set<boolean> and provided Set<boolean>
    "listOfChar = [ ]", // expected List<char> and provided List<>
    "listOfChar = [ 'a' ]", // expected List<char> and provided List<char>
    "listOfChar = [ aChar ]", // expected List<char> and provided List<char>
    "listOfChar = [ 'a', 'b' ]", // expected List<char> and provided List<char>
    "listOfChar = [ aChar, aChar ]", // expected List<char> and provided List<char>
    "setOfChar = { }", // expected Set<char> and provided Set<>
    "setOfChar = { 'a' }", // expected Set<char> and provided Set<char>
    "setOfChar = { aChar }", // expected Set<char> and provided Set<char>
    "setOfChar = { 'a', 'b' }", // expected Set<char> and provided Set<char>
    "setOfChar = { aChar, aChar }", // expected Set<char> and provided Set<char>
    "setOfChar = Set { }", // expected Set<char> and provided Set<>
    "setOfChar = Set { 'a' }", // expected Set<char> and provided Set<char>
    "setOfChar = Set { aChar }", // expected Set<char> and provided Set<char>
    "setOfChar = Set { 'a', 'b' }", // expected Set<char> and provided Set<char>
    "setOfChar = Set { aChar, aChar }", // expected Set<char> and provided Set<char>
    "listOfByte = [ ]", // expected List<byte> and provided List<>
    //"listOfByte = [ 0 ]", // expected List<byte> and provided List<byte>
    "listOfByte = [ aByte ]", // expected List<byte> and provided List<byte>
    //"listOfByte = [ 0, 1 ]", // expected List<byte> and provided List<byte>
    "listOfByte = [ aByte, aByte ]", // expected List<byte> and provided List<byte>
    "setOfByte = { }", // expected Set<byte> and provided Set<>
    //"setOfByte = { 0 }", // expected Set<byte> and provided Set<byte>
    "setOfByte = { aByte }", // expected Set<byte> and provided Set<byte>
    //"setOfByte = { 0, 1 }", // expected Set<byte> and provided Set<byte>
    "setOfByte = { aByte, aByte }", // expected Set<byte> and provided Set<byte>
    "setOfByte = Set { }", // expected Set<byte> and provided Set<>
    //"setOfByte = Set { 0 }", // expected Set<byte> and provided Set<byte>
    "setOfByte = Set { aByte }", // expected Set<byte> and provided Set<byte>
    //"setOfByte = Set { 0, 1 }", // expected Set<byte> and provided Set<byte>
    "setOfByte = Set { aByte, aByte }", // expected Set<byte> and provided Set<byte>
    "listOfShort = [ ]", // expected List<short> and provided List<>
    //"listOfShort = [ 0 ]", // expected List<short> and provided List<short>
    "listOfShort = [ aShort ]", // expected List<short> and provided List<short>
    //"listOfShort = [ 0, 1 ]", // expected List<short> and provided List<short>
    "listOfShort = [ aShort, aShort ]", // expected List<short> and provided List<short>
    "setOfShort = { }", // expected Set<short> and provided Set<>
    //"setOfShort = { 0 }", // expected Set<short> and provided Set<short>
    "setOfShort = { aShort }", // expected Set<short> and provided Set<short>
    //"setOfShort = { 0, 1 }", // expected Set<short> and provided Set<short>
    "setOfShort = { aShort, aShort }", // expected Set<short> and provided Set<short>
    "setOfShort = Set { }", // expected Set<short> and provided Set<>
    //"setOfShort = Set { 0 }", // expected Set<short> and provided Set<short>
    "setOfShort = Set { aShort }", // expected Set<short> and provided Set<short>
    //"setOfShort = Set { 0, 1 }", // expected Set<short> and provided Set<short>
    "setOfShort = Set { aShort, aShort }", // expected Set<short> and provided Set<short>
    "listOfInt = [ ]", // expected List<int> and provided List<>
    "listOfInt = [ 0 ]", // expected List<int> and provided List<int>
    "listOfInt = [ anInt ]", // expected List<int> and provided List<int>
    "listOfInt = [ 0, 1 ]", // expected List<int> and provided List<int>
    "listOfInt = [ anInt, anInt ]", // expected List<int> and provided List<int>
    "setOfInt = { }", // expected Set<int> and provided Set<>
    "setOfInt = { 0 }", // expected Set<int> and provided Set<int>
    "setOfInt = { anInt }", // expected Set<int> and provided Set<int>
    "setOfInt = { 0, 1 }", // expected Set<int> and provided Set<int>
    "setOfInt = Set { }", // expected Set<int> and provided Set<>
    "setOfInt = Set { 0 }", // expected Set<int> and provided Set<int>
    "setOfInt = Set { anInt }", // expected Set<int> and provided Set<int>
    "setOfInt = Set { 0, 1 }", // expected Set<int> and provided Set<int>
    "listOfLong = [ ]", // expected List<long> and provided List<>
    //"listOfLong = [ 0 ]", // expected List<long> and provided List<long>
    "listOfLong = [ 0l ]", // expected List<long> and provided List<long>
    "listOfLong = [ aLong ]", // expected List<long> and provided List<long>
    //"listOfLong = [ 0, 1 ]", // expected List<long> and provided List<long>
    "listOfLong = [ 0l, 1 ]", // expected List<long> and provided List<long>
    "listOfLong = [ 0, 1l ]", // expected List<long> and provided List<long>
    "listOfLong = [ 0l, 1l ]", // expected List<long> and provided List<long>
    "listOfLong = [ aLong, aLong ]", // expected List<long> and provided List<long>
    "setOfLong = { }", // expected Set<long> and provided Set<>
    //"setOfLong = { 0 }", // expected Set<long> and provided Set<long>
    "setOfLong = { 0l }", // expected Set<long> and provided Set<long>
    "setOfLong = { aLong }", // expected Set<long> and provided Set<long>
    //"setOfLong = { 0, 1 }", // expected Set<long> and provided Set<long>
    "setOfLong = { 0l, 1 }", // expected Set<long> and provided Set<long>
    "setOfLong = { 0, 1l }", // expected Set<long> and provided Set<long>
    "setOfLong = { 0l, 1l }", // expected Set<long> and provided Set<long>
    "setOfLong = { aLong, aLong }", // expected Set<long> and provided Set<long>
    "setOfLong = Set { }", // expected Set<long> and provided Set<>
    //"setOfLong = Set { 0 }", // expected Set<long> and provided Set<long>
    "setOfLong = Set { 0l }", // expected Set<long> and provided Set<long>
    "setOfLong = Set { aLong }", // expected Set<long> and provided Set<long>
    //"setOfLong = Set { 0, 1 }", // expected Set<long> and provided Set<long>
    "setOfLong = Set { 0l, 1 }", // expected Set<long> and provided Set<long>
    "setOfLong = Set { 0, 1l }", // expected Set<long> and provided Set<long>
    "setOfLong = Set { 0l, 1l }", // expected Set<long> and provided Set<long>
    "setOfLong = Set { aLong, aLong }", // expected Set<long> and provided Set<long>
    "listOfFloat = [ ]", // expected List<float> and provided List<>
    //"listOfFloat = [ 0 ]", // expected List<float> and provided List<float>
    "listOfFloat = [ 0.0f ]", // expected List<float> and provided List<float>
    "listOfFloat = [ aFloat ]", // expected List<float> and provided List<float>
    //"listOfFloat = [ 0, 1 ]", // expected List<float> and provided List<float>
    "listOfFloat = [ 0.0f, 1 ]", // expected List<float> and provided List<float>
    "listOfFloat = [ 0, 1.0f ]", // expected List<float> and provided List<float>
    "listOfFloat = [ 0.0f, 1.0f ]", // expected List<float> and provided List<float>
    "listOfFloat = [ aFloat, aFloat ]", // expected List<float> and provided List<float>
    "setOfFloat = { }", // expected Set<float> and provided Set<>
    //"setOfFloat = { 0 }", // expected Set<float> and provided Set<float>
    "setOfFloat = { 0.0f }", // expected Set<float> and provided Set<float>
    "setOfFloat = { aFloat }", // expected Set<float> and provided Set<float>
    //"setOfFloat = { 0, 1 }", // expected Set<float> and provided Set<float>
    "setOfFloat = { 0.0f, 1 }", // expected Set<float> and provided Set<float>
    "setOfFloat = { 0, 1.0f }", // expected Set<float> and provided Set<float>
    "setOfFloat = { 0.0f, 1.0f }", // expected Set<float> and provided Set<float>
    "setOfFloat = { aFloat, aFloat }", // expected Set<float> and provided Set<float>
    "setOfFloat = Set { }", // expected Set<float> and provided Set<>
    //"setOfFloat = Set { 0 }", // expected Set<float> and provided Set<float>
    "setOfFloat = Set { 0.0f }", // expected Set<float> and provided Set<float>
    "setOfFloat = Set { aFloat }", // expected Set<float> and provided Set<float>
    //"setOfFloat = Set { 0, 1 }", // expected Set<float> and provided Set<float>
    "setOfFloat = Set { 0.0f, 1 }", // expected Set<float> and provided Set<float>
    "setOfFloat = Set { 0, 1.0f }", // expected Set<float> and provided Set<float>
    "setOfFloat = Set { 0.0f, 1.0f }", // expected Set<float> and provided Set<float>
    "setOfFloat = Set { aFloat, aFloat }", // expected Set<float> and provided Set<float>
    "listOfDouble = [ ]", // expected List<double> and provided List<>
    //"listOfDouble = [ 0 ]", // expected List<double> and provided List<double>
    "listOfDouble = [ 0.0 ]", // expected List<double> and provided List<double>
    "listOfDouble = [ aDouble ]", // expected List<double> and provided List<double>
    //"listOfDouble = [ 0, 1 ]", // expected List<double> and provided List<double>
    "listOfDouble = [ 0.0, 1 ]", // expected List<double> and provided List<double>
    "listOfDouble = [ 0, 1.0 ]", // expected List<double> and provided List<double>
    "listOfDouble = [ 0.0, 1.0 ]", // expected List<double> and provided List<double>
    "listOfDouble = [ aDouble, aDouble ]", // expected List<double> and provided List<double>
    "setOfDouble = { }", // expected Set<double> and provided Set<>
    //"setOfDouble = { 0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = { 0.0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = { aDouble }", // expected Set<double> and provided Set<double>
    //"setOfDouble = { 0, 1 }", // expected Set<double> and provided Set<double>
    "setOfDouble = { 0.0, 1 }", // expected Set<double> and provided Set<double>
    "setOfDouble = { 0, 1.0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = { 0.0, 1.0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = { aDouble, aDouble }", // expected Set<double> and provided Set<double>
    "setOfDouble = Set { }", // expected Set<double> and provided Set<>
    //"setOfDouble = Set { 0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = Set { 0.0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = Set { aDouble }", // expected Set<double> and provided Set<double>
    //"setOfDouble = Set { 0, 1 }", // expected Set<double> and provided Set<double>
    "setOfDouble = Set { 0.0, 1 }", // expected Set<double> and provided Set<double>
    "setOfDouble = Set { 0, 1.0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = Set { 0.0, 1.0 }", // expected Set<double> and provided Set<double>
    "setOfDouble = Set { aDouble, aDouble }", // expected Set<double> and provided Set<double>
  })
  public void testValidExpression(@NotNull String expr) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkArgument(!expr.isBlank());

    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    tc.deriveType(ast).getResult();

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be valid, there shouldn't be any findings.")
      .isEmpty();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "aBool isin listOfChar, 0xFD541", // isin not applicable to boolean, List<char>
    "aBool isin setOfChar, 0xFD541", // isin not applicable to boolean, Set<char>
    "aBool isin listOfByte, 0xFD541", // isin not applicable to boolean, List<byte>
    "aBool isin setOfByte, 0xFD541", // isin not applicable to boolean, Set<byte>
    "aBool isin listOfShort, 0xFD541", // isin not applicable to boolean, List<short>
    "aBool isin setOfShort, 0xFD541", // isin not applicable to boolean, Set<short>
    "aBool isin listOfInt, 0xFD541", // isin not applicable to boolean, List<int>
    "aBool isin setOfInt, 0xFD541", // isin not applicable to boolean, Set<int>
    "aBool isin listOfLong, 0xFD541", // isin not applicable to boolean, List<long>
    "aBool isin setOfLong, 0xFD541", // isin not applicable to boolean, Set<long>
    "aBool isin listOfFloat, 0xFD541", // isin not applicable to boolean, List<float>
    "aBool isin setOfFloat, 0xFD541", // isin not applicable to boolean, Set<float>
    "aBool isin listOfDouble, 0xFD541", // isin not applicable to boolean, List<double>
    "aBool isin setOfDouble, 0xFD541", // isin not applicable to boolean, Set<double>
    "aChar isin listOfBool, 0xFD541", // isin not applicable to char, List<boolean>
    "aChar isin setOfBool, 0xFD541", // isin not applicable to char, Set<boolean>
    "aChar isin listOfByte, 0xFD541", // isin not applicable to char, List<byte>
    "aChar isin setOfByte, 0xFD541", // isin not applicable to char, Set<byte>
    "aChar isin listOfShort, 0xFD541", // isin not applicable to char, List<short>
    "aChar isin setOfShort, 0xFD541", // isin not applicable to char, Set<short>
    "aByte isin listOfBool, 0xFD541", // isin not applicable to byte, List<boolean>
    "aByte isin setOfBool, 0xFD541", // isin not applicable to byte, Set<boolean>
    "aByte isin listOfChar, 0xFD541", // isin not applicable to byte, List<char>
    "aByte isin setOfChar, 0xFD541", // isin not applicable to byte, Set<char>
    "aShort isin listOfBool, 0xFD541", // isin not applicable to short, List<boolean>
    "aShort isin setOfBool, 0xFD541", // isin not applicable to short, Set<boolean>
    "aShort isin listOfChar, 0xFD541", // isin not applicable to short, List<char>
    "aShort isin setOfChar, 0xFD541", // isin not applicable to short, Set<char>
    "anInt isin listOfBool, 0xFD541", // isin not applicable to int, List<boolean>
    "anInt isin setOfBool, 0xFD541", // isin not applicable to int, Set<boolean>
    "aLong isin listOfBool, 0xFD541", // isin not applicable to long, List<boolean>
    "aLong isin setOfBool, 0xFD541", // isin not applicable to long, Set<boolean>
    "aFloat isin listOfBool, 0xFD541", // isin not applicable to float, List<boolean>
    "aFloat isin setOfBool, 0xFD541", // isin not applicable to float, Set<boolean>
    "aDouble isin listOfBool, 0xFD541", // isin not applicable to double, List<boolean>
    "aDouble isin setOfBool, 0xFD541", // isin not applicable to double, Set<boolean>
    "aBool notin listOfChar, 0xFD541", // notin not applicable to boolean, List<char>
    "aBool notin setOfChar, 0xFD541", // notin not applicable to boolean, Set<char>
    "aBool notin listOfByte, 0xFD541", // notin not applicable to boolean, List<byte>
    "aBool notin setOfByte, 0xFD541", // notin not applicable to boolean, Set<byte>
    "aBool notin listOfShort, 0xFD541", // notin not applicable to boolean, List<short>
    "aBool notin setOfShort, 0xFD541", // notin not applicable to boolean, Set<short>
    "aBool notin listOfInt, 0xFD541", // notin not applicable to boolean, List<int>
    "aBool notin setOfInt, 0xFD541", // notin not applicable to boolean, Set<int>
    "aBool notin listOfLong, 0xFD541", // notin not applicable to boolean, List<long>
    "aBool notin setOfLong, 0xFD541", // notin not applicable to boolean, Set<long>
    "aBool notin listOfFloat, 0xFD541", // notin not applicable to boolean, List<float>
    "aBool notin setOfFloat, 0xFD541", // notin not applicable to boolean, Set<float>
    "aBool notin listOfDouble, 0xFD541", // notin not applicable to boolean, List<double>
    "aBool notin setOfDouble, 0xFD541", // notin not applicable to boolean, Set<double>
    "aChar notin listOfBool, 0xFD541", // notin not applicable to char, List<boolean>
    "aChar notin setOfBool, 0xFD541", // notin not applicable to char, Set<boolean>
    "aChar notin listOfByte, 0xFD541", // notin not applicable to char, List<byte>
    "aChar notin setOfByte, 0xFD541", // notin not applicable to char, Set<byte>
    "aChar notin listOfShort, 0xFD541", // notin not applicable to char, List<short>
    "aChar notin setOfShort, 0xFD541", // notin not applicable to char, Set<short>
    "aByte notin listOfBool, 0xFD541", // notin not applicable to byte, List<boolean>
    "aByte notin setOfBool, 0xFD541", // notin not applicable to byte, Set<boolean>
    "aByte notin listOfChar, 0xFD541", // notin not applicable to byte, List<char>
    "aByte notin setOfChar, 0xFD541", // notin not applicable to byte, Set<char>
    "aShort notin listOfBool, 0xFD541", // notin not applicable to short, List<boolean>
    "aShort notin setOfBool, 0xFD541", // notin not applicable to short, Set<boolean>
    "aShort notin listOfChar, 0xFD541", // notin not applicable to short, List<char>
    "aShort notin setOfChar, 0xFD541", // notin not applicable to short, Set<char>
    "anInt notin listOfBool, 0xFD541", // notin not applicable to int, List<boolean>
    "anInt notin setOfBool, 0xFD541", // notin not applicable to int, Set<boolean>
    "aLong notin listOfBool, 0xFD541", // notin not applicable to long, List<boolean>
    "aLong notin setOfBool, 0xFD541", // notin not applicable to long, Set<boolean>
    "aFloat notin listOfBool, 0xFD541", // notin not applicable to float, List<boolean>
    "aFloat notin setOfBool, 0xFD541", // notin not applicable to float, Set<boolean>
    "aDouble notin listOfBool, 0xFD541", // notin not applicable to double, List<boolean>
    "aDouble notin setOfBool, 0xFD541", // notin not applicable to double, Set<boolean>
    "listOfBool union listOfChar, 0xFD543", // union not applicable to List<boolean>, List<char>
    "listOfBool union setOfChar, 0xFD543", // union not applicable to List<boolean>, Set<char>
    "setOfBool union listOfChar, 0xFD543", // union not applicable to Set<boolean>, List<char>
    "setOfBool union setOfChar, 0xFD543", // union not applicable to Set<boolean>, Set<char>
    "listOfBool union listOfByte, 0xFD543", // union not applicable to List<boolean>, List<byte>
    "listOfBool union setOfByte, 0xFD543", // union not applicable to List<boolean>, Set<byte>
    "setOfBool union listOfByte, 0xFD543", // union not applicable to Set<boolean>, List<byte>
    "setOfBool union setOfByte, 0xFD543", // union not applicable to Set<boolean>, Set<byte>
    "listOfBool union listOfShort, 0xFD543", // union not applicable to List<boolean>, List<short>
    "listOfBool union setOfShort, 0xFD543", // union not applicable to List<boolean>, Set<short>
    "setOfBool union listOfShort, 0xFD543", // union not applicable to Set<boolean>, List<short>
    "setOfBool union setOfShort, 0xFD543", // union not applicable to Set<boolean>, Set<short>
    "listOfBool union listOfInt, 0xFD543", // union not applicable to List<boolean>, List<int>
    "listOfBool union setOfInt, 0xFD543", // union not applicable to List<boolean>, Set<int>
    "setOfBool union listOfInt, 0xFD543", // union not applicable to Set<boolean>, List<int>
    "setOfBool union setOfInt, 0xFD543", // union not applicable to Set<boolean>, Set<int>
    "listOfBool union listOfLong, 0xFD543", // union not applicable to List<boolean>, List<long>
    "listOfBool union setOfLong, 0xFD543", // union not applicable to List<boolean>, Set<long>
    "setOfBool union listOfLong, 0xFD543", // union not applicable to Set<boolean>, List<long>
    "setOfBool union setOfLong, 0xFD543", // union not applicable to Set<boolean>, Set<long>
    "listOfBool union listOfFloat, 0xFD543", // union not applicable to List<boolean>, List<float>
    "listOfBool union setOfFloat, 0xFD543", // union not applicable to List<boolean>, Set<float>
    "setOfBool union listOfFloat, 0xFD543", // union not applicable to Set<boolean>, List<float>
    "setOfBool union setOfFloat, 0xFD543", // union not applicable to Set<boolean>, Set<float>
    "listOfBool union listOfDouble, 0xFD543", // union not applicable to List<boolean>, List<double>
    "listOfBool union setOfDouble, 0xFD543", // union not applicable to List<boolean>, Set<double>
    "setOfBool union listOfDouble, 0xFD543", // union not applicable to Set<boolean>, List<double>
    "setOfBool union setOfDouble, 0xFD543", // union not applicable to Set<boolean>, Set<double>
    "listOfChar union listOfBool, 0xFD543", // union not applicable to List<char>, List<boolean>
    "listOfChar union setOfBool, 0xFD543", // union not applicable to List<char>, Set<boolean>
    "setOfChar union listOfBool, 0xFD543", // union not applicable to Set<char>, List<boolean>
    "setOfChar union setOfBool, 0xFD543", // union not applicable to Set<char>, Set<boolean>
    "listOfChar union listOfByte, 0xFD543", // union not applicable to List<char>, List<byte>
    "listOfChar union setOfByte, 0xFD543", // union not applicable to List<char>, Set<byte>
    "setOfChar union listOfByte, 0xFD543", // union not applicable to Set<char>, List<byte>
    "setOfChar union setOfByte, 0xFD543", // union not applicable to Set<char>, Set<byte>
    "listOfChar union listOfShort, 0xFD543", // union not applicable to List<char>, List<short>
    "listOfChar union setOfShort, 0xFD543", // union not applicable to List<char>, Set<short>
    "setOfChar union listOfShort, 0xFD543", // union not applicable to Set<char>, List<short>
    "setOfChar union setOfShort, 0xFD543", // union not applicable to Set<char>, Set<short>
    "listOfChar union listOfInt, 0xFD543", // union not applicable to List<char>, List<int>
    "listOfChar union setOfInt, 0xFD543", // union not applicable to List<char>, Set<int>
    "setOfChar union listOfInt, 0xFD543", // union not applicable to Set<char>, List<int>
    "setOfChar union setOfInt, 0xFD543", // union not applicable to Set<char>, Set<int>
    "listOfChar union listOfLong, 0xFD543", // union not applicable to List<char>, List<long>
    "listOfChar union setOfLong, 0xFD543", // union not applicable to List<char>, Set<long>
    "setOfChar union listOfLong, 0xFD543", // union not applicable to Set<char>, List<long>
    "setOfChar union setOfLong, 0xFD543", // union not applicable to Set<char>, Set<long>
    "listOfChar union listOfFloat, 0xFD543", // union not applicable to List<char>, List<float>
    "listOfChar union setOfFloat, 0xFD543", // union not applicable to List<char>, Set<float>
    "setOfChar union listOfFloat, 0xFD543", // union not applicable to Set<char>, List<float>
    "setOfChar union setOfFloat, 0xFD543", // union not applicable to Set<char>, Set<float>
    "listOfChar union listOfDouble, 0xFD543", // union not applicable to List<char>, List<double>
    "listOfChar union setOfDouble, 0xFD543", // union not applicable to List<char>, Set<double>
    "setOfChar union listOfDouble, 0xFD543", // union not applicable to Set<char>, List<double>
    "setOfChar union setOfDouble, 0xFD543", // union not applicable to Set<char>, Set<double>
    "listOfByte union listOfBool, 0xFD543", // union not applicable to List<byte>, List<boolean>
    "listOfByte union setOfBool, 0xFD543", // union not applicable to List<byte>, Set<boolean>
    "setOfByte union listOfBool, 0xFD543", // union not applicable to Set<byte>, List<boolean>
    "setOfByte union setOfBool, 0xFD543", // union not applicable to Set<byte>, Set<boolean>
    "listOfByte union listOfChar, 0xFD543", // union not applicable to List<byte>, List<char>
    "listOfByte union setOfChar, 0xFD543", // union not applicable to List<byte>, Set<char>
    "setOfByte union listOfChar, 0xFD543", // union not applicable to Set<byte>, List<char>
    "setOfByte union setOfChar, 0xFD543", // union not applicable to Set<byte>, Set<char>
    "listOfShort union listOfBool, 0xFD543", // union not applicable to List<short>, List<boolean>
    "listOfShort union setOfBool, 0xFD543", // union not applicable to List<short>, Set<boolean>
    "setOfShort union listOfBool, 0xFD543", // union not applicable to Set<short>, List<boolean>
    "setOfShort union setOfBool, 0xFD543", // union not applicable to Set<short>, Set<boolean>
    "listOfShort union listOfChar, 0xFD543", // union not applicable to List<short>, List<char>
    "listOfShort union setOfChar, 0xFD543", // union not applicable to List<short>, Set<char>
    "setOfShort union listOfChar, 0xFD543", // union not applicable to Set<short>, List<char>
    "setOfShort union setOfChar, 0xFD543", // union not applicable to Set<short>, Set<char>
    "listOfInt union listOfBool, 0xFD543", // union not applicable to List<int>, List<boolean>
    "listOfInt union setOfBool, 0xFD543", // union not applicable to List<int>, Set<boolean>
    "setOfInt union listOfBool, 0xFD543", // union not applicable to Set<int>, List<boolean>
    "setOfInt union setOfBool, 0xFD543", // union not applicable to Set<int>, Set<boolean>
    "listOfInt union listOfChar, 0xFD543", // union not applicable to List<int>, List<char>
    "listOfInt union setOfChar, 0xFD543", // union not applicable to List<int>, Set<char>
    "setOfInt union listOfChar, 0xFD543", // union not applicable to Set<int>, List<char>
    "setOfInt union setOfChar, 0xFD543", // union not applicable to Set<int>, Set<char>
    "listOfLong union listOfBool, 0xFD543", // union not applicable to List<long>, List<boolean>
    "listOfLong union setOfBool, 0xFD543", // union not applicable to List<long>, Set<boolean>
    "setOfLong union listOfBool, 0xFD543", // union not applicable to Set<long>, List<boolean>
    "setOfLong union setOfBool, 0xFD543", // union not applicable to Set<long>, Set<boolean>
    "listOfLong union listOfChar, 0xFD543", // union not applicable to List<long>, List<char>
    "listOfLong union setOfChar, 0xFD543", // union not applicable to List<long>, Set<char>
    "setOfLong union listOfChar, 0xFD543", // union not applicable to Set<long>, List<char>
    "setOfLong union setOfChar, 0xFD543", // union not applicable to Set<long>, Set<char>
    "listOfFloat union listOfBool, 0xFD543", // union not applicable to List<float>, List<boolean>
    "listOfFloat union setOfBool, 0xFD543", // union not applicable to List<float>, Set<boolean>
    "setOfFloat union listOfBool, 0xFD543", // union not applicable to Set<float>, List<boolean>
    "setOfFloat union setOfBool, 0xFD543", // union not applicable to Set<float>, Set<boolean>
    "listOfFloat union listOfChar, 0xFD543", // union not applicable to List<float>, List<char>
    "listOfFloat union setOfChar, 0xFD543", // union not applicable to List<float>, Set<char>
    "setOfFloat union listOfChar, 0xFD543", // union not applicable to Set<float>, List<char>
    "setOfFloat union setOfChar, 0xFD543", // union not applicable to Set<float>, Set<char>
    "listOfDouble union listOfBool, 0xFD543", // union not applicable to List<double>, List<boolean>
    "listOfDouble union setOfBool, 0xFD543", // union not applicable to List<double>, Set<boolean>
    "setOfDouble union listOfBool, 0xFD543", // union not applicable to Set<double>, List<boolean>
    "setOfDouble union setOfBool, 0xFD543", // union not applicable to Set<double>, Set<boolean>
    "listOfDouble union listOfChar, 0xFD543", // union not applicable to List<double>, List<char>
    "listOfDouble union setOfChar, 0xFD543", // union not applicable to List<double>, Set<char>
    "setOfDouble union listOfChar, 0xFD543", // union not applicable to Set<double>, List<char>
    "setOfDouble union setOfChar, 0xFD543", // union not applicable to Set<double>, Set<char>
    "listOfBool intersect listOfBool, 0xFD546", // intersect not applicable to List<boolean>, List<boolean>
    "listOfBool intersect setOfBool, 0xFD546", // intersect not applicable to List<boolean>, Set<boolean>
    "setOfBool intersect listOfBool, 0xFD546", // intersect not applicable to Set<Boolean>, List<Boolean>
    "listOfBool intersect listOfChar, 0xFD546", // intersect not applicable to List<boolean>, List<char>
    "listOfBool intersect listOfChar, 0xFD546", // intersect not applicable to List<boolean>, List<char>
    "setOfBool intersect listOfChar, 0xFD546", // intersect not applicable to List<boolean>, Set<char>
    "setOfBool intersect listOfChar, 0xFD546", // intersect not applicable to List<boolean>, Set<char>
    "listOfBool intersect listOfByte, 0xFD546", // intersect not applicable to List<boolean>, List<byte>
    "listOfBool intersect listOfByte, 0xFD546", // intersect not applicable to List<boolean>, List<byte>
    "setOfBool intersect listOfByte, 0xFD546", // intersect not applicable to List<boolean>, Set<byte>
    "setOfBool intersect listOfByte, 0xFD546", // intersect not applicable to List<boolean>, Set<byte>
    "listOfBool intersect listOfShort, 0xFD546", // intersect not applicable to List<boolean>, List<short>
    "listOfBool intersect listOfShort, 0xFD546", // intersect not applicable to List<boolean>, List<short>
    "setOfBool intersect listOfShort, 0xFD546", // intersect not applicable to List<boolean>, Set<short>
    "setOfBool intersect listOfShort, 0xFD546", // intersect not applicable to List<boolean>, Set<short>
    "listOfBool intersect listOfInt, 0xFD546", // intersect not applicable to List<boolean>, List<int>
    "listOfBool intersect listOfInt, 0xFD546", // intersect not applicable to List<boolean>, List<int>
    "setOfBool intersect listOfInt, 0xFD546", // intersect not applicable to List<boolean>, Set<int>
    "setOfBool intersect listOfInt, 0xFD546", // intersect not applicable to List<boolean>, Set<int>
    "listOfBool intersect listOfLong, 0xFD546", // intersect not applicable to List<boolean>, List<long>
    "listOfBool intersect listOfLong, 0xFD546", // intersect not applicable to List<boolean>, List<long>
    "setOfBool intersect listOfLong, 0xFD546", // intersect not applicable to List<boolean>, Set<long>
    "setOfBool intersect listOfLong, 0xFD546", // intersect not applicable to List<boolean>, Set<long>
    "listOfBool intersect listOfFloat, 0xFD546", // intersect not applicable to List<boolean>, List<float>
    "listOfBool intersect listOfFloat, 0xFD546", // intersect not applicable to List<boolean>, List<float>
    "setOfBool intersect listOfFloat, 0xFD546", // intersect not applicable to List<boolean>, Set<float>
    "setOfBool intersect listOfFloat, 0xFD546", // intersect not applicable to List<boolean>, Set<float>
    "listOfBool intersect listOfDouble, 0xFD546", // intersect not applicable to List<boolean>, List<double>
    "listOfBool intersect listOfDouble, 0xFD546", // intersect not applicable to List<boolean>, List<double>
    "setOfBool intersect listOfDouble, 0xFD546", // intersect not applicable to List<boolean>, Set<double>
    "setOfBool intersect listOfDouble, 0xFD546", // intersect not applicable to List<boolean>, Set<double>
    "listOfChar intersect listOfBool, 0xFD546", // intersect not applicable to List<char>, List<boolean>
    "listOfChar intersect listOfBool, 0xFD546", // intersect not applicable to List<char>, List<boolean>
    "setOfChar intersect listOfBool, 0xFD546", // intersect not applicable to List<char>, Set<boolean>
    "setOfChar intersect listOfBool, 0xFD546", // intersect not applicable to List<char>, Set<boolean>
    "listOfChar intersect listOfChar, 0xFD546", // intersect not applicable to List<char>, List<char>
    "listOfChar intersect setOfChar, 0xFD546", // intersect not applicable to List<char>, Set<char>
    "setOfChar intersect listOfChar, 0xFD546", // intersect not applicable to Set<char>, List<char>
    "listOfChar intersect listOfByte, 0xFD546", // intersect not applicable to List<char>, List<byte>
    "listOfChar intersect listOfByte, 0xFD546", // intersect not applicable to List<char>, List<byte>
    "setOfChar intersect listOfByte, 0xFD546", // intersect not applicable to List<char>, Set<byte>
    "setOfChar intersect listOfByte, 0xFD546", // intersect not applicable to List<char>, Set<byte>
    "listOfChar intersect listOfShort, 0xFD546", // intersect not applicable to List<char>, List<short>
    "listOfChar intersect listOfShort, 0xFD546", // intersect not applicable to List<char>, List<short>
    "setOfChar intersect listOfShort, 0xFD546", // intersect not applicable to List<char>, Set<short>
    "setOfChar intersect listOfShort, 0xFD546", // intersect not applicable to List<char>, Set<short>
    "listOfChar intersect listOfInt, 0xFD546", // intersect not applicable to List<char>, List<int>
    "listOfChar intersect listOfInt, 0xFD546", // intersect not applicable to List<char>, List<int>
    "setOfChar intersect listOfInt, 0xFD546", // intersect not applicable to List<char>, Set<int>
    "setOfChar intersect listOfInt, 0xFD546", // intersect not applicable to List<char>, Set<int>
    "listOfChar intersect listOfLong, 0xFD546", // intersect not applicable to List<char>, List<long>
    "listOfChar intersect listOfLong, 0xFD546", // intersect not applicable to List<char>, List<long>
    "setOfChar intersect listOfLong, 0xFD546", // intersect not applicable to List<char>, Set<long>
    "setOfChar intersect listOfLong, 0xFD546", // intersect not applicable to List<char>, Set<long>
    "listOfChar intersect listOfFloat, 0xFD546", // intersect not applicable to List<char>, List<float>
    "listOfChar intersect listOfFloat, 0xFD546", // intersect not applicable to List<char>, List<float>
    "setOfChar intersect listOfFloat, 0xFD546", // intersect not applicable to List<char>, Set<float>
    "setOfChar intersect listOfFloat, 0xFD546", // intersect not applicable to List<char>, Set<float>
    "listOfChar intersect listOfDouble, 0xFD546", // intersect not applicable to List<char>, List<double>
    "listOfChar intersect listOfDouble, 0xFD546", // intersect not applicable to List<char>, List<double>
    "setOfChar intersect listOfDouble, 0xFD546", // intersect not applicable to List<char>, Set<double>
    "setOfChar intersect listOfDouble, 0xFD546", // intersect not applicable to List<char>, Set<double>
    "listOfByte intersect listOfBool, 0xFD546", // intersect not applicable to List<byte>, List<boolean>
    "listOfByte intersect setOfBool, 0xFD546", // intersect not applicable to List<byte>, Set<boolean>
    "setOfByte intersect listOfBool, 0xFD546", // intersect not applicable to Set<byte>, List<boolean>
    "setOfByte intersect setOfBool, 0xFD540", // intersect not applicable to Set<byte>, Set<boolean>
    "listOfByte intersect listOfChar, 0xFD546", // intersect not applicable to List<byte>, List<char>
    "listOfByte intersect setOfChar, 0xFD546", // intersect not applicable to List<byte>, Set<char>
    "setOfByte intersect listOfChar, 0xFD546", // intersect not applicable to Set<byte>, List<char>
    "setOfByte intersect setOfChar, 0xFD540", // intersect not applicable to Set<byte>, Set<char>
    "listOfByte intersect listOfByte, 0xFD546", // intersect not applicable to List<byte>, List<byte>
    "listOfByte intersect setOfByte, 0xFD546", // intersect not applicable to List<byte>, Set<byte>
    "setOfByte intersect listOfByte, 0xFD546", // intersect not applicable to Set<byte>, List<byte>
    "listOfByte intersect listOfShort, 0xFD546", // intersect not applicable to List<byte>, List<short>
    "listOfByte intersect setOfShort, 0xFD546", // intersect not applicable to List<byte>, Set<short>
    "setOfByte intersect listOfShort, 0xFD546", // intersect not applicable to Set<byte>, List<short>
    "listOfByte intersect listOfInt, 0xFD546", // intersect not applicable to List<byte>, List<int>
    "listOfByte intersect setOfInt, 0xFD546", // intersect not applicable to List<byte>, Set<int>
    "setOfByte intersect listOfInt, 0xFD546", // intersect not applicable to Set<byte>, List<int>
    "listOfByte intersect listOfLong, 0xFD546", // intersect not applicable to List<byte>, List<long>
    "listOfByte intersect setOfLong, 0xFD546", // intersect not applicable to List<byte>, Set<long>
    "setOfByte intersect listOfLong, 0xFD546", // intersect not applicable to Set<byte>, List<long>
    "listOfByte intersect listOfFloat, 0xFD546", // intersect not applicable to List<byte>, List<float>
    "listOfByte intersect setOfFloat, 0xFD546", // intersect not applicable to List<byte>, Set<float>
    "setOfByte intersect listOfFloat, 0xFD546", // intersect not applicable to Set<byte>, List<float>
    "listOfByte intersect listOfDouble, 0xFD546", // intersect not applicable to List<byte>, List<double>
    "listOfByte intersect setOfDouble, 0xFD546", // intersect not applicable to List<byte>, Set<double>
    "setOfByte intersect listOfDouble, 0xFD546", // intersect not applicable to Set<byte>, List<double>
    "listOfShort intersect listOfBool, 0xFD546", // intersect not applicable to List<short>, List<boolean>
    "listOfShort intersect setOfBool, 0xFD546", // intersect not applicable to List<short>, Set<boolean>
    "setOfShort intersect listOfBool, 0xFD546", // intersect not applicable to Set<short>, List<boolean>
    "setOfShort intersect setOfBool, 0xFD540", // intersect not applicable to Set<short>, Set<boolean>
    "listOfShort intersect listOfChar, 0xFD546", // intersect not applicable to List<short>, List<char>
    "listOfShort intersect setOfChar, 0xFD546", // intersect not applicable to List<short>, Set<char>
    "setOfShort intersect listOfChar, 0xFD546", // intersect not applicable to Set<short>, List<char>
    "setOfShort intersect setOfChar, 0xFD540", // intersect not applicable to Set<short>, Set<char>
    "listOfShort intersect listOfByte, 0xFD546", // intersect not applicable to List<short>, List<byte>
    "listOfShort intersect setOfByte, 0xFD546", // intersect not applicable to List<short>, Set<byte>
    "setOfShort intersect listOfByte, 0xFD546", // intersect not applicable to Set<short>, List<byte>
    "listOfShort intersect listOfShort, 0xFD546", // intersect not applicable to List<short>, List<short>
    "listOfShort intersect setOfShort, 0xFD546", // intersect not applicable to List<short>, Set<short>
    "setOfShort intersect listOfShort, 0xFD546", // intersect not applicable to Set<short>, List<short>
    "listOfShort intersect listOfInt, 0xFD546", // intersect not applicable to List<short>, List<int>
    "listOfShort intersect setOfInt, 0xFD546", // intersect not applicable to List<short>, Set<int>
    "setOfShort intersect listOfInt, 0xFD546", // intersect not applicable to Set<short>, List<int>
    "listOfShort intersect listOfLong, 0xFD546", // intersect not applicable to List<short>, List<long>
    "listOfShort intersect setOfLong, 0xFD546", // intersect not applicable to List<short>, Set<long>
    "setOfShort intersect listOfLong, 0xFD546", // intersect not applicable to Set<short>, List<long>
    "listOfShort intersect listOfFloat, 0xFD546", // intersect not applicable to List<short>, List<float>
    "listOfShort intersect setOfFloat, 0xFD546", // intersect not applicable to List<short>, Set<float>
    "setOfShort intersect listOfFloat, 0xFD546", // intersect not applicable to Set<short>, List<float>
    "listOfShort intersect listOfDouble, 0xFD546", // intersect not applicable to List<short>, List<double>
    "listOfShort intersect setOfDouble, 0xFD546", // intersect not applicable to List<short>, Set<double>
    "setOfShort intersect listOfDouble, 0xFD546", // intersect not applicable to Set<short>, List<double>
    "listOfInt intersect listOfBool, 0xFD546", // intersect not applicable to List<int>, List<boolean>
    "listOfInt intersect setOfBool, 0xFD546", // intersect not applicable to List<int>, Set<boolean>
    "setOfInt intersect listOfBool, 0xFD546", // intersect not applicable to Set<int>, List<boolean>
    "setOfInt intersect setOfBool, 0xFD540", // intersect not applicable to Set<int>, Set<boolean>
    "listOfInt intersect listOfChar, 0xFD546", // intersect not applicable to List<int>, List<char>
    "listOfInt intersect setOfChar, 0xFD546", // intersect not applicable to List<int>, Set<char>
    "setOfInt intersect listOfChar, 0xFD546", // intersect not applicable to Set<int>, List<char>
    "setOfInt intersect setOfChar, 0xFD540", // intersect not applicable to Set<int>, Set<char>
    "listOfInt intersect listOfByte, 0xFD546", // intersect not applicable to List<int>, List<byte>
    "listOfInt intersect setOfByte, 0xFD546", // intersect not applicable to List<int>, Set<byte>
    "setOfInt intersect listOfByte, 0xFD546", // intersect not applicable to Set<int>, List<byte>
    "listOfInt intersect listOfShort, 0xFD546", // intersect not applicable to List<int>, List<short>
    "listOfInt intersect setOfShort, 0xFD546", // intersect not applicable to List<int>, Set<short>
    "setOfInt intersect listOfShort, 0xFD546", // intersect not applicable to Set<int>, List<short>
    "listOfInt intersect listOfInt, 0xFD546", // intersect not applicable to List<int>, List<int>
    "listOfInt intersect setOfInt, 0xFD546", // intersect not applicable to List<int>, Set<int>
    "setOfInt intersect listOfInt, 0xFD546", // intersect not applicable to Set<int>, List<int>
    "listOfInt intersect listOfLong, 0xFD546", // intersect not applicable to List<int>, List<long>
    "listOfInt intersect setOfLong, 0xFD546", // intersect not applicable to List<int>, Set<long>
    "setOfInt intersect listOfLong, 0xFD546", // intersect not applicable to Set<int>, List<long>
    "listOfInt intersect listOfFloat, 0xFD546", // intersect not applicable to List<int>, List<float>
    "listOfInt intersect setOfFloat, 0xFD546", // intersect not applicable to List<int>, Set<float>
    "setOfInt intersect listOfFloat, 0xFD546", // intersect not applicable to Set<int>, List<float>
    "listOfInt intersect listOfDouble, 0xFD546", // intersect not applicable to List<int>, List<double>
    "listOfInt intersect setOfDouble, 0xFD546", // intersect not applicable to List<int>, Set<double>
    "setOfInt intersect listOfDouble, 0xFD546", // intersect not applicable to Set<int>, List<double>
    "listOfLong intersect listOfBool, 0xFD546", // intersect not applicable to List<long>, List<boolean>
    "listOfLong intersect setOfBool, 0xFD546", // intersect not applicable to List<long>, Set<boolean>
    "setOfLong intersect listOfBool, 0xFD546", // intersect not applicable to Set<long>, List<boolean>
    "setOfLong intersect setOfBool, 0xFD540", // intersect not applicable to Set<long>, Set<boolean>
    "listOfLong intersect listOfChar, 0xFD546", // intersect not applicable to List<long>, List<char>
    "listOfLong intersect setOfChar, 0xFD546", // intersect not applicable to List<long>, Set<char>
    "setOfLong intersect listOfChar, 0xFD546", // intersect not applicable to Set<long>, List<char>
    "setOfLong intersect setOfChar, 0xFD540", // intersect not applicable to Set<long>, Set<char>
    "listOfLong intersect listOfByte, 0xFD546", // intersect not applicable to List<long>, List<byte>
    "listOfLong intersect setOfByte, 0xFD546", // intersect not applicable to List<long>, Set<byte>
    "setOfLong intersect listOfByte, 0xFD546", // intersect not applicable to Set<long>, List<byte>
    "listOfLong intersect listOfShort, 0xFD546", // intersect not applicable to List<long>, List<short>
    "listOfLong intersect setOfShort, 0xFD546", // intersect not applicable to List<long>, Set<short>
    "setOfLong intersect listOfShort, 0xFD546", // intersect not applicable to Set<long>, List<short>
    "listOfLong intersect listOfInt, 0xFD546", // intersect not applicable to List<long>, List<int>
    "listOfLong intersect setOfInt, 0xFD546", // intersect not applicable to List<long>, Set<int>
    "setOfLong intersect listOfInt, 0xFD546", // intersect not applicable to Set<long>, List<int>
    "listOfLong intersect listOfLong, 0xFD546", // intersect not applicable to List<long>, List<long>
    "listOfLong intersect setOfLong, 0xFD546", // intersect not applicable to List<long>, Set<long>
    "setOfLong intersect listOfLong, 0xFD546", // intersect not applicable to Set<long>, List<long>
    "listOfLong intersect listOfFloat, 0xFD546", // intersect not applicable to List<long>, List<float>
    "listOfLong intersect setOfFloat, 0xFD546", // intersect not applicable to List<long>, Set<float>
    "setOfLong intersect listOfFloat, 0xFD546", // intersect not applicable to Set<long>, List<float>
    "listOfLong intersect listOfDouble, 0xFD546", // intersect not applicable to List<long>, List<double>
    "listOfLong intersect setOfDouble, 0xFD546", // intersect not applicable to List<long>, Set<double>
    "setOfLong intersect listOfDouble, 0xFD546", // intersect not applicable to Set<long>, List<double>
    "listOfFloat intersect listOfBool, 0xFD546", // intersect not applicable to List<float>, List<boolean>
    "listOfFloat intersect setOfBool, 0xFD546", // intersect not applicable to List<float>, Set<boolean>
    "setOfFloat intersect listOfBool, 0xFD546", // intersect not applicable to Set<float>, List<boolean>
    "setOfFloat intersect setOfBool, 0xFD540", // intersect not applicable to Set<float>, Set<boolean>
    "listOfFloat intersect listOfChar, 0xFD546", // intersect not applicable to List<float>, List<char>
    "listOfFloat intersect setOfChar, 0xFD546", // intersect not applicable to List<float>, Set<char>
    "setOfFloat intersect listOfChar, 0xFD546", // intersect not applicable to Set<float>, List<char>
    "setOfFloat intersect setOfChar, 0xFD540", // intersect not applicable to Set<float>, Set<char>
    "listOfFloat intersect listOfByte, 0xFD546", // intersect not applicable to List<float>, List<byte>
    "listOfFloat intersect setOfByte, 0xFD546", // intersect not applicable to List<float>, Set<byte>
    "setOfFloat intersect listOfByte, 0xFD546", // intersect not applicable to Set<float>, List<byte>
    "listOfFloat intersect listOfShort, 0xFD546", // intersect not applicable to List<float>, List<short>
    "listOfFloat intersect setOfShort, 0xFD546", // intersect not applicable to List<float>, Set<short>
    "setOfFloat intersect listOfShort, 0xFD546", // intersect not applicable to Set<float>, List<short>
    "listOfFloat intersect listOfInt, 0xFD546", // intersect not applicable to List<float>, List<int>
    "listOfFloat intersect setOfInt, 0xFD546", // intersect not applicable to List<float>, Set<int>
    "setOfFloat intersect listOfInt, 0xFD546", // intersect not applicable to Set<float>, List<int>
    "listOfFloat intersect listOfLong, 0xFD546", // intersect not applicable to List<float>, List<long>
    "listOfFloat intersect setOfLong, 0xFD546", // intersect not applicable to List<float>, Set<long>
    "setOfFloat intersect listOfLong, 0xFD546", // intersect not applicable to Set<float>, List<long>
    "listOfFloat intersect listOfFloat, 0xFD546", // intersect not applicable to List<float>, List<float>
    "listOfFloat intersect setOfFloat, 0xFD546", // intersect not applicable to List<float>, Set<float>
    "setOfFloat intersect listOfFloat, 0xFD546", // intersect not applicable to Set<float>, List<float>
    "listOfFloat intersect listOfDouble, 0xFD546", // intersect not applicable to List<float>, List<double>
    "listOfFloat intersect setOfDouble, 0xFD546", // intersect not applicable to List<float>, Set<double>
    "setOfFloat intersect listOfDouble, 0xFD546", // intersect not applicable to Set<float>, List<double>
    "listOfDouble intersect listOfBool, 0xFD546", // intersect not applicable to List<double>, List<boolean>
    "listOfDouble intersect setOfBool, 0xFD546", // intersect not applicable to List<double>, Set<boolean>
    "setOfDouble intersect listOfBool, 0xFD546", // intersect not applicable to Set<double>, List<boolean>
    "setOfDouble intersect setOfBool, 0xFD540", // intersect not applicable to Set<double>, Set<boolean>
    "listOfDouble intersect listOfChar, 0xFD546", // intersect not applicable to List<double>, List<char>
    "listOfDouble intersect setOfChar, 0xFD546", // intersect not applicable to List<double>, Set<char>
    "setOfDouble intersect listOfChar, 0xFD546", // intersect not applicable to Set<double>, List<char>
    "setOfDouble intersect setOfChar, 0xFD540", // intersect not applicable to Set<double>, Set<char>
    "listOfDouble intersect listOfByte, 0xFD546", // intersect not applicable to List<double>, List<byte>
    "listOfDouble intersect setOfByte, 0xFD546", // intersect not applicable to List<double>, Set<byte>
    "setOfDouble intersect listOfByte, 0xFD546", // intersect not applicable to Set<double>, List<byte>
    "listOfDouble intersect listOfShort, 0xFD546", // intersect not applicable to List<double>, List<short>
    "listOfDouble intersect setOfShort, 0xFD546", // intersect not applicable to List<double>, Set<short>
    "setOfDouble intersect listOfShort, 0xFD546", // intersect not applicable to Set<double>, List<short>
    "listOfDouble intersect listOfInt, 0xFD546", // intersect not applicable to List<double>, List<int>
    "listOfDouble intersect setOfInt, 0xFD546", // intersect not applicable to List<double>, Set<int>
    "setOfDouble intersect listOfInt, 0xFD546", // intersect not applicable to Set<double>, List<int>
    "listOfDouble intersect listOfLong, 0xFD546", // intersect not applicable to List<double>, List<long>
    "listOfDouble intersect setOfLong, 0xFD546", // intersect not applicable to List<double>, Set<long>
    "setOfDouble intersect listOfLong, 0xFD546", // intersect not applicable to Set<double>, List<long>
    "listOfDouble intersect listOfFloat, 0xFD546", // intersect not applicable to List<double>, List<float>
    "listOfDouble intersect setOfFloat, 0xFD546", // intersect not applicable to List<double>, Set<float>
    "setOfDouble intersect listOfFloat, 0xFD546", // intersect not applicable to Set<double>, List<float>
    "listOfDouble intersect listOfDouble, 0xFD546", // intersect not applicable to List<double>, List<double>
    "listOfDouble intersect setOfDouble, 0xFD546", // intersect not applicable to List<double>, Set<double>
    "setOfDouble intersect listOfDouble, 0xFD546", // intersect not applicable to Set<double>, List<double>
    "listOfBool \\ listOfBool, 0xFD546", // \\ not applicable to List<boolean>, List<boolean>
    "listOfBool \\ setOfBool, 0xFD546", // \\ not applicable to List<boolean>, Set<boolean>
    "setOfBool \\ listOfBool, 0xFD546", // \\ not applicable to Set<Boolean>, List<Boolean>
    "listOfBool \\ listOfChar, 0xFD546", // \\ not applicable to List<boolean>, List<char>
    "listOfBool \\ listOfChar, 0xFD546", // \\ not applicable to List<boolean>, List<char>
    "setOfBool \\ listOfChar, 0xFD546", // \\ not applicable to List<boolean>, Set<char>
    "setOfBool \\ listOfChar, 0xFD546", // \\ not applicable to List<boolean>, Set<char>
    "listOfBool \\ listOfByte, 0xFD546", // \\ not applicable to List<boolean>, List<byte>
    "listOfBool \\ listOfByte, 0xFD546", // \\ not applicable to List<boolean>, List<byte>
    "setOfBool \\ listOfByte, 0xFD546", // \\ not applicable to List<boolean>, Set<byte>
    "setOfBool \\ listOfByte, 0xFD546", // \\ not applicable to List<boolean>, Set<byte>
    "listOfBool \\ listOfShort, 0xFD546", // \\ not applicable to List<boolean>, List<short>
    "listOfBool \\ listOfShort, 0xFD546", // \\ not applicable to List<boolean>, List<short>
    "setOfBool \\ listOfShort, 0xFD546", // \\ not applicable to List<boolean>, Set<short>
    "setOfBool \\ listOfShort, 0xFD546", // \\ not applicable to List<boolean>, Set<short>
    "listOfBool \\ listOfInt, 0xFD546", // \\ not applicable to List<boolean>, List<int>
    "listOfBool \\ listOfInt, 0xFD546", // \\ not applicable to List<boolean>, List<int>
    "setOfBool \\ listOfInt, 0xFD546", // \\ not applicable to List<boolean>, Set<int>
    "setOfBool \\ listOfInt, 0xFD546", // \\ not applicable to List<boolean>, Set<int>
    "listOfBool \\ listOfLong, 0xFD546", // \\ not applicable to List<boolean>, List<long>
    "listOfBool \\ listOfLong, 0xFD546", // \\ not applicable to List<boolean>, List<long>
    "setOfBool \\ listOfLong, 0xFD546", // \\ not applicable to List<boolean>, Set<long>
    "setOfBool \\ listOfLong, 0xFD546", // \\ not applicable to List<boolean>, Set<long>
    "listOfBool \\ listOfFloat, 0xFD546", // \\ not applicable to List<boolean>, List<float>
    "listOfBool \\ listOfFloat, 0xFD546", // \\ not applicable to List<boolean>, List<float>
    "setOfBool \\ listOfFloat, 0xFD546", // \\ not applicable to List<boolean>, Set<float>
    "setOfBool \\ listOfFloat, 0xFD546", // \\ not applicable to List<boolean>, Set<float>
    "listOfBool \\ listOfDouble, 0xFD546", // \\ not applicable to List<boolean>, List<double>
    "listOfBool \\ listOfDouble, 0xFD546", // \\ not applicable to List<boolean>, List<double>
    "setOfBool \\ listOfDouble, 0xFD546", // \\ not applicable to List<boolean>, Set<double>
    "setOfBool \\ listOfDouble, 0xFD546", // \\ not applicable to List<boolean>, Set<double>
    "listOfChar \\ listOfBool, 0xFD546", // \\ not applicable to List<char>, List<boolean>
    "listOfChar \\ listOfBool, 0xFD546", // \\ not applicable to List<char>, List<boolean>
    "setOfChar \\ listOfBool, 0xFD546", // \\ not applicable to List<char>, Set<boolean>
    "setOfChar \\ listOfBool, 0xFD546", // \\ not applicable to List<char>, Set<boolean>
    "listOfChar \\ listOfChar, 0xFD546", // \\ not applicable to List<char>, List<char>
    "listOfChar \\ setOfChar, 0xFD546", // \\ not applicable to List<char>, Set<char>
    "setOfChar \\ listOfChar, 0xFD546", // \\ not applicable to Set<char>, List<char>
    "listOfChar \\ listOfByte, 0xFD546", // \\ not applicable to List<char>, List<byte>
    "listOfChar \\ listOfByte, 0xFD546", // \\ not applicable to List<char>, List<byte>
    "setOfChar \\ listOfByte, 0xFD546", // \\ not applicable to List<char>, Set<byte>
    "setOfChar \\ listOfByte, 0xFD546", // \\ not applicable to List<char>, Set<byte>
    "listOfChar \\ listOfShort, 0xFD546", // \\ not applicable to List<char>, List<short>
    "listOfChar \\ listOfShort, 0xFD546", // \\ not applicable to List<char>, List<short>
    "setOfChar \\ listOfShort, 0xFD546", // \\ not applicable to List<char>, Set<short>
    "setOfChar \\ listOfShort, 0xFD546", // \\ not applicable to List<char>, Set<short>
    "listOfChar \\ listOfInt, 0xFD546", // \\ not applicable to List<char>, List<int>
    "listOfChar \\ listOfInt, 0xFD546", // \\ not applicable to List<char>, List<int>
    "setOfChar \\ listOfInt, 0xFD546", // \\ not applicable to List<char>, Set<int>
    "setOfChar \\ listOfInt, 0xFD546", // \\ not applicable to List<char>, Set<int>
    "listOfChar \\ listOfLong, 0xFD546", // \\ not applicable to List<char>, List<long>
    "listOfChar \\ listOfLong, 0xFD546", // \\ not applicable to List<char>, List<long>
    "setOfChar \\ listOfLong, 0xFD546", // \\ not applicable to List<char>, Set<long>
    "setOfChar \\ listOfLong, 0xFD546", // \\ not applicable to List<char>, Set<long>
    "listOfChar \\ listOfFloat, 0xFD546", // \\ not applicable to List<char>, List<float>
    "listOfChar \\ listOfFloat, 0xFD546", // \\ not applicable to List<char>, List<float>
    "setOfChar \\ listOfFloat, 0xFD546", // \\ not applicable to List<char>, Set<float>
    "setOfChar \\ listOfFloat, 0xFD546", // \\ not applicable to List<char>, Set<float>
    "listOfChar \\ listOfDouble, 0xFD546", // \\ not applicable to List<char>, List<double>
    "listOfChar \\ listOfDouble, 0xFD546", // \\ not applicable to List<char>, List<double>
    "setOfChar \\ listOfDouble, 0xFD546", // \\ not applicable to List<char>, Set<double>
    "setOfChar \\ listOfDouble, 0xFD546", // \\ not applicable to List<char>, Set<double>
    "listOfByte \\ listOfBool, 0xFD546", // \\ not applicable to List<byte>, List<boolean>
    "listOfByte \\ setOfBool, 0xFD546", // \\ not applicable to List<byte>, Set<boolean>
    "setOfByte \\ listOfBool, 0xFD546", // \\ not applicable to Set<byte>, List<boolean>
    "setOfByte \\ setOfBool, 0xFD540", // \\ not applicable to Set<byte>, Set<boolean>
    "listOfByte \\ listOfChar, 0xFD546", // \\ not applicable to List<byte>, List<char>
    "listOfByte \\ setOfChar, 0xFD546", // \\ not applicable to List<byte>, Set<char>
    "setOfByte \\ listOfChar, 0xFD546", // \\ not applicable to Set<byte>, List<char>
    "setOfByte \\ setOfChar, 0xFD540", // \\ not applicable to Set<byte>, Set<char>
    "listOfByte \\ listOfByte, 0xFD546", // \\ not applicable to List<byte>, List<byte>
    "listOfByte \\ setOfByte, 0xFD546", // \\ not applicable to List<byte>, Set<byte>
    "setOfByte \\ listOfByte, 0xFD546", // \\ not applicable to Set<byte>, List<byte>
    "listOfByte \\ listOfShort, 0xFD546", // \\ not applicable to List<byte>, List<short>
    "listOfByte \\ setOfShort, 0xFD546", // \\ not applicable to List<byte>, Set<short>
    "setOfByte \\ listOfShort, 0xFD546", // \\ not applicable to Set<byte>, List<short>
    "listOfByte \\ listOfInt, 0xFD546", // \\ not applicable to List<byte>, List<int>
    "listOfByte \\ setOfInt, 0xFD546", // \\ not applicable to List<byte>, Set<int>
    "setOfByte \\ listOfInt, 0xFD546", // \\ not applicable to Set<byte>, List<int>
    "listOfByte \\ listOfLong, 0xFD546", // \\ not applicable to List<byte>, List<long>
    "listOfByte \\ setOfLong, 0xFD546", // \\ not applicable to List<byte>, Set<long>
    "setOfByte \\ listOfLong, 0xFD546", // \\ not applicable to Set<byte>, List<long>
    "listOfByte \\ listOfFloat, 0xFD546", // \\ not applicable to List<byte>, List<float>
    "listOfByte \\ setOfFloat, 0xFD546", // \\ not applicable to List<byte>, Set<float>
    "setOfByte \\ listOfFloat, 0xFD546", // \\ not applicable to Set<byte>, List<float>
    "listOfByte \\ listOfDouble, 0xFD546", // \\ not applicable to List<byte>, List<double>
    "listOfByte \\ setOfDouble, 0xFD546", // \\ not applicable to List<byte>, Set<double>
    "setOfByte \\ listOfDouble, 0xFD546", // \\ not applicable to Set<byte>, List<double>
    "listOfShort \\ listOfBool, 0xFD546", // \\ not applicable to List<short>, List<boolean>
    "listOfShort \\ setOfBool, 0xFD546", // \\ not applicable to List<short>, Set<boolean>
    "setOfShort \\ listOfBool, 0xFD546", // \\ not applicable to Set<short>, List<boolean>
    "setOfShort \\ setOfBool, 0xFD540", // \\ not applicable to Set<short>, Set<boolean>
    "listOfShort \\ listOfChar, 0xFD546", // \\ not applicable to List<short>, List<char>
    "listOfShort \\ setOfChar, 0xFD546", // \\ not applicable to List<short>, Set<char>
    "setOfShort \\ listOfChar, 0xFD546", // \\ not applicable to Set<short>, List<char>
    "setOfShort \\ setOfChar, 0xFD540", // \\ not applicable to Set<short>, Set<char>
    "listOfShort \\ listOfByte, 0xFD546", // \\ not applicable to List<short>, List<byte>
    "listOfShort \\ setOfByte, 0xFD546", // \\ not applicable to List<short>, Set<byte>
    "setOfShort \\ listOfByte, 0xFD546", // \\ not applicable to Set<short>, List<byte>
    "listOfShort \\ listOfShort, 0xFD546", // \\ not applicable to List<short>, List<short>
    "listOfShort \\ setOfShort, 0xFD546", // \\ not applicable to List<short>, Set<short>
    "setOfShort \\ listOfShort, 0xFD546", // \\ not applicable to Set<short>, List<short>
    "listOfShort \\ listOfInt, 0xFD546", // \\ not applicable to List<short>, List<int>
    "listOfShort \\ setOfInt, 0xFD546", // \\ not applicable to List<short>, Set<int>
    "setOfShort \\ listOfInt, 0xFD546", // \\ not applicable to Set<short>, List<int>
    "listOfShort \\ listOfLong, 0xFD546", // \\ not applicable to List<short>, List<long>
    "listOfShort \\ setOfLong, 0xFD546", // \\ not applicable to List<short>, Set<long>
    "setOfShort \\ listOfLong, 0xFD546", // \\ not applicable to Set<short>, List<long>
    "listOfShort \\ listOfFloat, 0xFD546", // \\ not applicable to List<short>, List<float>
    "listOfShort \\ setOfFloat, 0xFD546", // \\ not applicable to List<short>, Set<float>
    "setOfShort \\ listOfFloat, 0xFD546", // \\ not applicable to Set<short>, List<float>
    "listOfShort \\ listOfDouble, 0xFD546", // \\ not applicable to List<short>, List<double>
    "listOfShort \\ setOfDouble, 0xFD546", // \\ not applicable to List<short>, Set<double>
    "setOfShort \\ listOfDouble, 0xFD546", // \\ not applicable to Set<short>, List<double>
    "listOfInt \\ listOfBool, 0xFD546", // \\ not applicable to List<int>, List<boolean>
    "listOfInt \\ setOfBool, 0xFD546", // \\ not applicable to List<int>, Set<boolean>
    "setOfInt \\ listOfBool, 0xFD546", // \\ not applicable to Set<int>, List<boolean>
    "setOfInt \\ setOfBool, 0xFD540", // \\ not applicable to Set<int>, Set<boolean>
    "listOfInt \\ listOfChar, 0xFD546", // \\ not applicable to List<int>, List<char>
    "listOfInt \\ setOfChar, 0xFD546", // \\ not applicable to List<int>, Set<char>
    "setOfInt \\ listOfChar, 0xFD546", // \\ not applicable to Set<int>, List<char>
    "setOfInt \\ setOfChar, 0xFD540", // \\ not applicable to Set<int>, Set<char>
    "listOfInt \\ listOfByte, 0xFD546", // \\ not applicable to List<int>, List<byte>
    "listOfInt \\ setOfByte, 0xFD546", // \\ not applicable to List<int>, Set<byte>
    "setOfInt \\ listOfByte, 0xFD546", // \\ not applicable to Set<int>, List<byte>
    "listOfInt \\ listOfShort, 0xFD546", // \\ not applicable to List<int>, List<short>
    "listOfInt \\ setOfShort, 0xFD546", // \\ not applicable to List<int>, Set<short>
    "setOfInt \\ listOfShort, 0xFD546", // \\ not applicable to Set<int>, List<short>
    "listOfInt \\ listOfInt, 0xFD546", // \\ not applicable to List<int>, List<int>
    "listOfInt \\ setOfInt, 0xFD546", // \\ not applicable to List<int>, Set<int>
    "setOfInt \\ listOfInt, 0xFD546", // \\ not applicable to Set<int>, List<int>
    "listOfInt \\ listOfLong, 0xFD546", // \\ not applicable to List<int>, List<long>
    "listOfInt \\ setOfLong, 0xFD546", // \\ not applicable to List<int>, Set<long>
    "setOfInt \\ listOfLong, 0xFD546", // \\ not applicable to Set<int>, List<long>
    "listOfInt \\ listOfFloat, 0xFD546", // \\ not applicable to List<int>, List<float>
    "listOfInt \\ setOfFloat, 0xFD546", // \\ not applicable to List<int>, Set<float>
    "setOfInt \\ listOfFloat, 0xFD546", // \\ not applicable to Set<int>, List<float>
    "listOfInt \\ listOfDouble, 0xFD546", // \\ not applicable to List<int>, List<double>
    "listOfInt \\ setOfDouble, 0xFD546", // \\ not applicable to List<int>, Set<double>
    "setOfInt \\ listOfDouble, 0xFD546", // \\ not applicable to Set<int>, List<double>
    "listOfLong \\ listOfBool, 0xFD546", // \\ not applicable to List<long>, List<boolean>
    "listOfLong \\ setOfBool, 0xFD546", // \\ not applicable to List<long>, Set<boolean>
    "setOfLong \\ listOfBool, 0xFD546", // \\ not applicable to Set<long>, List<boolean>
    "setOfLong \\ setOfBool, 0xFD540", // \\ not applicable to Set<long>, Set<boolean>
    "listOfLong \\ listOfChar, 0xFD546", // \\ not applicable to List<long>, List<char>
    "listOfLong \\ setOfChar, 0xFD546", // \\ not applicable to List<long>, Set<char>
    "setOfLong \\ listOfChar, 0xFD546", // \\ not applicable to Set<long>, List<char>
    "setOfLong \\ setOfChar, 0xFD540", // \\ not applicable to Set<long>, Set<char>
    "listOfLong \\ listOfByte, 0xFD546", // \\ not applicable to List<long>, List<byte>
    "listOfLong \\ setOfByte, 0xFD546", // \\ not applicable to List<long>, Set<byte>
    "setOfLong \\ listOfByte, 0xFD546", // \\ not applicable to Set<long>, List<byte>
    "listOfLong \\ listOfShort, 0xFD546", // \\ not applicable to List<long>, List<short>
    "listOfLong \\ setOfShort, 0xFD546", // \\ not applicable to List<long>, Set<short>
    "setOfLong \\ listOfShort, 0xFD546", // \\ not applicable to Set<long>, List<short>
    "listOfLong \\ listOfInt, 0xFD546", // \\ not applicable to List<long>, List<int>
    "listOfLong \\ setOfInt, 0xFD546", // \\ not applicable to List<long>, Set<int>
    "setOfLong \\ listOfInt, 0xFD546", // \\ not applicable to Set<long>, List<int>
    "listOfLong \\ listOfLong, 0xFD546", // \\ not applicable to List<long>, List<long>
    "listOfLong \\ setOfLong, 0xFD546", // \\ not applicable to List<long>, Set<long>
    "setOfLong \\ listOfLong, 0xFD546", // \\ not applicable to Set<long>, List<long>
    "listOfLong \\ listOfFloat, 0xFD546", // \\ not applicable to List<long>, List<float>
    "listOfLong \\ setOfFloat, 0xFD546", // \\ not applicable to List<long>, Set<float>
    "setOfLong \\ listOfFloat, 0xFD546", // \\ not applicable to Set<long>, List<float>
    "listOfLong \\ listOfDouble, 0xFD546", // \\ not applicable to List<long>, List<double>
    "listOfLong \\ setOfDouble, 0xFD546", // \\ not applicable to List<long>, Set<double>
    "setOfLong \\ listOfDouble, 0xFD546", // \\ not applicable to Set<long>, List<double>
    "listOfFloat \\ listOfBool, 0xFD546", // \\ not applicable to List<float>, List<boolean>
    "listOfFloat \\ setOfBool, 0xFD546", // \\ not applicable to List<float>, Set<boolean>
    "setOfFloat \\ listOfBool, 0xFD546", // \\ not applicable to Set<float>, List<boolean>
    "setOfFloat \\ setOfBool, 0xFD540", // \\ not applicable to Set<float>, Set<boolean>
    "listOfFloat \\ listOfChar, 0xFD546", // \\ not applicable to List<float>, List<char>
    "listOfFloat \\ setOfChar, 0xFD546", // \\ not applicable to List<float>, Set<char>
    "setOfFloat \\ listOfChar, 0xFD546", // \\ not applicable to Set<float>, List<char>
    "setOfFloat \\ setOfChar, 0xFD540", // \\ not applicable to Set<float>, Set<char>
    "listOfFloat \\ listOfByte, 0xFD546", // \\ not applicable to List<float>, List<byte>
    "listOfFloat \\ setOfByte, 0xFD546", // \\ not applicable to List<float>, Set<byte>
    "setOfFloat \\ listOfByte, 0xFD546", // \\ not applicable to Set<float>, List<byte>
    "listOfFloat \\ listOfShort, 0xFD546", // \\ not applicable to List<float>, List<short>
    "listOfFloat \\ setOfShort, 0xFD546", // \\ not applicable to List<float>, Set<short>
    "setOfFloat \\ listOfShort, 0xFD546", // \\ not applicable to Set<float>, List<short>
    "listOfFloat \\ listOfInt, 0xFD546", // \\ not applicable to List<float>, List<int>
    "listOfFloat \\ setOfInt, 0xFD546", // \\ not applicable to List<float>, Set<int>
    "setOfFloat \\ listOfInt, 0xFD546", // \\ not applicable to Set<float>, List<int>
    "listOfFloat \\ listOfLong, 0xFD546", // \\ not applicable to List<float>, List<long>
    "listOfFloat \\ setOfLong, 0xFD546", // \\ not applicable to List<float>, Set<long>
    "setOfFloat \\ listOfLong, 0xFD546", // \\ not applicable to Set<float>, List<long>
    "listOfFloat \\ listOfFloat, 0xFD546", // \\ not applicable to List<float>, List<float>
    "listOfFloat \\ setOfFloat, 0xFD546", // \\ not applicable to List<float>, Set<float>
    "setOfFloat \\ listOfFloat, 0xFD546", // \\ not applicable to Set<float>, List<float>
    "listOfFloat \\ listOfDouble, 0xFD546", // \\ not applicable to List<float>, List<double>
    "listOfFloat \\ setOfDouble, 0xFD546", // \\ not applicable to List<float>, Set<double>
    "setOfFloat \\ listOfDouble, 0xFD546", // \\ not applicable to Set<float>, List<double>
    "listOfDouble \\ listOfBool, 0xFD546", // \\ not applicable to List<double>, List<boolean>
    "listOfDouble \\ setOfBool, 0xFD546", // \\ not applicable to List<double>, Set<boolean>
    "setOfDouble \\ listOfBool, 0xFD546", // \\ not applicable to Set<double>, List<boolean>
    "setOfDouble \\ setOfBool, 0xFD540", // \\ not applicable to Set<double>, Set<boolean>
    "listOfDouble \\ listOfChar, 0xFD546", // \\ not applicable to List<double>, List<char>
    "listOfDouble \\ setOfChar, 0xFD546", // \\ not applicable to List<double>, Set<char>
    "setOfDouble \\ listOfChar, 0xFD546", // \\ not applicable to Set<double>, List<char>
    "setOfDouble \\ setOfChar, 0xFD540", // \\ not applicable to Set<double>, Set<char>
    "listOfDouble \\ listOfByte, 0xFD546", // \\ not applicable to List<double>, List<byte>
    "listOfDouble \\ setOfByte, 0xFD546", // \\ not applicable to List<double>, Set<byte>
    "setOfDouble \\ listOfByte, 0xFD546", // \\ not applicable to Set<double>, List<byte>
    "listOfDouble \\ listOfShort, 0xFD546", // \\ not applicable to List<double>, List<short>
    "listOfDouble \\ setOfShort, 0xFD546", // \\ not applicable to List<double>, Set<short>
    "setOfDouble \\ listOfShort, 0xFD546", // \\ not applicable to Set<double>, List<short>
    "listOfDouble \\ listOfInt, 0xFD546", // \\ not applicable to List<double>, List<int>
    "listOfDouble \\ setOfInt, 0xFD546", // \\ not applicable to List<double>, Set<int>
    "setOfDouble \\ listOfInt, 0xFD546", // \\ not applicable to Set<double>, List<int>
    "listOfDouble \\ listOfLong, 0xFD546", // \\ not applicable to List<double>, List<long>
    "listOfDouble \\ setOfLong, 0xFD546", // \\ not applicable to List<double>, Set<long>
    "setOfDouble \\ listOfLong, 0xFD546", // \\ not applicable to Set<double>, List<long>
    "listOfDouble \\ listOfFloat, 0xFD546", // \\ not applicable to List<double>, List<float>
    "listOfDouble \\ setOfFloat, 0xFD546", // \\ not applicable to List<double>, Set<float>
    "setOfDouble \\ listOfFloat, 0xFD546", // \\ not applicable to Set<double>, List<float>
    "listOfDouble \\ listOfDouble, 0xFD546", // \\ not applicable to List<double>, List<double>
    "listOfDouble \\ setOfDouble, 0xFD546", // \\ not applicable to List<double>, Set<double>
    "setOfDouble \\ listOfDouble, 0xFD546", // \\ not applicable to Set<double>, List<double>
    "listOfBool = [ 'a' ], 0xFD451", // expected List<boolean> but provided List<char>
    "listOfBool = [ 1 ], 0xFD451", // expected List<boolean> but provided List<int>
    "listOfBool = [ 1l ], 0xFD451", // expected List<boolean> but provided List<long>
    "listOfBool = [ 1.0f ], 0xFD451", // expected List<boolean> but provided List<float>
    "listOfBool = [ 1.0 ], 0xFD451", // expected List<boolean> but provided List<double>
    "setOfBool = { 'a' }, 0xFD451", // expected Set<boolean but provided Set<char>
    "setOfBool = { 1 }, 0xFD451", // expected Set<boolean but provided Set<int>
    "setOfBool = { 1l }, 0xFD451", // expected Set<boolean but provided Set<long>
    "setOfBool = { 1.0f }, 0xFD451", // expected Set<boolean> but provided Set<float>
    "setOfBool = { 1.0 }, 0xFD451", // expected Set<boolean> but provided Set<double>
    "setOfBool = Set { 'a' }, 0xFD451", // expected Set<boolean but provided Set<char>
    "setOfBool = Set { 1 }, 0xFD451", // expected Set<boolean but provided Set<int>
    "setOfBool = Set { 1l }, 0xFD451", // expected Set<boolean but provided Set<long>
    "setOfBool = Set { 1.0f }, 0xFD451", // expected Set<boolean> but provided Set<float>
    "setOfBool = Set { 1.0 }, 0xFD451", // expected Set<boolean> but provided Set<double>
  })
  public void testInvalidExpression(@NotNull String expr,
                                    @NotNull String error) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(error);
    Preconditions.checkArgument(!expr.isBlank());
    Preconditions.checkArgument(!error.isBlank());

    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    tc.deriveType(ast).getResult();

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be invalid, there should be findings.")
      .isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings()))
      .as("Expression " + expr + " is invalid, the findings should contain exactly the expected error.")
      .containsExactly(error);
  }
}
