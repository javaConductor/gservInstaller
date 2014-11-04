package org.groovyrest.gserv.installer
/**
 * Created by lcollins on 10/31/2014.
 */
class EnvPathUtils {

    static def lastPart = "#gserv - DO NOT REMOVE - gserv"
    static  def env = System.getenv()

    static def isBash(env){
        (env?.SHELL == "bash" || env?.SHELL?.endsWith( "/bash") )
    }

    static def isCsh(env){
        (env?.SHELL == "csh" || env?.SHELL?.endsWith( "/csh") ) ||
        (env?.SHELL == "tcsh" || env?.SHELL?.endsWith( "/tcsh") )
    }

    /**
     *
     * @return Current User home directory
     */
    public static File homeDir() {
        File userHome = new File(System.getProperty("user.home"));
        userHome
    }

    /**
     *
     * @param dirPath
     * @return
     */
    static def  addScriptDirToPath( File dirPath){
        def ret = []
        //// Default
        def profile = new File(homeDir(), ".profile")
        if(profile.exists())
           addPathSettingToScript(profile, dirPath)
        ret << profile
        //// Shell specific
        if (isBash(env) ){
            def bashProfile = new File(homeDir(), ".bashrc")
            if(bashProfile.exists())
                addPathSettingToScript(bashProfile, dirPath)
            ret << bashProfile
        }//if

        if (env.SHELL == "zsh"){
            def bashProfile = new File(homeDir(), ".zshrc")
            if(bashProfile.exists())
            addPathSettingToScript(bashProfile, dirPath)
            ret << bashProfile
        }

    }

    /**
     *
     * @param scriptFile
     * @param dirPath
     * @return
     */
    static def addPathSettingToScript(File scriptFile, File dirPath){
        def line = shellLineForPath(dirPath)
        def lines = scriptFile.readLines()
        lines << line
        writeLinesToFile(lines, scriptFile)
    }

    /**
     *
     * @param dirPath
     * @return
     */
    public static shellLineForPath( dirPath ) {
        shellLineFromShellName(env.SHELL, dirPath)
    }

    /**
     *
     * @param shellName
     * @param dirPath
     * @return
     */
    public static String shellLineFromShellName(String shellName, dirPath) {
        def line

        if (isBash(env)) {
            line = "export PATH=\$PATH:${dirPath.absolutePath} $lastPart"
        } else if (isCsh(env)) {
            line = "export PATH=\$PATH:${dirPath.absolutePath} $lastPart"
        } else{
            switch (shellName) {
                case "bourne":
                    line = "PATH=\$PATH:${dirPath.absolutePath} $lastPart"
                    line += "\nexport PATH"
                    break;
                case "bash":
                    line = "export PATH=\$PATH:${dirPath.absolutePath} $lastPart"
                    break;
                case "csh":
                case "tcsh":
                    line = "setenv PATH \$PATH:${dirPath.absolutePath} $lastPart"
                    break;
                default:
                    line = "export PATH=\$PATH:${dirPath.absolutePath} $lastPart"
                    break;
            }
        }
        "\n$line"
    }//

    static def  removeScriptDirFromPath( ){
        def profile = new File(homeDir(), ".profile")
        removePathSettingFromScript(profile)
        //// Shell specific
        if (env.SHELL == "bash" || env.SHELL?.endsWith("/bash")){
            def bashProfile = new File(homeDir(), ".bashrc")
            removePathSettingFromScript(bashProfile)
        }

        if (env.SHELL == "zsh"){
            def zshProfile = new File(homeDir(), ".zshrc")
            removePathSettingFromScript(zshProfile)
        }

    }

    /**
     *
     * @param scriptFile
     * @return
     */
    static def removePathSettingFromScript(File scriptFile){
        if (!scriptFile.exists())
            return;

        def lines = scriptFile.readLines()
            def newLines = lines.findAll { l ->
                !l.contains( lastPart )
            }
        writeLinesToFile(newLines, scriptFile)
    }

    /**
     * Writes each line in 'lines' to 'file'
     *
     * @param lines
     * @param file
     * @return The number of things written
     */
    static def writeLinesToFile(List lines, File file){
        def fw = new FileWriter( file )
        lines.each { fw.append(it).append('\n')}
        fw.close()
        lines.size()
    }
}
