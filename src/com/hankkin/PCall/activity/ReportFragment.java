package com.hankkin.PCall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.baidu.mapapi.SDKInitializer;
import com.hankkin.PCall.R;
import com.hankkin.PCall.popwindow.SelectWayPopWindow;

/**
 * Created by Hankkin on 2015/7/13.
 * 上报Fragment
 */
public class ReportFragment extends Fragment implements View.OnClickListener {

    /*报警原因按钮*/
    private Button btnReportAlarm;
    /*选择报警原因popwindow*/
    private SelectWayPopWindow popWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplicationContext());
        return inflater.inflate(R.layout.fragment_report, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
    }

    private void initViews() {
        btnReportAlarm = (Button) getActivity().findViewById(R.id.btn_report_alarm);
        btnReportAlarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_report_alarm:
                showSelectWayPop();
                break;
        }
    }

    private void showSelectWayPop() {
        popWindow = new SelectWayPopWindow(getActivity(), itemsOnClick);
        //显示窗口
        popWindow.showAtLocation(getActivity().findViewById(R.id.ll_report), Gravity.CENTER, 0, 0); //设置layout在PopupWindow中显示的位置
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            popWindow.dismiss();
        }
    };
}
