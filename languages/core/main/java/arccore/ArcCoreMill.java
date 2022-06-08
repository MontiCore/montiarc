/* (c) https://github.com/MontiCore/monticore */
package arccore;

import arcbasis._visitor.IFullPrettyPrinter;
import arccore._symboltable.ArcCoreSymbolTableCompleter;
import arccore._symboltable.ArcCoreSymbolTableCompleterDelegator;
import arccore._visitor.ArcCoreFullPrettyPrinter;

public class ArcCoreMill extends ArcCoreMillTOP {

  protected static ArcCoreMill millArcCoreSymbolTableCompleter ;

  protected static ArcCoreMill millArcCoreSymbolTableCompleterDelegator;

  protected static ArcCoreMill millFullPrettyPrinter;
  
  public static ArcCoreSymbolTableCompleter symbolTableCompleter ()  {
    if (millArcCoreSymbolTableCompleter == null) {
      millArcCoreSymbolTableCompleter = getMill();
    }
    return millArcCoreSymbolTableCompleter._symbolTableCompleter();
  }

  protected ArcCoreSymbolTableCompleter _symbolTableCompleter() {
    return new ArcCoreSymbolTableCompleter();
  }

  public static ArcCoreSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millArcCoreSymbolTableCompleterDelegator == null) {
      millArcCoreSymbolTableCompleterDelegator = getMill();
    }
    return millArcCoreSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected ArcCoreSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new ArcCoreSymbolTableCompleterDelegator();
  }

  public static IFullPrettyPrinter fullPrettyPrinter() {
    if (millFullPrettyPrinter == null) {
      millFullPrettyPrinter = getMill();
    }
    return millFullPrettyPrinter._fullPrettyPrinter();
  }

  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return new ArcCoreFullPrettyPrinter();
  }

  public static void initMe(ArcCoreMill a)  {
    ArcCoreMillTOP.initMe(a);
    millArcCoreSymbolTableCompleter = a;
    millArcCoreSymbolTableCompleterDelegator = a;
    millFullPrettyPrinter = a;
  }

  public static void reset() {
    ArcCoreMillTOP.reset();
    millArcCoreSymbolTableCompleter = null;
    millArcCoreSymbolTableCompleterDelegator = null;
    millFullPrettyPrinter = null;
  }
}