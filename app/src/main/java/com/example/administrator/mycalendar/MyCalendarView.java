package com.example.administrator.mycalendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/7.
 */
public class MyCalendarView extends View {

    private Paint mMonthPaint;//月
    private Paint mWeekPaint;//周
    private Paint mDayPaint;//日
    private Paint mLinePaint;//分割线
    private Paint mDayBgPaint;//日背景
//白彩，白绿，白黑  黑绿，黑蓝
    private float mWidth;
    private float mHeight;

    //整个View的高度分为三部分：月、周、日
    private float mMonthHeight;//月份高度
    private float mWeekHeight;//周高度
    private float mDayHeight;//日高度
    private float mTextSpec;//箭头与文字间距
    private float mArrowWidth;//箭头宽度
    private float mArrowHeight;//箭头宽度

    private Date mCurrentDate;//当前的日期

    private int mCurrentYear;//当前的年
    private int mCurrentMonth;//当前的月
    private int mCurrentDay;//当前的日

    private int mSwitchYear;//切换到的年
    private int mSwitchMonth;//切换到的月
    private int mSwitchDay;//切换到的日

    private int mCurrentDayOfMonth;//当前月份总天数
    private int mFirstWeekIndex;//当月第一天星期的索引
    private int mEndWeekIndex;//当月最后一天星期的索引

    private int mFirstLineDayCount;//第一行天数
    private int mEndLine;//最后一行天数
    private boolean isTouch = false;//是否响应事件

    private int mClickYear;//点击的年
    private int mClickMonth;//点击的月
    private int mClickDay;//点击的日

    private DateCallBack mDateCallBack;
    public MyCalendarView(Context context) {
        super(context);
    }

