package com.xiangxm.checkpackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiangxm.cls.cls_savehistory;
import com.xiangxm.utils.Common;

/**
 * <p>
 * 包裹详细信息
 * </p>
 * 
 * @author xiangxm 2013-12-6
 * 
 * 
 */

public class PackageDetailInfoActivity extends Activity implements
		OnClickListener {

	private TextView companyNameTxt;
	// private TextView companyIdTxt;
	/**
	 * aboutResult title
	 * 
	 */
	private HashMap<String, String> detailMap = new HashMap<String, String>();
	private ListView resultListView;
	private MyAdaper myAdaper;
	private Button savecheckinfoBtn;
	/**
	 * 
	 * 存放该订单所有相关信息
	 */
	private HashMap<String, String> infoMap = new HashMap<String, String>();
	private ArrayList<String> timeList;
	private ArrayList<String> infoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.packagedetailactivity);

		companyNameTxt = (TextView) this
				.findViewById(R.id.packagedetail_companynametxt);
		savecheckinfoBtn = (Button) this.findViewById(R.id.savecheckinfoBtn);
		savecheckinfoBtn.setOnClickListener(this);
		// companyIdTxt = (TextView) this.findViewById(R.id.delivernumtxt);

		Intent intent = getIntent();
		timeList = intent.getStringArrayListExtra("timeList");
		infoList = intent.getStringArrayListExtra("infoList");
		Bundle bundle = intent.getExtras();
		infoMap.put("companyName", bundle.getString("companyName"));
		infoMap.put("companyId", bundle.getString("companyId"));
		infoMap.put("order_num", bundle.getString("order_num"));

		companyNameTxt.setText(infoMap.get("companyName") + ":"
				+ infoMap.get("order_num"));
		// changeData(list) ;

		resultListView = (ListView) findViewById(R.id.resultlistview);

		myAdaper = new MyAdaper(infoList, timeList, this);
		resultListView.setAdapter(myAdaper);

		resultListView.setOnItemClickListener(new MyItemOnclickListener());

		backBtn = (Button) findViewById(R.id.detailbackBtn);
		backBtn.setOnClickListener(this);
	}

	class MyItemOnclickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			view.setBackgroundColor(Color.TRANSPARENT);

		}

	}

	class Holder {

		TextView timeText;
		TextView infoText;
		ImageView imageView;

	}

	/**
	 * 自定义适配器
	 * 
	 * @author xiangxm
	 * 
	 */
	public class MyAdaper extends BaseAdapter {

		LayoutInflater layoutInflater = null;
		List<String> timeList = null;
		List<String> infoList = null;
		Holder holder = null;
		Context mContext;
		String[] strArr = null;

		public MyAdaper(List<String> infoList, List<String> timeList,
				Context context) {

			mContext = context;
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.timeList = new ArrayList<String>() ;
			this.infoList = new ArrayList<String>() ;
			this.timeList = timeList;
			this.infoList = infoList;
			this.strArr = new String[2];

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			strArr[0] = timeList.get(position);
			strArr[1] = infoList.get(position);
			return strArr;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {

				convertView = layoutInflater.inflate(
						R.layout.resultlistview_item, null);

				holder = new Holder();

				holder.timeText = (TextView) convertView
						.findViewById(R.id.timetxt);
				holder.infoText = (TextView) convertView
						.findViewById(R.id.infotxt);
				// holder.imageView = (ImageView) convertView
				// .findViewById(R.id.imageview);
				convertView.setTag(holder);

			} else {

				holder = (Holder) convertView.getTag();
			}

			// holder.imageView.setVisibility(View.VISIBLE);

			holder.timeText.setTextColor(Color.parseColor("#1C86EE"));
			holder.infoText.setTextColor(Color.parseColor("#515151"));

			String[] str = (String[]) getItem(position);
			if ((position == timeList.size() - 1) && str[1].contains("已签收")) {// 如果是最后一个并且包含已经签收三个字就显示红色
				// holder.imageView.setVisibility(View.GONE);
				holder.timeText.setTextColor(Color.parseColor("#CD0000"));
				holder.infoText.setTextColor(Color.parseColor("#CD0000"));
			}
			holder.timeText.setText(str[0]);
			holder.infoText.setText(str[1]);
			return convertView;
		}

	}

	/**
	 * 转换数据
	 * 
	 * @param resultList
	 */
	public void changeData(ArrayList<String> resultList) {

		int size = resultList.size();
		int n = 0;
		while (n < size) {

			if (n == 0) {

				detailMap.put("aboutResult", resultList.get(n));
				continue;
			} else if (n == 1) {

				detailMap.put("title", resultList.get(n));
				continue;
			}

			String key = resultList.get(n).substring(0, 19);// 2013-07-01
			// 00:00:00

			String value = resultList.get(n).substring(20);
			detailMap.put(key, value);

		}

	}

	private Button backBtn;

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.savecheckinfoBtn) {

			// 保存历史记录
			// 当前时间 、快递信息 、
			cls_savehistory savehistory = new cls_savehistory();
			savehistory.his_cd = savehistory.getMaxIndexNo(Common.dbh);
			savehistory.id = infoMap.get("companyId");
			savehistory.name = infoMap.get("companyName");
			savehistory.code = infoMap.get("order_num");// 订单号
			savehistory.create_time = Common.getCurTime();

			int size = timeList.size();

			if (size != infoList.size()) {
				Toast.makeText(PackageDetailInfoActivity.this,
						"信息保存失败,该信息暂时不能保存!", Toast.LENGTH_SHORT).show();
				return;
			}

			StringBuffer buffer = new StringBuffer();
			// 处理信息保存查询历史
			for (int i = 0; i < size; i++) {

				if(i!=size-1){
					
				buffer.append(timeList.get(i)).append("-xiangxm-")
						.append(infoList.get(i)).append("-xiangxm-");
				}else{
					buffer.append(timeList.get(i)).append("-xiangxm-")
					.append(infoList.get(i));
				}

			}
			savehistory.info = buffer.toString();
			Log.e("--------保存查询的历史信息---------", buffer.toString());
			Common.dbh.beginTransaction();
			try {
				boolean bol = savehistory.addData(Common.dbh);
				if (!bol) {

					Toast.makeText(PackageDetailInfoActivity.this, "保存信息失败!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Common.dbh.setTransactionSuccessful();
			} catch (Exception ex) {

				ex.printStackTrace();
				Log.e("------保存历史信息-----", "保存失败");
			}finally{
				
//				不管回滚不会滚，都要执行这句话
				Common.dbh.endTransaction();
			}

			
			
			timeList.clear() ;
			infoList.clear() ;
			myAdaper.notifyDataSetChanged() ;
			Toast.makeText(PackageDetailInfoActivity.this, "保存信息成功!",
					Toast.LENGTH_SHORT).show();
			
			
			this.finish() ;
		} else if (v.getId() == R.id.detailbackBtn) {

			this.finish();
			// 添加动画
		}

	}
}
