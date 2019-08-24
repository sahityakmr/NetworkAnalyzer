package the_anonymous_koder_2.networkanalyzer.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import the_anonymous_koder_2.networkanalyzer.AppController;
import the_anonymous_koder_2.networkanalyzer.Model.EventLog;
import the_anonymous_koder_2.networkanalyzer.Model.InfoPacket;

/**
 * Created by Sahitya on 3/25/2018.
 */

public class Database extends SQLiteOpenHelper {

    SQLiteDatabase db;
    String TAG = "Database Class";


    private static final String DATABASE = "NetworkAnalyzer";

    public static final int STORED = 1;
    public static final int QUEUED = 2;
    public static final int SENT = 3;

    public static final String NORMAL_TABLE = "normal_reports";
    public static final String CRASH_TABLE = "crash_reports";
    public static final String NORMAL_TABLE_QUEUE = "normal_reports_queue";
    public static final String CRASH_TABLE_QUEUE = "crash_reports_queue";
    public static final String LOG_TABLE = "event_logs";

    private static final String IPT_ID = "id";
    private static final String IPT_DATE_TIME = "date_time";
    private static final String IPT_LAT = "latitude";
    private static final String IPT_LON = "longitude";
    private static final String IPT_STRENGTH = "strength";
    private static final String IPT_LEVEL = "level";
    private static final String IPT_OPERATOR = "operator";
    private static final String IPT_AREA = "area";
    private static final String IPT_STATUS = "status";

    private static final String LOG_DATE_TIME = "date_time";
    private static final String LOG_TYPE = "type";
    private static final String LOG_CONTENT = "content";

    private static final String CREATE_INFO_TABLE = "CREATE TABLE `"+ NORMAL_TABLE +"`(`"
            +IPT_DATE_TIME +"`	TEXT NOT NULL,`"
            +IPT_LAT+"`	REAL NOT NULL,`"
            +IPT_LON+"`	REAL NOT NULL,`"
            +IPT_AREA +"`	TEXT NOT NULL,`"
            +IPT_OPERATOR +"`	TEXT NOT NULL,`"
            +IPT_STRENGTH+"` INTEGER NOT NULL,`"
            +IPT_LEVEL+"` INTEGER NOT NULL);";

    private static final String CREATE_CRASH_TABLE = "CREATE TABLE `"+CRASH_TABLE+"`(`"
            +IPT_DATE_TIME +"`	TEXT NOT NULL,`"
            +IPT_LAT+"`	REAL NOT NULL,`"
            +IPT_LON+"`	REAL NOT NULL,`"
            +IPT_AREA +"`	TEXT NOT NULL,`"
            +IPT_OPERATOR +"`	TEXT NOT NULL,`"
            +IPT_STRENGTH+"` INTEGER NOT NULL,`"
            +IPT_LEVEL+"` INTEGER NOT NULL);";

    private static final String CREATE_CRASH_QUEUE = "CREATE TABLE `"+ CRASH_TABLE_QUEUE +"`(`"
            +IPT_ID +"`	INTEGER NOT NULL,`"
            +IPT_DATE_TIME +"`	TEXT NOT NULL,`"
            +IPT_LAT+"`	REAL NOT NULL,`"
            +IPT_LON+"`	REAL NOT NULL,`"
            +IPT_AREA +"`	TEXT NOT NULL,`"
            +IPT_OPERATOR +"`	TEXT NOT NULL,`"
            +IPT_STRENGTH+"` INTEGER NOT NULL,`"
            +IPT_STATUS+"` INTEGER NOT NULL,`"
            +IPT_LEVEL+"` INTEGER NOT NULL);";

    private static final String CREATE_NORMAL_QUEUE = "CREATE TABLE `"+NORMAL_TABLE_QUEUE+"`(`"
            +IPT_ID +"`	INTEGER NOT NULL,`"
            +IPT_DATE_TIME +"`	TEXT NOT NULL,`"
            +IPT_LAT+"`	REAL NOT NULL,`"
            +IPT_LON+"`	REAL NOT NULL,`"
            +IPT_AREA +"`	TEXT NOT NULL,`"
            +IPT_OPERATOR +"`	TEXT NOT NULL,`"
            +IPT_STRENGTH+"` INTEGER NOT NULL,`"
            +IPT_STATUS+"` INTEGER NOT NULL,`"
            +IPT_LEVEL+"` INTEGER NOT NULL);";

