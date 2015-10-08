package com.tilak.noteshare;

import android.content.Context;
import android.graphics.Typeface;

public class NoteShareFonts {

	static String arial = "arial_1.ttf";
	static String arialbold = "arialbd_1.ttf";
	static String arialitalic = "ariali_1.ttf";
	static String timesnewroman = "times_1.ttf";
	static String timesnewromanbold = "timesbd_1.ttf";
	static String timesnewromanitalic = "timesbi_1.ttf";
	static String monotypecorsiva = "MTCORSVA_1.TTF";
	static String erasmediumitc = "ERASMD_1.TTF";
	static String helvetica = "HelveticaLTStd-Cond_0.otf";
	static String helveticabold = "HelveticaNeueLTStd-Bd_0.otf";
	static String helveticaitalic = "HelveticaLTStd-Obl_0.otf";
	static String mongolianbaiti = "monbaiti_1.ttf";

	static String dosisbook = "Dosis-Regular_1.ttf";
	static String dancingscript = "Dancing Script.ttf";
	static String erasmediumitcitalic = "ERASDEMI_1.TTF";

	static String cinzeldecorative = "CinzelDecorative-Regular.ttf";
	static String cinzeldecorativebold = "CinzelDecorative-Bold.ttf";
	static String edwardian = "ITCEDSCR_1.TTF";

	static String junction = "Junction.otf";
	static String lindenhill = "LindenHill-Regular.ttf";
	static String lindenhillitalic = "LindenHill-Italic.ttf";
	static String pacifico = "Pacifico_0.ttf";
	static String windsong = "Windsong.ttf";
	static String sakkalmajalla = "majalla_1.ttf";
	static String sakkalmajallabold = "majallab_1.ttf";
	static String sakkalmajallaitalic = "monbaiti_1.ttf";

	public static Typeface asTypeface(Context context, String fileName) {

		String fileName1 = fileName.replaceAll(" ", "").trim().toLowerCase();
		System.out.println("font Name:" + fileName1);

		Typeface typeface = null;
		if (fileName1.equalsIgnoreCase(arial)) {
			typeface = Typeface.createFromAsset(context.getAssets(), arial);
		} else if (fileName1.equalsIgnoreCase(arialbold)) {
			typeface = Typeface.createFromAsset(context.getAssets(), arialbold);
		} else if (fileName1.equalsIgnoreCase(arialitalic)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					arialitalic);
		}

