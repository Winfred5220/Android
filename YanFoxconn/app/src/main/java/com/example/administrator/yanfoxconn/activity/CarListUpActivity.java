package com.example.administrator.yanfoxconn.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.bean.CarPictureMessage;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.constant.Constants;
import com.example.administrator.yanfoxconn.constant.ImageCaptureManager;
import com.example.administrator.yanfoxconn.intent.PhotoPickerIntent;
import com.example.administrator.yanfoxconn.intent.PhotoPreviewIntent;
import com.example.administrator.yanfoxconn.utils.BaseActivity;
import com.example.administrator.yanfoxconn.utils.FileUtil;
import com.example.administrator.yanfoxconn.utils.HttpConnectionUtil;
import com.example.administrator.yanfoxconn.utils.ImageZipUtils;
import com.example.administrator.yanfoxconn.utils.ToastUtils;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

/**
 * 碼頭放行 上傳照片
 * Created by song on 2018/5/28.
 */

public class CarListUpActivity extends BaseActivity implements View.OnClickListener {
    private final int MESSAGE_TOAST = 2;
    private final int MESSAGE_SET_TEXT = 1;//tv賦值

    private static final int REQUEST_CAMERA_CODE_ONE = 111;
    private static final int REQUEST_PREVIEW_CODE_ONE = 221;
    private static final int REQUEST_CAMERA_CODE_TWO = 112;
    private static final int REQUEST_PREVIEW_CODE_TWO = 222;
    private static final int REQUEST_CAMERA_CODE_THREE = 113;
    private static final int REQUEST_PREVIEW_CODE_THREE = 223;
    private static final int REQUEST_CAMERA_CODE_FOUR = 114;
    private static final int REQUEST_PREVIEW_CODE_FOUR = 224;
    private static final int REQUEST_CAMERA_CODE_FIVE = 115;
    private static final int REQUEST_PREVIEW_CODE_FIVE = 225;
    private static final int REQUEST_CAMERA_CODE_SIX = 116;
    private static final int REQUEST_PREVIEW_CODE_SIX = 226;

    private ArrayList<String> imagePathsOne = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsOne = new ArrayList<String>();//圖片壓縮后地址

    private ArrayList<String> imagePathsTwo = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsTwo = new ArrayList<String>();//圖片壓縮后地址

    private ArrayList<String> imagePathsThree = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsThree = new ArrayList<String>();//圖片壓縮后地址

    private ArrayList<String> imagePathsFour = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsFour = new ArrayList<String>();//圖片壓縮后地址

    private ArrayList<String> imagePathsFive = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsFive = new ArrayList<String>();//圖片壓縮后地址

    private ArrayList<String> imagePathsSix = null;//圖片未壓縮地址
    private ArrayList<String> imgZipPathsSix = new ArrayList<String>();//圖片壓縮后地址

    private ImageCaptureManager captureManager; // 相机拍照处理类

    @BindView(R.id.tv_title)
    TextView tvTitle;//標題
    @BindView(R.id.btn_title_left)
    Button btnBack;//返回
    @BindView(R.id.btn_title_right)
    Button btnUp;//提交

    @BindView(R.id.tv_file_one)
    TextView tvFileOne;
    @BindView(R.id.iv_empty1)
    ImageView ivEmptyOne;//空白图片占位
    @BindView(R.id.gv_photo1)
    GridView gridViewOne;//图片显示区域

    @BindView(R.id.tv_file_two)
    TextView tvFileTwo;
    @BindView(R.id.iv_empty2)
    ImageView ivEmptyTwo;//空白图片占位
    @BindView(R.id.gv_photo2)
    GridView gridViewTwo;//图片显示区域

    @BindView(R.id.tv_file_three)
    TextView tvFileThree;
    @BindView(R.id.iv_empty3)
    ImageView ivEmptyThree;//空白图片占位
    @BindView(R.id.gv_photo3)
    GridView gridViewThree;//图片显示区域

    @BindView(R.id.tv_file_four)
    TextView tvFileFour;
    @BindView(R.id.iv_empty4)
    ImageView ivEmptyFour;//空白图片占位
    @BindView(R.id.gv_photo4)
    GridView gridViewFour;//图片显示区域

    @BindView(R.id.tv_file_five)
    TextView tvFileFive;
    @BindView(R.id.iv_empty5)
    ImageView ivEmptyFive;//空白图片占位
    @BindView(R.id.gv_photo5)
    GridView gridViewFive;//图片显示区域

    @BindView(R.id.tv_file_six)
    TextView tvFileSix;
    @BindView(R.id.iv_empty6)
    ImageView ivEmptySix;//空白图片占位
    @BindView(R.id.gv_photo6)
    GridView gridViewSix;//图片显示区域

