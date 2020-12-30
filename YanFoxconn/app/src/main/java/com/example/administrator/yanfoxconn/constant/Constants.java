
package com.example.administrator.yanfoxconn.constant;

/**
 * url
 * Created by song on 2017/8/30.
 */

public class Constants {

//    public static final String BASE_URL = "http://ytlogisticgps.foxconn.com/SafeServer/";//玲姐
//    public static final String EMP_BASE = "http://ytlogisticgps.foxconn.com/Server/";    //杰哥

    public static final String BASE_URL = "http://ytsafeserver.foxconn.com/SafeServer/";//玲姐
    public static final String EMP_BASE = "http://ytsafeserver.foxconn.com/Server/";    //杰哥


    public static String J4 = "http://192.168.0.108:8080/Server/";//杰哥4F接口測試
//  public static String J5 = "http://192.168.1.112:8080/Server/";//杰哥5F接口測試

    public static String W4 = "http://192.168.1.121:8080/Server/";//我的接口測試
    public static String L4 = "http://192.168.1.137:8080/SafeServer/";//玲姐4F接口測試

    public static final String HTTP_LOGIN = BASE_URL + "LoginServlet";//登錄
    public static String HTTP_DIMEMSION_SERVLET = BASE_URL + "safeCheck/DimemsionServlet";//巡檢進度列表
    public static String HTTP_DIMEMSION_STATE_SERVLET = BASE_URL + "safeCheck/DimemsionStateServlet";//掃描二維碼交互
    public static String HTTP_UPLOAD_FILE_SERVLET = BASE_URL + "safeCheck/UploadFileServlet";//安全部,異常提交
    public static String HTTP_EXCEPTION_VIEW_SERVLET = BASE_URL + "safeCheck/ExceptionViewServlet";//查詢異常列表
    public static String HTTP_EXCEPTION_DELETE_SERVLET = BASE_URL + "safeCheck/ExceptionDeleteServlet";//個人異常刪除
    public static String HTTP_GET_VERSION_CODE = BASE_URL + "VersionServlet";//獲取最新版本信息
    public static String HTTP_EXCEPTION_DETAIL_SERVLET = BASE_URL + "safeCheck/ExceptionDetailServlet";//查詢異常詳情

