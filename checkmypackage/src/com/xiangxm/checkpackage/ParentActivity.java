package com.xiangxm.checkpackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * <p>
 * 父Activity 解决startActivityForResult() ，onActivityResult()不被调用问题
 * </p>
 * 
 * @author xiangxm 2013-12-8
 * 
 * 
 */

public class ParentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private setResultCallBack resultCallBack;

	public void setResultCallBack(setResultCallBack resultCallBack) {
		this.resultCallBack = resultCallBack;
	}

	/**
	 * 接口用于回调
	 * 
	 * @author xiangxm
	 * 
	 */
	public interface setResultCallBack {

		void setResult(int requestCode, int resonseCode,Intent srcIntent);
//		void getResult(int requestCode ,int resonseCode,Intent resultIntent) ;
	}
	
	
	
}
