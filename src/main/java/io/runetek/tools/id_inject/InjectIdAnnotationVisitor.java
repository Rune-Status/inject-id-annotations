package io.runetek.tools.id_inject;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.runetek.annotations.ID;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since 11/07/2017
 */
class InjectIdAnnotationVisitor extends VoidVisitorAdapter<AtomicInteger> {
    @Override
    public void visit(final CompilationUnit n, final AtomicInteger counter) {
        super.visit(n, counter);
        n.addImport(ID.class);
    }

    @Override
    public void visit(final ClassOrInterfaceDeclaration n, final AtomicInteger counter) {
        injectIdAnnotation(n, counter.getAndIncrement());
        super.visit(n, counter);
        System.out.println(n.toString());
    }

    @Override
    public void visit(final FieldDeclaration n, final AtomicInteger counter) {
        super.visit(n, counter);
        injectIdAnnotation(n, counter.getAndIncrement());
    }

    @Override
    public void visit(final MethodDeclaration n, final AtomicInteger counter) {
        super.visit(n, counter);
        injectIdAnnotation(n, counter.getAndIncrement());
    }

    private <T extends Node & NodeWithAnnotations<T>> void injectIdAnnotation(T node, int id) {
        node.addSingleMemberAnnotation(ID.class, Integer.toString(id));
    }
}
