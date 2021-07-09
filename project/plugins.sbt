addSbtPlugin("com.eed3si9n"      % "sbt-unidoc"             % "0.4.3")
addSbtPlugin("com.github.gseitz" % "sbt-release"            % "1.0.13")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"                % "2.0.1")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"           % "2.4.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header"             % "5.6.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-ghpages"            % "0.6.3")
addSbtPlugin("com.typesafe.sbt"  % "sbt-site"               % "1.4.1")
addSbtPlugin("org.scalastyle"    %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.wartremover"   % "sbt-wartremover"        % "2.4.13")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype"           % "3.9.5")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
