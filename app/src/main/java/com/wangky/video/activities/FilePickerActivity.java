package com.wangky.video.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangky.video.R;
import com.wangky.video.adapter.LocalFileAdapter;
import com.wangky.video.beans.LocalFile;
import com.wangky.video.dao.DBTools;
import com.wangky.video.enums.FileTyp;
import com.wangky.video.util.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePickerActivity extends AppCompatActivity implements LocalFileAdapter.onItemClickListener{


    private RecyclerView mFilePickerList;

    private List<LocalFile> fileList = new ArrayList<>();

    private LocalFileAdapter adapter;

    private TextView mCurrentPath;

    //当前所处路径
    private String currentPath = Const.ROOT_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        mCurrentPath = findViewById(R.id.current_path);
        Toolbar toolbar = findViewById(R.id.file_picker_toolbar);
        setSupportActionBar(toolbar);
        setTitle("文件选择");
        //获取上一次访问的路径
        String lastPath = DBTools.getInstance().getRecentVisitPath();
        if(null !=lastPath){
            File file = new File(lastPath);
            if(file.exists()){
                currentPath = file.getParent();
            }
        }
        mCurrentPath.setText(currentPath);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_heart);
        toolbar.setNavigationOnClickListener(v -> finish());
        mFilePickerList = findViewById(R.id.file_picker_list);

        adapter= new LocalFileAdapter(FilePickerActivity.this,fileList,this);

        mFilePickerList.setAdapter(adapter);
        mFilePickerList.setLayoutManager(new LinearLayoutManager(FilePickerActivity.this,RecyclerView.VERTICAL,false));
//        mFilePickerList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        initData();
    }



    private void initData() {
        mCurrentPath.setText(currentPath);
        //从根目录开始
        File rootPath = new File(currentPath);
        List<LocalFile> files = new ArrayList<>();
        if(!currentPath.equals(Const.ROOT_PATH)){
            LocalFile lastPath = new LocalFile("..",FileTyp.BACK,System.currentTimeMillis(),0l,rootPath.getParent());
            files.add(lastPath);
        }

        files.addAll(listFiles(rootPath));

        adapter.refreshFileList(files);
    }


    /**
     * 查询目录下文件
     * @return
     */
    public List<LocalFile> listFiles(File rootPath){
        List<LocalFile> files = new ArrayList<>();
        File[] roots = rootPath.listFiles();
        if(null == roots){
            return Collections.EMPTY_LIST;
        }
        for (File file :roots){
            if(file.isDirectory() && !file.isHidden()){
                LocalFile localFile = new LocalFile(file.getName(), FileTyp.FOLDER,file.lastModified(), file.length(),file.getAbsolutePath());
                files.add(localFile);
            }else if(file.isFile() && !file.isHidden()){
                LocalFile localFile = new LocalFile(file.getName(), FileTyp.FILE,file.lastModified(), file.length(),file.getAbsolutePath());
                files.add(localFile);
            }
        }
        return files;
    }


    /**
     * 按名称排序
     * @param byASC 是否升序
     */
    private void initDataSortByName(boolean byASC) {
        mCurrentPath.setText(currentPath);
        //从根目录开始
        File rootPath = new File(currentPath);
        List<LocalFile> files = new ArrayList<>();
        if(!currentPath.equals(Const.ROOT_PATH)){
            LocalFile lastPath = new LocalFile("..",FileTyp.BACK,System.currentTimeMillis(),0l,rootPath.getParent());
            files.add(lastPath);
        }
        List<LocalFile> source = listFiles(rootPath);
        if(byASC){
            Collections.sort(source, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        }else {
            Collections.sort(source, (o1, o2) -> o2.getName().compareTo(o1.getName()));
        }

        files.addAll(source);
        adapter.refreshFileList(files);
    }


    /**
     * 按修改时间排序
     * @param byASC 是否升序
     */
    private void initDataSortByModifyTime(boolean byASC) {
        mCurrentPath.setText(currentPath);
        //从根目录开始
        File rootPath = new File(currentPath);
        List<LocalFile> files = new ArrayList<>();
        if(!currentPath.equals(Const.ROOT_PATH)){
            LocalFile lastPath = new LocalFile("..",FileTyp.BACK,System.currentTimeMillis(),0l,rootPath.getParent());
            files.add(lastPath);
        }
        List<LocalFile> source = listFiles(rootPath);
        if(byASC){
            Collections.sort(source, (o1, o2) -> {
                if(o1.getModifyTime() > o2.getModifyTime()){
                    return 1;
                }else if(o1.getModifyTime() < o2.getModifyTime()){
                    return -1;
                }
                return 0;
            });

        }else {

            Collections.sort(source, (o1, o2) -> {
                if(o1.getModifyTime() > o2.getModifyTime()){
                    return -1;
                }else if(o1.getModifyTime() < o2.getModifyTime()){
                    return 1;
                }
                return 0;
            });
        }

        files.addAll(source);
        adapter.refreshFileList(files);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_picker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.back_home:
                currentPath = Const.ROOT_PATH;
                initData();
                break;
            case R.id.sort_name_asc:
                initDataSortByName(true);
                break;
            case R.id.sort_time_asc:
                initDataSortByModifyTime(true);
                break;

            case R.id.sort_name_desc:
                initDataSortByName(false);
                break;
            case R.id.sort_time_desc:
                initDataSortByModifyTime(false);
                break;
            default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClicked(LocalFile file) {
        if(file.getType().equals(FileTyp.FOLDER)|| file.getType().equals(FileTyp.BACK)){
            String filePath = file.getFilePath();
            currentPath = filePath;
            initData();
        }else {
            //保存访问路径
            DBTools.getInstance().saveRecentVisitPath(file.getFilePath());
            //选择的是文件
            Intent intent = new Intent();
            intent.putExtra("filePath",file.getFilePath());
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){//返回键
            if(currentPath.equals(Const.ROOT_PATH)){
                finish();
            }else {
                currentPath = new File(currentPath).getParent();
                initData();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
