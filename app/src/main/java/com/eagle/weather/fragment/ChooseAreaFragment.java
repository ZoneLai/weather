package com.eagle.weather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eagle.weather.R;
import com.eagle.weather.activitys.MainActivity;
import com.eagle.weather.activitys.WeatherActivity;
import com.eagle.weather.db.City;
import com.eagle.weather.db.County;
import com.eagle.weather.db.Province;
import com.eagle.weather.util.HttpUtil;
import com.eagle.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by DELL on 2017/12/9.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;
    private TextView mTextView;
    private Button mButton;
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;
    private List<String> mArrayList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> mProvinceList;

    /**
     * 市列表
     */
    private List<City> mCityList;

    /**
     * 县列表
     */
    private List<County> mCountyList;

    /**
     * 选中的省份
     */
    private Province mProvince;

    /**
     * 选中的市
     */
    private City mCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        mTextView = view.findViewById(R.id.tv_title);
        mButton = view.findViewById(R.id.bt_back);
        mListView = view.findViewById(R.id.lv_view);
        mArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mArrayList);
        mListView.setAdapter(mArrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    mProvince = mProvinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    mCity = mCityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String string = mCountyList.get(i).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", string);
                        startActivity(intent);
                        getActivity().finish();
                    }
//                    else if (getActivity() instanceof WeatherActivity) {
//                        WeatherActivity activity = (WeatherActivity) getActivity();
//                        // TODO...
//
//                    }
                }
            }
        });
        // TODO...
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所在的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        mTextView.setText("中国");
        mButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            mArrayList.clear();
            for (Province province : mProvinceList) {
                mArrayList.add(province.getProvinceName());
            }
            mArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFormServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        mTextView.setText(mProvince.getProvinceName());
        mButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceid = ?", String.valueOf(mProvince.getId())).find(City.class);
        if (mCityList.size() > 0) {
            mArrayList.clear();
            for (City city : mCityList) {
                mArrayList.add(city.getCityName());
            }
            mArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = mProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFormServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        mTextView.setText(mCity.getCityName());
        mButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityid = ?", String.valueOf(mCity.getId())).find(County.class);
        if (mCountyList.size() > 0) {
            mArrayList.clear();
            for (County county : mCountyList) {
                mArrayList.add(county.getCountyName());
            }
            mArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = mProvince.getProvinceCode();
            int cityCode = mCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFormServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
    private void queryFormServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, mProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, mCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
