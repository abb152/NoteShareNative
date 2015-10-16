package com.tilak.noteshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.ak.android.widget.colorpickerseekbar.ColorPickerSeekBar;

/**
 * Created by Wohlig on 16/10/15.
 */
public class ColorPickerDialog extends DialogFragment {

    int color_selected = 0;
    long note_id;
    ColorPickerSeekBar colorPickerSeekBar = null;

    public Dialog onCreateDialog(Bundle bundle) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.brush_chooser, null, false);
        final View v = view.findViewById(R.id.view);
        final NoteMainActivity parentActivity = (NoteMainActivity)getActivity();

        colorPickerSeekBar = (ColorPickerSeekBar) view.findViewById(R.id.colorpicker);
        colorPickerSeekBar.setOnColorSeekbarChangeListener(new ColorPickerSeekBar.OnColorSeekBarChangeListener() {
            @Override
            public void onColorChanged(SeekBar seekBar, int color, boolean b) {
                v.setBackgroundColor(color);
                color_selected = color;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)

                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String strColor = String.format("#%06X", 0xFFFFFF & color_selected);
                        //parentActivity.setColor(strColor);
                    }


                })

                        // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                    }
                }).create();
    }

}
