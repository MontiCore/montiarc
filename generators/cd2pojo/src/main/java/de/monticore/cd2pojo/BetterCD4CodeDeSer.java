/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4code._symboltable.CD4CodeDeSer;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._symboltable.CDPackageSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BetterCD4CodeDeSer extends CD4CodeDeSer {
  
  @Override
  public String serialize(ICD4CodeArtifactScope toSerialize) {
    List<Remove<?>> symbols = createRemovers(toSerialize);
    symbols.forEach(Remove::perform);
    String freshlySerializedString = super.serialize(toSerialize);
    symbols.forEach(Remove::undo);
    return freshlySerializedString;
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
    TypeVarSymbol.class,
    CDPackageSymbol.class
  };
  
  /**
   * provides objects that can remove duplicates from the given scope.
   * that obviously has side effects that make the scope unsuitable for further conventional use.
   * So the removed symbols can also be re-added later on
   * @param scope object to modify
   */
  private List<Remove<?>> createRemovers(ICD4CodeScope scope) {
    // note that all these remove- and add-lambdas are actually different methods
    List<Remove<?>> returnee = new ArrayList<>(Collections.singletonList(
      new Remove<CDPackageSymbol>(scope::getCDPackageSymbols, scope::remove, scope::add)
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