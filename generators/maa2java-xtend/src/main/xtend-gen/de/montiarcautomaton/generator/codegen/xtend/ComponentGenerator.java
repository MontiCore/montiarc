/**
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.montiarcautomaton.generator.codegen.xtend;

import de.montiarcautomaton.generator.codegen.xtend.Deploy;
import de.montiarcautomaton.generator.codegen.xtend.Input;
import de.montiarcautomaton.generator.codegen.xtend.Result;
import de.montiarcautomaton.generator.codegen.xtend.atomic.AtomicComponent;
import de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.AbstractAtomicImplementation;
import de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.automaton.AutomatonGenerator;
import de.montiarcautomaton.generator.codegen.xtend.atomic.behavior.javap.JavaPGenerator;
import de.montiarcautomaton.generator.codegen.xtend.composed.ComposedComponent;
import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.IterablePath;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._symboltable.ComponentSymbol;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class ComponentGenerator {
  public void generateAll(final File targetPath, final File hwc, final ComponentSymbol comp) {
    IterablePath _from = IterablePath.from(hwc, ".java");
    String _packageName = comp.getPackageName();
    String _plus = (_packageName + ".");
    String _name = comp.getName();
    String _plus_1 = (_plus + _name);
    boolean existsHWCClass = TransformationHelper.existsHandwrittenClass(_from, _plus_1);
    String _name_1 = comp.getName();
    String _plus_2 = (_name_1 + "Input");
    this.toFile(targetPath, _plus_2, Input.generateInput(comp));
    String _name_2 = comp.getName();
    String _plus_3 = (_name_2 + "Result");
    this.toFile(targetPath, _plus_3, Result.generateResult(comp));
    boolean _isAtomic = comp.isAtomic();
    if (_isAtomic) {
      this.toFile(targetPath, comp.getName(), AtomicComponent.generateAtomicComponent(comp));
      if ((!existsHWCClass)) {
        String _name_3 = comp.getName();
        String _plus_4 = (_name_3 + "Impl");
        this.toFile(targetPath, _plus_4, this.generateBehaviorImplementation(comp));
      }
    } else {
      this.toFile(targetPath, comp.getName(), ComposedComponent.generateComposedComponent(comp));
    }
    boolean _containsKey = comp.getStereotype().containsKey("deploy");
    if (_containsKey) {
      String _name_4 = comp.getName();
      String _plus_5 = ("Deploy" + _name_4);
      this.toFile(targetPath, _plus_5, Deploy.generateDeploy(comp));
    }
  }
  
  public void toFile(final File targetPath, final String name, final String content) {
    String _absolutePath = targetPath.getAbsolutePath();
    String _plus = (_absolutePath + "\\");
    String _plus_1 = (_plus + name);
    String _plus_2 = (_plus_1 + ".java");
    Path path = Paths.get(_plus_2);
    FileReaderWriter writer = new FileReaderWriter();
    InputOutput.<String>println((("Writing to file " + path) + "."));
    writer.storeInFile(path, content);
  }
  
  protected String _generateBehavior(final ASTJavaPBehavior ajava, final ComponentSymbol comp) {
    try {
      return JavaPGenerator.class.newInstance().generate(comp);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected String _generateBehavior(final ASTAutomatonBehavior automaton, final ComponentSymbol comp) {
    return new AutomatonGenerator(comp).generate(comp);
  }
  
  public String generateBehaviorImplementation(final ComponentSymbol comp) {
    ASTNode _get = comp.getAstNode().get();
    ASTComponent compAST = ((ASTComponent) _get);
    boolean hasBehavior = false;
    List<ASTElement> _elementList = compAST.getBody().getElementList();
    for (final ASTElement element : _elementList) {
      if ((element instanceof ASTBehaviorElement)) {
        hasBehavior = true;
        return this.generateBehavior(((ASTCNode) element), comp);
      }
    }
    if ((!hasBehavior)) {
      return AbstractAtomicImplementation.generateAbstractAtomicImplementation(comp);
    }
    return null;
  }
  
  public String generateBehavior(final ASTCNode automaton, final ComponentSymbol comp) {
    if (automaton instanceof ASTAutomatonBehavior) {
      return _generateBehavior((ASTAutomatonBehavior)automaton, comp);
    } else if (automaton instanceof ASTJavaPBehavior) {
      return _generateBehavior((ASTJavaPBehavior)automaton, comp);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(automaton, comp).toString());
    }
  }
}
