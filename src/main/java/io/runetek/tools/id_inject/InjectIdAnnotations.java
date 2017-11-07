package io.runetek.tools.id_inject;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceZip;
import io.runetek.annotations.ID;

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
            AtomicInteger counter = new AtomicInteger(1);
            try {
                final SourceZip zip = new SourceZip(source.toPath());
                zip.parse((relativeZipEntryPath, result) -> {
                    result.getResult().ifPresent(cu -> {
                        cu.accept(new VoidVisitorAdapter<Object>() {
                            @Override
                            public void visit(final ClassOrInterfaceDeclaration n, final Object arg) {
                                cu.addImport(ID.class);
                                injectIdAnnotation(n, counter.getAndIncrement());
                                super.visit(n, arg);
                                System.out.println(n.toString());
                            }

                            @Override
                            public void visit(final FieldDeclaration n, final Object arg) {
                                super.visit(n, arg);
                                injectIdAnnotation(n, counter.getAndIncrement());
                            }

                            @Override
                            public void visit(final MethodDeclaration n, final Object arg) {
                                super.visit(n, arg);
                                injectIdAnnotation(n, counter.getAndIncrement());
                            }
                        }, null);
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

    private static <T extends Node & NodeWithAnnotations<T>> void injectIdAnnotation(T node, int id) {
        node.addSingleMemberAnnotation(ID.class, Integer.toString(id));
    }

    private static boolean isArchive(final File f) {
        final String name = f.getName();
        return name.endsWith(".jar") || name.endsWith(".zip");
    }
}
