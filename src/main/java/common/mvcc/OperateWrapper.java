package common.mvcc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperateWrapper {

    /** 操作类型 */
    private Integer operate;

    /** 操作前睡眠多久 单位(秒) */
    private Integer sleepTime;

    /** 操作对象 */
    private Record record;

}
