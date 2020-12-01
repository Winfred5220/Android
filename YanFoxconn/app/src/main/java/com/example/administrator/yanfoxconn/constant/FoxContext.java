package com.example.administrator.yanfoxconn.constant;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.baidu.location.BDLocation;
import com.example.administrator.yanfoxconn.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局變量
 * Created by song on 2017/9/4.
 */

public class FoxContext {
    private final Context appContext;
    private static FoxContext sInstance;

    private String[] types= new String[]{"A", "B","H","K","I","L","J","M","T"};//所有巡檢類型
    private String[] typesName = new String[]{"安全部值星","安全1部值星","安全2部值星","安全3部值星","待定","碼頭出貨"};//巡檢名稱
    private int[] icon = new int[]{R.mipmap.icon_anquan,R.mipmap.icon_lianluo,R.mipmap.icon_guanai,R.mipmap.icon_anquan,R.mipmap.icon_guanai,R.mipmap.icon_guanai};//icon

    private BDLocation location = null ;//GPS獲取定位
    private String type ="";//巡檢類型,登陸后在類型選擇界面選取
    private String roles = "";//用戶所有巡檢類型
    private String name ="";//用戶名稱
    private String dep="";//司機車牌
    private String loginId = "";//用戶工號
    private String contact = "";//用戶聯繫方式
    private boolean isUpdate = false;//是否更新
    private String needFlag="";//是否需要維護巡檢人
    private String needResult = "";//是否已維護過巡檢人
    private String logDate = "";//行車日誌,選擇的日期 0:今日,-1:昨日
    private String logDateTime="";//行車日誌,選擇的日期
    private int groupPosition = 0;//二级菜单父级
    private String takePic = "";//及時拍照
    private String gName = "";//总务临时工 课组

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public Context getContext() {
        return appContext;
    }
    public BDLocation getLocation() {
        return location;
    }

    public void setLocation(BDLocation location) {
        this.location = location;
    }

    public FoxContext(Context appContext) {
        this.appContext = appContext.getApplicationContext();

    }

    public synchronized static final void init(Context context) {
        if (sInstance == null) {
            sInstance = new FoxContext(context.getApplicationContext());
        }
    }

    public static final FoxContext getInstance() {
        return sInstance;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String[] getTypes() {
        return types;
    }

//    public int[] getColors() {
//        return colors;
//    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public int[] getIcon() {
        return icon;
    }

    public void setIcon(int[] icon) {
        this.icon = icon;
    }

    public String[] getTypesName() {
        return typesName;
    }

    public String getNeedFlag() {
        return needFlag;
    }

    public void setNeedFlag(String needFlag) {
        this.needFlag = needFlag;
    }

    public String getNeedResult() {
        return needResult;
    }

    public void setNeedResult(String needResult) {
        this.needResult = needResult;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(String logDateTime) {
        this.logDateTime = logDateTime;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    public String getTakePic() {
        return takePic;
    }

    public void setTakePic(String takePic) {
        this.takePic = takePic;
    }
}
