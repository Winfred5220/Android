
package com.example.administrator.yanfoxconn.constant;

/**
 * url
 * Created by song on 2017/8/30.
 */

public class Constants {
    public static final int MESSAGE_SET_TEXT = 1;//掃描成功賦值
    public static final int MESSAGE_TOAST = 2;//掃描失敗彈出框
    public static final int MESSAGE_UP = 3;//提交信息回復
    public static final int MESSAGE_SHOW = 4;//顯示提醒
    public static final int MESSAGE_NETMISTAKE = 5;//網絡錯誤
    public static final int MESSAGE_DELETE_SUCCESS = 6;//刪除成功，刷新列表
    public static final int MESSAGE_NOT_NET = 7;//網絡錯誤


//14.19服务器
//    public static final String SAFE_SERVER = "http://ytlogisticgps.foxconn.com/SafeServer/";//玲姐
//    public static final String SERVER = "http://ytlogisticgps.foxconn.com/Server/";    //杰哥
//14.40服务器
    public static final String SAFE_SERVER = "http://ytsafeserver.foxconn.com/SafeServer/";//玲姐
    public static final String SERVER = "http://ytsafeserver.foxconn.com/Server/";    //杰哥
    public static String HTTP_WEBVIEW = "http://ytlogisticgps.foxconn.com/" ;//WEBVIEW
//本地测试服务器
    public static String safeServerlocal = "http://192.168.1.133:8080/SafeServer/";//SafeServer接口測試
    public static String serverlocal = "http://192.168.1.133:8080/Server/";//Server接口測試
    public static String L4 = "http://10.193.22.189:8080/SafeServer/";//玲
    public static final String HTTP_LOGIN = SAFE_SERVER + "LoginServlet";//登錄
    public static String HTTP_DIMEMSION_SERVLET = SAFE_SERVER + "safeCheck/DimemsionServlet";//巡檢進度列表
    public static String HTTP_DIMEMSION_STATE_SERVLET = SAFE_SERVER + "safeCheck/DimemsionStateServlet";//掃描二維碼交互
    public static String HTTP_UPLOAD_FILE_SERVLET = SAFE_SERVER + "safeCheck/UploadFileServlet";//安全部,異常提交
    public static String HTTP_EXCEPTION_VIEW_SERVLET = SAFE_SERVER + "safeCheck/ExceptionViewServlet";//查詢異常列表
    public static String HTTP_EXCEPTION_DELETE_SERVLET = SAFE_SERVER + "safeCheck/ExceptionDeleteServlet";//個人異常刪除
    public static String HTTP_GET_VERSION_CODE = SAFE_SERVER + "VersionServlet";//獲取最新版本信息
    public static String HTTP_EXCEPTION_DETAIL_SERVLET = SAFE_SERVER + "safeCheck/ExceptionDetailServlet";//查詢異常詳情

