package org.hotcode.hotcode.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.hotcode.hotcode.HotCodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to dump transformed class to file jdk.
 * 
 * @author khotyn 13-6-24 PM8:41
 */
public class ClassDumper {

    private static final Logger logger = LoggerFactory.getLogger(ClassDumper.class);

    public static void dump(String className, byte[] classfile) {
        if (!HotCodeConfiguration.ENABLE_CLASS_DUMP) {
            return;
        }

        String dumpPath = HotCodeConfiguration.CLASS_DUMP_PATH;

        if (StringUtils.isBlank(dumpPath)) {
            dumpPath = "/tmp";
        }

        try {
            FileUtils.writeByteArrayToFile(new File(dumpPath + "/" + className + ".class"), classfile);
        } catch (IOException e) {
            logger.error("Error occur while try writing " + className + " to disk.", e);
        }
    }
}
