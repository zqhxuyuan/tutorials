package com.baidu.unbiz.common.division;

import static com.baidu.unbiz.common.StringPool.Symbol.COMMA;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.baidu.unbiz.common.ArrayUtil;
import com.baidu.unbiz.common.CollectionUtil;
import com.baidu.unbiz.common.ExceptionUtil;
import com.baidu.unbiz.common.StringUtil;
import com.baidu.unbiz.common.i18n.GBKMap;
import com.baidu.unbiz.common.io.ReaderUtil;
import com.baidu.unbiz.common.logger.CachedLogger;
import com.baidu.unbiz.common.logger.Logger;
import com.baidu.unbiz.common.logger.LoggerFactory;

/**
 * 中国省份城市实现
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年9月16日 上午4:01:49
 */
public class ChinaDivisionManager extends CachedLogger implements DivisionManager {

    private static final Logger logger = LoggerFactory.getLogger(ChinaDivisionManager.class);

    private static final ChinaDivisionManager chinaDivisionManager = new ChinaDivisionManager();

    private static final int LINE_ITEMS = 6;

    /** 部定义编号对应的对象 */
    private final Map<Integer, ChinaDivision> idMap = CollectionUtil.createHashMap();
    /** 国标缩写对应的对象 */
    private final Map<String, List<Division>> abbNameMap = CollectionUtil.createHashMap();
    /** 名字对应的对象 */
    private final Map<String, List<Division>> nameMap = CollectionUtil.createHashMap();
    /** 邮编对应的对象 */
    private final Map<String, List<Division>> zipMap = CollectionUtil.createHashMap();

    private final String divisionData = "chinaDivision.dat";

    private boolean init;

