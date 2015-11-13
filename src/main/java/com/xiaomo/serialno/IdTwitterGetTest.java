package com.xiaomo.serialno;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * @author xiaomo (hcxxiaomo@gmail.com)
 * @since 0.1
 */
public class IdTwitterGetTest {
	
    public static void main(String[] args) {

        IdTwitterGet iw = new IdTwitterGet(1, 2, 0);
        List<Long> l = new ArrayList<Long>();
        for (int i = 0; i < 40; i++) {
        	l.add(iw.getId());
        }
        LogUtil.getLog(IdTwitterGetTest.class,Level.INFO, Arrays.asList(l.toArray()));
    }
}
