package de.uvwxy.habitrpg.api;

import android.graphics.Color;

public class HabitColors {
	public static int colorBackground = Color.rgb(245,245,245);
	public static int colorHP = Color.rgb(214, 64, 64);
	public static int colorXP = Color.rgb(255, 207, 66);
	public static int colorAdd = Color.rgb(141, 177, 240);
	
	public static int colorWorst = Color.rgb(230, 184, 175);
	public static int colorworse = Color.rgb(244, 204, 204);
	public static int colorBad = Color.rgb(252, 229, 205);
	public static int colorNeutral = Color.rgb(255, 242, 204);
	public static int colorGood = Color.rgb(217, 234, 211);
	public static int colorBetter = Color.rgb(208, 224, 227);
	public static int colorBest = Color.rgb(201, 218, 248);
	public static int colorCompleted = Color.rgb(217, 217, 217);

	//	// color variables
	//	$worst      =   rgb(230, 184, 175)
	//	$worse      =   rgb(244, 204, 204)
	//	$bad        =   rgb(252, 229, 205)
	//	$neutral    =   rgb(255, 242, 204)
	//	$good       =   rgb(217, 234, 211)
	//	$better     =   rgb(208, 224, 227)
	//	$best       =   rgb(201, 218, 248)
	//	$completed  =   rgb(217, 217, 217)

	public static int colorFromValue(double value){
		if (value < -20)
	     return colorWorst;
	   else if( value < -10)
	     return colorworse;
	   else if( value < -1)
	     return colorBad;
	   else if( value < 1)
	     return colorNeutral;
	   else if( value < 5)
	     return colorGood;
	   else if( value < 10)
	     return colorBetter;
	   else
	     return colorBest;
	}
}
