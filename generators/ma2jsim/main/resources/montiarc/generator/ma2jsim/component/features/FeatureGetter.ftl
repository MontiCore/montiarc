<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTArcFeature ast -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
@Override
public boolean ${prefixes.feature()}${ast.getName()}() {
  return this.${prefixes.feature()}${ast.getName()};
}