    public static String HTTP_GET_IMG = BASE_URL + "photos/";//異常圖片查看
    public static String HTTP_GET_APK = BASE_URL + "apk/";//獲取最新版本apk
    public static String HTTP_PATORLMAN_SERVLET = BASE_URL + "PatrolmanServlet";//巡檢人員維護交互
    public static String HTTP_CAR_SCAN_SERVLET = BASE_URL + "CarScanServlet";//碼頭派車單掃描交互
    public static String HTTP_CAR_UPDATE_SERVLET = BASE_URL + "CarUpdateServlet";//碼頭掃描提交交互(文字與圖片)
    public static String HTTP_CAR_RECORD_SELECT = BASE_URL + "CarRecordSelectServlet";//車調日誌 獲取日誌列表
    public static String HTTP_CAR_RECORD_INSERT = BASE_URL + "CarRecordInsertServlet";//車調日誌 上傳行車日誌
    public static String HTTP_CAR_RECORD_VIEW = BASE_URL + "CarRecordViewServlet";//車調日誌 日誌詳情查看
    public static String HTTP_CAR_RECORD_DELETE = BASE_URL + "CarRecordDeleteServlet";//車調日誌 日誌記錄刪除
    public static String HTTP_CAR_LOG_IMG = BASE_URL + "CarRecord/";//車調日誌 圖片查看
    public static String HTTP_CAR_LOG_RETURN = BASE_URL + "CarRecordBackServlet";//車調日誌 退件查看
    public static String HTTP_MENU_SERVLET = BASE_URL + "MenuServlet";//宿舍分類二級菜單,餐飲二級分類
    public static String HTTP_DIMEMSION_NEXT_SERVLET = BASE_URL + "safeCheck/DimemsionNextServlet";//宿舍查詢巡檢點
    public static String HTTP_EXPORTIOUPDATE_SERVLET = BASE_URL + "ExportioupdateServlet";//跨區掃描  提交掃描人工號
    public static String HTTP_EXPORTIO_SERVLET = BASE_URL + "ExportioServlet";//跨區掃描 掃描攜帶人工號
    public static String HTTP_CARVIEW_SERVLET = BASE_URL + "CarviewServlet";//在碼頭車輛交互
    public static String HTTP_CARPICTURE_SERVLET = BASE_URL + "CarpictureServlet";//碼頭車輛是否上傳圖片
    public static String HTTP_CARPICTURE_UP_SERVLET = BASE_URL + "CarpicUploadServlet";//碼頭車輛 上傳圖片
    public static String HTTP_CAR_LEAVE_SERVLET = BASE_URL + "CarLeaveServlet";//碼頭車輛離開（全銷單，維護人）
    public static String HTTP_BARCODE_CREATE_SERVLET = BASE_URL + "BarcodeCreateServlet";//人資活動創建
    public static String HTTP_BARCODE_VIEW_SERVLET = BASE_URL + "BarcodeViewServlet";//人資活動查看
    public static String HTTP_BARCODE_MOIDY_SERVLET = BASE_URL + "BarcodeMoidyServlet";//人資活動取消
    public static String HTTP_BARCODE_SCAN_SERVLET = BASE_URL + "BarcodeScanServlet";//人資活動掃描
    public static String HTTP_BARCODE_JBVIEW_SERVLET = BASE_URL + "BarcodeJbviewServlet";//人資查看加班時數
    public static String HTTP_COMMON_FORMS_UPDATE_SERVLET = EMP_BASE + "CommonFormsupdateServlet";//員工違規表單,員工進出異常表單 提交用
    public static String HTTP_COMMON_FORMS_PHOTOS_SERVLET = EMP_BASE + "CommonFormsupload_photoservlet";//二輪車違規帶圖片上傳
    public static String HTTP_COMMON_FORMS_TWO_WHEEL_SERVLET = EMP_BASE + "CommonFormsTwoWheelServlet";//二輪車違規工號查詢,並獲取門崗列表
    public static String HTTP_COMMON_FORMS_DAY_RECORD_SERVLET = EMP_BASE + "CommonFormsDayRecordServlet";//二輪車違規查詢當日記錄
    public static String HTTP_COMMON_FORMS_PRIVATE_CAR_SERVLET = W4 + "CommonFormsPrivateCarUpServlet";//私家車違規稽核提交接口
    public static String HTTP_COMMON_FORMS_PRIVATE_CAR_PARKING = W4 + "CommonFormsPrivateCarParkingServlet";//私家車違規帶出停車場接口


    public static String HTTP_COMMON_FORMS_SERVLET = EMP_BASE + "CommonFormsServlet";//工號查詢,並獲取門崗列表
    public static String HTTP_COMMON_GOODS_SERVLET = EMP_BASE + "CommongoodsServlet";//普通物品 工號查詢,並獲取門崗列表，提交信息
    public static String HTTP_INTELLIGENCE_GOODS_SERVLET = EMP_BASE + "IntelligenceGoodsServlet";//智慧物品放行 工號查詢,並獲取門崗列表，提交信息接口
    public static String HTTP_SANFANG_SERVLET = EMP_BASE + "SanFangServlet";//三防隱患獲取責任單位接口
    public static String HTTP_SANFANG_UPLOAD_SERVLET = EMP_BASE + "SanFangupload_photoservlet";//安全部三防隱患提交接口

