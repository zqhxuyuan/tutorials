package com.github.code4craft.helloworld.creational.abstract_factory;

import com.github.code4craft.helloworld.SplitHelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class DesignPatternSplitHelloWorldFactory implements SplitHelloWorldFactory {

    @Override
    public SplitHelloWorld.HelloWorldInterjection createHelloWorldInterjection() {
        return new SplitHelloWorld.DefaultInterjection();
    }

    @Override
    public SplitHelloWorld.HelloWorldObject createHelloWorldObject() {
        return new DesignPatternHelloWorldObject();
    }

    class DesignPatternHelloWorldObject implements SplitHelloWorld.HelloWorldObject {

        @Override
        public String object() {
            return "Abstract Factory";
        }
    }
}
