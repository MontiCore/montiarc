/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import arcbasis._visitor.IFullPrettyPrinter;
import variablearc._symboltable.VariableArcSymbolTableCompleter;
import variablearc._symboltable.VariableArcSymbolTableCompleterDelegator;
import variablearc._visitor.VariableArcFullPrettyPrinter;

import java.util.function.Supplier;

public class VariableArcMill extends VariableArcMillTOP {

  protected static VariableArcMill millVariableArcSymbolTableCompleter;

  protected static VariableArcMill millVariableArcSymbolTableCompleterDelegator;

  protected static VariableArcMill millVariableArcFullPrettyPrinter;
  protected static Supplier<IFullPrettyPrinter> supplierFullPrettyPrinter;

  public static VariableArcSymbolTableCompleter symbolTableCompleter() {
    if (millVariableArcSymbolTableCompleter == null) {
      millVariableArcSymbolTableCompleter = getMill();
    }
    return millVariableArcSymbolTableCompleter._symbolTableCompleter();
  }

  public static VariableArcSymbolTableCompleterDelegator symbolTableCompleterDelegator() {
    if (millVariableArcSymbolTableCompleterDelegator == null) {
      millVariableArcSymbolTableCompleterDelegator = getMill();
    }
    return millVariableArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  public static IFullPrettyPrinter variableArcFullPrettyPrinter() {
    if (millVariableArcFullPrettyPrinter == null) {
      millVariableArcFullPrettyPrinter = getMill();
    }
    if (supplierFullPrettyPrinter == null) {
      supplierFullPrettyPrinter = millVariableArcFullPrettyPrinter::_variableArcFullPrettyPrinter;
    }
    return supplierFullPrettyPrinter.get();
  }

  public static void setSupplierFullPrettyPrinter(Supplier<IFullPrettyPrinter> pp) {
    supplierFullPrettyPrinter = pp;
  }

  public static void initMe(VariableArcMill a) {
    VariableArcMillTOP.initMe(a);
    millVariableArcSymbolTableCompleter = a;
    millVariableArcSymbolTableCompleterDelegator = a;
    millVariableArcFullPrettyPrinter = a;
    supplierFullPrettyPrinter = a::_variableArcFullPrettyPrinter;
  }

  public static void reset() {
    VariableArcMillTOP.reset();
    millVariableArcSymbolTableCompleter = null;
    millVariableArcSymbolTableCompleterDelegator = null;
    millVariableArcFullPrettyPrinter = null;
    supplierFullPrettyPrinter = null;
  }

  protected VariableArcSymbolTableCompleter _symbolTableCompleter() {
    return new VariableArcSymbolTableCompleter();
  }

  protected VariableArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new VariableArcSymbolTableCompleterDelegator();
  }

  protected IFullPrettyPrinter _variableArcFullPrettyPrinter() {
    return new VariableArcFullPrettyPrinter();
  }
}