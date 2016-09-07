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

package kantan.build

import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.site.SitePlugin
import com.typesafe.sbt.site.preprocess.PreprocessPlugin
import com.typesafe.sbt.site.util.SiteHelpers._
import sbt._
import sbt.Keys._
import sbtunidoc.Plugin._
import sbtunidoc.Plugin.UnidocKeys._
import tut.Plugin._

/** Plugin for documentation projects.
  *
  * Enabling this will set things up so that:
  *  - `makeSite` compiles all tut files, generates the unidoc API and builds a complete documentation site.
  *  - `ghpagesPushSite` generates the site and pushes it to the current repository's github pages.
  */
object DocumentationPlugin extends AutoPlugin {
  // - Public settings -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  object autoImport {
    val tutSiteDir: SettingKey[String] = settingKey[String]("Website tutorial directory")
    val apiSiteDir: SettingKey[String] = settingKey[String]("Unidoc API directory")
  }
  import autoImport._

  override def projectSettings = ghpages.settings ++ unidocSettings ++ tutSettings ++ Seq(
    tutSiteDir := "_tut",
    apiSiteDir := "api",
    scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
      "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
      "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
    ),
    tutScalacOptions ~= (_.filterNot(Set("-Ywarn-unused-import"))),
    ghpagesNoJekyll := false,
    includeFilter in SitePlugin.autoImport.makeSite :=
    "*.yml" | "*.md" | "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.eot" | "*.svg" | "*.ttf" |
    "*.woff" | "*.woff2" | "*.otf",
    addMappingsToSiteDir(tut, tutSiteDir),
    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), apiSiteDir)
  )

  override def requires = PreprocessPlugin && UnpublishedPlugin

  override def trigger = noTrigger
}
