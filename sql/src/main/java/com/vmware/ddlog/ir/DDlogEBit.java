/*
 * Copyright (c) 2021 VMware, Inc.
 * SPDX-License-Identifier: MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.vmware.ddlog.ir;

import com.facebook.presto.sql.tree.Node;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class DDlogEBit extends DDlogExpression {
    /**
     * When width is 0 it means it is unspecified.
     */
    private final int width;
    private final BigInteger ival;

    public DDlogEBit(@Nullable Node node, int width, BigInteger ival) {
        super(node, new DDlogTBit(node, width, false));
        this.width = width;
        this.ival = ival;
    }

    @Override
    public String toString() {
        String result = "";
        if (this.width > 0)
            result = this.width + "'d";
        result += this.ival.toString();
        return result;
    }

    @Override
    public boolean compare(DDlogExpression val, IComparePolicy policy) {
        if (!super.compare(val, policy))
            return false;
        if (!val.is(DDlogEBit.class))
            return false;
        DDlogEBit other = val.to(DDlogEBit.class);
        return this.width == other.width &&
                this.ival.equals(other.ival);
    }
}
