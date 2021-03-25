package Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FuzzyAssociationRuleOld {
    public HashMap<String, double[]> doFuzzyAssociationRule(HashMap<String, double[]> resultDataConvertion, double alpha, double theta){
        //convert data type HashMap (keys) to ArrayList
        List<String> dataKeys = resultDataConvertion.keySet().stream().collect(Collectors.toList());
        //convert data type HashMap (values) to ArrayList
        List<double[]> dataValues = resultDataConvertion.values().stream().collect(Collectors.toList());
        
        int numberOfDocument = dataValues.size();
        int numberOfTerm = dataValues.get(0).length;
        
        //debugging raw data
        System.out.println("raw data");
        for (int i = 0; i < numberOfDocument; i++) {
            System.out.format("%s = %s\n", dataKeys.get(i), Arrays.toString(dataValues.get(i)));
        }
        System.out.println("\n");
        
        double minValue = this.getMinValue(dataValues);
        double midValue = this.getMidValue(dataValues);
        double maxValue = this.getMaxValue(dataValues);
        
        //debugging min, mid, max
        System.out.format("min data = %f\n", minValue);
        System.out.format("mid data = %f\n", midValue);
        System.out.format("max data = %f\n", maxValue);
        System.out.println("\n");
        
        //1. create L0
        double[][][] L0 = this.createL0(dataValues, minValue, midValue, maxValue);
        
        //debugging result L0
        System.out.println("L0");
        for (int i = 0; i < numberOfDocument; i++) {
            System.out.format("doc ke-%d\n", i+1);
            for (int j = 0; j < numberOfTerm; j++) {
                System.out.format("term-%d -> low : %f\n", j+1, L0[i][j][0]); //low
                System.out.format("term-%d -> mid : %f\n", j+1, L0[i][j][1]); //mid
                System.out.format("term-%d -> high : %f\n", j+1, L0[i][j][2]); //high
            }
            System.out.println("\n");
        }
        
        //2. sum L0
        MatrixOperation oMO = new MatrixOperation();
        int newNumberOfMemberFunction = L0[0][0].length;
        
        //2.1 transpose dimention 1 & 2 L0
        double[][][] resultTransposeDim1and2 = new double[numberOfTerm][numberOfDocument][newNumberOfMemberFunction];
        for (int i = 0; i < numberOfTerm; i++) {
            for (int j = 0; j < numberOfDocument; j++) {
                for (int k = 0; k < newNumberOfMemberFunction; k++) {
                    resultTransposeDim1and2[i][j][k] = L0[j][i][k];
                }
            }
        }
        
        //2.2 transpose dimention 2 & 3 L0
        double[][] sumL0 = new double[numberOfTerm][newNumberOfMemberFunction];
        for (int i = 0; i < numberOfTerm; i++) {
            double[][] resultTransposeDim2and3 = oMO.MatrixTranspose2DDouble(resultTransposeDim1and2[i]);
            
            //sum
            for (int j = 0; j < resultTransposeDim2and3.length; j++) {
                double tempSum = 0;
                for (int k = 0; k < resultTransposeDim2and3[j].length; k++) {
                    tempSum += resultTransposeDim2and3[j][k];
                }
                sumL0[i][j] = tempSum;
            }
        }
        
        //debugging sum L0
        System.out.println("sum L0");
        for (int i = 0; i < numberOfTerm; i++) {
            System.out.format("term-%d -> %s\n", i+1, Arrays.toString(sumL0[i]));
        }
        System.out.println("\n");
        
        //3. get max value L0
        int[] bestIndeL0 = new int[numberOfTerm];
        double[] bestValueL0 = new double[numberOfTerm];
        for (int i = 0; i < numberOfTerm; i++) {
            double max = 0;
            int index = 0;
            for (int j = 0; j < 3; j++) {
                if (sumL0[i][j] > max) {
                    max = sumL0[i][j];
                    index = j;
                }
            }
            
            //save max for index L0
            bestIndeL0[i] = index;
            
            //save max for value L0
            double temp = max/numberOfDocument;
            if (Double.isNaN(temp) || Double.isInfinite(temp)) {
                bestValueL0[i] = 0;
            } else{
                bestValueL0[i] = temp;
            }
        }
        
        //debugging get max value L0
        System.out.println("best value L0");
        for (int i = 0; i < numberOfTerm; i++) {
            System.out.format("best value term-%d -> %f \n", i+1, bestValueL0[i]);
        }
        System.out.println("\n");
        
        //4. create C1
        ArrayList<Integer> C1Index = new ArrayList();
        ArrayList<Double> C1Value = new ArrayList();
        
        for (int i = 0; i < numberOfTerm; i++) {
            if (bestValueL0[i] >= theta) {
                C1Index.add(i);
                C1Value.add(bestValueL0[i]);
            }
        }
        
        //debugging C1
        System.out.println("C1");
        for (int i = 0; i < C1Index.size(); i++) {
            System.out.format("term-%d -> %f\n", C1Index.get(i)+1, C1Value.get(i));
        }
        System.out.println("\n");
        
        //5. create L1        
        ArrayList<ArrayList<String>> LqTerm = new ArrayList();
        ArrayList<ArrayList<ArrayList<Double>>> LqValue = new ArrayList();
        
        int numberOfC1 = C1Value.size();
        for (int i = 0; i < numberOfC1; i++) {
            ArrayList<String> tempIndex2 = new ArrayList();
            ArrayList<ArrayList<Double>> tempValue2 = new ArrayList();
            for (int j = 0; j < numberOfC1-(i+1); j++) {
                ArrayList<Double> tempValue1 = new ArrayList();
                for (int k = 0; k < numberOfDocument; k++) {
                    //save min for value
                    double value = Math.min(L0[k][C1Index.get(i)][bestIndeL0[C1Index.get(i)]], L0[k][C1Index.get(i+j+1)][bestIndeL0[C1Index.get(i+j+1)]]);
                    tempValue1.add(value);
                }
                //save min for index
                String key = String.valueOf(C1Index.get(i))+","+String.valueOf(C1Index.get(i+j+1));
                
                tempIndex2.add(key);
                tempValue2.add(tempValue1);
            }
            LqTerm.add(tempIndex2);
            LqValue.add(tempValue2);
        }
        
        //debugging L1
        System.out.println("L1");
        for (int i = 0; i < LqValue.size(); i++) {
            for (int j = 0; j < LqValue.get(i).size(); j++) {
                for (int k = 0; k < LqValue.get(i).get(j).size(); k++) {
                    System.out.format("doc-%s: term-%s = %f\n", k+1, LqTerm.get(i).get(j), LqValue.get(i).get(j).get(k));
                }
                System.out.print("\n");
            }
        }
        System.out.println("\n");
        
        //6. sum L1
        ArrayList<ArrayList<Double>> sumL1 = new ArrayList();
        for (int i = 0; i < LqValue.size(); i++) {
            ArrayList<Double> temp = new ArrayList();
            for (int j = 0; j < LqValue.get(i).size(); j++) {
                double total = 0;
                for (int k = 0; k < LqValue.get(i).get(j).size(); k++) {
                    total += LqValue.get(i).get(j).get(k);
                }
                temp.add(total);
            }
            sumL1.add((ArrayList<Double>) temp.clone());
            temp.clear();
        }
        
        //debugging sum L1
        System.out.println("sum L1");
        for (int i = 0; i < sumL1.size(); i++) {
            for (int j = 0; j < sumL1.get(i).size(); j++) {
                System.out.format("term %s = %f\n", LqTerm.get(i).get(j), sumL1.get(i).get(j));
            }
        }
        
        //7. create C2
        ArrayList<String> CqTerm = new ArrayList();
        ArrayList<String> CqIndex = new ArrayList();
        ArrayList<Double> CqValue = new ArrayList();
        
        for (int i = 0; i < sumL1.size(); i++) {
            for (int j = 0; j < sumL1.get(i).size(); j++) {
                if (sumL1.get(i).get(j) >= theta) {
                    double value = sumL1.get(i).get(j)/numberOfDocument;
                    CqValue.add(value);
                    CqTerm.add(LqTerm.get(i).get(j));
                    String index = String.valueOf(i)+","+String.valueOf(j);
                    CqIndex.add(index);
                }
            } 
        }
        
        //debugging C2
        System.out.println("C2");
        for (int i = 0; i < CqValue.size(); i++) {
            System.out.format("C2 -> term-> %s index-> %s = %f\n", CqTerm.get(i), CqIndex.get(i), CqValue.get(i));
        }
        System.out.println("\n");
        

        //crete var for temp value Cq & Lq old
        ArrayList<ArrayList<String>> LqTermOld = new ArrayList();
        ArrayList<ArrayList<ArrayList<Double>>> LqValueOld = new ArrayList();
        ArrayList<String> CqTermOld = new ArrayList();
        ArrayList<Double> CqValueOld = new ArrayList();
        ArrayList<String> CqIndexOld = new ArrayList();
        
        //8. cek apakah Cq = NULL?
        while(!CqIndex.isEmpty())
        {
            //clone Lq index, Lq value, Cq index, Cq value, Cq term
            LqTermOld = (ArrayList<ArrayList<String>>) LqTerm.clone();
            LqValueOld = (ArrayList<ArrayList<ArrayList<Double>>>) LqValue.clone();
            CqTermOld = (ArrayList<String>) CqTerm.clone();
            CqValueOld = (ArrayList<Double>) CqValue.clone();
            CqIndexOld = (ArrayList<String>) CqIndex.clone();
        
            //clear Lq index, Lq value, Cq index, Cq value, Cq term
            LqTerm.clear();
            LqValue.clear();
            CqTerm.clear();
            CqValue.clear();
            CqIndex.clear();
            
            //9. create Lq
            int numberOfCq = CqTermOld.size();
            for (int i = 0; i < numberOfCq; i++) {
                ArrayList<String> tempIndex2 = new ArrayList();
                ArrayList<ArrayList<Double>> tempValue2 = new ArrayList();
                for (int j = 0; j < numberOfCq-(i+1); j++) {
                    ArrayList<Double> tempValue1 = new ArrayList();
                    
                    String[] indexLeft = CqIndexOld.get(i).split(",");
                    String[] indexRight = CqIndexOld.get(i+j+1).split(",");
                    
                    System.out.println("======================");
                    System.out.println(Arrays.toString(indexLeft));
                    System.out.println(Arrays.toString(indexRight));
                    System.out.println("++++++++++++++++++++++");
                    for (int k = 0; k < numberOfDocument; k++) {
                        //save best value
                        double value = Math.min(LqValueOld.get(Integer.parseInt(indexLeft[0])).get(Integer.parseInt(indexLeft[1])).get(k), LqValueOld.get(Integer.parseInt(indexRight[0])).get(Integer.parseInt(indexRight[1])).get(k));
                        System.out.format("doc-%d = %f\n", k+1, value);
                        tempValue1.add(value);
                    }
                    String key = String.valueOf(CqTermOld.get(i))+","+String.valueOf(CqTermOld.get(i+j+1));
                    System.out.format("term -> %s\n", key);
                    System.out.println("======================");
                    
                    tempIndex2.add(key);
                    tempValue2.add(tempValue1);
                }
                LqTerm.add(tempIndex2);
                LqValue.add(tempValue2);
            }
            
            //10. sum Lq
            ArrayList<ArrayList<Double>> sumLq = new ArrayList();
            for (int i = 0; i < LqValue.size(); i++) {
                ArrayList<Double> temp = new ArrayList();
                for (int j = 0; j < LqValue.get(i).size(); j++) {
                    double total = 0;
                    for (int k = 0; k < LqValue.get(i).get(j).size(); k++) {
                        total += LqValue.get(i).get(j).get(k);
                    }
                    temp.add(total);
                }
                sumLq.add((ArrayList<Double>) temp.clone());
                temp.clear();
            }
            
            //debugging sum Lq
            System.out.println("sum Lq");
            for (int i = 0; i < sumLq.size(); i++) {
                for (int j = 0; j < sumLq.get(i).size(); j++) {
                    System.out.format("new term %s = %f\n", LqTerm.get(i).get(j), sumLq.get(i).get(j));
                }
            }
            
            //11. create new Cq
            for (int i = 0; i < sumLq.size(); i++) {
                for (int j = 0; j < sumLq.get(i).size(); j++) {
                    if (sumLq.get(i).get(j) >= theta) {
                        double value = sumLq.get(i).get(j)/numberOfDocument;
                        CqValue.add(value);
                        CqTerm.add(LqTerm.get(i).get(j));
                        String index = String.valueOf(i)+","+String.valueOf(j);
                        CqIndex.add(index);
                    }
                } 
            }
            
            //debugging new Cq
            System.out.println("Cq");
            for (int i = 0; i < CqValue.size(); i++) {
                System.out.format("new Cq -> term-> %s index-> %s = %f\n", CqTerm.get(i), CqIndex.get(i), CqValue.get(i));
            }
            System.out.println("\n");
        }
        return null;
    }
    
    private double getMinValue(List<double[]> data){
         double minimum = 99999;
        for (double[] Data1 : data) {
            for (int j = 0; j < data.get(0).length; j++) {
                if (minimum > Data1[j] && Data1[j] != 0) {
                    minimum = Data1[j];
                }
            }
        }
        return minimum;
    }
    
    private double getMidValue(List<double[]> data){
        HashSet<Double> tempSet = new HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).length; j++) {
                tempSet.add(data.get(i)[j]);
            }
        }
        ArrayList<Double> tempArraylist = new ArrayList(tempSet);
        Collections.sort(tempArraylist); //sorting data
        int midIndex = (int) Math.ceil(tempArraylist.size()/2);
        return tempArraylist.get(midIndex);
    }
    
    private double getMaxValue(List<double[]> data){
        double maksimum = 0;
        for (double[] Data1 : data) {
            for (int j = 0; j < data.get(0).length; j++) {
                if (maksimum < Data1[j]) {
                    maksimum = Data1[j];
                }
            }
        }
        return maksimum;
    }
    
    private double calcMemberFunctionLow(double a1, double a2, double term){
        double lowValue = 0;
        if (term == 0) {
            lowValue = 0;
        }
        else if (term > 0 && term < a1) {
            lowValue = (1+term)/(a1);
        }
        else if (term == a1) {
            lowValue = 2;
        }
        else if (term > a1 && term < a2) {
            lowValue = (1+a2-term)/(a2-1);
        }
        else if (term >= a2) {
            lowValue = 1;
        }
        
        return lowValue;
    }
    
    private double calcMemberFunctionMid(double a1, double a2, double a3, double term){
        double midValue = 0;
        if (term == 0) {
            midValue = 0;
        }
        else if (term == a1) {
            midValue = 1;
        }
        else if (term > a1 && term < a2) {
            midValue = (1+term-a1)/(a2-a1);
        }
        else if (term == a2) {
            midValue = 2;
        }
        else if (term > a2 && term < a3) {
            midValue = (1+a3-term)/(a3-a2);
        }
        else if (term == a3) {
            midValue = 1;
        }
        return midValue;
    }
    
    private double calcMemberFunctionHigh(double a2, double a3, double term){
        double highValue = 0;
        if (term == 0) {
            highValue = 0;
        }
        else if (term > 0 && term <= a2) {
            highValue = 1;
        }
        else if (term > a2 && term < a3) {
            highValue = (1+term)/(a3-a2);
        }
        else if (term == a3) {
            highValue = 2;
        }
        return highValue;
    }
    
    private double[][][] createL0(List<double[]> dataValues, double minValue, double midValue, double maxValue){
        int numberOfDocument = dataValues.size();
        int numberOfTerm = dataValues.get(0).length;
        
        double[][][] L1 = new double[numberOfDocument][numberOfTerm][3];
        for (int i = 0; i < numberOfDocument; i++) {
            for (int j = 0; j < numberOfTerm; j++) {
                L1[i][j][0] = this.calcMemberFunctionLow(minValue, midValue, dataValues.get(i)[j]);
                L1[i][j][1] = this.calcMemberFunctionMid(minValue, midValue, maxValue, dataValues.get(i)[j]);
                L1[i][j][2] = this.calcMemberFunctionHigh(midValue, maxValue, dataValues.get(i)[j]);
            }
        }
        return L1;
    }
}
