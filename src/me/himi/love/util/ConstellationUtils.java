package me.himi.love.util;

/**
 * @ClassName:ConstellactionUtils
 * @author sparklee liduanwei_911@163.com
 * @date Nov 8, 2014 11:18:25 AM
 */
public class ConstellationUtils {
    public static enum Constellation {
	BAI_YANG, JING_NIU, SHUANG_ZI, JU_XIE, SHI_ZI, CHU_NV, TIAN_CHENG, TIAN_XIE, SHE_SHOU, MO_XIE, SHUI_PING, SHUANG_YU
    }

    public static Constellation get(int month, int day) {
	Constellation constellaction = Constellation.SHUI_PING;
	switch (month) {
	case 1:
	    if (day >= 20) {
		constellaction = Constellation.SHUI_PING;
	    } else {
		constellaction = Constellation.MO_XIE;
	    }
	    break;
	case 2:
	    if (day >= 18) {
		constellaction = Constellation.SHUI_PING;
	    } else {
		constellaction = Constellation.SHUANG_YU;
	    }
	    break;
	case 3:
	    if (day >= 21) {
		constellaction = Constellation.BAI_YANG;
	    } else {
		constellaction = Constellation.SHUANG_YU;
	    }
	    break;
	case 4:
	    if (day >= 20) {
		constellaction = Constellation.JING_NIU;
	    } else {
		constellaction = Constellation.BAI_YANG;
	    }
	    break;
	case 5:
	    if (day >= 21) {
		constellaction = Constellation.SHUANG_ZI;
	    } else {
		constellaction = Constellation.JING_NIU;
	    }
	    break;
	case 6:
	    if (day >= 22) {
		constellaction = Constellation.JU_XIE;
	    } else {
		constellaction = Constellation.SHUANG_ZI;
	    }
	    break;
	case 7:
	    if (day >= 23) {
		constellaction = Constellation.SHI_ZI;
	    } else {
		constellaction = Constellation.JU_XIE;
	    }
	    break;
	case 8:
	    if (day >= 23) {
		constellaction = Constellation.CHU_NV;
	    } else {
		constellaction = Constellation.SHI_ZI;
	    }
	    break;
	case 9:
	    if (day >= 23) {
		constellaction = Constellation.TIAN_CHENG;
	    } else {
		constellaction = Constellation.CHU_NV;
	    }
	    break;
	case 10:
	    if (day >= 24) {
		constellaction = Constellation.TIAN_XIE;
	    } else {
		constellaction = Constellation.TIAN_CHENG;
	    }
	    break;
	case 11:
	    if (day >= 23) {
		constellaction = Constellation.SHE_SHOU;
	    } else {
		constellaction = Constellation.TIAN_XIE;
	    }
	    break;
	case 12:
	    if (day >= 22) {
		constellaction = Constellation.MO_XIE;
	    } else {
		constellaction = Constellation.SHE_SHOU;
	    }
	    break;
	}
	return constellaction;
    }

    public static String getName(Constellation constellation) {
	switch (constellation) {
	case BAI_YANG:
	    return "白羊";

	case JING_NIU:
	    return "金牛";

	case SHUANG_ZI:
	    return "双子";

	case JU_XIE:
	    return "巨蟹";

	case SHI_ZI:
	    return "狮子";

	case CHU_NV:
	    return "处女";

	case TIAN_CHENG:
	    return "天秤";

	case TIAN_XIE:
	    return "天蝎";

	case SHE_SHOU:
	    return "射手";

	case MO_XIE:
	    return "魔蝎";
	case SHUI_PING:
	    return "水瓶";
	case SHUANG_YU:
	    return "双鱼";
	default:
	    return "";
	}
    }

    public static ConstellationDateRange getConstellationDateRange(Constellation constellation) {
	ConstellationDateRange dateRange = null;
	switch (constellation) {
	case BAI_YANG:
	    dateRange = new ConstellationDateRange(new ConsDate(3, 21), new ConsDate(4, 19));
	    break;
	case JING_NIU:
	    dateRange = new ConstellationDateRange(new ConsDate(4, 20), new ConsDate(5, 20));
	    break;
	case SHUANG_ZI:
	    dateRange = new ConstellationDateRange(new ConsDate(5, 21), new ConsDate(6, 21));
	    break;
	case JU_XIE:
	    dateRange = new ConstellationDateRange(new ConsDate(6, 22), new ConsDate(7, 22));
	    break;
	case SHI_ZI:
	    dateRange = new ConstellationDateRange(new ConsDate(7, 23), new ConsDate(8, 22));
	    break;
	case CHU_NV:
	    dateRange = new ConstellationDateRange(new ConsDate(8, 23), new ConsDate(9, 22));
	    break;
	case TIAN_CHENG:
	    dateRange = new ConstellationDateRange(new ConsDate(9, 23), new ConsDate(10, 23));
	    break;
	case TIAN_XIE:
	    dateRange = new ConstellationDateRange(new ConsDate(10, 24), new ConsDate(11, 22));
	    break;
	case SHE_SHOU:
	    dateRange = new ConstellationDateRange(new ConsDate(11, 23), new ConsDate(12, 21));
	    break;
	case MO_XIE:
	    dateRange = new ConstellationDateRange(new ConsDate(12, 22), new ConsDate(1, 19));
	    break;
	case SHUI_PING:
	    dateRange = new ConstellationDateRange(new ConsDate(1, 20), new ConsDate(2, 18));
	    break;
	}
	return dateRange;
    }

    public static class ConstellationDateRange {
	public ConsDate beginDate, endDate;

	public ConstellationDateRange(ConsDate beginDate, ConsDate endDate) {
	    this.beginDate = beginDate;
	    this.endDate = endDate;
	}
    }

    public static class ConsDate {
	public int month, day;

	public ConsDate(int month, int day) {
	    this.month = month;
	    this.day = day;
	}
    }
}
