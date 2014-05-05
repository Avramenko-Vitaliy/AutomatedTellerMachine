package com.printek.atm.readers;

import android.database.Cursor;
import com.printek.atm.Contract;
import com.printek.atm.DBHelper;
import com.printek.atm.adapters.DBAdapter;
import com.printek.atm.items.Notes;

import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 17.04.2014
 * Time: 9:36
 * To change this template use File | Settings | File Templates.
 */
public class ReadNotes extends ReadFromDB
{
    private String selection,orderBy;
    private String[] args;

    public ReadNotes(DBAdapter adapter, DBHelper helper, String table, String selection, String[] args, String orderBy)
    {
        super(adapter,helper,table);
        this.selection=selection;
        this.orderBy=orderBy;
        this.args=args;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        Cursor cursor=getDatabase().query(getTable(),null,selection,args,null,null,orderBy);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()&&!isCancelled())
        {
            GregorianCalendar calendar=new GregorianCalendar(cursor.getInt(cursor.getColumnIndex(Contract.YEAR)),cursor.getInt(cursor.getColumnIndex(Contract.MONTH)),cursor.getInt(cursor.getColumnIndex(Contract.DAY)));
            calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Contract.TIME)));
            publishProgress(new Notes(cursor.getInt(cursor.getColumnIndex(Contract._ID)),cursor.getInt(cursor.getColumnIndex(Contract.FOREIGN_ID_ATM)),calendar,cursor.getString(cursor.getColumnIndex(Contract.NOTES))));
            cursor.moveToNext();
            Contract.log("Position async "+cursor.getPosition());
        }
        return null;
    }
}
