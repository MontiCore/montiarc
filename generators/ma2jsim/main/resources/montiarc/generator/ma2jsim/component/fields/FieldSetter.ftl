<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("field")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

@Override
public void ${prefixes.setterMethod()}${prefixes.field()}${field.getName()}(<@Util.getTypeString field.getType()/> value) {
  this.${prefixes.field()}${field.getName()} = value;
  <@Log.info log_aspects.fieldValue() "getName()">
    "${field.getName()} = " + montiarc.rte.logging.DataFormatter.format(this.${prefixes.field()}${field.getName()})
  </@Log.info>
}