    public static String HTTP_HUB_LOGIN_SERVLET = BASE_URL + "HubSys/HubViewServlet";//HUB倉選擇領用人接口
    public static String HTTP_HUB_UP_SERVLET = BASE_URL + "HubSys/HubUpdateServlet";//HUB倉領取物品提交接口
    public static String HTTP_HUB_AREA_SERVLET = BASE_URL  + "HubSys/HubSearchServlet";//HUB倉選擇區域接口
    public static String HTTP_HUB_RECEIVE_INFO_SERVLET = BASE_URL + "HubSys/HubReciveInfoServlet";//HUB倉簽收查找
    public static String HTTP_HUB_RECEIVE_SERVLET = BASE_URL + "HubSys/HubReciveServlet";//HUB倉簽收OK
    public static String HTTP_HUB_TAKE_VIEW_SERVLET = BASE_URL + "HubSys/HubTakeViewServlet";//HUB簽收查詢
    public static String HTTP_HUB_IMG = BASE_URL + "hubImgs/";//簽名圖片查看
    public static String HTTP_MOBILE_CONTROL_SERVLET = BASE_URL + "Mobile/MobileSearchServlet";//移動上網設備接口
    public static String HTTP_FORKLIFT_MESSAGE_SERVLET = EMP_BASE + "EquipmentCheckServlet";//叉車掃描接口
    public static String HTTP_FORKLIFT_CHECK_SERVLET =  EMP_BASE + "EquipmentServlet";//叉車點檢接口
    public static String HTTP_FORKLIFT_UPDATE_SERVLET = EMP_BASE + "Equipment_updateServlet";//叉車點檢提交接口
    public static String HTTP_FORKLIFT_REPAIR_SERVLET = EMP_BASE + "Equipment_BxServlet";//叉車報修提交接口
    public static String HTTP_FORKLIFT_MAINTENANCE_SERVLET = EMP_BASE + "EquipmentBxviewServlet";//叉車維修接口
    public static String HTTP_FORKLIFT_MAINTENANCE_UPDATE_SERVLET = EMP_BASE + "Equipment_WxServlet";//叉車維修提交接口
    public static String HTTP_EXPORTIO_SERVLET_CHE = EMP_BASE + "ExportioServlet_che";//車輛跨區獲取信息接口
    public static String HTTP_EXPORTIOUPDATE_SERVLET_CHE = EMP_BASE + "ExportioupdateServlet_che";//車輛跨區放行接口
    public static String HTTP_SCRAP_LEAVE_SERVLET = EMP_BASE + "scrapLeaveServlet";//廢料放行接口
    public static String HTTP_SCRAP_LEAVE_UPDATE_SERVLET = EMP_BASE + "scrapLeaveUpdateServlet";//廢料放行提交接口
    public static String HTTP_DUTY_CHIEF_UPDATE_SERVLET = EMP_BASE + "DutyChiefUpdateServlet";//值班科長上傳接口
    public static String HTTP_DUTY_CHIEF_VIEW_SERVLET = EMP_BASE + "DutyChiefViewServlet";//值班科長值班情況異常記錄查詢接口
    public static String HTTP_DUTY_CHIEF_MAIN_UPDATE_SERVLET = EMP_BASE + "DutyChiefMainUpdateServlet";//值班科長值班情況異常記錄上傳接口
    public static String HTTP_DUTY_CHIEF_PROBLEM_UPDATE_SERVLET = EMP_BASE + "DutyChiefProblemUpdateServlet";//值班科長稽核問題上傳接口
    public static String HTTP_DUTY_CHIEF_PROBLEM_SEE_SERVLET = EMP_BASE + "DutyChiefProblemSeeServlet";//值班科長稽核問題查看接口
    public static String HTTP_DUTY_CHIEF_PROBLEM_DEL_SERVLET = EMP_BASE + "DutyChiefProblemDeleteServlet";//值班科長稽核問題刪除接口

