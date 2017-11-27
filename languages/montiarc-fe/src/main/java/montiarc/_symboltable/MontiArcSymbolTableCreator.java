/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.javadsl._ast.ASTPrimaryExpression;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.types.JTypeSymbolsHelper.JTypeReferenceFactory;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTWildcardType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcConstants;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTComponentHead;
import montiarc._ast.ASTConnector;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTJavaValuation;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcAutoConnect;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTSimpleConnector;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._ast.ASTStereoValue;
import montiarc._ast.ASTSubComponent;
import montiarc._ast.ASTSubComponentInstance;
import montiarc._ast.ASTTransition;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueInitialization;
import montiarc._ast.ASTVariable;
import montiarc._ast.MontiArcPackage;
import montiarc._symboltable.ValueSymbol.Kind;
import montiarc.helper.JavaHelper;
import montiarc.helper.Timing;
import montiarc.trafos.AutoConnection;

/**
 * Visitor that creats the symboltable of a MontiArc AST.
 *
 * @author Robert Heim
 */
public class MontiArcSymbolTableCreator extends MontiArcSymbolTableCreatorTOP {
  
  private String compilationUnitPackage = "";
  
  // extra stack of components that is used to determine which components are
  // inner components.
  private Stack<ComponentSymbol> componentStack = new Stack<>();
  
  private List<ImportStatement> currentImports = new ArrayList<>();
  
  private AutoConnection autoConnectionTrafo = new AutoConnection();
  
  private final static JavaSymbolFactory jSymbolFactory = new JavaSymbolFactory();
  
  private final static JTypeReferenceFactory<JavaTypeSymbolReference> jTypeRefFactory = (name,
      scope, dim) -> new JavaTypeSymbolReference(name, scope, dim);
  
