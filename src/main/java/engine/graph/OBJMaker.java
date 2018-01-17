package engine.graph;
import org.joml.Vector3f;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Created by IceEye on 2017-11-23.
 */
public class OBJMaker {

    List<Float> dupePositions = new ArrayList<>();
    List<Float> edgePositions = new ArrayList<>();


    private Formatter x;

    public void createFile(String filename){
         try {
             x = new Formatter(filename);
         }
         catch (Exception e) {
             System.out.println("Issue");
         }
    }

    public void setVerticies(List<Float> positionList, List<Integer> indexList){
        for (int I = positionList.size()-1; I > 0; I = I- 3){
            x.format("%s%f%s%f%s%f", "v ", positionList.get(I)," ", positionList.get(I-1), " ", positionList.get(I-2));
            x.format("%n");
        }
    }

    public void setIndices(List<Integer> indexList){
        for (int I = indexList.size()-1; I > 0; I = I - 3){

            x.format("%s%03d%s%03d%s%03d", "f ", indexList.get(I)," ", indexList.get(I-1), " ", indexList.get(I-2));
            x.format("%n");
        }
    }

    public void closeFile(){
        x.close();
    }

}
