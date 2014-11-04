package org.groovyrest.gserv.installer

import java.nio.file.*

class Installer {
    static def VERSION_FILENAME = "version.txt";
    static void main(String[] args){
        Installer installer = new Installer();

        /// get the user's home directory
        File userHome = EnvPathUtils.homeDir()
        File gservHome = new File( userHome, ".gserv");
        if (gservHome.exists()){
            /// uninstall previous version - look for Version.txt for the version number
            File versionFile=new File(gservHome, VERSION_FILENAME)
            if (!versionFile.exists()){
                // clear the directory
                Files.delete(gservHome.toPath());
            }else{
                installer.unInstall(gservHome)
            }
        }//if
        installer.install(gservHome);
    }

    EnvPathUtils envPathUtils
    def Installer(){
        /// PRE- CONDITION: org.groovyrest.gserv.installer.Installer MUST have 3 things on its classpath:
        //// gserv.jar
        //// gserv.sh
        //// version.txt

        // validate presence of files
        def bOk = getJarFile() && getScriptFile() && getVersionFile()
        if (!bOk){
            System.err.println("Bad installer jar.  Nothing to install!!")
            throw new InstallationException("Bad installer jar.  Nothing to install!!");
        }

    }

    def copyFile( File destDir, File sourceFile){
        if ( !destDir.exists()){
            Files.createDirectories(destDir.toPath())
        }
        File outFile = new File ( destDir, sourceFile.name);
        OutputStream os = new FileOutputStream(outFile);
        Files.copy(sourceFile.toPath(), os )
        os.close();
        outFile
    }

    def install(File gservHome){
        envPathUtils = new EnvPathUtils(gservHome)
        // destination dirs
        File dirBin = new File(gservHome, "bin")
        File dirScripts = new File(gservHome, "scripts")
        File versionFile = new File(gservHome, VERSION_FILENAME)

        /// 1. copy gserv jar to ~/.gserv/bin - it should be embedded in the org.groovyrest.gserv.installer.Installer.jar (classpath resource)
        // The gserv.jar file should be on the classpath
        File gservJar = getJarFile();
        copyFile( dirBin, gservJar)

        /// 2. Create gserv script in ~/.gserv/gserv
        ///  chmod the script to X for all
        File gservScript = getScriptFile();
        File f = copyFile( dirScripts, gservScript)
        f.renameTo(new File(dirScripts,"gserv"))
        f.setExecutable(true, false)

        /// 2b. Add a file version.txt with the Version/license info for gServ
        File gservVersion = getVersionFile();
        copyFile( gservHome, gservVersion)

        ///3. Add gserv to the PATH
        /// /// add the ~/.gserv/scripts to the PATH in its own line (append)
        envPathUtils.addScriptDirToPath(dirScripts)

        ///5. report what version was installed and where
        /// What Version - get it from the version.txt in the installer jar
        File vFile = getVersionFile()
        def versionProps = new Properties()
        versionProps.load(new FileReader(vFile))
        def version = versionProps.version
        println "gServ v$version installed @${gservHome.absolutePath}."

        ///6. invite user to run the new gserv command
        println "To test installation: type 'gserv' at prompt."
    }



    File resourceAsFile(name){
        def url = ClassLoader.getSystemResource(name)
        def uri = url.toURI()
        new File(uri);
    }

    File getJarFile() {
        resourceAsFile("gserv.jar")
    }

    File getVersionFile() {
        resourceAsFile("version.txt")
    }

    File getScriptFile() {
        resourceAsFile("gserv.sh")
    }

    def unInstall(File gservHome){
        File versionFile=new File(gservHome, VERSION_FILENAME)
        if (!versionFile.exists()){
            // clear the directory
            Files.delete(gservHome.toPath());
        }else{
            Properties p = new Properties();
            def fis = new FileInputStream(versionFile)
            p.load( fis )
            fis.close();
            if( p.name != "gServ" ){
                Files.delete(gservHome.toPath());
            }else {
                new UnInstaller().unInstall(gservHome, p.version);
            }
        }
    }//
}

