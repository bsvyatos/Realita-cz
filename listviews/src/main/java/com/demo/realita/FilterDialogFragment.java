package com.demo.realita;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Svyatoslav on 27-Apr-15.
 */
public class FilterDialogFragment extends DialogFragment{
    private String mTitle;
    private String mPositive;
    private String mNegative;
    private NoticeDialogListener mListener;
    private LayoutInflater inflater;
    private int mDialogState;

    public static FilterDialogFragment newInstance(String title, String pos, String neg, int mDialogState) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("pos", pos);
        args.putString("neg", neg);
        args.putInt("mState", mDialogState);
        frag.setArguments(args);
        return frag;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try{
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch(ClassCastException e){
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mTitle = getArguments().getString("title");
        mPositive = getArguments().getString("pos");
        mNegative = getArguments().getString("neg");
        mDialogState = getArguments().getInt("mState");
        inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.filter_dialog, null);
        final EditText mNum = (EditText) v.findViewById(R.id.filter_set_text);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setView(v)
                .setTitle(mTitle)
                .setPositiveButton(mPositive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(FilterDialogFragment.this, mNum.getText().toString(), mDialogState);
                    }
                })
                .setNegativeButton(mNegative, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        mListener.onDialogNegativeClick(FilterDialogFragment.this);
                    }
                });


        return mBuilder.create();
    }

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String uInp, int State);
        void onDialogNegativeClick(DialogFragment dialog);
    }
    public void SetText(String Message, String Positive, String Negative){
        this.mTitle = Message;
        this.mPositive = Positive;
        this.mNegative = Negative;
    }

}
