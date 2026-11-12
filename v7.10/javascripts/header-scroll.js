// (c) https://github.com/MontiCore/monticore
// Marks the document as scrolled so the header can switch from solid to translucent.
(function () {
  var root = document.documentElement;
  function update() {
    if (window.scrollY > 0) {
      root.setAttribute("data-scrolled", "");
    } else {
      root.removeAttribute("data-scrolled");
    }
  }
  window.addEventListener("scroll", update, { passive: true });
  update();
})();
