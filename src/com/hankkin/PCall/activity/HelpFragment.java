package com.hankkin.PCall.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.hankkin.PCall.R;
import com.hankkin.PCall.dialog.AlterDialog;
import com.hankkin.PCall.util.CheckHasNet;
import com.hankkin.PCall.util.HelpAssistor;
import com.hankkin.PCall.util.ToastUtils;

/**
 * Created by Hankkin on 2015/7/13.
 * 求助Fragment
 */
public class HelpFragment extends Fragment {

    /*定位相关*/
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    private BitmapDescriptor mCurrentMarker;

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    /*定位方式*/
    private Button btnLocWay;
    /* 是否首次定位*/
    private boolean isFirstLoc = true;

    private HelpFragment frgInstance;
    /*定位信息文本*/
    private TextView tvLocInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        frgInstance = this;
        super.onActivityCreated(savedInstanceState);

        HelpAssistor.createView(getActivity().getApplicationContext());
        HelpAssistor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlterDialog.showAlertView(getActivity(), "温馨提示",
                        "您确定要一键求助吗？", "确定", null,
                        new String[]{"取消"}, null).show();
            }
        });
        initViews();
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        btnLocWay.setText("跟随");
                        mCurrentMode = LocationMode.FOLLOWING;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case COMPASS:
                        btnLocWay.setText("普通");
                        mCurrentMode = LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case FOLLOWING:
                        btnLocWay.setText("罗盘");
                        mCurrentMode = LocationMode.COMPASS;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                }
            }
        };
        btnLocWay.setOnClickListener(btnClickListener);
    }

    /**
     * 初始化组件
     * by Hankkin at:2015年7月15日 22:39:40
     * 增加网络判断
     * by Hankkin at:2015年7月15日 23:25:51
     */
    private void initViews() {
        btnLocWay = (Button) getActivity().findViewById(R.id.btn_help_locway);
        tvLocInfo = (TextView) getActivity().findViewById(R.id.tv_help_locinfo);
        mCurrentMode = LocationMode.NORMAL;
        btnLocWay.setText("普通");
        // 地图初始化
        mMapView = (MapView) getActivity().findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        if (CheckHasNet.isMobileNet(getActivity().getApplicationContext()) || CheckHasNet.isNetWorkOk(getActivity().getApplicationContext())) {
            mLocClient = new LocationClient(getActivity());
            mLocClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            option.setAddrType("all");  //设置获取位置信息，若不设置address为null
            mLocClient.setLocOption(option);
            mLocClient.start();
        } else {
            ToastUtils.showLToast(getActivity().getApplicationContext(), "网络异常，请稍后重试");
        }
    }

    /**
     * 定位SDK监听函数
     * by Hankkin at:2015年7月14日 21:28:12
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("Time : ");
            sb.append(location.getTime());
            sb.append("\nError code : ");
            sb.append(location.getLocType());
            sb.append("\nLatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nLontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nRadius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nSpeed : ");
                sb.append(location.getSpeed());
                sb.append("\nSatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\nAddress : ");
                sb.append(location.getAddrStr());
            }
            tvLocInfo.setText(sb.toString());
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 一键求助小助手创建、点击事件
     * by Hankkin at:2015年7月14日 22:08:34
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
