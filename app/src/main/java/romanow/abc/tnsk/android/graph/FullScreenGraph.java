package romanow.abc.tnsk.android.graph;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.ArrayList;
import romanow.abc.core.Pair;
import romanow.abc.core.constants.Values;
import romanow.abc.core.entity.server.TSegmentStatistic;
import romanow.abc.core.prepare.Cell;
import romanow.abc.core.prepare.DayCellList;
import romanow.abc.tnsk.android.R;
import romanow.abc.tnsk.android.service.AppData;
import romanow.abc.tnsk.android.service.BaseActivity;

public class FullScreenGraph  extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.graph_gorizontal);
            getSupportActionBar().hide();
            LinearLayout lrr = (LinearLayout) findViewById(R.id.viewPanelHoriz);
            LinearLayout hd = (LinearLayout) findViewById(R.id.viewPanelHead);
            ArrayList<Pair<String, TSegmentStatistic>> statList = AppData.ctx().getStatList();
            LinearLayout graph = createMultiGraph(R.layout.graphviewhoriz,0);
            lrr.addView(graph);
            int idx=0;
            for (Pair<String, TSegmentStatistic> statistic : statList) {
                Button bb = new Button(this);
                bb.setTextColor(getPaintColor(idx) | 0xFF000000);
                bb.setBackgroundColor(0xFF00574B);
                bb.setTextSize(20);
                bb.setHeight(40);
                bb.setWidth(150);
                bb.setPadding(10, 0, 0, 0);
                bb.setText(statistic.o1);
                hd.addView(bb);
                paintOneGraph(getMultiGraph(), statistic.o2, getPaintColor(idx++) | 0xFF000000);
                }
            } catch (Exception ee){
                addToLog(createFatalMessage(ee,10));
                }
    }
    public void paintOneGraph(LineGraphView graphView, TSegmentStatistic statistic , int color){
        int size = statistic.getWeekCells().size()*(Values.LastStatisticHour-Values.FirstStatisticHour);
        GraphView.GraphViewData zz[] = new GraphView.GraphViewData[size];
        int idx=0;
        for(DayCellList day : statistic.getWeekCells())
            for (Cell cell : day.getHourCells()){
            zz[idx] = new GraphView.GraphViewData(idx,cell.middle());
            idx++;
            }
        GraphViewSeries series = new GraphViewSeries(zz);
        series.getStyle().color = color | 0xFF000000;
        graphView.addSeries(series);
        }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void clearLog() {}

    @Override
    public void addToLog(String ss, int textSize) {}

    @Override
    public void addToLogHide(String ss) {}

    @Override
    public void addToLog(boolean fullInfoMes, String ss, int textSize, int textColor) {}

    @Override
    public void popupAndLog(String ss) {}

    @Override
    public void errorMes(String ss) { }

    @Override
    public void notify(String s) { }

    @Override
    public boolean isFinish() {
        return false;
        }

    @Override
    public void onClose() {

    }
}