    public static String HTTP_TUNGANDPRODUCT_SERVLET = W4 + "TungAndProductServlet";//獲取樓棟產品處接口
    public static String HTTP_INDUSSAFE_DEPARTUPDATE_SERVLET = W4 + "IndusSafe_departUpdateServlet";//部門基本信息提交接口
    public static String HTTP_INDUSSAFE_DEPARTLISTVIEW_SERVLET = W4 + "IndusSafe_departListViewServlet";//獲取部門基本信息列表接口
    public static String HTTP_INDUSSAFE_PERSONUPDATE_SERVLET = W4 + "IndusSafe_personUpdateServlet";//巡查人陪查人上傳接口
    public static String HTTP_INDUSSAFE_SUGGESTUPDATE_SERVLET = W4 + "IndusSafe_suggestUpdateServlet";//優缺建議上傳接口
    public static String HTTP_INDUSSAFE_DANGER_UPDATE_SERVLET = W4 + "IndustrySafetyDangerPhotoServlet";//隱患上傳接口
    public static String HTTP_INDUSSAFE_DANGER_VIEW_SERVLET = W4 + "IndusSafe_dangerListViewServlet";//隱患查看接口
    public static String HTTP_INDUSSAFE_DANGER_DELETE_SERVLET = W4 + "IndusSafe_dangerDeleteServlet";//隱患刪除接口

    public static String HTTP_CY_SAFEVIEW_SERVLET = EMP_BASE + "SafeViewServlet";//消殺車輛獲取列表
    public static String HTTP_XS_CY_SAFEDATASAVE_SERVLET = EMP_BASE+"XSUploadFileServlet";//消殺點檢提交接口
    public static String HTTP_XS_CZ_SAFEDATAQUERY_SERVLET = EMP_BASE+"safeDataQueryServlet";//消殺查驗接口
    public static String HTTP_XS_SAFEDATADEL_SERVLET = EMP_BASE + "SafeDataDelServlet";//消殺刪除記錄
    public static String HTTP_XS_GET_IMG = EMP_BASE + "SafePhotos/";//異常圖片查看
    public static String HTTP_SUSHE_BEDSERVLET =BASE_URL+ "SuShe/BedServlet";//宿舍查驗 area,building,room
    public static String HTTP_SUSHE_TYPESERVLET=BASE_URL+"SuShe/TypeServlet";//宿舍查驗 類型下拉菜單
    public static String HTTP_SUSHE_JCOKSERVLET =BASE_URL+ "SuShe/JCOkServlet";//宿舍查驗 提交
    public static String HTTP_SUSHE_VIEW_SERVLET = BASE_URL+"SuShe/SuSheViewServlet";//宿舍查驗 查看查驗信息
    public static String HTTP_SUSHE_DELETE_SERVLET = BASE_URL+"SuShe/SuSheDeleteServlet";//宿舍查驗 刪除查驗信息
    public static String HTTP_SUSHE_REFORMLIST_SERVLET = BASE_URL+"SuShe/ReformListServlet"; //宿舍異常維護 獲取列表area, building, room
    public static String HTTP_SUSHE_REFORMUPDATE_SERVLET=BASE_URL+"SuShe/ReformUpdateServlet"; //宿舍異常維護 提交按鈕jc_id, yc_id,zg_creator
    public static String HTTP_SUSHE_BUILDING = BASE_URL+"SuShe/BuildingByAreaServlet";//       jc_area   区-棟
    public static String HTTP_SUSHE_FLOOR = BASE_URL+"SuShe/FloorByBuildingServlet";//     jc_area jc_building   区栋-层
    public static String HTTP_SUSHE_ROOM = BASE_URL+"SuShe/RoomByFloorServlet";//     jc_area jc_builing  jc_floor  区栋层-房间
    public static String HTTP_SUSHE_photp = BASE_URL+"SuShe/";

