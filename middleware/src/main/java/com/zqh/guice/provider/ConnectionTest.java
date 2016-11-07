package com.zqh.guice.provider;

import com.google.inject.*;

public class ConnectionTest {

    public static void main(String args[]){
        Injector injector = Guice.createInjector(
                new Module(){
                    @Override
                    public void configure(Binder binder) {
                        binder.bind(MockConnection.class).toProvider(ConnectionProvider.class);
                    }
                }
        );

        MockConnection connection = injector.getInstance(MockConnection.class);
        connection.connect();
        connection.disConnect();
    }
}