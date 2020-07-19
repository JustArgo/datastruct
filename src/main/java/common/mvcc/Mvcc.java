package common.mvcc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mvcc {

    public static final Map<Integer,Record> tableMap = new HashMap<Integer,Record>();

    public static final Map<Integer,List<Record>> historyMap = new HashMap<Integer, List<Record>>();

    public static void main(String[] args) {

    }
}

class TrxThread extends Thread{

    private int operate;
    private Record record;
    private Map<Integer,List<Record>> historyMap = new HashMap<Integer, List<Record>>();

    public TrxThread(int operate, Record record){
        this.operate = operate;
        this.record = record;
    }

    @Override
    public void run() {
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

    public void insert(Record record){
        if(record==null || Mvcc.tableMap.containsKey(record.getId())){
            throw new RuntimeException("参数错误");
        }
        //设置事务id
        Mvcc.tableMap.put(record.getId(),record);
        //设置historyMap
    }

    public void update(Record record){
        if(record==null || !Mvcc.tableMap.containsKey(record.getId())){
            throw new RuntimeException("参数错误");
        }
        //设置新的事务id
        Mvcc.tableMap.put(record.getId(),record);
        //设置historyMap
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