  public MontiArcSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }
  
  public MontiArcSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }
  
  @Override
  public void visit(ASTMACompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for Component: " + compilationUnit.getComponent().getName(),
        MontiArcSymbolTableCreator.class.getSimpleName());
    compilationUnitPackage = Names.getQualifiedName(compilationUnit.getPackage());
    
    // imports
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTImportStatement astImportStatement : compilationUnit.getImportStatements()) {
      String qualifiedImport = Names.getQualifiedName(astImportStatement.getImportList());
      ImportStatement importStatement = new ImportStatement(qualifiedImport,
          astImportStatement.isStar());
      imports.add(importStatement);
    }
    JavaHelper.addJavaDefaultImports(imports);
    
    ArtifactScope artifactScope = new MontiArcArtifactScope(
        Optional.empty(),
        compilationUnitPackage,
        imports);
    this.currentImports = imports;
    putOnStack(artifactScope);
  }
  
  @Override
  public void endVisit(ASTMACompilationUnit node) {
    // artifact scope
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTPort node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    
    String name = node.getName().orElse(StringTransformations.uncapitalize(typeName));
    PortSymbol sym = new PortSymbol(name);
    
    JTypeReference<JTypeSymbol> typeRef = new MAJTypeReference(typeName, JTypeSymbol.KIND,
        currentScope().get());
    
    typeRef.setDimension(TypesHelper.getArrayDimensionIfArrayOrZero(astType));
    
    addTypeArgumentsToTypeSymbol(typeRef, astType);
    
    sym.setTypeReference(typeRef);
    sym.setDirection(node.isIncoming());
    
    // stereotype
    if (node.getStereotype().isPresent()) {
      for (ASTStereoValue st : node.getStereotype().get().getValues()) {
        sym.addStereotype(st.getName(), st.getValue());
      }
    }
    
    addToScopeAndLinkWithNode(sym, node);
  }
  
  @Override
  public void visit(ASTVariable node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    
    String name = node.getName();
    VariableSymbol sym = new VariableSymbol(name);
    
    JTypeReference<JTypeSymbol> typeRef = new MAJTypeReference(typeName, JTypeSymbol.KIND,
        currentScope().get());
    addTypeArgumentsToTypeSymbol(typeRef, astType);

    
    typeRef.setDimension(TypesHelper.getArrayDimensionIfArrayOrZero(astType));
    
    sym.setTypeReference(typeRef);
    
    addToScopeAndLinkWithNode(sym, node);
  }
  
  private void addTypeArgumentsToTypeSymbol(JTypeReference<? extends JTypeSymbol> typeRef,
      ASTType astType) {
    JTypeSymbolsHelper.addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get(),
        new JTypeSymbolsHelper.CommonJTypeReferenceFactory());
  }
  
  @Override
  public void visit(ASTConnector node) {
    String sourceName = node.getSource().toString();
    
    for (ASTQualifiedName target : node.getTargets()) {
      String targetName = target.toString();
      
      ConnectorSymbol sym = new ConnectorSymbol(sourceName, targetName);
      
      // stereotype
      if (node.getStereotype().isPresent()) {
        for (ASTStereoValue st : node.getStereotype().get().getValues()) {
          sym.addStereotype(st.getName(), st.getValue());
        }
      }
      
      addToScopeAndLinkWithNode(sym, node);
    }
  }
  
  @Override
  public void visit(ASTSubComponent node) {
    String referencedCompName = TypesPrinter
        .printTypeWithoutTypeArgumentsAndDimension(node.getType());
    
    // String refCompPackage = Names.getQualifier(referencedCompName);
    String simpleCompName = Names.getSimpleName(referencedCompName);
    
    ComponentSymbolReference componentTypeReference = new ComponentSymbolReference(
        referencedCompName,
        currentScope().get());
    // actual type arguments
    addTypeArgumentsToComponent(componentTypeReference, node.getType());
    
    // ref.setPackageName(refCompPackage);
    
    List<ValueSymbol<TypeReference<TypeSymbol>>> configArgs = new ArrayList<>();
    for (ASTExpression arg : node.getArguments()) {
      arg.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(arg);
      configArgs.add(new ValueSymbol<>(arg, Kind.Expression));
    }
    
    // instances
    
    if (!node.getInstances().isEmpty()) {
      // create instances of the referenced components.
      for (ASTSubComponentInstance i : node.getInstances()) {
        createInstance(i.getName(), i, componentTypeReference, configArgs, i.getConnectors());
      }
    }
    else {
      // auto instance because instance name is missing
      createInstance(StringTransformations.uncapitalize(simpleCompName), node,
          componentTypeReference, configArgs, new ArrayList<>());
    }
    
    node.setEnclosingScope(currentScope().get());
  }
  
  /**
   * Creates the instance and adds it to the symTab.
   */
  private void createInstance(String name, ASTNode node,
      ComponentSymbolReference componentTypeReference,
      List<ValueSymbol<TypeReference<TypeSymbol>>> configArguments,
      List<ASTSimpleConnector> connectors) {
    ComponentInstanceSymbol instance = new ComponentInstanceSymbol(name,
        componentTypeReference);
    configArguments.forEach(v -> instance.addConfigArgument(v));
    // create a subscope for the instance
    addToScopeAndLinkWithNode(instance, node);
    for (ASTSimpleConnector c : connectors) {
      String sourceName = c.getSource().toString();
      for (ASTQualifiedName target : c.getTargets()) {
        String targetName = target.toString();
        ConnectorSymbol sym = new ConnectorSymbol(sourceName, targetName);
        addToScope(sym);
      }
    }
    // remove the created instance's scope
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTComponent node) {
    String componentName = node.getName();
    
    String componentPackageName = "";
    if (componentStack.isEmpty()) {
      // root component (most outer component of the diagram)
      componentPackageName = compilationUnitPackage;
    }
    else {
      // inner component uses its parents component full name as package
      componentPackageName = componentStack.peek().getFullName();
    }
    ComponentSymbol component = new ComponentSymbol(componentName);
    component.setImports(currentImports);
    component.setPackageName(componentPackageName);
    
    // generic type parameters
    addTypeParametersToComponent(component, node.getHead().getGenericTypeParameters(),
        currentScope().get());
    
    // parameters
    setParametersOfComponent(component, node.getHead());
    
    // super component
    if (node.getHead().getSuperComponent().isPresent()) {
      ASTReferenceType superCompRef = node.getHead().getSuperComponent().get();
      String superCompName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(superCompRef);
      
      ComponentSymbolReference ref = new ComponentSymbolReference(superCompName,
          currentScope().get());
      ref.setAccessModifier(BasicAccessModifier.PUBLIC);
      // actual type arguments
      addTypeArgumentsToComponent(ref, superCompRef);
      
      component.setSuperComponent(Optional.of(ref));
    }
    
    // stereotype
    if (node.getStereotype().isPresent()) {
      for (ASTStereoValue st : node.getStereotype().get().getValues()) {
        component.addStereotype(st.getName(), Optional.of(st.getValue()));
      }
    }
    
    // check if this component is an inner component
    if (!componentStack.isEmpty()) {
      component.setIsInnerComponent(true);
    }
    
    // timing
    component.setBehaviorKind(Timing.getBehaviorKind(node));
    
    componentStack.push(component);
    addToScopeAndLinkWithNode(component, node);
    autoConnectionTrafo.transformAtStart(node, component);
  }
  
  @Override
  public void visit(ASTMontiArcAutoConnect node) {
    autoConnectionTrafo.transform(node, componentStack.peek());
  }
  
  private void setParametersOfComponent(final ComponentSymbol componentSymbol,
      final ASTComponentHead astMethod) {
    for (ASTParameter astParameter : astMethod.getParameters()) {
      final String paramName = astParameter.getName();
      astParameter.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(astParameter);
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astParameter.getType());
      JTypeReference<? extends JTypeSymbol> paramTypeSymbol = new JavaTypeSymbolReference(
          TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astParameter
              .getType()),
          currentScope().get(), dimension);
      
      addTypeArgumentsToTypeSymbol(paramTypeSymbol, astParameter.getType());
      final JFieldSymbol parameterSymbol = jSymbolFactory.createFormalParameterSymbol(paramName,
          (JavaTypeSymbolReference) paramTypeSymbol);
      componentSymbol.addConfigParameter(parameterSymbol);
    }
  }
  
  private boolean needsInstanceCreation(ASTComponent node, ComponentSymbol symbol) {
    boolean instanceNameGiven = node.getInstanceName().isPresent();
    boolean autoCreationPossible = symbol.getFormalTypeParameters().size() == 0;
    
    return instanceNameGiven || autoCreationPossible;
  }
  
  @Override
  public void endVisit(ASTComponent node) {
    ComponentSymbol component = componentStack.pop();
    autoConnectionTrafo.transformAtEnd(node, component);
    
    removeCurrentScope();
    
    // for inner components the symbol must be fully created to reference it.
    // Hence, in endVisit we
    // can reference it and put the instance of the inner component into its
    // parent scope.
    
    if (component.isInnerComponent()) {
      String referencedComponentTypeName = component.getFullName();
      ComponentSymbolReference refEntry = new ComponentSymbolReference(
          referencedComponentTypeName, component.getSpannedScope());
      refEntry.setReferencedComponent(Optional.of(component));
      
      if (needsInstanceCreation(node, component)) {
        // create instance
        String instanceName = node.getInstanceName()
            .orElse(StringTransformations.uncapitalize(component.getName()));
        
        if (node.getActualTypeArgument().isPresent()) {
          setActualTypeArgumentsOfCompRef(refEntry,
              node.getActualTypeArgument().get().getTypeArguments());
        }
        
        ComponentInstanceSymbol instanceSymbol = new ComponentInstanceSymbol(instanceName,
            refEntry);
        Log.debug("Created component instance " + instanceSymbol.getName()
            + " referencing component type " + referencedComponentTypeName,
            MontiArcSymbolTableCreator.class.getSimpleName());
        
        addToScope(instanceSymbol);
      }
    }
  }
  
  private void setActualTypeArgumentsOfCompRef(ComponentSymbolReference typeReference,
      List<ASTTypeArgument> astTypeArguments) {
    List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
    for (ASTTypeArgument astTypeArgument : astTypeArguments) {
      if (astTypeArgument instanceof ASTWildcardType) {
        ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;
        
        // Three cases can occur here: lower bound, upper bound, no bound
        if (astWildcardType.lowerBoundIsPresent() || astWildcardType.upperBoundIsPresent()) {
          // We have a bound.
          // Examples: Set<? extends Number>, Set<? super Integer>
          
          // new bound
          boolean lowerBound = astWildcardType.lowerBoundIsPresent();
          ASTType typeBound = lowerBound
              ? astWildcardType.getLowerBound().get()
              : astWildcardType.getUpperBound().get();
          int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
          JTypeReference<? extends JTypeSymbol> typeBoundSymbolReference = new JavaTypeSymbolReference(
              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
              currentScope().get(), dimension);
          ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
              typeBoundSymbolReference);
          
          // init bound
          addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound);
          
          actualTypeArguments.add(actualTypeArgument);
        }
        else {
          // No bound. Example: Set<?>
          actualTypeArguments.add(new ActualTypeArgument(false, false, null));
        }
      }
      else if (astTypeArgument instanceof ASTType) {
        // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
        ASTType astTypeNoBound = (ASTType) astTypeArgument;
        int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound);
        JTypeReference<? extends JTypeSymbol> typeArgumentSymbolReference = new JavaTypeSymbolReference(
            TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
            currentScope().get(), dimension);
        
        addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound);
        
        actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
      }
      else {
        Log.error("0xMA073 Unknown type argument " + astTypeArgument + " of type "
            + typeReference);
      }
    }
    typeReference.setActualTypeArguments(actualTypeArguments);
  }
  
  protected JTypeReferenceFactory<JavaTypeSymbolReference> typeRefFactory = (name, scope,
      dim) -> new JavaTypeSymbolReference(name, scope, dim);
  
  private void addTypeArgumentsToComponent(ComponentSymbolReference typeReference,
      ASTType astType) {
    if (astType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
      if (!astSimpleReferenceType.getTypeArguments().isPresent()) {
        return;
      }
      setActualTypeArgumentsOfCompRef(typeReference,
          astSimpleReferenceType.getTypeArguments().get().getTypeArguments());
    }
    else if (astType instanceof ASTComplexReferenceType) {
      ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
      for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
          .getSimpleReferenceTypes()) {
        /* ASTComplexReferenceType represents types like class or interface
         * types which always have ASTSimpleReferenceType as qualification. For
         * example: a.b.c<Arg>.d.e<Arg> */
        setActualTypeArgumentsOfCompRef(typeReference,
            astSimpleReferenceType.getTypeArguments().get().getTypeArguments());
      }
    }
    
  }
  
  /**
   * Adds the TypeParameters to the ComponentSymbol if it declares
   * TypeVariables. Since the restrictions on TypeParameters may base on the
   * JavaDSL its the actual recursive definition of bounds is respected and its
   * implementation within the JavaDSL is reused. Example:
   * <p>
   * component Bla<T, S extends SomeClass<T> & SomeInterface>
   * </p>
   * T and S are added to Bla.
   *
   * @param componentSymbol
   * @param optionalTypeParameters
   * @return currentScope
   * @see JTypeSymbolsHelper
   */
  protected static List<JTypeSymbol> addTypeParametersToComponent(
      ComponentSymbol componentSymbol, Optional<ASTTypeParameters> optionalTypeParameters,
      Scope currentScope) {
    if (optionalTypeParameters.isPresent()) {
      // component has type parameters -> translate AST to Java Symbols and add
      // these to the
      // componentSymbol.
      ASTTypeParameters astTypeParameters = optionalTypeParameters.get();
      for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
          .getTypeVariableDeclarations()) {
        // TypeParameters/TypeVariables are seen as type declarations.
        JavaTypeSymbol javaTypeVariableSymbol = jSymbolFactory
            .createTypeVariable(astTypeParameter.getName());
        
        List<ASTType> types = new ArrayList<ASTType>(astTypeParameter.getUpperBounds());
        // reuse JavaDSL
        JTypeSymbolsHelper.addInterfacesToType(javaTypeVariableSymbol, types, currentScope,
            jTypeRefFactory);
        
        componentSymbol.addFormalTypeParameter(javaTypeVariableSymbol);
      }
    }
    return componentSymbol.getFormalTypeParameters();
  }
  
  /***************************************
   * Java/P integration
   ***************************************/
  
  @Override
  public void visit(ASTJavaPBehavior node) {
    String name = node.getName().orElse("JavaP");
    JavaBehaviorSymbol ajavaDef = new JavaBehaviorSymbol(name);
    node.setEnclosingScope(currentScope().get());
    addToScopeAndLinkWithNode(ajavaDef, node);
  }
  
  @Override
  public void endVisit(ASTJavaPBehavior node) {
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  public void visit(ASTValueInitialization init) {
    Scope enclosingScope = currentScope().get();
    String qualifiedName = init.getQualifiedName().toString();
    Optional<VariableSymbol> var = enclosingScope
        .<VariableSymbol> resolve(qualifiedName, VariableSymbol.KIND);
    if (var.isPresent()) {
      var.get().setValuation(Optional.of(init.getValuation()));
    }
    
  }
  
  /**
   * Create reference to existing symbols.
   * 
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTPrimaryExpression)
   */
  @Override
  public void visit(ASTPrimaryExpression node) {
    super.visit(node);
    if (node.getName().isPresent() &&
        Character.isLowerCase(node.getName().get().charAt(0))) {
      String name = node.getName().get();
      Scope enclosingScope = currentScope().get().getEnclosingScope().get();
      Optional<PortSymbol> port = enclosingScope.resolve(name, PortSymbol.KIND);
      Optional<PortSymbol> port1 = enclosingScope.resolveDown(name, PortSymbol.KIND);
      Optional<PortSymbol> port2 = enclosingScope.resolveLocally(name, PortSymbol.KIND);
      Optional<VariableSymbol> var = enclosingScope.resolve(name, VariableSymbol.KIND);
      Collection<JFieldSymbol> field = enclosingScope.resolveMany(name, JFieldSymbol.KIND);
      
      if (port.isPresent()) {
        PortSymbolReference portRef = new PortSymbolReference(name, currentScope().get());
        addToScopeAndLinkWithNode(portRef, node);
      }
      else if (var.isPresent()) {
        VariableSymbolReference varReference = new VariableSymbolReference(node.getName().get(),
            currentScope().get());
        varReference.setTypeReference(var.get().getTypeReference());
        addToScopeAndLinkWithNode(varReference, node);
      }
      else if (!field.isEmpty()) {
        addToScopeAndLinkWithNode(field.stream().findFirst().get(), node);
      }
      else {
        Log.error("0xMA030 Used variable " + name
            + " in ajava definition is not a port, component variable or locally defined variable.",
            node.get_SourcePositionStart());
        
      }
    }
  }
  
  /***************************************
   * I/O Automaton integration
   ***************************************/
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTAutomatonBehavior)
   */
  @Override
  public void visit(ASTAutomatonBehavior node) {
    String name = node.getName().orElse("Automaton");
    AutomatonSymbol aut = new AutomatonSymbol(name);
    addToScopeAndLinkWithNode(aut, node);
    
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#endVisit(de.monticore.lang.montiarc.montiarc._ast.ASTAutomatonBehavior)
   */
  @Override
  public void endVisit(ASTAutomatonBehavior node) {
    removeCurrentScope();
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTAutomaton)
   */
  @Override
  public void visit(ASTAutomaton node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void endVisit(ASTAutomaton node) {
    setEnclosingScopeOfNodes(node);
    // automaton core loaded & all enclosing scopes set, so we can reconstruct
    // the missing assignment names
    node.accept(new AssignmentNameCompleter(currentScope().get()));
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTState)
   */
  @Override
  public void visit(ASTState node) {
    StateSymbol state = new StateSymbol(node.getName());
    if (node.getStereotype().isPresent()) {
      for (ASTStereoValue value : node.getStereotype().get().getValues()) {
        state.addStereoValue(value.getName());
      }
    }
    addToScopeAndLinkWithNode(state, node);
  }
  
  /**
   * @see montiarc._visitor.MontiArcVisitor#endVisit(montiarc._ast.ASTState)
   */
  @Override
  public void endVisit(ASTState node) {
    super.endVisit(node);
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._visitor.MontiArcVisitor#visit(de.monticore.lang.montiarc.montiarc._ast.ASTInitialStateDeclaration)
   */
  @Override
  public void visit(ASTInitialStateDeclaration node) {
    MutableScope scope = currentScope().get();
    for (String name : node.getNames()) {
      scope.<StateSymbol> resolveMany(name, StateSymbol.KIND).forEach(c -> {
        c.setInitial(true);
        c.setInitialReactionAST(node.getBlock());
        if (node.getBlock().isPresent()) {
          for (ASTIOAssignment assign : node.getBlock().get().getIOAssignments()) {
            if (assign.getOperator() == MontiArcPackage.ASTIOAssignment_Operator)
              if (assign.getName().isPresent()) {
                Optional<VariableSymbol> var = currentScope().get()
                    .<VariableSymbol> resolve(assign.getName().get(), VariableSymbol.KIND);
                if (var.isPresent()) {
                  if (assign.getValueList().isPresent()
                      && !assign.getValueList().get().getAllValuations().isEmpty()) {
                    //This only covers the case "var i = somevalue"
                    ASTValuation v = assign.getValueList().get().getAllValuations().get(0);
                    var.get().setValuation(Optional.of(v));
                  }
                }
              }
          }
        }
      });
    }
  }
  
  @Override
  public void visit(ASTTransition node) {
    // get target name, if there is no get source name (loop to itself)
    // TODO what about same transitions with other stimulus? -> name clash
    String targetName = node.getTarget().orElse(node.getSource());
    
    StateSymbolReference source = new StateSymbolReference(node.getSource(), currentScope().get());
    StateSymbolReference target = new StateSymbolReference(targetName, currentScope().get());
    
    TransitionSymbol transition = new TransitionSymbol(node.getSource() + " -> " + targetName);
    transition.setSource(source);
    transition.setTarget(target);
    
    transition.setGuardAST(node.getGuard());
    transition.setReactionAST(node.getReaction());
    transition.setStimulusAST(node.getStimulus());
    
    addToScopeAndLinkWithNode(transition, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTTransition node) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTIOAssignment node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  
  /**
   * @see montiarc._symboltable.MontiArcSymbolTableCreatorTOP#visit(montiarc._ast.ASTStateDeclaration)
   */
  @Override
  public void visit(ASTStateDeclaration ast) {
    // to prevent creation of unnecessary scope we override with nothing
  }
  
  /**
   * @see montiarc._symboltable.MontiArcSymbolTableCreatorTOP#endVisit(montiarc._ast.ASTStateDeclaration)
   */
  @Override
  public void endVisit(ASTStateDeclaration ast) {
    // to prevent deletion of not existing scope we override with nothing
  }
  
}
