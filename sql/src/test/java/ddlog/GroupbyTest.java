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

package ddlog;

import org.junit.Test;

import java.util.Arrays;

public class GroupbyTest extends BaseQueriesTest {
    @Test
    public void testGroupBy() {
        String query = "create view v0 as SELECT COUNT(*) AS c FROM t1 GROUP BY column2";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{c:signed<64>}\n" +
                "function agg(g: Group<string, Tt1>):TRtmp {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(TRtmp{.c = count})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v)))," +
                "var v0 = TRtmp{.c = aggResult.c},var v1 = v0.";
        this.testTranslation(query, program);
    }

    @Test
    public void testTwoQueriesGroupBy() {
        String query0 = "create view v0 as SELECT COUNT(*) AS c FROM t1 GROUP BY column2";
        String query1 = "create view v1 as SELECT COUNT(*) AS c FROM t1 GROUP BY column2";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{c:signed<64>}\n" +
                "function agg(g: Group<string, Tt1>):TRtmp {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(TRtmp{.c = count})\n}\n\n" +
                "function agg1(g: Group<string, Tt1>):TRtmp {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(TRtmp{.c = count})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "relation Rtmp0[TRtmp]\n" +
                "output relation Rv1[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v)))," +
                "var v0 = TRtmp{.c = aggResult.c},var v1 = v0.\n" +
                "Rv1[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg1((v)))," +
                "var v0 = TRtmp{.c = aggResult.c},var v1 = v0.";
        this.testTranslation(Arrays.asList(query0, query1), program, false);
    }

    @Test
    public void testGroupBy1() {
        String query = "create view v0 as SELECT column2, COUNT(*) AS c FROM t1 GROUP BY column2";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{column2:string, c:signed<64>}\n" +
                "typedef Tagg = Tagg{c:signed<64>}\n" +
                "function agg(g: Group<string, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(Tagg{.c = count})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v)))," +
                "var v0 = TRtmp{.column2 = gb,.c = aggResult.c},var v1 = v0.";
        this.testTranslation(query, program);
    }

    @Test
    public void testGroupBy2() {
        String query = "create view v0 as SELECT column2, column3, COUNT(*) AS c, SUM(column1) AS s FROM t1 GROUP BY column2, column3";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{column2:string, column3:bool, c:signed<64>, s:signed<64>}\n" +
                "typedef Tagg = Tagg{c:signed<64>, s:signed<64>}\n" +
                "function agg(g: Group<(string, bool), Tt1>):Tagg {\n" +
                "(var gb, var gb0) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(var sum = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1));\n" +
                "(var incr = v.column1);\n" +
                "(sum = agg_sum_signed_R(sum, incr))}\n" +
                ");\n" +
                "(Tagg{.c = count,.s = sum})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v2] :- Rt1[v],var gb = v.column2,var gb0 = v.column3," +
                "var aggResult = Aggregate((gb, gb0), agg((v)))," +
                "var v1 = TRtmp{.column2 = gb,.column3 = gb0,.c = aggResult.c,.s = aggResult.s},var v2 = v1.";
        this.testTranslation(query, program);
    }

