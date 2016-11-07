package com.zqh.guice.hello;


import com.google.inject.AbstractModule;

public class MyApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        //bind the service to implementation class
        bind(MessageService.class).to(EmailService.class);
    }

}