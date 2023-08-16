<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("className")}
protected String name;

public ${className} setName(String name) { this.name = name; return this; }

public String getName() { return this.name; }