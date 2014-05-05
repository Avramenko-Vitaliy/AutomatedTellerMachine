package com.printek.atm.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.printek.atm.*;
import com.printek.atm.activities.AddNewATM;
import com.printek.atm.items.Notes;
import com.printek.atm.activities.NotesDialog;
import com.printek.atm.readers.ReadNotes;

import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 08.04.14
 * Time: 8:33
 * To change this template use File | Settings | File Templates.
 */
public class NotesAdapter extends DBAdapter
{
    private AddNewATM mParent;
    private ReadNotes readNotes;

    public NotesAdapter(Context context, AddNewATM activity)
    {
        super(context);
        mParent=activity;
    }

    @Override
    public void refresh()
    {
        clear();
        readNotes=new ReadNotes(this,getDB(),Contract.TABLE_NOTES,Contract.FOREIGN_ID_ATM+"=?",new String[]{""+mParent.getID()},Contract.YEAR+" desc,"+Contract.MONTH+" desc,"+Contract.DAY+" desc,"+Contract.TIME+" desc");
        readNotes.execute();
    }

    public void cancel()
    {
        if(readNotes!=null&&(readNotes.getStatus()==AsyncTask.Status.RUNNING||readNotes.getStatus()==AsyncTask.Status.PENDING))
            readNotes.cancel(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Notes note=(Notes)getItem(position);
        LinearLayout layout=(LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_notes,null).findViewById(R.id.layout_notes);
        TextView date_time_text=(TextView)layout.findViewById(R.id.text_date);
        date_time_text.setText(android.text.format.DateFormat.getDateFormat(getContext()).format(note.getCalendar().getTime())+"\n"+android.text.format.DateFormat.getTimeFormat(getContext()).format(note.getCalendar().getTimeInMillis()));
        TextView text_note=(TextView)layout.findViewById(R.id.text_notes);
        text_note.setText(note.getNote().toString());
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NotesDialog notesDialog=new NotesDialog(note);
                notesDialog.setResultDialog(new NotesDialog.OnResultDialog()
                {
                    @Override
                    public void onResultDialog(int result, Notes note)
                    {
                        if(result==Contract.OK)
                            update(note, Contract.TABLE_NOTES, setContentValues(note));
                    }
                });
                notesDialog.show(mParent.getFragmentManager(),Contract.NOTES);
            }
        });
        layout.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                mParent.openContextMenu(layout);
                return true;
            }
        });
        return layout;
    }

    @Override
    public void startProgress()
    {
        mParent.findViewById(R.id.notes_progress).setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgress()
    {
        mParent.findViewById(R.id.notes_progress).setVisibility(View.INVISIBLE);
    }

    public ContentValues setContentValues(Notes note)
    {
        ContentValues values=new ContentValues();
        values.put(Contract.FOREIGN_ID_ATM,note.getId_ATM());
        values.put(Contract.NOTES,note.getNote());
        values.put(Contract.YEAR,note.getYear());
        values.put(Contract.MONTH,note.getMonth());
        values.put(Contract.DAY,note.getDay());
        values.put(Contract.TIME,note.getCalendar().getTimeInMillis());
        return values;
    }

    public void setParent(AddNewATM mParent)
    {
        this.mParent=mParent;
    }
}
