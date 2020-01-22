package com.wangky.video.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wangky.video.R;

public class OperationDialogFragment extends DialogFragment {

    private RadioGroup mSpeedGroup;

    private RadioGroup mScaleGroup;

    private OnOperationListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_operation,container,false);
        mSpeedGroup = view.findViewById(R.id.speed_radio_group);
        mScaleGroup = view.findViewById(R.id.scale_radio_group);
        initOperation();
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OperationDialogFragment.OnOperationListener) {
            mListener = (OperationDialogFragment.OnOperationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOperationListener");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void initOperation(){
        Bundle bundle = getArguments();
        float sp = bundle.getFloat("speed",1f);
        int rm = bundle.getInt("resizeMode",0);
        //设置默认选中
        RadioButton spTarget = mSpeedGroup.findViewById(R.id.speed_normal) ;
        if(sp == 0.5f){
            spTarget = mSpeedGroup.findViewById(R.id.speed_0_5) ;
        }else if(sp == 1.5f){
            spTarget = mSpeedGroup.findViewById(R.id.speed_1_5) ;
        } else if (sp == 2f) {
            spTarget = mSpeedGroup.findViewById(R.id.speed_2) ;
        }

        if(null !=spTarget){
            spTarget.setChecked(true);
        }

        RadioButton rmTarget = mScaleGroup.findViewById(R.id.scale_default) ;
        if(rm == 1){
            rmTarget = mScaleGroup.findViewById(R.id.scale_fit_height);
        }else if(rm == 2){
            rmTarget = mScaleGroup.findViewById(R.id.scale_fit_with_width);
        }else if(rm == 3){
            rmTarget = mScaleGroup.findViewById(R.id.scale_fit_fill);
        }else if(rm == 4){
            rmTarget = mScaleGroup.findViewById(R.id.scale_fit_zoom);
        }

        if(null != rmTarget){
            rmTarget.setChecked(true);
        }

        mSpeedGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb= group.findViewById(checkedId);
            String speed  = (String) rb.getTag();
            mListener.onPlaySpeedChange(Float.valueOf(speed));
        });


        mScaleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb= group.findViewById(checkedId);
            String scale  = (String) rb.getTag();
            mListener.onViewScaleChange(Integer.valueOf(scale));
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments  = getArguments();
        Boolean orientation = arguments.getBoolean("orientation");
        Window window =  getDialog().getWindow();
        //一定要设置Background，如果不设置，window属性设置无效
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams wmlp = window.getAttributes();
        if(orientation){//横屏显示
            wmlp.width = dm.widthPixels/2;
            wmlp.height =ViewGroup.LayoutParams.MATCH_PARENT ;
            wmlp.gravity = Gravity.RIGHT;
            window.getAttributes().windowAnimations = R.style.SlideDialog_Animation;
        }else {
            //竖屏显示
            wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wmlp.height =dm.heightPixels/2 ;
            wmlp.gravity = Gravity.BOTTOM;
            window.getAttributes().windowAnimations = R.style.BottomDialog_Animation;
        }

        window.setAttributes(wmlp);
    }



    public interface OnOperationListener{
        void onPlaySpeedChange(float speed);
        void onViewScaleChange(int scale);
    }

}
