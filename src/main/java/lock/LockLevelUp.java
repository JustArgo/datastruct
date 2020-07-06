package lock;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LockLevelUp {

    public static volatile Boolean biasLockFlag = false;

    private static String threadName;

    private static Map<String,Boolean> liveMap = new HashMap<String,Boolean>();

    public static void getLock(){
        if(biasLockFlag){
            System.out.println("锁已被持有");
            if(threadName!=null && threadName.equals(Thread.currentThread().getName())){
                System.out.println("当前线程"+threadName+"持有锁, 并且重入");
            }else{
                System.out.println("发生锁竞争");
                if(liveMap.get(threadName)==null || !liveMap.get(threadName)){
                    System.out.println("持有偏向锁的线程已销毁，当前线程持有锁");
                    biasLockFlag = true;
                    threadName = Thread.currentThread().getName();
                }else{
                    System.out.println("升级为轻量级锁");

                }
            }
        }else{
            biasLockFlag = true;
            threadName = Thread.currentThread().getName();
        }
    }

    public static void main(String[] args) {

        UserThread u1 = new UserThread("thread-1");
        UserThread u2 = new UserThread("thread-2");
        u1.start();
        u2.start();

        while (true){

        }

    }

}

class UserThread extends Thread{

    public UserThread(String name){
        this.setName(name);
    }

    private Random random = new Random();

    @Override
    public void run() {
        for(int i=0;i<100;i++){
            if(random.nextInt(5)==1){
                System.out.println(Thread.currentThread().getName()+" get lock");
            }
        }
    }
}