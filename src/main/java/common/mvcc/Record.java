package common.mvcc;

import lombok.Data;

@Data
public class Record {

    private Integer trxId;

    private Integer id;

    private String name;

    private Record rollPointer;

}
