package DoubleArrayTrie;

import com.sun.deploy.util.ArrayUtil;

import java.util.*;

public class DATrie {

    private int[] base;
    private int[] check;
    private String[] state;

    /** 啊     阿     埃     根     胶     拉     及     廷     伯     人 */
    private static Map<String,Integer> charCodeMap = new HashMap<String,Integer>(){
        {
            put("啊",1);
            put("阿",2);
            put("埃",3);
            put("根",4);
            put("胶",5);
            put("拉",6);
            put("及",7);
            put("廷",8);
            put("伯",9);
            put("人",10);
        }
    };

    public static void main(String[] args) {

        List<String> wordList = new ArrayList<String>();
        wordList.add("埃");
        wordList.add("啊");
        wordList.add("阿");
        wordList.add("阿拉");
        wordList.add("阿拉伯");
        wordList.add("阿根廷");
        wordList.add("阿拉伯人");
        wordList.add("埃及");
        wordList.add("阿胶");
        wordList.add("阿根");
        Collections.sort(wordList);

        DATrie trie = new DATrie();

        trie.build(wordList);
        trie.dump();
        //System.out.println(wordList);
    }

    public void build(List<String> wordList){
        this.base = new int[wordList.size()*5];
        this.check = new int[wordList.size()*5];
        this.state = new String[wordList.size()*5];

        int setCount = 0;

        int idx = 0;
        for(int i=0;i<wordList.size();i++){
            if(wordList.get(i).toCharArray().length==1){
                state[idx++] = wordList.get(i);
                setCount++;
            }
        }

        System.out.println("setCount:"+setCount);

        /*for(int i=0;i<wordList.size();i++){
            if(wordList.get(i).length()!=1){
                char[] wordArr = wordList.get(i).toCharArray();
                char[] prefixArr = Arrays.copyOf(wordArr,wordArr.length-1);
                int pos = findPrefixIndex(wordList,prefixArr);
                check[i] = pos;
            }
        }*/

        while(setCount<wordList.size()){
            for(int i=0;i<state.length;i++){
                List<String> suffixList = findSuffixList(wordList,state[i]);
                if(suffixList.size()>0){
                    List<Integer> codeList = new ArrayList<Integer>();
                    for(String word:suffixList){
                        char c = word.charAt(word.length()-1);
                        codeList.add(charCodeMap.get(String.valueOf(c)));
                    }
                    int k = findK(codeList,suffixList);
                    if(k>0){
                        base[i] = k;
                        int index = 0;
                        for(Integer codeIdx:codeList){
                            check[k+codeIdx] = i;
                            state[k+codeIdx] = suffixList.get(index++);
                            setCount++;
                        }
                    }
                }
                if(setCount>=wordList.size()){
                    break;
                }
            }
        }
    }

    /**
     * 查找出符合某个前缀的词汇
     */
    public List<String> findSuffixList(List<String> wordList, String prefixWord){
        List<String> suffixList = new ArrayList<String>();
        for(String word:wordList){
            if(word!=null && prefixWord!=null && word.indexOf(prefixWord)==0 && word.length()-prefixWord.length()==1){
                suffixList.add(word);
            }
        }
        return suffixList;
    }

    /**
     * 找到一个步长，使得满足公式
     */
    public int findK(List<Integer> codeList, List<String> suffixList){
        for(int k=1;k<10000;k++){
            int count = 0;
            for(Integer codeIdx:codeList){
                if(base[k+codeIdx]==0 && check[k+codeIdx]==0){
                    count++;
                }
            }
            if(count==codeList.size()){
                return k;
            }
        }
        return 0;
    }

    /**
     * 找出某个字符串前缀所在的位置
     */
    public int findPrefixIndex(List<String> wordList, char[] prefixArr){
        String prefix = prefixArr.toString();
        for(int i=0;i<this.state.length;i++){
            if(state[i]!=null && state[i].equals(prefix)){
                return i;
            }
        }
        return 0;
    }

    //打印字典树的信息
    private void dump(){
        for(int i=0;i<base.length;i++){
            System.out.print(String.format("%4d",base[i])+"\t");
        }
        System.out.println("");
        for(int i=0;i<check.length;i++){
            System.out.print(String.format("%4d",check[i])+"\t");
        }
        System.out.println("");
        for(int i=0;i<state.length;i++){
            System.out.print(String.format("%4s",state[i])+"\t");
        }
    }
}
