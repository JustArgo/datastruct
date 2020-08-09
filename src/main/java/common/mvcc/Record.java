package common.mvcc;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Record {

    /** 事务id */
    private Integer trxId;

    /** 主键id */
    private Integer id;

    /** 姓名 */
    private String name;

    /** 回滚指针 */
    private Record rollPointer;

    /** 删除标志位 */
    private byte deleteBit;

    public Record (Integer id) {
        this.id = id;
    }

    public Record customClone () {
        Record record = new Record();
        record.setId(this.getId());
        record.setTrxId(this.getTrxId());
        record.setName(this.getName());
        record.setRollPointer(this.getRollPointer());
        return record;
    }
}
