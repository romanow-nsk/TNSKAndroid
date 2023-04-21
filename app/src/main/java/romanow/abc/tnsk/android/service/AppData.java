
package romanow.abc.tnsk.android.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.HashMap;

import romanow.abc.core.API.RestAPI;
import romanow.abc.core.constants.ConstValue;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.EntityRefList;
import romanow.abc.core.entity.WorkSettings;
import romanow.abc.core.entity.server.TCare;
import romanow.abc.core.entity.server.TPassenger;
import romanow.abc.core.entity.server.TPassengerPoint;
import romanow.abc.core.entity.subjectarea.TRoute;
import romanow.abc.tnsk.android.FileDescriptionList;
import romanow.abc.tnsk.android.R;
import romanow.abc.core.API.RestAPIBase;

import romanow.abc.core.Utils;
import romanow.abc.core.entity.base.BugList;
import romanow.abc.core.entity.base.BugMessage;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.core.utils.OwnDateTime;
import romanow.abc.tnsk.android.AppSettings;
import romanow.abc.tnsk.android.StoryList;

public class AppData extends Application {
    public final static String apkVersion = "1.0.1, 01.04.2023";
    private final static String codeGenPassword="pi31415926";
    public final static String MAPKIT_API_KEY = "fda3e521-bbc6-4c75-9ec7-ccd4fdaa34d3";
    public final static int PopupShortDelay=4;     // Время короткого popup
    public final static int PopupMiddleDelay=7;    // Время длинного popup
    public final static int PopupLongDelay=10;     // Время длинного popup
    public final static int CKeepALiveTime=10;     // Интервал времени проверки соединения
    public final static int MapStartDelay=5;       // Задержка старта карты (чтобы передавать события)
    public final static double ScreenMas=0.9;
    public final static int GPSModeNone=0;
    public final static int GPSModeFirst=1;
    public final static int GPSModeNext=2;
    public final static int GPSModeLast=3;
    public final static int GPSModeRoute=4;
    public final static int GPSModePassenger=5;
    //---------------------------------------------------------------------------------------------
    public final static int CStateGray=0;          // Состояние соединения не определено
    public final static int CStateRed=1;           // Нет соединения
    public final static int CStateYellow=2;        // Восстановление
    public final static int CStateGreen=3;         // Есть соединение
    public final static int CStatesRes[]=new int[]{
            R.drawable.status_gray,
            R.drawable.status_red,
            R.drawable.status_yellow,
            R.drawable.status_green,
        };
    public final static int CNetRes[]=new int[]{
            R.drawable.ballgray,
            R.drawable.ballred,
            R.drawable.ballyellow,
            R.drawable.ballgreen,
    };
    public final static int StorySize=50;           // Размерность сохраненных событий
    public final static int HTTPTimeOut=30;
    public final static int FatalExceptionStackSize=20;
    //-------------------------------------------- События -------------------------------------------------------------
    public final static String Event_CState="me.romanow.CState";           // Изменнеие состояния сети
    public final static String Event_Clock="me.romanow.Clock";             // Таймер
    public final static String Event_GPS="me.romanow.GPS";                 // Изменение состояния GPS
    public final static String Event_Popup="me.romanow.Popup";             // Всплывающее сообщение, параметры header, text
    //----------------------------------------------------------------------------
    public final static ArrayList<String> WinFuncList=new ArrayList<>();{
        WinFuncList.add("Прямоугольник");
        WinFuncList.add("Треугольник");
        WinFuncList.add("Синус");
        WinFuncList.add("Парабола");
        }
    public final static String mapkitDir = "mapkit";
    public final static String waveDir = "wave";
    public final static String excelDir = "xls";
    public final static HashMap<String,Object> SubDirList=new HashMap<>();{
        SubDirList.put(mapkitDir,mapkitDir);
        SubDirList.put(excelDir,excelDir);
        SubDirList.put(waveDir,waveDir);
        }
    public final static HashMap<Integer,Integer> StateColors=new HashMap<>();{
        StateColors.put(0,R.drawable.status_gray);
        StateColors.put(1,R.drawable.status_green);
        StateColors.put(2,R.drawable.status_light_green);
        StateColors.put(3,R.drawable.status_red);
        StateColors.put(4,R.drawable.status_red);
        StateColors.put(5,R.drawable.status_gray);
        StateColors.put(6,R.drawable.status_red);
        StateColors.put(7,R.drawable.status_yellow);
        StateColors.put(8,R.drawable.status_light_red);
        StateColors.put(9,R.drawable.status_light_yellow);
        }
    /*
    public final static int FuncNoSim=0;        // Нет данных сим-карты
    public final static int FuncNoServerSim=1;  // сим-карта не зарегистрирована
    public final static int FuncAnotherSim=2;   // данные сим-карт не совпадают
    public final static int FuncNormal=3;       // сим-карта зарегистрирована
    public final String FuncStates[]={
        "Нет данных сим-карты",
        "Сим-карта не зарегистрирована",
        "Посторонняя сим-карта",
        "Cим-карта зарегистрирована"
        };
    */
    //------------------------------------------------------------------------------
    private boolean canSendPopup=false;
    private BugList fatalMessages = new BugList();              // Список фатальных ошибок
    private WorkSettings workSettings = new WorkSettings();     // Параметры от сервера
    private StoryList storyList = new StoryList();
    private AppSettings loginSettings = new AppSettings();      // Параметры приложения
    private TPassenger passenger = new TPassenger();            // Трек пассажира
    Object appSynch = new Object();
    private RestAPIBase service = null;                         // Основное API
    private RestAPI service2 = null;                            // API NskGorTrans
    private HashMap<Integer, ConstValue> careTypeMap;           // Типы ТС
    private EntityRefList<TCare> cares = new EntityRefList<>(); // Выбранные борта
    private TRoute route = new TRoute();                        // Выбранный маршрут
    private int cState = AppData.CStateGray;                    // Состояние соединения
    //---------------------------------------------------------------------------------
    public TRoute route(){ return route; }
    public void route(TRoute route0) { route = route0; }
    public TPassenger passenger(){ return passenger; }
    public void passenger(TPassenger passenger1) { passenger = passenger1; }
    public WorkSettings workSettings(){ return workSettings; }
    public StoryList storyList(){ return storyList; }
    public BugList fatalMessages(){ return fatalMessages; }
    public void workSettings(WorkSettings ws){ workSettings=ws; }
    public void storyList(StoryList ss){ storyList=ss; }
    public void fatalMessages(BugList bb){ fatalMessages=bb; }
    public void loginSettings(AppSettings ss){ loginSettings=ss; }
    public synchronized int cState() { return cState; }
    public synchronized void cState(int cState) {
        this.cState = cState;
        Intent intent = new Intent();
        intent.setAction(Event_CState);
        context.sendBroadcast(intent);
        }
    //------------------------------------------------------------------------------
    FileService fileService = new FileService(this);
    private String registrationCode="";         // Хэш-код регистрации приложения
    private boolean registeredOnServer=false;   //
    //-----------------------------------------------------------------------------
    private AppData(){
        Values.init();
        careTypeMap = Values.constMap().getGroupMapByValue("RouteType");
        }
    private static AppData ctx = null;          // ГОРЯЧИЙ РЕСТАРТ - обнулить stopApplication
    public static AppData ctx(){
        if (ctx==null){
            ctx = new AppData();
            ctx.startApplication();
            }
        return ctx;
        }
    //----------------------------------------------------------------------------
    private FileDescriptionList fileList = new FileDescriptionList();   // Мультивыборка
    private volatile boolean busy = false;              // Признак выполнения фоновой операции
    private String actualVersion = "";
    private int satCount = 0;                           // Количество спутников
    private GPSPoint lastGPS=new GPSPoint();
    private Thread.UncaughtExceptionHandler oldHandler;
    private boolean applicationOn = false;              // Приложение работает
    private Context context;
    //-----------------------------------------------------------------------------------
    public HashMap<Integer, ConstValue> getCareTypeMap() {
        return careTypeMap; }
    public void setCanSendPopup(boolean canSendPopup) {
        this.canSendPopup = canSendPopup; }
    public boolean isCanSendPopup() {
        return  canSendPopup; }
    public GPSPoint getLastGPS() {
        return lastGPS; }
    public void setLastGPS(GPSPoint lastGPS) {
        this.lastGPS = lastGPS; }
    public Context getContext() {
        return context; }
    public void setContext(Context context) {
        this.context = context; }
    public final String androidFileDirectory(){
        return context.getExternalFilesDir(null).getAbsolutePath();
        }
    public FileDescriptionList getFileList() {
        return fileList; }
    public void setFileList(FileDescriptionList fileList) {
        this.fileList = fileList; }
    public boolean isApplicationOn() {
        return applicationOn;
    }
    public synchronized boolean isBusy() { return busy; }
    public synchronized void setBusy(boolean busy) { this.busy = busy; }
    public int getSatCount() { return satCount; }
    public String getActualVersion() { return actualVersion; }
    public void setActualVersion(String actualVersion) { this.actualVersion = actualVersion; }
    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() { return oldHandler; }
    public AppSettings loginSettings(){ return loginSettings; }
    public FileService getFileService() { return fileService; }
    public RestAPIBase getService() { return service; }
    public RestAPI getService2() { return service2; }
    public void service(RestAPIBase service) { this.service = service; }
    public void service2(RestAPI service2) { this.service2 = service2; }
    public String getCodeGenPassword() { return codeGenPassword; }
    public boolean isRegisteredOnServer() { return registeredOnServer; }
    public void setRegisteredOnServer(boolean registeredOnServer) { this.registeredOnServer = registeredOnServer; }
    public EntityRefList<TCare> getCares() { return cares; }
    public void setCares(EntityRefList<TCare> cares) { this.cares = cares; }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopApplication();
        }

    @SuppressLint("MissingPermission")
    public void startApplication() {
        applicationOn=true;
        }
    public FileService fileService(){ return fileService; }
    //============================================================================================================================
    public void stopApplication() {
        applicationOn = false;
        ctx = null;                     // ГОРЯЧИЙ РЕСТАРТ - повторная инициализация синглетона
        }

    public void sendPopup(int drawId, boolean error, boolean popup, boolean tolog, String mes){
        if (!canSendPopup)
            return;
        Intent intent = new Intent();
        intent.setAction(Event_Popup);
        intent.putExtra("error",error);
        intent.putExtra("drawId",drawId);
        intent.putExtra("toLog",tolog);
        intent.putExtra("popup",popup);
        intent.putExtra("mes",mes);
        context.sendBroadcast(intent);
        }
    public void sendGPSMode(int mode,String title, int drawId){
        Intent intent = new Intent();
        intent.setAction(Event_GPS);
        intent.putExtra("title",title);
        intent.putExtra("current",false);
        intent.putExtra("drawId",drawId);
        intent.putExtra("gpsX",0);
        intent.putExtra("gpsY",0);
        intent.putExtra("state",0);
        intent.putExtra("mode",mode);
        intent.putExtra("moveTo",true);
        context.sendBroadcast(intent);
    }
    public void sendGPS(GPSPoint gpsPoint){
        Intent intent = new Intent();
        intent.setAction(Event_GPS);
        intent.putExtra("title","Пассажир");
        intent.putExtra("current",true);
        intent.putExtra("drawId",R.drawable.where);
        intent.putExtra("gpsX",gpsPoint.geox());
        intent.putExtra("gpsY",gpsPoint.geoy());
        intent.putExtra("state",gpsPoint.state());
        context.sendBroadcast(intent);
        }
    public void sendGPS(GPSPoint gpsPoint, String title, int drawId, boolean moveTo){
        sendGPS(gpsPoint,title,drawId,moveTo,AppData.GPSModeNone);
        }
    public void sendGPS(GPSPoint gpsPoint, String title, int drawId, boolean moveTo, int mode){
        Intent intent = new Intent();
        intent.setAction(Event_GPS);
        intent.putExtra("mode",mode);
        intent.putExtra("title",title);
        intent.putExtra("current",false);
        intent.putExtra("drawId",drawId);
        intent.putExtra("gpsX",gpsPoint.geox());
        intent.putExtra("gpsY",gpsPoint.geoy());
        intent.putExtra("state",gpsPoint.state());
        intent.putExtra("moveTo",moveTo);
        context.sendBroadcast(intent);
    }
    //-------------------------------------------------------------------------------------
    public void addPassengetPoint(TPassengerPoint point) {
        passenger.addPassengerPoint(loginSettings.getPassengerStoryHours(),point);
        fileService.saveJSON(passenger);
        }
    public void addBugMessage(String ss) {
        BugMessage bug = new BugMessage(AppData.ctx().loginSettings.getUserId(), new OwnDateTime(), ss);
        while (fatalMessages.size() > StorySize)
            fatalMessages.remove(0);
        fatalMessages.add(bug);
        fileService.saveJSON(fatalMessages);
        }
    public void addStoryMessage(String mes) {
        while (storyList.size() > StorySize)
            storyList.remove(0);
        storyList.add(new BugMessage(mes));
        fileService.saveJSON(mes);
        }
    public String createBugMessage(String mes) {
        String ss = "Фиксация ошибки: "+mes + "\n";
        StackTraceElement dd[] = Thread.currentThread().getStackTrace();
        for (int i = 0; i < dd.length && i < AppData.FatalExceptionStackSize; i++) {
            ss += dd[i].getClassName() + "." + dd[i].getMethodName() + ":" + dd[i].getLineNumber() + "\n";
            }
        addBugMessage(ss);
        return ss;
        }
    public String createFatalMessage(Throwable ee) {
        String ss = ee.toString() + "\n";
        StackTraceElement dd[] = ee.getStackTrace();
        for (int i = 0; i < dd.length && i < AppData.FatalExceptionStackSize; i++) {
            ss += dd[i].getClassName() + "." + dd[i].getMethodName() + ":" + dd[i].getLineNumber() + "\n";
            }
        String out = "Необработанное исключение:\n" + ss;
        addBugMessage(out);
        return out;
        }
    public void popupToastFatal(Exception ee){
        String ss = Utils.createFatalMessage(ee);
        toLog(true,"Ошибка: "+ee.toString()+"\n"+ss);
        addBugMessage(ss);
        }
    public void popupAndLog(boolean fatal,String ss){
        sendPopup(fatal ? R.drawable.problem : R.drawable.info,false,true,true,ss);
        }
    public void popup(boolean fatal,String ss){
        sendPopup(fatal ? R.drawable.problem : R.drawable.info,fatal,true,false,ss);
        }
    public void errorMes(String ss){
        sendPopup(R.drawable.problem,true, false,true,ss);
        }
    public void toLog(boolean fatal,String ss){
        sendPopup(fatal ? R.drawable.problem : R.drawable.info,fatal,false,true,ss);
        }
    //---------------------------------------------------------------------------------------
}
