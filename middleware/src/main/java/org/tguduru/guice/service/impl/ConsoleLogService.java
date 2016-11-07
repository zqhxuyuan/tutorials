package org.tguduru.guice.service.impl;

import org.tguduru.guice.service.Log;

/**
 * An implementation of {@link org.tguduru.guice.service.Log} which logs messages onto console.
 * @author Guduru, Thirupathi Reddy
 */
public class ConsoleLogService implements Log{
    @Override
    public void log() {
        System.out.println("Logging onto console");
    }
}
