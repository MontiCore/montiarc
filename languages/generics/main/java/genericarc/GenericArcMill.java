/* (c) https://github.com/MontiCore/monticore */
package genericarc;

import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import genericarc._symboltable.GenericArcScopesGenitorP2;
import genericarc._symboltable.GenericArcScopesGenitorP2Delegator;
import genericarc.check.GenericArcCompTypeExprDeSer;

public class GenericArcMill extends GenericArcMillTOP {

  protected static GenericArcMill millGenericArcScopesGenitorP2;

  protected static GenericArcMill millGenericArcScopesGenitorP2Delegator;

  protected static GenericArcMill millFullPrettyPrinter;

  protected static GenericArcMill millCompTypeExprDeSer;

  public static GenericArcScopesGenitorP2 scopesGenitorP2() {
    if (millGenericArcScopesGenitorP2 == null) {
      millGenericArcScopesGenitorP2 = GenericArcMillTOP.getMill();
    }
    return millGenericArcScopesGenitorP2._scopesGenitorP2();
  }

  protected GenericArcScopesGenitorP2 _scopesGenitorP2() {
    return new GenericArcScopesGenitorP2();
  }

  public static GenericArcScopesGenitorP2Delegator scopesGenitorP2Delegator() {
    if (millGenericArcScopesGenitorP2Delegator == null) {
      millGenericArcScopesGenitorP2Delegator = GenericArcMillTOP.getMill();
    }
    return millGenericArcScopesGenitorP2Delegator._scopesGenitorP2Delegator();
  }

  protected GenericArcScopesGenitorP2Delegator _scopesGenitorP2Delegator() {
    return new GenericArcScopesGenitorP2Delegator();
  }

  public static ComposedCompTypeExprDeSer compTypeExprDeSer() {
    if (millCompTypeExprDeSer == null) {
      millCompTypeExprDeSer = getMill();
    }
    return millCompTypeExprDeSer._compTypeExprDeSer();
  }

  protected ComposedCompTypeExprDeSer _compTypeExprDeSer() {
    return new GenericArcCompTypeExprDeSer();
  }

  public static void initMe(GenericArcMill a) {
    GenericArcMillTOP.initMe(a);
    millGenericArcScopesGenitorP2 = a;
    millGenericArcScopesGenitorP2Delegator = a;
    millFullPrettyPrinter = a;
    millCompTypeExprDeSer = a;
  }

  public static void reset() {
    GenericArcMillTOP.reset();
    millGenericArcScopesGenitorP2 = null;
    millGenericArcScopesGenitorP2Delegator = null;
    millFullPrettyPrinter = null;
    millCompTypeExprDeSer = null;
  }
}