package common.mvcc;

import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Mvcc {

    /** 1-读未提交  2-读已提交 3-可重复读 4-序列化 */
    public static Integer trxLevel = 3;

    public static Set<Integer> onlineTrxIdSet = new HashSet<Integer>();

    public static final Map<Integer,Record> tableMap = new HashMap<Integer,Record>();

    public static final Map<Integer,List<Record>> historyMap = new HashMap<Integer, List<Record>>();

    public static final AtomicInteger trxIdGenerator = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {


        wrapperThread0().start();

        wrapperThread1().start();

        wrapperThread2().start();

        //Thread.yield();

        System.out.println("end");
        while(true){

        }
    }

    private static Thread wrapperThread0(){
        Record record = new Record();
        record.setId(1);
        record.setName("学生1");
        record.setTrxId(trxIdGenerator.incrementAndGet());

        List<OperateWrapper> operateList = new ArrayList<OperateWrapper>();
        operateList.add(new OperateWrapper(1, 0, record));

        return new TrxThread(operateList);
    }

    private static Thread wrapperThread1(){
        Record record = new Record();
        record.setId(1);
        record.setName("学生1更新");
        record.setTrxId(trxIdGenerator.incrementAndGet());

        List<OperateWrapper> operateList = new ArrayList<OperateWrapper>();
        operateList.add(new OperateWrapper(2, 3, record));

        Record record2 = record.customClone();
        record2.setName("学生1_1");
        operateList.add(new OperateWrapper(2, 8, record2));

        //operateList.add(new OperateWrapper(3, 0, new Record(1)));

        //operateList.add(new OperateWrapper(4, 0, new Record(1)));

        return new TrxThread(operateList);
    }

    private static Thread wrapperThread2(){
        Record record = new Record();
        record.setId(1);
        record.setName("学生1");
        record.setTrxId(trxIdGenerator.incrementAndGet());

        List<OperateWrapper> operateList = new ArrayList<OperateWrapper>();
        //operateList.add(new OperateWrapper(1, 0, record));

        Record record2 = record.customClone();
        record2.setName("学生1_1");
        //operateList.add(new OperateWrapper(2, 0, record2));

        //operateList.add(new OperateWrapper(3, 0, new Record(1)));

        operateList.add(new OperateWrapper(4, 5, new Record(1)));

        return new TrxThread(operateList);
    }

}

class TrxThread extends Thread{

    List<OperateWrapper> operateList = new ArrayList<OperateWrapper>();

    /** 开启快照读时，存在的线程id集合 */
    Set<Integer> snapshotOnlineTrxIdSet = new HashSet<Integer>();

    public TrxThread(List<OperateWrapper> operateList){
        this.operateList = operateList;
    }

    @Override
    public void run() {
        Mvcc.onlineTrxIdSet.add(operateList.get(0).getRecord().getTrxId());
        for(OperateWrapper wrapper : operateList){
            Integer operate = wrapper.getOperate();
            Record record = wrapper.getRecord();
            if(wrapper.getSleepTime() != null && wrapper.getSleepTime() > 0) {
                try{
                    Thread.sleep(wrapper.getSleepTime() * 1000);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            switch (operate){
                case 1:
                    insert(record);
                    break;
                case 2:
                    update(record);
                    break;
                case 3:
                    delete(record.getId());
                    break;
                case 4:
                    read(record.getId());
                    break;
                default:
                    System.out.println("none operation");
                    break;
            }
        }
        Mvcc.onlineTrxIdSet.remove(operateList.get(0).getRecord().getTrxId());
    }

    public void insert(Record record){
        if(record==null){
            throw new RuntimeException("参数错误");
        }
        if (Mvcc.tableMap.containsKey(record.getId())) {
            throw new RuntimeException("数据已存在");
        }
        //设置事务id
        Mvcc.tableMap.put(record.getId(),record);

        //设置historyMap
        List<Record> recordList = new ArrayList<Record>();

        recordList.add(record);
        Mvcc.historyMap.put(record.getId(),recordList);
    }

    public void update(Record record){
        if(record==null || !Mvcc.tableMap.containsKey(record.getId())){
            throw new RuntimeException("参数错误");
        }

        Record oldRecord = Mvcc.tableMap.get(record.getId());

        //设置新的事务id
        Mvcc.tableMap.put(record.getId(),record);
        //设置historyMap
        List<Record> recordList = Mvcc.historyMap.get(record.getId());
        if(recordList == null){
            recordList = new ArrayList<Record>();
        }
        recordList.add(0,record);
    }

    public void delete(Integer id){
        if(id==null || !Mvcc.tableMap.containsKey(id)){
            return;
        }
        //设置新的时候id
        Mvcc.tableMap.remove(id);
        //设置historyMap
    }

    public void read(Integer id){
        Record record = Mvcc.tableMap.get(id);
        if(record == null){
            System.out.println(String.format("id={}的记录不存在",id));
        }else{
           switch (Mvcc.trxLevel){
               case 1:
                   System.out.println(JSON.toJSONString(record));
                   break;
               case 2:
                   if(Mvcc.onlineTrxIdSet.contains(record.getTrxId())){
                       System.out.println("记录处于事务中");
                       List<Record> recordList = Mvcc.historyMap.get(record.getId());
                       if(recordList != null){
                           for(Record hisRecord:recordList){
                               if(hisRecord.getTrxId() != record.getTrxId()){
                                   System.out.println(String.format("读取id={}旧记录",record.getId()));
                                   System.out.println(JSON.toJSONString(hisRecord));
                               }
                           }
                       }
                   }else{
                       System.out.println(JSON.toJSONString(record));
                   }
                   break;
               case 3:
                   //Map<Integer, Record> copyMap = copyNoOnlineMap(Mvcc.tableMap);
                   //第一次读取需要记录，事务中的事务id
                   Integer maxTrxId = -1;
                   if(snapshotOnlineTrxIdSet == null){
                       snapshotOnlineTrxIdSet = new HashSet<Integer>();
                       snapshotOnlineTrxIdSet.addAll(Mvcc.onlineTrxIdSet);
                       snapshotOnlineTrxIdSet.remove(operateList.get(0).getRecord().getTrxId());
                       //取当前记录中最大的事务id

                       for(Record hisRecord:Mvcc.historyMap.get(id)){
                           if(hisRecord.getTrxId()>maxTrxId){
                               maxTrxId =hisRecord.getTrxId();
                           }
                       }
                   }

                   //开始读取数据
                   List<Record> recordList = Mvcc.historyMap.get(id);
                   if(recordList != null){
                       for(int i=recordList.size()-1;i>=0;i--){
                           Record hisRecord = recordList.get(i);
                           if(snapshotOnlineTrxIdSet.contains(hisRecord.getTrxId()) || hisRecord.getTrxId() > maxTrxId){
                               continue;
                           }
                           System.out.println("读取视图："+JSON.toJSONString(hisRecord));
                       }
                   }

                   break;
               case 4:
                   System.out.println("读取序列化事务");
                   break;
               default:
                   System.out.println(JSON.toJSONString(record));
           }
        }
    }

    public Map<Integer, Record> copyNoOnlineMap(Map<Integer, Record> recordMap){
        Map<Integer, Record> resultMap = new HashMap<Integer, Record>();
        for(Map.Entry<Integer, Record> entry:recordMap.entrySet()){
            if(Mvcc.onlineTrxIdSet.contains(entry.getValue().getTrxId())){
                continue;
            }
            resultMap.put(entry.getKey(),entry.getValue().customClone());
        }
        return resultMap;
    }

}
