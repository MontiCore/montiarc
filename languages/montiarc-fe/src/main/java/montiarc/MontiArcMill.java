/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc._symboltable.MontiArcSymbolTableCompleter;
import montiarc._symboltable.MontiArcSymbolTableCompleterDelegator;

public class MontiArcMill extends MontiArcMillTOP {

  protected static MontiArcMill millMontiArcSymbolTableCompleter ;

  protected static MontiArcMill millMontiArcSymbolTableCompleterDelegator;

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

  public static void initMe(MontiArcMill a)  {
    MontiArcMillTOP.initMe(a);
    millMontiArcSymbolTableCompleter = a;
    millMontiArcSymbolTableCompleterDelegator = a;
  }

  public static void reset() {
    MontiArcMillTOP.reset();
    millMontiArcSymbolTableCompleter = null;
    millMontiArcSymbolTableCompleterDelegator = null;
  }
}