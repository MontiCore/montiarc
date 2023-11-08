/* (c) https://github.com/MontiCore/monticore */
package // line comment after package keyword
automata // line comment after package name
; // line comment after package statement

// line comment before import statement
import //line comment after import keyword
Types.OnOff // line comment after import
; // line comment after import statement

// line comment before component head
component // line comment after component keyword
Comments // line comment after component name
{ // line comment after component body opening bracket

  // line comment before port declaration statement
  port // line comment after port keyword
  in // line comment after port direction
  OnOff // line comment after port type
  i // line comment after port name
  ; // line comment after port declaration statement

  // line comment before second port declaration statement
  port // line comment after port keyword
  out // line comment after port direction
  OnOff // line comment after port type
  o1, // line comment after first port name
  o2 // line comment after second port name
  ; // line comment after second port declaration statement

  // line comment before variable declaration statement
  int // line comment after variable type
  v // line comment after variable name
  = // line comment after assignment
  1 // line comment after expression
  ; // line comment after variable declaration statement

  // line comment before automaton
  automaton // line comment after automaton keyword
  { // line comment after automaton opening bracket

    // line comment before first state declaration
    initial // line comment after initial keyword
    { // line comment after ante opening bracket
    } // line comment after ante closing bracket
    state // line comment after state keyword
    S // line comment after state name
    ; // line comment after first state declaration

    // line comment before transition
    S // line comment after source state
    -> // line comment after arrow keyword
    S // line comment after target state
    / // line comment after action delimiter keyword
    { // line comment after transition action opening bracket
    // line comment before expression statement
    o1 = i // line comment after expression
    ; // line comment after expression statement
    } // line comment after transition action closing bracket
    ; // line comment after transition
  }
}
