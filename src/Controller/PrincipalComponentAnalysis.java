package Controller;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.SingularValueDecomposition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PrincipalComponentAnalysis {
    MatrixOperation oMO = new MatrixOperation();
    
    public HashMap<String, double[]> doPrincipalComponentAnalysis(HashMap<String, double[]> data){
        System.out.println("---------------------- PCA ----------------------");
        System.out.println("\n");
        
        //konversi HashMap (keys) ke ArrayList
        List<String> dataPaths = data.keySet().stream().collect(Collectors.toList());
        //konversi HashMap (values) ke ArrayList
        List<double[]> dataValues = data.values().stream().collect(Collectors.toList());
        
        //konversi ArrayList ke Array
        double[][] dataValuesArray = new double[dataValues.size()][dataValues.get(0).length];
        for (int i = 0; i < dataValues.size(); i++) {
            for (int j = 0; j < dataValues.get(i).length; j++) {
                dataValuesArray[i][j] = dataValues.get(i)[j];
            }
        }
        
        //menghitung rata-rata
        double[] rComputeMean = this.computeMean(dataValuesArray);
        
        //normalisasi data
        DoubleMatrix2D rNormalize = this.normalize(new DenseDoubleMatrix2D(dataValuesArray), rComputeMean);
        
        //menghitung kovarian
        DoubleMatrix2D rComputeCovariance = this.computeCovariance(rNormalize.viewDice(), rNormalize);
        
        //cari U dari kovarian
        SingularValueDecomposition oSVD = new SingularValueDecomposition(rComputeCovariance);
        
        double[][] multiplyDoubleMatrix2D = oMO.multiplyDoubleMatrix2D(rNormalize, oSVD.getU());
        
        System.out.println("data setelah reduksi");
        for (int i = 0; i < multiplyDoubleMatrix2D.length; i++) {
            System.out.format("data-%d = %s\n", i+1, Arrays.toString(multiplyDoubleMatrix2D[i]));
        }
        
        //konversi Array ke HashMap
        HashMap<String, double[]> resultDimentionalityReduction = new HashMap();
        for (int i = 0; i < dataPaths.size(); i++) {
            resultDimentionalityReduction.put(dataPaths.get(i), multiplyDoubleMatrix2D[i]);
        }
        
        return resultDimentionalityReduction;
    }
    
    public double[] computeMean(double[][] data){
        double[] mean = new double[data[0].length];
        for(int j=0; j<data[0].length; j++){
            double sum = 0;
            for (double[] data1 : data) {
                sum += data1[j];
            }
            double m = sum/data.length;
            mean[j] = m;
        }
        return mean;
    }
    
    public DoubleMatrix2D normalize(DoubleMatrix2D data, double[] mean){
        DoubleMatrix2D normalizedData = new DenseDoubleMatrix2D(data.rows(), data.columns());
        for(int i=0; i<data.columns(); i++){
            for(int j=0; j<data.rows(); j++){
                normalizedData.set(j, i, data.get(j, i)-mean[i]);
            }
        }
        return normalizedData;
    }
    
    public DoubleMatrix2D computeCovariance(DoubleMatrix2D data1, DoubleMatrix2D data2){
        DoubleMatrix2D tempA = new DenseDoubleMatrix2D(oMO.multiplyDoubleMatrix2D(data1, data2));
        DoubleMatrix2D A = new DenseDoubleMatrix2D(tempA.rows(), tempA.columns());
        for(int i=0; i<A.rows(); i++){
            for(int j=0; j<A.columns(); j++){
                A.set(i, j, ((float)tempA.get(i, j)/(data1.columns()-1)));
            }
        }
        return A;
    }
}