    private GridAdapter gridAdapterOne;
    private GridAdapter gridAdapterTwo;
    private GridAdapter gridAdapterThree;
    private GridAdapter gridAdapterFour;
    private GridAdapter gridAdapterFive;
    private GridAdapter gridAdapterSix;
    private String packingNo;//銷單號

    private List<CarPictureMessage> carPictureMessage;

    @Optional
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_up);

        ButterKnife.bind(this);

        tvTitle.setText("派車單信息");
        btnBack.setOnClickListener(this);
        ivEmptyOne.setOnClickListener(this);
        ivEmptyTwo.setOnClickListener(this);
        ivEmptyThree.setOnClickListener(this);
        ivEmptyFour.setOnClickListener(this);
        ivEmptyFive.setOnClickListener(this);
        ivEmptySix.setOnClickListener(this);
        btnUp.setVisibility(View.VISIBLE);
        btnUp.setText("提交");
        btnUp.setOnClickListener(this);

        packingNo = getIntent().getExtras().getString("packingNo");
        carPictureMessage = (ArrayList<CarPictureMessage>) getIntent().getSerializableExtra("list");
        setTitle(carPictureMessage);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridViewOne.setNumColumns(cols);
        gridViewTwo.setNumColumns(cols);
        gridViewThree.setNumColumns(cols);
        gridViewFour.setNumColumns(cols);
        gridViewFive.setNumColumns(cols);
        gridViewSix.setNumColumns(cols);

        // preview
        gridViewOne.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePathsOne.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarListUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePathsOne); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE_ONE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarListUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePathsOne);

                    startActivityForResult(intent, REQUEST_PREVIEW_CODE_ONE);
                }
            }
        });
        gridViewTwo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePathsTwo.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarListUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePathsTwo); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE_TWO);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarListUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePathsTwo);

                    startActivityForResult(intent, REQUEST_PREVIEW_CODE_TWO);
                }
            }
        });
        gridViewThree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePathsThree.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarListUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePathsThree); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE_THREE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarListUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePathsThree);

                    startActivityForResult(intent, REQUEST_PREVIEW_CODE_THREE);
                }
            }
        });
        gridViewFour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePathsFour.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarListUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePathsFour); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE_FOUR);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarListUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePathsFour);

                    startActivityForResult(intent, REQUEST_PREVIEW_CODE_FOUR);
                }
            }
        });
        gridViewFive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePathsFive.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarListUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePathsFive); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE_FIVE);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarListUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePathsFive);

                    startActivityForResult(intent, REQUEST_PREVIEW_CODE_FIVE);
                }
            }
        });
        gridViewSix.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imagePathsSix.size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(CarListUpActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(1); // 最多选择照片数量，默认为1
                    intent.setSelectedPaths(imagePathsSix); // 已选中的照片地址， 用于回显选中状态

                    startActivityForResult(intent, REQUEST_CAMERA_CODE_SIX);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(CarListUpActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePathsSix);

                    startActivityForResult(intent, REQUEST_PREVIEW_CODE_SIX);
                }
            }
        });
    }

    private void setTitle(List<CarPictureMessage> list) {
        for (int i = 0; i < list.size(); i++) {
            Log.e("-----------", list.get(i).getFilepath());
            Log.e("-----------", list.get(i).getLogopath());
            Log.e("-----------", list.get(i).getShifengpath());
            Log.e("-----------", list.get(i).getRemark9());
            Log.e("-----------", list.get(i).getRemark10());
            Log.e("-----------", list.get(i).getRemark8());

        if (!list.get(i).getFilepath().equals("")) {
            tvFileOne.setText("開箱照片 已有照片,繼續上傳會覆蓋!!!");
        }
        if (!list.get(i).getLogopath().equals("")) {
            tvFileTwo.setText("關箱照片 已有照片,繼續上傳會覆蓋!!!");
        }
        if (!list.get(i).getShifengpath().equals("")) {
            tvFileThree.setText("鉛封照片 已有照片,繼續上傳會覆蓋!!!");
        }
        if (!list.get(i).getRemark9().equals("")) {
            tvFileFour.setText("派車單 已有照片,繼續上傳會覆蓋!!!");
        }
        if (!list.get(i).getRemark10().equals("")) {
            tvFileFive.setText("設備交接單 已有照片,繼續上傳會覆蓋!!!");
        }
        if (!list.get(i).getRemark8().equals("")) {
            tvFileSix.setText("三角木 已有照片,繼續上傳會覆蓋!!!");
        }
        }
    }

    //照片上傳 返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE_ONE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_ONE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE_TWO:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_TWO:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE_THREE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_THREE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE_FOUR:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_FOUR:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE_FIVE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_FIVE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE_SIX:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT), requestCode);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE_SIX:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT), requestCode);
                    break;

                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if (captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();

                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdpater(paths, requestCode);
                        Log.e("==========", paths.get(1));
                    }
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths, int code) {
        switch (code) {
            case REQUEST_CAMERA_CODE_ONE:
                loadOne(paths);
                break;
            case REQUEST_PREVIEW_CODE_ONE:
                loadOne(paths);
                break;
            case REQUEST_CAMERA_CODE_TWO:
                loadTwo(paths);
                break;
            case REQUEST_PREVIEW_CODE_TWO:
                loadTwo(paths);
                break;
            case REQUEST_CAMERA_CODE_THREE:
                loadThree(paths);
                break;
            case REQUEST_PREVIEW_CODE_THREE:
                loadThree(paths);
                break;
            case REQUEST_CAMERA_CODE_FOUR:
                loadFour(paths);
                break;
            case REQUEST_PREVIEW_CODE_FOUR:
                loadFour(paths);
                break;
            case REQUEST_CAMERA_CODE_FIVE:
                loadFive(paths);
                break;
            case REQUEST_PREVIEW_CODE_FIVE:
                loadFive(paths);
                break;
            case REQUEST_CAMERA_CODE_SIX:
                loadSix(paths);
                break;
            case REQUEST_PREVIEW_CODE_SIX:
                loadSix(paths);
                break;
        }

    }

    private void loadOne(ArrayList<String> paths) {
        if (imagePathsOne == null) {
            imagePathsOne = new ArrayList<>();
        }
        imagePathsOne.clear();
        imagePathsOne.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyOne.setVisibility(View.GONE);
        } else {
            ivEmptyOne.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsOne);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterOne == null) {
            gridAdapterOne = new GridAdapter(imagePathsOne);
            gridViewOne.setAdapter(gridAdapterOne);
            Log.e("----------------", "ddd==" + imagePathsOne.size());
        } else {
            gridAdapterOne.notifyDataSetChanged();
        }
    }

    private void loadTwo(ArrayList<String> paths) {
        if (imagePathsTwo == null) {
            imagePathsTwo = new ArrayList<>();
        }
        imagePathsTwo.clear();
        imagePathsTwo.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyTwo.setVisibility(View.GONE);
        } else {
            ivEmptyTwo.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsTwo);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterTwo == null) {
            gridAdapterTwo = new GridAdapter(imagePathsTwo);
            gridViewTwo.setAdapter(gridAdapterTwo);
            Log.e("----------------", "ddd==" + imagePathsTwo.size());
        } else {
            gridAdapterTwo.notifyDataSetChanged();
        }
    }

    private void loadThree(ArrayList<String> paths) {
        if (imagePathsThree == null) {
            imagePathsThree = new ArrayList<>();
        }
        imagePathsThree.clear();
        imagePathsThree.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyThree.setVisibility(View.GONE);
        } else {
            ivEmptyThree.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsThree);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterThree == null) {
            gridAdapterThree = new GridAdapter(imagePathsThree);
            gridViewThree.setAdapter(gridAdapterThree);
            Log.e("----------------", "ddd==" + imagePathsThree.size());
        } else {
            gridAdapterThree.notifyDataSetChanged();
        }
    }

    private void loadFour(ArrayList<String> paths) {
        if (imagePathsFour == null) {
            imagePathsFour = new ArrayList<>();
        }
        imagePathsFour.clear();
        imagePathsFour.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyFour.setVisibility(View.GONE);
        } else {
            ivEmptyFour.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsFour);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterFour == null) {
            gridAdapterFour = new GridAdapter(imagePathsFour);
            gridViewFour.setAdapter(gridAdapterFour);
            Log.e("----------------", "ddd==" + imagePathsFour.size());
        } else {
            gridAdapterFour.notifyDataSetChanged();
        }
    }

    private void loadFive(ArrayList<String> paths) {
        if (imagePathsFive == null) {
            imagePathsFive = new ArrayList<>();
        }
        imagePathsFive.clear();
        imagePathsFive.addAll(paths);

        if (paths.size() > 0) {
            ivEmptyFive.setVisibility(View.GONE);
        } else {
            ivEmptyFive.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsFive);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterFive == null) {
            gridAdapterFive = new GridAdapter(imagePathsFive);
            gridViewFive.setAdapter(gridAdapterFive);
            Log.e("----------------", "ddd==" + imagePathsFive.size());
        } else {
            gridAdapterFive.notifyDataSetChanged();
        }
    }

    private void loadSix(ArrayList<String> paths) {
        if (imagePathsSix == null) {
            imagePathsSix = new ArrayList<>();
        }
        imagePathsSix.clear();
        imagePathsSix.addAll(paths);

        if (paths.size() > 0) {
            ivEmptySix.setVisibility(View.GONE);
        } else {
            ivEmptySix.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray obj = new JSONArray(imagePathsSix);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapterSix == null) {
            gridAdapterSix = new GridAdapter(imagePathsSix);
            gridViewSix.setAdapter(gridAdapterSix);
            Log.e("----------------", "ddd==" + imagePathsSix.size());
        } else {
            gridAdapterSix.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_left:
                finish();
                break;
            case R.id.iv_empty1:
                PhotoPickerIntent intent = new PhotoPickerIntent(CarListUpActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePathsOne); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent, REQUEST_CAMERA_CODE_ONE);
                break;
            case R.id.iv_empty2:
                PhotoPickerIntent intent2 = new PhotoPickerIntent(CarListUpActivity.this);
                intent2.setSelectModel(SelectModel.MULTI);
                intent2.setShowCarema(true); // 是否显示拍照
                intent2.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent2.setSelectedPaths(imagePathsTwo); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent2, REQUEST_CAMERA_CODE_TWO);
                break;
            case R.id.iv_empty3:
                PhotoPickerIntent intent3 = new PhotoPickerIntent(CarListUpActivity.this);
                intent3.setSelectModel(SelectModel.MULTI);
                intent3.setShowCarema(true); // 是否显示拍照
                intent3.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent3.setSelectedPaths(imagePathsThree); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent3, REQUEST_CAMERA_CODE_THREE);
                break;
            case R.id.iv_empty4:
                PhotoPickerIntent intent4 = new PhotoPickerIntent(CarListUpActivity.this);
                intent4.setSelectModel(SelectModel.MULTI);
                intent4.setShowCarema(true); // 是否显示拍照
                intent4.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent4.setSelectedPaths(imagePathsFour); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent4, REQUEST_CAMERA_CODE_FOUR);
                break;
            case R.id.iv_empty5:
                PhotoPickerIntent intent5 = new PhotoPickerIntent(CarListUpActivity.this);
                intent5.setSelectModel(SelectModel.MULTI);
                intent5.setShowCarema(true); // 是否显示拍照
                intent5.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent5.setSelectedPaths(imagePathsFive); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent5, REQUEST_CAMERA_CODE_FIVE);
                break;
            case R.id.iv_empty6:
                PhotoPickerIntent intent6 = new PhotoPickerIntent(CarListUpActivity.this);
                intent6.setSelectModel(SelectModel.MULTI);
                intent6.setShowCarema(true); // 是否显示拍照
                intent6.setMaxTotal(1); // 最多选择照片数量，默认为3
                intent6.setSelectedPaths(imagePathsSix); // 已选中的照片地址， 用于回显选中状态

                startActivityForResult(intent6, REQUEST_CAMERA_CODE_SIX);
                break;
            case R.id.btn_title_right://提交
                Log.e("-------", "btn_title_right");
                if (imagePathsOne == null && imagePathsTwo == null && imagePathsThree == null && imagePathsFour == null && imagePathsFive == null && imagePathsSix == null) {
                    ToastUtils.showLong(this, "至少選擇一張圖片才能提交,否則請返回!!!");
                } else {
                    normal2();
                }
                break;
        }
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            if (listUrls.size() == 1) {
                return 1;
            } else {
                return listUrls.size() + 1;
            }

        }

        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(imageView);
                // 重置ImageView宽高
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
//                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }

            if (position == listUrls.size()) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_addpic_unfocused));
                if (position == 1) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                Glide.with(CarListUpActivity.this)
                        .load(new File(getItem(position)))
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);
            }
            return convertView;
        }
    }

    public void normal2() {

        final String url = Constants.HTTP_CARPICTURE_UP_SERVLET; //此處寫上自己的URL

        final Map<String, String> paramMap = new HashMap<String, String>(); //文本資料全部添加到Map裡

        final Map<String, File> fileMap = new HashMap<String, File>(); //檔全部添加到Map裡

        String[] b = packingNo.toString().split(" ");

        paramMap.put("packing_no", b[0]);
        Log.e("--------","packing_no="+b[0]);


        if (imagePathsOne != null) {
            for (int i = 0; i < imagePathsOne.size(); i++) {
                File pictureFile = new File(imagePathsOne.get(i)); //通過路徑獲取檔
                final String pic_path = imagePathsOne.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                final File compressedPic = new File(compressImage);
                imgZipPathsOne.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
                    fileMap.put("file" + (i + 1), compressedPic);//添加第一個文件
                } else {//直接上传
                    fileMap.put("file" + (i + 1), pictureFile);
                }
            }
        }
        if (imagePathsTwo != null) {
            for (int i = 0; i < imagePathsTwo.size(); i++) {
                File pictureFile = new File(imagePathsTwo.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
                final String pic_path = imagePathsTwo.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                //getFilesDir().getAbsolutePath()+"compressPic.jpg";
                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                final File compressedPic = new File(compressImage);
                imgZipPathsTwo.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
                    fileMap.put("file" + (i + 2), compressedPic);//添加第一個文件
                } else {//直接上传
                    fileMap.put("file" + (i + 2), pictureFile);
                }
            }
        }
        if (imagePathsThree != null) {
            for (int i = 0; i < imagePathsThree.size(); i++) {
                File pictureFile = new File(imagePathsThree.get(i)); //通過路徑獲取檔
                final String pic_path = imagePathsThree.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                final File compressedPic = new File(compressImage);
                imgZipPathsThree.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
                    fileMap.put("file" + (i + 3), compressedPic);//添加第一個文件
                } else {//直接上传
                    fileMap.put("file" + (i + 3), pictureFile);
                }
            }
        }
        if (imagePathsFour != null) {
            for (int i = 0; i < imagePathsFour.size(); i++) {
                File pictureFile = new File(imagePathsFour.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
                final String pic_path = imagePathsFour.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                //getFilesDir().getAbsolutePath()+"compressPic.jpg";
                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                final File compressedPic = new File(compressImage);
                imgZipPathsFour.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
                    fileMap.put("file" + (i + 4), compressedPic);//添加第一個文件

                } else {//直接上传
                    fileMap.put("file" + (i + 4), pictureFile);
                }
            }
        }
        if (imagePathsFive != null) {
            for (int i = 0; i < imagePathsFive.size(); i++) {
                File pictureFile = new File(imagePathsFive.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
                final String pic_path = imagePathsFive.get(i);

                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";
                //getFilesDir().getAbsolutePath()+"compressPic.jpg";
                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                final File compressedPic = new File(compressImage);
                imgZipPathsFive.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
                    fileMap.put("file" + (i + 5), compressedPic);//添加第一個文件

                } else {//直接上传
                    fileMap.put("file" + (i + 5), pictureFile);
                }
            }
        }
        if (imagePathsSix != null) {
            for (int i = 0; i < imagePathsSix.size(); i++) {
                File pictureFile = new File(imagePathsSix.get(i)); //通過路徑獲取檔
//            fileMap.put("file" + i, pictureFile);//添加第一個文件
                final String pic_path = imagePathsSix.get(i);
                String sign_dir = Environment.getExternalStorageDirectory() + File.separator + "YanFoxconn" + File.separator + "Photos";
                String _path =  sign_dir + File.separator  + System.currentTimeMillis() +i+ ".jpg";

                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = ImageZipUtils.compressImage(pic_path, _path, 30);
                final File compressedPic = new File(compressImage);

                imgZipPathsSix.add(i, compressImage);
                Log.e("----com---", compressImage);
                if (compressedPic.exists()) {
                    Log.e("-------------", "图片压缩上传");
                    fileMap.put("file" + (i + 6), compressedPic);//添加第一個文件

                } else {//直接上传
                    fileMap.put("file" + (i + 6), pictureFile);
                }
            }
        }

        //開啟一個新執行緒，向伺服器上傳資料
        new Thread() {
            public void run() {
                try {
                    showDialog();
                    HttpURLConnection b = HttpConnectionUtil.doPostPicture(url, paramMap, fileMap);
                    Log.e("---------", "==fff===" + url);
                    if (b != null) {
                        dismissDialog();
                        Log.e("---------", "==fff===" + b);
                        if (b.getResponseCode() == 200) {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                            finish();
                        } else {
                            Message message = new Message();
                            message.what = MESSAGE_TOAST;
                            message.obj = b.getResponseMessage();
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //此刪除圖片方法有用，2020/12/18碼頭王浩，說要保留照片
//                    FileUtil.deletePhotos(CarListUpActivity.this);
                }
            }
        }.start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_TOAST://Toast彈出
                    ToastUtils.showLong(CarListUpActivity.this, msg.obj.toString());

                    break;
                case MESSAGE_SET_TEXT://text賦值
//                        setText();
                    break;

            }
            super.handleMessage(msg);
        }
    };


}
