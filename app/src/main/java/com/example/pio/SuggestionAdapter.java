package com.example.pio;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import java.util.ArrayList;



public class SuggestionAdapter extends CursorAdapter {



    private static String TAG = SuggestionAdapter.class.getCanonicalName();



    private ArrayList<Integer> mListItems;


    public  SuggestionAdapter(Context context, Cursor c, ArrayList<Integer> items) {
        super(context, c, false);
        this.mListItems =items;

    }





    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        if(cursor.getPosition()>=0 && cursor.getPosition()<mListItems.size()){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_suggestion, parent, false);
            TextView mTextView = (TextView) view.findViewById(R.id.driver_id_option);
            Log.d(TAG,"cursor.getPosition(): "+cursor.getPosition());
            Log.d(TAG,"mListItems.size(): "+mListItems.size());
            Log.d(TAG,"mListItems.get(cursor.getPosition()).toString(): "+mListItems.get(cursor.getPosition()).toString());
            mTextView.setText(""+String.format(mListItems.get(cursor.getPosition()).toString()));
            return view;
        }
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mTextView = (TextView) view.findViewById(R.id.driver_id_option);
        mTextView.setText(""+String.format(mListItems.get(cursor.getPosition()).toString()));
    }



}
