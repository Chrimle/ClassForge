/** Forge Java Classes! */
module io.github.chrimle.classforge {
  exports io.github.chrimle.classforge;
  exports io.github.chrimle.classforge.classes;
  exports io.github.chrimle.classforge.enums;

  // Requires (non-static)
  requires io.github.chrimle.exceptionfactory;
  requires io.github.chrimle.semver;
  requires java.compiler;

  // Requires (static)
  requires static org.apiguardian.api;
  requires static org.jetbrains.annotations;
  requires gg.jte.runtime;
  requires gg.jte;
}
