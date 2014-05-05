package com.printek.atm.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.printek.atm.Contract;
import com.printek.atm.R;
import com.printek.atm.items.Notes;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 15.04.2014
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class NotesDialog extends DialogFragment
{
    private GregorianCalendar calendar_Notes;
    private OnResultDialog resultDialog;
    private Notes note;

    public NotesDialog(Notes note)
    {
        super();
        this.note=note;
        calendar_Notes=note.getCalendar();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity()).setTitle(getActivity().getBaseContext().getString(R.string.dialog_title));
        View view=View.inflate(getActivity().getApplicationContext(),R.layout.add_note,null);
        TextView text_date=(TextView)view.findViewById(R.id.text_date_note);
        text_date.setText(android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext()).format(calendar_Notes.getTime())+"\n"+android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext()).format(calendar_Notes.getTimeInMillis()));
        //android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext()).format(calendar_Notes.getTime()));
        builder.setView(view);
        builder.setNegativeButton(R.string.btn_close,new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(resultDialog!=null)
                    resultDialog.onResultDialog(Contract.CANCEL,note);
            }
        });
        builder.setPositiveButton(R.string.btn_sub,new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(resultDialog!=null)
                {
                    note.setNote(((EditText)view.findViewById(R.id.new_text_note)).getText().toString());
                    resultDialog.onResultDialog(Contract.OK, note);
                }
            }
        });
        ((EditText)view.findViewById(R.id.new_text_note)).setText(note.getNote());
        return builder.create();
    }

    public void setResultDialog(OnResultDialog resultDialog)
    {
        this.resultDialog=resultDialog;
    }

    public interface OnResultDialog
    {
        public void onResultDialog(int result,Notes note);
    }
}
