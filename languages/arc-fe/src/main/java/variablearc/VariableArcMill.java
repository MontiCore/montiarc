/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import variablearc._symboltable.VariableArcSymbolTableCompleter;
import variablearc._symboltable.VariableArcSymbolTableCompleterDelegator;

public class VariableArcMill extends VariableArcMillTOP {

  protected static VariableArcMill millVariableArcSymbolTableCompleter ;

  protected static VariableArcMill millVariableArcSymbolTableCompleterDelegator;

  public static VariableArcSymbolTableCompleter symbolTableCompleter ()  {
    if (millVariableArcSymbolTableCompleter == null) {
      millVariableArcSymbolTableCompleter = getMill();
    }
    return millVariableArcSymbolTableCompleter._symbolTableCompleter();
  }

  protected VariableArcSymbolTableCompleter _symbolTableCompleter() {
    return new VariableArcSymbolTableCompleter();
  }

  public static VariableArcSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millVariableArcSymbolTableCompleterDelegator == null) {
      millVariableArcSymbolTableCompleterDelegator = getMill();
    }
    return millVariableArcSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected VariableArcSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new VariableArcSymbolTableCompleterDelegator();
  }

  public static void initMe(VariableArcMill a)  {
    VariableArcMillTOP.initMe(a);
    millVariableArcSymbolTableCompleter = a;
    millVariableArcSymbolTableCompleterDelegator = a;
  }

  public static void reset() {
    VariableArcMillTOP.reset();
    millVariableArcSymbolTableCompleter = null;
    millVariableArcSymbolTableCompleterDelegator = null;
  }
}