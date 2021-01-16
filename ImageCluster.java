package com.company;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCluster {
    //主要功能就是读取一副图像，再对图像进行分割
    //需要分类的簇数
    private int k;
    //迭代次数
    private int m;
    //数据集合
    private dataItem[][] source;
    //中心集合
    private dataItem[] center;
    //统计每个簇的各项数据的总和，用于计算新的点数
    private dataItem[] centerSum;

    //读取指定目录的图片数据，并且写入数组，这个数据要继续处理
    private int[][] getImageData(String path){
        BufferedImage bi=null;
        try{
            bi=ImageIO.read(new File(path));
        }catch (IOException e){
            e.printStackTrace();
        }
        int width=bi.getWidth();
        int height=bi.getHeight();
        int [][] data=new int[width][height];
        for(int i=0;i<width;i++)
            for(int j=0;j<height;j++)
                data[i][j]=bi.getRGB(i, j);
        return data;
    }
    //用来处理获取的像素数据，提取我们需要的写入dataItem数组
    private dataItem[][] InitData(int [][] data){
        dataItem[][] dataitems=new dataItem[data.length][data[0].length];
        for(int i=0;i<data.length;i++){
            for(int j=0;j<data[0].length;j++){
                dataItem di=new dataItem();
                Color c=new Color(data[i][j]);
                di.r=(double)c.getRed();
                di.g=(double)c.getGreen();
                di.b=(double)c.getBlue();
                di.group=1;
                dataitems[i][j]=di;
            }
        }
        return dataitems;
    }
    //生成随机的初始中心
    private void initCenters(int k){
        center =new dataItem[k];
        centerSum=new dataItem[k];//用来统计每个聚类里面的RGB分别之和，方便计算均值
        int width,height;
        for(int i=0;i<k;i++){
            //boolean flag=true;
            dataItem cent=new dataItem();
            dataItem cent2=new dataItem();

            width=(int)(Math.random()*source.length);
            height=(int)(Math.random()*source[0].length);
            cent.group=i;
            cent.r=(double)source[width][height].r;
            cent.g=(double)source[width][height].g;
            cent.b=(double)source[width][height].b;
            center[i]=cent;


            cent2.r=cent.r;
            cent2.g=cent.g;
            cent2.b=cent.b;
            cent2.group=0;
            centerSum[i]=cent2;

            width=0;height=0;
        }
        System.out.println("初始4个中心");
        for (int i = 0; i < center.length; i++)
        {
            System.out.println("("+center[i].r+","+center[i].g+","+center[i].b+")");
        }
    }
    //计算两个像素之间的欧式距离，用RGB作为三维坐标
    private double distance(dataItem first,dataItem second){
        double distance=0;
        distance=Math.sqrt(Math.pow((first.r-second.r),2)+Math.pow((first.g-second.g),2)+
                Math.pow((first.b-second.b),2));
        return distance;
    }
    //返回一个数组中最小的坐标
    private int minDistance(double[] distance){
        double minDistance=distance[0];
        int minLocation=0;
        for(int i=0;i<distance.length;i++){
            if(distance[i]<minDistance){
                minDistance=distance[i];
                minLocation=i;
            }else if(distance[i]==minDistance){
                if((Math.random()*10)<5){
                    minLocation=i;
                }
            }
        }
        return minLocation;
    }
    //每个点进行分类
    private void clusterSet(){
        int group=-1;
        double distance[]=new double[k];
        for(int i=0;i<source.length;i++){
            for(int j=0;j<source[0].length;j++){
                //求出距离中心点最短的中心
                for(int q=0;q<center.length;q++){
                    distance[q]=distance(center[q],source[i][j]);
                }
                group=minDistance(distance);//寻找该点最近的中心
                source[i][j].group=group;//把该点进行分类
                centerSum[group].r+=source[i][j].r;//分类完求出该类的RGB和
                centerSum[group].g+=source[i][j].g;
                centerSum[group].b+=source[i][j].b;
                centerSum[group].group+=1;//这个就是用来统计聚类里有几个点
                group=-1;
            }
        }
    }
    //设置新的中心
    public void setNewCenter(){
        for(int i=0;i<centerSum.length;i++){
            System.out.println(i+":"+centerSum[i].group+":"+centerSum[i].r+":"+centerSum[i].g+":"+centerSum[i].b);
            //取平均值为新的中心
            center[i].r=(int)(centerSum[i].r/centerSum[i].group);
            center[i].g=(int)(centerSum[i].g/centerSum[i].group);
            center[i].b=(int)(centerSum[i].b/centerSum[i].group);
            //重置之前的求和结果
            centerSum[i].r=center[i].r;
            centerSum[i].g=center[i].g;
            centerSum[i].b=center[i].b;
            centerSum[i].group=0;
        }
    }
    //输出聚类好的数据
    private void ImagedataOut(String path){
        Color c0=new Color(255,0,0);
        Color c1=new Color(0,255,0);
        Color c2=new Color(0,0,255);
        Color c3=new Color(128,128,128);
        Color c4 = new Color(255,255,0);
        BufferedImage nbi=new BufferedImage(source.length,source[0].length,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<source.length;i++){
            for(int j=0;j<source[0].length;j++){
                if(source[i][j].group==0)
                    nbi.setRGB(i, j, c0.getRGB());
                else if(source[i][j].group==1)
                    nbi.setRGB(i, j, c1.getRGB());
                else if(source[i][j].group==2)
                    nbi.setRGB(i, j, c2.getRGB());
                else if (source[i][j].group==3)
                    nbi.setRGB(i, j, c3.getRGB());
                else if(source[i][j].group == 4)
                    nbi.setRGB(i, j, c4.getRGB());
                //Color c=new Color((int)center[source[i][j].group].r,
                //		(int)center[source[i][j].group].g,(int)center[source[i][j].group].b);
                //nbi.setRGB(i, j, c.getRGB());
            }
        }
        try{
            ImageIO.write(nbi, "jpg", new File(path));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    //进行kmeans计算的核心函数
    public void kmeans(String path,int k,int m){
        source=InitData(getImageData(path));
		/*测试输出
		for(int i=0;i<source.length;i++)
			for(int j=0;j<source[0].length;j++)
				System.out.println("("+source[i][j].x+","+source[i][j].y+","+source[i][j].r+","+source[i][j].g+","+source[i][j].b+")");
		*/
        this.k=k;
        this.m=m;
        //初始化聚类中心
        initCenters(k);
		/*测试输出
		for (int i = 0; i < center.length; i++)
			System.out.println("("+center[i].x+","+center[i].y+","+center[i].r+","+center[i].g+","+center[i].b+")");
		*/
        //进行m次聚类
        for(int level=0;level<m;level++){
            clusterSet();
            setNewCenter();
            for (int i = 0; i < center.length; i++)
            {
                System.out.println("("+center[i].r+","+center[i].g+","+center[i].b+")");
            }
        }
        clusterSet();
        System.out.println("第"+m+"次迭代完成，聚类中心为：");
        for (int i = 0; i < center.length; i++)
        {
            System.out.println("("+center[i].r+","+center[i].g+","+center[i].b+")");
        }
        System.out.println("迭代总次数："+m);//进行图像输出，这个随意改
        ImagedataOut("C:\\Users\\CHENG\\Desktop\\test" + k + m + "two"+ ".jpg");
    }

    public static void main(String[] args){
        ImageCluster ic=new ImageCluster();
        ic.kmeans("C:\\Users\\CHENG\\Desktop\\test3.jfif", 5,5);
    }
}

class dataItem {
    public double r;
    public double g;
    public double b;
    public int group;
}