		else if (fileName1.equalsIgnoreCase(timesnewroman)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					timesnewroman);
		} else if (fileName1.equalsIgnoreCase(timesnewromanbold)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					timesnewromanbold);
		} else if (fileName1.equalsIgnoreCase(timesnewromanitalic)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					timesnewromanitalic);
		} else if (fileName1.equalsIgnoreCase(monotypecorsiva)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					monotypecorsiva);
		} else if (fileName1.equalsIgnoreCase(erasmediumitc)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					erasmediumitc);
		} else if (fileName1.equalsIgnoreCase(helvetica)) {
			typeface = Typeface.createFromAsset(context.getAssets(), helvetica);
		}

		else if (fileName1.equalsIgnoreCase(helveticabold)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					helveticabold);
		}

		else if (fileName1.equalsIgnoreCase(helveticaitalic)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					helveticaitalic);
		}

		else if (fileName1.equalsIgnoreCase(mongolianbaiti)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					mongolianbaiti);
		}

		else if (fileName1.equalsIgnoreCase(dosisbook)) {
			typeface = Typeface.createFromAsset(context.getAssets(), dosisbook);
		}

		else if (fileName1.equalsIgnoreCase(dancingscript)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					dancingscript);
		}

		else if (fileName1.equalsIgnoreCase(erasmediumitcitalic)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					erasmediumitcitalic);
		}

		else if (fileName1.equalsIgnoreCase(cinzeldecorative)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					cinzeldecorative);
		}

		else if (fileName1.equalsIgnoreCase(cinzeldecorativebold)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					cinzeldecorativebold);
		}

		else if (fileName1.equalsIgnoreCase(edwardian)) {
			typeface = Typeface.createFromAsset(context.getAssets(), edwardian);
		}

		else if (fileName1.equalsIgnoreCase(junction)) {
			typeface = Typeface.createFromAsset(context.getAssets(), junction);
		} else if (fileName1.equalsIgnoreCase(lindenhill)) {
			typeface = Typeface
					.createFromAsset(context.getAssets(), lindenhill);
		}

		else if (fileName1.equalsIgnoreCase(pacifico)) {
			typeface = Typeface.createFromAsset(context.getAssets(), pacifico);
		} else if (fileName1.equalsIgnoreCase(windsong)) {
			typeface = Typeface.createFromAsset(context.getAssets(), windsong);
		} else if (fileName1.equalsIgnoreCase(sakkalmajalla)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					sakkalmajalla);
		} else if (fileName1.equalsIgnoreCase(sakkalmajallabold)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					sakkalmajallabold);
		} else if (fileName1.equalsIgnoreCase(sakkalmajallaitalic)) {
			typeface = Typeface.createFromAsset(context.getAssets(),
					sakkalmajallaitalic);
		} else {
			typeface = Typeface.createFromAsset(context.getAssets(), arial);
		}

		return typeface;
	}

	public static String asTypefacefaimly(String fileName) {

		String fileName1 = fileName.replaceAll(" ", "").trim().toLowerCase();
		System.out.println("font Name:" + fileName1);
		String fontFamily = "";

		Typeface typeface = null;
		if (fileName1.equalsIgnoreCase(arial)) {
			fontFamily = arial;
		} else if (fileName1.equalsIgnoreCase(arialbold)) {
			fontFamily = arialbold;
		} else if (fileName1.equalsIgnoreCase(arialitalic)) {
			fontFamily = arialitalic;
		}

		else if (fileName1.equalsIgnoreCase(timesnewroman)) {
			fontFamily = timesnewroman;
		} else if (fileName1.equalsIgnoreCase(timesnewromanbold)) {
			fontFamily = timesnewromanbold;
		} else if (fileName1.equalsIgnoreCase(timesnewromanitalic)) {
			fontFamily = timesnewromanitalic;
		} else if (fileName1.equalsIgnoreCase(monotypecorsiva)) {
			fontFamily = monotypecorsiva;
		} else if (fileName1.equalsIgnoreCase(erasmediumitc)) {
			fontFamily = erasmediumitc;
		} else if (fileName1.equalsIgnoreCase(helvetica)) {
			fontFamily = helvetica;
		}

		else if (fileName1.equalsIgnoreCase(helveticabold)) {
			fontFamily = helveticabold;
		}

		else if (fileName1.equalsIgnoreCase(helveticaitalic)) {
			fontFamily = helveticaitalic;
		}

		else if (fileName1.equalsIgnoreCase(mongolianbaiti)) {
			fontFamily = mongolianbaiti;
		}

		else if (fileName1.equalsIgnoreCase(dosisbook)) {
			fontFamily = dosisbook;
		}

		else if (fileName1.equalsIgnoreCase(dancingscript)) {
			fontFamily=
					dancingscript;
		}

		else if (fileName1.equalsIgnoreCase(erasmediumitcitalic)) {
			fontFamily=
					erasmediumitcitalic;
		}

		else if (fileName1.equalsIgnoreCase(cinzeldecorative)) {
			fontFamily=
					cinzeldecorative;
		}

		else if (fileName1.equalsIgnoreCase(cinzeldecorativebold)) {
			fontFamily=
					cinzeldecorativebold;
		}

		else if (fileName1.equalsIgnoreCase(edwardian)) {
			fontFamily= edwardian;
		}

		else if (fileName1.equalsIgnoreCase(junction)) {
			fontFamily=junction;
		} else if (fileName1.equalsIgnoreCase(lindenhill)) {
			fontFamily=lindenhill;
		}

		else if (fileName1.equalsIgnoreCase(pacifico)) {
			fontFamily= pacifico;
		} else if (fileName1.equalsIgnoreCase(windsong)) {
			fontFamily=windsong;
		} else if (fileName1.equalsIgnoreCase(sakkalmajalla)) {
			fontFamily=
					sakkalmajalla;
		} else if (fileName1.equalsIgnoreCase(sakkalmajallabold)) {
			fontFamily=
					sakkalmajallabold;
		} else if (fileName1.equalsIgnoreCase(sakkalmajallaitalic)) {
			fontFamily=
					sakkalmajallaitalic;
		} else {
			fontFamily= arial;
		}

		return fontFamily;
	}
}
