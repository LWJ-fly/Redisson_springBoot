package test.redisson.utils.LockUtil.enums;

public enum LockEnum {
    /**
     * 生成导出任务枚举
     */
    EXPORT_TASK(1, "生成导出任务枚举"),
    
    ;
    
    private final Integer code;
    private final String desc;
    LockEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
