package com.example.administrator.yanfoxconn.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.yanfoxconn.R;
import com.example.administrator.yanfoxconn.activity.TimeDatePickerDialog;
import com.example.administrator.yanfoxconn.bean.SelectModel;
import com.example.administrator.yanfoxconn.bean.ZhiyinshuiCheckMsg;
import com.example.administrator.yanfoxconn.intent.PhotoPickerIntent;
import com.example.administrator.yanfoxconn.intent.PhotoPreviewIntent;
import com.example.administrator.yanfoxconn.utils.ToastUtils;
import com.example.administrator.yanfoxconn.widget.MyGridView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static androidx.core.app.ActivityCompat.startActivityForResult;

/**
 * Created by S1007989 on 2020/7/26
 * description：車調車輛點檢項目點檢共用
 */
public class CarSystemCheckAdapter extends RecyclerView.Adapter<CarSystemCheckAdapter.ViewHolder> {
    private List<ZhiyinshuiCheckMsg> lists;
    private static HashMap<Integer, String> isSelected; //存放checkBox状态的map
    private static HashMap<Integer, ArrayList<String>> imagePathsMap=new HashMap<>(); //存放圖片地址的map
    private static HashMap<Integer, GridAdapter> gridMap = new HashMap<>(); //存放GridAdapter的map
    private static HashMap<Integer, String> etMap = new HashMap<>();//存放editText值的map
    private static HashMap<Integer, String> etCheckMap = new HashMap<>();//存放editText值的map

    public static Context mContext;
    private static int pos;
    public boolean isChange;
    private static final int REQUEST_CAMERA_CODE = 11;
    private static final int REQUEST_PREVIEW_CODE = 22;
    ImageView hivEmpty;
    GridView hgvPhoto;
    static TextView htvDate;

