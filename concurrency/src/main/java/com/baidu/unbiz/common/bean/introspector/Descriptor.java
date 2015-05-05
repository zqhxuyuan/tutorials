/**
 * 
 */
package com.baidu.unbiz.common.bean.introspector;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月18日 下午8:32:16
 */
public abstract class Descriptor {

    protected final ClassDescriptor classDescriptor;
    protected final boolean isPublic;

    protected Descriptor(ClassDescriptor classDescriptor, boolean isPublic) {
        this.classDescriptor = classDescriptor;
        this.isPublic = isPublic;
    }

    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean matchDeclared(boolean declared) {
        if (!declared) {
            return isPublic;
        }
        return true;
    }

    public abstract String getName();

}