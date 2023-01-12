/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis._visitor.IFullPrettyPrinter;
import com.microsoft.z3.Context;
import montiarc._symboltable.MontiArcSymbolTableCompleter;
import montiarc._symboltable.MontiArcSymbolTableCompleterDelegator;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import montiarc.evaluation.MontiArcDeriveSMTExpr;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

public class MontiArcMill extends MontiArcMillTOP {

  protected static MontiArcMill millMontiArcSymbolTableCompleter ;

  protected static MontiArcMill millMontiArcSymbolTableCompleterDelegator;

  protected static MontiArcMill millMontiArcFullPrettyPrinter;

  protected static MontiArcMill millMontiArcFullConverter;

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

  public static IFullPrettyPrinter fullPrettyPrinter() {
    if (millMontiArcFullPrettyPrinter == null) {
      millMontiArcFullPrettyPrinter = getMill();
    }
    return millMontiArcFullPrettyPrinter._fullPrettyPrinter();
  }

  protected IFullPrettyPrinter _fullPrettyPrinter() {
    return new MontiArcFullPrettyPrinter();
  }

  public static IDeriveSMTExpr fullConverter(Context context) {
    if (millMontiArcFullPrettyPrinter == null) {
      millMontiArcFullPrettyPrinter = getMill();
    }
    return millMontiArcFullPrettyPrinter._fullConverter(context);
  }

  protected IDeriveSMTExpr _fullConverter(Context context) {
    return new MontiArcDeriveSMTExpr(context);
  }

  public static void initMe(MontiArcMill a)  {
    MontiArcMillTOP.initMe(a);
    millMontiArcSymbolTableCompleter = a;
    millMontiArcSymbolTableCompleterDelegator = a;
    millMontiArcFullPrettyPrinter = a;
    millMontiArcFullConverter = a;
  }

  public static void reset() {
    MontiArcMillTOP.reset();
    millMontiArcSymbolTableCompleter = null;
    millMontiArcSymbolTableCompleterDelegator = null;
    millMontiArcFullPrettyPrinter = null;
    millMontiArcFullConverter = null;
  }
}