    public static String HTTP_GET_IMG = SAFE_SERVER + "photos/";//異常圖片查看
    public static String HTTP_GET_APK = SAFE_SERVER + "apk/";//獲取最新版本apk
    public static String HTTP_PATORLMAN_SERVLET = SAFE_SERVER + "PatrolmanServlet";//巡檢人員維護交互
    public static String HTTP_CAR_SCAN_SERVLET = SAFE_SERVER + "CarScanServlet";//碼頭派車單掃描交互
    public static String HTTP_CAR_UPDATE_SERVLET = SAFE_SERVER + "CarUpdateServlet";//碼頭掃描提交交互(文字與圖片)
    public static String HTTP_CAR_RECORD_SELECT = SAFE_SERVER + "CarRecordSelectServlet";//車調日誌 獲取日誌列表
    public static String HTTP_CAR_RECORD_INSERT = SAFE_SERVER + "CarRecordInsertServlet";//車調日誌 上傳行車日誌
    public static String HTTP_CAR_RECORD_VIEW = SAFE_SERVER + "CarRecordViewServlet";//車調日誌 日誌詳情查看
    public static String HTTP_CAR_RECORD_DELETE = SAFE_SERVER + "CarRecordDeleteServlet";//車調日誌 日誌記錄刪除
    public static String HTTP_CAR_LOG_IMG = SAFE_SERVER + "CarRecord/";//車調日誌 圖片查看
    public static String HTTP_CAR_LOG_RETURN = SAFE_SERVER + "CarRecordBackServlet";//車調日誌 退件查看
    public static String HTTP_MENU_SERVLET = SAFE_SERVER + "MenuServlet";//宿舍分類二級菜單,餐飲二級分類
    public static String HTTP_DIMEMSION_NEXT_SERVLET = SAFE_SERVER + "safeCheck/DimemsionNextServlet";//宿舍查詢巡檢點
    public static String HTTP_EXPORTIOUPDATE_SERVLET = SAFE_SERVER + "ExportioupdateServlet";//跨區掃描  提交掃描人工號
    public static String HTTP_EXPORTIO_SERVLET = SAFE_SERVER + "ExportioServlet";//跨區掃描 掃描攜帶人工號
    public static String HTTP_CARVIEW_SERVLET = SAFE_SERVER + "CarviewServlet";//在碼頭車輛交互
    public static String HTTP_CARPICTURE_SERVLET = SAFE_SERVER + "CarpictureServlet";//碼頭車輛是否上傳圖片
    public static String HTTP_CARPICTURE_UP_SERVLET = SAFE_SERVER + "CarpicUploadServlet";//碼頭車輛 上傳圖片
    public static String HTTP_CAR_LEAVE_SERVLET = SAFE_SERVER + "CarLeaveServlet";//碼頭車輛離開（全銷單，維護人）
    public static String HTTP_BARCODE_CREATE_SERVLET = SAFE_SERVER + "BarcodeCreateServlet";//人資活動創建
    public static String HTTP_BARCODE_VIEW_SERVLET = SAFE_SERVER + "BarcodeViewServlet";//人資活動查看
    public static String HTTP_BARCODE_MOIDY_SERVLET = SAFE_SERVER + "BarcodeMoidyServlet";//人資活動取消
    public static String HTTP_BARCODE_SCAN_SERVLET = SAFE_SERVER + "BarcodeScanServlet";//人資活動掃描
    public static String HTTP_BARCODE_JBVIEW_SERVLET = SAFE_SERVER + "BarcodeJbviewServlet";//人資查看加班時數
    public static String HTTP_COMMON_FORMS_UPDATE_SERVLET = SERVER + "CommonFormsupdateServlet";//員工違規表單,員工進出異常表單 提交
    public static String HTTP_COMMON_FORMS_110JQTB_UP_SERVLET = SERVER + "CommonForms110JQTBUpServlet";//110接處警警情通報提交
    public static String HTTP_COMMON_FORMS_110CJFK_LIST_SERVLET = SERVER + "CommonForms110CJFKListServlet";//110接處警處警反饋列表獲取
    public static String HTTP_COMMON_FORMS_110CJFK_SERVLET = SERVER + "CommonForms110CJFKServlet";//110接處警處警基本信息獲取
    public static String HTTP_COMMON_FORMS_110CJFK_UP_SERVLET = SERVER + "CommonForms110CJFKUpServlet";//110接處警處警提交

    public static String HTTP_COMMON_FORMS_PHOTOS_SERVLET = SERVER + "CommonFormsupload_photoservlet";//二輪車違規帶圖片上傳
    public static String HTTP_COMMON_FORMS_TWO_WHEEL_SERVLET = SERVER + "CommonFormsTwoWheelServlet";//二輪車違規工號查詢,並獲取門崗列表
    public static String HTTP_COMMON_FORMS_DAY_RECORD_SERVLET = SERVER + "CommonFormsDayRecordServlet";//二輪車違規查詢當日記錄
    public static String HTTP_COMMON_FORMS_PRIVATE_CAR_SERVLET = SERVER + "CommonFormsPrivateCarUpServlet";//私家車違規稽核提交接口
    public static String HTTP_COMMON_FORMS_PRIVATE_CAR_PARKING = SERVER + "CommonFormsPrivateCarParkingServlet";//私家車違規帶出停車場接口

