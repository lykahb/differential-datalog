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

import com.vmware.ddlog.util.Linq;
import com.facebook.presto.sql.tree.Node;

import javax.annotation.Nullable;

public class DDlogAtom extends DDlogNode {
    public final String relation;
    public final DDlogExpression val;

    public DDlogAtom(@Nullable Node node, String relation, DDlogExpression val) {
        super(node);
        this.relation = relation;
        this.val = val;
    }

    @Override
    public String toString() {
        if (this.val instanceof DDlogEStruct) {
            DDlogEStruct struct = (DDlogEStruct)this.val;
            return this.relation + "(" + String.join(",",
                Linq.map(struct.fields, f ->
                        (f.getName().isEmpty() ? "" : "." + f.getName()) + " = "
                                + f.getValue().toString(), String.class)) + ")";
        }
        return this.relation + "[" + this.val.toString() + "]";
    }

    public boolean compare(DDlogAtom other, IComparePolicy policy) {
        if (!policy.compareRelation(this.relation, other.relation))
            return false;
        return this.val.compare(other.val, policy);
    }
}