    public MyCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context){
        mMonthPaint = new Paint();
        mMonthPaint.setAntiAlias(true);
        mMonthPaint.setColor(Color.BLACK);
        mMonthPaint.setTextSize(50);
        mMonthPaint.setStrokeWidth(3);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.parseColor("#b6b6b6"));
        mLinePaint.setStrokeWidth((float) 0.5);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mWeekPaint = new Paint();
        mWeekPaint.setAntiAlias(true);
        mWeekPaint.setColor(Color.BLACK);
        mWeekPaint.setTextSize(50);
        mWeekPaint.setStrokeWidth(3);
        mWeekPaint.setTextAlign(Paint.Align.CENTER);

        mDayPaint = new Paint();
        mDayPaint.setAntiAlias(true);
        mDayPaint.setColor(Color.BLACK);
        mDayPaint.setTextSize(50);
        mDayPaint.setStrokeWidth(3);
        mDayPaint.setTextAlign(Paint.Align.CENTER);

        mDayBgPaint = new Paint();
        mDayBgPaint.setAntiAlias(true);
        mDayBgPaint.setColor(Color.parseColor("#009cfd"));
        mDayBgPaint.setStyle(Paint.Style.FILL);

        mWidth = getPhoneW(context);
        mMonthHeight = 150;
        mWeekHeight = 150;
        mDayHeight = 140;
        mTextSpec = 120;
        mArrowWidth = 23;
        setMonth();
    }

    public void setDateCallBack(DateCallBack dateCallBack){
        this.mDateCallBack = dateCallBack;
    }

    private void setMonth(){
        mCurrentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);

        mCurrentYear = calendar.get(Calendar.YEAR);//获取当前的月
        mCurrentMonth = calendar.get(Calendar.MONTH) +1;//获取当前的月
        mCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);//获取当前的日
        //第一次加载蓝色背景在当前日期
        mClickYear = mCurrentYear;
        mClickMonth = mCurrentMonth;
        mClickDay = mCurrentDay;

        mCurrentDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取当月总天数
        //周日索引为0
        calendar.set(Calendar.DATE,1);//将日期定位到当月第一天
        mFirstWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;//获取当月第一天星期的索引
        calendar.set(Calendar.DATE,mCurrentDayOfMonth);//将日期定位到当月最后一天
        mEndWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;//获取当月最后一天星期的索引

        mFirstLineDayCount = 7 - mFirstWeekIndex;//第一行天数
        mEndLine = mEndWeekIndex + 1;//最后一行天数
        //Log.i("touch","mFirstLineDayCount="+mFirstLineDayCount+"---mEndLine="+mEndLine);
        mSwitchYear = calendar.get(Calendar.YEAR);//获取切换到的年
        mSwitchMonth= calendar.get(Calendar.MONTH)+1;//获取切换到的月
        mSwitchDay = calendar.get(Calendar.DAY_OF_MONTH);//获取切换到的日

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = (int) mWidth;
        int height = (int) (mMonthHeight + mWeekHeight + mDayHeight * 6);
        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST){
            width = (int) mWidth;
        }
        if(heightMode == MeasureSpec.EXACTLY){
            height = (int) (mMonthHeight + mWeekHeight + mDayHeight * 6);
        }else if(heightMode == MeasureSpec.AT_MOST){
            height = (int) (mMonthHeight + mWeekHeight + mDayHeight * 6);
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mSwitchYear==mClickYear&&mSwitchMonth == mClickMonth) {
            drawDayCircle(canvas);
        }
        drawMonth(canvas);
        drawWeek(canvas);
        drawDay(canvas,mFirstWeekIndex,mCurrentDayOfMonth);
        Log.i("touch","mFirstLineDayCount="+mFirstLineDayCount+"---mEndLine="+mEndLine);
    }

    //绘制月份跟箭头
    private void drawMonth(Canvas canvas){
        String data = getMonthStr(mCurrentDate);//mCurrentDate为当前的日前
        //计算文本的宽高
        Rect rect = new Rect();
        mMonthPaint.getTextBounds(data,0,data.length(),rect);
        float textStartX = mWidth/2 - rect.width()/2;
        float textY = (mMonthHeight - rect.height())/2 + rect.height() - 5;
        //绘制文字
        canvas.drawText(data,textStartX,textY,mMonthPaint);
        //绘制分割线
        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setStrokeWidth(1);
        canvas.drawLine(0,mMonthHeight,mWidth,mMonthHeight,mLinePaint);

        mLinePaint.setStrokeWidth(4);
        //mLinePaint.setStrokeJoin(Paint.Join.MITER);

        //绘制左箭头
        Path path = new Path();
        path.moveTo(textStartX-mTextSpec,55);
        path.lineTo(textStartX-mTextSpec-mArrowWidth,mMonthHeight/2);
        path.lineTo( textStartX-mTextSpec,mMonthHeight-55);
        canvas.drawPath(path,mLinePaint);

        //绘制右箭头
        Path path1 = new Path();
        path1.moveTo(textStartX+mTextSpec+rect.width(),55);
        path1.lineTo(textStartX+mTextSpec+rect.width()+mArrowWidth,mMonthHeight/2);
        path1.lineTo(textStartX+mTextSpec+rect.width(),mMonthHeight-55);
        canvas.drawPath(path1,mLinePaint);

    }

    //绘制周
    private void drawWeek(Canvas canvas){
        //每个周所占用的宽度
        float weekWidth = mWidth/7;
        drawWeekText(canvas,"日",weekWidth/2);
        drawWeekText(canvas,"一",weekWidth/2+weekWidth*1);
        drawWeekText(canvas,"二",weekWidth/2+weekWidth*2);
        drawWeekText(canvas,"三",weekWidth/2+weekWidth*3);
        drawWeekText(canvas,"四",weekWidth/2+weekWidth*4);
        drawWeekText(canvas,"五",weekWidth/2+weekWidth*5);
        drawWeekText(canvas,"六",weekWidth/2+weekWidth*6);

    }

    private void drawWeekText(Canvas canvas,String text,float weekWidth){
        Rect rect = new Rect();
        String textRect = "日";
        mWeekPaint.getTextBounds(textRect,0,textRect.length(),rect);
        float textX = weekWidth;
        //FontUtil.getFontLeading(mPaint)
        float textY = mMonthHeight + (mWeekHeight - rect.height())/2 + rect.height() - 5;
        canvas.drawText(text,textX,textY,mWeekPaint);
    }

    /**
     *
     * @param canvas
     * @param weekIndex 当月第一天星期索引
     * @param monthDayCount 当月的总天数
     */
    private void drawDay(Canvas canvas,int weekIndex,int monthDayCount){
        Rect rect = new Rect();
        String textRect = "1";
        mDayPaint.getTextBounds(textRect,0,textRect.length(),rect);

        //每日所占用的宽度
        float dayWidth = mWidth/7;
        //当前绘制日期的横向距离
        float currentWidth = dayWidth*weekIndex - dayWidth/2;
        //当前绘制日期的纵向距离
        float currentHeight = mMonthHeight + mWeekHeight +
                (mDayHeight - rect.height())/2 + rect.height() - 5;
        for(int i =0;i<monthDayCount;i++){
            if(mSwitchYear==mClickYear&&mSwitchMonth==mClickMonth
                    &&i+1==mClickDay){
                mDayPaint.setColor(Color.WHITE);
            }else {
                mDayPaint.setColor(Color.BLACK);
            }
            if(weekIndex!=0&&weekIndex%7==0){
                currentWidth = dayWidth/2;
                currentHeight = currentHeight + mDayHeight;
                canvas.drawText(i+1+"",currentWidth,currentHeight,mDayPaint);
                //绘制分割线
                float lineHeight = currentHeight - (mDayHeight - rect.height())/2 - rect.height() + 5;
                mLinePaint.setStrokeWidth((float) 0.5);
                canvas.drawLine(0,lineHeight,mWidth,lineHeight,mLinePaint);
            }else {
                currentWidth = currentWidth + dayWidth;
                canvas.drawText(i+1+"",currentWidth,currentHeight,mDayPaint);
            }
            weekIndex++;
        }

        //当切换到点击日期的年月份
        if(mClickYear == mSwitchYear &&
                mClickMonth == mSwitchMonth){
            //drawDayCircle(canvas);
        }
    }

    //为当前日添加圆形背景
    private void drawDayCircle(Canvas canvas){

        //RectF rectF = new RectF(dayX,dayY-mDayHeight,dayX+mWidth/7,dayY);
        float dayY = 0,dayX = 0;//当前日的X、Y轴坐标
        if(mClickDay<=mFirstLineDayCount){
            //点击了第一行
            int clickDayCount = 7-(mFirstLineDayCount - mClickDay);
            dayX = (clickDayCount-1)*(mWidth/7);
            dayY = mMonthHeight + mWeekHeight + mDayHeight;
        } else {
            //点击日期相对于所在行的位置
            int clickDayCount = (mClickDay - mFirstLineDayCount - 1) % 7;
            //除去第一行跟最后一行的行数
            int centerColumnY = (mClickDay - mFirstLineDayCount - clickDayCount) / 7 + 2;
            Log.i("calendar", "centerColumnY=" + centerColumnY);
            dayX = clickDayCount * (mWidth / 7);
            dayY = mMonthHeight + mWeekHeight + centerColumnY * mDayHeight;

        }
        float radiusX = dayX+(mWidth/7)/2;
        float radiusY = dayY - mDayHeight/2;
        Log.i("calendar","x="+radiusX+"---y="+radiusY);
        float radius = mDayHeight/2 - 10;
        canvas.drawCircle(radiusX,radiusY,radius,mDayBgPaint);

    }

    float startX = 0,startY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float endX,endY;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                clickEvent(startX,startY,endX,endY);
                return true;
        }
        return super.onTouchEvent(event);
    }
    //模拟的点击事件
    private void clickEvent(float startX,float startY,float endX,float endY){
        String data = getMonthStr(mCurrentDate);
        //计算标题文本的宽高
        Rect rect = new Rect();
        mMonthPaint.getTextBounds(data,0,data.length(),rect);
        float textStartX = mWidth/2 - rect.width()/2;//辩题文字的X轴起始坐标
        float arrowLeftStartX = textStartX - mTextSpec - (mArrowWidth+60);
        float arrowLeftEndX = textStartX - mTextSpec +60;
        float arrowLeftStartY = 10;
        float arrowLeftEndY = mMonthHeight-10;
        //当按下和抬起坐标都在箭头规定的范围内视为点击
        //点解了左箭头
        if(startX>arrowLeftStartX&&startX<arrowLeftEndX
                &&startY>arrowLeftStartY&&startY<arrowLeftEndY
            &&endX>arrowLeftStartX&&endX<arrowLeftEndX
                &&endY>arrowLeftStartY&&endY<arrowLeftEndY){
            subDate();
        }

        float arrowRightStartX = textStartX + rect.width() + mTextSpec - 60;
        float arrowRightEndX = textStartX + rect.width() + mTextSpec +mArrowWidth + 60;
        float arrowRightStartY = 10;
        float arrowRightEndY = mMonthHeight-10;
        //点解了右箭头
        if(startX>arrowRightStartX && startX<arrowRightEndX
                && startY>arrowRightStartY && startY<arrowRightEndY
                && endX>arrowRightStartX && endX<arrowRightEndX
                && endY>arrowRightStartY && endY<arrowRightEndY){
            addDate();
        }
        clickDate(startX,startY,endX,endY);
    }

    //日期减月
    private void subDate(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);
        calendar.add(Calendar.MONTH,-1);

        mCurrentDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取当月总天数
        //周日索引为0
        calendar.set(Calendar.DATE,1);//将日期定位到当月第一天
        mFirstWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;//获取当月第一天星期的索引
        calendar.set(Calendar.DATE,mCurrentDayOfMonth);//将日期定位到当月最后一天
        mEndWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;//获取当月最后一天星期的索引
        mFirstLineDayCount = 7 - mFirstWeekIndex;//第一行天数
        mEndLine = mEndWeekIndex + 1;//最后一行天数

        mCurrentDate = calendar.getTime();

        mSwitchYear = calendar.get(Calendar.YEAR);//获取切换到的年
        mSwitchMonth= calendar.get(Calendar.MONTH) +1;//获取切换到的月
        mSwitchDay = calendar.get(Calendar.DAY_OF_MONTH);//获取切换到的日

        invalidate();
        Log.i("calendar","subDate---------------");

    }

    //日期加月
    private void addDate(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);
        calendar.add(Calendar.MONTH,1);

        mCurrentDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获取当月总天数
        //周日索引为0
        calendar.set(Calendar.DATE,1);//将日期定位到当月第一天
        mFirstWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;//获取当月第一天星期的索引
        calendar.set(Calendar.DATE,mCurrentDayOfMonth);//将日期定位到当月最后一天
        mEndWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;//获取当月最后一天星期的索引
        mFirstLineDayCount = 7 - mFirstWeekIndex;//第一行天数
        mEndLine = mEndWeekIndex + 1;//最后一行天数

        mCurrentDate = calendar.getTime();

        mSwitchYear = calendar.get(Calendar.YEAR);//获取切换到的年
        mSwitchMonth= calendar.get(Calendar.MONTH) +1;//获取切换到的月
        mSwitchDay = calendar.get(Calendar.DAY_OF_MONTH);//获取切换到的日

        invalidate();
        Log.i("calendar","addDate---------------");
    }

    //通过坐标获取当前点击的日期
    private void clickDate(float startX,float startY,float endX,float endY){
        int startLineX,startColumnY,endLineX,endColumnY;
        int clickStartDay = 0,clickEndDay = 0;
        //当点击周以下的位置，即日部分
        if(startY>mMonthHeight+mWeekHeight&&endY>mMonthHeight+mWeekHeight){
            //按下时的天数
            startLineX = (int) (startX/(mWidth/7)) +1;
            startColumnY = (int) ((startY-mMonthHeight-mWeekHeight)/mDayHeight) + 1;
            if(startColumnY==1){//点击第一行
                if(startLineX-1>=mFirstWeekIndex){
                    clickStartDay = startLineX - mFirstWeekIndex;
                }else {//点击了第一行空白处
                    return;
                }
            }else {
                //中间整行数*7 + 第一行天数 + 最后一行天数
                clickStartDay = (startColumnY-2)*7 + mFirstLineDayCount + startLineX;
                if(clickStartDay>mCurrentDayOfMonth){//大于当月总天数
                    return;
                }
            }

            //抬起时的天数
            endLineX = (int) (endX/(mWidth/7)) +1;
            endColumnY = (int) ((endY-mMonthHeight-mWeekHeight)/mDayHeight) + 1;
            if(endColumnY==1){//点击第一行
                if(endLineX-1>=mFirstWeekIndex){
                    clickEndDay = endLineX - mFirstWeekIndex;
                }else {//点击了第一行空白处
                    return;
                }
            }else {
                //中间整行数*7 + 第一行天数 + 最后一行天数
                clickEndDay = (endColumnY-2)*7 + mFirstLineDayCount + endLineX;
                if(clickStartDay>mCurrentDayOfMonth){//大于当月总天数
                    return;
                }
            }

            if(clickStartDay==clickEndDay){
                mClickYear = mSwitchYear;
                mClickMonth = mSwitchMonth;
                mClickDay = clickStartDay;
                invalidate();
                String date = mSwitchYear+"年"+mSwitchMonth+"月"+clickStartDay+"日";
                if(mDateCallBack!=null){
                    mDateCallBack.onClick(date);
                }
                Log.i("touch",date);
            }
        }
    }

    /**获取月份标题*/
    private String getMonthStr(Date month){
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
        return df.format(month);
    }

    //获取日期对象
    private Date getData(String str){
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
            return df.parse(str);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取手机分辨率--W
     * */
    public static int getPhoneW(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int disW = dm.widthPixels;
        return disW;
    }

    public interface DateCallBack{
        void onClick(String date);
    }
}
