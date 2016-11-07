package com.zqh.avro;

import org.apache.avro.AvroRemoteException;

import example.proto.Curse;
import example.proto.Greeting;
import example.proto.HelloWorld;

public class HelloWorldImpl implements HelloWorld {

	@Override
	public Greeting hello(Greeting greeting) throws AvroRemoteException, Curse {
		if(greeting.getMessage().toString().equalsIgnoreCase("how are you")){  
            greeting.setMessage("not too bad");  
            return greeting;  
        }  
        return new Greeting("hello");  
	}

}
