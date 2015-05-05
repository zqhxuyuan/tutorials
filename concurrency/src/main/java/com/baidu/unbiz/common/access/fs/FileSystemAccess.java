/**
 *
 */
package com.baidu.unbiz.common.access.fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.baidu.unbiz.common.FileUtil;
import com.baidu.unbiz.common.access.Access;
import com.baidu.unbiz.common.access.AccessException;
import com.baidu.unbiz.common.access.AccessStrategy;
import com.baidu.unbiz.common.access.Resource;
import com.baidu.unbiz.common.access.ResourceHeader;
import com.baidu.unbiz.common.io.ByteArray;
import com.baidu.unbiz.common.io.StreamUtil;
import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月25日 下午7:04:04
 */
public abstract class FileSystemAccess extends CachedLogger implements Access {

    private AccessStrategy strategy;

    protected FileSystemAccess(AccessStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String store(Resource accessable) throws AccessException {
        long id = accessable.getId();

        String where = where(accessable, id);

        String physical = physicalLocation(where);
        if (!FileUtil.exist(physical)) {
            try {
                FileUtil.createFile(physical);
            } catch (IOException e) {
                logger.error("create file error", e);
                throw new AccessException(e);
            }
        }

        ByteArray body = accessable.getBody();

        try {
            OutputStream out = new FileOutputStream(physical);
            StreamUtil.writeBytes(body.getRawBytes(), out, true);
        } catch (IOException e) {
            logger.error("store error", e);
            throw new AccessException(e);
        }

        return physical;

    }

    private String where(Resource accessable, long id) {
        String where = strategy.find(id);
        ResourceHeader header = accessable.getHeader();
        if (header != null && header.ext() != null) {
            where += "." + header.ext();
        }

        return where;
    }

    @Override
    public Resource retrieve(long id, String ext) throws AccessException {
        String where = strategy.find(id);
        where = (ext != null) ? where + "." + ext : where;

        String physical = physicalLocation(where);
        try {
            ByteArray body = StreamUtil.readBytes(new File(physical));
            Resource resource = new FileResource(id, body, physical);
            resource.getHeader().ext(ext);

            return resource;
        } catch (IOException e) {
            logger.error("retrieve", e);
            throw new AccessException(e);
        }

    }

    @Override
    public boolean remove(long id, String ext) {
        String where = strategy.find(id);
        where = (ext != null) ? where + "." + ext : where;

        String physical = physicalLocation(where);

        return FileUtil.delete(physical);
    }

    @Override
    public String getWhere(long id, String ext) {
        String where = strategy.find(id);
        where = (ext != null) ? where + "." + ext : where;

        return physicalLocation(where);
    }

    protected abstract String physicalLocation(String logicalLocation);

}