    @Test
    public void testGroupByNull1() {
        String query = "create view v0 as SELECT column2, COUNT(*) AS c FROM t1 GROUP BY column2";
        String program = this.header(true) +
                "typedef TRtmp = TRtmp{column2:Option<string>, c:signed<64>}\n" +
                "typedef Tagg = Tagg{c:signed<64>}\n" +
                "function agg(g: Group<Option<string>, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(Tagg{.c = count})\n}\n" +
                this.relations(true) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v)))," +
                "var v0 = TRtmp{.column2 = gb,.c = aggResult.c},var v1 = v0.";
        this.testTranslation(query, program, true);
    }

    @Test
    public void testMixAggregateGroupBy() {
        String query = "create view v0 as SELECT column2, SUM(column1) AS s FROM t1 GROUP BY column2";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{column2:string, s:signed<64>}\n" +
                "typedef Tagg = Tagg{s:signed<64>}\n" +
                "function agg(g: Group<string, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var sum = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(var incr = v.column1);\n" +
                "(sum = agg_sum_signed_R(sum, incr))}\n" +
                ");\n" +
                "(Tagg{.s = sum})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v))),var v0 = TRtmp{.column2 = gb,.s = aggResult.s},var v1 = v0.";
        this.testTranslation(query, program);
    }

    @Test
    public void testMixAggregateGroupByNull() {
        String query = "create view v0 as SELECT column2, SUM(column1) AS s FROM t1 GROUP BY column2";
        String program = this.header(true) +
                "typedef TRtmp = TRtmp{column2:Option<string>, s:Option<signed<64>>}\n" +
                "typedef Tagg = Tagg{s:Option<signed<64>>}\n" +
                "function agg(g: Group<Option<string>, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var sum = None{}: Option<signed<64>>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(var incr = v.column1);\n" +
                "(sum = agg_sum_signed_N(sum, incr))}\n" +
                ");\n" +
                "(Tagg{.s = sum})\n}\n" +
                this.relations(true) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v))),var v0 = TRtmp{.column2 = gb,.s = aggResult.s},var v1 = v0.";
        this.testTranslation(query, program, true);
    }

    @Test
    public void testHaving() {
        String query = "create view v0 as SELECT COUNT(column2) AS c FROM t1 GROUP BY column1 HAVING COUNT(column2) > 2";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{c:signed<64>}\n" +
                "typedef Tagg = Tagg{c:signed<64>, col:bool}\n" +
                "function agg(g: Group<signed<64>, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(var incr = v.column2);\n" +
                "(count = agg_count_R(count, incr))}\n" +
                ");\n" +
                "(Tagg{.c = count,.col = (count > 64'sd2)})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column1,var aggResult = Aggregate((gb), agg((v)))," +
                "var v0 = TRtmp{.c = aggResult.c},aggResult.col,var v1 = v0.";
        this.testTranslation(query, program);
    }

    @Test
    public void testHavingNull() {
        String query = "create view v0 as SELECT COUNT(column2) AS c FROM t1 GROUP BY column1 HAVING ANY(column3)";
        String program = this.header(true) +
                "typedef TRtmp = TRtmp{c:Option<signed<64>>}\n" +
                "typedef Tagg = Tagg{c:Option<signed<64>>, col:Option<bool>}\n" +
                "function agg(g: Group<Option<signed<64>>, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = None{}: Option<signed<64>>);\n" +
                "(var any = Some{false}: Option<bool>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(var incr = v.column2);\n" +
                "(count = agg_count_N(count, incr));\n" +
                "(var incr0 = v.column3);\n" +
                "(any = agg_any_N(any, incr0))}\n" +
                ");\n" +
                "(Tagg{.c = count,.col = any})\n}\n" +
                this.relations(true) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v2] :- Rt1[v],var gb = v.column1,var aggResult = Aggregate((gb), agg((v)))," +
                "var v1 = TRtmp{.c = aggResult.c},unwrapBool(aggResult.col),var v2 = v1.";
        this.testTranslation(query, program, true);
    }

    @Test
    public void testHaving1() {
        String query = "create view v0 as SELECT COUNT(column2) AS c FROM t1 GROUP BY column1 HAVING COUNT(column2) > 2 and column1 = 3";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{c:signed<64>}\n" +
                "typedef Tagg = Tagg{c:signed<64>, col:bool}\n" +
                "function agg(g: Group<signed<64>, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(var incr = v.column2);\n" +
                "(count = agg_count_R(count, incr))}\n" +
                ");\n" +
                "(Tagg{.c = count,.col = ((count > 64'sd2) and (gb == 64'sd3))})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column1,var aggResult = Aggregate((gb), agg((v)))," +
                "var v0 = TRtmp{.c = aggResult.c},aggResult.col,var v1 = v0.";
        this.testTranslation(query, program);
    }

    @Test
    public void testHaving2() {
        String query = "create view v0 as SELECT SUM(column1) AS s FROM t1 GROUP BY column2 HAVING COUNT(*) > 2";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{s:signed<64>}\n" +
                "typedef Tagg = Tagg{s:signed<64>, col:bool}\n" +
                "function agg(g: Group<string, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var sum = 64'sd0: signed<64>);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(var incr = v.column1);\n" +
                "(sum = agg_sum_signed_R(sum, incr));\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(Tagg{.s = sum,.col = (count > 64'sd2)})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v)))," +
                "var v0 = TRtmp{.s = aggResult.s},aggResult.col,var v1 = v0.";
        this.testTranslation(query, program);
    }

    @Test
    public void testHaving3() {
        String query = "create view v0 as SELECT column2, SUM(column1) AS s FROM t1 GROUP BY column2 HAVING COUNT(DISTINCT column3) > 1";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{column2:string, s:signed<64>}\n" +
                "typedef Tagg = Tagg{s:signed<64>, col:bool}\n" +
                "function agg(g: Group<string, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var sum = 64'sd0: signed<64>);\n" +
                "(var count_distinct = set_empty(): Set<bool>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(var incr = v.column1);\n" +
                "(sum = agg_sum_signed_R(sum, incr));\n" +
                "(var incr0 = v.column3);\n" +
                "(set_insert(count_distinct, incr0))}\n" +
                ");\n" +
                "(Tagg{.s = sum,.col = (set_size(count_distinct) as signed<64> > 64'sd1)})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v2] :- Rt1[v],var gb = v.column2,var aggResult = Aggregate((gb), agg((v)))," +
                "var v1 = TRtmp{.column2 = gb,.s = aggResult.s},aggResult.col,var v2 = v1.";
        this.testTranslation(query, program);
    }

    @Test
    public void testGroupByExpression() {
        String query = "create view v0 as SELECT substr(column2, 0, 1) AS s, COUNT(*) AS c FROM t1 GROUP BY substr(column2, 0, 1)";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{s:string, c:signed<64>}\n" +
                "typedef Tagg = Tagg{c:signed<64>}\n" +
                "function agg(g: Group<string, Tt1>):Tagg {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(Tagg{.c = count})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = sql_substr(v.column2, 64'sd0, 64'sd1)," +
                "var aggResult = Aggregate((gb), agg((v))),var v0 = TRtmp{.s = gb,.c = aggResult.c},var v1 = v0.";
        this.testTranslation(query, program);
    }

    @Test
    public void testExpressionOfGroupBy() {
        String query = "create view v0 as SELECT substr(column2, 0, 1) AS s, COUNT(*) AS c FROM t1 GROUP BY column2";
        String program = this.header(false) +
                "typedef TRtmp = TRtmp{s:string, c:signed<64>}\n" +
                "function agg(g: Group<string, Tt1>):TRtmp {\n" +
                "(var gb) = group_key(g);\n" +
                "(var count = 64'sd0: signed<64>);\n" +
                "(for ((i, _) in g) {\n" +
                "var v = i;\n" +
                "(count = agg_count_R(count, 64'sd1))}\n" +
                ");\n" +
                "(TRtmp{.s = sql_substr(gb, 64'sd0, 64'sd1),.c = count})\n}\n" +
                this.relations(false) +
                "relation Rtmp[TRtmp]\n" +
                "output relation Rv0[TRtmp]\n" +
                "Rv0[v1] :- Rt1[v],var gb = v.column2," +
                "var aggResult = Aggregate((gb), agg((v))),var v0 = TRtmp{.s = aggResult.s,.c = aggResult.c},var v1 = v0.";
        this.testTranslation(query, program);
    }

}
