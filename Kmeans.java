package com.company;
import java.util.ArrayList;
import java.util.Random;

public class Kmeans {
    //聚类的簇数
    private int k;
    //聚类迭代的次数
    private int m;
    //数据集合
    private ArrayList<DataPoint> datas;
    //中心点的集合
    private DataPoint[] center;
    //统计每个簇的各个维度上的总和，用来计算平均值来计算新的中心点
    private DataPoint[] centerSum;

    public Kmeans(int k, int m) {
        this.k = k;
        this.m = m;
    }

    private void initCenters(int k){
        center = new DataPoint[k];
        centerSum = new DataPoint[k];
        for (int i = 0; i < k; i++) {
            DataPoint cent = new DataPoint();
            DataPoint centCopy = new DataPoint();

            //随机选中横坐标位w，纵坐标为h的
            int ra = (int)(Math.random() * datas.size());

            cent.clusterId = i;
            cent.x = (double)datas.get(ra).x;
            cent.y = (double)datas.get(ra).y;
            cent.z = (double)datas.get(ra).z;
            cent.a = (double)datas.get(ra).a;
            cent.b = (double)datas.get(ra).b;
            cent.c = (double)datas.get(ra).c;

            center[i] = cent;

            centCopy.x = cent.x;
            centCopy.y = cent.y;
            centCopy.z = cent.z;
            centCopy.a = cent.a;
            centCopy.b = cent.b;
            centCopy.c = cent.c;
            centerSum[i] = centCopy;
        }
        System.out.println("========初始"+ k +"个中心========");
        for (int i = 0; i < center.length; i++)
        {
            System.out.println("中心点"+ (i + 1) +"坐标为:" + center[i].toString());
        }
    }

    private double distance(DataPoint first,DataPoint second){
        double distance=0;
        distance=Math.sqrt(Math.pow((first.x-second.x),2)+Math.pow((first.y-second.y),2)+
                Math.pow((first.z-second.z),2) + Math.pow((first.a-second.a),2) +
                Math.pow((first.b-second.b),2) + Math.pow((first.c-second.c),2));
        return distance;
    }

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

    private void clusterSet(){
        int clusterId = -1;
        double distance[]=new double[k];
        for(DataPoint d : datas){
            for(int q = 0;q < center.length;q ++){
                distance[q] = distance(center[q], d);
            }
            clusterId = minDistance(distance);
            d.clusterId = clusterId;
            centerSum[clusterId].x += d.x;
            centerSum[clusterId].y += d.y;
            centerSum[clusterId].z += d.z;
            centerSum[clusterId].a += d.a;
            centerSum[clusterId].b += d.b;
            centerSum[clusterId].c += d.c;
            centerSum[clusterId].clusterId++;
            clusterId = -1;
        }
    }

    private void setNewCenter(){
        for(int i=0;i<centerSum.length;i++){
            System.out.println(i + " 号聚类目前有" + centerSum[i].clusterId + "个点");
            //取平均值为新的中心x
            center[i].x=(int)(centerSum[i].x/centerSum[i].clusterId);
            center[i].y=(int)(centerSum[i].y/centerSum[i].clusterId);
            center[i].z=(int)(centerSum[i].z/centerSum[i].clusterId);
            center[i].a=(int)(centerSum[i].a/centerSum[i].clusterId);
            center[i].b=(int)(centerSum[i].b/centerSum[i].clusterId);
            center[i].c=(int)(centerSum[i].c/centerSum[i].clusterId);
            //重置之前的求和结果
            centerSum[i].x = center[i].x;
            centerSum[i].y = center[i].y;
            centerSum[i].z = center[i].z;
            centerSum[i].a = center[i].a;
            centerSum[i].b = center[i].b;
            centerSum[i].c = center[i].c;
            centerSum[i].clusterId = 0;
        }
    }

    private double[] computeAvgC(){
        double[] avgOfC = new double[k];

        for(int i = 0;i < k;i++){
            int pointsNumOfthisCul = centerSum[i].clusterId;
            double sum = 0.0;
            int size = datas.size();
            for(int j = 0;j < size;j++){
                if(datas.get(j).clusterId == i){
                    for(int k = j + 1;k < size;k ++){
                        if(datas.get(k).clusterId != i)
                            continue;
                        else{
                            sum += distance(datas.get(j), datas.get(k));
                        }
                    }
                }
            }
            if(pointsNumOfthisCul <= 1)
                avgOfC[i] = 0;
            else
                avgOfC[i] = 2 * sum/(pointsNumOfthisCul * (pointsNumOfthisCul - 1));
        }
        return avgOfC;
    }

