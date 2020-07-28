package common.mvcc;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Mvcc {

    public static final Map<Integer,Record> tableMap = new HashMap<Integer,Record>();

    public static final Map<Integer,List<Record>> historyMap = new HashMap<Integer, List<Record>>();

    public static final AtomicInteger trxIdGenerator = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        Record record = new Record();
        record.setId(1);
        record.setName("学生1");
        record.setTrxId(trxIdGenerator.incrementAndGet());

        List<OperateWrapper> operateList = new ArrayList<OperateWrapper>();
        operateList.add(new OperateWrapper(1, record));

        Record record2 = record.customClone();
        record2.setName("学生1_1");
        operateList.add(new OperateWrapper(2, record2));

        operateList.add(new OperateWrapper(3, new Record(1)));

        operateList.add(new OperateWrapper(4, new Record(1)));

        Thread.yield();

        System.out.println("end");
    }
}

class TrxThread extends Thread{

    List<OperateWrapper> operateList = new ArrayList<OperateWrapper>();

    public TrxThread(List<OperateWrapper> operateList){
        this.operateList = operateList;
    }

    @Override
    public void run() {
        for(OperateWrapper wrapper : operateList){
            Integer operate = wrapper.getOperate();
            Record record = wrapper.getRecord();

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

    }

}
