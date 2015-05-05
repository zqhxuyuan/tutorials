/**
 * 
 */
package com.baidu.unbiz.common.ip;

/**
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 下午5:08:41
 */
public interface IpLib {

    String DATA_FILE = "cz.txt";
    String OUT_FILE = "yip.dat";
    // i18n
    String COLLEAGE_FILE = "colleage_config.properties";
    // private static final String colleageFile =
    // "colleage_config_zh.properties";
    String DATA_HEADER = "IP数据库";

    int IP_ADDRESS_SPILIT = 32;

    String CHINA = "中国";
    String PROVINCE = "省";
    String CITY = "市";
    String BEIJING = "北京";

    String UNIVERSITY = "大学";
    String COLLEGE = "学院";

    String[] MUNICIPALITIES = new String[] { "北京市", "天津市", "上海市", "重庆市" };
    String[] AUTONOMOUS_REGION = new String[] { "广西", "内蒙古", "西藏", "宁夏", "新疆", "香港", "澳门" };

    String UNKNOWN = "未知";
    // special
    String MASSACHUSETTS = "麻省";
    String CANADA = "加拿大";

    String CZ88 = "CZ88.NET";

    int ITEM_SIZE = 400000; // IP条数，既数组的大小

}