    public static String HTTP_COMMON_FORMS_SERVLET = SERVER + "CommonFormsServlet";//工號查詢,並獲取門崗列表
    public static String HTTP_COMMON_GOODS_SERVLET = SERVER + "CommongoodsServlet";//普通物品 工號查詢,並獲取門崗列表，提交信息
    public static String HTTP_INTELLIGENCE_GOODS_SERVLET = SERVER + "IntelligenceGoodsServlet";//智慧物品放行 工號查詢,並獲取門崗列表，提交信息接口
    public static String HTTP_SANFANG_SERVLET = SERVER + "SanFangServlet";//三防隱患獲取責任單位接口
    public static String HTTP_SANFANG_UPLOAD_SERVLET = SERVER + "SanFangupload_photoservlet";//安全部三防隱患提交接口

    public static String HTTP_HUB_LOGIN_SERVLET = SAFE_SERVER + "HubSys/HubViewServlet";//HUB倉選擇領用人接口
    public static String HTTP_HUB_UP_SERVLET = SAFE_SERVER + "HubSys/HubUpdateServlet";//HUB倉領取物品提交接口
    public static String HTTP_HUB_AREA_SERVLET = SAFE_SERVER  + "HubSys/HubSearchServlet";//HUB倉選擇區域接口
    public static String HTTP_HUB_RECEIVE_INFO_SERVLET = SAFE_SERVER + "HubSys/HubReciveInfoServlet";//HUB倉簽收查找
    public static String HTTP_HUB_RECEIVE_SERVLET = SAFE_SERVER + "HubSys/HubReciveServlet";//HUB倉簽收OK
    public static String HTTP_HUB_TAKE_VIEW_SERVLET = SAFE_SERVER + "HubSys/HubTakeViewServlet";//HUB簽收查詢
    public static String HTTP_HUB_IMG = SAFE_SERVER + "hubImgs/";//簽名圖片查看
    public static String HTTP_MOBILE_CONTROL_SERVLET = SAFE_SERVER + "Mobile/MobileSearchServlet";//移動上網設備接口
    public static String HTTP_FORKLIFT_MESSAGE_SERVLET = SERVER + "EquipmentCheckServlet";//叉車掃描接口
    public static String HTTP_FORKLIFT_CHECK_SERVLET =  SERVER + "EquipmentServlet";//叉車點檢接口
    public static String HTTP_FORKLIFT_UPDATE_SERVLET = SERVER + "Equipment_updateServlet";//叉車點檢提交接口
    public static String HTTP_FORKLIFT_REPAIR_SERVLET = SERVER + "Equipment_BxServlet";//叉車報修提交接口
    public static String HTTP_FORKLIFT_MAINTENANCE_SERVLET = SERVER + "EquipmentBxviewServlet";//叉車維修接口
    public static String HTTP_FORKLIFT_MAINTENANCE_UPDATE_SERVLET = SERVER + "Equipment_WxServlet";//叉車維修提交接口
    public static String HTTP_EXPORTIO_SERVLET_CHE = SERVER + "ExportioServlet_che";//車輛跨區獲取信息接口
    public static String HTTP_EXPORTIOUPDATE_SERVLET_CHE = SERVER + "ExportioupdateServlet_che";//車輛跨區放行接口
    public static String HTTP_SCRAP_LEAVE_SERVLET = SERVER + "scrapLeaveServlet";//廢料放行接口
    public static String HTTP_SCRAP_LEAVE_UPDATE_SERVLET = SERVER + "scrapLeaveUpdateServlet";//廢料放行提交接口
    public static String HTTP_DUTY_CHIEF_UPDATE_SERVLET = SERVER + "DutyChiefUpdateServlet";//值班科長上傳接口
    public static String HTTP_DUTY_CHIEF_VIEW_SERVLET = SERVER + "DutyChiefViewServlet";//值班科長值班情況異常記錄查詢接口
    public static String HTTP_DUTY_CHIEF_MAIN_UPDATE_SERVLET = SERVER + "DutyChiefMainUpdateServlet";//值班科長值班情況異常記錄上傳接口
    public static String HTTP_DUTY_CHIEF_PROBLEM_UPDATE_SERVLET = SERVER + "DutyChiefProblemUpdateServlet";//值班科長稽核問題上傳接口
    public static String HTTP_DUTY_CHIEF_PROBLEM_SEE_SERVLET = SERVER + "DutyChiefProblemSeeServlet";//值班科長稽核問題查看接口
    public static String HTTP_DUTY_CHIEF_PROBLEM_DEL_SERVLET = SERVER + "DutyChiefProblemDeleteServlet";//值班科長稽核問題刪除接口

