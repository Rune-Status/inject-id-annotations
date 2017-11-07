package io.runetek.tools.id_inject;

import com.github.javaparser.utils.SourceZip;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since 11/07/2017
 */
public class InjectIdAnnotations {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Missing argument: <source>");
            System.exit(1);
        }

        final File source = new File(args[0]);
        if (isArchive(source)) {
            final AtomicInteger idCounter = new AtomicInteger(1);
            try {
                final SourceZip zip = new SourceZip(source.toPath());
                zip.parse((relativeZipEntryPath, result) -> {
                    result.getResult().ifPresent(cu -> {
                        cu.accept(new InjectIdAnnotationVisitor(), idCounter);
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Only archive (.zip|.jar) usage is supported at this time.");
            System.exit(1);
        }
    }

    private static boolean isArchive(final File f) {
        final String name = f.getName();
        return name.endsWith(".jar") || name.endsWith(".zip");
    }
}
