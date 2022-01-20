/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._visitor.IFullPrettyPrinter;
import montiarc._symboltable.MontiArcSymbolTableCompleter;
import montiarc._symboltable.MontiArcSymbolTableCompleterDelegator;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import variablearc.VariableArcMill;

import java.util.function.Supplier;

public class MontiArcMill extends MontiArcMillTOP {

  protected static MontiArcMill millMontiArcSymbolTableCompleter ;

  protected static MontiArcMill millMontiArcSymbolTableCompleterDelegator;

  protected static MontiArcMill millMontiArcFullPrettyPrinter;
  protected static Supplier<IFullPrettyPrinter> supplierFullPrettyPrinter;

  public static MontiArcSymbolTableCompleter symbolTableCompleter ()  {
    if (millMontiArcSymbolTableCompleter == null) {
      millMontiArcSymbolTableCompleter = getMill();
    }
    return millMontiArcSymbolTableCompleter._symbolTableCompleter();
  }

  protected MontiArcSymbolTableCompleter _symbolTableCompleter() {
    return new MontiArcSymbolTableCompleter();
  }

  public static MontiArcSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millMontiArcSymbolTableCompleterDelegator == null) {
      millMontiArcSymbolTableCompleterDelegator = getMill();
    }
    return millMontiArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected MontiArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new MontiArcSymbolTableCompleterDelegator();
  }

  public static IFullPrettyPrinter variableArcFullPrettyPrinter() {
    if (millMontiArcFullPrettyPrinter == null) {
      millMontiArcFullPrettyPrinter = getMill();
    }
    if (supplierFullPrettyPrinter == null){
      supplierFullPrettyPrinter = millMontiArcFullPrettyPrinter::_montiArcFullPrettyPrinter;
    }
    return supplierFullPrettyPrinter.get();
  }

  protected IFullPrettyPrinter _montiArcFullPrettyPrinter() {
    return new MontiArcFullPrettyPrinter();
  }

  public static void initMe(MontiArcMill a)  {
    MontiArcMillTOP.initMe(a);
    millMontiArcSymbolTableCompleter = a;
    millMontiArcSymbolTableCompleterDelegator = a;
    millMontiArcFullPrettyPrinter = a;

    VariableArcMill.setSupplierFullPrettyPrinter(a::_montiArcFullPrettyPrinter);
  }

  public static void reset() {
    MontiArcMillTOP.reset();
    millMontiArcSymbolTableCompleter = null;
    millMontiArcSymbolTableCompleterDelegator = null;
    millMontiArcFullPrettyPrinter = null;
  }
}