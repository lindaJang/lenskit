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
package org.lenskit.mf.svd;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.linear.RealVector;
import org.lenskit.inject.Shareable;
import org.lenskit.data.ratings.PreferenceDomain;

import javax.annotation.Nonnull;
import net.jcip.annotations.Immutable;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Kernel that clamps each feature's contribution to the preference domain.
 *
 * @since 2.1
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
@Shareable
@Immutable
public class DomainClampingKernel implements BiasedMFKernel, Serializable {
    private static final long serialVersionUID = 1L;

    private final PreferenceDomain domain;

    @Inject
    public DomainClampingKernel(PreferenceDomain dom) {
        domain = dom;
    }

    @Override
    public double apply(double bias, @Nonnull RealVector user, @Nonnull RealVector item) {
        final int n = user.getDimension();
        Preconditions.checkArgument(item.getDimension() == n, "vectors have different lengths");

        double result = bias;
        for (int i = 0; i < n; i++) {
            result = domain.clampValue(result + user.getEntry(i) * item.getEntry(i));
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainClampingKernel that = (DomainClampingKernel) o;

        return domain.equals(that.domain);
    }

    @Override
    public int hashCode() {
        return domain.hashCode();
    }

    @Override
    public String toString() {
        return "ClampKernel(" + domain + ")";
    }
}