    public static String HTTP_RZTX_SERVLET =EMP_BASE+"RZTX_Servlet";//人資退訓信息
    public static String HTTP_RZTX_UPDATE_SERVLET =EMP_BASE+"RZTX_Update_Servlet";//人資退訓放行
    public static String HTTP_BANDAO_VIEW_SERVLET =EMP_BASE+"BandaoVeiwServlet";//人資班導帶出內容接口
    public static String HTTP_BANDAO_BASE_SERVLET =EMP_BASE+"BandaoBaseServlet";//人資班導帶出記錄接口
    public static String HTTP_BANDAO_UPDATE_SERVLET =EMP_BASE+"BandaoUpdateServlet";//人資班導提交內容接口

    public static String HTTP_WATER_SCAN_SERVLET = BASE_URL + "Water/WaterScanServlet";//點檢項巡檢掃描二維碼帶出信息接口
    public static String HTTP_WATER_SCAN_VIEW_SERVLET = BASE_URL + "Water/WaterScanViewServlet";//獲取進度列表接口
    public static String HTTP_WATER_INFO_SERVLET = BASE_URL + "Water/WaterInfoServlet";//獲取點檢項接口
    public static String HTTP_WATER_SCAN_OK_SERVLET = BASE_URL + "Water/WaterScanOkServlet";//點檢提交接口
    public static String HTTP_WATER_EXCE_VIEW_SERVLET = BASE_URL + "Water/WaterExceViewServlet";//異常設備列表查看接口
    public static String HTTP_WATER_PHOTO_INFO_SERVLET = BASE_URL + "Water/WaterPhotoInfoServlet";//異常圖片看接口
    public static String HTTP_WATER_REPAIRED_SERVLET = BASE_URL + "Water/RepairedServlet";//維護正常照片提交接口
    public static String HTTP_WATER_EXCE_VIEW_INFO_SERVLET = BASE_URL + "/Water/WaterExceViewInfoServlet";//異常列表查看接口
    public static String HTTP_WATER_AREA_SERVLET = BASE_URL + "Water/AreaServlet";//區域
    public static String HTTP_WATER_BUILDING_SERVLET = BASE_URL + "Water/BuildingServlet";//樓棟
    public static String HTTP_WATER_FLOOR_SERVLET = BASE_URL + "Water/FloorServlet";//樓層
    public static String HTTP_WATER_PROCESS_SERVLET = BASE_URL + "Water/ProcessServlet";//進度
    public static String HTTP_WATER_MAINTAIN_SERVLET = BASE_URL + "Water/MaintainViewServlet";//維保查看
    public static String HTTP_WATER_MAINTAIN_OK_SERVLET = BASE_URL + "Water/MaintainOKServlet";//維保提交
    public static String HTTP_WATER_EXCE_DELETE = BASE_URL + "Water/ExceDeleteServlet";//異常刪除
    public static String HTTP_MAINTAIN_INFO_DELETE = BASE_URL + "Water/MaintainInfoViewServlet";//維保列表

    public static String HTTP_RZ_SCAN_STATUS = BASE_URL+"RZ/ScanStatusServlet";//人資監餐 狀態查詢
    public static String HTTP_RZ_SCAN_SIGNIN = BASE_URL+"RZ/ScanSignInServlet";//人資監餐 簽到
    public static String HTTP_RZ_SCAN_SIGNOUT = BASE_URL+"RZ/ScanSignOutServlet";//人資監餐 簽退
    public static String HTTP_RZ_FOOD_UPLOAD = BASE_URL+"RZ/FoodUploadServlet";//人資監餐 菜品照片上傳
    public static String HTTP_RZ_FOOD_DELETE = BASE_URL+"RZ/FoodDeleteServlet";//人資監餐 菜品照片刪除
    public static String HTTP_RZ_FOOD_VIEW = BASE_URL+"RZ/FoodViewServlet";//人資監餐 菜品照片查看
    public static String HTTP_RZ_TYPE_SEARCH1 = BASE_URL+"RZ/typeSearch1Servlet";//人資監餐 類別根據1查2
    public static String HTTP_RZ_TYPE_SEARCH2 = BASE_URL+"RZ/typeSearch2Servlet";//人資監餐 類別根據1.2查3
    public static String HTTP_RZ_EXCE_UPLOAD = BASE_URL+"RZ/exceUploadServlet";//人資監餐 異常上傳
    public static String HTTP_RZ_EXCE_VIEW = BASE_URL+"RZ/RZExceViewServlet";//人資監餐 異常列表及異常詳情
    public static String HTTP_RZ_EXCE_DELETE = BASE_URL+"RZ/RZExceDeleteServlet";//人資監餐 異常刪除

