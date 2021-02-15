/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4code._symboltable.CD4CodeScopeDeSer;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2JsonTOP;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BetterCD4CodeDeSer extends CD4CodeScopeDeSer {
  
  @Override
  public String serialize(ICD4CodeScope toSerialize) {
    List<Remove<?>> symbols = createRemovers(toSerialize);
    symbols.forEach(Remove::perform);
    String freshlySerializedString = superSerialize(toSerialize);
    symbols.forEach(Remove::undo);
    return freshlySerializedString;
  }
  
  public String superSerialize(ICD4CodeScope toSerialize) {
    de.monticore.symboltable.serialization.JsonPrinter printer = new de.monticore.symboltable.serialization.JsonPrinter();
    de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor symbolTablePrinter = new de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor();
    symbolTablePrinter.setCD4AnalysisVisitor(new de.monticore.cd4analysis._symboltable.CD4AnalysisSymbols2Json(printer));
    symbolTablePrinter.setBitExpressionsVisitor(new de.monticore.expressions.bitexpressions._symboltable.BitExpressionsSymbols2Json(printer));
    symbolTablePrinter.setCommonExpressionsVisitor(new de.monticore.expressions.commonexpressions._symboltable.CommonExpressionsSymbols2Json(printer));
    symbolTablePrinter.setMCFullGenericTypesVisitor(new de.monticore.types.mcfullgenerictypes._symboltable.MCFullGenericTypesSymbols2Json(printer));
    symbolTablePrinter.setMCLiteralsBasisVisitor(new de.monticore.literals.mcliteralsbasis._symboltable.MCLiteralsBasisSymbols2Json(printer));
    symbolTablePrinter.setMCBasicTypesVisitor(new de.monticore.types.mcbasictypes._symboltable.MCBasicTypesSymbols2Json(printer));
    symbolTablePrinter.setCDInterfaceAndEnumVisitor(new de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbols2Json(printer));
    symbolTablePrinter.setCD4CodeVisitor(new BetterCD4CodeSymbols2Json(printer));
    symbolTablePrinter.setCDBasisVisitor(new de.monticore.cdbasis._symboltable.CDBasisSymbols2Json(printer));
    symbolTablePrinter.setBasicSymbolsVisitor(new de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json(printer));
    symbolTablePrinter.setExpressionsBasisVisitor(new de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymbols2Json(printer));
    symbolTablePrinter.setUMLModifierVisitor(new de.monticore.umlmodifier._symboltable.UMLModifierSymbols2Json(printer));
    symbolTablePrinter.setMCArrayTypesVisitor(new de.monticore.types.mcarraytypes._symboltable.MCArrayTypesSymbols2Json(printer));
    symbolTablePrinter.setMCCommonLiteralsVisitor(new de.monticore.literals.mccommonliterals._symboltable.MCCommonLiteralsSymbols2Json(printer));
    symbolTablePrinter.setCDAssociationVisitor(new de.monticore.cdassociation._symboltable.CDAssociationSymbols2Json(printer));
    symbolTablePrinter.setMCSimpleGenericTypesVisitor(new de.monticore.types.mcsimplegenerictypes._symboltable.MCSimpleGenericTypesSymbols2Json(printer));
    symbolTablePrinter.setMCCollectionTypesVisitor(new de.monticore.types.mccollectiontypes._symboltable.MCCollectionTypesSymbols2Json(printer));
    symbolTablePrinter.setCD4CodeBasisVisitor(new de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbols2Json(printer));
    symbolTablePrinter.setOOSymbolsVisitor(new de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json(printer));
    symbolTablePrinter.setMCBasicsVisitor(new de.monticore.mcbasics._symboltable.MCBasicsSymbols2Json(printer));
    symbolTablePrinter.setUMLStereotypeVisitor(new de.monticore.umlstereotype._symboltable.UMLStereotypeSymbols2Json(printer));
    toSerialize.accept(symbolTablePrinter);
    return printer.getContent();
  }
  
  
  
  /**
   * all types that appear as subclasses in the kind hierarchy,
   * given as classes so that their package is almost granted to be correct this time
   */
  private static final Class<?>[] SYMBOL_KINDS = new Class<?>[]{
    CDRoleSymbol.class,
    CDTypeSymbol.class,
    FieldSymbol.class,
    CDMethodSignatureSymbol.class,
    OOTypeSymbol.class,
    MethodSymbol.class,
    TypeVarSymbol.class
  };
  
  /**
   * provides objects that can remove duplicates from the given scope.
   * that obviously has side effects that make the scope unsuitable for further conventional use.
   * So the removed symbols can also be re-added later on
   * @param scope object to modify
   */
  private List<Remove<?>> createRemovers(ICD4CodeScope scope) {
    Remove<OOTypeSymbol> extraRem = new Remove<>(scope::getCDTypeSymbols, scope::remove, scope::add);
    extraRem.perform();
    // note that all these remove- and add-lambdas are actually different methods
    List<Remove<?>> returnee = new ArrayList<>(Arrays.asList(
      new Remove<VariableSymbol>(scope::getFieldSymbols, scope::remove, scope::add),
      new Remove<FieldSymbol>(scope::getCDRoleSymbols, scope::remove, scope::add),
      new Remove<FunctionSymbol>(scope::getMethodSymbols, scope::remove, scope::add),
      new Remove<MethodSymbol>(scope::getCDMethodSignatureSymbols, scope::remove, scope::add),
      new Remove<TypeSymbol>(scope::getTypeVarSymbols, scope::remove, scope::add),
      new Remove<TypeSymbol>(scope::getOOTypeSymbols, scope::remove, scope::add),
      extraRem
    ));
    scope.getSubScopes().forEach(subScope -> returnee.addAll(createRemovers(subScope)));
    return returnee;
  }
  
  /**
   * this class allows explicitly defining the supertype used in {@link #perform()}
   * @param <Super> type of the collection from which objects should be removed
   */
  private static class Remove<Super>{
    final List<Super> duplicates;
    final Consumer<Super> remove;
    final Consumer<Super> undo;
    /**
     * please create all removers before performing the first removal,
     * because the removing may have effects on the input given on the fist parameter, because some contents of that
     * multimap may then already have been removed
     * <p></p>
     * enables this objects to remove all elements from the supplier with the consumer.
     * @param symbols provides symbols that have to be removed
     * @param remover removes one duplicated symbol
     * @param adder provides a way to re-add the removed symbol to the scope
     * @param <Sub> direct subtype of symbol to remove from the container of the supertype
     */
    private <Sub extends Super> Remove(Supplier<LinkedListMultimap<String, Sub>> symbols,
                                       Consumer<Super> remover, Consumer<Super> adder) {
      duplicates = symbols.get().values().stream().map(x -> (Super) x).collect(Collectors.toList());
      remove = remover;
      undo = adder;
    }
    
    /**
     * removes all duplicates from the scope
     */
    private void perform(){
      duplicates.forEach(remove);
    }
    
    /**
     * re-adds all duplicates to the scope
     */
    private void undo(){
      duplicates.forEach(undo);
    }
    
    @Override
    public String toString() {
      return duplicates.toString();
    }
  }
}