/* (c) https://github.com/MontiCore/monticore */
package parser;

component UC {
  port in int i;
  port out int o;

  usecasediagram {
    @i --
      Play,
      Pay;

    ShowAd extend Play [!isPremium];
    RegisterScore extend Play;

    abstract Pay include CheckPremium;
    CreditCard specializes Pay;
    Bank specializes Pay;
    ChangeProfilePicture [isPremium];
  }
}
