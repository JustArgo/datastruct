package common.mvcc;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Record {

    private Integer trxId;

    private Integer id;

    private String name;

    private Record rollPointer;

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
