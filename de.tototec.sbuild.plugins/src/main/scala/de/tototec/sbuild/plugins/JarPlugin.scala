package de.tototec.sbuild.plugins

import de.tototec.sbuild.Project
import de.tototec.sbuild.Plugin
import de.tototec.sbuild.Target
import de.tototec.sbuild.Util.NullSafe
import de.tototec.sbuild.TargetContext
import de.tototec.sbuild.ant.tasks.AntJar
import de.tototec.sbuild.Path

class JarPlugin()(implicit _project: Project) extends Plugin {

  var targetDir: String = null
  var artifact: String = null
  var version: String = null

  var classesDir: String = null

  var jarCustomizer: AntJar => Unit = null

  override def init {
    targetDir = targetDir whenNull "target"
    artifact = artifact whenNull "main"
    classesDir = classesDir whenNull "target/classes"

    val finalName = targetDir + "/" + artifact + (if (version != null) s"-$version" else "") + ".jar"

    Target(finalName) dependsOn "compile" exec { ctx: TargetContext =>
      val antJar = new AntJar(destFile = ctx.targetFile.get, baseDir = Path(classesDir))
      jarCustomizer.notNull(c => c(antJar))
      antJar.execute()
    }
    Target("phony:jar") dependsOn finalName

  }

}