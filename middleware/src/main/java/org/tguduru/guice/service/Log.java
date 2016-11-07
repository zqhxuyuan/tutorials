package org.tguduru.guice.service;

import com.google.inject.ImplementedBy;

import org.tguduru.guice.service.impl.LogService;

/**
 * @author Guduru, Thirupathi Reddy
 *         Modified on : Jul 27, 2014 10:38:55 PM
 */
@ImplementedBy(LogService.class)
// this annotation tells guice that this implementation is the default injection if one not binding to any
// implementation in guice module configuration.
public interface Log {
    public void log();
}
