package com.printek.atm.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.printek.atm.Contract;
import com.printek.atm.DBHelper;
import com.printek.atm.R;
import com.printek.atm.items.ListInformation;
import com.printek.atm.readers.ReadFromDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 08.04.14
 * Time: 9:41
 * To change this template use File | Settings | File Templates.
 */
public abstract class DBAdapter extends BaseAdapter
{
    private List<ListInformation>list =new ArrayList<>();
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Context mContext;

    public DBAdapter(Context context)
    {
        mContext=context;
        dbHelper=new DBHelper(mContext, Contract.DATA_BASE_NAME,null,Contract.DB_VERSION);
        try
        {
            dbHelper.createDataBase();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Insert в базу данных
     *
     * @param item обьект который добавиться в список данных
     * @param table_name имя таблици в которую будет сделан insert
     * @param values запакованые данные об обьекте
     * @return возвращает id обьекта в таблице
     */
    public int insert(ListInformation item, String table_name,ContentValues values)
    {
        try
        {
            startTransaction();
            item.set_id((int) database.insert(table_name, null, values));
            Contract.log("_id new record equals "+item.get_id());
            add(item);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //сообщаем об ошибке
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.err_insert),Toast.LENGTH_LONG).show();
        }
        finally
        {
            endTransaction();
            notifyDataSetChanged();
        }
        return item.get_id();
    }

    /**
     * метод получения списка id обьктов которые были отмечены
     *
     * @param array список статуса обьектов (отмечен, не отмечен)
     * @return возвращает строку в которой записаны id отмеченых обьктов, которые разделены ","
     */
    private synchronized String getChooseIDs(SparseBooleanArray array)
    {
        String ids="";
        List<ListInformation> tmpList=new ArrayList<>();
        tmpList.addAll(list);
        for(ListInformation item : tmpList)
        {
            if(array.get(item.get_id()))
            {
                ids+=","+item.get_id();
                array.delete(item.get_id());
                list.remove(item);
            }
        }
        Contract.log("IDs for delete "+(ids.substring(1,ids.length())));
        return ids.substring(1,ids.length());
    }


    /**
     * удаляем несколько отмеченых данных
     *
     * @param table_name имя таблици с которой будут удалены записи
     * @param array список статуса обьектов (отмечен, не отмечен)
     */
    public void deleteSomeItems(String table_name, SparseBooleanArray array)
    {
        if(array.indexOfValue(true)!=-1)
        {
            try
            {
                startTransaction();
                database.delete(table_name, Contract._ID+" in ("+getChooseIDs(array)+")",null);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                //оповищаем об ошибки при удалении
                Toast.makeText(getContext(),getContext().getResources().getString(R.string.err_delete),Toast.LENGTH_LONG).show();
            }
            finally
            {
                endTransaction();
                notifyDataSetChanged();
            }
        }
    }

    public void setContext(Context context)
    {
        mContext=context;
    }

    /**
     * удаление одной записи в таблице
     *
     * @param table_name имя таблици с которой будет удалена запись
     * @param id строки
     */
    public void delete(String table_name,long id)
    {
        try
        {
            startTransaction();
            database.delete(table_name, Contract._ID+"=?", new String[]{""+id});
            List<ListInformation> tmpList=new ArrayList<>();
            tmpList.addAll(list);
            for(ListInformation item:tmpList)
                if(item.get_id()==id)
                    list.remove(item);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.err_delete),Toast.LENGTH_LONG).show();
        }
        finally
        {
            endTransaction();
            notifyDataSetChanged();
        }
    }


    /**
     * обновление данных в базе
     *
     * @param item для получения id
     * @param table_name имя таблици в которой обновляем данные
     * @param values запакованые данные об обьекте
     */
    public void update(ListInformation item,String table_name,ContentValues values)
    {
        Contract.log("_id ATM="+item.get_id());
        if(item.get_id()!=-1)
        {
            try
            {
                startTransaction();
                database.update(table_name, values, Contract._ID+"=?", new String[]{""+item.get_id()});
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getContext(),getContext().getResources().getString(R.string.err_update),Toast.LENGTH_LONG).show();
            }
            finally
            {
                endTransaction();
                notifyDataSetChanged();
            }
        }
    }

    /**
     * абстрактный метод для реализации своего чтения с базы
     */
    public abstract void refresh();

    public void add(ListInformation item)
    {
        list.add(item);
        notifyDataSetChanged();
    }

    private void endTransaction()
    {
        try
        {
            database.setTransactionSuccessful();
            database.endTransaction();
        }
        finally
        {
            dbHelper.close();
        }
    }

    private void startTransaction()
    {
        database=dbHelper.getReadableDatabase();
        database.beginTransaction();
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public ListInformation getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return list.get(position).get_id();
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public abstract void startProgress();

    public abstract void stopProgress();

    public Context getContext()
    {
        return mContext;
    }

    public List<ListInformation> getList()
    {
        return list;
    }

    public void clear()
    {
        list.clear();
    }

    protected DBHelper getDB()
    {
        return dbHelper;
    }
}
