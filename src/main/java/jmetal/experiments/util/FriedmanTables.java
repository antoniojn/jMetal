//  FriedmanTables.java
//
//  Authors:
//       Jorge Rodriguez
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//

package jmetal.experiments.util ;

import jmetal.experiments.Experiment;
import jmetal.util.Configuration;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 20/02/14.
 */
public class FriedmanTables implements IExperimentOutput {
  Experiment experiment_ ;

  public FriedmanTables(Experiment experiment) {
    experiment_ = experiment ;
  }

  @Override
  public void generate() {
    for (String indicator : experiment_.getIndicatorList()) {
      executeTest(indicator);
    }
  }
  
  private void executeTest(String indic){
    Vector algorithms;
    Vector datasets;
    Vector data;
    String string = "";
    StringTokenizer lineas;
    String linea;
    int i, j, k;
    int position;
    double mean[][];
    Pair order[][];
    Pair rank[][];
    boolean found;
    int ig;
    double sum;
    boolean visto[];
    Vector porVisitar;
    double Rj[];
    double friedman;
    double sumatoria=0;
    double termino1, termino2;

    String indicator_ = indic;

    /*Read the result file*/

    String outDir = experiment_.getExperimentBaseDirectory() + "/latex";
    String outFile = outDir +"/FriedmanTest"+indicator_+".tex";

    String Output = "";
    Output = Output + ("\\documentclass{article}\n" +
            "\\usepackage{graphicx}\n" +
            "\\title{Results}\n" +
            "\\author{}\n" +
            "\\date{\\today}\n" +
            "\\begin{document}\n" +
            "\\oddsidemargin 0in \\topmargin 0in" +
            "\\maketitle\n" +
            "\\section{Tables of Friedman Tests}");

    algorithms = new Vector();
    datasets = new Vector();
    data = new Vector();

    for(int alg = 0; alg<experiment_.getAlgorithmNameList().length; alg++){
      algorithms.add(new String(experiment_.getAlgorithmNameList()[alg]));
      data.add(new Vector());
      String rutaAlg = experiment_.getExperimentBaseDirectory() + "/data/"
              + experiment_.getAlgorithmNameList()[alg] + "/";

      for(int prob = 0; prob<experiment_.getProblemList().length; prob++){
        if(alg == 0){
          datasets.add(experiment_.getProblemList()[prob]);
        }

        String ruta = rutaAlg + experiment_.getProblemList()[prob] + "/" + indicator_;

        string = "";

        try {
          FileInputStream fis = new FileInputStream(ruta);

          byte[] leido = new byte[4096];
          int bytesLeidos = 0;

          while (bytesLeidos != -1) {
            bytesLeidos = fis.read(leido);

            if (bytesLeidos != -1) {
              string += new String(leido, 0, bytesLeidos);
            }
          }

          fis.close();
        }
        catch (IOException e) {
          Configuration.logger_.log(Level.SEVERE, "Error", e);
          throw new RuntimeException();
        }

        lineas = new StringTokenizer (string,"\n\r");

        double valor = 0.0;
        int n = 0;

        while (lineas.hasMoreTokens()) {
          linea = lineas.nextToken();
          valor = valor + Double.parseDouble(linea);
          n++;
        }
        if(n!=0){
          ((Vector)data.elementAt(alg)).add(new Double(valor/n));
        }else{
          ((Vector)data.elementAt(alg)).add(new Double(valor));
        }
      }
    }

    /*Compute the average performance per algorithm for each data set*/
    mean = new double[datasets.size()][algorithms.size()];

    for (j=0; j<algorithms.size(); j++) {
      for (i=0; i<datasets.size(); i++) {
        mean[i][j] = ((Double)((Vector)data.elementAt(j)).elementAt(i)).doubleValue();
      }
    }


    /*We use the pareja structure to compute and order rankings*/
    order = new Pair[datasets.size()][algorithms.size()];
    for (i=0; i<datasets.size(); i++) {
      for (j=0; j<algorithms.size(); j++){
        order[i][j] = new Pair(j,mean[i][j]);
      }
      Arrays.sort(order[i]);
    }

    /*building of the rankings table per algorithms and data sets*/
    rank = new Pair[datasets.size()][algorithms.size()];
    position = 0;
    for (i=0; i<datasets.size(); i++) {
      for (j=0; j<algorithms.size(); j++){
        found = false;
        for (k=0; k<algorithms.size() && !found; k++) {
          if (order[i][k].indice == j) {
            found = true;
            position = k+1;
          }
        }
        rank[i][j] = new Pair(position,order[i][position-1].valor);
      }
    }

    /*In the case of having the same performance, the rankings are equal*/
    for (i=0; i<datasets.size(); i++) {
      visto = new boolean[algorithms.size()];
      porVisitar= new Vector();

      Arrays.fill(visto,false);
      for (j=0; j<algorithms.size(); j++) {
        porVisitar.removeAllElements();
        sum = rank[i][j].indice;
        visto[j] = true;
        ig = 1;
        for (k=j+1;k<algorithms.size();k++) {
          if (rank[i][j].valor == rank[i][k].valor && !visto[k]) {
            sum += rank[i][k].indice;
            ig++;
            porVisitar.add(new Integer(k));
            visto[k] = true;
          }
        }
        sum /= (double)ig;
        rank[i][j].indice = sum;
        for (k=0; k<porVisitar.size(); k++) {
          rank[i][((Integer)porVisitar.elementAt(k)).intValue()].indice = sum;
        }
      }
    }

    /*compute the average ranking for each algorithm*/
    Rj = new double[algorithms.size()];
    for (i=0; i<algorithms.size(); i++){
      Rj[i] = 0;
      for (j=0; j<datasets.size(); j++) {
        Rj[i] += rank[j][i].indice / ((double)datasets.size());
      }
    }

    /*Print the average ranking per algorithm*/
    Output = Output + "\n"+("\\begin{table}[!htp]\n" +
            "\\centering\n" +
            "\\caption{Average Rankings of the algorithms\n}"+// for "+ experiment_.problemList_[prob] +" problem\n}" +
            "\\begin{tabular}{c|c}\n" +
            "Algorithm&Ranking\\\\\n\\hline");

    for (i=0; i<algorithms.size();i++) {
      Output = Output + "\n" + (String)algorithms.elementAt(i)+"&"+Rj[i]+"\\\\";
    }

    Output = Output + "\n" +
            "\\end{tabular}\n" +
            "\\end{table}"; 

    /*Compute the Friedman statistic*/
    termino1 = (12*(double)datasets.size())/((double)algorithms.size()*((double)algorithms.size()+1));
    termino2 = (double)algorithms.size()*((double)algorithms.size()+1)*((double)algorithms.size()+1)/(4.0);
    for (i=0; i<algorithms.size();i++) {
      sumatoria += Rj[i]*Rj[i];
    }
    friedman = (sumatoria - termino2) * termino1;

    Output = Output + "\n" + "\n\nFriedman statistic considering reduction performance (distributed according to chi-square with "+(algorithms.size()-1)+" degrees of freedom: "+friedman+").\n\n";

    Output = Output + "\n" + "\\end{document}";
    try {
      File latexOutput;
      latexOutput = new File(outDir);
      if(!latexOutput.exists()){
        latexOutput.mkdirs();
      }
      FileOutputStream f = new FileOutputStream(outFile);
      DataOutputStream fis = new DataOutputStream((OutputStream) f);

      fis.writeBytes(Output);

      fis.close();
      f.close();
    } catch (IOException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
      throw new RuntimeException() ;
    }
  }

  private class Pair implements Comparable {

    public double indice;
    public double valor;

    public Pair() {
    }

    public Pair(double i, double v) {
      indice = i;
      valor = v;
    }

    public int compareTo (Object o1) {
      if (Math.abs(this.valor) > Math.abs(((Pair)o1).valor)){
        //return -1;
        return 1;
      } else if (Math.abs(this.valor) < Math.abs(((Pair)o1).valor)){
        //return 1;
        return -1;
      } else {
        return 0;
      }
    }
  }
}