    public static String HTTP_TUNGANDPRODUCT_SERVLET = serverlocal + "TungAndProductServlet";//獲取樓棟產品處接口
    public static String HTTP_INDUSSAFE_DEPARTUPDATE_SERVLET = serverlocal + "IndusSafe_departUpdateServlet";//部門基本信息提交接口
    public static String HTTP_INDUSSAFE_DEPARTLISTVIEW_SERVLET = serverlocal + "IndusSafe_departListViewServlet";//獲取部門基本信息列表接口
    public static String HTTP_INDUSSAFE_PERSONUPDATE_SERVLET = serverlocal + "IndusSafe_personUpdateServlet";//巡查人陪查人上傳接口
    public static String HTTP_INDUSSAFE_SUGGESTUPDATE_SERVLET = serverlocal + "IndusSafe_suggestUpdateServlet";//優缺建議上傳接口
    public static String HTTP_INDUSSAFE_DANGER_UPDATE_SERVLET = serverlocal + "IndustrySafetyDangerPhotoServlet";//隱患上傳接口
    public static String HTTP_INDUSSAFE_DANGER_VIEW_SERVLET = serverlocal + "IndusSafe_dangerListViewServlet";//隱患查看接口
    public static String HTTP_INDUSSAFE_DANGER_DELETE_SERVLET = serverlocal + "IndusSafe_dangerDeleteServlet";//隱患刪除接口

    public static String HTTP_CY_SAFEVIEW_SERVLET = SERVER + "SafeViewServlet";//消殺車輛獲取列表
    public static String HTTP_XS_CY_SAFEDATASAVE_SERVLET = SERVER+"XSUploadFileServlet";//消殺點檢提交接口
    public static String HTTP_XS_CZ_SAFEDATAQUERY_SERVLET = SERVER+"safeDataQueryServlet";//消殺查驗接口
    public static String HTTP_XS_SAFEDATADEL_SERVLET = SERVER + "SafeDataDelServlet";//消殺刪除記錄
    public static String HTTP_XS_GET_IMG = SERVER + "SafePhotos/";//異常圖片查看
    public static String HTTP_SUSHE_BEDSERVLET =SAFE_SERVER+ "SuShe/BedServlet";//宿舍查驗 area,building,room
    public static String HTTP_SUSHE_TYPESERVLET=SAFE_SERVER+"SuShe/TypeServlet";//宿舍查驗 類型下拉菜單
    public static String HTTP_SUSHE_JCOKSERVLET =SAFE_SERVER+ "SuShe/JCOkServlet";//宿舍查驗 提交
    public static String HTTP_SUSHE_VIEW_SERVLET = SAFE_SERVER+"SuShe/SuSheViewServlet";//宿舍查驗 查看查驗信息
    public static String HTTP_SUSHE_DELETE_SERVLET = SAFE_SERVER+"SuShe/SuSheDeleteServlet";//宿舍查驗 刪除查驗信息
    public static String HTTP_SUSHE_REFORMLIST_SERVLET = SAFE_SERVER+"SuShe/ReformListServlet"; //宿舍異常維護 獲取列表area, building, room
    public static String HTTP_SUSHE_REFORMUPDATE_SERVLET=SAFE_SERVER+"SuShe/ReformUpdateServlet"; //宿舍異常維護 提交按鈕jc_id, yc_id,zg_creator
    public static String HTTP_SUSHE_BUILDING = SAFE_SERVER+"SuShe/BuildingByAreaServlet";//       jc_area   区-棟
    public static String HTTP_SUSHE_FLOOR = SAFE_SERVER+"SuShe/FloorByBuildingServlet";//     jc_area jc_building   区栋-层
    public static String HTTP_SUSHE_ROOM = SAFE_SERVER+"SuShe/RoomByFloorServlet";//     jc_area jc_builing  jc_floor  区栋层-房间
    public static String HTTP_SUSHE_photp = SAFE_SERVER+"SuShe/";