    private double computeDBI(){
        double[] avgOfC = computeAvgC();
        double DBI = 0.0;
        for(int i = 0;i < k;i++){
            double maxOfCurI = 0.0;
            for(int j = 0;j != i && j < k;j++){
                double disOfIJ = distance(center[i], center[j]);
                maxOfCurI = Math.max(maxOfCurI, (avgOfC[i] + avgOfC[j]) / disOfIJ);
            }
            DBI += maxOfCurI;
        }
        return DBI / k;
    }

    private void kmProcess(){
        initData();
        initCenters(k);
        for (int level = 0; level < m; level++) {
            clusterSet();
            System.out.println("DBI: " + computeDBI());
            setNewCenter();
            System.out.println("新的" + k + "中心点坐标是：");
            for(DataPoint p : center){
                System.out.println(p.toString());
            }

        }
        clusterSet();
        System.out.println("DBI: " + computeDBI());
        for (int i = 0; i < centerSum.length; i++) {
            System.out.println(i + "号聚类共有" + centerSum[i].clusterId + "个数据点");
        }
    }



    private void initData(){
        datas = new ArrayList<DataPoint>();
        datas.add(new DataPoint(-3.555092661,8.557989485,-0.844095408,2.485912643,2.955549309,2.911345909));
        datas.add(new DataPoint(-0.537228509,9.296086244,0.568777791,1.601950104,1.932231275,2.277591972));
        datas.add(new DataPoint(1.37071479,8.913828759,1.121977017,1.595010121,2.421250877,2.943803601));
        datas.add(new DataPoint(-3.702193295,8.475791391,0.609276967,2.377791396,3.030810584,2.940278907));
        datas.add(new DataPoint(-1.645337093,3.266686712,5.994481408,6.152862588,4.013475177,3.597190916));
        datas.add(new DataPoint(0.873680422,9.432396246,0.637303196,1.70386614,2.162598974,1.891739713));
        datas.add(new DataPoint(3.02505325,8.572284808,0.328174116,2.457607479,2.773923368,3.017599559));
        datas.add(new DataPoint(-3.102340719,8.690122259,0.593621078,2.305386065,2.19540295,2.386814203));
        datas.add(new DataPoint(-0.284172431,9.283838768,-0.647609487,1.266899988,1.901006361,2.783320176));
        datas.add(new DataPoint(-3.284639716,8.731872294,-0.191077555,2.019474296,2.193120524,2.631808786));
        datas.add(new DataPoint(-3.457044616,8.58995667,0.424913216,2.473346604,2.203994511,2.581500987));
        datas.add(new DataPoint(-2.203634395,7.671018733,0.083504966,4.415256288,2.828010613,2.822531045));
        datas.add(new DataPoint(-0.325081352,9.072540943,1.497468323,1.610382566,2.958314088,2.475812198));
        datas.add(new DataPoint(-1.483358628,8.764040949,3.323292351,1.102075631,1.520136883,1.647079348));
        datas.add(new DataPoint(-1.141034013,9.238944544,0.764353521,1.804508421,2.860425702,2.498253549));
        datas.add(new DataPoint(-1.355792069,8.742230949,1.34795718,2.917660307,2.811596431,3.610532482));
        datas.add(new DataPoint(-2.545296303,8.279692942,0.355632159,3.196948672,3.509449205,3.48443141));
        datas.add(new DataPoint(-2.392798879,8.558407354,0.711495022,3.205438156,2.93573251,3.847781334));
        datas.add(new DataPoint(-0.68796965,8.629187157,8.629187157,1.48446446,2.78060205,2.78060205));
        datas.add(new DataPoint(-0.237532608,9.672159357,-0.937886285,1.487324036,2.60403594,1.910626685));
        datas.add(new DataPoint(-0.246054862,9.317894062,0.933528727,0.999903304,1.690629693,2.023778955));
        datas.add(new DataPoint(-2.657877565,8.954836349,-0.337884773,1.874631708,2.084419429,2.510735576));
    }


    public static void main(String[] args) {
        int k = 3;
        int m = 5;
        Kmeans kmeans = new Kmeans(k,m);
        kmeans.kmProcess();
    }

}
class DataPoint{
    double x;
    double y;
    double z;
    double a;
    double b;
    double c;
    int clusterId;
    public DataPoint(){

    }

    public DataPoint(double x, double y, double z, double a, double b, double c) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", clusterId=" + clusterId +
                '}';
    }
}