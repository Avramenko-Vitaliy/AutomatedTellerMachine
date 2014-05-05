package com.printek.atm;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 18.03.14
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
public class Contract
{
    private final static String TAG="Project_ATM";//log тег
    public final static String _ID="_id";
    public final static String SER="ser";
    public final static String ADDRESS="address";
    public final static String HOST="host";
    public final static String INDEX="index";//поле для передачи данных между активити что бы не грузить данные с базы заново а просто обновить запись по позиции
    public final static String MANUFACTURER="manufacturer";
    public final static String MODEL="model";
    public final static String COMPANY="company";
    public final static String DATA_BASE_NAME="ATMs";//имя базы данных
    public final static String TABLE_ATMS_NAME="table_atms";//название таблици банкоматов
    public final static String TABLE_NOTES="table_notes";//название таблици для заметок
    public final static String FOREIGN_ID_ATM="id_atm";//колонка внешнего клюяча в таблице заметок
    public final static String NOTES="notes";
    public final static String YEAR="year";
    public final static String MONTH="month";
    public final static String DAY="day";
    public static final String TIME="time";//хранит время в милисекундах для того что бы отображать потом в 24 или 12 часовом формате
    public final static String STATUS_LIST="status_list";//условное обозначение для сохранения/получения статуса ListView при повороте экрана
    public final static String INDEX_NAME_NOTE_TO_ATM="notes_to_atm";//имя индекса внешнего ключа
    public static final int DB_VERSION=1;//версия базы данных
    /**
     * коды для поведения данных в программе зависимо от действий
     */
    public final static int UPDATE=0;
    public final static int INSERT=1;
    public static final int DELETE=2;
    public final static int CANCEL=3;
    public final static int OK=4;

    public static void log(String msg)
    {
        Log.i(TAG, msg);
    }
}
