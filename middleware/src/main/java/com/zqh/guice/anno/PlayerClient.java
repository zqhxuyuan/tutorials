package com.zqh.guice.anno;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import javax.inject.Named;
//import com.google.inject.name.Named;

/**
 * http://www.cnblogs.com/youngC/archive/2012/12/21/2828419.html
 */
public class PlayerClient {

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new PlayerModule());

        //@Good Player player = (Player)injector.getInstance(Player.class);

        //null
//        System.out.println(injector.getAllBindings().get(Key.get(Player.class)));

        /**
         * Exception in thread "main" com.google.inject.ConfigurationException: Guice configuration errors:

         1) No implementation for com.zqh.guice.anno.Player was bound.
         while locating com.zqh.guice.anno.Player
         */
//        @Named("Good") Player goodPlayer = (Player)injector.getInstance(Player.class);
//        @Named("Bad") Player badPlayer = (Player)injector.getInstance(Player.class);
//        goodPlayer.bat();
//        goodPlayer.bowl();


        /**
         1) No implementation for com.zqh.guice.anno.Player was bound.
         while locating com.zqh.guice.anno.Player
         for parameter 0 at com.zqh.guice.anno.PlayerApp.<init>(PlayerApp.java:13)
         while locating com.zqh.guice.anno.PlayerApp
         */
        PlayerApp app = injector.getInstance(PlayerApp.class);
        app.play();
    }
}