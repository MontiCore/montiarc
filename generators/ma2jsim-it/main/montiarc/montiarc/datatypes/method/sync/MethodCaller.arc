/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.method.sync;

import montiarc.types.AbstractMethodProvider;
import montiarc.types.AmbiguousMethodProvider;
import montiarc.types.MethodProvider;

component MethodCaller {
  port in int number;

  <<sync>> automaton {
    initial state A;

    A -> A / {
      // --- Static methods ---
      AbstractMethodProvider.staticEmpty();
      montiarc.types.AbstractMethodProvider.staticEmpty();

      MethodProvider.staticEmpty();
      montiarc.types.MethodProvider.staticEmpty();

      MethodProvider.staticChildEmpty();
      montiarc.types.MethodProvider.staticChildEmpty();

      // --- Constructor with method call ---
      MethodProvider.MethodProvider().empty();
      montiarc.types.MethodProvider.MethodProvider().empty();

      // --- Method call ---
      AbstractMethodProvider abstractProvider = MethodProvider.MethodProvider();
      abstractProvider.staticEmpty();
      abstractProvider.abstractEmpty();
      abstractProvider.empty();

      // --- Assignment ---
      int var = abstractProvider.field;
      var = abstractProvider.staticField;
      AbstractMethodProvider.staticField = abstractProvider.field;

      // --- Qualified variable --
      montiarc.types.MethodProvider provider = MethodProvider.MethodProvider();
      provider.staticEmpty();
      provider.staticChildEmpty();
      provider.abstractEmpty();
      provider.empty();

      // --- Assignment ---
      provider.field = AbstractMethodProvider.staticField;
      montiarc.types.AbstractMethodProvider.staticField = provider.field;

      //  --- Ambiguous method call  ---
      AmbiguousMethodProvider.call(number, 1);
    };
  }
}
