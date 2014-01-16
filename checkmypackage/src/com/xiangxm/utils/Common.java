package com.xiangxm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.xiangxm.checkpackage.R;
import com.xiangxm.cls.cls_companyinfo;

/**
 * 工具类
 * 
 * @author xiangxm
 * 
 */
public class Common {

	/**
	 * 
	 * 用于写入配置文件 判断是否已经加载了数据库
	 */
	public static final String HASLOAD_DATABASE = "hasload_database";

	/**
	 * 
	 */
	public static DatabaseHelper dbh;

	/**
	 * 进入画面时，关闭键盘
	 * 
	 * @param context
	 */
	public static void closeWhenOncreate(Context context) {

		// SOFT_INPUT_STATE_ALWAYS_VISIBLE 键盘始终显示
		((Activity) context).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	/**
	 * 加载数据流程：第一次安装该应用的时候，就创建数据库 并且解析数据插入数据库
	 * 
	 * 
	 * SD卡 创建数据库目录
	 * 
	 * @throws Exception
	 */
	public static void loadDatabase(Context context) throws Exception {
		// 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>
		File dir = new File("data/data/" + context.getPackageName()
				+ "/databases");

		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
		File file = new File(dir, "mypackage");
		// file.delete() ;
		if (!file.exists()) {
			// 获得封装.db文件的InputStream对象
			InputStream is = context.getResources().openRawResource(
					R.raw.mypackage);
			FileOutputStream fos = new FileOutputStream("data/data/"
					+ context.getPackageName() + "/databases/mypackage");
			byte[] buffer = new byte[7168];
			int count = 0;
			// 开始复制.db文件
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();

		}
	}

	/**
	 * 解析json数据插入数据库
	 * 
	 * @param context
	 * 
	 */
	public static void readData2Db(Context context) {

		InputStream in = context.getResources().openRawResource(R.raw.companyinfos);
		try {
			// 将in读入reader 中
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"GBK"));
			StringBuffer buffer = new StringBuffer("");
			String tem = "";
			while ((tem = br.readLine()) != null) {
				buffer.append(tem);
			}
			br.close();
			JSONObject myjson = new JSONObject(buffer.toString());
			JSONArray jsonarr = myjson.getJSONArray("companyinfos");
			cls_companyinfo companyinfo = new cls_companyinfo();
			for (int i = 0; i < jsonarr.length(); i++) {
				JSONObject njson = jsonarr.getJSONObject(i);
				String compName = njson.getString("name");
				String compId = njson.getString("id");
				String helpinfo = njson.getString("helpinfo");
				String phoneNumber = njson.getString("phonenumber");
				companyinfo.info_cd = companyinfo.getMaxIndexNo(Common.dbh);
				companyinfo.name = compName;
				companyinfo.id = compId;
				companyinfo.count = "0";
				companyinfo.helpInfo = helpinfo;
				companyinfo.phoneNumber = phoneNumber;
				companyinfo.addData(Common.dbh);
			}

			Common.writeConfig(context, Common.HASLOAD_DATABASE, "1");// 加载数据库完成，写入成功标志位
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 解析json数据插入数据库
	 * 
	 * @param context
	 * 
	 */
	public static void readData(Context context) {

		InputStream in = context.getResources().openRawResource(R.raw.companyinfos);
		List<cls_companyinfo> dataList = new ArrayList<cls_companyinfo>();
		try {
			// 将in读入reader 中
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"GBK"));
			StringBuffer buffer = new StringBuffer("");
			String tem = "";
			while ((tem = br.readLine()) != null) {
				buffer.append(tem);
			}
			br.close();
			JSONObject myjson = new JSONObject(buffer.toString());
			JSONArray jsonarr = myjson.getJSONArray("ids");
			for (int i = 0; i < jsonarr.length(); i++) {
				JSONObject njson = jsonarr.getJSONObject(i);
				String compName = njson.getString("name");
				String compId = njson.getString("id");
				String helpinfo = njson.getString("helpinfo");
				String phoneNumber = njson.getString("phonenumber");

				// dataList.add(compName);
				// dataList.add(compId);
				// dataList.add(helpinfo);
				// dataList.add(phoneNumber);
			}

			Common.writeConfig(context, Common.HASLOAD_DATABASE, "1");// 加载数据库完成，写入成功标志位
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static void writeConfig(Context context, String key, String val) {
		SharedPreferences share = context.getSharedPreferences("perference",
				Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public static String readConfig(Context context, String key, String defval) {
		String str = "";
		SharedPreferences share = context.getSharedPreferences("perference",
				Context.MODE_PRIVATE);
		str = share.getString(key, defval);
		return str;
	}

	public static void setClassValueBycursor(Object obj, Cursor cursor) {
		int ColCount = cursor.getColumnCount();
		int i = 0;
		for (i = 0; i < ColCount; i++) {
			String ColName = cursor.getColumnName(i);

			try {
				Field f = obj.getClass().getField(ColName);
				String ret = cursor.getString(i);
				if (f == null)
					continue;
				if (ret == null)
					ret = "";
				f.set(obj, ret);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Map<String, Object> getListObjectBycursor(Cursor cursor) {
		int ColCount = cursor.getColumnCount();
		int i = 0;
		Map<String, Object> map = new HashMap<String, Object>();

		for (i = 0; i < ColCount; i++) {
			String ColName = cursor.getColumnName(i);
			try {

				String ret = cursor.getString(i);
				if (ret == null)
					ret = "";
				map.put(ColName, ret);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}

	public static void closeKeyboardWhenOnCreate(Context mContex) {
		((Activity) mContex).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	/**
	 * 为程序创建桌面快捷方式
	 */
	public static void addShortcut(Context context) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				context.getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClassName(context, context.getClass().getName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				context, R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		context.sendBroadcast(shortcut);
	}

	/**
	 * 判断桌面快捷方式是否存在
	 */
	public static boolean hasShortcut(Context context) {
		boolean isInstallShortcut = false;
		final ContentResolver cr = context.getContentResolver();
		String AUTHORITY = null;

		if (android.os.Build.VERSION.SDK_INT < 8)
			AUTHORITY = "com.android.launcher.settings";
		else
			AUTHORITY = "com.android.launcher2.settings";

		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { context.getString(R.string.app_name).trim() },
				null);
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
			c.close();
		}

		return isInstallShortcut;
	}

	/**
	 * @param context
	 * @return 联网标志
	 */
	public static boolean onlineFlag(Context context) {

		boolean bol = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager == null ? null
				: connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isAvailable()) {

			bol = true;
			return bol;
		}

		return bol;
	}

	/*
	 * 获取当前系统时间,YYYY-MM-DD hh:mm:ss
	 */
	public static String getCurTime() {
		String Time = null;

		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Time = sDateFormat.format(new java.util.Date());

		return Time;
	}

	/**
	 * 普通关闭
	 * 
	 * @param context
	 */
	public static void closeKeyboardCommAct(Context context) {

		InputMethodManager imm = (InputMethodManager) ((Activity) context)
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (((Activity) context).getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	/**
	 * 全角转换成半角，适应屏幕:TextView换行时，全角和半角导致显示混乱。 /全角空格为12288，半角空格为32 /其他字符半角(33
	 * -126)与全角(65281- 65374)的对应关系是：均相差65248
	 * 
	 * @param input
	 * @return
	 */
	public static String changeQuanj2Banj(String input) {

		char[] c = input.toCharArray();

		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
			}
			if (c[i] > 65280 && c[i] < 65375) {

				c[i] = (char) (c[i] - 65248);
			}
		}
		return String.valueOf(c);

	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String object2String(Object obj) {

		String str = "";
		if (obj != null) {

			str = String.valueOf(obj);
		}

		return str;
	}

}
