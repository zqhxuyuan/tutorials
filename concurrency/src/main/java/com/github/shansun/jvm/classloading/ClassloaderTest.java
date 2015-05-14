/**
 * wgz
 * classloader.demo
 * ClassloaderDemo.java
 * 
 * 2014-8-13-下午02:07:21
 *  2014wgz
 * 
 */
package com.github.shansun.jvm.classloading;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 不同的类加载器对instanceOf结果的影响
 */
public class ClassloaderTest {
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
        //自定义类加载器: 加载与自己在同一个路径下的Class文件
		ClassLoader myclassloader=new ClassLoader() {
			public Class<?> loadClass(String name) throws ClassNotFoundException{
				String path=name.substring(name.lastIndexOf(".")+1).concat(".class");
				InputStream is=getClass().getResourceAsStream(path);
				try{
					if(is==null){
						return super.loadClass(name);
					}
					byte[] availBytes=new byte[is.available()];
					is.read(availBytes);
					return super.defineClass(name, availBytes, 0, availBytes.length);
				}catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						if(is!=null){
							is.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}
		};

        //加载并实例化一个ClassLoaderTest对象
		Object obj = myclassloader.loadClass("com.github.shansun.jvm.classloading.ClassloaderTest").newInstance();
		System.out.println(obj.getClass());

		//显示false 因为此时obj的类加载器是 myclassloader, 而 ClassloaderTest的类加载是系统类加载器，所以class对象不相同
		System.out.println(obj instanceof com.github.shansun.jvm.classloading.ClassloaderTest);
	}
}
