package com.example.lenovotab.assignment2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class PlotView extends View {
    private ArrayList<Float> array = new ArrayList<>(11);
    private ArrayList<Float> mean = new ArrayList<>(11);
    private ArrayList<Float> stdev = new ArrayList<>(11);
    private ArrayList<Integer> arrayTime = new ArrayList<>(11);
    private int counterX = 0;
    private String sensor = "";

    public PlotView(Context context) {
        super(context);
    }

    public PlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public PlotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int margin = 100;
        int width = roundToTen(this.getWidth())-margin;
        int height = roundToTen((int)(this.getHeight()*0.9))-margin;

        drawGraph(canvas, margin, margin, height, width,  margin);
    }

    private void drawAxis (Canvas canvas, int height, int width, int margin){

        Paint p = new Paint();
        p.setColor(Color.BLACK);

        float textSize = p.getTextSize();
        p.setTextSize(textSize * 3);

        canvas.drawText(sensor, width/2, 25,  p);
        p.setTextSize(textSize * 2);
        canvas.drawText("Time (x100 msec)", (width+margin)/2, height + 75,  p);

        String yName = "";
        if (sensor.equals("Accelerator")) {
            yName = "m/s^2";
        }
        else if (sensor.equals("Light")) {
            yName = "lx";
        }
        canvas.save();
        canvas.rotate(270f);
        canvas.drawText(yName, -(height+margin)/2 , 25, p);
        canvas.restore();
    }

    private void drawGraph(Canvas canvas, float topx, float topy, int height, int width, int margin){
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        float textSize = p.getTextSize();
        p.setTextSize(textSize * 2);

        Paint currentColor = new Paint();
        currentColor.setColor(Color.YELLOW);
        currentColor.setTextSize(textSize *2);

        Paint meanColor = new Paint();
        meanColor.setColor(Color.CYAN);
        meanColor.setTextSize(textSize *2);

        Paint stdevColor = new Paint();
        stdevColor.setColor(Color.GREEN);
        stdevColor.setTextSize(textSize *2);

        int numLines = 10;
        int incrementWidth =  (width-margin)/numLines;
        int incrementHeight = (height-margin)/numLines;
        int newWidth = 0;
        int newHeight = 0;

        float[] yAxis = calYAxis();
        float previousY = -1;
        float previousMeanY = -1;
        float previousStdevY = -1;

        canvas.drawRect(margin, margin, width, height, p);
        drawAxis(canvas, height, width, margin);

        for (int i = 0; i <= numLines;i++) {
            float currentX = topx + newWidth;
            float currentY = topy+newHeight;

            p.setColor(Color.WHITE);
            canvas.drawLine(currentX, topy, currentX, height, p);
            canvas.drawLine(topx, currentY, width, currentY, p);
            if ((i % 2 == 0) && i < array.size()) {
                p.setColor(Color.BLACK);
                canvas.drawText(String.valueOf(arrayTime.get(i)), currentX, height + 25,  p);
                canvas.drawText(String.valueOf(yAxis[i]), topx - 50, currentY,  p);
            }

            if (i < array.size()){
                float calY = calculateY(array.get(i), height-margin) + margin;
                float calMeanY = calculateY(mean.get(i), height-margin) + margin;
                float calStdevY = calculateY (stdev.get(i), height-margin)+ margin;

                canvas.drawCircle(topx + newWidth, calY, 5, currentColor);
                canvas.drawCircle(topx + newWidth, calMeanY, 5, meanColor);
                canvas.drawCircle(topx + newWidth, calStdevY, 5, stdevColor);

                if (previousY < 0){
                    previousY =  calY;
                    previousMeanY = calMeanY;
                    previousStdevY = calStdevY;
                }
                else{
                    canvas.drawLine(currentX, calY, currentX-incrementWidth,previousY, currentColor);
                    canvas.drawLine(currentX, calMeanY, currentX-incrementWidth,previousMeanY, meanColor);
                    canvas.drawLine(currentX, calStdevY, currentX-incrementWidth, previousStdevY, stdevColor);

                    previousY = calY;
                    previousMeanY = calMeanY;
                    previousStdevY = calStdevY;
                }
            }
            newWidth +=incrementWidth;
            newHeight +=incrementHeight;
        }

        canvas.drawRect(0, height+125, getWidth(),  height+155, p);
        canvas.drawText("Value", (float) (width * .2) , height + 150, currentColor);
        canvas.drawText("Mean", (float) (width * .5) , height + 150, meanColor);
        canvas.drawText("Std Dev", (float) (width * .8) , height + 150, stdevColor);
    }

    private float calculateY (float point, int height){
        float range = calRange();
        return height-((point/range) * height);
    }

    private float calRange(){
        float min = 0;
        float max = Float.MIN_VALUE;

        for (float a : array){
            if (max < a){
                max = a;
            }
        }

        max = (int) (max + 1);
        return max-min;
    }

    private float[] calYAxis(){
        float range = calRange();
        float increment = range / 10;
        float total = 0;

        float[] yAxis = new float[11];
        for (int i = 0; i < yAxis.length; i++){
            yAxis[i] = range - total;
            total+=increment;
        }
        return yAxis;
    }

    private int roundToTen (int number){
        return (number / 10) * 10;
    }

    public void addPoint (float newPoint){
        if (array.size()>=11){
            array.remove(0);
            arrayTime.remove(0);
            mean.remove(0);
            stdev.remove(0);
        }

        if (array.size() <= 2){
            mean.add(newPoint);
            stdev.add(newPoint);
        }
        else{
            float meanTemp =(newPoint + array.get(array.size()-1) + array.get(array.size()-2)) / 3;
            mean.add(meanTemp);
            stdev.add((float) Math.sqrt((Math.pow(newPoint-meanTemp,2) +
                    Math.pow(array.get(array.size()-1)-meanTemp,2) +
                    Math.pow(array.get(array.size()-2)-meanTemp,2))/ 3));
        }

        array.add(newPoint);
        arrayTime.add(counterX);
        counterX++;
        invalidate();
    }

    public boolean run(){
        if (sensor.equals("Accelerator")) {
            if (mean.size() != 0) {
                if (mean.get(mean.size() - 1) > 10 || mean.get(mean.size() - 1) < 9.3) {
                    return true;
                }
            }
        }
        else if (sensor.equals("Light")){
            if (stdev.size()!=0){
                if (stdev.get(stdev.size() - 1) > 3){
                    return true;
                }
            }
        }
        return false;
    }

    public void setSensorName (String name) {
        sensor = name;
    }

    public void clearList(){
        for (int i = array.size()-1; i >=0 ;i--){
            array.remove(i);
            arrayTime.remove(i);
            mean.remove(i);
            stdev.remove(i);
        }
    }
}
