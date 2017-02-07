package com.example.tong.jiaowuxitong.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tong.jiaowuxitong.GlobalResource;
import com.example.tong.jiaowuxitong.R;
import com.example.tong.jiaowuxitong.entity.VOCourse;
import com.example.tong.jiaowuxitong.entity.VOCourseSys;
import com.example.tong.jiaowuxitong.net.GsonUtil;
import com.example.tong.jiaowuxitong.net.Message;
import com.example.tong.jiaowuxitong.net.NetUtil;
import com.example.tong.jiaowuxitong.view.custom.CourseDegreeDeptDataSet;
import com.example.tong.jiaowuxitong.view.custom.CourseDegreeIXAxisValueFormat;
import com.example.tong.jiaowuxitong.view.custom.CourseDegreeIYAxisValueFormat;
import com.example.tong.jiaowuxitong.view.custom.CourseDegreeStudentDataSet;
import com.example.tong.jiaowuxitong.view.custom.CourseDegreeViewMaker;
import com.example.tong.jiaowuxitong.view.custom.StringUtils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

import static java.util.Arrays.sort;


public class CourseDegreeFragment extends BaseFragment {


    private static final int UPDATE = 155;
    private BarChartFragment chartFragment3;

    public CourseDegreeFragment() {
        // Required empty public constructor
    }

    private VOCourse voCourse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            voCourse = (VOCourse) getArguments().getSerializable("course");
        }
    }


    private BarChartFragment chartFragment1;
    private BarChartFragment chartFragment2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.course_degree_fragment, container, false);
//        mChart = (BarChart) view.findViewById(R.id.barChart);
//        progressBar = (ProgressBar) view.findViewById(R.id.pb);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        com.example.tong.jiaowuxitong.TestUtil.log("onViewCreated", "onViewCreated");
//        progressBar.setVisibility(View.VISIBLE);
//        mChart.setVisibility(View.INVISIBLE);
        chartFragment1 = (BarChartFragment) Fragment.instantiate(mContext, BarChartFragment.class.getName());
        chartFragment2 = (BarChartFragment) Fragment.instantiate(mContext, BarChartFragment.class.getName());
        chartFragment3 = (BarChartFragment) Fragment.instantiate(mContext, BarChartFragment.class.getName());

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.container1, chartFragment1, "first");
        transaction.add(R.id.container2, chartFragment2, "second");
        transaction.add(R.id.container3, chartFragment3, "thrid");
        transaction.commit();
        initBarChart();
        NetUtil.asyncPost(GsonUtil.toJson(voCourse), GlobalResource.FIND_COURSE, UPDATE);
        NetUtil.asyncPost(GsonUtil.toJson(voCourse), GlobalResource.GET_DEGREE_EVA_OF_COURSE, COURSE_EVA);
    }

    private void initBarChart() {
        IAxisValueFormatter iax = new CourseDegreeIXAxisValueFormat();
        IAxisValueFormatter iay = new CourseDegreeIYAxisValueFormat();
        MarkerView marker = new CourseDegreeViewMaker(mContext, iax);
        chartFragment1.initChart(iax, iay, marker, 5, "学生成绩分布");


    }

    private static final int COURSE_EVA = 996;

    @Override
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onGet(Message msg) {
        if (msg != null && msg.tag == UPDATE) {
            voCourse = GsonUtil.fromJson((String) msg.msg, VOCourse.class);

        } else if (msg != null && msg.tag == COURSE_EVA) {
//            ArrayList<Float> floatArrayList = GsonUtil.fromJson((String) msg.msg, new TypeToken<ArrayList<Float>>() {
//            }.getType());
            VOCourseSys voCourseSys = GsonUtil.fromJson((String) msg.msg, VOCourseSys.class);

            if (voCourseSys != null) {
                List<Float> degreesOfStudent = voCourseSys.getDegreesOfStudent();
                setData1(degreesOfStudent);
                setData2(voCourseSys.getCourses());
            }


        }
    }

    private void setData2(final List<VOCourse> courses) {
        if (courses != null) {

            IAxisValueFormatter iax = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int tmp = (int) value;
                    if (tmp < courses.size())
                        return courses.get(tmp).getName();
                    else return "";
                }
            };
            IAxisValueFormatter iay = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    String s = StringUtils.formatFloat(value, 2);
                    return s;
                }
            };
//            MarkerView marker = new CourseDegreeViewMaker(mContext, iax);
            chartFragment2.initChart(iax, iay, null, courses.size(), "部门课程优秀率");

            CourseDegreeDeptDataSet dataSet = new CourseDegreeDeptDataSet(courses.size());
            int c = 0;
            for (VOCourse voCourse : courses
                    ) {
                dataSet.add(voCourse);
                if (voCourse.getExcellentRate() < this.voCourse.getExcellentRate()) {
                    c++;
                }
            }


            BarDataSet set1 = new BarDataSet(dataSet.getLe(), "本门课程优秀率占部门的前" + StringUtils.float2PercentString(c * 1.f / courses.size(), 2));
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
//
            chartFragment2.setData(dataSets);

            VOCourse[] vs = new VOCourse[courses.size()];
            for (int i = 0; i < courses.size(); i++) {
                vs[i] = courses.get(i);
            }
            sort(vs, new Comparator<VOCourse>() {
                @Override
                public int compare(VOCourse lhs, VOCourse rhs) {
                    return (int) (lhs.getUnpassRate() - rhs.getUnpassRate());
                }
            });

            chartFragment3.initChart(iax, iay, null, courses.size(), "部门课程不及格率");

            dataSet = new CourseDegreeDeptDataSet(courses.size());
            c = 0;
            for (VOCourse voCourse : vs
                    ) {
                dataSet.add(voCourse);
                if (voCourse.getUnpassRate() > this.voCourse.getUnpassRate()) {
                    c++;
                }
            }


            set1 = new BarDataSet(dataSet.getLe(), "本门课程不及格率占部门的前" + StringUtils.float2PercentString(c * 1.f / courses.size(), 2));
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
//
            chartFragment3.setData(dataSets);
        }

    }

    private void setData1(List<Float> data) {
        if (data != null) {

            CourseDegreeStudentDataSet dataSet = new CourseDegreeStudentDataSet();
            int c = 0;
            for (Float f : data
                    ) {
                dataSet.add(f);
            }

            BarDataSet set1 = new BarDataSet(dataSet.getLe(), "本门课程学生成绩分布");
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            chartFragment1.setData(dataSets);
        }
    }
}
