package com.printek.atm.readers;

import android.database.Cursor;
import com.printek.atm.Contract;
import com.printek.atm.DBHelper;
import com.printek.atm.adapters.DBAdapter;
import com.printek.atm.items.AutomatedTellerMachine;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 17.04.2014
 * Time: 9:36
 * To change this template use File | Settings | File Templates.
 */
public class ReadATMs extends ReadFromDB
{
    public ReadATMs(DBAdapter adapter, DBHelper helper, String table)
    {
        super(adapter, helper, table);
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        Cursor cursor=getDatabase().query(getTable(),null,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()&&!isCancelled())
        {
                publishProgress(new AutomatedTellerMachine(cursor.getInt(cursor.getColumnIndex(Contract._ID)),
                        cursor.getString(cursor.getColumnIndex(Contract.COMPANY)),
                        cursor.getString(cursor.getColumnIndex(Contract.MODEL)),
                        cursor.getString(cursor.getColumnIndex(Contract.MANUFACTURER)),
                        cursor.getString(cursor.getColumnIndex(Contract.ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(Contract.SER)),
                        cursor.getString(cursor.getColumnIndex(Contract.HOST)),
                        cursor.getInt(cursor.getColumnIndex(Contract.YEAR)),
                        cursor.getInt(cursor.getColumnIndex(Contract.MONTH)),
                        cursor.getInt(cursor.getColumnIndex(Contract.DAY))));
            cursor.moveToNext();
            Contract.log("Position async "+cursor.getPosition());
        }
        return null;
    }
}
