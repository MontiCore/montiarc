///*
// * Copyright (c) 2015 RWTH Aachen. All rights reserved.
// *
// * http://www.se-rwth.de/
// */
//package de.monticore.lang.montiarc;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import com.google.common.collect.Lists;
//import de.monticore.java.javadsl._ast.ASTArrayCreator;
//import de.monticore.java.javadsl._ast.ASTArrayDimensionByInitializer;
//import de.monticore.lang.montiarc.helper.IndentPrinter;
//import de.monticore.lang.montiarc.helper.SymbolPrinter;
//import de.monticore.lang.montiarc.montiarc._ast.ASTConnector;
//import de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit;
//import de.monticore.lang.montiarc.montiarc._ast.ASTPort;
//import de.monticore.lang.montiarc.montiarc._ast.ASTSubComponent;
//import de.monticore.lang.montiarc.montiarc._parser.MontiArcParser;
//import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
//import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
//import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
//import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
//import de.monticore.symboltable.Scope;
//import de.monticore.types.types._ast.ASTSimpleReferenceType;
//import org.junit.Ignore;
//import org.junit.Test;
//
///**
// * Tests for toString methods of MontiArc symbols.
// *
// * @author Michael von Wenckstern
// */
//public class ToStringTest extends AbstractSymtabTest {
//
//  @Ignore
//  @Test
//  public void testPortSymbolNt() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//    PortSymbol port = symTab.<PortSymbol>resolve(
//        "a.TypesTest.nt", PortSymbol.KIND).orElse(null);
//    assertNotNull(port);
//    MontiArcParser parser = new MontiArcParser();
//    ASTPort astport = parser.parseString_Port(port.toString()).get();
//
//    // in NewType<String, Integer> nt
//    assertTrue(astport.isIncoming());
//    assertEquals(((ASTSimpleReferenceType) astport.getType()).getNames().get(0), "NewType");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astport.getType()).getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "String");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astport.getType()).getTypeArguments().get().getTypeArguments().get(1)).getNames().get(0), "Integer");
//    assertEquals(astport.getName().get(), "nt");
//  }
//
//  @Test
//  public void testPortSymbolStrOut() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//    PortSymbol port = symTab.<PortSymbol>resolve(
//        "a.TypesTest.strOut", PortSymbol.KIND).orElse(null);
//    assertNotNull(port);
//    MontiArcParser parser = new MontiArcParser();
//    ASTPort astport = parser.parseString_Port(port.toString()).get();
//
//    // out String strOut
//    assertFalse(astport.isIncoming());
//    assertEquals(((ASTSimpleReferenceType) astport.getType()).getNames().get(0), "String");
//    assertFalse(((ASTSimpleReferenceType) astport.getType()).getTypeArguments().isPresent());
//    assertEquals(astport.getName().get(), "strOut");
//  }
//
//  @Ignore
//  @Test
//  public void testPortSymbolComplexIn() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//    PortSymbol port = symTab.<PortSymbol>resolve(
//        "a.TypesTest.complexIn", PortSymbol.KIND).orElse(null);
//    assertNotNull(port);
//    MontiArcParser parser = new MontiArcParser();
//    ASTPort astport = parser.parseString_Port(port.toString()).get();
//
//    // in List<NewType<String, List<String>>> complexIn
//    assertTrue(astport.isIncoming());
//    assertEquals(((ASTSimpleReferenceType) astport.getType()).getNames().get(0), "List");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astport.getType()).getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "NewType");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astport.getType()).getTypeArguments().get().getTypeArguments().get(0))
//        .getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "String");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astport.getType()).getTypeArguments().get().getTypeArguments().get(0))
//        .getTypeArguments().get().getTypeArguments().get(1)).getNames().get(0), "List");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astport.getType()).getTypeArguments().get().getTypeArguments().get(0))
//        .getTypeArguments().get().getTypeArguments().get(1)).getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "String");
//    assertEquals(astport.getName().get(), "complexIn");
//  }
//
//  @Test
//  public void testConnectorSymbolIncoming() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ConnectorSymbol con = symTab.<ConnectorSymbol>resolve(
//        "a.TypesTest.gen.incoming", ConnectorSymbol.KIND).orElse(null);
//    assertNotNull(con);
//    MontiArcParser parser = new MontiArcParser();
//    ASTConnector astcon = parser.parseString_Connector("connect " + con.toString() + ";").get();
//
//    // incoming -> gen.incoming
//    assertEquals(astcon.getSource().toString(), "incoming");
//    assertEquals(astcon.getTargets().size(), 1);
//    assertEquals(astcon.getTargets().get(0).toString(), "gen.incoming");
//  }
//
//  @Ignore
//  @Test
//  public void testComponentInstanceSymbolIncoming() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentInstanceSymbol inst = symTab.<ComponentInstanceSymbol>resolve("a.TypesTest.complexPartner", ComponentInstanceSymbol.KIND).orElse(null);
//    assertNotNull(inst);
//    MontiArcParser parser = new MontiArcParser();
//    ASTSubComponent astInst = parser.parseString_SubComponent("component " + inst.toString() + ";").get();
//
//    // GenericPartner<List<NewType<String, List<String>>>> complexPartner
//    assertEquals(((ASTSimpleReferenceType) astInst.getType()).getNames().get(0), "GenericPartner");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astInst.getType()).getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "List");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astInst.getType()).getTypeArguments().get().getTypeArguments().get(0))
//        .getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "NewType");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astInst.getType()).getTypeArguments().get().getTypeArguments().get(0))
//        .getTypeArguments().get().getTypeArguments().get(0)).getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "String");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astInst.getType()).getTypeArguments().get().getTypeArguments().get(0))
//        .getTypeArguments().get().getTypeArguments().get(0)).getTypeArguments().get().getTypeArguments().get(1)).getNames().get(0), "List");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astInst.getType()).getTypeArguments().get().getTypeArguments().get(0))
//        .getTypeArguments().get().getTypeArguments().get(0)).getTypeArguments().get().getTypeArguments().get(1)).getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "String");
//    assertEquals(astInst.getInstances().size(), 1);
//    assertEquals(astInst.getInstances().get(0).getName(), "complexPartner");
//  }
//
//  @Ignore
//  @Test
//  public void testComponentInstanceSymbolCp() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentInstanceSymbol inst = symTab.<ComponentInstanceSymbol>resolve("params.UsingComplexGenericParams.cp", ComponentInstanceSymbol.KIND).orElse(null);
//    assertNotNull(inst);
//    MontiArcParser parser = new MontiArcParser();
//    ASTSubComponent astInst = parser.parseString_SubComponent("component " + inst.toString() + ";").get();
//
//    System.out.println(inst.toString());
//
//    // ComplexGenericParams<K, V> (new int[]{1, 2, 3}, new HashMap<List<K>, List<V>>()) cp
//    assertEquals(((ASTSimpleReferenceType) astInst.getType()).getNames().get(0), "ComplexGenericParams");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astInst.getType()).getTypeArguments().get().getTypeArguments().get(0)).getNames().get(0), "K");
//    assertEquals(((ASTSimpleReferenceType) ((ASTSimpleReferenceType) astInst.getType()).getTypeArguments().get().getTypeArguments().get(1)).getNames().get(0), "V");
//    assertEquals(astInst.getArguments().size(), 2);
//    assertEquals(((ASTArrayCreator) astInst.getArguments().get(0).getCreator().get()).getCreatedName().getIdentifierAndTypeArguments().get(0).getName(), "int");
//    assertEquals(((ASTArrayDimensionByInitializer) (((ASTArrayCreator) astInst.getArguments().get(0).getCreator().get()).
//        getArrayDimensionSpecifier())).getArrayInitializer().getVariableInitializers().size(), 3); // {1, 2, 3}
//  }
//
//  @Test
//  public void testComponentTypesTest() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve("a.TypesTest", ComponentSymbol.KIND).orElse(null);
//    assertNotNull(cmp);
//    MontiArcParser parser = new MontiArcParser();
//    ASTMACompilationUnit unit = parser.parse_String(cmp.toString()).get();
//
//    assertEquals(unit.getPackage().stream().collect(Collectors.joining(".")), "a");
//    assertTrue(unit.getImportStatements().stream().map(a -> a.getImportList().stream().collect(Collectors.joining("."))).
//        collect(Collectors.toList()).containsAll(Arrays.asList("java.lang.String", "java.lang.Integer",
//        "a.myTypes.List", "a.myTypes.NewType", "z.GenericPartner")));
//    assertEquals(unit.getComponent().getName(), "TypesTest");
//    assertEquals(unit.getComponent().getPorts().size(), 6); // Ports already tested before
//    assertEquals(unit.getComponent().getConnectors().size(), 6);
//    assertEquals(unit.getComponent().getSubComponents().size(), 2);
//  }
//
//  @Test
//  public void testComponentGenericCompWithInnerGenericComp() throws Exception {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve("a.GenericCompWithInnerGenericComp", ComponentSymbol.KIND).orElse(null);
//    assertNotNull(cmp);
//    System.out.println(cmp.toString());
//
//    MontiArcParser parser = new MontiArcParser();
//    ASTMACompilationUnit unit = parser.parse_String(cmp.toString()).get();
//    assertEquals(unit.getComponent().getInnerComponents().size(), 1);
//  }
//
//  @Test
//  public void testIndentPrinter() {
//    String s = IndentPrinter.groups("import {0};\n")
//        .<ArrayList<String>>params(Lists.newArrayList("de.monticore", "de.montiarc")).toString();
//    System.out.println(s);
//    assertEquals(s, "import de.monticore;\nimport de.montiarc;\n");
//
//    s = IndentPrinter.groups("package {0};\n")
//        .<Optional<String>>params(Optional.of("de.test")).toString();
//    System.out.println(s);
//    assertEquals(s, "package de.test;\n");
//
//    s = IndentPrinter.groups("package {0};\n")
//        .<Optional>params(Optional.empty()).toString();
//    System.out.println(s);
//    assertEquals(s, "");
//  }
//
//  @Test
//  public void testIndentPrinterCollection() {
//    String s = IndentPrinter.groups("in Integer {0 : 'ports ' : ', ' : ';\n'}")
//        .<ArrayList<String>>params(Lists.newArrayList("in1", "in2", "in3")).toString();
//    System.out.println(s);
//    assertEquals(s, "ports in Integer in1, in Integer in2, in Integer in3;\n");
//
//    s = IndentPrinter.groups("in Integer {0 : 'ports ' : ', ' : ';\n'}")
//        .<ArrayList<String>>params(new ArrayList<>()).toString();
//    System.out.println(s);
//    assertEquals(s, "");
//
//    s = IndentPrinter.groups("hallo {0 : 'start ' : ' end'}")
//        .<ArrayList<String>>params(Lists.newArrayList("Tom geht.", "Bianca lebt!", "Arthur auch?"))
//        .toString();
//    System.out.println(s);
//    assertEquals(s, "start hallo Tom geht.hallo Bianca lebt!hallo Arthur auch? end");
//
//    s = IndentPrinter.groups("hallo {0 : 'start ' : ' end'}")
//        .<ArrayList<String>>params(new ArrayList<>()).toString();
//    System.out.println(s);
//    assertEquals(s, "");
//
//    s = IndentPrinter.groups("abc {0 : ' and '} def")
//        .<ArrayList<String>>params(Lists.newArrayList("Jan", "Olaf", "Otto", "Norman", "Dieter"))
//        .toString();
//    System.out.println(s);
//    assertEquals(s, "abc Jan def and abc Olaf def and abc Otto def and abc Norman def and abc Dieter def");
//
//    s = IndentPrinter.groups("abc {0 : ' and '} def")
//        .<ArrayList<String>>params(new ArrayList<>()).toString();
//    System.out.println(s);
//    assertEquals(s, "");
//  }
//
//  @Test
//  public void testHandler() {
//    String s = IndentPrinter.groups("\tpoint {0 : 'points:\n' : '\n' : ';\n'}").
//        <ArrayList<Point>>params(Lists.newArrayList(new Point(10, 15), new Point(30, 35)))
//        .handle(Point.class, p -> "(" + p.getX() + ", " + p.getY() + ")").toString();
//    System.out.println(s);
//    assertEquals(s, "points:\n\tpoint (10.0, 15.0)\n\tpoint (30.0, 35.0);\n");
//  }
//
//  @Test
//  public void testFormalParametersExtends() {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve("a.AutoConnectGeneric", ComponentSymbol.KIND).orElse(null);
//    assertNotNull(cmp);
//    String types = SymbolPrinter.printFormalTypeParameters(cmp.getFormalTypeParameters());
//    System.out.println(types);
//    assertEquals(types , "<T extends a.myTypes.DBInterface>");
//  }
//
//  @Test
//  public void testFormalParametersExtends2() {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve("a.AutoConnectGeneric2", ComponentSymbol.KIND).orElse(null);
//    assertNotNull(cmp);
//    String types = SymbolPrinter.printFormalTypeParameters(cmp.getFormalTypeParameters());
//    System.out.println(types);
//    assertEquals("<T extends a.myTypes.DBInterface&a.myTypes.DBInterface2<java.lang.Boolean,java.lang.Integer>>", types);
//  }
//
//  @Test
//  public void testFormalParametersExtends3() {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve("a.AutoConnectGeneric3", ComponentSymbol.KIND).orElse(null);
//    assertNotNull(cmp);
//    String types = SymbolPrinter.printFormalTypeParameters(cmp.getFormalTypeParameters());
//    System.out.println(types);
//    assertEquals("<T extends a.myTypes.DBInterface2<java.lang.Boolean[],java.lang.Integer[][]>>", types);
//  }
//
//  @Test
//  public void testFormalParametersExtends4() {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve("a.AutoConnectGeneric4", ComponentSymbol.KIND).orElse(null);
//    assertNotNull(cmp);
//    String types = SymbolPrinter.printFormalTypeParameters(cmp.getFormalTypeParameters());
//    System.out.println(types);
//    assertEquals("<T extends a.myTypes.DBInterface2<? super java.lang.Integer,? extends a.myTypes.NewType<java.lang.Boolean[],java.lang.Integer[][][]>>>", types);
//  }
//
//  @Test
//  public void testFormalParametersExtends5() {
//    Scope symTab = createSymTab("src/test/resources/arc/symtab");
//
//    ComponentSymbol cmp = symTab.<ComponentSymbol>resolve("a.AutoConnectGeneric5", ComponentSymbol.KIND).orElse(null);
//    assertNotNull(cmp);
//    String types = SymbolPrinter.printFormalTypeParameters(cmp.getFormalTypeParameters());
//    System.out.println(types);
//    assertEquals("<T extends a.myTypes.DBInterface2<? super java.lang.Integer,? extends java.lang.Boolean>>", types);
//  }
//}
