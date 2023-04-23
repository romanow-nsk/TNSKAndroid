package romanow.abc.tnsk.android;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import romanow.abc.core.API.RestAPI;
import romanow.abc.core.API.RestAPIBase;
import romanow.abc.core.UniException;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.baseentityes.JInt;
import romanow.abc.core.utils.GPSPoint;
import romanow.abc.core.utils.Pair;

import romanow.abc.tnsk.android.menu.MIMapCareNearest;
import romanow.abc.tnsk.android.menu.MIMapCareStory;
import romanow.abc.tnsk.android.menu.MILogin;
import romanow.abc.tnsk.android.menu.MIMapPassenger;
import romanow.abc.tnsk.android.menu.MIMapRoutes;
import romanow.abc.tnsk.android.menu.MIMapSegments;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.Base64Coder;
import romanow.abc.tnsk.android.service.BaseActivity;
import romanow.abc.tnsk.android.service.GPSService;
import romanow.abc.tnsk.android.service.I_GPSService;
import romanow.abc.tnsk.android.service.NetBack;
import romanow.abc.tnsk.android.service.NetCall;
import romanow.abc.tnsk.android.menu.MIAbout;
import romanow.abc.tnsk.android.menu.MenuItemAction;

public class MainActivity extends BaseActivity {     //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public MailSender mail = new MailSender(this);
    private I_GPSService gpsService;
    public I_GPSService getGpsService(){ return gpsService; }
    private Handler event = new Handler();
    public volatile boolean shutDown = false;
    public boolean voiceRun = false;
    private AppData ctx;
    public TextView headerInfo;
    //-------------- Постоянные параметры snn-core ---------------------------------------
    private final int MiddleColor = 0x0000FF00;
    private final int DispColor = 0x000000FF;
    private final int GraphBackColor = 0x00A0C0C0;
    final public static int DefaultTextColor=0x00035073;
    final public static String archiveFile = "LEP500Archive.json";
    final public static double ViewProcHigh = 0.6;
    final public static String VoiceFile = "LEP500.wave";
    //----------------------------------------------------------------------------
    private LinearLayout log;
    private ScrollView scroll;
    private final int CHOOSE_RESULT = 100;
    private final int CHOOSE_RESULT_COPY = 101;
    public final int REQUEST_ENABLE_BT = 102;
    public final int REQUEST_ENABLE_GPS = 103;
    public final int REQUEST_ENABLE_READ = 104;
    public final int REQUEST_ENABLE_WRITE = 105;
    public final int REQUEST_ENABLE_PHONE = 106;
    public final int REQUEST_ENABLE_AUDIO = 107;
    private ImageView MenuButton;
    private ImageView GPSState;
    private ImageView NETState;
    //--------------------------------------------------------------------------
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GPSPoint gps = new GPSPoint();
            int state = intent.getIntExtra("state",GPSPoint.GeoNone);
            gps.setCoord(intent.getDoubleExtra("geoY",0),
                    intent.getDoubleExtra("geoX",0),false);
            gps.state(state);
            if (state == GPSPoint.GeoNone)
                GPSState.setImageResource(R.drawable.gps_off);
            if (state == GPSPoint.GeoNet)
                GPSState.setImageResource(R.drawable.gsm);
            if (state == GPSPoint.GeoGPS)
                GPSState.setImageResource(R.drawable.gps);
            }
        };
    private I_EventListener logEvent = new I_EventListener() {
        @Override
        public void onEvent(String ss) {
            addToLog(ss);
        }
        };
    public void addMenuList(MenuItemAction action) {
        menuList.add(action);
    }
    public LinearLayout getLogLayout() {
        return log; }
    //------------------------------------------------------------------------------------------------------
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NETState.setImageResource(AppData.CNetRes[AppData.ctx().cState()]);
            }
        };
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(AppData.Event_CState);
        this.registerReceiver(receiver, filter);
        filter = new IntentFilter(AppData.Event_GPS);
        this.registerReceiver(gpsReceiver, filter);
        }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        unregisterReceiver(gpsReceiver);
        }
    //----------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ENABLE_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else
                errorMes("Включите разрешение геолокации");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else errorMes("Включите разрешение работы с памятью");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else errorMes("Включите разрешение работы с памятью");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else errorMes("Включите разрешение работы с микрофоном");
            if (testPermission()) onAllPermissionsEnabled();
        }
        if (requestCode == REQUEST_ENABLE_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else
                errorMes("Включите разрешение работы с телефоном");
            if (testPermission()) onAllPermissionsEnabled();
        }
        //--------------- Не включается -----------------------------------------------------------
        //if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
        //    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        //    } else
        //        errorMes("Включите разрешение работы с BlueTooth");
        //    if (testPermission()) onAllPermissionsEnabled();
        //}
    }
    //----------------------------------------------------------------------------------------------
    private boolean testPermission(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_ENABLE_READ);
            return false;
            }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ENABLE_WRITE);
            return false;
            }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_GPS);
            return false;
            }
        //if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        //        && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_ENABLE_PHONE);
        //    return false;
        //    }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_ENABLE_AUDIO);
            return false;
            }
        //----------------------------------------------------------------------------------------------------------
        //if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        //        && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
        //    return false;
        //    }
        return true;
    }

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = AppData.ctx();
        try {
            Values.init();                  // Статические данные
            ctx.setContext(getApplicationContext());
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_main);
            MenuButton = (ImageView) findViewById(R.id.headerMenu);
            GPSState = (ImageView) findViewById(R.id.headerGPS);
            log = (LinearLayout) findViewById(R.id.log);
            scroll = (ScrollView) findViewById(R.id.scroll);
            NETState = (ImageView) findViewById(R.id.headerNet);
            if (testPermission()){
                onAllPermissionsEnabled();
                }
            } catch (Exception ee) {
                errorMes(createFatalMessage(ee, 10));
                }
        }
    public void putHeaderInfo(String text){
        StringTokenizer ss = new StringTokenizer(text,"\n");
        int cnt = ss.countTokens();
        headerInfo.setLines(cnt);
        headerInfo.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        headerInfo.setText(text);
        headerInfo.setTextSize(cnt==1 ? 25 : 15);
        }
    private void onAllPermissionsEnabled(){
        try{
            ctx.fileService().loadContext();
            ctx.cState(AppData.CStateGray);
            createMenuList();
            gpsService = new GPSService();
            gpsService.startService(this);
            if (!isLocationEnabled()) {
                errorMes(EmoSet, " Включить \"Местоположение\" в настройках");
                popupToast(R.drawable.problem, " Включить \"Местоположение\" в настройках");
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            else
                gpsService.startService(this);
            MenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createMenuList();
                    menuDialog = new ListBoxDialog(MainActivity.this, createMenuTitles(), "Меню", new I_ListBoxListener() {
                        @Override
                        public void onSelect(int index) {
                            procMenuItem(index);
                            menuDialog = null;
                            }
                        @Override
                        public void onLongSelect(int index) {
                            menuDialog = null;
                            }
                        @Override
                        public void onCancel() {
                            menuDialog = null;
                            }
                        });
                    menuDialog.create();
                }
            });
            //------------------------------------------------
            //int[] surrogates = {0xD83D, 0xDC7D};
            //String title = "Звенящие опоры России "+
            //        new String(Character.toChars(0x1F349))+
            //        new String(surrogates, 0, surrogates.length)+
            //        "\uD83D\uDC7D";
            String fatalMessage = ctx.loginSettings().getFatalMessage();
            if (fatalMessage.length()!=0){
                addToLog(false,fatalMessage,14,0x00A00000);
                ctx.loginSettings().setFatalMessage("");
                saveContext();
                }
            headerInfo = (TextView) findViewById(R.id.headerInfo);
            String title = "Транспорт\nНовосибирска";
            putHeaderInfo(title);
            if (ctx.loginSettings().isAutoConnect())
                MILogin.login(this);
            //addToLog(false, title, 22, 0);
            //addToLogButton("Рег.код: "+createRegistrationCode(),true,null,null);
            //addToLogButton("ID: "+getSoftwareId64(),true,null,null);
            /*
            if (!createRegistrationCode().equals(ctx.loginSettings().getRegistrationCode())) {
                addToLog(false,"Приложение не зарегистрировано\nПолучить регистрационный код для",
                        18,0x00A00000);
                addToLogButton("ID: " + getSoftwareId64(),true,null,null);
                }
            else{
                addToLog(false,"Приложение зарегистрировано\nПолная функциональность",
                        18,0);
                }
            */
            } catch (Exception ee) {
                errorMes(createFatalMessage(ee, 10));
                }
            //addToLogImage(R.drawable.status_green);
            //---------- проверка перехвата исключений по умолчанию
            //Object oo=null;
            //oo.toString();
        }

    public void clearLog() {
        log.removeAllViews();
        }

    public void popupAndLog(String ss) {
        addToLog(ss);
        popupInfo(ss);
        }

    public void popupAndLog(String ss,int textSize, int color) {
        addToLog(false,ss,textSize,color);
        popupInfo(ss);
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shutDown = true;
        gpsService.stopService();
        saveContext();
        AppData.ctx().stopApplication();
        }

    public void scrollDown() {
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void errorMes(int emoCode,String text){
        addToLog(false,(emoCode==0 ? "" : (new String(Character.toChars(emoCode)))+" ")+text,14,0x00FF0000);
        }
    public void errorMes(String text){
        errorMes(EmoErr,text);
    }
    public void addToLog(String ss) {
        addToLog(false, ss, 0);
        }
    public void addToLog(boolean fullInfoMes, String ss) {
        addToLog(fullInfoMes, ss, 0);
    }
    @Override
    public void addToLog(String ss, int textSize) {
        addToLog(false, ss, textSize);
    }
    @Override
    public void addToLogHide(String ss) {
        addToLog(ss);
        }
    public void addToLog(boolean fullInfoMes, final String ss, final int textSize) {
        addToLog(fullInfoMes, ss, textSize, 0);
        }
    public void addToLog(boolean fullInfoMes, final String ss, final int textSize, final int textColor) {
        addToLog(fullInfoMes, ss, textSize, textColor, -1);
        }
    public void addToLog(boolean fullInfoMes, final String ss, final int textSize, final int textColor, final int imgRes) {
        guiCall(new Runnable() {
            @Override
            public void run() {
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.log, null);
                if (imgRes != -1) {
                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setImageResource(imgRes);
                    layout.addView(imageView);
                }
                TextView txt = new TextView(MainActivity.this);
                txt.setText(ss);
                int tColor = textColor==0 ? DefaultTextColor : textColor;
                txt.setTextColor(tColor | 0xFF000000);
                if (textSize != 0)
                    txt.setTextSize(textSize);
                layout.addView(txt);
                log.addView(layout);
                scrollDown();
            }
        });
    }

    public void addToLog(final String ss, final int textSize, final View.OnClickListener listener) {
        guiCall(new Runnable() {
            @Override
            public void run() {
                Button tt = new Button(MainActivity.this);
                tt.setText(ss);
                tt.setPadding(5, 5, 5, 5);
                tt.setBackgroundResource(R.drawable.button_background);
                tt.setTextColor(0xFFFFFFFF);
                tt.setOnClickListener(listener);
                tt.setTextSize(textSize);
                log.addView(tt);
                scrollDown();
            }
        });
    }

    public LinearLayout addToLogButton(String ss) {
        return addToLogButton(ss, false,null, null);
    }

    public LinearLayout addToLogButton(String ss, View.OnClickListener listener) {
        return addToLogButton(ss, false,listener, null);
        }

    public LinearLayout addToLogButton(String ss, boolean jetBrain,View.OnClickListener listener, View.OnLongClickListener listenerLong) {
        LinearLayout button = (LinearLayout) getLayoutInflater().inflate(R.layout.log_item, null);
        Button bb = (Button) button.findViewById(R.id.ok_button);
        bb.setText(ss);
        bb.setTextSize(greatTextSize);
        if (jetBrain)
            bb.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.jetbrainsmonolight));
        if (listener != null)
            bb.setOnClickListener(listener);
        if (listenerLong != null)
            bb.setOnLongClickListener(listenerLong);
        log.addView(button);
        scrollDown();
        return button;
        }

    private void preloadFromText(int resultCode) {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("text/plain");
        intent = Intent.createChooser(chooseFile, "Выбрать txt");
        startActivityForResult(intent, resultCode);
        }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public Pair<InputStream, FileDescription> openSelected(Intent data) throws FileNotFoundException {
        Uri uri = data.getData();
        String ss = getFileName(uri);
        /*
        String ss = uri.getEncodedPath();
        try {
            ss = URLDecoder.decode( ss, "UTF-8" );
            } catch (UnsupportedEncodingException e) {
                addToLog("Системная ошибка в имени файла:"+e.toString());
                addToLog(ss);
                return new Pair<>(null,null);
                }
        String ss0 = ss;
        int idx= ss.lastIndexOf("/");
        if (idx!=-1) ss = ss.substring(idx+1);
         */
        FileDescription description = new FileDescription(ss);
        addToLog(description.getOriginalFileName(), greatTextSize);
        InputStream is = getContentResolver().openInputStream(uri);
        return new Pair(is, description);
        }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        String path = "";
        try {
            if (requestCode == REQUEST_ENABLE_BT) {
                popupAndLog("BlueTooth включен, повторите команду");
            }
            if (requestCode == CHOOSE_RESULT) {
                Pair<InputStream, FileDescription> res = openSelected(data);
                InputStream is = res.o1;
                if (is == null)
                    return;
                log.addView(createMultiGraph(R.layout.graphview, ViewProcHigh));
            }
            if (requestCode == CHOOSE_RESULT_COPY) {
                final Pair<InputStream, FileDescription> pp = openSelected(data);
                final InputStream is = pp.o1;
                if (is == null)
                    return;
                File ff = new File(ctx.androidFileDirectory());
                if (!ff.exists()) {
                    ff.mkdir();
                }
                final FileOutputStream fos = new FileOutputStream(ctx.androidFileDirectory() + "/" + pp.o2.getOriginalFileName());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                int vv = is.read();
                                if (vv == -1)
                                    break;
                                fos.write(vv);
                            }
                            fos.flush();
                            fos.close();
                            is.close();
                        } catch (final Exception ee) {
                            errorMes(createFatalMessage(ee, 10));
                        }
                    }
                });
                thread.start();
            }
        } catch (Throwable ee) {
            errorMes(createFatalMessage(ee, 10));
        }
    }

    public void procArchive(FileDescription fd, boolean longClick) {
        setFullInfo(longClick);
        procArchive(fd);
        }

    //--------------------------------------------------------------------------
    public FileDescriptionList loadArchive() {
        try {
            Gson gson = new Gson();
            File ff = new File(ctx.androidFileDirectory());
            if (!ff.exists()) {
                ff.mkdir();
            }
            String ss = ctx.androidFileDirectory() + "/" + archiveFile;
            InputStreamReader out = new InputStreamReader(new FileInputStream(ss), "UTF-8");
            FileDescriptionList archive = (FileDescriptionList) gson.fromJson(out, FileDescriptionList.class);
            out.close();
            return archive;
        } catch (Exception ee) {
            errorMes("Ошибка чтения архива:\n" + ee.toString() + "\nСоздан пустой");
            popupInfo("Ошибка чтения архива,создан пустой");
            FileDescriptionList archive2 = new FileDescriptionList();
            saveArchive(archive2);
            return archive2;
        }
    }

    public void saveArchive(FileDescriptionList archive) {
        try {
            Gson gson = new Gson();
            File ff = new File(ctx.androidFileDirectory());
            if (!ff.exists()) {
                ff.mkdir();
            }
            String ss = ctx.androidFileDirectory() + "/" + archiveFile;
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(ss), "UTF-8");
            gson.toJson(archive, out);
            out.flush();
            out.close();
        } catch (Exception ee) {
            errorMes("Ошибка записи архива:\n" + ee.toString());
        }
        popupInfo("Ошибка записи архива");
    }

    //------------------------------------------------------------------------
    private ArrayList<MenuItemAction> menuList = new ArrayList<>();

    private String[] createMenuTitles() {
        String out[] = new String[menuList.size()];
        for (int i = 0; i < out.length; i++)
            out[i] = menuList.get(i).title;
        return out;
    }

    public void procMenuItem(int index) {
        menuList.get(index).onSelect();
    }

    public void createMenuList() {
        menuList.clear();
        menuList.add(new MenuItemAction("Связь с сервером") {
            @Override
            public void onSelect() {
                    new MILogin(MainActivity.this);
                }
            });
        menuList.add(new MenuItemAction("Ближайшие на карте") {
            @Override
            public void onSelect() {
                new MIMapCareNearest(MainActivity.this);
            }
            });
        menuList.add(new MenuItemAction("История борта") {
            @Override
            public void onSelect() {
                new MIMapCareStory(MainActivity.this);
            }
            });
        menuList.add(new MenuItemAction("Привязка к пассажиру") {
            @Override
            public void onSelect() {
                new MIMapPassenger(MainActivity.this);
            }
            });
        menuList.add(new MenuItemAction("Маршруты") {
            @Override
            public void onSelect() {
                new MIMapRoutes(MainActivity.this);
            }
            });
        menuList.add(new MenuItemAction("Сегменты") {
            @Override
            public void onSelect() {
                new MIMapSegments(MainActivity.this);
            }
            });
        menuList.add(new MenuItemAction("Очистить ленту") {
            @Override
            public void onSelect() {
                log.removeAllViews();
                String title = "Транспорт NSK";
                addToLog(false, title, 22, 0);
                }
            });
        menuList.add(new MenuItemAction("Настройки") {
            @Override
            public void onSelect() {
                new SettingsMenu(MainActivity.this);
            }
            });
        /*
        if (!ctx.loginSettings().isTechnicianMode())
            new MIDeleteFromArchive(this);
        new MIArchive(this);
        new MIArchiveFull(this);
        new MIFullScreen(this);
        new MIGroupCreate(this);
        new MIGroupDestroy(this);
        if (ctx.cState()== AppData.CStateGreen && isAllEnabled()){
            new MIUpLoad(MainActivity.this);
            }
        new MIMap(this);
        if (!ctx.loginSettings().isTechnicianMode()) {
            new MISendMail(this);
            new MIResultsPlayer(this);
            new MIExport(this);
            new MIFileCopy(this);
            new MIFileProcess(this, false);
            new MIFileProcess(this, true);
            }
        new MIExportAndSendMail(this);
         */
        new MIAbout(this);
        if (!ctx.loginSettings().isTechnicianMode()) {
            menuList.add(new MenuItemAction("Регистрация") {
                @Override
                public void onSelect() {
                    new RegistrationMenu(MainActivity.this);
                }
                });
            }
        menuList.add(new MenuItemAction("Выход") {
            @Override
            public void onSelect() {
                finish();
            }
        }   );
    }

    private I_ArchiveSelector uploadSelector = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            File file = new File(ctx.androidFileDirectory() + "/" + fd.getOriginalFileName());
        }
    };
    private I_ArchiveSelector deleteSelector = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            File file = new File(ctx.androidFileDirectory() + "/" + fd.getOriginalFileName());
            file.delete();
        }
    };
    //-------------------------------------------------------------------------------------------------
    private I_ArchiveSelector archiveProcView = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            procArchive(fd, false);
        }
    };
    private I_ArchiveSelector archiveProcViewFull = new I_ArchiveSelector() {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            procArchive(fd, true);
        }
    };

    //----------------------------------------------------------------------------------------------
    public void moveFile(String src, String dst) throws Exception {
        BufferedReader fd1 = new BufferedReader(new InputStreamReader(new FileInputStream(src), "Windows-1251"));
        BufferedWriter fd2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst), "Windows-1251"));
        String ss;
        while ((ss = fd1.readLine()) != null) {
            fd2.write(ss);
            fd2.newLine();
        }
        fd2.flush();
        fd1.close();
        fd2.close();
        File file = new File(src);
        file.delete();
    }

    //----------------------------------------------------------------------------------------------
    public void selectFromArchive(String title, final I_ArchiveSelector selector) {
        final ArrayList<FileDescription> ss = createArchive();
        ArrayList<String> out = new ArrayList<>();
        for (FileDescription ff : ss)
            out.add(ff.toString());
        new ListBoxDialog(this, out, title, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                selector.onSelect(ss.get(index), false);
            }

            @Override
            public void onLongSelect(int index) {
                selector.onSelect(ss.get(index), true);
            }

            @Override
            public void onCancel() {
            }
        }).create();
    }

    public void selectMultiFromArchive(String title, final I_ArchiveMultiSelector selector) {
        selectMultiFromArchive(false, title, selector);
    }

    public void selectMultiFromArchive(boolean dirList, String title, final I_ArchiveMultiSelector selector) {
        final ArrayList<FileDescription> ss = dirList ? createDirArchive() : createArchive();
        final ArrayList<String> list = new ArrayList<>();
        for (FileDescription ff : ss)
            list.add(dirList ? ff.getOriginalFileName() : ff.toString());
        new MultiListBoxDialog(this, title, list, new MultiListBoxListener() {
            @Override
            public void onSelect(boolean[] selected) {
                FileDescriptionList out = new FileDescriptionList();
                for (int i = 0; i < ss.size(); i++)
                    if (selected[i])
                        out.add(ss.get(i));
                setDefferedList(out);
                selector.onSelect(out, false);
            }
        });
    }

    public void showWaveForm() {
        final FileDescriptionList ss = createArchive();
        ArrayList<String> out = new ArrayList<>();
        for (FileDescription ff : ss)
            out.add(ff.toString());
        new ListBoxDialog(this, out, "Просмотр волны", new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {

            }

            @Override
            public void onLongSelect(int index) {
            }

            @Override
            public void onCancel() {
            }
        }).create();
    }

    public void addArchiveItemToLog(final FileDescription ff) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setFullInfo(false);
                    FileInputStream fis = new FileInputStream(ctx.androidFileDirectory() + "/" + ff.getOriginalFileName());
                    addToLog(ff.toString(), greatTextSize);
                } catch (Throwable e) {
                    errorMes("Файл не открыт: " + ff.getOriginalFileName() + "\n" + createFatalMessage(e, 10));
                }
            }
        };
        View.OnLongClickListener listenerLong = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    setFullInfo(true);
                    FileInputStream fis = new FileInputStream(ctx.androidFileDirectory() + "/" + ff.getOriginalFileName());
                    addToLog(ff.toString());
                } catch (Throwable e) {
                    errorMes("Файл не открыт: " + ff.getOriginalFileName() + "\n" + e.toString());
                    return false;
                }
                return true;
            }
        };
        addToLogButton(ff.toString(), false,listener, listenerLong);
    }


    public LinearLayout log() {
        return log;
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                addToLog("Ошибка определения сервиса местоположения:\n" + e.toString());
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    //---------------------------------------- Параметры трубы --------------------------------------
    /*
    public String getSimCardICC() {
        SubscriptionManager manager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        int defaultSmsId = SubscriptionManager.getDefaultSmsSubscriptionId();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
            }
        SubscriptionInfo info = manager.getActiveSubscriptionInfo(defaultSmsId);
        return info.getIccId();
        }
     */
    //-----------------------------------------------------------------------------------------------
    Retrofit retrofit = null;
    private Runnable httpKeepAlive = new Runnable() {
        @Override
        public void run() {
            if (AppData.ctx().cState()== AppData.CStateGray)
                return;
            String token = ctx.loginSettings().getSessionToken();
            System.out.println(token);
            new NetCall<JInt>().call(MainActivity.this,ctx.getService().keepalive(token), new NetBack() {
                @Override
                public void onError(int code, String mes) {
                    ctx.toLog(false,"Ошибка keep alive: "+mes+". Сервер недоступен");
                    sessionOff();
                    }
                @Override
                public void onError(UniException ee) {
                    ctx.toLog(false,"Ошибка keep alive: "+ee.toString()+". Сервер недоступен");
                    sessionOff();
                    }
                @Override
                public void onSuccess(Object val) {
                }
            });
            if (!shutDown && AppData.ctx().isApplicationOn())
                setDelay(AppData.CKeepALiveTime, httpKeepAlive);
        }
    };
    public void retrofitDisconnect() {
        ctx.cState(AppData.CStateGray);
        }

    public void retrofitConnect() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(AppData.HTTPTimeOut, TimeUnit.SECONDS)
                .connectTimeout(AppData.HTTPTimeOut, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" +ctx.loginSettings().getDataSetverIP() + ":" + ctx.loginSettings().getDataServerPort())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        ctx.service(retrofit.create(RestAPIBase.class));
        ctx.service2(retrofit.create(RestAPI.class));
        }
    public void  sessionOn(){
        AppSettings log = ctx.loginSettings();
        httpKeepAlive.run();                // Сразу и потом по часам
        ctx.cState(AppData.CStateGreen);
        }
    public void sessionOff() {
        putHeaderInfo("");
        cancelDelay(httpKeepAlive);
        ctx.cState(AppData.CStateGray);
        }
    public void setDelay(int sec, Runnable code) {          // С возвратом в GUI
        event.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(code);
                }
            }, sec * 1000);
        }
    public void cancelDelay(Runnable code) {
        event.removeCallbacks(code);
        }
    public String createRegistrationCode(){
        return Registration.createRegistrationCode(getSoftwareId());
        }
    public boolean isAllEnabled(){
        String ss = ctx.loginSettings().getRegistrationCode();
        return ss.equals(createRegistrationCode()) && ss.length()!=0;
        }
    public String getSoftwareId() {         // Закодированный
        String ss = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return ss;
        }
    public String getSoftwareId64() {         // Закодированный
        return Base64Coder.encodeString(getSoftwareId());
        }

    @Override
    public void notify(String s) {

    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void onClose() {

    }
}
