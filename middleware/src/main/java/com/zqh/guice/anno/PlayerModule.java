package com.zqh.guice.anno;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class PlayerModule implements Module {

    public void configure(Binder binder) {

        binder.bind(Player.class).annotatedWith(Names.named("Good")).to(GoodPlayer.class);
        binder.bind(Player.class).annotatedWith(Names.named("Bad")).to(BadPlayer.class);
    }

}