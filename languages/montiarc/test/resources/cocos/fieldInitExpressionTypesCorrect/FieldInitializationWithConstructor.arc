/* (c) https://github.com/MontiCore/monticore */
package fieldInitExpressionTypesCorrect;

import Message;

/**
 * Valid model.
 */
component FieldInitializationWithConstructor {
  Message msg1 = Message.Message(5);
  Message msg2 = msg1.getThis();
  Message msg3 = msg2.getThis().getThis();
}