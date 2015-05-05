/**
 *
 */
package com.baidu.unbiz.common.ip;

import static com.baidu.unbiz.common.StringPool.Symbol.BACK_SLASH;
import static com.baidu.unbiz.common.StringPool.Symbol.COMMA;
import static com.baidu.unbiz.common.StringPool.Symbol.DOLLARS;
import static com.baidu.unbiz.common.StringPool.Symbol.NULL;

import com.baidu.unbiz.common.Emptys;
import com.baidu.unbiz.common.StringUtil;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 下午4:10:13
 */
public enum IpData implements IpLib {
    YAHOO("yip.dat", COMMA, COMMA) {
        @Override
        public int lineItems() {
            return 6;
        }

        @Override
        public IpEntry create(String[] items) {
            IpEntry entry = new IpEntry();

            entry.setCountry(getInfo(items[1]));
            entry.setProvince(getInfo(items[2]));
            entry.setCity(getInfo(items[3]));

            // 拼接包含省市的地址
            StringBuilder builder = new StringBuilder();
            if (CHINA.equals(entry.getCountry())) {
                if (!NULL.equals(entry.getProvince())) {
                    builder.append(entry.getProvince());
                }
                if (!NULL.equals(entry.getCity())) {
                    builder.append(entry.getCity());
                }
            }
            if (items.length == 5) {
                builder.append(getInfo(items[4]));
            }

            entry.setAddress(builder.toString());
            return entry;
        }
    },
    CZ("czip.dat", DOLLARS, BACK_SLASH + DOLLARS) {
        @Override
        public int lineItems() {
            return 3;
        }

        @Override
        public IpEntry create(String[] items) {
            IpEntry entry = new IpEntry();
            entry.setAddress(getInfo(items[1]));

            return entry;
        }
    };

    private String dataFile;

    private String split;

    private String splitRegex;

    IpData(String dataFile, String split, String splitRegex) {
        this.dataFile = dataFile;
        this.split = split;
        this.splitRegex = splitRegex;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getSplitRegex() {
        return splitRegex;
    }

    public void setSplitRegex(String splitRegex) {
        this.splitRegex = splitRegex;
    }

    public abstract int lineItems();

    public abstract IpEntry create(String[] items);

    private static String getInfo(String line) {
        String ret = Emptys.EMPTY_STRING;

        if (StringUtil.isNotEmpty(line) && !NULL.equals(line) && !CZ88.equals(line)) {
            ret = StringUtil.replace(line, CZ88, Emptys.EMPTY_STRING).trim();
        }

        return ret;
    }
}
