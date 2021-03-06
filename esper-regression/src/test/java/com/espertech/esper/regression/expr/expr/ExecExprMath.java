/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.regression.expr.expr;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import com.espertech.esper.supportregression.bean.SupportBean;
import com.espertech.esper.supportregression.execution.RegressionExecution;

import static org.junit.Assert.assertEquals;

public class ExecExprMath implements RegressionExecution {
    public void run(EPServiceProvider epService) throws Exception {
        epService.getEPAdministrator().getConfiguration().addEventType(SupportBean.class);

        String epl = "select intPrimitive/intBoxed as result from SupportBean";
        EPStatement stmt = epService.getEPAdministrator().createEPL(epl);
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);
        assertEquals(Double.class, stmt.getEventType().getPropertyType("result"));

        sendEvent(epService, 100, 3);
        assertEquals(100 / 3d, listener.assertOneGetNewAndReset().get("result"));

        sendEvent(epService, 100, null);
        assertEquals(null, listener.assertOneGetNewAndReset().get("result"));

        sendEvent(epService, 100, 0);
        assertEquals(Double.POSITIVE_INFINITY, listener.assertOneGetNewAndReset().get("result"));

        sendEvent(epService, -5, 0);
        assertEquals(Double.NEGATIVE_INFINITY, listener.assertOneGetNewAndReset().get("result"));
    }

    private void sendEvent(EPServiceProvider epService, Integer intPrimitive, Integer intBoxed) {
        SupportBean bean = new SupportBean();
        bean.setIntBoxed(intBoxed);
        bean.setIntPrimitive(intPrimitive);
        epService.getEPRuntime().sendEvent(bean);
    }
}
