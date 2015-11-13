/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaomo.serialno;


import java.util.Random;

/**
 * from https://github.com/twitter/snowflake/blob/scala_28/src/test/scala/com/twitter/service/snowflake/IdWorkerSpec.scala
 * @author xiaomo (hcxxiaomo@gmail.com)
 * @since 0.1
 * 
 * Twitter的snowflake算法，ID组成为 毫秒级时间(41位)+机器ID(10位，数据中心(5位)+节点(5位))+毫秒内序列(12位).
 * 时间戳则是记录了从 1288834974657 (Thu, 04 Nov 2010 01:42:54 GMT) 这一时刻到当前时间所经过的毫秒数，41位，符号位为0
 */
public class IdTwitterGet {

    private final long workerId;//数据中心(5位)
    private final long datacenterId;//节点(5位)
    private final long idepoch;//毫秒级时间(41位)
    private long sequence;//毫秒内序列(12位)

    
    private static final long datacenterIdShift = 17L;
    private static final long workerIdShift = 12L;
    private static final long timestampLeftShift = 22L;
    private static final long maxWorkerId = 31L;
    private static final long maxDatacenterId = 31L;
    private static final long sequenceMask = 4095L;//序列编号共12位，每节点每毫秒生成4096个ID
    
    

    private long lastTimestamp = -1L;//可以根据需要设定一个最早时间，任何时间不得小于最早时间，默认不设限制。
    private long defaultTimestamp = 1288834974657L;//可以根据实情进行时间的修改。
    private static final Random r = new Random();
    
    /**
     * 数据中心=random，节点中心=random，毫秒内序列(12位)=0,毫秒级时间(41位)=now
     */
    public IdTwitterGet() {
        this(System.currentTimeMillis());
    }
    
    /**
     * 数据中心=random，节点中心=random，毫秒内序列(12位)=0
     * @param idepoch 毫秒级时间(41位)
     */
    public IdTwitterGet(long idepoch) {
        this(r.nextInt((int) maxWorkerId), r.nextInt((int) maxDatacenterId), 0, idepoch);
    }
    
    /**
     * 毫秒内序列 = 0, 毫秒级时间 = now, [常用的构造为这个构造]
     * @param workerId 数据中心(5位)
     * @param datacenterId 节点(5位)
     */
    public IdTwitterGet(long workerId, long datacenterId) {
        this(workerId, datacenterId, 0, System.currentTimeMillis());
    }
    
    /**
     * 毫秒级时间 = now
     * @param workerId 数据中心(5位)
     * @param datacenterId 节点(5位)
     * @param sequence 毫秒内序列(12位)
     */
    public IdTwitterGet(long workerId, long datacenterId, long sequence) {
    	this(workerId, datacenterId, sequence, System.currentTimeMillis());
    }

    /**
     * 数据中心，节点中心，毫秒内序列(12位)，毫秒级时间(41位)
     * @param workerId 数据中心(5位)
     * @param datacenterId 节点(5位)
     * @param sequence 毫秒内序列(12位)
     * @param idepoch 毫秒级时间(41位)
     */
    public IdTwitterGet(long workerId, long datacenterId, long sequence, long idepoch) {
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
        this.idepoch = idepoch;
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException(String.format("非法workerId 数据中心(5位)应大于0而小于%d，而当前值为:%d", maxWorkerId,workerId));
        }
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException(String.format("非法datacenterId 节点(5位)应大于0而小于%d，而当前值为:%d ", maxDatacenterId , datacenterId));
        }
    }
    
    /**
     * @return 数据中心5位
     */
    public long getDatacenterId() {
        return datacenterId;
    }
    
    /**
     * @return 节点5位
     */
    public long getWorkerId() {
        return workerId;
    }
    
    /**
     * @return 当前时间
     */
    public long getTime() {
        return System.currentTimeMillis();
    }

    /**
     * @return 得到long类型id
     */
    public long getId() {
        long id = nextId();
        return id;
    }
    
    /**
     * 设置最早时间
     * @param lastTimestamp 最早时间
     */
    public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}
    
	private synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(String.format("时间早于最低时间:%d",lastTimestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        //a|b的意思就是把a和b按位或， 按位或的意思就是先把a和b都换成2进制，然后用或操作
        //[也可以改成直接+操作，直接+操作效率高，但是会有极低概率产生重复ID]
        long id = ((timestamp - defaultTimestamp) << timestampLeftShift)//前41位
                | (datacenterId << datacenterIdShift)//中间前5位
                | (workerId << workerIdShift)//中间后5位
                | sequence;//最后12位
/*        long id = ((timestamp - defaultTimestamp) << timestampLeftShift)//前41位
        		+ (datacenterId << datacenterIdShift)//中间前5位
        		+ (workerId << workerIdShift)//中间后5位
        		+ sequence;//最后12位
*/       
        return id;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdWorker{");
        sb.append("workerId=").append(workerId);
        sb.append(", datacenterId=").append(datacenterId);
        sb.append(", idepoch=").append(idepoch);
        sb.append(", lastTimestamp=").append(lastTimestamp);
        sb.append(", sequence=").append(sequence);
        sb.append('}');
        return sb.toString();
    }
}
