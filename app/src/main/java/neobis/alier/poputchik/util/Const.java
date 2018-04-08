package neobis.alier.poputchik.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Alier on 03.04.2018.
 */

public class Const {
    public static final String URL = "http://165.227.147.84:4567/api/v1/";
    public static final String ERROR_LOAD = "Ошибка загрузки";
    public static final String CACHE_PATH = "cache";
    public static final String DIR_DRIVER = "drivers";
    public static final String DIR_RIDER = "rider";



    public static final String MAP_RESULT = "map_result";
    public static final String MAP_LOCATION = "location";

    public static final int MAP_START = 0;
    public static final int MAP_END = 1;

    public static final SimpleDateFormat format = new SimpleDateFormat("MMMM-dd-yyyy hh:mm", Locale.getDefault());
    public static final SimpleDateFormat formatSend = new SimpleDateFormat("YYYY-MM-DDThh:mm", Locale.getDefault());
}
