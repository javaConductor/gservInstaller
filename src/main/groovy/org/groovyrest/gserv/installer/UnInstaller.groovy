package org.groovyrest.gserv.installer

import org.apache.commons.io.FileUtils

import java.nio.file.Files

/**
 * Created by lcollins on 10/31/2014.
 */
class UnInstaller {

    def unInstall(File gservHome, String version) {

        switch (version) {
            case "1.0.0":
                break;
            default:
                defaultUnInstall(gservHome, version);
                break;

        }

    }

    /**
     * By default, we remove everything.  We may have to leave the plugin folder.
     * @param gservHome
     * @param version
     * @return
     */
    def defaultUnInstall(File gservHome, String version) {
        //TODO don't forget to (when possible)  preserve the Plugin folder
        //TODO check the version for special unInstall needs
        FileUtils.deleteDirectory(gservHome);
        EnvPathUtils.removeScriptDirFromPath()
    }

}