    public static String HTTP_CAR_MESSAGE_SERVLET = BASE_URL +"servlet/CarServlet";//車輛點檢獲取車輛基本資料
    public static String HTTP_CAR_INSPRCTOK_SERVLET = BASE_URL+"servlet/CarInspectOkServlet";//車輛點檢提交
    public static String HTTP_CAR_INSPRCT_SERVLET = BASE_URL+"servlet/CarInspectServlet";//車輛點檢項
    public static String HTTP_CAR_EXCE_VIEW_INFO_SERVLET = BASE_URL+"servlet/CarExceViewInfoServlet";//車輛點檢異常列表
    public static String HTTP_CAR_REPAIRED_SERVLET = BASE_URL+"servlet/CarRepairedServlet";//車輛異常維修提交

    public static String HTTP_ZW_SCAN_STATUS = BASE_URL +"ZW/ZWScanStatusServlet";// 总务临时工，状态查看
    public static String HTTP_ZW_SCAN_SIGNIN = BASE_URL+"ZW/ZWScanSignInServlet";//总务临时工，签到
    public static String HTTP_ZW_EXCE_UOLOAD = BASE_URL+"ZW/ZWExceUploadServlet";//总务临时工，异常上传
    public static String HTTP_ZW_EXCE_VIEW = BASE_URL+"ZW/ZWExceViewServlet";//总务临时工，异常列表
    public static String HTTP_ZW_EXVE_DETAIL = BASE_URL+"ZW/ZWExceDetailServlet";//总务临时工，异常详情
    public static String HTTP_ZW_EXCE_REFORM = BASE_URL+"ZW/ZWExceReformUpdateServlet";//总务临时工，异常整改
    public static String HTTP_ZW_EXCE_DALETE = BASE_URL+"ZW/ZWExceDeleteServlet";//总务临时工，异常删除
    public static String HTTP_ZW_SCAN_SIGNOUT = BASE_URL+"ZW/ZWScanSignOutServlet";//总务临时工，签退
    public static String HTTP_ZW_SCAN_DUTY = BASE_URL+"ZW/ZWScanDutyServlet";//总务临时工，职能信息

    public static String HTTP_HEALTH_SCAN =BASE_URL+"Health/HealthScanServlet";//安全健康追蹤，人員信息查看
    public static String HTTP_HEALTH_COMMIT=BASE_URL+"Health/HealthCommitServlet";//安全健康追蹤，提交生成單頭
    public static String HTTP_BODY_SELECT=BASE_URL+"Health/HealthBodySelectServlet";//安全健康追蹤，查詢已有紀錄
    public static String HTTP_HEAD_DELETE = BASE_URL+"Health/HeadDeleteServlet";//安全部健康追蹤，刪除單頭
    public static String HTTP_HEALTH_TRACE_COMMIT=BASE_URL+"Health/HealthTraceCommitServlet";//安全部健康追蹤 單身錄入
    public static String HTTP_BODY_DELETE = BASE_URL+"Health/HealthBodyDeleteServlet";//安全部健康追蹤 單身刪除
    public static String HTTP_BODY_DELETE_SELECT = BASE_URL+"Health/HealthBodyDeleteSelectServlet";//安全部健康追蹤 單身查看
    public static String HTTP_CASE_FISH = BASE_URL+"Health/HealthCaseFishServlet";//安全部健康追蹤 結案
    public static String HTTP_TRACE_ADD = BASE_URL+"Health/HealthTraceAddServlet";//安全部体症异常追踪 体温补录


}