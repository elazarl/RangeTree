package com.github.elazarl.rangetree.examples.starbucks;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Guice binding
 */
public class GuiceModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(StarbucksNear.class);
        binder.bind(StarbucksFinder.class);
    }
}
