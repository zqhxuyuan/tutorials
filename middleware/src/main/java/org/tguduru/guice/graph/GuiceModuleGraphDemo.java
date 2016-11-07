package org.tguduru.guice.graph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;

import org.tguduru.guice.scope.SingletonScopeModule;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Demonstrate the generation of the graph of the dependencies in a guice module. Once generates the .dot file, open it
 * with .dot viewer to view the dependency graph.
 * @author Guduru, Thirupathi Reddy
 */
public class GuiceModuleGraphDemo {
    public static void main(final String[] args) throws IOException {
        final PrintWriter printWriter = new PrintWriter(new File("/graph.dot"), "UTF-8");

        final Injector singletonInjector = Guice.createInjector(new SingletonScopeModule());
        final Injector injector = Guice.createInjector(new GraphvizModule());
        final GraphvizGrapher graphvizGrapher = injector.getInstance(GraphvizGrapher.class);
        graphvizGrapher.setOut(printWriter);
        graphvizGrapher.setRankdir("TB");
        graphvizGrapher.graph(singletonInjector);// this one generates the dependency graph for the provided injector.
        printWriter.flush();
        printWriter.close();
        System.out.println("graph created");
    }
}
