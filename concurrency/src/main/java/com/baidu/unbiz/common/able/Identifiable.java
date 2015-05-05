package com.baidu.unbiz.common.able;

import java.util.UUID;

/**
 * 无重复唯一认证，这里采用<code>UUID</code>
 *
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月19日 上午2:29:19
 */
public interface Identifiable {

    void setIdentification(UUID id);

    UUID getIdentification();
}
