package industry;

component Filtering {
  ports in Integer omega,
        in Integer windSpeed,
        out Integer filteredOmega,
        out Integer filteredWindSpeed;
}