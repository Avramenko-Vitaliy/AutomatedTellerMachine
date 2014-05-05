package com.printek.atm;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 21.03.14
 * Time: 8:57
 * To change this template use File | Settings | File Templates.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private Context context;
    private static String DB_PATH;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.context=context;
        DB_PATH="//data/data//"+context.getPackageName()+"//databases//";
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /*Contract.log("Create data base "+Contract.DATA_BASE_NAME);
        db.execSQL("create table "+Contract.TABLE_ATMS_NAME+" ("+Contract._ID+" integer primary key autoincrement,"
                +Contract.SER+" text,"
                +Contract.HOST+" text,"
                +Contract.DAY+" integer,"
                +Contract.MONTH+" integer,"
                +Contract.YEAR+" integer,"
                +Contract.ADDRESS+" text,"
                +Contract.COMPANY+" text,"
                +Contract.MANUFACTURER+" text,"
                +Contract.MODEL+" text);");
        db.execSQL("create table "+Contract.TABLE_NOTES+"("+Contract._ID+" integer primary key autoincrement,"+
                Contract.FOREIGN_ID_ATM+" integer,"+
                Contract.DAY+" integer,"+
                Contract.MONTH+" integer,"+
                Contract.YEAR+" integer,"+
                Contract.TIME+" text,"+
                Contract.NOTES+" text,constraint "+Contract.INDEX_NAME_NOTE_TO_ATM+" foreign key("+Contract.FOREIGN_ID_ATM+")references "+Contract.TABLE_ATMS_NAME+"("+Contract._ID+")on delete cascade);");
        db.execSQL("CREATE INDEX DAY_MONTH_YEAR_TIME_INDEX on TABLE_NOTES("+Contract.DAY+","+Contract.MONTH+","+Contract.YEAR+","+Contract.TIME+")");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     */
    public void createDataBase() throws IOException
    {
        if(!checkDataBase())
        {
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();
            try
            {
                copyDataBase();
            }
            catch(IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     *
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase()
    {
        File dbFile=new File(DB_PATH+Contract.DATA_BASE_NAME);
        return dbFile.exists();
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     */
    private void copyDataBase() throws IOException
    {
        //Открываем локальную БД как входящий поток
        InputStream myInput=context.getAssets().open(Contract.DATA_BASE_NAME);
        //Путь ко вновь созданной БД
        String outFileName=DB_PATH+Contract.DATA_BASE_NAME;
        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput=new FileOutputStream(outFileName);
        //перемещаем байты из входящего файла в исходящий
        byte[] buffer=new byte[1024];
        int length;
        while((length=myInput.read(buffer))>0)
            myOutput.write(buffer, 0, length);
        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
}