/* (c) https://github.com/MontiCore/monticore */
package montiarc_with_cd._lsp.language_aggregation.language_access;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.SourcePosition;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adapts a CDTypeSymbol as a TypeSymbol.
 * This is needed since {@link CDTypeSymbol#accept} only works with Traversers of type CDBasisTraverser,
 * which the MontiArc does not provide.
 * {@link TypeSymbol#accept} also works with a BasicSymbolsTraverse, which MontiArc provides.
 */
public class CDTypeAsTypeAdapter extends TypeSymbol {

  protected CDTypeSymbol delegate;

  public static List<TypeSymbol> from(List<CDTypeSymbol> types) {
    return types.stream().map(CDTypeAsTypeAdapter::new).collect(Collectors.toList());
  }

  public CDTypeAsTypeAdapter(CDTypeSymbol delegate) {
    super(delegate.getName());
    this.delegate = delegate;
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return delegate.getEnclosingScope();
  }

  @Override
  public ASTType getAstNode() {
    return delegate.getAstNode();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public IBasicSymbolsScope getSpannedScope() {
    return delegate.getSpannedScope();
  }

  @Override
  public List<TypeVarSymbol> getTypeParameterList() {
    return delegate.getTypeParameterList();
  }

  @Override
  public void addTypeVarSymbol(TypeVarSymbol t) {
    delegate.addTypeVarSymbol(t);
  }

  @Override
  public boolean isPresentSuperClass() {
    return delegate.isPresentSuperClass();
  }

  @Override
  public SymTypeExpression getSuperClass() {
    return delegate.getSuperClass();
  }

  @Override
  public List<SymTypeExpression> getSuperClassesOnly() {
    return delegate.getSuperClassesOnly();
  }

  @Override
  public List<SymTypeExpression> getInterfaceList() {
    return delegate.getInterfaceList();
  }

  @Override
  public AccessModifier getAccessModifier() {
    return delegate.getAccessModifier();
  }

  @Override
  public void setFunctionList(List<FunctionSymbol> methodList) {
    delegate.setFunctionList(methodList);
  }

  @Override
  public List<FunctionSymbol> getFunctionList() {
    return delegate.getFunctionList();
  }

  @Override
  public List<FunctionSymbol> getFunctionList(String methodname) {
    return delegate.getFunctionList(methodname);
  }

  @Override
  public List<VariableSymbol> getVariableList() {
    return delegate.getVariableList();
  }

  @Override
  public List<VariableSymbol> getVariableList(String fieldname) {
    return delegate.getVariableList(fieldname);
  }

  @Override
  public void addVariableSymbol(VariableSymbol f) {
    delegate.addVariableSymbol(f);
  }

  @Override
  public void addFunctionSymbol(FunctionSymbol m) {
    delegate.addFunctionSymbol(m);
  }

  @Override
  public boolean containsSuperTypes(Object element) {
    return delegate.containsSuperTypes(element);
  }

  @Override
  public boolean containsAllSuperTypes(Collection<?> collection) {
    return delegate.containsAllSuperTypes(collection);
  }

  @Override
  public boolean isEmptySuperTypes() {
    return delegate.isEmptySuperTypes();
  }

  @Override
  public Iterator<SymTypeExpression> iteratorSuperTypes() {
    return delegate.iteratorSuperTypes();
  }

  @Override
  public int sizeSuperTypes() {
    return delegate.sizeSuperTypes();
  }

  @Override
  public SymTypeExpression[] toArraySuperTypes(SymTypeExpression[] array) {
    return delegate.toArraySuperTypes(array);
  }

  @Override
  public Object[] toArraySuperTypes() {
    return delegate.toArraySuperTypes();
  }

  @Override
  public Spliterator<SymTypeExpression> spliteratorSuperTypes() {
    return delegate.spliteratorSuperTypes();
  }

  @Override
  public Stream<SymTypeExpression> streamSuperTypes() {
    return delegate.streamSuperTypes();
  }

  @Override
  public Stream<SymTypeExpression> parallelStreamSuperTypes() {
    return delegate.parallelStreamSuperTypes();
  }

  @Override
  public SymTypeExpression getSuperTypes(int index) {
    return delegate.getSuperTypes(index);
  }

  @Override
  public int indexOfSuperTypes(Object element) {
    return delegate.indexOfSuperTypes(element);
  }

  @Override
  public int lastIndexOfSuperTypes(Object element) {
    return delegate.lastIndexOfSuperTypes(element);
  }

  @Override
  public boolean equalsSuperTypes(Object o) {
    return delegate.equalsSuperTypes(o);
  }

  @Override
  public int hashCodeSuperTypes() {
    return delegate.hashCodeSuperTypes();
  }

  @Override
  public ListIterator<SymTypeExpression> listIteratorSuperTypes() {
    return delegate.listIteratorSuperTypes();
  }

  @Override
  public ListIterator<SymTypeExpression> listIteratorSuperTypes(int index) {
    return delegate.listIteratorSuperTypes(index);
  }

  @Override
  public List<SymTypeExpression> subListSuperTypes(int start, int end) {
    return delegate.subListSuperTypes(start, end);
  }

  @Override
  public List<SymTypeExpression> getSuperTypesList() {
    return delegate.getSuperTypesList();
  }

  @Override
  public void clearSuperTypes() {
    delegate.clearSuperTypes();
  }

  @Override
  public boolean addSuperTypes(SymTypeExpression element) {
    return delegate.addSuperTypes(element);
  }

  @Override
  public boolean addAllSuperTypes(Collection<? extends SymTypeExpression> collection) {
    return delegate.addAllSuperTypes(collection);
  }

  @Override
  public boolean removeSuperTypes(Object element) {
    return delegate.removeSuperTypes(element);
  }

  @Override
  public boolean removeAllSuperTypes(Collection<?> collection) {
    return delegate.removeAllSuperTypes(collection);
  }

  @Override
  public boolean retainAllSuperTypes(Collection<?> collection) {
    return delegate.retainAllSuperTypes(collection);
  }

  @Override
  public boolean removeIfSuperTypes(Predicate<? super SymTypeExpression> filter) {
    return delegate.removeIfSuperTypes(filter);
  }

  @Override
  public void forEachSuperTypes(Consumer<? super SymTypeExpression> action) {
    delegate.forEachSuperTypes(action);
  }

  @Override
  public void addSuperTypes(int index, SymTypeExpression element) {
    delegate.addSuperTypes(index, element);
  }

  @Override
  public boolean addAllSuperTypes(int index, Collection<? extends SymTypeExpression> collection) {
    return delegate.addAllSuperTypes(index, collection);
  }

  @Override
  public SymTypeExpression removeSuperTypes(int index) {
    return delegate.removeSuperTypes(index);
  }

  @Override
  public SymTypeExpression setSuperTypes(int index, SymTypeExpression element) {
    return delegate.setSuperTypes(index, element);
  }

  @Override
  public void replaceAllSuperTypes(UnaryOperator<SymTypeExpression> operator) {
    delegate.replaceAllSuperTypes(operator);
  }

  @Override
  public void sortSuperTypes(Comparator<? super SymTypeExpression> comparator) {
    delegate.sortSuperTypes(comparator);
  }

  @Override
  public void setSuperTypesList(List<SymTypeExpression> superTypes) {
    delegate.setSuperTypesList(superTypes);
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public void setName(String name) {
    delegate.setName(name);
  }

  @Override
  public void setEnclosingScope(IBasicSymbolsScope enclosingScope) {
    delegate.setEnclosingScope(enclosingScope);
  }

  @Override
  public boolean isPresentAstNode() {
    return delegate.isPresentAstNode();
  }

  @Override
  public void setAstNode(ASTType astNode) {
    delegate.setAstNode(astNode);
  }

  @Override
  public void setAstNodeAbsent() {
    delegate.setAstNodeAbsent();
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    delegate.setAccessModifier(accessModifier);
  }

  @Override
  public void setFullName(String fullName) {
    delegate.setFullName(fullName);
  }

  @Override
  public String getFullName() {
    return delegate.getFullName();
  }

  @Override
  public void setPackageName(String packageName) {
    delegate.setPackageName(packageName);
  }

  @Override
  public String getPackageName() {
    return delegate.getPackageName();
  }

  @Override
  public void setSpannedScope(IBasicSymbolsScope scope) {
    delegate.setSpannedScope(scope);
  }

  @Override
  public SourcePosition getSourcePosition() {
    return delegate.getSourcePosition();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }
}
