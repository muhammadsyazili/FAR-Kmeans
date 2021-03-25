package Controller;

import java.sql.SQLException;
import java.util.HashMap;

public class run {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        HashMap<String, double[]> dataMap = new HashMap();
        double[][] data = {
            {0.0,0.0,1.6,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.6,1.6,0.0,0.0,0.0,1.6,0.0,0.0,0.0,1.6,1.6,0.6,0.0,1.6},
            {0.0,0.0,0.0,0.0,1.6,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.6,0.0,0.0,0.0,0.0,0.0,1.6,0.0},
            {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.6,0.0,0.0,0.0,0.0,0.0,1.6,0.0,0.0,0.0,0.0,0.0,1.6,0.0,0.0,0.6,0.0,0.0},
            {0.0,0.0,0.0,1.6,0.0,1.6,1.3,1.6,0.0,0.6,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0},
            {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.6,1.6,0.0,0.0,0.0,0.0,1.6,0.0,0.0,0.6,0.0,0.0,0.0,0.0,0.0,0.0,0.0}
        };
        
//        double[][] data = {
//            {1.6,1.6,0.6,0.0,1.6},
//            {0.0,0.0,0.0,1.6,0.0},
//            {0.0,0.0,0.6,0.0,0.0},
//            {0.0,0.0,0.0,0.0,0.0},
//            {0.0,0.0,0.0,0.0,0.0}
//        };
        for (int i = 0; i < data.length; i++) {
            dataMap.put("doc"+i, data[i]);
        }
        
        FuzzyAssociationRule oFAR = new FuzzyAssociationRule();
        oFAR.doFuzzyAssociationRule(dataMap, 0.6, 0.4);
    }
    
}
