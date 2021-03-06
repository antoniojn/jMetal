//  SolutionTypeTest.java
//
//  Author:
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>

package test.core;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.problems.ZDT.ZDT5;
import jmetal.util.JMException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 18/05/14.
 */
public class SolutionTypeTest {

  @Test
  public void getProblemTest() throws JMException {
    Problem problem = new ZDT5("Binary", 10) ;

    SolutionType solutionType = new BinarySolutionType(problem) ;
    assertEquals(problem, solutionType.getProblem()) ;
  }
}