    public static String HTTP_RZTX_SERVLET =SERVER+"RZTX_Servlet";//人資退訓信息
    public static String HTTP_RZTX_UPDATE_SERVLET =SERVER+"RZTX_Update_Servlet";//人資退訓放行
    public static String HTTP_BANDAO_VIEW_SERVLET =SERVER+"BandaoVeiwServlet";//人資班導帶出內容接口
    public static String HTTP_BANDAO_BASE_SERVLET =SERVER+"BandaoBaseServlet";//人資班導帶出記錄接口
    public static String HTTP_BANDAO_UPDATE_SERVLET =SERVER+"BandaoUpdateServlet";//人資班導提交內容接口

    public static String HTTP_WATER_SCAN_SERVLET = SAFE_SERVER + "Water/WaterScanServlet";//點檢項巡檢掃描二維碼帶出信息接口
    public static String HTTP_WATER_SCAN_VIEW_SERVLET = SAFE_SERVER + "Water/WaterScanViewServlet";//獲取進度列表接口
    public static String HTTP_WATER_INFO_SERVLET = SAFE_SERVER + "Water/WaterInfoServlet";//獲取點檢項接口
    public static String HTTP_WATER_SCAN_OK_SERVLET = SAFE_SERVER + "Water/WaterScanOkServlet";//點檢提交接口
    public static String HTTP_WATER_EXCE_VIEW_SERVLET = SAFE_SERVER + "Water/WaterExceViewServlet";//異常設備列表查看接口
    public static String HTTP_WATER_PHOTO_INFO_SERVLET = SAFE_SERVER + "Water/WaterPhotoInfoServlet";//異常圖片看接口
    public static String HTTP_WATER_REPAIRED_SERVLET = SAFE_SERVER + "Water/RepairedServlet";//維護正常照片提交接口
    public static String HTTP_WATER_EXCE_VIEW_INFO_SERVLET = SAFE_SERVER + "/Water/WaterExceViewInfoServlet";//點檢列表查看接口
    public static String HTTP_WATER_AREA_SERVLET = SAFE_SERVER + "Water/AreaServlet";//區域
    public static String HTTP_WATER_BUILDING_SERVLET = SAFE_SERVER + "Water/BuildingServlet";//樓棟
    public static String HTTP_WATER_FLOOR_SERVLET = SAFE_SERVER + "Water/FloorServlet";//樓層
    public static String HTTP_WATER_PROCESS_SERVLET = SAFE_SERVER + "Water/ProcessServlet";//進度
    public static String HTTP_WATER_MAINTAIN_SERVLET = SAFE_SERVER + "Water/MaintainViewServlet";//維保查看
    public static String HTTP_WATER_MAINTAIN_OK_SERVLET = SAFE_SERVER + "Water/MaintainOKServlet";//維保提交
    public static String HTTP_WATER_EXCE_DELETE = SAFE_SERVER + "Water/ExceDeleteServlet";//異常刪除
    public static String HTTP_MAINTAIN_INFO_DELETE = SAFE_SERVER + "Water/MaintainInfoViewServlet";//維保列表

    public static String HTTP_STORE_OERSION_INFO = SAFE_SERVER+"Store/PersonInfoServlet";//寄存獲取人員住宿信息 userid=
    public static String HTTP_STORE_TYPE_INFO = SAFE_SERVER+"Store/TypeInfoServlet";//寄存獲取物品類型
    public static String HTTP_STORE_APPLY_OK = SAFE_SERVER+"Store/ApplyOKServlet";//寄存 提交
    public static String HTTP_STORE_DEPOSIT_LIST = SAFE_SERVER+"Store/DepositListServlet";//列表
    public static String HTTP_VIEW_BY_ID = SAFE_SERVER+"Store/ViewByIdServlet";//寄存信息產看並排配 sid
    public static String HTTP_STORE_INFO = SAFE_SERVER+"Store/StoreInfoServlet";//寄存信息產看並排配 獲取倉庫sid+zwuser
    public static String HTTP_LOCATION_INFO = SAFE_SERVER+"Store/LocationInfoServlet";//寄存信息產看並排配 獲取儲位sid+zwuser
    public static String HTTP_DEPOSIT_OK = SAFE_SERVER+"Store/DepositOKServlet";//寄存信息產看並排配 提交
    public static String HTTP_ISSUE_OK = SAFE_SERVER+"Store/IssueOKServlet";//領取確認
    public static String HTTP_STORE_DELETE_BY_ID = SAFE_SERVER+"Store/DeleteByIdServlet";//刪除申請  sid

