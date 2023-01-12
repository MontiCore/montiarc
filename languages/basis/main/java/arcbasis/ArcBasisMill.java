/* (c) https://github.com/MontiCore/monticore */
package arcbasis;

import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import arcbasis._symboltable.ArcBasisSymbolTableCompleterDelegator;
import arcbasis._prettyprint.ArcBasisFullPrettyPrinter;
import arcbasis._visitor.IFullPrettyPrinter;

public class ArcBasisMill extends ArcBasisMillTOP {

  protected static ArcBasisMill millArcBasisSymbolTableCompleter ;

  protected static ArcBasisMill millArcBasisSymbolTableCompleterDelegator;

  protected static ArcBasisMill millFullPrettyPrinter;

  public static ArcBasisSymbolTableCompleter symbolTableCompleter ()  {
    if (millArcBasisSymbolTableCompleter == null) {
      millArcBasisSymbolTableCompleter = getMill();
    }
    return millArcBasisSymbolTableCompleter._symbolTableCompleter();
  }

  protected ArcBasisSymbolTableCompleter _symbolTableCompleter() {
    return new ArcBasisSymbolTableCompleter();
  }

  public static ArcBasisSymbolTableCompleterDelegator symbolTableCompleterDelegator ()  {
    if (millArcBasisSymbolTableCompleterDelegator == null) {
      millArcBasisSymbolTableCompleterDelegator = getMill();
    }
    return millArcBasisSymbolTableCompleterDelegator._symbolTableCompleterDelegator();
  }

  protected ArcBasisSymbolTableCompleterDelegator _symbolTableCompleterDelegator() {
    return new ArcBasisSymbolTableCompleterDelegator();
  }

  public static IFullPrettyPrinter fullPrettyPrinter() {
    if (millFullPrettyPrinter == null) {
      millFullPrettyPrinter = getMill();
    }
    return millFullPrettyPrinter._fullPrettyPrinter();
  }

  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return new ArcBasisFullPrettyPrinter();
  }

  public static void initMe(ArcBasisMill a)  {
    ArcBasisMillTOP.initMe(a);
    millArcBasisSymbolTableCompleter = a;
    millArcBasisSymbolTableCompleterDelegator = a;
    millFullPrettyPrinter = a;
  }

  public static void reset() {
    ArcBasisMillTOP.reset();
    millArcBasisSymbolTableCompleter = null;
    millArcBasisSymbolTableCompleterDelegator = null;
    millFullPrettyPrinter = null;
  }
}