    private String nowDateTime;//获取当前时间
    private String changeDate;//改變的時間,同時用於設置tvDate
    private static Calendar noChangeTime = Calendar.getInstance();
    private Date selectTime = null;//所選時間
    private Date nowTime = null;//所選時間
    static TimeDatePickerDialog timeDatePickerDialog;
    private static SimpleDateFormat formatter;
    private SimpleDateFormat formattery = new SimpleDateFormat("yyyy");
    private SimpleDateFormat formatterm = new SimpleDateFormat("MM");
    private SimpleDateFormat formatterd = new SimpleDateFormat("dd");
    private static int mYear, mMonth, mDay;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvItem,tvContent,tvDate;//序號 部位 檢查項目
        RadioButton rbTure,rbFalse;//單選
        EditText etAbnormal,etInput;// 填寫信息
        LinearLayout llAbnormal,llSigle;//異常信息
        ImageView ivEmpty;//空白图片占位
        MyGridView gvPhoto;//图片显示区域
        Spinner spSelect;//下拉選擇

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llSigle = itemView.findViewById(R.id.ll_sigle);
            spSelect = itemView.findViewById(R.id.sp_select);
            etInput= itemView.findViewById(R.id.et_input);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvItem = (TextView) itemView.findViewById(R.id.tv_item);
            tvContent = itemView.findViewById(R.id.tv_content);
            llAbnormal = itemView.findViewById(R.id.ll_abnormal);
            rbTure = itemView.findViewById(R.id.rb_true);
            rbFalse = itemView.findViewById(R.id.rb_false);
            etAbnormal = itemView.findViewById(R.id.et_abnormal);
            ivEmpty = itemView.findViewById(R.id.iv_empty);
            gvPhoto = itemView.findViewById(R.id.gv_photo);

        }

    }
    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < lists.size(); i++) {
            isSelected.put(i, "true");
        }
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        noChangeTime = Calendar.getInstance();
        nowTime = new Date(System.currentTimeMillis());
        nowDateTime = formatter.format(noChangeTime.getTime());
        mYear = Integer.parseInt(formattery.format(noChangeTime.getTime()));
        mMonth = Integer.parseInt(formatterm.format(noChangeTime.getTime()));
        mDay = Integer.parseInt(formatterd.format(noChangeTime.getTime()));
    }
    public CarSystemCheckAdapter(Context context, List<ZhiyinshuiCheckMsg> lists, HashMap<Integer, String> isSelected){
        this.lists=lists;
        this.isSelected=isSelected;
        this.mContext = context;
        initDate();
    }

    /**
     * 重写 避免滑动过程界面混乱
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_zhiyinshui_check,parent,false);
        ViewHolder holder = new ViewHolder(view);

        holder.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                htvDate=holder.tvDate;
                timeDatePickerDialog = new TimeDatePickerDialog(mContext,mYear, mMonth-1, mDay);
                timeDatePickerDialog.showDatePickerDialog();
            }
        });

        //異常輸入框
        holder.etAbnormal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.etAbnormal.setFocusable(true);
                holder.etAbnormal.setFocusableInTouchMode(true);
                holder.etAbnormal.requestFocus();
            }
        });
        //異常輸入框
        holder.etAbnormal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int position = holder.getAdapterPosition();
                etMap.put(position,holder.etAbnormal.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //點檢內容輸入框
        holder.etInput.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.etInput.setFocusable(true);
                holder.etInput.setFocusableInTouchMode(true);
                holder.etInput.requestFocus();
            }
        });
        //點檢內容輸入框
        holder.etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int position = holder.getAdapterPosition();
                //輸入文字
                etCheckMap.put(position,holder.etInput.getText().toString());
                isSelected.put(position, "input_true");

                if (lists.get(position).getFlag().equals("Y")&&!holder.etInput.getText().toString().equals("")) {
                    //365
                    float temp = Float.parseFloat(holder.etInput.getText().toString());
                    Log.e("--------", temp+"="+Float.parseFloat(lists.get(position).getMin())+"="+Float.parseFloat(lists.get(position).getMax())  );
                        if (temp > Float.parseFloat(lists.get(position).getMax()) ) {
                            ToastUtils.showShort(mContext,"體溫高于最大值");
                            holder.etInput.setTextColor(mContext.getResources().getColor(R.color.color_d73d19));
                            isSelected.put(position, "input_false");
                        }else if (temp < Float.parseFloat(lists.get(position).getMin())){
                            ToastUtils.showShort(mContext,"體溫低于最小值");
                            holder.etInput.setTextColor(mContext.getResources().getColor(R.color.color_d73d19));
                            isSelected.put(position, "input_false");
                        }else {
                            holder.etInput.setTextColor(mContext.getResources().getColor(R.color.blank));
                            isSelected.put(position, "input_true");
                        }
                    }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //日期選擇
        holder.tvDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int position = holder.getAdapterPosition();
                isSelected.put(position, "input_true");
                etCheckMap.put(position,holder.tvDate.getText().toString());

                if (lists.get(position).getFlag().equals("Y")){
                    if (holder.tvDate.getText().toString().compareTo(lists.get(position).getMax())>0||
                            holder.tvDate.getText().toString().compareTo(lists.get(position).getMin())<0){
                        isSelected.put(position, "input_false");
                    }else {
                        isSelected.put(position, "input_true");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        holder.rbTure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int position = holder.getAdapterPosition();
                hivEmpty = holder.ivEmpty;
                hgvPhoto = holder.gvPhoto;

                if (isChange) {
                if (isSelected.get(position).equals("true")) {
                    holder.llAbnormal.setVisibility(View.VISIBLE);
                    isSelected.put(position, "false");
                    setIsSelected(isSelected);

                } else {
                    holder.llAbnormal.setVisibility(View.GONE);
//                        holder.etAbnormal.setVisibility(View.GONE);
                    if (imagePathsMap.get(position)!=null){
                        imagePathsMap.get(position).clear();
                        gridMap.put(position,null);
                        loadAdpater(imagePathsMap.get(position),position);
                    }

                    holder.etAbnormal.setText("");
                    etMap.put(position,holder.etAbnormal.getText().toString());
                    isSelected.put(position, "true");
                    setIsSelected(isSelected);

                }
                }
            }
        });

        holder.ivEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                hivEmpty = holder.ivEmpty;
                hgvPhoto = holder.gvPhoto;
                setPosition(position);
                Log.e("-----------","imageViewposition=="+position);
                PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                intent.setSelectedPaths(imagePathsMap.get(position)); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult((Activity) mContext,intent, REQUEST_CAMERA_CODE,null);
            }
        });

        int cols = mContext.getResources().getDisplayMetrics().widthPixels / mContext.getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        holder.gvPhoto.setNumColumns(cols);

        holder.gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                int position = holder.getAdapterPosition();
                setPosition(position);
                hivEmpty = holder.ivEmpty;
                hgvPhoto = holder.gvPhoto;
                Log.e("-----------","gridViewposition=="+position);
                if (pos == imagePathsMap.get(position).size()) {
                    PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(3); // 最多选择照片数量，默认为3
                    intent.setSelectedPaths(imagePathsMap.get(position)); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult((Activity) mContext,intent, REQUEST_CAMERA_CODE,null);
                } else {
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(mContext);
                    intent.setCurrentItem(pos);
                    intent.setPhotoPaths(imagePathsMap.get(position));
                    startActivityForResult((Activity) mContext,intent, REQUEST_PREVIEW_CODE,null);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int num=position+1;
        ZhiyinshuiCheckMsg zhiyinshuiCheckMsg = lists.get(position);
        holder.tvItem.setText(num+"");
        holder.tvContent.setText(zhiyinshuiCheckMsg.getContent());
       if (zhiyinshuiCheckMsg.getType().equals("single")){
           holder.spSelect.setVisibility(View.GONE);
           holder.etInput.setVisibility(View.GONE);
           holder.tvDate.setVisibility(View.GONE);
           // 根据isSelected来设置checkbox的选中状况
           isChange = false;
           holder.rbTure.setChecked(true);
           if (zhiyinshuiCheckMsg.getOption().get(0).getFlag().equals("0")){
               holder.rbTure.setText(zhiyinshuiCheckMsg.getOption().get(0).getOpt());
               holder.rbFalse.setText(zhiyinshuiCheckMsg.getOption().get(1).getOpt());
           }else{
               holder.rbTure.setText(zhiyinshuiCheckMsg.getOption().get(1).getOpt());
               holder.rbFalse.setText(zhiyinshuiCheckMsg.getOption().get(0).getOpt());
           }
           isChange = true;

       }else if(zhiyinshuiCheckMsg.getType().equals("input")){
           isSelected.put(position, "input_true");
           holder.llSigle.setVisibility(View.GONE);
           holder.spSelect.setVisibility(View.GONE);
           if (zhiyinshuiCheckMsg.getOption().get(0).getOpt().equals("文字")){
               holder.tvDate.setVisibility(View.GONE);
               holder.etInput.setHint("請輸入文字");
               holder.etInput.setInputType(InputType.TYPE_CLASS_TEXT);
           }else if (zhiyinshuiCheckMsg.getOption().get(0).getOpt().equals("數字")){
               holder.tvDate.setVisibility(View.GONE);
               holder.etInput.setHint("請輸入數字");
               //holder.etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
               DigitsKeyListener numericOnlyListener = new DigitsKeyListener(false,true);
               holder.etInput.setKeyListener(numericOnlyListener);
               holder.etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

               if (zhiyinshuiCheckMsg.getFlag().equals("Y")){
                   holder.tvContent.setText(zhiyinshuiCheckMsg.getContent()+"-界值:("+zhiyinshuiCheckMsg.getMin()+"~"+zhiyinshuiCheckMsg.getMax()+")");
               }
           }else{
               holder.etInput.setVisibility(View.GONE);
               holder.tvDate.setHint("請選擇日期");
               if (zhiyinshuiCheckMsg.getFlag().equals("Y")){
                   holder.tvContent.setText(zhiyinshuiCheckMsg.getContent()+"-界值:("+zhiyinshuiCheckMsg.getMin()+"~"+zhiyinshuiCheckMsg.getMax()+")");
               }
           }

       }else {
           holder.llSigle.setVisibility(View.GONE);
           holder.etInput.setVisibility(View.GONE);
           holder.tvDate.setVisibility(View.GONE);

           List<String> selectData = new ArrayList<>();//下拉列表數據
           List<String> flagData = new ArrayList<>();//下拉列表數據
           if (zhiyinshuiCheckMsg.getOption()!=null&&zhiyinshuiCheckMsg.getOption().size()!=0){
               for (int i=0;i<zhiyinshuiCheckMsg.getOption().size();i++){
                   selectData.add(zhiyinshuiCheckMsg.getOption().get(i).getOpt());
                   flagData.add(zhiyinshuiCheckMsg.getOption().get(i).getFlag());
               }
           }

           //下拉列表選擇
           holder.spSelect.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, selectData));
           holder.spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                   etCheckMap.put(position,selectData.get(pos));
                   if (flagData.get(pos).equals("0")){

                       holder.llAbnormal.setVisibility(View.GONE);
//                        holder.etAbnormal.setVisibility(View.GONE);
                       if (imagePathsMap.get(position)!=null){
                           imagePathsMap.get(position).clear();
                           gridMap.put(position,null);
                           loadAdpater(imagePathsMap.get(position),position);
                       }

                       holder.etAbnormal.setText("");
                       etMap.put(position,holder.etAbnormal.getText().toString());
                       isSelected.put(position, "select_true");
                       setIsSelected(isSelected);
                   }else{
                       holder.llAbnormal.setVisibility(View.VISIBLE);
                       isSelected.put(position, "select_false");
                       setIsSelected(isSelected);
                   }
               }
               @Override
               public void onNothingSelected(AdapterView<?> parent) {
               }
           });

       }

    }

    @Override
    public int getItemCount() {
        Log.e("--------","lists.size()==="+lists.size());
        return lists.size();
    }

    public HashMap<Integer, String> getIsSelected() {
        return isSelected;
    }

    public  void setIsSelected(HashMap<Integer, String> isSelected) {
        this.isSelected = isSelected;
    }

    public static int getPosition() {
        return pos;
    }

    public static void setPosition(int position) {
        CarSystemCheckAdapter.pos = position;
    }

    public static HashMap<Integer, ArrayList<String>> getImagePathsMap() {
        return imagePathsMap;
    }

    public static void setImagePathsMap(HashMap<Integer, ArrayList<String>> imagePathsMap) {
        CarSystemCheckAdapter.imagePathsMap = imagePathsMap;
    }

    public static HashMap<Integer, String> getEtMap() {
        return etMap;
    }

    public static void setEtMap(HashMap<Integer, String> etMap) {
        CarSystemCheckAdapter.etMap = etMap;
    }

    public static HashMap<Integer, String> getEtCheckMap() {
        return etCheckMap;
    }

    public static void setEtCheckMap(HashMap<Integer, String> etCheckMap) {
        CarSystemCheckAdapter.etCheckMap = etCheckMap;
    }

    public void loadAdpater(ArrayList<String> paths, int position) {

        imagePathsMap.put(position,paths);

        Log.e("-----Position----","Position=="+position);
        if (imagePathsMap.get(position).size() > 0) {
            hivEmpty.setVisibility(View.GONE);
        } else {
            hivEmpty.setVisibility(View.VISIBLE);
        }

        gridMap.put(position,new CarSystemCheckAdapter.GridAdapter(imagePathsMap.get(position)));
        hgvPhoto.setAdapter(gridMap.get(position));


    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            if (listUrls.size() == 3) {
                return 3;
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
//                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_image, null);

                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(imageView);
                // 重置ImageView宽高
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
//                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            if (position == listUrls.size()) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_addpic_unfocused));
                if (position == 3) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                Glide.with(mContext)
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

    //时间选择器----------确定
    public static void positiveListener() {
//        mYear = timeDatePickerDialog.getYear();
//        mDay = timeDatePickerDialog.getDay();
//        mMonth = timeDatePickerDialog.getMonth();
        htvDate.setText(timeDatePickerDialog.getDate());
    }

    //时间选择器-------取消
    public static void negativeListener() {
        if (timeDatePickerDialog.getTimeDate().equals("")) {
            ToastUtils.showShort(mContext, "請選擇時間");
        }
    }
}
