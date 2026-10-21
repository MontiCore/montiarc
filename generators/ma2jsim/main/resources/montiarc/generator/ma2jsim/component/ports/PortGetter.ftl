<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

@Override
public <@Util.getStaticPortInterface portSym/><<@Util.getPortTypeString portSym.getType()/>>
  ${prefixes.port()}${portSym.getName()}() {
  return this.${prefixes.port()}${portSym.getName()};
}