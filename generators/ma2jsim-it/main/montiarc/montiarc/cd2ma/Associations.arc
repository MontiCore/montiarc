/* (c) https://github.com/MontiCore/monticore */
package montiarc.cd2ma;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import montiarc.types.*;

component Associations {

  port in InterfaceLeft i1;
  port out InterfaceLeft o1;
  port out Set<InterfaceLeft> o7;
  port out List<InterfaceLeft> o13;
  port out Optional<InterfaceLeft> o19;

  port in InterfaceRight i2;
  port out InterfaceRight o2;
  port out Set<InterfaceRight> o8;
  port out List<InterfaceRight> o14;
  port out Optional<InterfaceRight> o20;

  port in AbstractClassLeft i3;
  port out AbstractClassLeft o3;
  port out Set<AbstractClassLeft> o9;
  port out List<AbstractClassLeft> o15;
  port out Optional<AbstractClassLeft> o21;

  port in AbstractClassRight i4;
  port out AbstractClassRight o4;
  port out Set<AbstractClassRight> o10;
  port out List<AbstractClassRight> o16;
  port out Optional<AbstractClassRight> o22;

  port in ClassLeft i5;
  port out ClassLeft o5;
  port out Set<ClassLeft> o11;
  port out List<ClassLeft> o17;
  port out Optional<ClassLeft> o23;

  port in ClassRight i6;
  port out ClassRight o6;
  port out Set<ClassRight> o12;
  port out List<ClassRight> o18;
  port out Optional<ClassRight> o24;

  automaton {
    initial state S;
    S -> S i1 / {
      // o2 = i1.interfaceRight;
      // o8 = i1.interfaceRightSet;
      // o14 = i1.interfaceRightList;
      // o20 = i1.interfaceRightOptional;
    }
    S -> S i2 / {
      // o1 = i2.interfaceLeft;
      // o7 = i2.interfaceLeftSet;
      // o13 = i2.interfaceLeftList;
      // o19 = i2.interfaceLeftOptional;
    }
    S -> S i3 / {
      // o2 = i3.interfaceRight;
      o4 = i3.abstractClassRight;
      // o8 = i3.interfaceRightSet;
      o10 = i3.abstractClassRightSet;
      // o14 = i3.interfaceRightList;
      o16 = i3.abstractClassRightList;
      // o20 = i3.interfaceRightOptional;
      o22 = i3.abstractClassRightOptional;
    }
    S -> S i4 / {
      // o1 = i4.interfaceLeft;
      // o3 = i4.abstractClassLeft;
      // o7 = i4.interfaceLeftSet;
      o9 = i4.abstractClassLeftSet;
      // o13 = i4.interfaceLeftList;
      o15 = i4.abstractClassLeftList;
      // o19 = i4.interfaceLeftOptional;
      o21 = i4.abstractClassLeftOptional;
    }
    S -> S i5 / {
      // o2 = i5.interfaceRight;
      o4 = i5.abstractClassRight;
      o6 = i5.classRight;
      // o8 = i5.interfaceRightSet;
      o10 = i5.abstractClassRightSet;
      o12 = i5.classRightSet;
      // o14 = i5.interfaceRightList;
      o16 = i5.abstractClassRightList;
      o18 = i5.classRightList;
      // o20 = i5.interfaceRightOptional;
      o22 = i5.abstractClassRightOptional;
      o24 = i5.classRightOptional;
    }
    S -> S i6 / {
      // o1 = i6.interfaceLeft;
      // o3 = i6.abstractClassLeft;
      // o5 = i6.classLeft;
      // o7 = i6.interfaceLeftSet;
      o9 = i6.abstractClassLeftSet;
      o11 = i6.classLeftSet;
      // o13 = i6.interfaceLeftList;
      o15 = i6.abstractClassLeftList;
      o17 = i6.classLeftList;
      // o19 = i6.interfaceLeftOptional;
      o21 = i6.abstractClassLeftOptional;
      o23 = i6.classLeftOptional;
    }
  }
}
