/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4code._symboltable.CD4CodeScopeDeSer;
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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BetterCD4CodeDeSer extends CD4CodeScopeDeSer {

  @Override
  public String serialize(ICD4CodeScope toSerialize) {
    List<Remove<?>> symbols = createRemovers(toSerialize);
    symbols.forEach(Remove::perform);
    String freshlySerializedString = super.serialize(toSerialize);
    symbols.forEach(Remove::undo);
    return fixPackages(freshlySerializedString);
  }

  /**
   * the packages stated in {@link de.monticore.cd4code._symboltable.CD4CodeSymbols2JsonTOP#printKindHierarchy}
   * are partially wrong, so they have to be corrected
   * @param string array (with length 1) whose first value describes the serialized symbol table
   * @return the same string, but with correct imports for the subclasses in the kind hierarchy
   */
  public static String fixPackages(String... string) {
    // the first parameter is an array, so it can be used in lambdas
    Arrays.stream(SYMBOL_KINDS).forEach(symbol -> string[0] = string[0].replaceFirst(
        WRONG_PACKAGE + symbol.getSimpleName(),
        symbol.getCanonicalName()));
    return string[0];
  }

  /**
   * The package wrongly used in {@link de.monticore.cd4code._symboltable.CD4CodeSymbols2JsonTOP#printKindHierarchy}
   * that has to be replaced. The Points are escaped for using it in a regex
   */
  private static final String WRONG_PACKAGE =
      "de\\.monticore\\.cd4code\\._symboltable\\.";

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
    // note that all these remove- and add-lambdas are actually different methods
    return Arrays.asList(
      new Remove<VariableSymbol>(scope::getFieldSymbols, scope::remove, scope::add),
      new Remove<FieldSymbol>(scope::getCDRoleSymbols, scope::remove, scope::add),
      new Remove<FunctionSymbol>(scope::getMethodSymbols, scope::remove, scope::add),
      new Remove<MethodSymbol>(scope::getCDMethodSignatureSymbols, scope::remove, scope::add),
      new Remove<TypeSymbol>(scope::getTypeVarSymbols, scope::remove, scope::add),
      new Remove<TypeSymbol>(scope::getOOTypeSymbols, scope::remove, scope::add),
      new Remove<OOTypeSymbol>(scope::getCDTypeSymbols, scope::remove, scope::add)
    );
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
  }
}