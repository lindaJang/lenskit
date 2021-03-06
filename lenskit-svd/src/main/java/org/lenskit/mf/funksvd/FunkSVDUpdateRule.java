/*
 * LensKit, an open-source toolkit for recommender systems.
 * Copyright 2014-2017 LensKit contributors (see CONTRIBUTORS.md)
 * Copyright 2010-2014 Regents of the University of Minnesota
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.lenskit.mf.funksvd;

import org.grouplens.lenskit.iterative.LearningRate;
import org.grouplens.lenskit.iterative.RegularizationTerm;
import org.grouplens.lenskit.iterative.StoppingCondition;
import org.grouplens.lenskit.iterative.TrainingLoopController;
import org.lenskit.bias.BiasModel;
import org.lenskit.data.ratings.PreferenceDomain;
import org.lenskit.data.ratings.RatingMatrix;
import org.lenskit.inject.Shareable;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Configuration for computing FunkSVD updates.
 *
 * @since 1.0
 */
@Shareable
public final class FunkSVDUpdateRule implements Serializable {
    private static final long serialVersionUID = 2L;

    private final double learningRate;
    private final double trainingRegularization;
    private final BiasModel biasModel;
    private final StoppingCondition stoppingCondition;
    @Nullable
    private final PreferenceDomain domain;

    /**
     * Construct a new FunkSVD configuration.
     *
     * @param lrate The learning rate.
     * @param reg   The regularization term.
     * @param stop  The stopping condition
     */
    @Inject
    public FunkSVDUpdateRule(@LearningRate double lrate,
                             @RegularizationTerm double reg,
                             BiasModel bias,
                             @Nullable PreferenceDomain dom,
                             StoppingCondition stop) {
        learningRate = lrate;
        trainingRegularization = reg;
        biasModel = bias;
        domain = dom;
        stoppingCondition = stop;
    }

    /**
     * Create an estimator to use while training the recommender.
     *
     * @return The estimator to use.
     */
    public TrainingEstimator makeEstimator(RatingMatrix snapshot) {
        return new TrainingEstimator(snapshot, biasModel, domain);
    }

    public double getLearningRate() {
        return learningRate;
    }

    public double getTrainingRegularization() {
        return trainingRegularization;
    }

    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

    @Nullable
    public PreferenceDomain getDomain() {
        return domain;
    }

    public TrainingLoopController getTrainingLoopController() {
        return stoppingCondition.newLoop();
    }

    public FunkSVDUpdater createUpdater() {
        return new FunkSVDUpdater(this);
    }
}
