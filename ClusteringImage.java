package com.company;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ClusteringImage {
    //聚类的簇数
    private int k;
    //聚类迭代的次数
    private int m;
    //数据集合
    private dataPoint[][] source;
    //中心点的集合
    private dataPoint[] center;
    //
    private int[][] centerIndex;
    //统计每个簇的各个维度上的总和，用来计算平均值来计算新的中心点
    private dataPoint[] centerSum;
    //
    private int[][] data;

    //读取指定路径下的图片数据，并且写入数组，这个数据
    private int[][] getImageData(String path){
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[][] data = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                data[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        this.data = data;
        return data;
    }

    //把读入的像素数据,提取Red、Green、Blue三种颜色的double类型的数据，写入一个dataPoint类型的二维数组中
    private dataPoint[][] InitData(int [][] data){
        dataPoint[][] dataPoints = new dataPoint[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                dataPoint p = new dataPoint();
                Color c = new Color(data[i][j]);
                p.r = c.getRed();
                p.g = c.getGreen();
                p.b = c.getBlue();
                dataPoints[i][j] = p;
            }
        }
        return dataPoints;
    }


    //生成随机的初始中心点,并且初始化center数组和centerSum数组
    private void initCenters(int k){
        center = new dataPoint[k];
        centerSum = new dataPoint[k];
        for (int i = 0; i < k; i++) {
            dataPoint cent = new dataPoint();
            dataPoint centCopy = new dataPoint();
            centerIndex = new int[k][2];

            //随机选中横坐标位w，纵坐标为h的
            int w = (int)(Math.random() * source.length);
            int h = (int)(Math.random() * source[0].length);
            centerIndex[i][0] = w;
            centerIndex[i][1] = h;

            cent.groupId = i;
            cent.r = (double)source[w][h].r;
            cent.g = (double)source[w][h].g;
            cent.b = (double)source[w][h].b;
            center[i] = cent;

            centCopy.r = cent.r;
            centCopy.g = cent.g;
            centCopy.b = cent.b;
            centerSum[i] = centCopy;
        }
    }

    //对每个点进行聚类
    private void clusterSet(){
        int group = -1;
        double[] distance = new double[k];
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[0].length; j++) {
                //求出最近的中心点的中心，并确定这个点的group
                for(int q = 0;q < center.length;q++){
                    distance[q] = distance(center[q], source[i][j]);
                }
                group = indexOfMinDistance(distance);
                source[i][j].groupId = group;
                centerSum[group].r += source[i][j].r;
                centerSum[group].g += source[i][j].g;
                centerSum[group].b += source[i][j].b;
                centerSum[group].groupId++;//在centerSum中groupId对应这个簇中对应的点数
                group = -1;
            }
        }
    }

    private void setNewCenter(){
        for (int i = 0; i < centerSum.length; i++) {
            center[i].r = (int)(centerSum[i].r / centerSum[i].groupId);
            center[i].g = (int)(centerSum[i].g / centerSum[i].groupId);
            center[i].b = (int)(centerSum[i].b / centerSum[i].groupId);

            centerSum[i].r = center[i].r;
            centerSum[i].g = center[i].g;
            centerSum[i].b = center[i].b;
            centerSum[i].groupId = 0;
        }
    }

    private void produceImage(String path){
//        Color[] colors = new Color[k];
//        for (int i = 0; i < center.length; i++) {
//            colors[i] = new Color((int)center[i].r, (int)center[i].g, (int)center[i].b);
//        }
        BufferedImage bufferedImage = new BufferedImage(source.length, source[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[0].length; j++) {
//                bufferedImage.setRGB(i, j, colors[source[i][j].groupId].getRGB());
                int ce = source[i][j].groupId;
                int w = centerIndex[ce][0];
                int h = centerIndex[ce][1];
                bufferedImage.setRGB(i, j, data[w][h]);
            }
        }
        try {
            ImageIO.write(bufferedImage, "jpg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kmeans(String path, int k, int m){
        this.k = k;
        this.m = m;
        source=InitData(getImageData(path));
        initCenters(k);
        for(int level=0;level<m;level++){
            clusterSet();
            setNewCenter();
//            produceImage("C:\\Users\\CHENG\\Desktop\\test" + k + level + ".jpg");
        }
        clusterSet();
        produceImage("C:\\Users\\CHENG\\Desktop\\test" + k + m + ".jpg");
    }
    //计算两个像素点直接在三维度上的欧几里得距离，用RGB的值进行计算
    private double distance(dataPoint first, dataPoint second) {
        return Math.sqrt(Math.pow((first.r - second.r), 2) + Math.pow((first.g - second.g), 2)
                + Math.pow((first.b - second.b), 2));
    }
    //返回一个距离数组中距离值最小的下标
    private int indexOfMinDistance(double[] distance){
        double minDistance = distance[0];
        int minDisIndex = 0;
        for (int i = 0; i < distance.length; i++) {
            if(distance[i] < minDistance){
                minDistance = distance[i];
                minDisIndex = i;
            }else if(distance[i] == minDistance){
                //即如果有
                if((Math.random()*10)<5){
                    minDisIndex=i;
                }
            }
        }
        return minDisIndex;
    }

    public static void main(String[] args) {
        ClusteringImage clusteringImage = new ClusteringImage();
        clusteringImage.kmeans("C:\\Users\\CHENG\\Desktop\\test3.jfif", 5,5);
    }
}

//每一个实例对应图像上的一个像素点
class dataPoint{
    //红色
    public double r;
    //绿色
    public double g;
    //蓝色
    public double b;
    //被聚类到哪个组的组号
    public int groupId;
}