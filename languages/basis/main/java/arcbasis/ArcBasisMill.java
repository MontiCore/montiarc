/* (c) https://github.com/MontiCore/monticore */
package arcbasis;

import arcbasis._symboltable.ArcBasisSymbolTableCompleter;
import arcbasis._symboltable.ArcBasisSymbolTableCompleterDelegator;
import arcbasis._prettyprint.ArcBasisFullPrettyPrinter;
import arcbasis._visitor.IFullPrettyPrinter;
import arcbasis.check.deser.ArcBasisCompTypeExprDeSer;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;

public class ArcBasisMill extends ArcBasisMillTOP {

  protected static ArcBasisMill millArcBasisSymbolTableCompleter;

  protected static ArcBasisMill millArcBasisSymbolTableCompleterDelegator;

  protected static ArcBasisMill millFullPrettyPrinter;

  protected static ArcBasisMill millCompTypeExprDeSer;

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

  public static ComposedCompTypeExprDeSer compTypeExprDeSer() {
    if (millCompTypeExprDeSer == null) {
      millCompTypeExprDeSer = getMill();
    }
    return millCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new ArcBasisCompTypeExprDeSer();
  }

  public static void initMe(ArcBasisMill a)  {
    ArcBasisMillTOP.initMe(a);
    millArcBasisSymbolTableCompleter = a;
    millArcBasisSymbolTableCompleterDelegator = a;
    millFullPrettyPrinter = a;
    millCompTypeExprDeSer = a;
  }

  public static void reset() {
    ArcBasisMillTOP.reset();
    millArcBasisSymbolTableCompleter = null;
    millArcBasisSymbolTableCompleterDelegator = null;
    millFullPrettyPrinter = null;
    millCompTypeExprDeSer = null;
  }
}