package com.wangky.video.view;

import android.app.Dialog;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wangky.video.R;

public class MetaDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_meta,container,false);
        Bundle bundle = getArguments();
        String frameRate = bundle.getString("frameRate","");
        String fileName = bundle.getString("fileName","");
        String fileSize = bundle.getString("fileSize","");
        String bitRate = bundle.getString("bitRate","");
        String resolution = bundle.getString("resolution","");
        TextView metaName =  view.findViewById(R.id.meta_name);
        TextView metaFrameRate =  view.findViewById(R.id.meta_rate);
        TextView metaFileSize =  view.findViewById(R.id.meta_size);
        TextView metaBitRate =  view.findViewById(R.id.meta_bitRate);
        TextView metaResolution = view.findViewById(R.id.meta_resolution);
        metaName.setText(fileName);
        metaFileSize.setText(fileSize);
        metaFrameRate.setText(frameRate);
        metaBitRate.setText(bitRate);
        metaResolution.setText(resolution);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments  = getArguments();
        Boolean orientation = arguments.getBoolean("orientation");
        Window window =  getDialog().getWindow();
        //一定要设置Background，如果不设置，window属性设置无效
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams wmlp = window.getAttributes();
        if(orientation){//横屏显示
            wmlp.width = dm.widthPixels/2;
            wmlp.height =ViewGroup.LayoutParams.MATCH_PARENT ;
            wmlp.gravity = Gravity.CENTER_HORIZONTAL;
            window.getAttributes().windowAnimations = R.style.BottomDialog_Animation;
        }else {
            //竖屏显示
            wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT; // dm.widthPixels/2;
            wmlp.height =dm.heightPixels/2 ;
            wmlp.gravity = Gravity.CENTER_VERTICAL;
            window.getAttributes().windowAnimations = R.style.BottomDialog_Animation;
        }

        window.setAttributes(wmlp);
    }
}