    static {
        try {
            chinaDivisionManager.init();
        } catch (DivisionException e) {
            logger.error("clinit", e);
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    private ChinaDivisionManager() {

    }

    public static DivisionManager getInstance() {
        return chinaDivisionManager;
    }

    public void init() throws DivisionException {
        try {
            if (!init) {
                initDivision();
            }

        } catch (DivisionException e) {
            logger.error("init error", e);
        }
    }

    public void reloadDivision() throws DivisionException {
        destoryDivision();
        initDivision();
    }

    private List<String> loadRowData() {

        try {
            return ReaderUtil.readLinesAndClose(this.getClass().getResourceAsStream(divisionData));
        } catch (IOException e) {
            logger.error("load division Data error", e);
            throw ExceptionUtil.toRuntimeException(e);
        }
    }

    private ChinaDivision create(String[] strings) throws Exception {
        ChinaDivision chinaDivision = new ChinaDivision();
        chinaDivision.setDivisionId(Integer.valueOf(strings[0]));
        chinaDivision.setDivisionName(strings[1]);
        chinaDivision.setDivisionAbbName(strings[2]);
        chinaDivision.setDivisionTname(GBKMap.covGBKS2T(chinaDivision.getDivisionName()));
        if (chinaDivision.getDivisionId() == ChinaDivision.DEFAULT_ALL_DIVISION) {
            return chinaDivision;
        }
        if (strings[3] != null && StringUtil.isNotBlank(strings[4])) {
            ChinaDivision parent = new ChinaDivision();
            parent.setDivisionId(Integer.valueOf(strings[4]));
            chinaDivision.setParentDivision(parent);
        }
        if (strings.length > 5) {
            chinaDivision.setDivisionZip(strings[5]);
        } else {
            chinaDivision.setDivisionZip(strings[0]);
        }

        return chinaDivision;
    }

    private void transformData(List<String> lines) {
        for (String line : lines) {
            // 文件格式：内部编号,中文名称,上级编号,邮编,
            String[] strings = StringUtil.split(line, COMMA, LINE_ITEMS);
            if (ArrayUtil.isEmpty(strings)) {
                continue;
            }

            ChinaDivision chinaDivision = null;
            try {
                chinaDivision = create(strings);
            } catch (Exception e) {
                logger.error("transformData error ", e, strings[0], strings[1], strings[2]);
                continue;
            }

            putIdMap(chinaDivision);

        }
    }

    private synchronized void initDivision() throws DivisionException {
        if (!init) {
            long start = System.currentTimeMillis();

            init = true;
            List<String> lines = loadRowData();
            transformData(lines);

            initMaps();
            init = true;
            long end = System.currentTimeMillis();

            logger.infoIfEnabled("load china division finished. have {}records, spend {}ms.", lines.size(),
                    (end - start));
        }
    }

    private void initMaps() {
        initIdMap();
        initAbbNameMap();
        initNameMap();
        initZipMap();
    }

    private synchronized void destoryDivision() {
        if (init) {
            idMap.clear();
            abbNameMap.clear();
            nameMap.clear();
            zipMap.clear();
            init = false;
        }
    }

    /**
     * 初始化key=id,value=ChinaDivisionVO
     */
    private void putIdMap(ChinaDivision cdVO) {
        if (cdVO != null) {
            idMap.put(Integer.valueOf(cdVO.getDivisionId()), cdVO);
        }

    }

    /**
     * 重组idMap,设置上下级关系
     */
    private void initIdMap() {
        for (Map.Entry<Integer, ChinaDivision> entry : idMap.entrySet()) {
            ChinaDivision chinaDivision = entry.getValue();
            if (chinaDivision != null && chinaDivision.getParentDivision() != null) {
                ChinaDivision parent = idMap.get(chinaDivision.getParentDivision().getDivisionId());
                if (parent == null) {
                    continue;
                }

                List<Division> childList = parent.getChildDivision();
                if (childList == null) {
                    childList = CollectionUtil.createArrayList();
                }

                childList.add(chinaDivision);
                Collections.sort(childList);
                parent.setChildDivision(childList);
                chinaDivision.setParentDivision(parent);
            }
        }

    }

    /**
     * 初始化key=name,value=List<ChinaDivisionVO>
     */
    private void initNameMap() {
        for (Map.Entry<Integer, ChinaDivision> entry : idMap.entrySet()) {
            ChinaDivision chinaDivision = entry.getValue();
            if (chinaDivision == null) {
                continue;
            }

            List<Division> divisionList = nameMap.get(chinaDivision.getDivisionName());
            if (divisionList == null) {
                divisionList = CollectionUtil.createArrayList();
            }
            divisionList.add(chinaDivision);
            nameMap.put(chinaDivision.getDivisionName(), divisionList);

        }

    }

    /**
     * 初始化key=abbName,value=List<ChinaDivisionVO>
     */
    private void initAbbNameMap() {
        for (Map.Entry<Integer, ChinaDivision> entry : idMap.entrySet()) {
            ChinaDivision chinaDivision = entry.getValue();
            if (chinaDivision == null) {
                continue;
            }

            List<Division> divisionList = abbNameMap.get(chinaDivision.getDivisionAbbName());
            if (divisionList == null) {
                divisionList = CollectionUtil.createArrayList();
            }
            divisionList.add(chinaDivision);
            abbNameMap.put(chinaDivision.getDivisionAbbName(), divisionList);
        }

    }

    /**
     * 初始化key=zip,value=ChinaDivisionVO
     * 
     * @param idMap
     */
    private void initZipMap() {
        for (Map.Entry<Integer, ChinaDivision> entry : idMap.entrySet()) {
            ChinaDivision chinaDivision = entry.getValue();
            if (chinaDivision == null) {
                continue;
            }

            List<Division> divisionList = zipMap.get(chinaDivision.getDivisionZip());
            if (divisionList == null) {
                divisionList = CollectionUtil.createArrayList();
            }
            divisionList.add(chinaDivision);
            zipMap.put(chinaDivision.getDivisionZip(), divisionList);

        }

    }

    public ChinaDivision getDivisionById(int id) {
        return idMap.get(id);
    }

    public List<Division> getDivisionByName(String name) {
        return nameMap.get(name);
    }

    public List<Division> getObscureDivisionByName(String name) {
        List<Division> result = CollectionUtil.createArrayList();
        for (Map.Entry<String, List<Division>> entry : nameMap.entrySet()) {
            String divisionName = entry.getKey();
            if (StringUtil.contains(divisionName, name)) {
                result.addAll(nameMap.get(divisionName));
            }
        }

        Collections.sort(result);
        return result;
    }

    public List<Division> getDivisionByAbbName(String abbName) {
        return abbNameMap.get(abbName);
    }

    public List<Division> getDivisionByZip(String zip) {
        return zipMap.get(zip);
    }

    public List<Division> getObscureDivisionByTName(String tName) {
        String name = GBKMap.covGBKT2S(tName);
        return this.getObscureDivisionByName(name);
    }

    public List<Division> getDivisionByTName(String tName) {
        String name = GBKMap.covGBKT2S(tName);
        return this.getDivisionByName(name);
    }

    public List<Division> getProvinceDisivion() {
        ChinaDivision chinaDivision = this.getDivisionById(ChinaDivision.DEFAULT_ALL_DIVISION);

        if (chinaDivision != null) {
            return chinaDivision.getChildDivision();
        }

        return null;
    }

    public boolean isCityDivision(int id) {
        ChinaDivision chinaDivision = getDivisionById(id);

        if ((chinaDivision != null)
                && (chinaDivision.getParentDivision() != null)
                && (chinaDivision.getParentDivision().getParentDivision() != null)
                && (chinaDivision.getParentDivision().getParentDivision().getDivisionId() == ChinaDivision.DEFAULT_ALL_DIVISION)) {
            return true;
        }

        return false;
    }

    public boolean isProvinceDivision(int id) {
        ChinaDivision chinaDivision = getDivisionById(id);

        if ((chinaDivision != null) && (chinaDivision.getParentDivision() != null)
                && (chinaDivision.getParentDivision().getDivisionId() == ChinaDivision.DEFAULT_ALL_DIVISION)) {
            return true;
        }

        return false;
    }

    public boolean isRegionDivision(int id) {
        ChinaDivision chinaDivision = getDivisionById(id);

        if ((chinaDivision != null)
                && (chinaDivision.getParentDivision() != null)
                && (chinaDivision.getParentDivision().getParentDivision() != null)
                && (chinaDivision.getParentDivision().getParentDivision().getParentDivision() != null)
                && (chinaDivision.getParentDivision().getParentDivision().getParentDivision().getDivisionId() == ChinaDivision.DEFAULT_ALL_DIVISION)) {
            return true;
        }

        return false;
    }

    public boolean isCityNameDivision(String cityName) {
        List<Division> cityList = getDivisionByName(cityName);
        if (CollectionUtil.isEmpty(cityList)) {
            return false;
        }

        for (Division division : cityList) {
            if (isCityDivision(division.getDivisionId())) {
                return true;
            }
        }

        return false;
    }

    public boolean isProvinceNameDivision(String provName) {
        List<Division> provinceList = getDivisionByName(provName);

        for (Division division : provinceList) {
            if (isProvinceDivision(division.getDivisionId())) {
                return true;
            }
        }

        return false;
    }

    public boolean isRegionNameDivision(String regionName) {
        List<Division> regionList = getDivisionByName(regionName);
        if (CollectionUtil.isEmpty(regionList)) {
            return false;
        }

        for (Division division : regionList) {
            if (isRegionDivision(division.getDivisionId())) {
                return true;
            }
        }

        return false;
    }

}