    private static final String CREATE_LOG_TABLE = "CREATE TABLE `"+LOG_TABLE+"`(`"
            +LOG_DATE_TIME+"`	TEXT NOT NULL,`"
            +LOG_TYPE+"`	TEXT NOT NULL,`"
            +LOG_CONTENT+"` TEXT NOT NULL);";

    public Database() {
        super(AppController.context, DATABASE, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_INFO_TABLE);
        sqLiteDatabase.execSQL(CREATE_CRASH_TABLE);
        sqLiteDatabase.execSQL(CREATE_LOG_TABLE);
        sqLiteDatabase.execSQL(CREATE_CRASH_QUEUE);
        sqLiteDatabase.execSQL(CREATE_NORMAL_QUEUE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void open(){
        db = getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public void clearCrashReports(){
        open();
        db.execSQL("delete from "+CRASH_TABLE);
        close();
    }

    public void clearNormalReports(){
        open();
        db.execSQL("delete from "+NORMAL_TABLE);
        close();
    }

    public long updateCrashQueue(int status){
        open();
        long l = updateProcessedReports(status, CRASH_TABLE_QUEUE);
        close();
        return l;
    }
    public long updateNormalQueue(int status){
        open();
        long l = updateProcessedReports(status, NORMAL_TABLE_QUEUE);
        close();
        return l;
    }

    public long updateProcessedReports(int status, String table) {
        ContentValues cv = new ContentValues();
        cv.put(IPT_STATUS,status);
        db.update(table,cv,null,null);
        return 1;
    }

    public long insertCrashReport(InfoPacket infoPacket){
        open();
        long l = addReport(infoPacket, CRASH_TABLE);
        close();
        return l;
    }

    public long insertNormalReport(InfoPacket infoPacket){
        open();
        long l = addReport(infoPacket, NORMAL_TABLE);
        close();
        return l;
    }

    public long insertCrashQueue(InfoPacket infoPacket,int id, int status){
        open();
        long l = addProcessedReports(infoPacket, CRASH_TABLE_QUEUE, id, status);
        close();
        return l;
    }
    public long insertNormalQueue(InfoPacket infoPacket, int id, int status){
        open();
        long l = addProcessedReports(infoPacket, NORMAL_TABLE_QUEUE, id, status);
        close();
        return l;
    }

    public ArrayList<InfoPacket> fetchNormalReports(){
        return fetchReports(NORMAL_TABLE);
    }

    public ArrayList<InfoPacket> fetchCrashReports(){
        return fetchReports(CRASH_TABLE);
    }

    public ArrayList<InfoPacket> fetchNormalQueue(){
        return fetchProcessedReports(NORMAL_TABLE_QUEUE);
    }

    public ArrayList<InfoPacket> fetchCrashQueue(){
        return fetchProcessedReports(CRASH_TABLE_QUEUE);
    }

    public ArrayList<InfoPacket> fetchReports(String table) {
        open();
        ArrayList<InfoPacket> infoPackets = new ArrayList<>();
        Cursor cursor = db.query(table,null,null,null,null,null,null);
        InfoPacket infoPacket = null;
        while (cursor.moveToNext()){
            infoPacket = new InfoPacket(cursor.getString(0),cursor.getDouble(1),cursor.getDouble(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5),cursor.getInt(6));
            infoPackets.add(infoPacket);
        }
        close();
        Log.d(TAG, "fetchReports: Total "+table+" "+infoPackets.size());
        return infoPackets;
    }

    public long addReport(InfoPacket infoPacket, String table) {
        ContentValues cv = new ContentValues();
        cv.put(IPT_DATE_TIME,infoPacket.getDate_time());
        cv.put(IPT_LAT,infoPacket.getLatitude());
        cv.put(IPT_LON,infoPacket.getLongitude());
        cv.put(IPT_AREA,infoPacket.getArea());
        cv.put(IPT_OPERATOR,infoPacket.getOperator());
        cv.put(IPT_STRENGTH,infoPacket.getStrength());
        cv.put(IPT_LEVEL,infoPacket.getLevel());
        long result = db.insert(table,null,cv);
        return result;
    }

    public ArrayList<InfoPacket> fetchProcessedReports(String table) {
        open();
        ArrayList<InfoPacket> infoPackets = new ArrayList<>();
        long millis = System.currentTimeMillis()%1000;
        long millisFourHourBefore = millis - 4*60*60*1000;
        Cursor cursor = db.query(table,null,IPT_ID +" < "+millis+" and "+IPT_STATUS+" = "+QUEUED,null,null,null,null);
        InfoPacket infoPacket = null;
        while (cursor.moveToNext()){
            infoPacket = new InfoPacket(cursor.getString(0),cursor.getDouble(1),cursor.getDouble(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5),cursor.getInt(6));
            infoPackets.add(infoPacket);
        }
        cursor.close();
        Log.d(TAG, "fetchProcessedReports: Total "+infoPackets.size());
        close();
        return infoPackets;
    }

    public long addProcessedReports(InfoPacket infoPacket, String table, int id, int status) {
        open();
        ContentValues cv = new ContentValues();
        cv.put(IPT_ID,id);
        cv.put(IPT_DATE_TIME,infoPacket.getDate_time());
        cv.put(IPT_LAT,infoPacket.getLatitude());
        cv.put(IPT_LON,infoPacket.getLongitude());
        cv.put(IPT_AREA,infoPacket.getArea());
        cv.put(IPT_OPERATOR,infoPacket.getOperator());
        cv.put(IPT_STRENGTH,infoPacket.getStrength());
        cv.put(IPT_LEVEL,infoPacket.getLevel());
        cv.put(IPT_STATUS,status);
        long result = db.insert(table,null,cv);
        Log.d(TAG, "addProcessedReports: data Inserted");
        close();
        return result;
    }

    public ArrayList<EventLog> fetchLog() {
        open();
        ArrayList<EventLog> logs = new ArrayList<>();
        Cursor cursor = db.query(LOG_TABLE,null,null,null,null,null,null);
        EventLog log = null;
        while (cursor.moveToNext()){
            log = new EventLog(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            logs.add(log);
        }
        Log.d(TAG, "fetchLog: "+logs.size());
        close();
        return logs;
    }

    public long addLog(EventLog log){
        open();
        ContentValues cv = new ContentValues();
        cv.put(LOG_DATE_TIME,log.getDate_time());
        cv.put(LOG_TYPE,log.getType());
        cv.put(LOG_CONTENT,log.getContent());
        long result = db.insert(LOG_TABLE,null,cv);
        close();
        return result;
    }

    public void clearLog(){
        open();
        close();
    }

    /*public void queryGen(){


        private int signal_level_counter[4];

        Cursor operator_cursor = db.rawQuery("select distinct IPT_OPERATOR from NORMAL_TABLE",null);
        operator_cursor.moveToFirst();
        while (operator_cursor.isAfterLast() == false)
        {

            String operator_name = operator_cursor.getString(operator_cursor.getColumnIndex("COLUMN_OPERATOR"));

            Cursor longitude_cursor = db.rawQuery("select distinct IPT_LON from NORMAL_TABLE where IPT_OPERATOR = " + operator_name,null);
            longitude_cursor.moveToFirst();
            while (longitude_cursor.isAfterLast() == false)
            {
                double longitude = operator_cursor.getString(operator_cursor.getColumnIndex(IPT_));

                Cursor latitude_cursor = db.rawQuery("select distinct IPT_LAT from NORMAL_TABLE where IPT_OPERATOR = " + operato_name + " and IPT_LON = " + longitude);
                longitude_cursor.moveToFirst();
                while (longitude_cursor.isAfterLast() == false)
                {


                    double latitude = operator_cursor.getString(operator_cursor.getColumnIndex("COLUMN_LAT"));

                    Cursor signal_counter_cursor = db.rawQuery("select distinct IPT_LAT from NORMAL_TABLE where IPT_OPERATOR = " + operato_name + " and IPT_LON = " + longitude+ " and IPT_LAT = " + latitude);
                    longitude_cursor.moveToFirst();
                    while (signal_counter_cursor.isAfterLast() == false)
                    {


                        int signal_level = operator_cursor.getString(operator_cursor.getColumnIndex("COLUMN_LEVEL"));
                        signal_level_counter[signal_level-1]++;




                        signal_counter_cursor.moveToNext();
                    }





                    latitude_cursor.moveToNext();
                }




                longitude_cursor.moveToNext();
            }

            operator_cursor.moveToNext();
        }


    }*/
}