    public static String HTTP_RZ_SCAN_STATUS = SAFE_SERVER+"RZ/ScanStatusServlet";//人資監餐 狀態查詢
    public static String HTTP_RZ_SCAN_SIGNIN = SAFE_SERVER+"RZ/ScanSignInServlet";//人資監餐 簽到
    public static String HTTP_RZ_SCAN_SIGNOUT = SAFE_SERVER+"RZ/ScanSignOutServlet";//人資監餐 簽退
    public static String HTTP_RZ_FOOD_UPLOAD = SAFE_SERVER+"RZ/FoodUploadServlet";//人資監餐 菜品照片上傳
    public static String HTTP_RZ_FOOD_DELETE = SAFE_SERVER+"RZ/FoodDeleteServlet";//人資監餐 菜品照片刪除
    public static String HTTP_RZ_FOOD_VIEW = SAFE_SERVER+"RZ/FoodViewServlet";//人資監餐 菜品照片查看
    public static String HTTP_RZ_TYPE_SEARCH1 = SAFE_SERVER+"RZ/typeSearch1Servlet";//人資監餐 類別根據1查2
    public static String HTTP_RZ_TYPE_SEARCH2 = SAFE_SERVER+"RZ/typeSearch2Servlet";//人資監餐 類別根據1.2查3
    public static String HTTP_RZ_EXCE_UPLOAD = SAFE_SERVER+"RZ/exceUploadServlet";//人資監餐 異常上傳
    public static String HTTP_RZ_EXCE_VIEW = SAFE_SERVER+"RZ/RZExceViewServlet";//人資監餐 異常列表及異常詳情
    public static String HTTP_RZ_EXCE_DELETE = SAFE_SERVER+"RZ/RZExceDeleteServlet";//人資監餐 異常刪除

    public static String HTTP_CAR_MESSAGE_SERVLET = SAFE_SERVER +"servlet/CarServlet";//車輛點檢獲取車輛基本資料
    public static String HTTP_CAR_INSPRCTOK_SERVLET = SAFE_SERVER+"servlet/CarInspectOkServlet";//車輛點檢提交
    public static String HTTP_CAR_INSPRCT_SERVLET = SAFE_SERVER+"servlet/CarInspectServlet";//車輛點檢項
    public static String HTTP_CAR_EXCE_VIEW_INFO_SERVLET = SAFE_SERVER+"servlet/CarExceViewInfoServlet";//車輛點檢異常列表
    public static String HTTP_CAR_REPAIRED_SERVLET = SAFE_SERVER+"servlet/CarRepairedServlet";//車輛異常維修提交

    public static String HTTP_ZW_SCAN_STATUS = SAFE_SERVER +"ZW/ZWScanStatusServlet";// 总务临时工，状态查看
    public static String HTTP_ZW_SCAN_SIGNIN = SAFE_SERVER+"ZW/ZWScanSignInServlet";//总务临时工，签到
    public static String HTTP_ZW_EXCE_UOLOAD = SAFE_SERVER+"ZW/ZWExceUploadServlet";//总务临时工，异常上传
    public static String HTTP_ZW_EXCE_VIEW = SAFE_SERVER+"ZW/ZWExceViewServlet";//总务临时工，异常列表
    public static String HTTP_ZW_EXVE_DETAIL = SAFE_SERVER+"ZW/ZWExceDetailServlet";//总务临时工，异常详情
    public static String HTTP_ZW_EXCE_REFORM = SAFE_SERVER+"ZW/ZWExceReformUpdateServlet";//总务临时工，异常整改
    public static String HTTP_ZW_EXCE_DALETE = SAFE_SERVER+"ZW/ZWExceDeleteServlet";//总务临时工，异常删除
    public static String HTTP_ZW_SCAN_SIGNOUT = SAFE_SERVER+"ZW/ZWScanSignOutServlet";//总务临时工，签退
    public static String HTTP_ZW_SCAN_DUTY = SAFE_SERVER+"ZW/ZWScanDutyServlet";//总务临时工，职能信息
    public static String HTTP_ZW_SCAN_TYPE = SAFE_SERVER+"ZW/ZWScanTypeServlet";//總務臨時工 獲取異常類別

