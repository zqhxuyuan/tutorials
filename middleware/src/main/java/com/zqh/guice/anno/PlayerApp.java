package com.zqh.guice.anno;

import com.google.inject.Inject;

/**
 * Created by zhengqh on 15/12/6.
 */
public class PlayerApp {

    private Player player;

    @Inject
    public PlayerApp(Player player){
        this.player = player;
    }

    public void play(){
        player.bat();
        player.bowl();
    }

}
