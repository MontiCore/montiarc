/* (c) https://github.com/MontiCore/monticore */
package prepost;

spec Storage {

  port in Screw din,
       in int id;
  port out Screw dout;

  Map<Integer, Screw> s = Map();

  ------------------------

  trigger din;
  pre: true;
  post: s.get(screw.id)==screw && dout==epsilon;

  ------------------------

  trigger id;
  pre: s.containsKey(id);
  post: s==s@pre.remove(id) && dout==s@pre.get(id);

}