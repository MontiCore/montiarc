/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

public class MontiArcSymbolTableCreatorDelegatorBuilder extends MontiArcSymbolTableCreatorDelegatorBuilderTOP {

  /**
   * Currently overrides generated build method because that replaces scope
   * stack after initializing the delegator visitor.
   * TODO: Remove after MontiCore fix.
   */
  @Override
  public  MontiArcSymbolTableCreatorDelegator build()  {
    return new MontiArcSymbolTableCreatorDelegator(globalScope);
  }
}