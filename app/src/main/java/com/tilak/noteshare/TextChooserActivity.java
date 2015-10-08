package com.tilak.noteshare;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TextChooserActivity extends Activity implements OnClickListener {

	public EditText edittextNoteAdd;
	public TextView textNoteViewer;
	public SpannableString spanUpdted, spanold;
	public RelativeLayout textstyledialogmain;
	public Button btnCooseStyle;
	public LayoutInflater inflater;
	public View vi;
	public ListView listviewoption;
	public TextChooserAdapter adapter;
	public Button btnUnderLine;
	public Button btnTypeface;
	public Button btncolor;

	public ArrayList<String> arrSpanedString;
	public String arrcolors[], arrtypeface[];
	int selectedButton;
	boolean isunderLine = false;
	public int textcolor, textbgColor;
	public int typefacae;
	public Dialog brushDialogtextStyle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textchooser_activity);
		edittextNoteAdd = (EditText) findViewById(R.id.editTextNoteadd);
		btnUnderLine = (Button) findViewById(R.id.btnUnderLine);
		textNoteViewer = (TextView) findViewById(R.id.textViewNoteViewer);
		textstyledialogmain = (RelativeLayout) findViewById(R.id.textstyledialogmain);
		arrSpanedString = new ArrayList<String>();

		arrcolors = getResources().getStringArray(R.array.colors);
		arrtypeface = getResources().getStringArray(R.array.Typeface);

		inilitze();

		btnCooseStyle = (Button) findViewById(R.id.btnCooseStyle);
		btnCooseStyle.setOnClickListener(this);

		selectedButton = R.id.buttoncolor;
		textbgColor = Color.WHITE;
		textcolor = Color.RED;
		typefacae = Typeface.NORMAL;

		btnUnderLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if (isunderLine == true) 
				{
					isunderLine = false;
					btnUnderLine.setBackgroundColor(Color.WHITE);
				} else 
				{
					btnUnderLine.setBackgroundColor(getResources().getColor(
							R.color.header_bg));

					isunderLine = true;
				}

			}
		});

		edittextNoteAdd.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub

				System.out
						.println("typed:" + arg0.getEditableText().toString());
				updatedText(isunderLine, textcolor, Color.TRANSPARENT, arg0
						.getEditableText().toString());
				edittextNoteAdd.setText("");
				return false;
			}
		});

	}

	void inilitze() {

		arrSpanedString.clear();

		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi = inflater.inflate(R.layout.text_theam_file, null);
		btncolor = (Button) vi.findViewById(R.id.buttoncolor);
		btncolor.setOnClickListener(this);
		Button btnUnderline = (Button) vi.findViewById(R.id.buttonunderline);
		btnUnderline.setOnClickListener(this);
		btnTypeface = (Button) vi.findViewById(R.id.buttontypeface);
		btnTypeface.setOnClickListener(this);
		listviewoption = (ListView) vi.findViewById(R.id.ListViewItems);
		for (int i = 0; i < arrcolors.length; i++) {
			arrSpanedString.add(arrcolors[i]);
		}

		adapter = new TextChooserAdapter(this, null, arrSpanedString);
		listviewoption.setAdapter(adapter);
		listviewoption.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				System.out.println("the index clicked:" + arg2);

				if (selectedButton == R.id.buttoncolor) {

					if (arrcolors[arg2].equalsIgnoreCase("RED")) {
						textcolor = Color.RED;
					} else if (arrcolors[arg2].equalsIgnoreCase("GREEN")) {
						textcolor = Color.GREEN;

					} else if (arrcolors[arg2].equalsIgnoreCase("CYAN")) {
						textcolor = Color.CYAN;
					} else if (arrcolors[arg2].equalsIgnoreCase("BLUE")) {
						textcolor = Color.BLUE;
					} else if (arrcolors[arg2].equalsIgnoreCase("BLACK")) {
						textcolor = Color.BLACK;
					}

				} else if (selectedButton == R.id.buttontypeface) {

					if (arrtypeface[arg2].equalsIgnoreCase("BOLD")) {
						typefacae = Typeface.BOLD;
					} else if (arrtypeface[arg2].equalsIgnoreCase("NORMAL")) {
						typefacae = Typeface.NORMAL;

					} else if (arrtypeface[arg2].equalsIgnoreCase("ITALIC")) {
						typefacae = Typeface.ITALIC;
					}
				}
				brushDialogtextStyle.dismiss();

			}
		});
	}

	void updatedText(boolean b, int textcolor, int textbgxcolor, String text) {
		if (text.length() > 0) 
		{

			spanUpdted = new SpannableString(text);

			spanUpdted.setSpan(new StyleSpan(typefacae), 0,
					spanUpdted.length(), 0);
			if (b) {
				spanUpdted.setSpan(new UnderlineSpan(), 0, spanUpdted.length(),
						0);
			}
			// if (textcolor > 0)
			{
				spanUpdted.setSpan(new BackgroundColorSpan(textbgxcolor), 0,
						spanUpdted.length(), 0);
			}
			// if (textbgxcolor > 0)
			{
				spanUpdted.setSpan(new ForegroundColorSpan(textcolor), 0,
						spanUpdted.length(), 0);
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (textNoteViewer.getText().length() > 0)
					{
						spanold = new SpannableString(textNoteViewer.getText());
						textNoteViewer.setText(TextUtils.concat(spanold, " ",
								spanUpdted));
					} else {
						textNoteViewer.setText(TextUtils.concat(spanUpdted));
					}

				}
			});

		}

	}

	public void chooseTextStyle() {

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnCooseStyle) {
			inilitze();

			brushDialogtextStyle = new Dialog(TextChooserActivity.this);
			// brushDialogtextStyle.setTitle("Text Style");
			brushDialogtextStyle.requestWindowFeature(Window.FEATURE_NO_TITLE);
			brushDialogtextStyle.setCancelable(true);
			brushDialogtextStyle.setContentView(vi);
			
			brushDialogtextStyle.show();

		} else if (v.getId() == R.id.buttoncolor) {

			selectedButton = R.id.buttoncolor;

			btncolor.setBackgroundColor(Color.WHITE);
			btnTypeface.setBackgroundColor(getResources().getColor(
					R.color.selected_bg_color));

			btncolor.setTextColor(getResources().getColor(
					R.color.selected_bg_color));
			btnTypeface.setTextColor(Color.WHITE);

			arrSpanedString.clear();
			for (int i = 0; i < arrcolors.length; i++) {
				arrSpanedString.add(arrcolors[i]);
			}
			adapter.notifyDataSetChanged();
			
		} else if (v.getId() == R.id.buttonunderline) {
			if (isunderLine) {
				isunderLine = false;
			} else {
				isunderLine = true;
			}

		} else if (v.getId() == R.id.buttontypeface) {

			selectedButton = R.id.buttontypeface;
			btnTypeface.setBackgroundColor(Color.WHITE);
			btncolor.setBackgroundColor(getResources().getColor(
					R.color.selected_bg_color));
			btnTypeface.setTextColor(getResources().getColor(
					R.color.selected_bg_color));
			btncolor.setTextColor(Color.WHITE);

			arrSpanedString.clear();
			for (int i = 0; i < arrtypeface.length; i++) {
				arrSpanedString.add(arrtypeface[i]);
			}
			adapter.notifyDataSetChanged();
			

		}

	}
}
