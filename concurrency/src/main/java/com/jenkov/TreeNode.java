package com.jenkov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqhxuyuan on 15-4-8.
 *
 * Dead Lock
 * 如果线程1调用parent.addChild(child)方法的同时有另外一个线程2调用child.setParent(parent)方法，
 * 两个线程中的parent表示的是同一个对象，child亦然，此时就会发生死锁
 *
 * 这两个线程需要同时调用parent.addChild(child)和child.setParent(parent)方法，
 * 并且是同一个parent对象和同一个child对象，才有可能发生死锁
 */
public class TreeNode {
    TreeNode parent   = null;
    List children = new ArrayList();

    //parent.addChild() locks parent
    public synchronized void addChild(TreeNode child){
        if(!this.children.contains(child)) { //require child lock
            this.children.add(child);
            child.setParentOnly(this);
        }
    }

    public synchronized void addChildOnly(TreeNode child){
        if(!this.children.contains(child)){
            this.children.add(child);
        }
    }

    //child.setParent locks child
    public synchronized void setParent(TreeNode parent){
        this.parent = parent;  // require parent lock
        parent.addChildOnly(this);
    }

    public synchronized void setParentOnly(TreeNode parent){
        this.parent = parent;
    }
}