/*
 * Copyright 2016 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.sbt.kantan

import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import com.typesafe.sbt.SbtGit.git
import kantan.sbt.release.KantanRelease
import kantan.sbt.scalafmt.KantanScalafmtPlugin, KantanScalafmtPlugin.autoImport._
import kantan.sbt.scalastyle.KantanScalastylePlugin, KantanScalastylePlugin.autoImport._
import sbt._, Keys._
import sbtrelease.ReleasePlugin.autoImport._, ReleaseTransformations._

/** Plugin that sets kantan-specific values.
  *
  * This is really only meant for kantan projects. Don't use this unless you're me.
  *
  * In order for kantan builds to behave properly, the following two lines *must* be present in the `build.sbt` files:
  * {{{
  * kantanProject in ThisBuild := "foobar"
  * startYear     in ThisBuild := Some(1978)
  * }}}
  */
object KantanKantanPlugin extends AutoPlugin {
  object autoImport {
    val kantanProject: SettingKey[String] = settingKey("Name of the kantan project")
  }
  import autoImport._

  override def trigger = allRequirements

  override def requires = KantanScalastylePlugin && KantanScalafmtPlugin

  override lazy val projectSettings = generalSettings ++ remoteSettings

  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  lazy val generalSettings: Seq[Setting[_]] = Seq(
    name                 := s"kantan.${kantanProject.value}",
    organization         := "com.nrinaudo",
    organizationHomepage := Some(url("https://nrinaudo.github.io")),
    organizationName     := "Nicolas Rinaudo",
    crossScalaVersions   := Seq("2.12.14", "2.13.3"),
    scalaVersion         := crossScalaVersions.value.last,
    licenses             := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    scalastyleResource   := Some("/kantan/sbt/scalastyle-config.xml"),
    scalafmtResource     := Some("/kantan/sbt/scalafmt.conf"),
    // This must be enabled for all modules, to make sure that aggregation picks up on multi-release. Typically,
    // root projects are unpublished, but if they do not have releaseCrossBuilder set to true, no underlying project
    // will either.
    releaseCrossBuild             := true,
    releasePublishArtifactsAction := publishSigned.value,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      KantanRelease.runCoverageOff,
      KantanRelease.runCheckStyle,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      releaseStepCommand("sonatypeReleaseAll"),
      KantanRelease.runPushSite,
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
    developers := List(
      Developer("nrinaudo", "Nicolas Rinaudo", "nicolas@nrinaudo.com", url("https://twitter.com/nicolasrinaudo"))
    )
  )

  /** Remote identifiers, computed from [[autoImport.kantanProject]]. */
  lazy val remoteSettings: Seq[Setting[_]] = Seq(
    homepage       := Some(url(s"https://nrinaudo.github.io/kantan.${kantanProject.value}")),
    apiURL         := Some(url(s"https://nrinaudo.github.io/kantan.${kantanProject.value}/api/")),
    git.remoteRepo := s"git@github.com:nrinaudo/kantan.${kantanProject.value}.git",
    scmInfo := Some(
      ScmInfo(
        url(s"https://github.com/nrinaudo/kantan.${kantanProject.value}"),
        s"scm:git:git@github.com:nrinaudo/kantan.${kantanProject.value}.git"
      )
    )
  )
}
