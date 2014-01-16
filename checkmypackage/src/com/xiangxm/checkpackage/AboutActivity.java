package com.xiangxm.checkpackage;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <p>
 * 关于画面
 * </p>
 * 
 * @author xiangxm 2013-12-8
 * 
 * 
 */

public class AboutActivity extends Activity implements OnTouchListener {

	private TextView textView1;
	private LinearLayout aboutLayout_1;
	private LinearLayout aboutLayout_2;
	private LinearLayout aboutLayout_3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().setUiOptions(
				ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		setContentView(R.layout.activity_main);
		setContentView(R.layout.aboutapplayout);
//		textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setText("设置");

		aboutLayout_1 = (LinearLayout) findViewById(R.id.aboutlayout_1);
		aboutLayout_2 = (LinearLayout) findViewById(R.id.aboutlayout_2);
		aboutLayout_3 = (LinearLayout) findViewById(R.id.aboutlayout_3);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {

		case R.id.aboutlayout_1:
			
			
			
			break;
		case R.id.aboutlayout_2:
			break;
		case R.id.aboutlayout_3:
			break;
		}

		return false;
	}

}
