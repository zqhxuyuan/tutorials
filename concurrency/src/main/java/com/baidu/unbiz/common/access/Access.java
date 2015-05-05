/**
 * 
 */
package com.baidu.unbiz.common.access;

/**
 * 访问接口
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午2:47:03
 */
public interface Access {

    /**
     * 存储访问介质
     * 
     * @param accessable 访问介质 @see Accessable
     * @return 存储路径
     * @throws AccessException
     */
    String store(Resource accessable) throws AccessException;

    /**
     * 获取访问介质
     * 
     * @param id 唯一<code>id</code>
     * @param ext 扩展名
     * @return 访问介质 @see Accessable
     * @throws AccessException
     */
    Resource retrieve(long id, String ext) throws AccessException;

    /**
     * 移除访问介质
     * 
     * @param id 唯一<code>id</code>
     * @param ext 扩展名
     * @return is success
     * @throws AccessException
     */
    boolean remove(long id, String ext) throws AccessException;

    /**
     * 获取介质所在
     * 
     * @param id 唯一<code>id</code>
     * @param ext 扩展名
     * @return where is
     * @throws AccessException
     */
    String getWhere(long id, String ext);

    /**
     * 设备名
     * 
     * @return 设备名
     */
    String name();
}
