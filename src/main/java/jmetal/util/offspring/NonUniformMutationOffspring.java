/*
 * NonUniformGenerator.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package jmetal.util.offspring;

import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.logging.Level;

public class NonUniformMutationOffspring extends Offspring {

  public Operator mutation_;
  private Operator selection_;

  private double mutationProbatility_ ;
  private double perturbation_ ;
  private int maxIterations_ ;

  public NonUniformMutationOffspring(double mutationProbability,
      double perturbation,
      int maxIterations
      ) throws JMException {     
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>() ;
    mutationParameters.put("probability", mutationProbatility_ = mutationProbability) ;
    mutationParameters.put("perturbation", perturbation_ = perturbation) ;
    mutationParameters.put("maxIterations", maxIterations_ = maxIterations) ;
    mutation_ = MutationFactory.getMutationOperator("NonUniformMutation", mutationParameters);                    
    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);
    id_ = "NonUniformMutation";
  }

  public Solution getOffspring(Solution solution) {
    Solution res = new Solution(solution);
    try {
      mutation_.execute(res);
    } catch (JMException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    }
    return res;
  }

  public String configuration() {
    String result = "-----\n" ;
    result += "Operator: " + id_ + "\n" ;
    result += "Probability: " + mutationProbatility_ + "\n" ;
    result += "MaxIterations: " + maxIterations_ + "\n" ;
    result += "Perturbation: " + perturbation_ ;

    return result ;
  }
} // PolynomialOffspringGenerator


