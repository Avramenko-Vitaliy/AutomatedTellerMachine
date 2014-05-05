package com.printek.atm.readers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import com.printek.atm.DBHelper;
import com.printek.atm.adapters.DBAdapter;
import com.printek.atm.items.ListInformation;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 21.03.14
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
public abstract class ReadFromDB extends AsyncTask<Void,ListInformation,Void>
{
    /**
     * родительский абстрактный класс для читателей базы данных
     * в onPreExecute() подготавливаем базу для чтения открывая транзакцию
     * в onPostExecute() закрываем транзакцию
     * onProgressUpdate - добавляем полученые данные
     * doInBackground оставляем абстрактным для того что бы каждый читатель смог реализовать свою логику чтения
     */

    private DBAdapter mAdapter;
    private DBHelper helper;
    private SQLiteDatabase database;
    private String table;

    public ReadFromDB(DBAdapter adapter,DBHelper helper,String table)
    {
        mAdapter=adapter;
        this.helper=helper;
        this.table=table;
    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            database=helper.getReadableDatabase();
            mAdapter.startProgress();
        }
        catch(SQLiteException e)
        {
            e.printStackTrace();
            cancel(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected abstract Void doInBackground(Void... params);

    @Override
    protected void onProgressUpdate(ListInformation... values)
    {
        super.onProgressUpdate(values);
        mAdapter.startProgress();
        mAdapter.add(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        helper.close();
        mAdapter.stopProgress();
    }

    protected String getTable()
    {
        return table;
    }

    protected SQLiteDatabase getDatabase()
    {
        return database;
    }
}
