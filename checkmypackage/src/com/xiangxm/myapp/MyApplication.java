package com.xiangxm.myapp;

import java.util.ArrayList;
import java.util.List;

import com.xiangxm.checkpackage.MainActivity;
import com.xiangxm.cls.cls_companyinfo;
import com.xiangxm.utils.Common;
import com.xiangxm.utils.DatabaseHelper;

import android.app.Application;
import android.content.Context;

/**
 * <p>
 * </p>
 * 
 * @author delvikCoder 2013-12-25
 * 
 * 
 */

public class MyApplication extends  android.app.Application {

	private Context mContext;
	private List<cls_companyinfo> companyInfoList = null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.mContext = mContext;
		myApplication  = this ;
		new Thread(task).start() ;
	}
	private Runnable task = new Runnable() {

		@Override
		public void run() {
			try {
				Common.loadDatabase(MyApplication.this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String hasLoadDB = Common.readConfig(MyApplication.this,
					Common.HASLOAD_DATABASE, "0");
			Common.dbh = new DatabaseHelper(MyApplication.this, "mypackage");
			if (hasLoadDB.equals("0")) {

				Common.readData2Db(MyApplication.this);
				Common.writeConfig(MyApplication.this, Common.HASLOAD_DATABASE,
						"1");
			}
//			companyInfoList = new ArrayList<cls_companyinfo>();
//			companyInfoList = cls_companyinfo.getCompanyInfoList(Common.dbh);
			
		}

	};
	private static MyApplication myApplication ;
	public synchronized static MyApplication getInstance(){
		
		return myApplication ;
	}
	public List<cls_companyinfo> getCompanyInfoList() {
		
		return companyInfoList;
	}
	public  void setCompanyInfoList() {
		this.companyInfoList = cls_companyinfo.getCompanyInfoList(Common.dbh);
	}
	
	
}
