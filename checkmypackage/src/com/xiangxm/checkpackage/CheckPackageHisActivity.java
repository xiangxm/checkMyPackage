package com.xiangxm.checkpackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangxm.cls.cls_savehistory;
import com.xiangxm.utils.Common;

public class CheckPackageHisActivity extends ListActivity implements
		OnScrollListener, OnItemClickListener {

	private LayoutInflater mLayoutInflater;

	private MyAdapter adapter;

	private boolean[] isSelectedItems;

	/*** 数据源 */
	private List<Map<String, Object>> contentDefileList;
	/**返回按钮**/
	private Button backBtn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setUiOptions(
				ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		setContentView(R.layout.historyactivity_layout);
		mLayoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		initView();
		this.setListAdapter(adapter);
		this.getListView().setDivider(
				getResources().getDrawable(R.color.transparent));
		this.getListView().setOnScrollListener(this);
		this.getListView().setOnItemClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initView() {

		backBtn = (Button) findViewById(R.id.checkpackage_backBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.setClass(CheckPackageHisActivity.this,
						MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
				CheckPackageHisActivity.this.finish();
			}
		});
		changeBtnBackground();
		contentDefileList = new ArrayList<Map<String, Object>>();

		contentDefileList.addAll(cls_savehistory.getHistoryList(Common.dbh));

		for (int i = 0; i < contentDefileList.size(); i++) {

			contentDefileList.get(i).put("status", false);
		}

		isSelectedItems = new boolean[contentDefileList.size()];

		for (int i = 0; i < isSelectedItems.length; i++) {
			isSelectedItems[i] = false;
		}

		adapter = new MyAdapter(this, contentDefileList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (isSelectedItems[position]) {
			isSelectedItems[position] = false;
		} else {
			isSelectedItems[position] = true;
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 自定义适配器
	 * 
	 * @author
	 * 
	 */
	class MyAdapter extends BaseAdapter {
		private Context context;
		private List<Map<String, Object>> contentDefileList;

		public MyAdapter(Context context,
				List<Map<String, Object>> contentDefileList) {

			this.context = context;
			this.contentDefileList = contentDefileList;
		}

		public int getCount() {
			return contentDefileList.size();
		}

		public Object getItem(int position) {
			return contentDefileList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup p) {
			CustomLinearLayout view = null;
			if (null == convertView) {
				view = new CustomLinearLayout(CheckPackageHisActivity.this,
						contentDefileList.get(position), position, false);
			} else {
				view = (CustomLinearLayout) convertView;
				view.setDetailInfoLayout(contentDefileList.get(position),
						position, isSelectedItems[position]);
			}
			return view;
		}
	}

	/**
	 * 其实就是一个view组合体 自定义layout
	 * 
	 * @author
	 * 
	 */
	public class CustomLinearLayout extends LinearLayout {

		private LinearLayout layout;
		private RelativeLayout contentTitleLayout;
		private LinearLayout contentDetailLayout;

		private ImageView statusImgView;
		private TextView his_companynamenumber;
		private TextView his_checkpackagetime;
		private TextView detailinfotxt;

		/**
		 * @param context
		 * @param contextDefailMap
		 * @param position
		 * @param isCurrentItem
		 */
		public CustomLinearLayout(Context context,
				final Map<String, Object> contextDefailMap, final int position,
				boolean isCurrentItem) {
			super(context);

			layout = (LinearLayout) mLayoutInflater.inflate(
					R.layout.checkpackagehisinfo_layout, null);

			contentTitleLayout = (RelativeLayout) layout
					.findViewById(R.id.titlelayout);

			contentTitleLayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {

					case MotionEvent.ACTION_DOWN:
						his_companynamenumber.setTextColor(Color.WHITE);
						his_checkpackagetime.setTextColor(Color.WHITE);
						v.setBackgroundColor(Color.parseColor("#0066ff"));

						break;
					case MotionEvent.ACTION_UP:
						his_companynamenumber.setTextColor(Color
								.parseColor("#4F4F4F"));
						his_checkpackagetime.setTextColor(Color
								.parseColor("#4F4F4F"));
						v.setBackgroundColor(Color.parseColor("#FFFFFF"));
						break;
					}

					return false;
				}
			});

			// contentTitleLayout
			// .setOnLongClickListener(new OnLongClickListener() {
			//
			// private PopupWindow popupWindow;
			// private LayoutInflater inflater;
			// private Button confirmBtn;
			// private Button cancelBtn;
			//
			// class onBtnClickListener implements OnClickListener {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			//
			// switch (v.getId()) {
			//
			// case R.id.confirmbtn:
			//
			//
			// break;
			// case R.id.cancelbtn:
			// popupWindow.dismiss() ;
			// break;
			// }
			// }
			// }
			//
			// @Override
			// public boolean onLongClick(final View v) {
			//
			// inflater = (LayoutInflater)
			// CheckPackageHisActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
			// ;
			// View view = inflater.inflate(
			// R.layout.popupwindow_layout, null);
			// confirmBtn = (Button) view
			// .findViewById(R.id.confirmbtn);
			// cancelBtn = (Button) view
			// .findViewById(R.id.cancelbtn);
			// popupWindow = new PopupWindow();
			// popupWindow
			// .setOnDismissListener(new OnDismissListener() {
			//
			// @Override
			// public void onDismiss() {
			//
			// v.setBackgroundColor(Color.WHITE);
			// }
			// });
			// popupWindow.setOutsideTouchable(true);
			// popupWindow.setWidth(200);
			// popupWindow.setHeight(80);
			// popupWindow.setContentView(view);
			// int location[] = new int[2];
			// v.getLocationInWindow(location);
			// popupWindow.showAtLocation(v, Gravity.LEFT
			// | Gravity.TOP, location[0], location[1]);
			// v.setBackgroundColor(Color.parseColor("#48D1CC"));
			// return false;
			// }
			// });
			contentDetailLayout = (LinearLayout) layout
					.findViewById(R.id.DetailLayout);

			his_companynamenumber = (TextView) layout
					.findViewById(R.id.his_companynamenumber);
			statusImgView = (ImageView) layout.findViewById(R.id.workStatusImg);
			his_checkpackagetime = (TextView) layout
					.findViewById(R.id.his_checkpackagetime);
			his_companynamenumber.setTextColor(Color.parseColor("#4F4F4F"));
			his_checkpackagetime.setTextColor(Color.parseColor("#4F4F4F"));
			detailinfotxt = (TextView) layout.findViewById(R.id.detailinfotxt);

			this.addView(layout);
			setDetailInfoLayout(contextDefailMap, position, isCurrentItem);
		}

		/**
		 * 显示具体内容 （查询历史）
		 * 
		 * @param detailInfoMap
		 * @param position
		 * @param isCurrentItem
		 */
		public void setDetailInfoLayout(
				final Map<String, Object> detailInfoMap, final int position,
				boolean isCurrentItem) {
			contentTitleLayout.setBackgroundColor(Color.WHITE);

			his_companynamenumber.setText(Common.object2String(detailInfoMap
					.get("name"))
					+ "："
					+ Common.object2String(detailInfoMap.get("code")));

			his_checkpackagetime.setText("上次查询时间："
					+ Common.object2String(detailInfoMap.get("create_time")));

			his_companynamenumber.setTextColor(Color.parseColor("#4F4F4F"));
			his_checkpackagetime.setTextColor(Color.parseColor("#4F4F4F"));
			statusImgView.setImageResource((detailInfoMap.get("status")
					.equals("true")) ? R.drawable.mm_submenu_down_normal
					: R.drawable.mm_submenu_normal);

			if (isCurrentItem) {
				String strArr = Common.object2String(detailInfoMap.get("info"))
						.replace("-xiangxm-", "\n");

				detailinfotxt.setText(strArr);
				statusImgView
						.setImageResource(R.drawable.mm_submenu_down_normal);

			}

			contentDetailLayout.setVisibility(isCurrentItem ? VISIBLE : GONE);
		}
	}

	/**
	 * 修改按钮背景图片
	 */
	public void changeBtnBackground() {

		class MyBtnOnTouch implements OnTouchListener {

			int drawable[] = null;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				drawable = new int[] { R.drawable.back_button_selected,
						R.drawable.back_button_normal };

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					v.setBackgroundResource(drawable[0]);

					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(drawable[1]);
					break;
				}

				return false;
			}

		}

		backBtn.setOnTouchListener(new MyBtnOnTouch());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuItem item = menu.add(0, SETTINGS_ID, 0, "settings");
		// item.setIcon(R.drawable.ic_setting);
		// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// return true;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {// 处理点击事件 最下面
		switch (item.getItemId()) {
		/*
		 * case R.id.menu_settings: startActivity(new Intent(this,
		 * SettingActivity.class)); break;
		 */

		case R.id.menu_delete:

			new AlertDialog.Builder(this)
					.setTitle("确认删除查询所有历史?")
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									cls_savehistory.deleteAll(Common.dbh);
									adapter.notifyDataSetChanged();
									overridePendingTransition(
											R.anim.slide_left,
											R.anim.slide_right);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).show();

			break;
		case R.id.back_to_main:

			Intent intent = new Intent();
			intent.setClass(CheckPackageHisActivity.this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		CheckPackageHisActivity.this.finish();
	}

	public void onScroll(AbsListView v, int i, int j, int k) {
	}

	public void onScrollStateChanged(AbsListView v, int state) {
		if (state == OnScrollListener.SCROLL_STATE_IDLE) {
		}
	}
}