    public static String HTTP_HEALTH_SCAN = SAFE_SERVER+"Health/HealthScanServlet";//安全健康追蹤，人員信息查看
    public static String HTTP_HEALTH_COMMIT = SAFE_SERVER+"Health/HealthCommitServlet";//安全健康追蹤，提交生成單頭
    public static String HTTP_BODY_SELECT= SAFE_SERVER+"Health/HealthBodySelectServlet";//安全健康追蹤，查詢已有紀錄
    public static String HTTP_HEAD_DELETE = SAFE_SERVER+"Health/HeadDeleteServlet";//安全部健康追蹤，刪除單頭
    public static String HTTP_HEALTH_TRACE_COMMIT=SAFE_SERVER+"Health/HealthTraceCommitServlet";//安全部健康追蹤 單身錄入
    public static String HTTP_BODY_DELETE = SAFE_SERVER+"Health/HealthBodyDeleteServlet";//安全部健康追蹤 單身刪除
    public static String HTTP_BODY_DELETE_SELECT = SAFE_SERVER+"Health/HealthBodyDeleteSelectServlet";//安全部健康追蹤 單身查看
    public static String HTTP_CASE_FISH = SAFE_SERVER+"Health/HealthCaseFishServlet";//安全部健康追蹤 結案
    public static String HTTP_TRACE_ADD = SAFE_SERVER+"Health/HealthTraceAddServlet";//安全部体症异常追踪 体温补录

    public static String HTTP_YJ_SCAN = SAFE_SERVER +"YJ/YJScanServlet";//工程管理 獲取點檢類目
    public static String HTTP_YJ_SCAN_OK = SAFE_SERVER + "YJ/YJScanOKServlet";//工程管理 上傳點檢內容
    public static String HTTP_YJ_VIEW = SAFE_SERVER +"YJ/YJViewServlet";///   工程查詢
    public static String HTTP_YJ_INFO_BY_NO = SAFE_SERVER +"YJ/YJInfoByNoServlet";//   更改時間時獲取工程信息
    public static String HTTP_YJ_UPDATE_BY_NO = SAFE_SERVER +"YJ/YJUpdateByNoServlet";//    更新提交
    public static String HTTP_YJ_LIST = SAFE_SERVER +"YJ/YJListServlet";//  異常列表
    public static String HTTP_YJ_EXCE_INFO = SAFE_SERVER +"YJ/YJExceInfoServlet";//    詳情
    public static String HTTP_YJ_EXCE_DELETE = SAFE_SERVER +"YJ/YJExceDeleteServlet";//   異常刪除
    public static String HTTP_DEVICE_BORROW_PHOTO = SAFE_SERVER +"photos/";    //  設備圖片
    public static String HTTP_DEVICE_BORROW_BASE = SERVER +"DeviceBorrowBaseServlet";//   設備基本信息獲取 設備狀態查詢
    public static String HTTP_DEVICE_BORROW_UP = SERVER +"DeviceBorrowUpServlet";//   設備借用歸還
    public static String HTTP_CANT_CHECK_BSAE = SAFE_SERVER +"DimemsionBaseServlet";//  無法點檢獲取點位基本資料
    public static String HTTP_CANT_CHECK_UP = SAFE_SERVER +"ExceCantCheckUpServlet";//  無法點檢上傳異常
    public static String HTTP_CAR_TYPE = SAFE_SERVER +"CarProgressTypeServlet";//  車輛點檢進度-車輛類別

    public static String HTTP_CPC_SEARCH_SERVLET = safeServerlocal +"CPCSearchServlet";//成品倉無紙化 銷單查詢
    public static String HTTP_CPC_BODY_LIST_SERVLET = safeServerlocal +"CPCGetBodyListServlet";//成品倉無紙化 銷單貨物信息
    public static String HTTP_CPC_CONFIRM_SERVLET = safeServerlocal +"CPCConfirmServlet";//成品倉無紙化 單筆確認
    public static String HTTP_CPC_CONFIRM_ALL_SERVLET = safeServerlocal +"CPCConfirmAllUpServlet";//成品倉無紙化 最終確認
    public static String HTTP_CPC_NG_UP_SERVLET = safeServerlocal +"CPCNGUPServlet";//成品倉無紙化 NG提交
    public static String HTTP_CPC_NG_DELETE_SERVLET = safeServerlocal +"CPCNGDeleteServlet";//成品倉無紙化 取消NG


}