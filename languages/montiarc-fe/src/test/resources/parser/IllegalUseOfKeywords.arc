/* (c) https://github.com/MontiCore/monticore */
package parser;

/*
 * Invalid model. Uses keywords as identifiers and types.
 * Formerly named "S2" in MontiArc3.
 */
component IllegalUseOfKeywords {
  
  port 
    in connect; //ERROR
  
  port in String out; //ERROR
    
  component out; //ERROR
  
  component components.body.subcomponents._subcomponents.HasStringInputAndOutput in;
    //ERROR
  
  component components.body.subcomponents._subcomponents.HasStringInputAndOutput correct;

}