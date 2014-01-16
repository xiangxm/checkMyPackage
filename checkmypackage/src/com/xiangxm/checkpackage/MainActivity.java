package com.xiangxm.checkpackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangxm.cls.cls_companyinfo;
import com.xiangxm.cls.cls_savehistory;
import com.xiangxm.myapp.MyApplication;
import com.xiangxm.utils.Common;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener {

	private LinearLayout selectCompanyLayout;
	private Button searchBtn;
	private Button historyBtn;
	private EditText order_numTxt;
	private TextView companynameTxt;
	private TextView helpinfotext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setUiOptions(
				ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		setContentView(R.layout.activity_main);

		myApplication = MyApplication.getInstance();
		initView();
		Common.closeWhenOncreate(MainActivity.this);// 进入界面关闭软键盘

	}

	/**
	 * 初始化界面
	 */
	public void initView() {

		title = (TextView) findViewById(R.id.title);
		phonenumber = (TextView) findViewById(R.id.phonenumbertxt);
		helpinfotext = (TextView) findViewById(R.id.helpinfotext);
		companynameTxt = (TextView) findViewById(R.id.companynametxt);
		companynameTxt.setText("");
		selectCompanyLayout = (LinearLayout) findViewById(R.id.companyname_layout);
		searchBtn = (Button) findViewById(R.id.searchbtn);
		historyBtn = (Button) findViewById(R.id.historybtn);
		order_numTxt = (EditText) findViewById(R.id.order_numtxt);

		selectCompanyLayout.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		historyBtn.setOnClickListener(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在为您查询...");
		progressDialog.setCancelable(true);
		progressDialog.setTitle("提示信息");

		changeBtnBackground();
		if (!Common.hasShortcut(this)) { // 创建快捷方式
			Common.addShortcut(this);
		}

		if (!Common.onlineFlag(MainActivity.this)) {

			Toast.makeText(this, "离线使用中，非联网状态不能查询订单信息!", Toast.LENGTH_SHORT)
					.show();

		}
	}

	private MyApplication myApplication;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		Intent intent = null;
		if (v.getId() == R.id.searchbtn) {

			if (TextUtils.isEmpty(companynameTxt.getText())) {

				Animation shakeAnim = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.shake_x);
				this.companynameTxt.startAnimation(shakeAnim);

				Toast.makeText(MainActivity.this, "您还未选择快递公司!",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (TextUtils.isEmpty(order_numTxt.getText())) {

				Animation shakeAnim = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.shake_x);
				this.order_numTxt.startAnimation(shakeAnim);

				Toast.makeText(MainActivity.this, "您还未输入订单号!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String order_num = order_numTxt.getText().toString().trim();
			Pattern p = Pattern.compile("[\\da-zA-Z]+?");
			Matcher m = p.matcher(order_num);
			if (!m.matches()) {
				Toast.makeText(MainActivity.this, "您的订单号不符合标准，请检查!",
						Toast.LENGTH_SHORT).show();
				return;
			}

			infoMap.put("order_num", order_num);
			infoMap.put("mark", "");// 备注信息暂时存为空

			new MyThread(infoMap).start();

		} else if (v.getId() == R.id.companyname_layout) {

			intent = new Intent();
			intent.setClass(MainActivity.this, SelectCompanyActivity.class);
			startActivityForResult(intent, 6);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else if (v.getId() == R.id.historybtn) {

			boolean bol = cls_savehistory.checkHasDataOrNot(Common.dbh);
			if (bol) {
				// 可伸缩的listview
				intent = new Intent();
				intent.setClass(MainActivity.this,
						CheckPackageHisActivity.class);
				overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
				startActivity(intent);
			} else {

				Toast.makeText(MainActivity.this, "暂无历史记录!", Toast.LENGTH_SHORT)
						.show();
			}

		}

	}

	HashMap<String, String> infoMap = new HashMap<String, String>();

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // MenuItem item = menu.add(0, SETTINGS_ID, 0, "settings");
	// // item.setIcon(R.drawable.ic_setting);
	// // item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	// // return true;
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.activity_main, menu);
	//
	// return super.onCreateOptionsMenu(menu);
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {// 处理点击事件 最下面
	// switch (item.getItemId()) {
	// /*
	// * case R.id.menu_settings: startActivity(new Intent(this,
	// * SettingActivity.class)); break;
	// */
	//
	// case R.id.menu_delete:
	//
	// new AlertDialog.Builder(this).setTitle("Action Menu Item")
	// .setPositiveButton("确认", null)
	// .setNegativeButton("取消", null).show();
	//
	// break;
	// default:
	// break;
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }

	/**
	 * 修改按钮背景图片
	 */
	public void changeBtnBackground() {

		class MyBtnOnTouch implements OnTouchListener {

			int drawable[] = null;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (v.getId() == R.id.searchbtn) {
					drawable = new int[] { R.drawable.searchbtn,
							R.drawable.searchbtn_pressed };

				} else if (v.getId() == R.id.historybtn) {

					drawable = new int[] { R.drawable.history_btn_normal,
							R.drawable.history_btn_pressed };

				}

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					v.setBackgroundResource(drawable[1]);

					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(drawable[0]);
					break;
				}

				return false;
			}

		}

		searchBtn.setOnTouchListener(new MyBtnOnTouch());
		historyBtn.setOnTouchListener(new MyBtnOnTouch());
	}

	private static final int WAITTING = 0;
	private static final int FINISHED = 1;
	private static final int ERROR = 2;
	private ProgressDialog progressDialog;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case WAITTING:
				progressDialog.show();

				break;
			case ERROR:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();

				}
				break;
			case FINISHED:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();

				}

				Common.closeKeyboardCommAct(MainActivity.this);// 关闭键盘
				break;

			case 10:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();

				}
				Toast.makeText(MainActivity.this, "未获取到相关信息，请验证查询条件!",
						Toast.LENGTH_SHORT).show();
				break;
			}

			super.handleMessage(msg);
		}

	};

	/**
	 * 用于存放结果的map key:timeList value: infoList
	 * **/
	private HashMap<List<String>, List<String>> resultMap = new HashMap<List<String>, List<String>>();

	/**
	 * 子线程联网获取数据
	 * 
	 * @author xiangxm
	 * 
	 */
	class MyThread extends Thread {

		private HashMap<String, String> threadMap = null;

		public MyThread(HashMap<String, String> map) {

			threadMap = new HashMap<String, String>();
			this.threadMap = map;

		}

		@Override
		public void run() {

			int rand = (int) Math.random() * 1000 + 1000;
			String strurl = "http://wap.kuaidi100.com/q.jsp?rand=" + rand
					+ "&id=" + threadMap.get("companyId") + "&postid="
					+ threadMap.get("order_num") + "&fromWeb=null";
			Message msg = new Message();
			msg.what = WAITTING;
			mHandler.sendMessage(msg);// http://wap.kuaidi100.com/q.jsp?rand=1000&id=aae&postid=987654321&fromWeb=null
			String result = getData(strurl);// http://m.kuaidi100.com/index_all.html?type=yuantong&postid=7409440775
			if (result.equals("")) {

				msg = new Message();
				msg.what = 10;
				mHandler.sendMessage(msg);

				return;
			}

			org.jsoup.nodes.Document doc = Jsoup.parse(result);// 7409440775
			Elements element = doc.body().getElementsByTag("p");
			Object[] objArr = element.toArray();
			Pattern p = Pattern.compile("<.+?>|\\&gt;|\\&middot;",
					Pattern.DOTALL);

			String tempStr = null;
			ArrayList<String> timeList = new ArrayList<String>();// 存放快递的时间
			ArrayList<String> infoList = new ArrayList<String>();// 存放快递的对应时间点的信息

			for (int i = 3; i < objArr.length - 2; i++) {
				Matcher m = p.matcher(objArr[i].toString());
				tempStr = m.replaceAll("");
				if (TextUtils.isEmpty(tempStr)) {

					continue;
				} else if (tempStr.startsWith("建议操作")) {

					msg = new Message();
					msg.what = 10;
					mHandler.sendMessage(msg);

					return;
				}
				// 2013-11-17 00:25:57 福建省泉州市石狮市公司 已收件 操作员：包叶明
				timeList.add(new String(tempStr.substring(0, 19)));
				infoList.add(new String(tempStr.substring(20)));
			}

			Log.e("----------时间---------最后结果", timeList.toString());
			Log.e("----------物流的详细信息---------最后结果", infoList.toString());
			msg = new Message();
			msg.what = FINISHED;
			mHandler.sendMessage(msg);

			if (timeList.isEmpty() || infoList.isEmpty()) {

				msg = new Message();
				msg.what = 10;
				mHandler.sendMessage(msg);

				return;
			}

			Intent intent = new Intent();
			intent.setClass(MainActivity.this, PackageDetailInfoActivity.class);
			intent.putStringArrayListExtra("timeList", timeList);
			intent.putStringArrayListExtra("infoList", infoList);

			Bundle bundle = new Bundle();
			bundle.putString("companyName", infoMap.get("companyName"));// 公司名称
			bundle.putString("companyId", infoMap.get("companyId"));
			bundle.putString("order_num", infoMap.get("order_num"));// 运单号
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		}

	}

	/**
	 * 
	 * @param strurl
	 * @return
	 */
	public String getData(String strurl) {
		String output = "";
		URL url;
		try {
			url = new URL(strurl);
			InputStream is = url.openStream();

			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));

			String tem = "";
			while ((tem = buffer.readLine()) != null) {
				output += tem;
			}
			return output;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// 点击空白处 软键盘消失

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null && imm.isActive()) {
			return imm.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), 0);
		}
		return false;

	}

	long waitTime = 2000;
	long touchTime = 0;

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - touchTime) >= waitTime) {
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			touchTime = currentTime;
		} else {
			finish();
		}
	}

	private TextView title;
	private TextView phonenumber;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode == 8 && requestCode == 6) {

			Bundle bundle = data.getExtras();
			if (bundle == null) {

				Toast.makeText(MainActivity.this, "获取数据失败!", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			infoMap = new HashMap<String, String>();
			String name = bundle.getString("companyName");
			String str = bundle.getString("companyId");
			String id = TextUtils.isEmpty(str) ? "" : str;
			infoMap.put("companyName", name);
			infoMap.put("companyId", id);

			List<String> resultList = cls_companyinfo.getCompanyName(
					Common.dbh, id);

			if (resultList.get(0).equals("")) {

				helpinfotext.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				title.setText("");
			} else {
				title.setText("温馨提示：");
				helpinfotext.setText("查询须知：" + resultList.get(0));
			}
			if (resultList.get(1).equals("")) {
				phonenumber.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				title.setText("");
			} else {
				title.setText("温馨提示：");
				phonenumber.setText("查询电话：" + resultList.get(1));
			}

			companynameTxt.setText(TextUtils.isEmpty(name) ? "" : name);

		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		MenuInflater inflater = getMenuInflater() ;
		inflater.inflate(R.menu.mainactivity_menu, menu) ;
		return super.onCreateOptionsMenu(menu);
		
	}

}
